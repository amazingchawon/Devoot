<!-- src/views/HomePage.vue -->
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

<script>
import LectureCardGroup from '@/components/Lecture/LectureCardGroup.vue'
import { searchLectures } from '@/helpers/lecture'

export default {
    name: 'HomePage',
    components: {
        LectureCardGroup,
    },
    data() {
        return {
            lectures: [],
        }
    },
    computed: {
        // 인기 강의: rating 내림차순 상위 8개
        popularLectures() {
            return [...this.lectures].sort((a, b) => (b.rating || 0) - (a.rating || 0)).slice(0, 8)
        },
        // 신규 강의: 최신 순 8개
        newestLectures() {
            return [...this.lectures].slice(-8).reverse()
        },
        // 무료 강의: currentPrice === 0
        freeLectures() {
            return this.lectures.filter((lecture) => lecture.currentPrice === 0).slice(0, 8)
        },
    },
    created() {
        this.fetchLectures()
    },
    methods: {
        async fetchLectures() {
            try {
                const response = await searchLectures({
                    category: '모바일 앱 개발',
                    tag: '데이터 분석,데이터 엔지니어링',
                    sort: 'newest',
                    query: 'Unity asdf',
                    page: 1,
                    size: 10,
                })

                // 실제 API 응답 구조에 맞춰 데이터 추출
                const { content } = response.data

                // LectureCard.vue의 props에 맞게 가공
                this.lectures = content.map((item, index) => ({
                    id: index, // 임시 ID
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
                    currentPrice: item.currentPrice ?? 0,
                    originalPrice: item.originPrice ?? 0,
                    rating: item.rating ?? 0,
                    reviewCount: item.reviewCount ?? 0,
                    isBookmarked: false,
                }))
            } catch (error) {
                console.error('Error loading lecture data:', error)
            }
        },
    },
}
</script>

<style scoped></style>
