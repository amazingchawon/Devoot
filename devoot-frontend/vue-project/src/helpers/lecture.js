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

//===============================================
// 홈 화면 강의 조회회 관련 API
//===============================================

// 인기, 신규규 강의 목록 조회 API
//  @param {string} order - 'popular' 또는 'newest'
//  @returns {Promise<Array>} 강의 목록 배열

const getLecture = async (options = {}) => {
    try {
        const params = { limit: 8, ...options } // 기본적으로 8개 제한
        const response = await instance.get('/lectures/search', { params })
        return response.data
    } catch (error) {
        console.error('강의 목록을 불러오는 중 오류 발생:', error)
        return []
    }
}
//===============================================
// 북마크 관련 API
//===============================================

// 북마크 추가
const addBookmark = async (token, profileId, lectureId) => {
    return instance.post(
        `/api/users/${profileId}/bookmarks`,
        { lectureId }, // body에 포함
        {
            headers: { Authorization: `Bearer ${token}` },
        }
    )
}

// 북마크 제거
const removeBookmark = async (token, profileId, lectureId) => {
    return instance.delete(`/api/users/${profileId}/bookmarks/${lectureId}`, {
        headers: { Authorization: `Bearer ${token}` },
    })
}

//===============================================
// 강의 상세 관련 API
//===============================================

// 강의 상세 불러오기
const getLectureDetail = async (lectureId) => {
    return instance.get(`/api/lecture/detail/${lectureId}`)
}

// 강의 리뷰 불러오기
const getLectureReview = async (lectureId, pageIndex) => {
    return instance.get(`/api/reviews/lectures/${lectureId}`, { params: { pageIndex } })
}

// 본인의 리뷰 가져오기
const getSelfReview = async (token, lectureId) => {
    return instance.get(`/api/reviews/lectures/${lectureId}/my-review`, {
        headers: { Authorization: `Bearer ${token}` },
    })
}

// 강의 리뷰 등록
const writeLectureReview = async (token, lectureId, score, content) => {
    return instance.post(
        `/api/reviews`,
        { lectureId, score, content }, // body에 포함
        {
            headers: { Authorization: `Bearer ${token}` },
        }
    )
}

// 강의 리뷰 수정
const editLectureReview = async (token, reviewId, lectureId, score, content) => {
    return instance.patch(
        `/api/reviews/${reviewId}`,
        { lectureId, score, content }, // body에 포함
        {
            headers: { Authorization: `Bearer ${token}` },
        }
    )
}

export {
    getLecture,
    addBookmark,
    removeBookmark,
    getLectureDetail,
    getLectureReview,
    getSelfReview,
    writeLectureReview,
    editLectureReview,
}
export default instance
