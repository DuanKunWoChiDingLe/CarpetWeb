<template>
  <div class="banner-management">
    <div class="action-bar">
      <el-button type="primary" @click="handleAdd">新增轮播图</el-button>
    </div>
    <el-table class="banner-table" :data="bannerList" border stripe style="width: 100%" v-loading="loading">
      <el-table-column label="图片" width="120">
        <template #default="{ row }">
          <el-image :src="getImageUrl(row.imageUrl)" fit="cover" style="width: 80px; height: 60px;" />
        </template>
      </el-table-column>
      <el-table-column prop="linkType" label="链接类型" width="100">
        <template #default="{ row }">
          <el-tag>{{ row.linkType === 'product' ? '商品' : 'URL' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="linkValue" label="链接值" min-width="150" show-overflow-tooltip />
      <el-table-column prop="sortOrder" label="排序" width="80" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-switch v-model="row.enabled" @change="toggleEnabled(row)" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="mobile-banner-list">
      <div v-for="row in bannerList" :key="row.id" class="mobile-banner-card">
        <el-image :src="getImageUrl(row.imageUrl)" fit="cover" class="mobile-banner-image" />
        <div class="mobile-banner-info">
          <div class="mobile-banner-line">
            <el-tag size="small">{{ row.linkType === 'product' ? '商品' : 'URL' }}</el-tag>
            <span class="mobile-banner-link">{{ row.linkValue }}</span>
          </div>
          <div class="mobile-banner-line">
            <span>排序：{{ row.sortOrder }}</span>
            <el-switch v-model="row.enabled" @change="toggleEnabled(row)" />
          </div>
          <div class="mobile-banner-actions">
            <el-button size="small" type="primary" plain @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" plain @click="handleDelete(row)">删除</el-button>
          </div>
        </div>
      </div>
    </div>
    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :total="total"
      :page-sizes="[5, 10, 20]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      class="pagination"
    />

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="图片" prop="imageUrl">
          <el-upload
            :action="`${baseURL}/upload/image`"
            :headers="{ Authorization: `Bearer ${userStore.token}` }"
            list-type="picture-card"
            :limit="1"
            :on-success="handleUploadSuccess"
            :on-remove="handleUploadRemove"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div v-if="form.imageUrl" class="upload-tip">已上传</div>
        </el-form-item>
        <el-form-item label="链接类型" prop="linkType">
          <el-radio-group v-model="form.linkType">
            <el-radio value="product">商品详情</el-radio>
            <el-radio value="url">外部URL</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="链接值" prop="linkValue">
          <el-input v-model="form.linkValue" placeholder="商品ID或完整URL" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="启用" prop="enabled">
          <el-switch v-model="form.enabled" />
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type UploadFile } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { bannerApi, type Banner } from '@/api/banner'
import type { UploadResponse } from '@/api/upload'
import { getImageUrl } from '@/utils/image'
import { apiBaseUrl as baseURL } from '@/config/runtime'

const userStore = useUserStore()

// 列表数据
const loading = ref(false)
const bannerList = ref<Banner[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 弹窗
const dialogVisible = ref(false)
const dialogTitle = ref('新增轮播图')
const submitting = ref(false)
const formRef = ref()
const form = reactive({
  id: 0,
  imageUrl: '',
  linkType: 'product' as 'product' | 'url',
  linkValue: '',
  sortOrder: 0,
  enabled: true,
})

// 表单校验
const rules = {
  linkType: [{ required: true, message: '请选择链接类型', trigger: 'change' }],
  linkValue: [{ required: true, message: '请输入链接值', trigger: 'blur' }],
  sortOrder: [{ required: true, message: '请输入排序', trigger: 'blur' }],
}


// 获取列表
const fetchBanners = async () => {
  loading.value = true
  try {
    const res = await bannerApi.getPage({
      page: currentPage.value - 1,
      size: pageSize.value,
    })
    bannerList.value = res.content
    total.value = res.totalElements
  } catch (error) {
    console.log(error);
    ElMessage.error('获取轮播图列表失败')
  } finally {
    loading.value = false
  }
}

// 分页
const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  fetchBanners()
}
const handleCurrentChange = (page: number) => {
  currentPage.value = page
  fetchBanners()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增轮播图'
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: Banner) => {
  dialogTitle.value = '编辑轮播图'
  Object.assign(form, {
    id: row.id,
    imageUrl: row.imageUrl,
    linkType: row.linkType,
    linkValue: row.linkValue,
    sortOrder: row.sortOrder,
    enabled: row.enabled,
  })
  dialogVisible.value = true
}

// 删除
const handleDelete = (row: Banner) => {
  ElMessageBox.confirm('确定删除该轮播图吗？', '提示', { type: 'warning' })
    .then(async () => {
      await bannerApi.delete(row.id)
      ElMessage.success('删除成功')
      fetchBanners()
    })
    .catch(() => {})
}

// 切换状态
const toggleEnabled = async (row: Banner) => {
  try {
    await bannerApi.updateStatus(row.id, row.enabled)
    ElMessage.success(`已${row.enabled ? '启用' : '禁用'}`)
  } catch {
    // 如果失败，恢复状态
    row.enabled = !row.enabled
    ElMessage.error('操作失败')
  }
}

// 图片上传
const handleUploadSuccess = (response: UploadResponse, _file: UploadFile) => {
  void _file
  const url = response.data?.url || response.url
  if (url) {
    form.imageUrl = url
    console.log('图片URL已设置:', url)
  } else {
    console.warn('上传响应中未找到URL', response)
    ElMessage.error('图片上传失败，响应格式异常')
  }
}
const handleUploadRemove = () => {
  form.imageUrl = ''
}

// 提交表单
const submitForm = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (form.id) {
      await bannerApi.update(form.id, {
        imageUrl: form.imageUrl,
        linkType: form.linkType,
        linkValue: form.linkValue,
        sortOrder: form.sortOrder,
        enabled: form.enabled,
      })
      ElMessage.success('编辑成功')
    } else {
      await bannerApi.add({
        imageUrl: form.imageUrl,
        linkType: form.linkType,
        linkValue: form.linkValue,
        sortOrder: form.sortOrder,
        enabled: form.enabled,
      })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchBanners()
  } catch (error) {
    // 错误已由拦截器处理
    console.log(error);
    
  } finally {
    submitting.value = false
  }
}

const resetForm = () => {
  form.id = 0
  form.imageUrl = ''
  form.linkType = 'product'
  form.linkValue = ''
  form.sortOrder = 0
  form.enabled = true
  formRef.value?.clearValidate()
}

onMounted(() => {
  fetchBanners()
})
</script>

<style scoped>
.banner-management {
  padding: 10px;
}
.action-bar {
  margin-bottom: 20px;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
.upload-tip {
  font-size: 12px;
  color: #67c23a;
  margin-top: 5px;
}
.mobile-banner-list {
  display: none;
}

@media (max-width: 767px) {
  .banner-table {
    display: none !important;
  }

  .mobile-banner-list {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }

  .mobile-banner-card {
    display: flex;
    gap: 12px;
    padding: 10px;
    background: #fff;
    border: 1px solid #ebeef5;
    border-radius: 10px;
  }

  .mobile-banner-image {
    width: 110px;
    height: 82px;
    flex: 0 0 110px;
    border-radius: 8px;
    overflow: hidden;
  }

  .mobile-banner-info {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    gap: 8px;
  }

  .mobile-banner-line {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 8px;
    color: #606266;
    font-size: 13px;
  }

  .mobile-banner-link {
    min-width: 0;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .mobile-banner-actions {
    display: flex;
    gap: 8px;
  }

  .mobile-banner-actions .el-button {
    flex: 1;
    margin-left: 0;
  }
}
</style>



