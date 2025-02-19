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

        <!-- 강의카드 -->
        <div
            v-for="(todo, index) in todos"
            :key="todo.id"
            class="flex items-center draggable w-full h-[4.25rem] rounded-lg border border-gray-200 px-1 cursor-default"
            draggable="true"
            @dragstart="(event) => dragStart(event, index)"
            @dragover.prevent
            @dragend="dragEnd"
            @drop="drop(index)"
        >
            <div>
                <Move class="w-6 h-6 text-gray-300 cursor-grab" />
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
                <a :href="todo.sourceUrl" class="text-gray-300 text-caption-sm whitespace-nowrap"
                    >강의 상세로 이동</a
                >
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

        // ✅ 완료(checked)된 경우 nextId = -1, 미완료 상태면 0
        let nextId = -1
        await updateTodoStatus(
            todo.id,
            userStore.token,
            route.params.id,
            updatedFinishedStatus,
            nextId
        )
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

//===============================================
// 드래그앤 드랍
//===============================================
const draggedItemIndex = ref(null) // ✅ 현재 드래그 중인 요소의 인덱스 저장

// 드래그 시작 시 실행되는 함수
const dragStart = (event, index) => {
    if (index === undefined) {
        console.warn('⚠ dragStart()에서 index가 undefined입니다.')
        return
    }

    draggedItemIndex.value = index

    // 드래그된 요소에 애니메이션 스타일 적용
    event.target.classList.add('dragging', 'highlight', 'cursor-grabbing')

    // 드래그된 요소가 살짝 떠오르는 효과
    setTimeout(() => {
        event.target.classList.add('drag-over')
    }, 50)
}

// 드롭 시 실행되는 함수 (배열 순서 변경)
const drop = (index) => {
    if (draggedItemIndex.value === null) return

    const draggedItem = todoStore.todos[draggedItemIndex.value]
    if (!draggedItem) {
        console.error('❌ drop()에서 draggedItem이 존재하지 않습니다.')
        return
    }

    // ✅ 배열에서 드래그된 요소를 제거하고 새로운 위치에 삽입
    const newTodos = [...todoStore.todos]
    newTodos.splice(draggedItemIndex.value, 1)
    newTodos.splice(index, 0, draggedItem)

    draggedItemIndex.value = index

    // ✅ 임시로 UI에 반영 (실제 서버 업데이트는 dragEnd에서 처리)
    todoStore.todos = newTodos

    // ✅ index가 유효한 경우에만 업데이트
    // if (index !== undefined) {
    //     draggedItemIndex.value = index
    // } else {
    //     console.warn('⚠ drop()에서 index가 undefined입니다.')
    // }
}

const dragEnd = async (event) => {
    event.target.classList.remove('dragging', 'highlight', 'drag-over')

    if (draggedItemIndex.value === null) return

    const draggedItem = todoStore.todos[draggedItemIndex.value]
    if (!draggedItem) {
        console.error('❌ dragEnd()에서 draggedItem이 존재하지 않습니다.')
        return
    }

    // ✅ nextId를 올바르게 설정
    let nextId = 0
    if (draggedItemIndex.value + 1 < todoStore.todos.length) {
        nextId = todoStore.todos[draggedItemIndex.value + 1]?.id || 0
    }
    console.log('보내기전 nextid', nextId)

    try {
        await updateTodoStatus(
            draggedItem.id,
            userStore.token,
            userStore.userId,
            draggedItem.finished,
            nextId
        )

        console.log('✅ Todo ', draggedItem.id, '위치 변경 완료. 새로운 nextId:', nextId)
        setTimeout(() => {
            alert('위치 옮기기 성공')
        }, 200) // 스타일이 제거될 시간을 충분히 확보 (200ms 정도)
    } catch (error) {
        console.error('❌ Todo 순서 변경 실패:', error)
    }

    // ✅ 드래그 상태 초기화
    draggedItemIndex.value = null
}
</script>

<style>
.draggable {
    transition:
        transform 0.2s ease-in-out,
        opacity 0.2s ease-in-out;
}

/* ✅ 드래그 중인 요소 스타일 */
.dragging {
    opacity: 0.5; /* 반투명 효과 */
    transform: scale(1.05); /* 살짝 확대 */
    transition: transform 0.2s ease-in-out;
}

/* ✅ 드래그 가능한 요소에 마우스를 올릴 때 */
.draggable:hover {
    transform: scale(1.02);
    transition: transform 0.15s ease-in-out;
}

/* ✅ 드래그 중 이동할 위치 표시 */
.drag-over {
    background-color: #cde3ff;
}
</style>
