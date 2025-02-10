<template>
    <div class="flex flex-col col-span-12 gap-9">
        <DetailHeader />

        <div class="overflow-hidden border border-gray-200 rounded-2xl">
            <!-- 탭 메뉴 -->
            <TabMenu v-model="selectedTab" tab-left="커리큘럼" tab-right="리뷰" />

            <!-- 커리큘럼 섹션 -->
            <CurriculumSection v-if="selectedTab === 'left'" />

            <!-- 리뷰 섹션 -->
            <LectureReviewSection
                v-if="selectedTab === 'right'"
                :self-review="selfReview"
                @edit-review="openReviewModal"
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
            />
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, watchEffect } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getLectureDetail, getSelfReview } from '@/helpers/lecture'

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

const lecture = ref({
    category: '프로그래밍언어',
    tags: '태그1,태그2,태그3',
    title: '독학으로 완성하는 파이썬',
    lecturer: 'All_That_AI',
    currentPrice: 38500,
    originPrice: 55000,
    sourceName: '인프런',
    sourceUrl: 'https://www.inflearn.com/course/독학-완성-파이썬',
    imgUrl: 'https://cdn.inflearn.com/public/courses/333468/cover/d9dbbe26-ebd8-4e8e-baa3-37df3a633907/333468.png',
    curriculum: {
        1: {
            majorTitle: '섹션 1. 파이썬 시작하기',
            subLectures: [
                { title: '입문자를 위한 파이썬 공부 비법', time: '20:03' },
                { title: '프로그래밍 이론으로 맛보기 1', time: '23:20' },
            ],
        },
    },
    bookmarkCount: 51,
    rating: 4.7,
    isBookmarked: true,
})

// API에서 강의 데이터 가져오기 (현재는 주석 처리된 상태)
// const lecter = ref(null)
// onMounted(async () => {
//     try {
//         const response = await getLectureDetail(route.params.id)
//         lecture.value = response.data
//     } catch (error) {
//         console.error('API 호출 중 오류 발생: ', error)
//     }
// })

// ✅ onMounted에서 fetchUser() 실행
onMounted(async () => {
    await userStore.fetchUser()
    console.log('🚀 유저 데이터 패치 완료')
})

// ✅ watchEffect 사용: userStore.token이 변경될 때 자동 실행
watchEffect(async () => {
    if (userStore.token) {
        try {
            console.log('✅ 토큰 확인됨, 리뷰 데이터 불러오기 시작')

            const selfReviewResponse = await getSelfReview(userStore.token, route.params.id)

            console.log('🔍 API 응답 전체:', selfReviewResponse)
            console.log('🔍 응답 데이터 타입:', typeof selfReviewResponse.data)

            if (selfReviewResponse.data === null) {
                console.warn('⚠️ API 응답이 null입니다. 기본값으로 설정합니다.')
            } else if (selfReviewResponse.data === '') {
                console.warn('⚠️ API 응답이 빈 문자열입니다. 기본값으로 설정합니다.')
            }

            selfReview.value = selfReviewResponse.data || null

            console.log('✅ 최종 selfReview 값:', selfReview.value)
        } catch (error) {
            console.error('❌ 본인 리뷰 불러오기 실패:', error)
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
</script>

<style scoped></style>
