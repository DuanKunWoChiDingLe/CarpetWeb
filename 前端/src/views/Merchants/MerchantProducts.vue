<template>
  <div class="merchant-series">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/merchant' }">商家中心</el-breadcrumb-item>
      <el-breadcrumb-item>系列管理</el-breadcrumb-item>
    </el-breadcrumb>

    <div class="action-bar">
      <el-button type="primary" @click="openAddDialog">+ 新增系列</el-button>
    </div>

    <el-card class="filter-card">
      <el-row :gutter="20" align="middle">
        <el-col :span="4">
          <el-select v-model="filters.brandId" placeholder="品牌" clearable>
            <el-option v-for="item in brandOptions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="filters.layType" placeholder="铺设方式" clearable>
            <el-option label="满铺毯" value="full" />
            <el-option label="方块毯" value="modular" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="filters.material" placeholder="材质" clearable>
            <el-option label="丙纶" value="polypropylene" />
            <el-option label="涤纶" value="polyester" />
            <el-option label="尼龙" value="nylon" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-input v-model="filters.keyword" placeholder="系列名称" clearable />
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-col>
      </el-row>
    </el-card>

    <div v-loading="loading" class="series-grid">
      <el-row :gutter="20">
        <el-col v-for="series in seriesList" :key="series.id" :xs="24" :sm="12" :md="8" :lg="6" class="grid-item">
          <el-card class="series-card" :body-style="{ padding: '0' }" shadow="hover">
            <div class="card-cover">
              <el-image :src="getImageUrl(series.coverImage)" fit="cover" class="cover-image" />
            </div>
            <div class="card-info">
              <h3 class="series-name">{{ series.name }}</h3>
              <div class="series-meta">
                <span>{{ series.brandName }} · {{ series.layType === 'full' ? '满铺毯' : '方块毯' }}</span>
              </div>
              <div class="series-stats">
                <span>{{ series.colorCount }} 个色号</span>
              </div>
              <div class="card-actions">
                <el-button type="primary" size="small" @click="viewDetail(series)">查看详情</el-button>
                <el-button type="warning" size="small" @click="editSeries(series)">编辑</el-button>
                <el-button type="danger" size="small" @click="confirmDelete(series)">删除</el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <el-empty v-if="!loading && seriesList.length === 0" description="暂无系列" />

    <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :total="total"
      :page-sizes="[12, 24, 36]" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
      @current-change="handleCurrentChange" class="pagination" />

    <!-- 新增/编辑系列弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="品牌" prop="brandId">
          <el-select v-model="form.brandId" placeholder="请选择品牌" style="width: 100%">
            <el-option v-for="item in brandOptions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="系列名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入系列名称" />
        </el-form-item>
        <el-form-item label="铺设方式" prop="layType">
          <el-radio-group v-model="form.layType">
            <el-radio value="full">满铺毯</el-radio>
            <el-radio value="modular">方块毯</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="材质" prop="material">
          <el-select v-model="form.material" placeholder="请选择材质" style="width: 100%">
            <el-option label="丙纶" value="polypropylene" />
            <el-option label="涤纶" value="polyester" />
            <el-option label="尼龙" value="nylon" />
          </el-select>
        </el-form-item>
        <el-form-item label="规格" prop="spec">
          <el-input v-model="form.spec" placeholder="例如 4m宽 / 50x50" />
        </el-form-item>
        <el-form-item label="封面图" prop="coverImage">
          <el-upload v-model:file-list="coverFileList" :action="`${baseURL}/upload/image`"
            :headers="{ Authorization: `Bearer ${userStore.token}` }" list-type="picture-card" :limit="1"
            :on-success="handleCoverSuccess" :on-remove="handleCoverRemove">
            <el-icon>
              <Plus />
            </el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type UploadFile } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { brandApi, type Brand } from '@/api/brand'
import { seriesApi, type Series } from '@/api/series'
import { type UploadResponse } from '@/api/upload'

const router = useRouter()
const userStore = useUserStore()
const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

// 品牌选项
const brandOptions = ref<Brand[]>([])

// 系列列表数据
const loading = ref(false)
const seriesList = ref<Series[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(12)

// 筛选条件
const filters = reactive({
  brandId: undefined as number | undefined,
  layType: '',
  material: '',
  keyword: ''
})

// 获取品牌列表
const fetchBrands = async () => {
  try {
    const res = await brandApi.getAll()
    brandOptions.value = res
    if (brandOptions.value.length === 0) {
      ElMessage.warning('暂无品牌数据，请先添加品牌')
    }
  } catch (error) {
    console.log(error);
    
    ElMessage.error('获取品牌列表失败')
  }
}

// 获取系列列表
const fetchSeries = async () => {
  loading.value = true
  try {
    const params: {
      page: number
      size: number
      brandId?: number
      layType?: string
      material?: string
      keyword?: string
    } = {
      page: currentPage.value - 1,
      size: pageSize.value,
    }
    if (filters.brandId) params.brandId = filters.brandId
    if (filters.layType) params.layType = filters.layType
    if (filters.material) params.material = filters.material
    if (filters.keyword) params.keyword = filters.keyword

    const res = await seriesApi.getPage(params)
    seriesList.value = res.content
    total.value = res.totalElements
  } catch (error) {
    console.log(error);
    
    ElMessage.error('获取系列列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索重置
const handleSearch = () => {
  currentPage.value = 1
  fetchSeries()
}
const resetFilters = () => {
  filters.brandId = undefined
  filters.layType = ''
  filters.material = ''
  filters.keyword = ''
  currentPage.value = 1
  fetchSeries()
}
const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  fetchSeries()
}
const handleCurrentChange = (page: number) => {
  currentPage.value = page
  fetchSeries()
}

// 新增/编辑弹窗
const dialogVisible = ref(false)
const dialogTitle = ref('新增系列')
const submitting = ref(false)
const formRef = ref<FormInstance | null>(null)
const form = reactive({
  id: 0,
  brandId: undefined as number | undefined,
  name: '',
  layType: 'full' as 'full' | 'modular',
  material: '',
  spec: '',
  coverImage: '',
  description: ''
})
const coverFileList = ref<UploadFile[]>([])

const rules = {
  brandId: [{ required: true, message: '请选择品牌', trigger: 'change' }],
  name: [{ required: true, message: '请输入系列名称', trigger: 'blur' }],
  layType: [{ required: true, message: '请选择铺设方式', trigger: 'change' }],
  material: [{ required: true, message: '请选择材质', trigger: 'change' }],
  spec: [{ required: true, message: '请输入规格', trigger: 'blur' }]
}

const handleCoverSuccess = (response: UploadResponse, file: UploadFile) => {
  // 兼容两种格式：直接返回 {url} 或 {code, data:{url}}
  const url = response.url || response.data?.url
  if (url) {
    form.coverImage = url
  }
}
const handleCoverRemove = () => { form.coverImage = '' }

const openAddDialog = () => {
  dialogTitle.value = '新增系列'
  resetForm()
  dialogVisible.value = true
}
const editSeries = (series: Series) => {
  dialogTitle.value = '编辑系列'
  form.id = series.id
  form.brandId = series.brandId
  form.name = series.name
  form.layType = series.layType
  form.material = series.material
  form.spec = series.spec
  form.coverImage = series.coverImage || ''
  form.description = series.description || ''
  if (series.coverImage) {
    coverFileList.value = [{
      name: 'cover',
      url: baseURL + series.coverImage,
      response: { url: series.coverImage },
      status: 'success'
    }] as UploadFile[]
  } else {
    coverFileList.value = []
  }
  dialogVisible.value = true
}
const confirmDelete = (series: Series) => {
  ElMessageBox.confirm(`确定要删除系列“${series.name}”吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await seriesApi.delete(series.id)
      ElMessage.success('删除成功')
      fetchSeries()
    })
    .catch(() => { })
}
const resetForm = () => {
  form.id = 0
  form.brandId = undefined
  form.name = ''
  form.layType = 'full'
  form.material = ''
  form.spec = ''
  form.coverImage = ''
  form.description = ''
  coverFileList.value = []
  formRef.value?.clearValidate()
}
const submitForm = async () => {
  await formRef.value?.validate()
  submitting.value = true
  try {
    const submitData = {
      brandId: form.brandId!,
      name: form.name,
      layType: form.layType,
      material: form.material,
      spec: form.spec,
      coverImage: form.coverImage,
      description: form.description
    }
    if (form.id) {
      await seriesApi.update(form.id, submitData)
      ElMessage.success('编辑成功')
    } else {
      await seriesApi.add(submitData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchSeries()
  } catch (error: unknown) {
    console.log(error);
    ElMessage.error('操作失败，请重试')
  } finally {
    submitting.value = false
  }
}

const getImageUrl = (path?: string) => {
  if (!path) {
    // 返回默认图片路径（根据你的实际存放位置调整）
    return '/default-cover.jpg'  // 建议将默认图片放在 public 目录下
  }
  if (path.startsWith('http')) {
    return path
  }
  // 如果路径以 /uploads 开头，直接返回（不需要拼接 baseURL）
  if (path.startsWith('/uploads')) {
    return path
  }
  // 其他情况（如开发环境相对路径）才拼接 baseURL
  return baseURL + path
}

// 查看详情
const viewDetail = (series: Series) => {
  router.push(`/merchant/series/${series.id}`)
}

// 监听筛选变化
watch([currentPage, pageSize], () => fetchSeries())

// 初始化
onMounted(() => {
  fetchBrands()
  fetchSeries()
})
</script>

<style scoped>
.merchant-series {
  max-width: 1400px;
  margin: 30px auto;
  padding: 0 20px;
}

.breadcrumb {
  margin-bottom: 20px;
}

.action-bar {
  margin-bottom: 20px;
}

.filter-card {
  margin-bottom: 20px;
  padding: 20px 20px 10px;
}

.series-grid {
  margin-top: 20px;
  min-height: 400px;
}

.grid-item {
  margin-bottom: 20px;
}

.series-card {
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.3s;
}

.series-card:hover {
  transform: translateY(-4px);
}

.card-cover {
  height: 160px;
  overflow: hidden;
  background: #f5f5f5;
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-info {
  padding: 15px;
}

.series-name {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 8px;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.series-meta {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.series-stats {
  font-size: 14px;
  color: #409EFF;
  margin-bottom: 12px;
}

.card-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.card-actions .el-button {
  flex: 1;
  min-width: 60px;
}

.pagination {
  margin-top: 30px;
  display: flex;
  justify-content: flex-end;
}
</style>