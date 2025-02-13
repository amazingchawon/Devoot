<template>
    <div class="flex flex-col col-span-12 gap-9">
        <DetailHeader v-if="lecture" :lecture="lecture" />

        <div class="overflow-hidden border border-gray-200 rounded-2xl">
            <!-- 탭 메뉴 -->
            <TabMenu v-model="selectedTab" tab-left="커리큘럼" tab-right="리뷰" />

            <!-- 커리큘럼 섹션 -->
            <CurriculumSection v-if="lecture && selectedTab === 'left'" :lecture="lecture" />

            <!-- 리뷰 섹션 -->
            <LectureReviewSection
                v-if="selectedTab === 'right'"
                :reviews="reviews"
                :self-review="selfReview"
                @edit-review="openReviewModal"
                @update-reviews="refreshReviews"
            />
        </div>

        <!-- 리뷰 수정 모달 (Dim 배경 포함) -->
        <div
            v-if="isModalOpen"
            class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50"
            @click.self="closeReviewModal"
        >
            <LectureReviewEditModal
                v-if="isModalOpen"
                :lecture="lecture"
                :self-review="selfReview"
                class="w-full max-w-2xl p-6 bg-white shadow-lg rounded-2xl"
                @close-modal="isModalOpen = false"
                @update-reviews="refreshReviews"
            />
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, watchEffect } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getLectureDetail, getSelfReview, getLectureReview } from '@/helpers/lecture'

import CurriculumSection from '@/components/Lecture/CurriculumSection.vue'
import DetailHeader from '@/components/Lecture/DetailHeader.vue'
import TabMenu from '@/components/Common/TabMenu.vue'
import LectureReviewSection from '@/components/Lecture/LectureReviewSection.vue'
import LectureReviewEditModal from '@/components/Lecture/LectureReviewEditModal.vue'

const route = useRoute()
const userStore = useUserStore()

const selectedTab = ref('left') // 기본값: '커리큘럼' 탭
const isModalOpen = ref(false) // 리뷰 수정 모달 상태
const selfReview = ref(null) // selfReview를 관리
const reviews = ref([]) // 전체 리뷰 목록을 저장

const lecture = ref(null)

// ✅ onMounted에서 fetchUser() 실행 / API에서 강의 데이터 가져오기
onMounted(async () => {
    await userStore.fetchUser()
    console.log('🚀 유저 데이터 패치 완료')
})

// ✅ watchEffect 사용: userStore.token이 변경될 때 자동 실행
watchEffect(async () => {
    if (userStore.token) {
        try {
            const response = await getLectureDetail(userStore.token, route.params.id)
            lecture.value = response.data.lectureDetail

            await refreshReviews() // ✅ 리뷰 목록과 본인 리뷰 가져오
        } catch (error) {
            console.error('❌ 강의 정보 불러오기 실패:', error)
        }
    }
})

// 모달 열기
const openReviewModal = () => {
    isModalOpen.value = true
}

// 모달 닫기
const closeReviewModal = () => {
    isModalOpen.value = false
}

// 페이지네이션
const pageIndex = ref(1) // 나중에 페이지네이션과 연결 해야함

// ✅ 리뷰 목록 가져오기
const fetchReviews = async () => {
    try {
        const response = await getLectureReview(route.params.id, pageIndex.value)
        reviews.value = response.data.content
    } catch (error) {
        console.error('❌ 리뷰 목록 불러오기 실패:', error)
    }
}

// ✅ 본인 리뷰 가져오기
const fetchSelfReview = async () => {
    if (userStore.token) {
        try {
            const response = await getSelfReview(userStore.token, route.params.id)
            selfReview.value = response.data || null // 리뷰가 없으면 null 설정
        } catch (error) {
            console.error('❌ selfReview 불러오기 실패:', error)
        }
    }
}

// ✅ 리뷰 목록과 본인 리뷰 모두 새로고침하는 함수
const refreshReviews = async () => {
    console.log('🔄 리뷰 목록 및 selfReview 새로고침')
    await fetchReviews()
    await fetchSelfReview()
}
</script>

<style scoped></style>
