<template>
    <div>
        <!-- 로딩 중 -->
        <div v-if="isLoading" class="relative z-10 space-y-4 animate-pulse">
            <div class="h-12 bg-gray-200 rounded"></div>
            <div class="w-3/4 h-6 bg-gray-200 rounded"></div>
            <div class="w-1/2 h-6 bg-gray-200 rounded"></div>
        </div>

        <!-- 로딩 완료 -->
        <div v-if="!isLoading" class="flex flex-col col-span-12 gap-9">
            <div class="flex flex-col gap-4">
                <!-- 강의 신고 -->
                <div class="flex flex-row items-center justify-end gap-4">
                    <p class="text-gray-300 text-caption">강의 정보에 오류가 있나요?</p>
                    <button class="flex flex-row gap-2 button-gray" @click="handleLectureReport">
                        <Report class="w-4 h-4 text-red-500 fill-red-500" />
                        <p class="text-gray-500 text-caption">신고하기</p>
                    </button>
                </div>

                <DetailHeader v-if="lecture" :lecture="lecture" />
            </div>

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
                    :current-page="pageIndex"
                    :total-pages="totalPages"
                    @edit-review="openReviewModal"
                    @update-reviews="refreshReviews"
                    @update-page="changePage"
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
    </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import {
    getLectureDetail,
    getLectureDetailWithLogout,
    reportLecture,
    getSelfReview,
    getLectureReview,
} from '@/helpers/lecture'

import CurriculumSection from '@/components/Lecture/CurriculumSection.vue'
import DetailHeader from '@/components/Lecture/DetailHeader.vue'
import TabMenu from '@/components/Common/TabMenu.vue'
import LectureReviewSection from '@/components/Lecture/LectureReviewSection.vue'
import LectureReviewEditModal from '@/components/Lecture/LectureReviewEditModal.vue'

import Report from '@/assets/icons/report.svg'

const route = useRoute()
const userStore = useUserStore()

const selectedTab = ref('left') // 기본값: '커리큘럼' 탭
const isModalOpen = ref(false) // 리뷰 수정 모달 상태
const selfReview = ref(null) // selfReview를 관리
const reviews = ref([]) // 전체 리뷰 목록을 저장

const lecture = ref(null)

const isLoading = ref(true) // 로딩 상태

onMounted(async () => {
    await userStore.fetchUser()
    await fetchLectureDetail()
})

// ✅ watchEffect 대신 watch 사용 (로그인 시 다시 실행)
watch(
    () => userStore.token,
    async () => {
        await fetchLectureDetail()
    },
    { immediate: false } // ✅ 첫 실행 시 중복 실행 방지
)

const fetchLectureDetail = async () => {
    console.log('📢 fetchLectureDetail 호출됨')

    try {
        if (userStore.token) {
            const response = await getLectureDetail(userStore.token, route.params.id)
            lecture.value = response.data.lectureDetail
            await refreshReviews() // 리뷰도 가져오기
            console.log('강의정보', lecture.value)
        } else {
            const response = await getLectureDetailWithLogout(route.params.id)
            lecture.value = response.data.lectureDetail
            await refreshReviews()
            console.log('강의정보시래이ㅏㄻ러ㅣㅁㄹ')
        }
    } catch (error) {
        console.error('❌ 강의 정보 불러오기 실패:', error)
    } finally {
        isLoading.value = false
    }
}

//===========================
// 페이지네이션
//===========================

const totalPages = ref(0)
const pageIndex = ref(1) // 나중에 페이지네이션과 연결 해야함

// ✅ 페이지 변경 함수
const changePage = (page) => {
    if (page !== pageIndex.value) {
        pageIndex.value = page
        console.log(`페이지가 ${pageIndex.value}로 변경됨, 데이터 새로 요청`)

        fetchReviews().then(() => {
            // ✅ 페이지네이션이 보이는 바닥으로 스크롤 이동
            setTimeout(() => {
                window.scrollTo({ top: document.body.scrollHeight, behavior: 'smooth' })
            }, 100) // 데이터 로드 후 스크롤 이동 (지연 시간 추가)
        })
    }
}

//===========================
// 리뷰
//===========================

// ✅ 리뷰 목록 가져오기
const fetchReviews = async () => {
    try {
        const response = await getLectureReview(route.params.id, pageIndex.value)
        reviews.value = response.data.content
        totalPages.value = response.data.totalPages
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

// 모달 열기
const openReviewModal = () => {
    isModalOpen.value = true
}

// 모달 닫기
const closeReviewModal = () => {
    isModalOpen.value = false
}

//===========================
// 리뷰
const handleLectureReport = async () => {
    if (!userStore.token) return

    const isConfirmed = confirm('이 강의를 신고하시겠습니까?')
    if (!isConfirmed) return

    try {
        await reportLecture(userStore.token, route.params.id)
        alert('신고가 접수되었습니다. 강의 내용을 검토한 후 조치하겠습니다!')
    } catch (error) {
        if (error.response?.status === 409) {
            alert('이미 신고하신 강의입니다. 빠른 검토 후 수정 조치하겠습니다!')
        } else {
            alert('신고 중 오류가 발생했습니다. 다시 시도해주세요.')
        }
    }
}
</script>

<style scoped></style>
