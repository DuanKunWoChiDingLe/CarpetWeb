import request from './request'

export interface UploadResponse {
  url: string
}

export const uploadApi = {
  // 上传图片
  uploadImage(file: File) {
    const formData = new FormData()
    formData.append('file', file)
    return request.post<any, UploadResponse>('/api/upload/image', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}