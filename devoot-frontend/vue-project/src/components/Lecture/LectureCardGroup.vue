<!-- src/components/Lecture/LectureCardGroup.vue -->
<template>
    <!-- pb-4: 하단 여백, overflow-x-auto: 가로 스크롤(필요 시) -->
    <div class="pb-4 overflow-x-auto custom-scrollbar">
        <!-- 그리드: 모바일은 2열, md 이상은 4열, 카드 간 간격 유지 -->
        <div class="grid grid-cols-2 md:grid-cols-4 gap-x-6 gap-y-4">
            <!-- API로 받아온 전체 강의 데이터를 렌더링 (LectureCard.vue에서 카드 크기, 간격 적용) -->
            <LectureCard v-for="lecture in lectures" :key="lecture.id" v-bind="lecture" />
        </div>
    </div>
</template>

<script setup>
// LectureCard 컴포넌트 import
import LectureCard from './LectureCard.vue'
import { defineProps } from 'vue'

/**
 * props 정의
 * - lectures: 부모 컴포넌트(HomePage.vue)로부터 전달받은 강의 데이터 배열
 */
const props = defineProps({
    lectures: {
        type: Array,
        required: true,
    },
})
</script>

<style scoped>
/* 추가 스타일이 필요하면 여기에 작성 */
</style>
