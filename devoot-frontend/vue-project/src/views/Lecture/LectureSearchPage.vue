<template>
    <div class="lecture-search-page">
        <!-- 검색 결과 헤더 -->
        <div class="flex flex-row items-baseline gap-2 mb-6">
            <p class="text-h1" v-if="searchQuery">"{{ searchQuery }}" 에 대한 검색 결과</p>
            <p class="text-h1" v-else-if="selectedCategory">
                "{{ selectedCategory }}" 카테고리에 대한 검색 결과
            </p>
            <p class="text-h1" v-else>전체 강의</p>
            <span>({{ totalElements }})</span>
        </div>

        <!-- 태그 선택 영역 -->
        <div class="flex flex-col w-full gap-12 mb-6">
            <div class="flex-1">
                <div class="flex flex-wrap w-full max-w-[1440px] gap-2 p-4">
                    <button
                        v-for="tag in displayedTags"
                        :key="tag"
                        type="button"
                        :class="{
                            'tag-gray py-1 px-2': !isTagSelected(tag),
                            'tag-primary py-1 px-2': isTagSelected(tag),
                        }"
                        @click="toggleTag(tag)"
                    >
                        <div class="flex flex-row items-center gap-1">
                            {{ tag }}
                            <span v-if="isTagSelected(tag)" class="ml-1">&times;</span>
                        </div>
                    </button>
                </div>
            </div>
        </div>

        <!-- 강의 목록 -->
        <div v-if="lectures.length" class="pb-4 overflow-x-auto">
            <div class="grid grid-cols-4 min-w-max gap-x-6 gap-y-4">
                <LectureCard
                    v-for="(lecture, index) in lectures"
                    :key="lecture.id || index"
                    v-bind="lecture"
                />
            </div>
        </div>
        <p v-else>검색 결과가 없습니다.</p>

        <!-- 페이지네이션 -->
        <div v-if="totalElements > 0" class="mt-6">
            <PaginationControl
                :currentPage="page"
                :totalPages="totalPages"
                @page-changed="onPageChange"
            />
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRoute } from 'vue-router'
import LectureCard from '@/components/Lecture/LectureCard.vue'
import PaginationControl from '@/components/Common/PaginationControl.vue'
import { searchLectures } from '@/helpers/lecture.js'

const route = useRoute()
const searchQuery = ref(route.query.q || '')
const selectedCategory = ref(route.query.category || '')
const selectedSort = ref(route.query.sort || 'newest')

const lectures = ref([])
const page = ref(1)
const size = ref(40)
const totalElements = ref(0)
const totalPages = ref(0)

// 전체 태그 목록
const allTags = [
    'HTML',
    'JavaScript',
    'Java',
    'Ruby',
    'TypeScript',
    'Swift',
    'Kotlin',
    'Python',
    'C',
    'C++',
    'C#',
    'Go',
    'Scala',
    'Dart',
    'MySQL',
    'Oracle',
    'Markdown',
    '데이터 분석',
    '데이터 엔지니어링',
    '딥러닝/머신러닝',
    '컴퓨터 비전',
    '자연어 처리',
    '시스템/운영체제',
    '블록체인',
    '컴퓨터 구조',
    '임베디드/IoT',
    '반도체',
    '로보공학',
    'UX/UI',
]
const selectedTags = ref([])
const presetTags = ref([])

// 검색어나 카테고리가 없으면 `allTags`, 있으면 `presetTags` 사용
const displayedTags = computed(() => {
    return searchQuery.value || selectedCategory.value ? presetTags.value : allTags
})

// 태그 선택/해제 후 API 호출
function toggleTag(tag) {
    if (isTagSelected(tag)) {
        selectedTags.value = selectedTags.value.filter((t) => t !== tag)
    } else {
        selectedTags.value.push(tag) // 선택 개수 제한 제거
    }
    page.value = 1
    fetchLectures(false) // 태그 선택 시 API 호출 (presetTags 갱신 X)
}

// 태그 선택 여부 확인
function isTagSelected(tag) {
    return selectedTags.value.includes(tag)
}

// 강의 검색 API 호출
const fetchLectures = async (updatePresetTags = true) => {
    try {
        const params = {
            category: selectedCategory.value || undefined,
            tag: selectedTags.value.length > 0 ? selectedTags.value.join(',') : undefined, // 선택한 태그들 전달
            sort: selectedSort.value,
            query: searchQuery.value || undefined,
            page: page.value,
            size: size.value,
        }
        const response = await searchLectures(params)
        totalElements.value = response.data.totalElements
        totalPages.value = response.data.totalPages
        lectures.value = response.data.content.map((item, index) => ({
            id: item.id || index,
            name: item.name,
            lecturer: item.lecturer,
            platform: item.sourceName || '인프런',
            imageUrl: item.imageUrl,
            tags: item.tags ? item.tags.split(',').map((tag) => tag.trim()) : [],
            currentPrice: Number(item.currentPrice),
            originalPrice: Number(item.originPrice),
            rating: Number(item.rating),
            reviewCount: item.reviewCnt,
            isBookmarked: false,
        }))

        // 검색어 및 카테고리가 변경될 때만 presetTags 업데이트
        if (updatePresetTags) {
            presetTags.value = Object.keys(response.data.aggregations.preset_tags || {})
        }
    } catch (error) {
        console.error('강의 검색 API 호출 실패:', error)
    }
}

// 페이지 변경 이벤트
const onPageChange = (newPage) => {
    page.value = newPage
    fetchLectures(false) // 페이지네이션 시 API 호출
}

// URL 변경 감지
watch(
    () => [route.query.q, route.query.category, route.query.sort],
    ([newQ, newCat, newSort]) => {
        searchQuery.value = newQ || ''
        selectedCategory.value = newCat || ''
        selectedSort.value = newSort || 'newest'
        page.value = 1
        fetchLectures(true) // 검색어/카테고리 변경 시 presetTags 업데이트
    }
)

// 컴포넌트 마운트 시 초기 검색 API 호출
onMounted(() => {
    fetchLectures()
})
</script>

<style scoped>
/* 선택 가능한 태그 스타일 */
.tag-gray {
    @apply bg-gray-100 text-gray-700 hover:bg-gray-200 cursor-pointer;
}

/* 선택된 태그 스타일 */
.tag-primary {
    @apply bg-blue-500 text-white;
}
</style>
