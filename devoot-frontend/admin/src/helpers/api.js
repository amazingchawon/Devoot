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

            // 특정 에러 코드에 대한 처리
            const { status, data } = error.response

            if (status === 400) {
                if (data.code === 'COMMON_400_1') {
                    console.error('🚨 잘못된 데이터 입력:', data.errors)
                    return Promise.reject({ type: 'VALIDATION_ERROR', errors: data.errors })
                }
                if (data.code === 'S3_400_1') {
                    console.error('🚨 S3 이미지 업로드 실패:', data.detail || data.message)
                    return Promise.reject({
                        type: 'S3_ERROR',
                        message: data.detail || data.message,
                    })
                }
            }
        } else {
            console.error('❌ 네트워크 오류 또는 서버 응답 없음:', error)
        }

        return Promise.reject(error) // 호출한 곳에서 추가 처리 가능
    }
)

//===============================================
// 회원 관리 관련 API
//===============================================

// 유저 정보 가져오는 API 함수
const getUserInfo = async (token) => {
    return instance.get('/api/login', {
        headers: { Authorization: `Bearer ${token}` },
    })
}

//===============================================
// 대시보드 API
//===============================================
// 관리자 유저 가져오는 API 함수
const getAdminUser = async (token) => {
    return instance.get('/api/users/admin', {
        headers: { Authorization: `Bearer ${token}` },
    })
}

//===============================================
// 강의 수정 API
//===============================================
// 강의 수정 요청 가져오는 API 함수
const getEditRequest = async (token) => {
    return instance.get('/api/lecture-requests/update', {
        headers: { Authorization: `Bearer ${token}` },
    })
}

//===============================================
// 댓글 관리 API
//===============================================
// 댓글 신고된 유저 가져오는 API 함수
const getReportedUsers = async (token, page, size) => {
    return instance.get('/api/users/reported', {
        headers: { Authorization: `Bearer ${token}` },
        params: { page, size },
    })
}

// 신고된 유저의 모든 리뷰를 가져오는 API 함수
const getReviewOfUser = async (token, profileId) => {
    return instance.get(`/api/users/${profileId}/reviews`, {
        headers: { Authorization: `Bearer ${token}` },
    })
}

// 신고된 유저의 모든 리뷰를 지우는 API 함수
const deleteReviewOfUser = async (token, profileId) => {
    return instance.delete(`/api/reviews/of/${profileId}`, {
        headers: { Authorization: `Bearer ${token}` },
    })
}

// 리뷰 신고 기록을 지우는 API 함수
const deleteReportOfUser = async (token, profileId) => {
    return instance.delete(`/api/reviews/reports/of/${profileId}`, {
        headers: { Authorization: `Bearer ${token}` },
    })
}

// 유저의 모든 댓글을 지우는 API 함수

export {
    getUserInfo,
    getAdminUser,
    getEditRequest,
    getReportedUsers,
    getReviewOfUser,
    deleteReviewOfUser,
    deleteReportOfUser,
}
export default instance
