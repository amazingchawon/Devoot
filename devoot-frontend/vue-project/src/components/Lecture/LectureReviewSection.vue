<template>
    <div class="flex flex-col gap-6 p-6">
        <div class="flex justify-end">
            <!-- 리뷰 작성/수정 버튼 -->
            <button class="flex gap-2 button-gray" @click="handleEditReview(selfReview)">
                <Edit class="w-4 h-4 text-gray-400" />
                <p>{{ selfReview ? '리뷰 수정하기' : '리뷰 작성하기' }}</p>
            </button>
        </div>

        <div v-if="reviews.length > 0" class="flex flex-col gap-6">
            <LectureReviewCard
                v-for="review in reviews"
                :key="review.id"
                :review="review"
                @edit-review="handleEditReview"
                @delete-review="refreshReviews"
            />
            <PaginationControl
                :current-page="currentPage"
                :total-pages="totalPages"
                @page-changed="updatePage"
            />
        </div>
        <p v-else class="text-black text-body-bold">아직 리뷰가 없습니다.</p>
    </div>
</template>

<script setup>
import LectureReviewCard from './LectureReviewCard.vue'
import PaginationControl from '../Common/PaginationControl.vue'
import Edit from '@/assets/icons/edit.svg'

defineProps({
    reviews: {
        type: Array,
        required: true,
    },
    totalPages: {
        type: Number,
        required: true,
    },
    currentPage: {
        type: Number,
        required: true,
    },
    selfReview: {
        type: Object,
        default: null,
    },
})

// 부모 컴포넌트에 이벤트 전달
const emit = defineEmits(['edit-review', 'update-reviews', 'update-page'])

// 페이지 변경 이벤트 처리
const updatePage = (page) => {
    emit('update-page', page)
}

// 리뷰 수정 이벤트 처리
const handleEditReview = (review) => {
    emit('edit-review', review)
}

const refreshReviews = () => {
    emit('update-reviews') // 부모(LectureDetailPage)에게 리뷰 갱신 요청
}
</script>

<style scoped></style>
