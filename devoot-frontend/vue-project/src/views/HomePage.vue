<!-- src/views/HomePage.vue -->
<template>
    <div class="space-y-12">
        <!-- 추천 강의 섹션 (로그인한 사용자만 표시) -->
        <section v-if="userStore.isAuthenticated" class="space-y-4">
            <p class="flex items-center text-h1 col-span-full">
                <DevootPuppy class="items-center w-9 h-9 fill-primary-500" />
                {{ userStore.userNickname }}님을 위한 추천 강의
            </p>

            <div class="flex gap-1.5 w-full">
                <div
                    v-for="tag in (userStore.userTags || '').split(',')"
                    :key="tag"
                    class="inline-flex gap-1 text-caption tag-gray text-primary-500"
                >
                    <p>#</p>
                    <p
                        class="overflow-hidden cursor-default text-ellipsis whitespace-nowrap"
                        :title="tag"
                    >
                        {{ tag }}
                    </p>
                </div>
            </div>

            <!-- API에서 이미 size=8로 받아오기 때문에 LectureCardGroup에서는 전체 배열을 전달 -->
            <LectureCardGroup :lectures="recommendLectures" />
        </section>

        <!-- 인기 강의 섹션 -->
        <section class="space-y-4">
            <p class="flex items-center gap-1 text-h1 col-span-full">
                인기 강의
                <LogoIcon class="items-center h-7 w-7 text-primary-500" />
            </p>
            <LectureCardGroup :lectures="popularLectures" />
        </section>

        <!-- 신규 강의 섹션 -->
        <section class="space-y-4">
            <p class="flex items-center gap-1 text-h1 col-span-full">
                신규 강의
                <LogoIcon class="items-center h-7 w-7 text-primary-500" />
            </p>
            <LectureCardGroup :lectures="newestLectures" />
        </section>

        <!-- 무료 강의 섹션 -->
        <section class="space-y-4">
            <p class="flex items-center gap-1 text-h1 col-span-full">
                무료 강의 <LogoIcon class="items-center h-7 w-7 text-primary-500" />
            </p>
            <LectureCardGroup :lectures="freeLectures" />
        </section>
    </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { searchLectures } from '@/helpers/lecture.js'
import { useUserStore } from '@/stores/user'

import LectureCardGroup from '@/components/Lecture/LectureCardGroup.vue'
import LogoIcon from '@/assets/icons/logo.svg'
import DevootPuppy from '@/assets/icons/devoot_mascotte.svg'

const userStore = useUserStore()

// 로그인한 사용자의 tags 가져오기
const userTags = computed(() => {
    if (userStore.isUserLoaded && userStore.userTags) {
        return userStore.userTags.split(',').map((tag) => tag.trim())
    }
    return []
})

// userStore.userTags 변경 감지 후 추천 강의 다시 불러오기
watch(
    userTags,
    (newTags) => {
        if (newTags.length > 0) {
            console.log('✅ 로그인한 사용자의 tags:', newTags)
            fetchRecommendLectures()
        }
    },
    { immediate: true }
)

onMounted(async () => {
    await userStore.fetchUser() // 유저 정보 로드 후 실행

    fetchPopularLectures()
    fetchNewestLectures()
    fetchFreeLectures()
})

/**
 * 헬퍼 함수: API 응답 데이터를 LectureCard 컴포넌트가 사용하는 형태로 가공
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

const recommendLectures = ref([])
const popularLectures = ref([])
const newestLectures = ref([])
const freeLectures = ref([])

// 추천 강의 API 호출
async function fetchRecommendLectures() {
    if (userTags.value.length === 0) {
        console.warn('⚠️ 추천 강의를 가져오려면 태그가 필요합니다.')
        return
    }

    const tagQuery = userTags.value.join(',')
    const params = { sort: 'relevance', page: 1, size: 8, tag: tagQuery }

    try {
        const response = await searchLectures(params)
        recommendLectures.value = response.data.content.map(mapLectureItem)
    } catch (error) {
        console.error('❌ Error fetching recommend lectures:', error)
    }
}

// 인기 강의 API 호출
async function fetchPopularLectures() {
    try {
        const params = { sort: 'popular', page: 1, size: 8 }
        const response = await searchLectures(params)
        popularLectures.value = response.data.content.map(mapLectureItem)
    } catch (error) {
        console.error('❌ Error fetching popular lectures:', error)
    }
}

// 신규 강의 API 호출
async function fetchNewestLectures() {
    try {
        const params = { sort: 'newest', page: 1, size: 8 }
        const response = await searchLectures(params)
        newestLectures.value = response.data.content.map(mapLectureItem)
    } catch (error) {
        console.error('❌ Error fetching newest lectures:', error)
    }
}

// 무료 강의 API 호출
async function fetchFreeLectures() {
    try {
        const params = { sort: 'price_asc', page: 1, size: 8 }
        const response = await searchLectures(params)
        freeLectures.value = response.data.content.map(mapLectureItem)
    } catch (error) {
        console.error('❌ Error fetching free lectures:', error)
    }
}
</script>
