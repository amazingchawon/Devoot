import axios from 'axios'
import { API_BASE_URL } from '@/config'

const instance = axios.create({
    baseURL: API_BASE_URL,
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

// 유저 정보 가져오는 API 함수
const fetchTimelineList = async (token) => {
    return instance.get('/api/timeline', {
        headers: { Authorization: `Bearer ${token}` },
        params: { page: 1, size: 500 }, // ✅ 기본 페이지 설정 추가
    })
}

export { fetchTimelineList }
export default instance
