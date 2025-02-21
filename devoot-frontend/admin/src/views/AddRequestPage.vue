<template>
    <div class="flex flex-col gap-4">
        <div class="content-center h-20 text-h3">강의 등록 요청 목록</div>

        <div class="flex flex-col gap-4 p-6 bg-white border-gray-200 rounded-2xl">
            <div class="text-h3">강의 요청 목록</div>

            <!-- 🚨 에러 메시지 표시 -->
            <div
                v-if="errorMessage"
                class="p-3 text-red-600 bg-red-100 border border-red-400 rounded"
            >
                {{ errorMessage }}
            </div>

            <div class="flex justify-end">
                <button
                    class="inline-flex items-center justify-center px-3 py-1 text-white rounded whitespace-nowrap bg-primary-500 text-body"
                    @click="goToAddLecturePage"
                >
                    강의 등록
                </button>
            </div>

            <!-- 요청 목록을 표로 표시 -->
            <table v-if="requestedLectures.length > 0" class="w-full border text-body">
                <thead>
                    <tr class="text-left bg-gray-100">
                        <th class="w-1/2 p-3 border-b border-gray-200">강의 링크</th>
                        <th class="w-1/3 p-3 border-b border-gray-200">요청 날짜</th>
                        <th class="w-1/6 p-3 text-center border-b border-gray-200">관리</th>
                    </tr>
                </thead>
                <tbody>
                    <tr
                        v-for="lecture in requestedLectures"
                        :key="lecture.id"
                        class="border-b border-gray-200 cursor-pointer hover:bg-gray-50"
                    >
                        <td class="w-1/2 p-3 max-w-[300px] overflow-hidden truncate">
                            <a
                                :href="lecture.sourceUrl"
                                target="_blank"
                                class="text-primary-500 max-w-[300px] truncate"
                            >
                                {{ lecture.sourceUrl }}
                            </a>
                        </td>
                        <td class="w-1/3 p-3 whitespace-nowrap">{{ lecture.createdAt }}</td>
                        <td class="w-1/6 p-3 text-center">
                            <button
                                class="inline-flex items-center justify-center px-3 py-1 text-white bg-red-500 rounded whitespace-nowrap w-fit text-body"
                                @click="removeRequestedLecture(lecture.id)"
                            >
                                삭제
                            </button>
                        </td>
                    </tr>
                </tbody>
            </table>

            <div v-else class="text-gray-500">등록된 강의 요청이 없습니다.</div>
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
const errorMessage = ref('') // 에러 메시지 상태

// 강의 요청 데이터 불러오기
const loadRequestedLecture = async () => {
    try {
        errorMessage.value = ''
        const response = await getRequestedLecture(userStore.token)
        requestedLectures.value = response.data
    } catch (error) {
        console.error('❌ 요청된 강의 불러오기 에러:', error)
        errorMessage.value = '강의 요청 데이터를 불러오는 중 오류가 발생했습니다.'
    }
}

// 요청된 강의 삭제하기
const removeRequestedLecture = async (requestId) => {
    try {
        const isConfirmed = window.confirm('요청된 강의를 삭제하시겠습니까?')

        if (!isConfirmed) return

        await deleteRequestedLecture(requestId, userStore.token)

        // 삭제된 강의 요청을 목록에서 제거
        requestedLectures.value = requestedLectures.value.filter(
            (lecture) => lecture.id !== requestId
        )
        alert('삭제 완료')
    } catch (error) {
        console.error('❌ 요청된 강의 삭제 에러:', error)
        errorMessage.value = '강의 요청 삭제 중 오류가 발생했습니다.'
    }
}

watch(
    () => userStore.token,
    async (newToken) => {
        if (newToken) await loadRequestedLecture()
    },
    { immediate: true }
)

// 강의 추가 페이지로 이동
const goToAddLecturePage = () => {
    router.push('/add/lecture')
}
</script>

<style scoped></style>
