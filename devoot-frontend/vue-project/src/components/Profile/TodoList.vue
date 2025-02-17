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
                    @click="todoStore.navigateDay(-1)"
                />
                <span class="text-h3"
                    >{{ todoStore.selectedDate.getMonth() + 1 }}월
                    {{ todoStore.selectedDate.getDate() }}일</span
                >
                <NavigateRight
                    class="w-[1.125rem] h-[1.125rem] cursor-pointer"
                    @click="todoStore.navigateDay(1)"
                />
            </div>
            <button
                v-if="followStatus === null"
                class="flex gap-1 p-1 button-line"
                @click="rescheduleTodo(selectedDate, token, userId)"
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
                    v-if="isMyProfile"
                    class="flex items-center justify-center w-5 h-5 border border-gray-200 rounded cursor-pointer"
                    :class="todo.finished ? 'bg-primary-500 ' : 'bg-white'"
                    @click="changeTodoStatus(todo)"
                >
                    <Check v-if="todo.finished" class="w-[1.125rem] h-[1.125rem] text-white" />
                </div>
                <p class="text-body">{{ todo.lectureName }}</p>
                <a :href="todo.sourceUrl" class="text-gray-300 text-caption-sm">강의 상세로 이동</a>
            </div>
            <div class="flex items-center justify-between w-full px-4">
                <p class="text-body">{{ todo.subLectureName }}</p>
                <Delete
                    v-if="isMyProfile"
                    class="w-6 h-6 cursor-pointer"
                    @click="removeTodo(todo)"
                />
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
import { ref, computed, watch } from 'vue'
import { useUserStore } from '@/stores/user'
import { useTodoStore } from '@/stores/todo'
import { useRoute } from 'vue-router'
import {
    getTodos,
    updateTodoStatus,
    deleteTodo,
    moveUndoneTodos,
    getContributions,
} from '@/helpers/todo'
const userStore = useUserStore() // Pinia 스토어 가져오기
const todoStore = useTodoStore()
const route = useRoute()

const props = defineProps({
    userId: {
        type: String,
        required: true,
    },
    followStatus: {
        type: [String, null], // myProfile 값은 문자열 또는 null일 수 있음
        default: null,
    },
})

const isMyProfile = computed(() => userStore.userId === route.params.id)

const todos = computed(() => todoStore.todos) // todo 목록
// const selectedDate = ref(new Date()) // 기본 날짜를 오늘 날짜로 설정, Date 객체로 설정

// todo 불러오기
const loadTodos = async () => {
    const formattedDate = todoStore.selectedDate.toISOString().split('T')[0] // 'YYYY-MM-DD'
    try {
        const response = await getTodos(userStore.token, route.params.id, formattedDate)
        todoStore.todos = response.data // todo 리스트 저장
    } catch (error) {
        console.error('❌ 투두 불러오기 에러:', error)
    }
}

// 투두 상태 변경
const changeTodoStatus = async (todo) => {
    try {
        // 상태 반전
        const updatedFinishedStatus = !todo.finished
        const selectedYear = todo.date.split('-')[0]
        todo.finished = updatedFinishedStatus
        await updateTodoStatus(todo.id, userStore.token, route.params.id, updatedFinishedStatus)
        // 상태 업데이트 (프론트엔드에서도 즉시 반영)
        // console.log('상태업데이트', todo)
        if (updatedFinishedStatus) {
            alert('발자국을 남겼습니다!')
        }
        const response = await getContributions(selectedYear, userStore.token, route.params.id)
        todoStore.updateContributions(response.data)
        // console.log('투두 상태 변경시 넘겨줄 데이터', response.data)
    } catch (error) {
        console.error('❌ 투두 상태 변경 에러:', error)
    }
}

// 투두 삭제
const removeTodo = async (todo) => {
    try {
        await deleteTodo(todo.id, userStore.token, route.params.id)
        // console.log('✅ todo 삭제완료')
        todoStore.todos = todoStore.todos.filter((item) => item.id !== todo.id)
    } catch (error) {
        console.error('❌ 투두 삭제 에러:', error)
    }
}

// 미완료 할 일 내일로 미루기
const rescheduleTodo = async () => {
    try {
        // const newDate = new Date(selectedDate.value)
        const formattedDate = todoStore.selectedDate.toISOString().split('T')[0]
        const response = await moveUndoneTodos(userStore.token, route.params.id, formattedDate)

        alert('할 일이 성공적으로 내일로 이동되었습니다!')
        console.log('미완료 투두 이동완료')
        // 새로운 todo 추가
        // ✅ 현재 날짜의 미완료 할 일 제거
        todoStore.todos = todoStore.todos.filter(
            (todo) =>
                todo.date !== todoStore.selectedDate.toISOString().split('T')[0] || todo.finished
        )

        // ✅ 다음 날 todos에 새롭게 추가

        // ✅ response.data의 타입에 따라 다른 처리
        if (Array.isArray(response.data)) {
            response.data.forEach((movedTodo) => {
                todoStore.todos.push({
                    ...movedTodo,
                    date: formattedDate,
                })
            })
            console.log('미완료 투두가 여러 개 이동되었습니다.')
        } else if (response.data && typeof response.data === 'object') {
            todoStore.todos.push({
                ...response.data,
                date: formattedDate,
            })
            console.log('미완료 투두 하나가 이동되었습니다.')
        }
    } catch (error) {
        console.error('❌ 미완료 투두 내일로 미루기 에러:', error)
    }
}

watch(
    () => [userStore.token, props.userId, todoStore.selectedDate], // ✅ 세 값을 모두 감시
    async ([newToken, newUserId, newDate]) => {
        if (newToken && newUserId && newDate) {
            await loadTodos()
        }
    },
    { immediate: true } // 초기 값도 즉시 확인
)
</script>

<style></style>
