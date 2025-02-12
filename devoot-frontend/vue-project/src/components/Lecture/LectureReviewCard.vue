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

            <!-- 간격용 -->
            <div class="flex-1"></div>

            <!-- 수정하기 / 삭제하기 / 신고하기 -->
            <div
                v-if="isOwnReview"
                id="review-action"
                class="flex flex-row items-center gap-2 text-gray-300 text-caption"
            >
                <!-- 본인 리뷰인 경우 : 수정하기 -->
                <div class="relative flex flex-row items-center gap-1 group" @click="editReview">
                    <Edit class="w-4 h-4" />
                    <p class="hidden md:inline">수정하기</p>
                    <!-- 툴팁 -->
                    <div
                        class="absolute px-2 py-1 text-xs text-white transition-opacity transform -translate-x-1/2 -translate-y-2 bg-black rounded opacity-0 left-1/2 bottom-full whitespace-nowrap group-hover:opacity-100"
                    >
                        수정하기
                    </div>
                </div>

                <!-- 삭제하기 -->
                <div class="relative flex flex-row items-center gap-1 group" @click="deleteReview">
                    <Trash class="w-4 h-4" />
                    <p class="hidden md:inline">삭제하기</p>
                    <!-- 툴팁 -->
                    <div
                        class="absolute px-2 py-1 text-xs text-white transition-opacity transform -translate-x-1/2 -translate-y-2 bg-black rounded opacity-0 left-1/2 bottom-full whitespace-nowrap group-hover:opacity-100"
                    >
                        삭제하기
                    </div>
                </div>
            </div>
            <div
                v-else
                id="review-action"
                class="flex flex-row items-center gap-2 text-gray-300 text-caption"
            >
                <!-- 본인 리뷰가 아닌 경우 : 신고하기 -->
                <div class="relative flex flex-row items-center gap-1 group" @click="reportReview">
                    <Report class="w-4 h-4" />
                    <p class="hidden md:inline">신고하기</p>
                    <!-- 툴팁 -->
                    <div
                        class="absolute px-2 py-1 text-xs text-white transition-opacity transform -translate-x-1/2 -translate-y-2 bg-black rounded opacity-0 left-1/2 bottom-full whitespace-nowrap group-hover:opacity-100"
                    >
                        신고하기
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
    </div>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { deleteLectureReview, reportLectureReview } from '@/helpers/lecture'

import Edit from '@/assets/icons/edit.svg'
import Trash from '@/assets/icons/trash.svg'
import Report from '@/assets/icons/report.svg'
import Star from '@/assets/icons/star_filled.svg'

const props = defineProps({
    review: {
        type: Object,
        required: true,
    },
})

const userStore = useUserStore()
const userId = computed(() => userStore.userId) // 현재 로그인한 유저의 ID

// 본인 리뷰인지 확인하는 computed 속성
const isOwnReview = computed(() => userId.value === props.review.profileId)

// 날짜 포맷 변경
const formattedDate = computed(() => {
    const date = new Date(props.review.createdAt)
    return `${date.getFullYear()}년 ${String(date.getMonth() + 1).padStart(2, '0')}월 ${String(date.getDate()).padStart(2, '0')}일`
})

// 별 개수 계산
const fullStars = computed(() => Math.floor(props.review.rating))
const hasHalfStar = computed(() => props.review.rating % 1 !== 0)
const emptyStars = computed(() => 5 - fullStars.value - (hasHalfStar.value ? 1 : 0))

//==========================
// 리뷰 삭제 / 수정 / 신고
//==========================
const emit = defineEmits(['edit-review']) // 부모 컴포넌트에 이벤트 전달

const editReview = () => {
    emit('edit-review', props.review) // LectureReviewSection으로 이벤트 전달
}

const deleteReview = async () => {
    console.log('✅✅✅✅✅✅')
    console.log(props.review.id)
    console.log(userStore.token)

    try {
        await deleteLectureReview(userStore.token, props.review.id)
        console.log('✅ 리뷰 삭제 성공')
    } catch (error) {
        console.error('❌ 리뷰 삭제 중 오류 발생:', error)
    }
}

const reportReview = async () => {
    try {
        await reportLectureReview(userStore.token, props.review.id)
        console.log('✅ 리뷰 신고 성공')
        alert('신고가 정상적으로 접수되었습니다.') // ✅ 성공 알림
    } catch (error) {
        console.error('❌ 리뷰 신고 중 오류 발생:', error)

        // ✅ 409 Conflict 에러 처리
        if (error.response && error.response.status === 409) {
            alert('이미 신고한 리뷰입니다.') // ✅ 이미 신고한 경우 알림
        } else {
            alert('신고 중 문제가 발생했습니다. 다시 시도해주세요.') // ✅ 기타 오류 처리
        }
    }
}
</script>

<style scoped>
/* 왼쪽 절반을 보이게 설정 */
.clip-half {
    clip-path: inset(0 50% 0 0); /* 상, 우, 하, 좌 */
}
</style>
