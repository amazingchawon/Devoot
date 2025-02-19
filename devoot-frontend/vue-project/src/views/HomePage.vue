<!-- src/views/HomePage.vue -->
<template>
    <div class="space-y-12">
        <!-- 인기 강의 섹션 -->
        <section class="space-y-4">
            <h1 class="text-h1 col-span-full">인기 강의</h1>
            <!-- API에서 이미 size=8로 받아오기 때문에 LectureCardGroup에서는 전체 배열을 전달 -->
            <LectureCardGroup :lectures="popularLectures" />
        </section>

        <!-- 신규 강의 섹션 -->
        <section class="space-y-4">
            <h1 class="text-h1 col-span-full">신규 강의</h1>
            <LectureCardGroup :lectures="newestLectures" />
        </section>

        <!-- 무료 강의 섹션 -->
        <section class="space-y-4">
            <h1 class="text-h1 col-span-full">무료 강의</h1>
            <LectureCardGroup :lectures="freeLectures" />
        </section>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import LectureCardGroup from '@/components/Lecture/LectureCardGroup.vue'
import { searchLectures } from '@/helpers/lecture.js'

/**
 * 헬퍼 함수: API 응답 데이터를 LectureCard 컴포넌트가 사용하는 형태로 가공
 * @param {Object} item - API에서 전달된 강의 데이터 객체
 * @param {number} index - 배열 내 인덱스 (id가 없을 경우 대체)
 */
function mapLectureItem(item, index) {
    return {
        id: item.id || index,
        name: item.name,
        lecturer: item.lecturer,
        platform: item.sourceName || '인프런',
        imageUrl: item.imageUrl,
        tags: item.tags
            ? item.tags
                  .split(',')
                  .map((tag) => tag.trim())
                  .filter(Boolean)
            : [],
        currentPrice: Number(item.currentPrice),
        originalPrice: Number(item.originPrice),
        rating: Number(item.rating),
        reviewCount: item.reviewCnt,
        isBookmarked: false,
        sourceUrl: item.sourceUrl || '',
    }
}

// 반응형 상태: 각 섹션별 강의 데이터 배열
const popularLectures = ref([])
const newestLectures = ref([])
const freeLectures = ref([])

/**
 * 인기 강의 API 호출
 * - 쿼리 파라미터: sort=popular, page=1, size=8
 */
async function fetchPopularLectures() {
    try {
        const params = { sort: 'popular', page: 1, size: 8 }
        const response = await searchLectures(params)
        // API 응답 데이터(content)를 가공하여 저장
        popularLectures.value = response.data.content.map(mapLectureItem)
    } catch (error) {
        console.error('Error fetching popular lectures:', error)
    }
}

/**
 * 신규 강의 API 호출
 * - 쿼리 파라미터: sort=newest, page=1, size=8
 */
async function fetchNewestLectures() {
    try {
        const params = { sort: 'newest', page: 1, size: 8 }
        const response = await searchLectures(params)
        newestLectures.value = response.data.content.map(mapLectureItem)
    } catch (error) {
        console.error('Error fetching newest lectures:', error)
    }
}

/**
 * 무료 강의 API 호출
 * - 무료 강의는 API에 무료 필터가 없으므로, size를 크게 요청한 후
 *   currentPrice가 0인 강의만 필터링하여 최대 8개만 사용합니다.
 */
async function fetchFreeLectures() {
    try {
        const params = { sort: 'price_asc', page: 1, size: 8 }
        const response = await searchLectures(params)
        freeLectures.value = response.data.content.map(mapLectureItem)
    } catch (error) {
        console.error('Error fetching free lectures:', error)
    }
}

// 컴포넌트가 마운트되면 세 섹션별 API 호출
onMounted(() => {
    fetchPopularLectures()
    fetchNewestLectures()
    fetchFreeLectures()
})
</script>

<style scoped>
/* 추가 스타일이 필요한 경우 여기에 작성 */
</style>
