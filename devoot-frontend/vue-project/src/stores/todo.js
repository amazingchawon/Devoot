import { defineStore } from 'pinia'
import { ref, watch } from 'vue'
import axios from 'axios'
import { useUserStore } from './user'

const userStore = useUserStore() // Pinia 스토어 가져오기
const baseURL = 'http://localhost:8080'

export const useTodoStore = defineStore('todo', () => {
    const todos = ref([]) // 할 일 목록
    const inprogressLectures = ref([])
    // const selectedLecture = ref(null) // 모달에서 선택한 강의

    // // ✅ [1] 서버에서 할 일 목록 불러오기 (GET 요청)
    // const getTodos = async () => {
    //     try {
    //         const mock_server_url = 'https://ed241dc6-2459-4f07-a53e-bbb686a6af68.mock.pstmn.io'
    //         const profileId = 'l3olvy' // 여기에 실제 사용자 ID를 넣어야 함
    //         // const profileId = userStore.userId // 여기에 실제 사용자 ID를 넣어야 함
    //         const date=
    //         const API_URL = `${mock_server_url}/api/users/${profileId}}/todos?date=${date}`
    //         // const token = 'asdfasdfasdf' // 여기에 Bearer 토큰을 넣어야 함
    //         const response = await axios.get('https://your-api.com/todos')
    //         todos.value = response.data // 할 일 목록 저장
    //     } catch (error) {
    //         console.error('❌ 할 일 불러오기 실패:', error)
    //     }
    // }

    // 진행중인 강의 목록 요청(모달에서 사용)
    const getInprogressLecture = async (token, userId) => {
        try {
            const mock_server_url = baseURL
            // const profileId = 'l3olvy' // 여기에 실제 사용자 ID를 넣어야 함
            // const profileId = userStore.userId // 여기에 실제 사용자 ID를 넣어야 함
            const API_URL = `${mock_server_url}/api/users/${userId}/bookmarks`
            // const token = 'asdfasdfasdf' // 여기에 Bearer 토큰을 넣어야 함
            const response = await axios.get(API_URL, {
                headers: {
                    'Content-Type': 'application/json', //필수 헤더 추가
                    Authorization: `Bearer ${token}`, // Bearer 토큰 추가
                },
            })
            inprogressLectures.value = response.data['in-progress']
            console.log('성공:', inprogressLectures.value)
        } catch (error) {
            console.error('진행중인 강의 불러오기 에러:', error)
        }
    }

    const addTodo = async (todoData, token, userId) => {
        try {
            const mock_server_url = baseURL
            // const profileId = 'l3olvy' // 여기에 실제 사용자 ID를 넣어야 함
            // const profileId = userStore.userId // 여기에 실제 사용자 ID를 넣어야 함
            const API_URL = `${mock_server_url}/api/users/${userId}/todos`
            // const token = 'asdfasdfasdf' // 여기에 Bearer 토큰을 넣어야 함
            const response = await axios.post(API_URL, todoData, {
                headers: {
                    'Content-Type': 'application/json', //필수 헤더 추가
                    Authorization: `Bearer ${token}`, // 필요 시 Bearer 토큰 추가
                },
            })
            todos.value.push(response.data) // 성공하면 todoList 업데이트
            return response.data
        } catch (error) {
            console.error('🚨 Todo 추가 실패:', error)
        }
    }
    // watch(
    //     () => [userStore.token, userStore.userId], // ✅ 두 값을 동시에 감시
    //     async ([newToken, newUserId]) => {
    //         if (newToken && newUserId) {
    //             // 두 값이 모두 존재할 때만 실행
    //             // console.log('✅ 토큰과 userId가 준비되었습니다.')
    //             await getInprogressLecture(newToken, newUserId)
    //         }
    //     },
    //     { immediate: true } // 이미 값이 존재할 경우 즉시 실행
    // )

    return { todos, getInprogressLecture, inprogressLectures, addTodo }
})
