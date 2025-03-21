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
        return Promise.reject(error)
    }
)

// page 인자를 추가하여 동적으로 페이지 번호를 전달
const fetchTimelineList = async (token, page) => {
    return instance.get('/api/timeline', {
        headers: { Authorization: `Bearer ${token}` },
        params: { page, size: 10 },
    })
}

export { fetchTimelineList }
export default instance
