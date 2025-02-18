import axios from 'axios'
import { API_BASE_URL } from '@/config'

const instance = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json', // JSON 응답 기대
    },
})

instance.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response) {
            console.error(`❌ API 요청 실패 (HTTP ${error.response.status}):`, error.response.data)
        } else {
            console.error('❌ 네트워크 오류 또는 서버 응답 없음:', error)
        }
        return Promise.reject(error) // 호출한 곳에서 추가 처리 가능
    }
)

// 요청된 강의 불러오기
const getRequestedLecture = async (token) => {
    return instance.get(`/api/lecture-requests/create`, {
        headers: { Authorization: `Bearer ${token}` },
    })
}

// 요청된 강의 삭제
const deleteRequestedLecture = async (requestId, token) => {
    return instance.delete(`/api/lecture-requests/create/${requestId}`, {
        headers: { Authorization: `Bearer ${token}` },
    })
}

// 강의 등록
const addLecture = async (lectureData, token) => {
    return instance.post(`/api/lectures`, lectureData, {
        headers: { Authorization: `Bearer ${token}` },
    })
}

export { getRequestedLecture, deleteRequestedLecture, addLecture }
export default instance
