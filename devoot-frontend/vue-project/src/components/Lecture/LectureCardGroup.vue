<!-- src/components/Lecture/LectureCardGroup.vue -->
<template>
    <!-- pb-4 (또는 필요에 따라 pb-6 등) 클래스로 아래쪽 여백을 추가 -->
    <div class="pb-4 overflow-x-auto custom-scrollbar">
        <div class="grid grid-cols-2 min-w-max md:grid-cols-4 gap-x-6 gap-y-4">
            <!-- 최대 8개 카드만 표시 -->
            <LectureCard
                v-for="(lecture, index) in lectures.slice(0, 8)"
                :key="lecture.id"
                :id="lecture.id"
                :name="lecture.name"
                :lecturer="lecture.lecturer"
                :platform="lecture.platform"
                :imageUrl="lecture.imageUrl"
                :tags="lecture.tags"
                :currentPrice="lecture.currentPrice"
                :originalPrice="lecture.originalPrice"
                :rating="lecture.rating"
                :reviewCount="lecture.reviewCount"
                :isBookmarked="lecture.isBookmarked"
            />
        </div>
    </div>
</template>

<script>
import LectureCard from './LectureCard.vue'

export default {
    name: 'LectureCardGroup',
    components: {
        LectureCard,
    },
    props: {
        lectures: {
            type: Array,
            required: true,
        },
    },
}
</script>

<style scoped></style>
