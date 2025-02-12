<template>
    <div class="flex flex-col border border-gray-200 rounded-[20px] w-full items-center p-6 gap-6">
        <div class="flex items-center justify-between w-full">
            <p class="text-h3">할 일 목록</p>
            <button
                v-if="followStatus === null"
                class="flex gap-1 p-1 button-primary"
                @click="$emit('open-add-modal')"
            >
                <Plus class="w-[1.125rem] h-[1.125rem]" />
                <p>할 일 추가하기</p>
            </button>
        </div>
        <div class="flex items-center justify-between w-full">
            <div class="flex items-center gap-2 text-black">
                <NavigateLeft
                    class="w-[1.125rem] h-[1.125rem] cursor-pointer"
                    @click="NavigateDay(-1)"
                />
                <span class="text-h3"
                    >{{ selectedDate.getMonth() + 1 }}월 {{ selectedDate.getDate() }}일</span
                >
                <NavigateRight
                    class="w-[1.125rem] h-[1.125rem] cursor-pointer"
                    @click="NavigateDay(1)"
                />
            </div>
            <button
                v-if="token && userId && followStatus === null"
                class="flex gap-1 p-1 button-line"
                @click="moveUndone(undoneDate, token, userId)"
            >
                <Arrow class="w-[1.125rem] h-[1.125rem]" />
                <p>미완료 할 일 내일로 미루기</p>
            </button>
        </div>
        <div
            v-for="todo in todos"
            :key="todo.id"
            class="flex items-center w-full h-[4.25rem] rounded-lg border border-gray-200 px-1"
        >
            <div>
                <Move class="w-6 h-6 text-gray-300" />
            </div>
            <div class="flex items-center gap-3 w-full h-full py-2.5 pr-4 border-r border-gray-200">
                <div
                    class="flex items-center justify-center w-5 h-5 border border-gray-200 rounded cursor-pointer"
                    :class="todo.finished ? 'bg-primary-500 ' : 'bg-white'"
                    @click="
                        userId && token
                            ? updateTodoStatus(todo, token, userId)
                            : console.error('❌ userId 또는 token이 없습니다.')
                    "
                >
                    <Check v-if="todo.finished" class="w-[1.125rem] h-[1.125rem] text-white" />
                </div>
                <p class="text-body">{{ todo.lectureName }}</p>
                <a :href="todo.sourceUrl" class="text-gray-300 text-caption-sm">강의 상세로 이동</a>
            </div>
            <div class="flex items-center justify-between w-full px-4">
                <p class="text-body">{{ todo.subLectureName }}</p>
                <Delete class="w-6 h-6 cursor-pointer" @click="deleteTodo(todo, token, userId)" />
            </div>
        </div>
    </div>
</template>

<script setup>
import Plus from '@/assets/icons/plus.svg'
import NavigateLeft from '@/assets/icons/navigate_left.svg'
import NavigateRight from '@/assets/icons/navigate_right.svg'
import Arrow from '@/assets/icons/arrow.svg'
import Move from '@/assets/icons/move.svg'
import Delete from '@/assets/icons/delete.svg'
import Check from '@/assets/icons/check.svg'
import { onMounted, ref, computed, watch } from 'vue'
import axios from 'axios'
import { useUserStore } from '@/stores/user'
import { useTodoStore } from '@/stores/todo'

const userStore = useUserStore() // Pinia 스토어 가져오기
const todoStore = useTodoStore()

const props = defineProps({
    userId: {
        type: String,
        required: true,
    },
    token: {
        type: String,
        required: true,
    },
    followStatus: {
        type: [String, null], // myProfile 값은 문자열 또는 null일 수 있음
        default: null,
    },
})

// 기본 날짜를 오늘 날짜로 설정
const selectedDate = ref(new Date()) // Date 객체로 설정
const undoneDate = computed(() => selectedDate.value)

const NavigateDay = (day) => {
    const newDate = new Date(selectedDate.value)
    // console.log('전 selectedDate', selectedDate.value)
    newDate.setDate(newDate.getDate() + day)
    // console.log('newDate', newDate)
    selectedDate.value = newDate

    // console.log('후 selectedDate', selectedDate.value)
}

const todos = computed(() => todoStore.todos)

const getTodos = async (token, userId, date) => {
    try {
        const mock_server_url = 'http://localhost:8080'
        // const profileId = 'l3olvy' // 여기에 실제 사용자 ID를 넣어야 함
        const formattedDate = date.toISOString().split('T')[0] // 'YYYY-MM-DD'
        // console.log('date', formattedDate)
        const API_URL = `${mock_server_url}/api/users/${userId}/todos?date=${formattedDate}`
        // const token = 'asdfasdfasdf' // 여기에 Bearer 토큰을 넣어야 함

        // const response = await axios.get(API_URL)

        const response = await axios.get(API_URL, {
            headers: {
                Authorization: `Bearer ${token}`, // Bearer 토큰을 헤더에 포함
            },
        })

        // console.log('응답 데이터:', response.data)
        todoStore.todos = response.data // todo 리스트 저장
        // console.log('누구의todo니', userId)
    } catch (error) {
        console.error('에러:', error)
    }
}
const todoId = ref(null) // 선택한 Todo의 ID를 저장할 변수
// 투두 상태 변경
const updateTodoStatus = async (todo, token, userId) => {
    // console.log('전달된 todo:', todo) // `todo` 값 확인
    try {
        const mock_server_url = 'http://localhost:8080'
        todoId.value = todo.id // 선택한 todo의 ID 저장
        // console.log('todoId', todoId.value)
        const API_URL = `${mock_server_url}/api/users/${userId}/todos/${todoId.value}/status`
        // 상태 반전
        const updatedFinishedStatus = !todo.finished

        const response = await axios.patch(
            API_URL,
            {
                finished: updatedFinishedStatus, // 상태 변경
                nextId: 0,
            },
            {
                headers: {
                    'Content-Type': 'application/json', //필수 헤더 추가
                    Authorization: `Bearer ${token}`, // 필요 시 Bearer 토큰 추가
                },
            }
        )
        // 상태 업데이트 (프론트엔드에서도 즉시 반영)
        todo.finished = updatedFinishedStatus
        console.log('상태업데이트', updatedFinishedStatus)
    } catch (error) {
        console.error('에러:', error)
    }
}

const deleteTodo = async (todo, token, userId) => {
    // console.log('전달된 todo, token:', todo, token) // `todo` 값 확인
    try {
        const mock_server_url = 'http://localhost:8080'
        todoId.value = todo.id // 선택한 todo의 ID 저장
        // console.log('todoId', todoId.value)
        const API_URL = `${mock_server_url}/api/users/${userId}/todos/${todoId.value}`
        const response = await axios.delete(API_URL, {
            headers: {
                Authorization: `Bearer ${token}`, // Bearer 토큰을 헤더에 포함
            },
        })
        console.log('삭제완료')
        // 삭제된 todo 제외
        todoStore.todos = todoStore.todos.filter((item) => item.id !== todoId.value)
    } catch (error) {
        console.error('에러:', error)
    }
}

const moveUndone = async (selectedDate, token, userId) => {
    try {
        const mock_server_url = 'http://localhost:8080'
        const newDate = new Date(selectedDate)
        const formattedDate = newDate.toISOString().split('T')[0]

        // console.log('date', date)
        // console.log('formattedDate', formattedDate)
        // console.log('selectedDate', selectedDate)
        const API_URL = `${mock_server_url}/api/users/${userId}/todos/move-undone?date=${formattedDate}`
        // console.log(API_URL)
        // console.log('사용 중인 Bearer 토큰:', token)

        const response = await axios.post(
            API_URL,
            {},
            {
                headers: {
                    'Content-Type': 'application/json', // 필수 헤더
                    Authorization: `Bearer ${token}`, // Bearer 토큰 추가
                },
            } // `todos` 키로 배열을 보내기
        )
        console.log('이동완료')
        // 새로운 todo 추가
        // ✅ 현재 날짜의 미완료 할 일 제거
        todoStore.todos = todoStore.todos.filter(
            (todo) => todo.date !== selectedDate.toISOString().split('T')[0] || todo.finished
        )

        // ✅ 다음 날 todos에 새롭게 추가
        response.data.forEach((movedTodo) => {
            todoStore.todos.push({
                ...movedTodo,
                date: formattedDate,
            })
        })
    } catch (error) {
        console.error('에러:', error)
        // console.log('에러 selectedDate', selectedDate.value)
        // console.log('todolist 토큰', token)
        // console.log('todolist 아이디', userId)
    }
}

watch(
    () => [userStore.token, props.userId, selectedDate.value], // ✅ 세 값을 모두 감시
    async ([newToken, newUserId, newDate]) => {
        if (newToken && newUserId && newDate) {
            await getTodos(newToken, newUserId, newDate)
        }
    },
    { immediate: true } // 초기 값도 즉시 확인
)
</script>

<style></style>
