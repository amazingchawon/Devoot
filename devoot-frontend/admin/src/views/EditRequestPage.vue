<template>
    <div class="flex flex-col gap-4">
        <div class="content-center h-20 text-h3">강의 수정 요청</div>
        <div class="grid grid-cols-12 gap-6">
            <LectureCard v-for="lecture in lectures" :key="lecture.id" :lecture="lecture.lecture" />
        </div>
    </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import LectureCard from '@/components/LectureCard.vue'
import { getEditRequest } from '@/helpers/api'

const userStore = useUserStore()
const lectures = ref([])

// 강의 수정 요청 가져오기
const fetchEditRequests = async () => {
    try {
        if (!userStore.token) return // token이 없으면 실행 안 함
        const response = await getEditRequest(userStore.token)
        lectures.value = response.data
    } catch (error) {
        console.error('강의 수정 요청 목록을 불러오는 데 실패했습니다:', error)
    }
}

// token이 설정될 때 API 요청 실행
watch(
    () => userStore.token,
    (newToken) => {
        if (newToken) {
            fetchEditRequests()
        }
    }
)

// 페이지가 마운트될 때 실행 (만약 token이 이미 존재하면 바로 실행)
onMounted(() => {
    if (userStore.token) {
        fetchEditRequests()
    }
})
</script>

<style scoped></style>
