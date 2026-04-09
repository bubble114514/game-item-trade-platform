import { defineStore } from 'pinia'
import { ref } from 'vue'
import { request } from '../api/client.js'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userId = ref(localStorage.getItem('userId') || '')
  const nickname = ref(localStorage.getItem('nickname') || '')
  const role = ref(localStorage.getItem('role') || '')

  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const setUserInfo = (info) => {
    userId.value = info.userId
    nickname.value = info.nickname
    role.value = info.role
    localStorage.setItem('userId', info.userId)
    localStorage.setItem('nickname', info.nickname)
    localStorage.setItem('role', info.role)
  }

  const clearUser = () => {
    token.value = ''
    userId.value = ''
    nickname.value = ''
    role.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('nickname')
    localStorage.removeItem('role')
  }

  const fetchCurrentUser = async () => {
    if (!token.value) return null
    try {
      const res = await request('/user/auth/current', { method: 'GET' })
      if (res.data) {
        setUserInfo(res.data)
        return res.data
      }
    } catch (e) {
      console.error('Failed to fetch current user:', e)
      clearUser()
    }
    return null
  }

  const login = async (username, password) => {
    const res = await request('/user/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username, password })
    })
    if (res.data && res.data.token) {
      setToken(res.data.token)
      setUserInfo(res.data)
      return res.data
    }
    throw new Error('登录失败，未获取到Token')
  }

  const logout = async () => {
    try {
      await request('/user/auth/logout', { method: 'POST' })
    } catch (e) {
      console.error('Logout request failed:', e)
    } finally {
      clearUser()
    }
  }

  return {
    token,
    userId,
    nickname,
    role,
    setToken,
    setUserInfo,
    clearUser,
    fetchCurrentUser,
    login,
    logout
  }
})
