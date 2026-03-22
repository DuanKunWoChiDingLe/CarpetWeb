<template>
  <div class="merchant-series-detail">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/merchant/products' }">系列管理</el-breadcrumb-item>
      <el-breadcrumb-item>{{ seriesInfo.name || '系列详情' }}</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card class="series-info-card" v-loading="loadingSeries">
      <el-row :gutter="20">
        <el-col :span="24">
          <h2 class="series-title">{{ seriesInfo.name }}</h2>
          <div class="series-meta">
            <span>品牌：{{ seriesInfo.brandName }}</span>
            <el-divider direction="vertical" />
            <span>铺设方式：{{ seriesInfo.layType === 'full' ? '满铺毯' : '方块毯' }}</span>
            <el-divider direction="vertical" />
            <span>材质：{{ materialMap[seriesInfo.material] || seriesInfo.material }}</span>
            <el-divider direction="vertical" />
            <span>规格：{{ seriesInfo.spec }}</span>
          </div>
          <div class="series-desc" v-if="seriesInfo.description">
            {{ seriesInfo.description }}
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 操作栏：新增按钮 + 排序栏（保留原有排序栏） -->
    <div class="action-bar" style="margin-bottom: 20px; display: flex; justify-content: space-between; align-items: center;">
      <el-button type="primary" @click="openAddDialog">+ 新增色号</el-button>
      <div class="sort-bar">
        <span style="margin-right: 10px;">排序：</span>
        <el-select v-model="sortBy" placeholder="排序" size="small" style="width: 150px;" @change="handleSortChange">
          <el-option label="默认" value="default" />
          <el-option label="价格低到高" value="price_asc" />
          <el-option label="价格高到低" value="price_desc" />
          <el-option label="色号" value="colorCode" />
        </el-select>
      </div>
    </div>

    <div v-loading="loadingProducts" class="product-grid">
      <el-row :gutter="20">
        <el-col
          v-for="product in products"
          :key="product.id"
          :xs="24" :sm="12" :md="8" :lg="6"
          class="grid-item"
        >
          <el-card class="product-card" :body-style="{ padding: '0' }" shadow="hover">
            <div class="card-cover">
              <el-image
                :src="product.images?.[0] || ''"
                fit="cover"
                class="cover-image"
              />
            </div>
            <div class="card-info">
              <h3 class="product-name">{{ product.name }}</h3>
              <div class="product-price">¥{{ product.pricePerSqm.toFixed(2) }}/m²</div>
              <div class="product-stock">库存：{{ product.stock }}</div>
              <div class="card-actions">
                <el-button type="warning" size="small" @click="editProduct(product)">编辑</el-button>
                <el-button type="danger" size="small" @click="confirmDelete(product)">删除</el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <el-empty v-if="!loadingProducts && products.length === 0" description="暂无色号" />

    <!-- 分页控件（添加条件隐藏） -->
    <el-pagination
      v-if="total > pageSize"
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :total="total"
      :page-sizes="[12, 24, 36]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      class="pagination"
    />

    <!-- 编辑商品弹窗（原有） -->
    <el-dialog v-model="dialogVisible" title="编辑商品" width="600px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="品牌">
          <el-input :value="seriesInfo.brandName" disabled />
        </el-form-item>
        <el-form-item label="系列">
          <el-input :value="seriesInfo.name" disabled />
        </el-form-item>
        <el-form-item label="色号" prop="colorCode">
          <el-input v-model="form.colorCode" maxlength="5" show-word-limit placeholder="例如 01、02A" />
        </el-form-item>
        <el-form-item label="铺设方式">
          <el-input :value="seriesInfo.layType === 'full' ? '满铺毯' : '方块毯'" disabled />
        </el-form-item>
        <el-form-item label="材质">
          <el-input :value="materialMap[seriesInfo.material] || seriesInfo.material" disabled />
        </el-form-item>
        <el-form-item label="规格">
          <el-input :value="seriesInfo.spec" disabled />
        </el-form-item>
        <el-form-item label="价格(元/m²)" prop="pricePerSqm">
          <el-input-number v-model="form.pricePerSqm" :precision="2" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="商品图片" prop="images">
          <el-upload
            v-model:file-list="fileList"
            :action="`${baseURL}/upload/image`"
            :headers="{ Authorization: `Bearer ${userStore.token}` }"
            list-type="picture-card"
            :limit="5"
            multiple
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :on-remove="handleRemove"
            :on-preview="handlePreview"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="form-tip">最多上传5张，第一张作为主图</div>
          <el-dialog v-model="previewVisible" append-to-body>
            <img :src="previewUrl" alt="预览" style="width: 100%" />
          </el-dialog>
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="上架状态" prop="status">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 新增商品弹窗（新增） -->
    <el-dialog v-model="addDialogVisible" title="新增色号" width="600px" @close="resetAddForm">
      <el-form ref="addFormRef" :model="addForm" :rules="rules" label-width="100px">
        <el-form-item label="品牌">
          <el-input :value="seriesInfo.brandName" disabled />
        </el-form-item>
        <el-form-item label="系列">
          <el-input :value="seriesInfo.name" disabled />
        </el-form-item>
        <el-form-item label="色号" prop="colorCode">
          <el-input v-model="addForm.colorCode" maxlength="5" show-word-limit placeholder="例如 01、02A" />
          <div class="form-tip">商品名称将自动生成为“系列名+色号”</div>
        </el-form-item>
        <el-form-item label="铺设方式">
          <el-input :value="seriesInfo.layType === 'full' ? '满铺毯' : '方块毯'" disabled />
        </el-form-item>
        <el-form-item label="材质">
          <el-input :value="materialMap[seriesInfo.material] || seriesInfo.material" disabled />
        </el-form-item>
        <el-form-item label="规格">
          <el-input :value="seriesInfo.spec" disabled />
        </el-form-item>
        <el-form-item label="价格(元/m²)" prop="pricePerSqm">
          <el-input-number v-model="addForm.pricePerSqm" :precision="2" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="addForm.stock" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="商品图片" prop="images">
          <el-upload
            v-model:file-list="addFileList"
            :action="`${baseURL}/upload/image`"
            :headers="{ Authorization: `Bearer ${userStore.token}` }"
            list-type="picture-card"
            :limit="5"
            multiple
            :on-success="handleAddUploadSuccess"
            :on-error="handleUploadError"
            :on-remove="handleAddRemove"
            :on-preview="handlePreview"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="form-tip">最多上传5张，第一张作为主图</div>
          <el-dialog v-model="previewVisible" append-to-body>
            <img :src="previewUrl" alt="预览" style="width: 100%" />
          </el-dialog>
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input v-model="addForm.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="上架状态" prop="status">
          <el-switch v-model="addForm.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAddForm" :loading="addSubmitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type UploadFile } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { seriesApi, type Series } from '@/api/series'
import { productApi, type Product } from '@/api/product'
import { type UploadResponse } from '@/api/upload'
import type { UploadFileResponse } from '@/types/upload'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

const materialMap: Record<string, string> = {
  polypropylene: '丙纶',
  polyester: '涤纶',
  nylon: '尼龙'
}

const seriesId = computed(() => route.params.id as string)

// 系列信息
const loadingSeries = ref(false)
const seriesInfo = ref<Series>({
  id: 0,
  brandId: 0,
  brandName: '',
  name: '',
  layType: 'full',
  material: '',
  spec: '',
  description: ''
})

// 色号列表
const loadingProducts = ref(false)
const products = ref<Product[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(12)
const sortBy = ref('default')

// 获取系列详情
const fetchSeriesDetail = async () => {
  if (!seriesId.value) return
  loadingSeries.value = true
  try {
    const res = await seriesApi.getDetail(Number(seriesId.value))
    seriesInfo.value = res
  } catch (error) {
    console.log(error);
    
    ElMessage.error('获取系列详情失败')
    router.push('/merchant/products')
  } finally {
    loadingSeries.value = false
  }
}

// 获取商品列表
const fetchProducts = async () => {
  if (!seriesId.value) return
  loadingProducts.value = true
  try {
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value,
      sort: sortBy.value
    }
    const res = await productApi.getListBySeries(Number(seriesId.value), params)
    products.value = res.content || []
    total.value = res.totalElements || 0
  } catch (error) {
    console.log(error);
    products.value = []
    total.value = 0
    ElMessage.error('获取色号列表失败')
  } finally {
    loadingProducts.value = false
  }
}

// 排序变化
const handleSortChange = () => {
  currentPage.value = 1
  fetchProducts()
}
const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  fetchProducts()
}
const handleCurrentChange = (page: number) => {
  currentPage.value = page
  fetchProducts()
}

// ---------- 编辑商品相关 ----------
const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref<FormInstance | null>(null)
const form = reactive({
  id: 0,
  seriesId: 0,
  colorCode: '',
  pricePerSqm: undefined as number | undefined,
  stock: undefined as number | undefined,
  images: [] as string[],
  description: '',
  status: 1
})
const fileList = ref<UploadFile[]>([])
const previewVisible = ref(false)
const previewUrl = ref('')

const rules = {
  colorCode: [
    { required: true, message: '请输入色号', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9]+$/, message: '色号只能包含字母和数字', trigger: 'blur' }
  ],
  pricePerSqm: [
    { required: true, message: '请输入价格', trigger: 'blur' },
    { type: 'number', min: 0, message: '价格不能小于0', trigger: 'blur' }
  ],
  stock: [
    { required: true, message: '请输入库存', trigger: 'blur' },
    { type: 'number', min: 0, message: '库存不能小于0', trigger: 'blur' }
  ]
}

const handleUploadSuccess = (response: UploadResponse, file: UploadFile, fileList: UploadFile[]) => {
  const url = response.data?.url || response.url
  if (url) {
    // 直接使用相对路径，不再拼接 baseURL
    file.url = url
    file.response = { url } as UploadFileResponse
    form.images = fileList.map(f => (f.response as UploadFileResponse)?.url).filter(Boolean) as string[]
  }
}
const handleUploadError = () => ElMessage.error('图片上传失败')
const handleRemove = (file: UploadFile, fileList: UploadFile[]) => {
  form.images = fileList.map(f => (f.response as UploadFileResponse)?.url).filter(Boolean) as string[]
}
const handlePreview = (file: UploadFile) => {
  previewUrl.value = file.url!
  previewVisible.value = true
}

const editProduct = (product: Product) => {
  form.id = product.id
  form.seriesId = product.seriesId
  form.colorCode = product.colorCode
  form.pricePerSqm = product.pricePerSqm
  form.stock = product.stock
  form.images = product.images || []
  form.description = product.description || ''
  form.status = product.status
  // 确保数字类型
  form.pricePerSqm = Number(product.pricePerSqm)
  form.stock = Number(product.stock)
  // 文件列表中的 url 也直接使用相对路径
  fileList.value = (product.images || []).map((url: string, index: number) => ({
    name: `image-${index}`,
    url: url,
    response: { url },
    status: 'success'
  })) as UploadFile[]
  dialogVisible.value = true
}

const confirmDelete = (product: Product) => {
  ElMessageBox.confirm(`确定要删除商品“${product.name}”吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await productApi.delete(product.id)
      ElMessage.success('删除成功')
      fetchProducts()
    })
    .catch(() => {})
}

const resetForm = () => {
  form.id = 0
  form.colorCode = ''
  form.pricePerSqm = undefined
  form.stock = undefined
  form.images = []
  form.description = ''
  form.status = 1
  fileList.value = []
  formRef.value?.clearValidate()
}

const submitForm = async () => {
  submitting.value = true
  try {
    await formRef.value?.validate()
    await productApi.update(form.id, {
      seriesId: form.seriesId,
      colorCode: form.colorCode,
      pricePerSqm: form.pricePerSqm,
      stock: form.stock,
      images: form.images,
      description: form.description,
      status: form.status
    })
    ElMessage.success('更新成功')
    dialogVisible.value = false
    fetchProducts()
  } catch (error: unknown) {
    if (error && typeof error === 'object' && 'fields' in error) {
      // 表单验证失败，输入框已显示具体错误，无需额外操作
    } else if (error && typeof error === 'object' && 'response' in error) {
      const axiosError = error as { response?: { data?: { message?: string } } }
      ElMessage.error(axiosError.response?.data?.message || '更新失败')
    } else {
      ElMessage.error('更新失败，请重试')
    }
  } finally {
    submitting.value = false
  }
}

// ---------- 新增商品相关 ----------
const addDialogVisible = ref(false)
const addSubmitting = ref(false)
const addFormRef = ref<FormInstance | null>(null)
const addForm = reactive({
  seriesId: Number(route.params.id),
  colorCode: '',
  pricePerSqm: undefined as number | undefined,
  stock: undefined as number | undefined,
  images: [] as string[],
  description: '',
  status: 1
})
const addFileList = ref<UploadFile[]>([])

const handleAddUploadSuccess = (response: UploadResponse, file: UploadFile, fileList: UploadFile[]) => {
  const url = response.data?.url
  if (url) {
    file.url = url
    file.response = { url }
    const urls = fileList.map(f => f.response?.url).filter(Boolean) as string[]
    addForm.images = urls
  } else {
    ElMessage.error('图片上传失败：返回数据格式异常')
  }
}

const handleAddRemove = (file: UploadFile, fileList: UploadFile[]) => {
  const urls = fileList.map(f => f.response?.url).filter(Boolean) as string[]
  addForm.images = urls
}

const openAddDialog = () => {
  addForm.colorCode = ''
  addForm.pricePerSqm = undefined
  addForm.stock = undefined
  addForm.images = []
  addForm.description = ''
  addForm.status = 1
  addFileList.value = []
  addDialogVisible.value = true
}

const resetAddForm = () => {
  addFormRef.value?.clearValidate()
}

const submitAddForm = async () => {
  addSubmitting.value = true
  try {
    await addFormRef.value?.validate()
    await productApi.add({
      seriesId: addForm.seriesId,
      colorCode: addForm.colorCode,
      pricePerSqm: addForm.pricePerSqm,
      stock: addForm.stock,
      images: addForm.images,
      description: addForm.description,
      status: addForm.status
    })
    ElMessage.success('新增成功')
    addDialogVisible.value = false
    fetchProducts()
  } catch (error: unknown) {
    console.log(error);
    ElMessage.error('新增失败，请重试')
  } finally {
    addSubmitting.value = false
  }
}

// 初始化
onMounted(() => {
  fetchSeriesDetail()
  fetchProducts()
})

// 监听分页和排序变化
watch([currentPage, pageSize, sortBy], () => {
  fetchProducts()
})
</script>

<style scoped>
.merchant-series-detail {
  max-width: 1400px;
  margin: 30px auto;
  padding: 0 20px;
}
.breadcrumb { margin-bottom: 20px; }
.series-info-card { margin-bottom: 20px; background: linear-gradient(135deg, #f5f7fa 0%, #e9ecef 100%); border: none; }
.series-title { font-size: 24px; font-weight: 600; margin: 0 0 10px; color: #333; }
.series-meta { font-size: 14px; color: #666; margin-bottom: 10px; }
.series-desc { font-size: 14px; color: #555; background: rgba(255,255,255,0.6); padding: 12px; border-radius: 6px; line-height: 1.6; }
.sort-bar { margin-bottom: 20px; text-align: right; }
.product-grid { margin-top: 20px; min-height: 400px; }
.grid-item { margin-bottom: 20px; }
.product-card { border-radius: 8px; overflow: hidden; transition: transform 0.3s; }
.product-card:hover { transform: translateY(-4px); }
.card-cover { height: 160px; overflow: hidden; background: #f5f5f5; }
.cover-image { width: 100%; height: 100%; object-fit: cover; }
.card-info { padding: 15px; }
.product-name { font-size: 16px; font-weight: 600; margin: 0 0 8px; color: #333; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.product-price { font-size: 16px; color: #f56c6c; font-weight: 500; margin-bottom: 4px; }
.product-stock { font-size: 14px; color: #999; margin-bottom: 12px; }
.card-actions { display: flex; gap: 8px; }
.card-actions .el-button { flex: 1; }
.pagination { margin-top: 30px; display: flex; justify-content: flex-end; }
.form-tip { font-size: 12px; color: #999; margin-top: 4px; }
</style>