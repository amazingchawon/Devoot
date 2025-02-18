<template>
    <div
        id="lecture-review"
        class="flex flex-col w-full gap-3 p-6 border border-gray-200 rounded-2xl"
    >
        <!-- 헤더 -->
        <div id="header" class="flex flex-row items-center">
            <!-- 작성자 정보 -->
            <div
                id="review-info"
                class="flex flex-row items-center h-10 min-w-0 gap-1 overflow-hidden text-caption w-fit flex-nowrap"
            >
                <!-- 프로필 이미지 -->
                <img :src="review.imageUrl" alt="프로필 이미지" class="w-10 h-10 rounded-full" />
                <div class="flex flex-col min-w-0 gap-1 overflow-hidden">
                    <!-- 프로필 아이디/닉네임 -->
                    <div id="reviewer-name" class="flex flex-row gap-1">
                        <p class="text-black truncate">{{ review.nickname }}</p>
                        <p class="text-gray-300 truncate">{{ review.profileId }}</p>
                    </div>
                    <!-- 작성일 -->
                    <p class="text-gray-300 truncate">{{ formattedDate }}</p>
                </div>
            </div>
        </div>
        <!-- 내용 -->
        <div class="flex flex-col gap-2">
            <!-- 별점 -->
            <div id="rating" class="flex flex-row items-center gap-1">
                <div id="stars" class="flex flex-row items-center">
                    <!-- 꽉 찬 별 -->
                    <div v-for="n in fullStars" :key="'full-' + n">
                        <Star class="w-6 h-6 text-yellow-300" />
                    </div>

                    <!-- 절반 별 -->
                    <div v-if="hasHalfStar" class="relative w-6 h-6">
                        <Star class="absolute top-0 left-0 w-full h-full text-gray-200" />
                        <Star
                            class="absolute top-0 left-0 z-10 w-full h-full text-yellow-300 clip-half"
                        />
                    </div>

                    <!-- 빈 별 -->
                    <div v-for="n in emptyStars" :key="'empty-' + n">
                        <Star class="w-6 h-6 text-gray-200" />
                    </div>
                </div>
                <p class="text-black text-body">{{ review.rating }}</p>
            </div>
            <!-- 내용 -->
            <div class="text-black text-body">{{ review.content }}</div>
        </div>
    </div>
</template>

<script setup>
import { computed } from 'vue'

import Star from '@/assets/icons/star_filled.svg'

const props = defineProps({
    review: {
        type: Object,
        required: true,
    },
})

// 날짜 포맷 변경
const formattedDate = computed(() => {
    if (!props.review.createdAt || props.review.createdAt.length < 6) return '날짜 없음'

    const [year, month, day, hour, minute] = props.review.createdAt

    return `${year}년 ${String(month).padStart(2, '0')}월 ${String(day).padStart(2, '0')}일 ${String(hour).padStart(2, '0')}시 ${String(minute).padStart(2, '0')}분`
})

// 별 개수 계산
const fullStars = computed(() => Math.floor(props.review.rating))
const hasHalfStar = computed(() => props.review.rating % 1 !== 0)
const emptyStars = computed(() => 5 - fullStars.value - (hasHalfStar.value ? 1 : 0))
</script>

<style scoped>
/* 왼쪽 절반을 보이게 설정 */
.clip-half {
    clip-path: inset(0 50% 0 0); /* 상, 우, 하, 좌 */
}
</style>
