<template>
    <div class="flex flex-col gap-4">
        <div class="content-center h-20 text-h3">리뷰 관리</div>
        <div
            id="reported-user-list"
            class="flex flex-col gap-4 p-6 bg-white border-gray-200 rounded-2xl"
        >
            <div class="flex flex-row justify-between">
                <div class="text-h3">{{ profileId }}님의 리뷰 목록</div>
                <div class="flex flex-row gap-1">
                    <button
                        class="inline-flex items-center justify-center px-3 py-1 text-white bg-red-500 rounded text-body"
                        @click="handleDeleteReviews"
                    >
                        리뷰 전체 삭제
                    </button>
                    <button
                        class="inline-flex items-center justify-center px-3 py-1 text-white rounded bg-primary-500 text-body"
                        @click="handleDeleteReports"
                    >
                        리뷰 신고 취소
                    </button>
                </div>
            </div>
            <div class="flex flex-col gap-1">
                <LectureReviewCard v-for="review in reviews" :key="review.id" :review="review" />
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getReviewOfUser, deleteReviewOfUser, deleteReportOfUser } from '@/helpers/api'
import LectureReviewCard from '@/components/LectureReviewCard.vue'

const errorMessage = ref(null)
const userStore = useUserStore()
const route = useRoute()
const router = useRouter()
const profileId = route.query.profileId
const reviews = ref([])

// 신고된 유저의 리뷰 가져오기
const fetchReportedReviewOfUser = async () => {
    try {
        if (!userStore.token) return
        const response = await getReviewOfUser(userStore.token, profileId)
        reviews.value = response.data.content
    } catch (error) {
        handleApiError(error)
    }
}

// 리뷰 전체 삭제 (deleteReviewOfUser 실행)
const handleDeleteReviews = async () => {
    if (!confirm('정말로 이 유저의 모든 리뷰를 삭제하시겠습니까?')) return

    try {
        await deleteReviewOfUser(userStore.token, profileId)
        reviews.value = [] // 삭제 후 목록 초기화
        alert('해당 유저의 모든 리뷰가 삭제되었습니다.')
        router.back()
    } catch (error) {
        handleApiError(error)
    }
}

// 리뷰 신고 취소 (deleteReportOfUser 실행)
const handleDeleteReports = async () => {
    if (!confirm('이 유저의 모든 리뷰 신고 기록을 삭제하시겠습니까?')) return

    try {
        await deleteReportOfUser(userStore.token, profileId)
        alert('해당 유저의 리뷰 신고 기록이 삭제되었습니다.')
        router.back()
    } catch (error) {
        handleApiError(error)
    }
}

// API 에러 핸들링 함수
const handleApiError = (error) => {
    if (error.response?.status === 403) {
        errorMessage.value = '🚨 관리자 계정이 아닙니다. 관리자 페이지에 접근이 불가능합니다.'
    } else {
        errorMessage.value = '🚨 데이터를 처리하는 중 오류가 발생했습니다.'
    }
    console.error('🚨 API 요청 실패:', error)
}

// token이 설정될 때 API 요청 실행
watch(
    () => userStore.token,
    (newToken) => {
        if (newToken) {
            fetchReportedReviewOfUser()
        }
    }
)

// 페이지가 마운트될 때 실행 (만약 token이 이미 존재하면 바로 실행)
onMounted(() => {
    if (userStore.token) {
        fetchReportedReviewOfUser()
    }
})
</script>

<style scoped></style>
