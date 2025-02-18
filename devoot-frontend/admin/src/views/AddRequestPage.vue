<template>
    <div class="flex flex-col gap-5 p-10">
        <p class="text-5xl font-bold">강의 등록 요청 목록</p>
        <div class="flex flex-col justify-center gap-3 bg-white">
            <div class="flex items-center justify-between bg-green-200">
                <div class="flex flex-col items-center justify-center">
                    <p class="text-2xl font-bold">요청 목록</p>
                </div>
                <div class="flex gap-2">
                    <button
                        class="px-3 py-1 bg-blue-500 rounded cursor-pointer"
                        @click="goToAddLecturePage"
                    >
                        등록
                    </button>
                </div>
            </div>
            <div class="w-full border-t border-gray-300"></div>
            <div class="flex items-center justify-between">
                <p class="text-gray-300">강의 링크</p>
                <p class="text-gray-300">요청 날짜</p>
            </div>
            <!-- 서버에서 받아온 데이터 -->
            <div
                class="flex flex-col gap-3"
                v-for="requestedLecture in requestedLectures"
                :key="requestedLecture.id"
            >
                <div class="w-full border-t border-gray-200"></div>
                <div class="flex justify-between">
                    <div class="w-7/12 overflow-hidden">
                        <a :href="requestedLecture.sourceUrl"> {{ requestedLecture.sourceUrl }}</a>
                    </div>
                    <div class="justify">
                        <p>{{ requestedLecture.createdAt }}</p>
                    </div>
                    <button
                        class="px-3 py-1 bg-red-500 rounded cursor-pointer w-fit h-fit"
                        @click="removeRequestedLecture(requestedLecture.id)"
                    >
                        삭제
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { getRequestedLecture, deleteRequestedLecture } from '@/helpers/lecture'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore() // Pinia 스토어 가져오기
const router = useRouter() // Vue Router 사용

const requestedLectures = ref([]) // 요청된 강의 정보

// 강의 요청 데이터 불러오기
const loadRequestedLecture = async () => {
    try {
        console.log(userStore.token)
        const response = await getRequestedLecture(userStore.token)
        requestedLectures.value = response.data // todo 리스트 저장
    } catch (error) {
        console.error('❌ 요청된 강의 불러오기 에러:', error)
    }
}

// 요청된 강의 삭제하기
const removeRequestedLecture = async (requestId) => {
    try {
        const isConfirmed = window.confirm('요청된 강의를 삭제하시겠습니까?')

        if (!isConfirmed) {
            return // 사용자가 취소하면 아무 동작도 하지 않음
        }

        await deleteRequestedLecture(requestId, userStore.token)
        // ✅ 요청된 ID를 제외한 새로운 배열로 업데이트
        requestedLectures.value = requestedLectures.value.filter(
            (lecture) => lecture.id !== requestId
        )
        alert('삭제 완료')
    } catch (error) {
        console.error('❌ 요청된 강의 삭제 에러:', error)
    }
}

watch(
    () => userStore.token, // ✅ 세 값을 모두 감시
    async (newToken) => {
        if (newToken) {
            await loadRequestedLecture()
        }
    },
    { immediate: true } // 초기 값도 즉시 확인
)

// 강의 추가 페이지로 이동
const goToAddLecturePage = () => {
    router.push('/add/lecture') // 🔥 해당 경로로 이동
}
</script>

<style></style>
