<template>
    <div class="flex flex-col gap-6 p-6">
        <div class="flex justify-end">
            <!--  리뷰 작성 상태 : 리뷰 수정하기 -->
            <button v-if="selfReview" class="flex gap-2 button-gray" @click="emit('edit-review')">
                <Edit class="w-4 h-4 text-gray-400" />
                <p>리뷰 수정하기</p>
            </button>
            <!-- 리뷰 미작성 상태 : 리뷰 작성하기 -->
            <button v-else class="flex gap-2 button-gray" @click="emit('edit-review')">
                <Edit class="w-4 h-4 text-gray-400" />
                <p>리뷰 작성하기</p>
            </button>
        </div>
        <div v-if="reviews.length > 0" class="flex flex-col gap-6">
            <LectureReviewCard v-for="review in reviews" :key="review.id" :review="review" />
        </div>
        <p v-else class="text-black text-body-bold">아직 리뷰가 없습니다.</p>
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'

import { getLectureReview, getSelfReview } from '@/helpers/lecture'
import LectureReviewCard from './LectureReviewCard.vue'

import Edit from '@/assets/icons/edit.svg'

// selfReview를 props로 받기
defineProps({
    selfReview: {
        type: Object,
        default: null, // 기본값을 null로 설정
    },
})

// 부모 컴포넌트에 이벤트 전달
const emit = defineEmits(['edit-review'])

// 라우트에 저장된 lectureId 저장
const route = useRoute()
const currentLectureId = ref(route.params.id)

// 토큰을 불러오기 위한 userStore
const userStore = useUserStore()

// API에서 받아올 변수
const totalElements = ref(null) // 리뷰 수
const totalPages = ref(null) // 페이지 수
const reviews = ref([]) // 리뷰를 담은 배열

// 페이지네이션
const pageIndex = ref(1) // 나중에 페이지네이션과 연결 해야함함

onMounted(async () => {
    try {
        // 전체 리뷰 불러오기
        const response = await getLectureReview(currentLectureId.value, pageIndex.value)
        totalElements.value = response.data.totalElements
        totalPages.value = response.data.totalPages
        reviews.value = response.data.content
    } catch (error) {
        console.error('API 호출 중 오류 발생: ', error)
    }
})
</script>

<style scoped></style>
