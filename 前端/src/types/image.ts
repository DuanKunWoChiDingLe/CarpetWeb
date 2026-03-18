const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

export const getImageUrl = (path?: string) => {
  if (!path) {
    // 返回默认图片路径（根据你的实际存放位置调整）
    return new URL('/src/assets/default-cover.jpg', import.meta.url).href
  }
  if (path.startsWith('http')) {
    return path
  }
  return baseURL + path
}