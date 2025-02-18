<!-- src/components/Lecture/LectureCardGroup.vue -->
<template>
    <!-- 외부 컨테이너: pb-4 (아래 여백)와 overflow-x-auto (수평 스크롤) 적용 -->
    <div class="pb-4 overflow-x-auto">
        <!-- 내부 그리드 컨테이너:
           - min-w-max: 내부 콘텐츠 너비를 유지하여 화면이 좁을 경우 스크롤이 생김
           - grid-cols-4: 1행에 4개씩 배치
           - gap-x-6: 좌우 간격 24px (6 * 4px = 24px)
           - gap-y-4: 상하 간격 16px (4 * 4px = 16px)
      -->
        <div class="grid grid-cols-4 min-w-max gap-x-6 gap-y-4">
            <LectureCard v-for="lecture in lectures" :key="lecture.id" v-bind="lecture" />
        </div>
    </div>
</template>

<script setup>
import { defineProps } from 'vue'
import LectureCard from './LectureCard.vue'

/**
 * 부모 컴포넌트로부터 강의 데이터 배열을 전달받음
 */
const props = defineProps({
    lectures: {
        type: Array,
        required: true,
    },
})
</script>

<style scoped>
/* custom-scrollbar 스타일이 필요하다면 여기에 추가 */
</style>
