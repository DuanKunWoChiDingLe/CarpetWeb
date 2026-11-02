const trimTrailingSlash = (value?: string) => (value || '').replace(/\/+$/, '')

const apiOrigin = trimTrailingSlash(import.meta.env.VITE_API_ORIGIN)
const explicitApiBase = trimTrailingSlash(import.meta.env.VITE_API_BASE_URL)

// Render static sites call the backend through its public origin. Local development
// keeps using the Vite proxy when VITE_API_ORIGIN is absent.
export const apiBaseUrl = apiOrigin ? `${apiOrigin}/api` : explicitApiBase || '/api'
export const assetBaseUrl = trimTrailingSlash(import.meta.env.VITE_ASSET_BASE_URL) || apiOrigin
export const wsBaseUrl = trimTrailingSlash(import.meta.env.VITE_WS_BASE_URL) || apiOrigin
