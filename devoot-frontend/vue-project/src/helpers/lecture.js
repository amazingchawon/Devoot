import axios from 'axios'
import { API_BASE_URL } from '@/config'

// axios 인스턴스 생성 및 기본 설정
const instance = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json', // JSON 응답 기대
    },
})

// ------------------------------------------
// 요청 인터셉터: 호출하는 API 정보와 쿼리 파라미터를 출력
// ------------------------------------------
instance.interceptors.request.use(
    (config) => {
        // API 호출 전에 method, url, params를 콘솔에 출력 (디버깅용)
        console.log(
            `🔍 API 호출: ${config.method.toUpperCase()} ${config.url}`,
            config.params || {}
        )
        return config
    },
    (error) => {
        console.error('API 요청 오류:', error)
        return Promise.reject(error)
    }
)

// ------------------------------------------
// 응답 인터셉터: 응답 데이터를 콘솔에 출력
// ------------------------------------------
instance.interceptors.response.use(
    (response) => {
        // 응답 성공 시 method, url, 응답 데이터를 콘솔에 출력 (디버깅용)
        console.log(
            `✅ API 응답: ${response.config.method.toUpperCase()} ${response.config.url}`,
            response.data
        )
        return response
    },
    (error) => {
        if (error.response) {
            console.error(`❌ API 요청 실패 (HTTP ${error.response.status}):`, error.response.data)
        } else {
            console.error('❌ 네트워크 오류 또는 서버 응답 없음:', error)
        }
        return Promise.reject(error) // 호출한 곳에서 추가 처리 가능
    }
)

// 응답 인터셉터: 에러 발생 시 콘솔에 출력
// instance.interceptors.response.use(
//     (response) => response,
//     (error) => {
//         if (error.response) {
//             console.error(`❌ API 요청 실패 (HTTP ${error.response.status}):`, error.response.data)
//         } else {
//             console.error('❌ 네트워크 오류 또는 서버 응답 없음:', error)
//         }
//         return Promise.reject(error) // 호출한 곳에서 추가 처리 가능
//     }
// )

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
const removeBookmark = async (token, profileId, bookmarkId) => {
    return instance.delete(`/api/users/${profileId}/bookmarks/${bookmarkId}`, {
        headers: { Authorization: `Bearer ${token}` },
    })
}

//===============================================
// 강의 조회 관련 API
//===============================================

const searchLectures = async (params = {}) => {
    return instance.get('/api/lectures/search', { params })
}

//===============================================
// 강의 상세 관련 API
//===============================================

// 강의 상세 불러오기
const getLectureDetail = async (token, lectureId) => {
    return instance.get(`/api/lectures/${lectureId}`, {
        headers: { Authorization: `Bearer ${token}` },
    })
}

const getLectureDetailWithLogout = async (lectureId) => {
    return instance.get(`/api/lectures/${lectureId}`, {
        headers: { Authorization: null },
    })
}

const reportLecture = async (token, lectureId) => {
    return instance.post(
        `/api/lecture-requests/update`,
        { lectureId },

        {
            headers: { Authorization: `Bearer ${token}` },
        }
    )
}
//===============================================
// 강의 리뷰 관련 API
//===============================================

// 강의 리뷰 불러오기
const getLectureReview = async (lectureId, pageIndex) => {
    return instance.get(`/api/reviews/lectures/${lectureId}`, { params: { page: pageIndex } }) // page로 수정
}

// 본인의 리뷰 가져오기
const getSelfReview = async (token, lectureId) => {
    return instance.get(`/api/reviews/lectures/${lectureId}/my-review`, {
        headers: { Authorization: `Bearer ${token}` },
    })
}

// 강의 리뷰 등록
const writeLectureReview = async (token, lectureId, content, rating) => {
    return instance.post(
        `/api/reviews`,
        { lectureId, content, rating }, // body에 포함
        {
            headers: { Authorization: `Bearer ${token}` },
        }
    )
}

// 강의 리뷰 수정
const editLectureReview = async (token, reviewId, lectureId, content, rating) => {
    return instance.patch(
        `/api/reviews/${reviewId}`,
        { lectureId, content, rating }, // body에 포함
        {
            headers: { Authorization: `Bearer ${token}` },
        }
    )
}

// 강의 리뷰 신고
const reportLectureReview = async (token, reviewId) => {
    return instance.post(
        `/api/reviews/${reviewId}/report`,
        {}, // body가 필요 없는 경우 빈 객체 `{}` 전달
        { headers: { Authorization: `Bearer ${token}` } } // ✅ headers를 올바른 위치에 배치
    )
}

// 강의 리뷰 삭제
const deleteLectureReview = async (token, reviewId) => {
    return instance.delete(`/api/reviews/${reviewId}`, {
        headers: { Authorization: `Bearer ${token}` },
    })
}

// 강의 등록 요청
const registerLecture = async (sourceUrl, token) => {
    return instance.post(
        `/api/lecture-requests/create`,
        { sourceUrl }, // body가 필요 없는 경우 빈 객체 `{}` 전달
        { headers: { Authorization: `Bearer ${token}` } } // ✅ headers를 올바른 위치에 배치
    )
}

export {
    addBookmark,
    removeBookmark,
    searchLectures,
    getLectureDetail,
    getLectureDetailWithLogout,
    reportLecture,
    getLectureReview,
    getSelfReview,
    writeLectureReview,
    editLectureReview,
    reportLectureReview,
    deleteLectureReview,
    registerLecture,
}
export default instance
