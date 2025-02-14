<template>
    <div class="flex justify-between col-span-12 gap-6 p-6 h-fit">
        <div class="flex flex-col flex-1 h-full gap-[12px]">
            <div class="inline-flex w-fit text-caption tag-gray">
                <p>수강 전</p>
            </div>

            <div class="overflow-hidden grow">
                <div
                    class="flex-col justify-start flex-wrap space-y-2.5 w-full h-[374px] bg-gray-100 p-3 rounded-2xl overflow-y-auto overflow-x-hidden container"
                    droppable="true"
                    data-status="todo"
                >
                    <KanbanCard
                        draggable="true"
                        :lecture="lecture"
                        v-for="lecture in lectureDatas.todo"
                        :key="lecture.id"
                        :data-id="lecture.id"
                        class="draggable cursor-grab"
                    />
                </div>
            </div>
        </div>
        <div class="flex flex-col flex-1 h-full gap-[12px]">
            <div class="inline-flex w-fit text-caption tag-gray">
                <p style="color: #fde03a !important">수강중</p>
            </div>

            <div class="overflow-hidden grow">
                <div
                    class="flex-col justify-start flex-wrap space-y-2.5 w-full h-[374px] bg-gray-100 p-3 rounded-2xl overflow-y-auto overflow-x-hidden container"
                    droppable="true"
                    data-status="in-progress"
                >
                    <KanbanCard
                        v-for="lecture in lectureDatas['in-progress']"
                        :key="lecture.id"
                        draggable="true"
                        :lecture="lecture"
                        :data-id="lecture.id"
                        class="draggable cursor-grab"
                    />
                </div>
            </div>
        </div>
        <div class="flex flex-col flex-1 h-full gap-[12px]">
            <div class="inline-flex w-fit text-caption tag-gray">
                <p style="color: #0edb8c !important">수강 완료</p>
            </div>

            <div class="overflow-hidden grow">
                <div
                    class="flex-col justify-start flex-wrap space-y-2.5 w-full h-[374px] bg-gray-100 p-3 rounded-2xl overflow-y-auto overflow-x-hidden container"
                    droppable="true"
                    data-status="done"
                >
                    <KanbanCard
                        v-for="lecture in lectureDatas.done"
                        :key="lecture.id"
                        draggable="true"
                        :lecture="lecture"
                        :data-id="lecture.id"
                        class="draggable cursor-grab"
                    />
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import KanbanCard from './KanbanCard.vue'
import { ref, onMounted, onUpdated, watch, computed } from 'vue'
import axios from 'axios'
import { useUserStore } from '@/stores/user'
// import { useTodoStore } from '@/stores/todo'

// const todoStore = useTodoStore()

defineProps({
    userId: {
        type: String,
        default: '',
    },
    token: {
        type: String,
        default: '',
    },
})

const userStore = useUserStore() // Pinia 스토어 가져오기
const lectureDatas = ref([])

const loadLectureDatas = async (token, userId) => {
    console.log('전달받은 토큰', token)
    // console.log('전달받은 아이디', userId)

    try {
        const mock_server_url = 'http://localhost:8080'
        const API_URL = `${mock_server_url}/api/users/${userId}/bookmarks`
        // const token = 'asdfasdfasdf' // 여기에 Bearer 토큰을 넣어야 함

        const response = await axios.get(API_URL, {
            headers: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${token}`,
            },
        })
        // console.log('전달된 헤더:', response.config.headers)

        lectureDatas.value = response.data
    } catch (error) {
        console.error('에러:', error)
    }
}

const updateStatus = async (el, bookmarkId, token, userId, afterBookmarkId) => {
    try {
        const mock_server_url = 'http://localhost:8080'
        const API_URL = `${mock_server_url}/api/users/${userId}/bookmarks/${bookmarkId}`

        const parentContainer = el.closest('.container') // 현재 이동된 컨테이너 찾기

        // ✅ 드롭된 컨테이너에 따라 상태(status) 값 변경
        let updatedStatus = 1 // 기본값 (todo)
        if (parentContainer) {
            if (parentContainer.dataset.status === 'in-progress') {
                updatedStatus = 2
            } else if (parentContainer.dataset.status === 'done') {
                updatedStatus = 3
            }
        }

        const response = await axios.patch(
            API_URL,
            {
                status: updatedStatus, // 상태 변경
                nextId: afterBookmarkId,
            },
            {
                headers: {
                    'Content-Type': 'application/json', //필수 헤더 추가
                    Authorization: `Bearer ${token}`, // 필요 시 Bearer 토큰 추가
                },
            }
        )
        console.log('응답', response)

        // if (updatedStatus === 2) {
        //     console.log('✅ Status가 2로 업데이트되었습니다:', bookmarkId)
        //     // console.log('lecture가 뭔데', lectureDatas.value)

        //     // ✅ Pinia Store에 추가
        //     // if (!todoStore.inprogressLectures.some((lecture) => lecture.id === bookmarkId)) {
        //     //     todoStore.inprogressLectures.push() // 값 추가
        //     //     console.log('📚 inprogressLectures에 추가되었습니다.')
        //     // }
        // }
        // // console.log('가까운 부모컨테이너', parentContainer.dataset.status)
        // console.log('업데이트 상태:', updatedStatus)
        // console.log('칸반섹션 데이터', lectureDatas.value)
    } catch (error) {
        console.error('에러:', error)
    }
}
// onMounted(() => {
//     loadLectureDatas() // JSON 데이터 가져오기
// })

watch(
    () => [userStore.token, userStore.userId], // ✅ 두 값을 동시에 감시
    async ([newToken, newUserId]) => {
        if (newToken && newUserId) {
            // 두 값이 모두 존재할 때만 실행
            // console.log('✅ 토큰과 userId가 준비되었습니다.')
            await loadLectureDatas(newToken, newUserId)
        }
    },
    { immediate: true } // 이미 값이 존재할 경우 즉시 실행
)

onUpdated(() => {
    const $ = (select) => document.querySelectorAll(select)
    const draggables = $('.draggable')
    const containers = $('.container')

    // 현재 프로필이 본인의 것인지 확인
    // const isMyProfile = userId === userStore.userId

    // console.log('드래그 가능한 엘리먼트', draggables)
    // console.log('컨테이너', containers)

    // 드래그 가능한 엘리먼트에 이벤트(드래그 시작, 드래그 종료) 추가
    draggables.forEach((el) => {
        // if (isMyProfile)
        el.addEventListener('dragstart', () => {
            el.classList.add('dragging', 'highlight', 'cursor-grabbing')
            // console.log('드래그 시작')
            // console.log('el이다', el)
        })

        el.addEventListener('dragend', () => {
            const container = el.closest('.container') // 현재 요소가 속한 컨테이너 찾기
            const afterElement = getDragAfterElement(container, el.getBoundingClientRect().bottom) // 현재 위치의 바로 아래 요소 찾기
            const bookmarkId = el.dataset.id // ✅ data-id에서 고유 id 가져오기

            let afterBookmarkId = 0 // ✅ 미리 선언
            if (afterElement) {
                afterBookmarkId = afterElement.dataset.id // ✅ 값 할당
                console.log('가장 가까운 아래 요소의 북마크 ID:', afterBookmarkId)
                console.log('북마크의 ID:', bookmarkId) // ✅ dataset 값 확인
            }
            el.classList.remove('dragging', 'highlight')
            console.log('가장 가까운 아래 요소의 북마크 ID:', afterBookmarkId)
            console.log('북마크의 ID:', bookmarkId) // ✅ dataset 값 확인

            if (userStore.token && userStore.userId) {
                updateStatus(el, bookmarkId, userStore.token, userStore.userId, afterBookmarkId) // ✅ updateStatus 함수 호출
            }
        })
    })

    // 현재 마우스 위치에서 가장 가까운 요소 찾기
    function getDragAfterElement(container, y) {
        // 1️⃣ `container` 내부의 모든 `.draggable` 요소를 가져오되,
        //    현재 드래그 중인(`.dragging`) 요소는 제외함.
        const draggableElements = [...container.querySelectorAll('.draggable:not(.dragging)')]

        // 2️⃣ `reduce`를 사용하여 `y` 위치에서 가장 가까운 요소 찾기
        return draggableElements.reduce(
            (closest, child) => {
                // closest() 메서드는 주어진 CSS 선택자와 일치하는 요소를 찾을 때까지, 자기 자신을 포함해 위쪽으로 문서 트리를 순회
                // 3️⃣ `child`(각 draggable 요소)의 위치와 크기 정보 가져오기
                const box = child.getBoundingClientRect()

                // 4️⃣ `offset` 계산 (마우스 y 좌표와 요소 중심 간의 거리)
                // `box.top`은 요소의 상단 위치
                // `box.height / 2`는 요소의 절반 높이 (즉, 중앙 지점)
                const offset = y - box.top - box.height / 2

                // 5️⃣ 마우스가 요소의 중앙 위쪽에 위치(`offset < 0`)하면서,
                //    현재까지 찾은 `closest.offset`보다 더 가까운 경우 `closest` 업데이트
                if (offset < 0 && offset > closest.offset) {
                    return { offset: offset, element: child }
                } else {
                    return closest
                }
            },
            // 6️⃣ `reduce`의 초기값 설정 (가장 작은 숫자, 즉 `Number.NEGATIVE_INFINITY`)
            //    이렇게 하면 첫 번째 비교에서 무조건 첫 번째 요소가 선택될 수 있도록 함.
            { offset: Number.NEGATIVE_INFINITY }
        ).element // 7️⃣ `.element`를 반환하여 가장 가까운 요소를 리턴
    }

    // 드래그중일 때
    containers.forEach((container) => {
        // if (isMyProfile)
        container.addEventListener('dragover', (e) => {
            e.preventDefault()
            const afterElement = getDragAfterElement(container, e.clientY) //clientY: 마우스 이벤트가 발생한 위치의 Y(수직) 좌표
            const draggable = document.querySelector('.dragging')
            if (afterElement) {
                container.insertBefore(draggable, afterElement)
            } else {
                container.appendChild(draggable) // 만약 `afterElement`가 없으면 마지막으로 보냄
            }
        })
    })
})

onMounted(async (token, userId) => {
    if (token && userId) {
        console.log('✅ token과 userId가 있습니다.')
        await loadLectureDatas(token, userId)
    } else {
        console.error('❌ token 또는 userId가 아직 정의되지 않았습니다.')
    }
})
</script>

<style>
::-webkit-scrollbar {
    width: 6px; /* 세로축 스크롤바 폭 너비 */
}

::-webkit-scrollbar-thumb {
    background: #e8ebee; /* 스크롤바 막대 색상 */
    border: 1px solid #f4f6f8; /* 스크롤바 막대 테두리 설정  */
    border-radius: 12px 12px 12px 12px;
}

.dragging {
    opacity: 0.5; /* 반투명 효과 */
    transform: scale(1.05); /* 살짝 확대 */
    transition: transform 0.2s ease-in-out;
}
.highlight {
    /* border: 2px dashed #ff9800; */
    background-color: #cde3ff;
}
</style>
