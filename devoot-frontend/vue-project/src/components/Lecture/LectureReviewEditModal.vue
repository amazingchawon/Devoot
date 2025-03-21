<template>
    <div class="flex flex-col gap-6 border border-gray-200 shadow-lg px-9 py-7">
        <div id="modal-header" class="flex flex-row">
            <p class="text-h2">
                {{ selfReview ? '댓글 수정하기' : '댓글 작성하기' }}
            </p>
            <div class="flex-1"></div>
            <Delete
                class="w-6 h-6 cursor-pointer hover:text-primary-500"
                @click="emit('closeModal')"
            />
        </div>

        <!-- 강의 카드 -->
        <ReviewEditModalLectureCard :lecture="lecture" @close-modal="emit('closeModal')" />

        <div id="text-container" class="flex flex-col gap-3">
            <!-- 별점 -->
            <div class="flex items-center gap-1" @mouseleave="resetHover">
                <div id="star-container" class="flex flex-row">
                    <!-- 별 렌더링 -->
                    <div
                        v-for="index in 5"
                        :key="'star-' + index"
                        class="relative w-6 h-6 cursor-pointer"
                        @mousemove="setHover(index, $event)"
                        @click="setRating(index, $event)"
                    >
                        <!-- 전체 별 -->
                        <Star class="absolute top-0 left-0 w-full h-full text-gray-200" />

                        <!-- 부분 채워진 별 -->
                        <Star
                            class="absolute top-0 left-0 w-full h-full text-yellow-300"
                            :style="{
                                clipPath: getClipPath(index),
                            }"
                        />
                    </div>
                </div>
                <p class="text-black text-body">{{ rating }}점</p>
            </div>
            <!-- 텍스트 칸 -->
            <div class="flex flex-col gap-[2px]">
                <textarea
                    v-model="text"
                    class="w-full px-4 py-2 overflow-y-auto bg-gray-100 border border-gray-200 rounded-lg resize-none text-body h-52 focus:border-2 focus:border-primary-500 focus:outline-none"
                    placeholder="댓글을 입력하세요"
                ></textarea>
                <p class="flex justify-start text-gray-400 text-caption">
                    *최대 500자까지 입력 가능합니다.
                </p>
            </div>
        </div>
        <!-- 저장 버튼 -->
        <div class="flex justify-end">
            <button class="button-primary" @click="handleReview">
                {{ selfReview ? '수정하기' : '저장하기' }}
            </button>
        </div>
    </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useUserStore } from '@/stores/user'
import ReviewEditModalLectureCard from './ReviewEditModalLectureCard.vue'
import { writeLectureReview, editLectureReview } from '@/helpers/lecture'

import Delete from '@/assets/icons/delete.svg'
import Star from '@/assets/icons/star_filled.svg'

const props = defineProps({
    lecture: {
        type: Object,
        required: true,
    },
    selfReview: {
        type: Object,
        default: () => null, // 부모로부터 리뷰 데이터 받기
    },
})

// `close` 및 `update-reviews` 이벤트를 부모 컴포넌트로 전달할 emit 정의
const emit = defineEmits(['closeModal', 'update-reviews'])

const userStore = useUserStore()

// 댓글 내용
const text = ref(props.selfReview?.content || '') // 기존 리뷰 내용

// 별점 상태
const rating = ref(props.selfReview?.rating || 0) // 기존 별점
const hoverRating = ref(rating.value) // 호버 중 별점

watch(
    () => props.selfReview,
    (newReview) => {
        text.value = newReview?.content || ''
        rating.value = newReview?.rating || 0
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
        if (props.selfReview) {
            // 리뷰 수정
            await editLectureReview(
                userStore.token,
                props.selfReview.id,
                props.lecture.id,
                text.value,
                rating.value
            )
            alert('리뷰가 수정되었습니다.')
        } else {
            // 리뷰 작성
            await writeLectureReview(userStore.token, props.lecture.id, text.value, rating.value)
            alert('리뷰가 등록되었습니다.')
        }

        emit('update-reviews') // ✅ 부모에게 리뷰 목록 갱신 요청
        emit('closeModal') // 모달 닫기
    } catch (error) {
        console.error('❌ 리뷰 저장/수정 실패:', error)
        alert('리뷰 저장/수정에 실패했습니다.')
    }
}
</script>

<style scoped></style>
