<template>
    <div class="flex flex-col w-full p-6 gap-6 max-h-[400px] overflow-y-auto">
        <ProfileReviewCard
            v-for="(review, index) in reviews.content"
            :key="index"
            :review="review"
            @edit-review="handleEditReview"
            @delete-review="deleteReview"
        />
    </div>
</template>

<script setup>
import ProfileReviewCard from './ProfileReviewCard.vue'

defineProps({
    reviews: {
        type: Array,
        required: true,
    },
    userId: String,
    token: String,
})

// 부모 컴포넌트에 이벤트 전달
const emit = defineEmits(['edit-review', 'update-reviews', 'delete-review'])

// 리뷰 수정 이벤트 처리
const handleEditReview = (review) => {
    emit('edit-review', review)
}

const deleteReview = (review) => {
    emit('delete-review', review) // ProfileReviewSection으로 이벤트 전달
}
</script>

<style></style>
