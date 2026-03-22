<template>
  <el-dialog v-model="visible" title="支付" width="400px" @close="reset">
    <el-form ref="formRef" label-width="80px">
      <el-form-item label="订单号">
        <span>{{ orderNo }}</span>
      </el-form-item>
      <el-form-item label="支付密码" prop="password">
        <div class="password-grid">
          <el-input
            v-for="(digit, index) in digits"
            :key="index"
            v-model="digits[index]"
            :maxlength="1"
            class="password-cell"
            :ref="setInputRef(index)"
            @input="onInput(index, $event)"
            @keydown.delete="onDelete(index, $event)"
            @paste.prevent="onPaste"
            inputmode="numeric"
            pattern="[0-9]*"
          />
        </div>
      </el-form-item>
      <p class="tip">默认支付密码：V我50</p>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="submit" :loading="loading">确认支付</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, nextTick, type ComponentPublicInstance } from 'vue'
import { ElMessage } from 'element-plus'
import { orderApi } from '@/api/order'

const props = defineProps<{
  orderId: number
  orderNo: string
}>()

const emit = defineEmits<{
  (e: 'success'): void
}>()

const visible = ref(false)
const loading = ref(false)
const formRef = ref()

// 6位数字数组
const digits = ref<string[]>(['', '', '', '', '', ''])
const inputRefs = ref<HTMLInputElement[]>([])

// 设置输入框的引用，处理 Element Plus 组件实例与原生 input 的差异
const setInputRef = (index: number) => (el: HTMLElement | ComponentPublicInstance | null) => {
  if (!el) return
  let inputElement: HTMLInputElement | null = null
  // 检查是否是组件实例（具有 $el 属性）
  if ('$el' in el) {
    // 组件实例，尝试找到内部的 input 元素
    const rootEl = el.$el as HTMLElement
    inputElement = rootEl.querySelector('input')
  } else {
    // 已经是原生元素
    inputElement = el as HTMLInputElement
  }
  if (inputElement) {
    inputRefs.value[index] = inputElement
  }
}

// 输入事件
const onInput = (index: number, value: string) => {
  // 只保留数字
  const numeric = value.replace(/\D/g, '')
  digits.value[index] = numeric
  if (numeric && index < 5) {
    // 自动聚焦下一个
    nextTick(() => {
      inputRefs.value[index + 1]?.focus()
    })
  }
}

// 删除事件
const onDelete = (index: number, event: KeyboardEvent) => {
  if (event.key === 'Backspace') {
    if (digits.value[index]) {
      // 如果当前有值，清空当前
      digits.value[index] = ''
    } else if (index > 0) {
      // 如果当前无值，聚焦上一个并清空它
      digits.value[index - 1] = ''
      nextTick(() => {
        inputRefs.value[index - 1]?.focus()
      })
    }
  }
}

// 粘贴事件（一次性填充所有数字）
const onPaste = (event: ClipboardEvent) => {
  const text = event.clipboardData?.getData('text') || ''
  const numbers = text.replace(/\D/g, '').split('').slice(0, 6)
  numbers.forEach((char, i) => {
    if (i < 6) digits.value[i] = char
  })
  // 聚焦下一个空位或最后一个
  const nextEmptyIndex = digits.value.findIndex(d => !d)
  if (nextEmptyIndex !== -1) {
    inputRefs.value[nextEmptyIndex]?.focus()
  } else {
    inputRefs.value[5]?.focus()
  }
}

// 重置
const reset = () => {
  digits.value = ['', '', '', '', '', '']
  formRef.value?.clearValidate()
}

// 打开弹窗
const open = () => {
  reset()
  visible.value = true
  // 自动聚焦第一个输入框
  nextTick(() => {
    inputRefs.value[0]?.focus()
  })
}

// 提交
const submit = async () => {
  // 组合密码
  const password = digits.value.join('')
  if (password.length !== 6) {
    ElMessage.warning('请输入6位支付密码')
    return
  }
  loading.value = true
  try {
    await orderApi.pay(props.orderId, { password })
    ElMessage.success('支付成功')
    visible.value = false
    emit('success')
  } catch (error: unknown) {
    // 处理错误
    let errorMsg = '支付失败'
    if (error && typeof error === 'object' && 'response' in error) {
      const err = error as { response?: { data?: { message?: string } } }
      errorMsg = err.response?.data?.message || errorMsg
    }
    ElMessage.error(errorMsg)
    // 清空输入框，重新输入
    reset()
    nextTick(() => {
      inputRefs.value[0]?.focus()
    })
  } finally {
    loading.value = false
  }
}

defineExpose({ open })
</script>

<style scoped>
.password-grid {
  display: flex;
  gap: 8px;
  justify-content: center;
}
.password-cell {
  width: 40px;
  text-align: center;
}
.password-cell :deep(.el-input__wrapper) {
  padding: 0;
  border-radius: 4px;
}
.password-cell :deep(.el-input__inner) {
  text-align: center;
  font-size: 18px;
  font-weight: bold;
}
.tip {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
  text-align: center;
}
</style>