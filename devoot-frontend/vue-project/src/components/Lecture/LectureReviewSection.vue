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
        </div>
        <p v-else class="text-black text-body-bold">아직 리뷰가 없습니다.</p>
    </div>
</template>

<script setup>
import LectureReviewCard from './LectureReviewCard.vue'
import Edit from '@/assets/icons/edit.svg'

// selfReview를 props로 받기
defineProps({
    reviews: {
        type: Array,
        required: true,
    },
    selfReview: {
        type: Object,
        default: null, // 기본값을 null로 설정
    },
})

// 부모 컴포넌트에 이벤트 전달
const emit = defineEmits(['edit-review', 'update-reviews'])

const handleEditReview = (review) => {
    emit('edit-review', review)
}

const refreshReviews = () => {
    emit('update-reviews') // ✅ 부모(LectureDetailPage)에게 리뷰 갱신 요청
}
</script>

<style scoped></style>
