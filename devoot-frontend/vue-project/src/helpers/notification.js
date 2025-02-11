// src\helpers\notification.js

import axios from 'axios'

const instance = axios.create({
    baseURL: 'http://localhost:8080',
    headers: {
        'Content-Type': 'application/json',
    },
})

// 응답 인터셉터: 오류 처리
instance.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response) {
            console.error(`❌ API 요청 실패 (HTTP ${error.response.status}):`, error.response.data)
        } else {
            console.error('❌ 네트워크 오류 또는 서버 응답 없음:', error)
        }
        return Promise.reject(error)
    }
)

// ✅ 읽지 않은 알림 여부 확인 API 함수
const hasUnread = async (token) => {
    return instance.get('/api/notifications/unread', {
        headers: { Authorization: `Bearer ${token}` },
    })
}

// ✅ 알림 목록을 가져오는 함수 (함수명 수정됨)
const getNotifications = async (token) => {
    try {
        const response = await instance.get('/api/notifications?page=1&size=10', {
            headers: { Authorization: `Bearer ${token}` },
        })
        return response.data
    } catch (error) {
        console.error('❌ 알림 데이터 가져오기 실패:', error)
        throw error
    }
}

export { hasUnread, getNotifications }
export default instance
