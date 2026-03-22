const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

export const getImageUrl = (path?: string) => {
  if (!path) {
    // 返回默认图片路径，建议放在 public 目录下，如 /default-cover.jpg
    return '/default-cover.jpg'
  }
  if (path.startsWith('http')) {
    return path
  }
  // 如果路径以 /uploads 开头，直接返回（不需要拼接 baseURL）
  if (path.startsWith('/uploads')) {
    return path
  }
  // 其他情况（可能开发环境直接相对路径）才拼接 baseURL
  return baseURL + path
}