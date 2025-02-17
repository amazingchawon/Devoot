<template>
    <!-- 오버레이 배경 -->
    <div class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
        <div
            class="relative flex flex-col w-full max-w-lg gap-6 bg-white border border-gray-200 rounded-lg shadow-lg px-9 py-7"
        >
            <div id="modal-header" class="flex flex-row">
                <p class="text-h2">
                    {{ review ? '댓글 수정하기' : '댓글 작성하기' }}
                </p>
                <div class="flex-1"></div>
                <Delete
                    class="w-6 h-6 cursor-pointer hover:text-primary-500"
                    @click="emit('closeModal')"
                />
            </div>

            <!-- 강의 카드 -->
            <ReviewEditModalLectureCard
                :lecture="lecture"
                @close-modal="gotoLectureDetail(props.review.lectureId)"
            />

            <div id="text-container" class="flex flex-col gap-1">
                <div class="flex items-center gap-1" @mouseleave="resetHover">
                    <div id="star-container" class="flex flex-row">
                        <div
                            v-for="index in 5"
                            :key="'star-' + index"
                            class="relative w-6 h-6 cursor-pointer"
                            @mousemove="setHover(index, $event)"
                            @click="setRating(index, $event)"
                        >
                            <Star class="absolute top-0 left-0 w-full h-full text-gray-200" />
                            <Star
                                class="absolute top-0 left-0 w-full h-full text-yellow-300"
                                :style="{ clipPath: getClipPath(index) }"
                            />
                        </div>
                    </div>
                    <p class="text-black text-body">{{ rating }}점</p>
                </div>

                <textarea
                    v-model="text"
                    class="w-full px-4 py-2 mt-4 overflow-y-auto bg-gray-200 border border-gray-200 rounded-lg resize-none text-body h-52 focus:border-2 focus:border-primary-500 focus:outline-none custom-scrollbar"
                    placeholder="댓글을 입력하세요"
                ></textarea>
            </div>
            <!-- 저장 버튼 -->
            <div class="flex justify-end">
                <button class="button-primary" @click="handleReview">
                    {{ review ? '수정하기' : '저장하기' }}
                </button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import ReviewEditModalLectureCard from '../Lecture/ReviewEditModalLectureCard.vue'
import {
    writeLectureReview,
    editLectureReview,
    getLectureDetailWithLogout,
} from '@/helpers/lecture'

import Delete from '@/assets/icons/delete.svg'
import Star from '@/assets/icons/star_filled.svg'

const props = defineProps({
    review: {
        type: Object,
        default: () => null, // 부모로부터 리뷰 데이터 받기
    },
})

// `close` 및 `update-reviews` 이벤트를 부모 컴포넌트로 전달할 emit 정의
const emit = defineEmits(['closeModal', 'update-reviews'])

const userStore = useUserStore()

// 댓글 내용
const text = ref(props.review?.content || '') // 기존 리뷰 내용

// // 별점 상태
const rating = ref(props.review?.rating || 0) // 기존 별점
const hoverRating = ref(rating.value) // 호버 중 별점

watch(
    () => [props.review, props.lecture],
    ([newReview, newLecture]) => {
        if (newReview && newLecture) {
            console.log('review와 lecture 데이터가 준비되었습니다.')
        }
    },
    { immediate: true }
)

// 마우스 호버 시 별점 설정
const setHover = (index, event) => {
    const rect = event.target.getBoundingClientRect()
    const isLeftHalf = event.clientX < rect.left + rect.width / 2
    hoverRating.value = isLeftHalf ? index - 0.5 : index // 절반 단위로 업데이트
}

// 별점 클릭 시 값 고정
const setRating = (index, event) => {
    const rect = event.target.getBoundingClientRect()
    const isLeftHalf = event.clientX < rect.left + rect.width / 2
    rating.value = isLeftHalf ? index - 0.5 : index // 절반 단위 고정
    hoverRating.value = rating.value // 호버 상태도 업데이트
}

// 마우스가 별을 벗어나면 기존 값으로 복원
const resetHover = () => {
    hoverRating.value = rating.value
}

// 별의 clip-path를 계산
const getClipPath = (index) => {
    if (hoverRating.value >= index) {
        return 'inset(0 0 0 0)' // 꽉 찬 별
    } else if (hoverRating.value >= index - 0.5) {
        return 'inset(0 50% 0 0)' // 절반 별
    } else {
        return 'inset(0 100% 0 0)' // 빈 별
    }
}

// 저장 및 수정 함수
const handleReview = async () => {
    if (!text.value.trim()) {
        alert('댓글을 입력해주세요!')
        return
    }
    if (rating.value === 0) {
        alert('별점을 선택해주세요!')
        return
    }

    try {
        if (props.review) {
            // 리뷰 수정
            await editLectureReview(
                userStore.token,
                props.review.id,
                props.review.lectureId,
                text.value,
                rating.value
            )
            alert('리뷰가 수정되었습니다.')
        } else {
            // 리뷰 작성
            await writeLectureReview(
                userStore.token,
                props.review.lectureId,
                text.value,
                rating.value
            )
            alert('리뷰가 등록되었습니다.')
        }

        emit('update-reviews') // ✅ 부모에게 리뷰 목록 갱신 요청
        emit('closeModal') // 모달 닫기
    } catch (error) {
        console.error('❌ 리뷰 저장/수정 실패:', error)
        console.log('모달에 들어갈 리뷰 데이터 ', props.review.id)
        console.log('모달에 들어갈 토큰  ', userStore.token)
        console.log('모달에 들어갈 별점 ', rating.value)

        console.log('모달에 들어갈 텍스트 ', text.value)
        console.log('모달에 들어갈 강의 데이터 ', props.lecture.lectureId)

        alert('리뷰 저장/수정에 실패했습니다.')
    }
}

const lecture = ref(null)

onMounted(async () => {
    const response = await getLectureDetailWithLogout(props.review?.lectureId)

    console.log(response.data)
    lecture.value = response.data.lectureDetail
    console.log(lecture.value)
})

const router = useRouter()

const gotoLectureDetail = (id) => {
    router.push({ name: 'lectureDetail', params: { id } })
}
</script>

<style scoped>
/* Tailwind 클래스를 적용한 스크롤바 스타일 */
.custom-scrollbar::-webkit-scrollbar {
    @apply w-2; /* 스크롤바 너비 */
}

.custom-scrollbar::-webkit-scrollbar-track {
    @apply bg-gray-100 rounded-lg; /* 트랙 배경 */
}

.custom-scrollbar::-webkit-scrollbar-thumb {
    @apply bg-gray-200 rounded-lg; /* 스크롤 핸들 색상 */
}

.custom-scrollbar::-webkit-scrollbar-thumb:hover {
    @apply bg-primary-100; /* 마우스 호버 시 색상 */
}
</style>
