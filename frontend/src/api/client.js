import { ElMessage } from 'element-plus'

function getToken() {
  return localStorage.getItem('token')
}

async function request(path, options = {}) {
  const token = getToken()

  const headers = {
    ...(options.headers || {})
  }

  if (token) {
    headers['Authorization'] = 'Bearer ' + token
  }

  const hasBody = Object.prototype.hasOwnProperty.call(options, 'body')
  if (hasBody && headers['Content-Type'] == null) {
    headers['Content-Type'] = 'application/json'
  }

  try {
    const resp = await fetch('/api' + path, {
      ...options,
      headers
    })

    const json = await resp.json().catch(() => ({}))

    if (!resp.ok) {
      if (resp.status === 401) {
        localStorage.removeItem('token')
        localStorage.removeItem('userId')
        localStorage.removeItem('nickname')
        localStorage.removeItem('role')
        if (window.location.pathname !== '/login' && window.location.pathname !== '/register') {
          window.location.href = '/login'
        }
      }
      const msg = json?.message || ('请求失败: ' + resp.status)
      ElMessage.error(msg)
      throw new Error(msg)
    }

    if (json && typeof json.code === 'number' && json.code !== 0) {
      ElMessage.error(json.message || '业务失败')
      throw new Error(json.message || '业务失败')
    }

    return json
  } catch (error) {
    if (error.name === 'TypeError' && error.message === 'Failed to fetch') {
      ElMessage.error('网络错误，请检查后端服务是否启动')
    }
    throw error
  }
}

export { request }
