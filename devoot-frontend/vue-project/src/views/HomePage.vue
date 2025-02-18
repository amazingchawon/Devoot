<template>
    <div class="space-y-12">
        <!-- 인기 강의 -->
        <section class="space-y-4">
            <h1 class="text-h1 col-span-full">인기 강의</h1>
            <LectureCardGroup :lectures="popularLectures" />
        </section>

        <!-- 신규 강의 -->
        <section class="space-y-4">
            <h1 class="text-h1 col-span-full">신규 강의</h1>
            <LectureCardGroup :lectures="newestLectures" />
        </section>

        <!-- 무료 강의 -->
        <section class="space-y-4">
            <h1 class="text-h1 col-span-full">무료 강의</h1>
            <LectureCardGroup :lectures="freeLectures" />
        </section>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import LectureCardGroup from '@/components/Lecture/LectureCardGroup.vue'
import { searchLectures } from '@/helpers/lecture.js'

// 강의 데이터를 저장할 ref
const lectures = ref([])

// 인기 강의: rating 기준 내림차순 정렬 후 상위 8개 선택
const popularLectures = computed(() => {
    return [...lectures.value].sort((a, b) => (b.rating || 0) - (a.rating || 0)).slice(0, 8)
})

// 신규 강의: 전체 강의 중 마지막 8개를 최신순(역순)으로 선택
const newestLectures = computed(() => {
    return [...lectures.value].slice(-8).reverse()
})

// 무료 강의: currentPrice가 0인 강의들을 최대 8개 선택
const freeLectures = computed(() => {
    return lectures.value.filter((lecture) => lecture.currentPrice === 0).slice(0, 8)
})

// API 호출: 쿼리 파라미터 없이 전체 강의를 조회 (필요한 경우 page와 size만 지정)
async function fetchLectures() {
    try {
        // 필터 조건 없이 모든 강의를 가져오기 위해 최소한의 파라미터만 전달합니다.
        const params = {
            page: 1,
            size: 50, // 충분한 개수를 받아서 프론트엔드에서 분류할 수 있도록
        }
        // 혹은 아래와 같이 빈 객체를 전달해도 됩니다.
        // const response = await searchLectures({});
        const response = await searchLectures(params)
        // console.log('서치렉쳐데이터:', response.data)
        const { content } = response.data

        // LectureCardGroup 컴포넌트에 맞게 데이터 가공 (숫자형 변환 포함)
        lectures.value = content.map((item, index) => ({
            id: index,
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
            currentPrice: Number(item.currentPrice), // 숫자형으로 변환
            originalPrice: Number(item.originPrice),
            rating: Number(item.rating),
            reviewCount: item.reviewCnt,
            isBookmarked: false,
        }))

        console.log('전체 강의 데이터:', lectures.value)
        console.log('무료 강의 데이터:', freeLectures.value)
    } catch (error) {
        console.error('Error fetching lectures:', error)
    }
}

onMounted(() => {
    fetchLectures()
})
</script>

<style scoped>
/* 필요한 스타일 작성 */
</style>
