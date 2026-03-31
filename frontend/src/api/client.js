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

  const resp = await fetch('/api' + path, {
    ...options,
    headers
  })

  const json = await resp.json().catch(() => ({}))

  if (!resp.ok) {
    const msg = json?.message || ('请求失败: ' + resp.status)
    throw new Error(msg)
  }

  if (json && typeof json.code === 'number' && json.code !== 0) {
    throw new Error(json.message || '业务失败')
  }

  return json
}

export { request }

