<template>
    <div class="flex flex-col w-full gap-3 p-6 border border-gray-200 rounded-2xl">
        <div class="flex justify-between">
            <div class="flex gap-[5px]">
                <p class="flex items-center text-gray-300 text-caption">{{ review.sourceName }}</p>
                <span class="mx-1 text-gray-300">·</span>

                <p class="flex items-center text-gray-300 text-caption">
                    <a :href="review.sourceUrl">{{ review.reviewName }}</a>
                </p>
            </div>
            <div class="flex gap-2">
                <!-- 본인 리뷰인 경우 : 수정하기 -->
                <div
                    v-if="isMyProfile"
                    class="relative flex flex-row items-center gap-1 text-gray-300 cursor-pointer group"
                    @click="editReview"
                >
                    <Edit class="w-4 h-4" />
                    <p class="hidden text-caption md:inline">수정하기</p>
                    <!-- 툴팁 -->
                    <div
                        class="absolute px-2 py-1 text-xs text-white transition-opacity transform -translate-x-1/2 -translate-y-2 bg-black rounded opacity-0 cursor-default left-1/2 bottom-full whitespace-nowrap group-hover:opacity-100"
                    >
                        수정하기
                    </div>
                </div>

                <!-- 삭제하기 -->
                <div
                    v-if="isMyProfile"
                    class="relative flex flex-row items-center gap-1 text-gray-300 cursor-pointer group"
                    @click="deleteReview"
                >
                    <Trash class="w-4 h-4" />
                    <p class="hidden md:inline text-caption">삭제하기</p>
                    <!-- 툴팁 -->
                    <div
                        class="absolute px-2 py-1 text-xs text-white transition-opacity transform -translate-x-1/2 -translate-y-2 bg-black rounded opacity-0 cursor-default left-1/2 bottom-full whitespace-nowrap group-hover:opacity-100"
                    >
                        삭제하기
                    </div>
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
        <div class="text-right text-gray-300 text-caption">{{ formattedDate }}</div>
    </div>
</template>

<script setup>
import Edit from '@/assets/icons/edit.svg'
import Trash from '@/assets/icons/trash.svg'
import Star from '@/assets/icons/star_filled.svg'
import { useUserStore } from '@/stores/user'
import { useRoute } from 'vue-router'
import { computed, ref } from 'vue'
const userStore = useUserStore() // Pinia 스토어 가져오기
const route = useRoute()
const props = defineProps({
    review: {
        type: Object,
        required: true,
    },
})

const isMyProfile = computed(() => userStore.userId === route.params.id)

// 날짜 포맷 변경
const createdAt = ref(props.review.createdAt) // Example ISO date string from the backend
const timezone = ref(Intl.DateTimeFormat().resolvedOptions().timeZone) // Get browser's timezone
const formattedDate = computed(() => {
    const date = new Date(createdAt.value)

    const options = {
        timeZone: timezone.value,
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: true,
    }

    return new Intl.DateTimeFormat('ko-KR', options).format(date)
})

// 별 개수 계산
const fullStars = computed(() => Math.floor(props.review.rating))
const hasHalfStar = computed(() => props.review.rating % 1 !== 0)
const emptyStars = computed(() => 5 - fullStars.value - (hasHalfStar.value ? 1 : 0))

console.log('리뷰 평점 확인', props.review.rating) // 리뷰 평점 확인
console.log('꽉 찬 별 개수', fullStars.value) // 꽉 찬 별 개수
console.log('true이면 반개 별 표시', hasHalfStar.value) // true이면 반개 별 표시
console.log('빈 별 개수', emptyStars.value) // 빈 별 개수

//==========================
// 리뷰 삭제 / 수정 / 신고
//==========================
const emit = defineEmits(['edit-review', 'delete-review', 'update-self-review']) // 부모 컴포넌트에 이벤트 전달

const editReview = () => {
    emit('edit-review', props.review) // ProfileReviewSection으로 이벤트 전달
}

const deleteReview = () => {
    emit('delete-review', props.review) // ProfileReviewSection으로 이벤트 전달
}
</script>

<style>
.clip-half {
    clip-path: inset(0 50% 0 0); /* 상, 우, 하, 좌 */
}
</style>
