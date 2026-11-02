import { assetBaseUrl } from '@/config/runtime'

export const DEFAULT_IMAGE = '/images/default-cover.svg'

export function getAssetUrl(path?: string | null, fallback = DEFAULT_IMAGE): string {
  if (!path) return fallback
  if (/^(https?:)?\/\//i.test(path) || /^(data|blob):/i.test(path)) return path

  const normalized = path.startsWith('/') ? path : `/${path}`
  return `${assetBaseUrl}${normalized}`
}

export const getImageUrl = getAssetUrl

