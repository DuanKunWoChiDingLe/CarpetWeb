import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useNotificationStore = defineStore('notification', () => {
  const newOrderCount = ref(0)

  function setNewOrderCount(count: number) {
    newOrderCount.value = count
  }

  return { newOrderCount, setNewOrderCount }
})