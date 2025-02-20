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
// contribution 관련 API
//===============================================

// 잔디 conrtribution 개수 불러오기
const getContributions = async (selectedYear, token, userId) => {
    return instance.get(`/api/users/${userId}/todos/contributions`, {
        params: { year: selectedYear },
        headers: { Authorization: `Bearer ${token}` },
    })
}

const getLevel = (contributions) => {
    if (contributions === 0) return 0
    if (contributions <= 2) return 1
    if (contributions <= 4) return 2
    if (contributions <= 6) return 3
    if (contributions <= 8) return 4
    return 5
}

//===============================================
// 칸반 관련 API
//===============================================

// 칸반 섹션에 각 status에 해당하는 강의 불러오기
const getLectureDatas = async (token, userId) => {
    return instance.get(`/api/users/${userId}/bookmarks`, {
        headers: { Authorization: `Bearer ${token}` },
    })
}

// 드래그앤 드랍으로 함수 status 업데이트
const updateKanbanStatus = async (bookmarkId, token, userId, updatedStatus, afterBookmarkId) => {
    return instance.patch(
        `/api/users/${userId}/bookmarks/${bookmarkId}`,
        {
            status: updatedStatus, // 상태 변경
            nextId: afterBookmarkId,
        },
        { headers: { Authorization: `Bearer ${token}` } }
    )
}

//===============================================
// todo 관련 API
//===============================================

// 투두 불러오기
const getTodos = async (token, userId, date) => {
    return instance.get(`/api/users/${userId}/todos`, {
        params: { date: date },
        headers: { Authorization: `Bearer ${token}` },
    })
}

// 투두 상태 변경(완료/미완료)
const updateTodoStatus = async (todoId, token, userId, finishedStatus, nextId) => {
    console.log('todoId', todoId)
    console.log('token', token)
    console.log('userId', userId)
    console.log('finishedStatus', finishedStatus)
    console.log('nextId', nextId)

    return instance.patch(
        `/api/users/${userId}/todos/${todoId}/status`,
        {
            finished: finishedStatus, // 상태 변경
            nextId: nextId,
        },
        { headers: { Authorization: `Bearer ${token}` } }
    )
}

// 투두 삭제
const deleteTodo = async (todoId, token, userId) => {
    return instance.delete(`/api/users/${userId}/todos/${todoId}`, {
        headers: { Authorization: `Bearer ${token}` },
    })
}

// 미완료 투두 내일로 미루기
const moveUndoneTodos = async (token, userId, date) => {
    return instance.post(
        `/api/users/${userId}/todos/move-undone`,
        {}, // body가 필요 없는 경우 빈 객체 `{}` 전달
        {
            params: { date: date },
            headers: { Authorization: `Bearer ${token}` },
        }
    )
}

export {
    getContributions,
    getLectureDatas,
    updateKanbanStatus,
    getLevel,
    getTodos,
    updateTodoStatus,
    deleteTodo,
    moveUndoneTodos,
}
export default instance
