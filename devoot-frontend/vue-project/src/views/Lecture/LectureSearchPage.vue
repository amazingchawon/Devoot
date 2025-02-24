<template>
    <div class="flex flex-col pb-20 lecture-search-page">
        <!-- 검색 결과 헤더 -->
        <div class="flex flex-row items-baseline gap-2 mb-4">
            <p class="text-h1" v-if="searchQuery">"{{ searchQuery }}" 에 대한 검색 결과</p>
            <p class="text-h1" v-else-if="selectedCategory">
                "{{ selectedCategory }}" 카테고리에 대한 검색 결과
            </p>
            <p class="text-h1" v-else>전체 강의</p>
            <span>({{ totalElements }})</span>
        </div>

        <!-- 태그 선택 영역 -->
        <div class="flex flex-col w-full gap-6 mb-4">
            <div class="flex-1">
                <div class="flex flex-wrap w-full max-w-[1440px] gap-2 py-4">
                    <button
                        v-for="tag in displayedTags"
                        :key="tag"
                        type="button"
                        :class="{
                            'tag-gray text-primary-500 py-1 px-2 rounded-full': !isTagSelected(tag),
                            'tag-primary py-1 px-2 text-white rounded-full': isTagSelected(tag),
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

        <!-- 정렬 드롭다운 (오른쪽 정렬) -->
        <div class="flex justify-end">
            <div class="relative">
                <!-- 정렬 버튼 -->
                <button
                    @click="toggleDropdown"
                    class="w-20 px-2 py-1 bg-white border rounded-md text-caption"
                >
                    {{ sortOptions.find((opt) => opt.value === selectedSort)?.label }}
                </button>

                <!-- 드롭다운 목록 -->
                <ul
                    v-if="isDropdownOpen"
                    class="absolute right-0 z-10 w-20 mt-1 overflow-hidden bg-white border rounded-md top-full"
                >
                    <li
                        v-for="option in sortOptions"
                        :key="option.value"
                        @click="selectSort(option.value)"
                        class="px-2 py-1 text-center cursor-pointer text-caption hover:bg-gray-100"
                    >
                        {{ option.label }}
                    </li>
                </ul>
            </div>
        </div>

        <!-- 강의 목록 -->
        <div v-if="lectures.length" class="pb-4 mt-4 overflow-x-auto">
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
import { useRoute, useRouter } from 'vue-router'
import LectureCard from '@/components/Lecture/LectureCard.vue'
import PaginationControl from '@/components/Common/PaginationControl.vue'
import { searchLectures } from '@/helpers/lecture.js'

const route = useRoute()
const router = useRouter()

const searchQuery = ref(route.query.q || '')
const selectedCategory = ref(route.query.category || '')
const selectedSort = ref(route.query.sort || 'relevance') // 기본값을 popular로 설정

const lectures = ref([])
const page = ref(1)
const size = ref(40)
const totalElements = ref(0)
const totalPages = ref(0)

// LectureSearchPage.vue (script setup 부분)

const isPresetTagsInitialized = ref(false)

const fetchLectures = async (updatePresetTags = true) => {
    try {
        const params = {
            category: selectedCategory.value || undefined,
            tag: selectedTags.value.length > 0 ? selectedTags.value.join(',') : undefined,
            sort: selectedSort.value, // 정렬 방식 반영
            query: searchQuery.value || undefined, // 검색어 유지
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

        // presetTags 업데이트: 플래그가 false일 때만 업데이트하고, 업데이트 후 true로 변경
        if (updatePresetTags && !isPresetTagsInitialized.value) {
            presetTags.value = Object.keys(response.data.aggregations.preset_tags || {})
            isPresetTagsInitialized.value = true
        }
    } catch (error) {
        console.error('강의 검색 API 호출 실패:', error)
    }
}

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

// 검색어나 카테고리가 없으면 allTags, 있으면 presetTags 사용
const displayedTags = computed(() => {
    return searchQuery.value || selectedCategory.value ? presetTags.value : allTags
})

// 태그 선택 여부 확인
function isTagSelected(tag) {
    return selectedTags.value.includes(tag)
}

// 정렬 변경 이벤트 핸들러
const onSortChange = () => {
    page.value = 1
    fetchLectures(false)
}

// 페이지 변경 이벤트
const onPageChange = (newPage) => {
    page.value = newPage
    fetchLectures(false)
}

// URL 변경 감지
watch(
    () => [route.query.q, route.query.category, route.query.sort],
    ([newQ, newCat, newSort]) => {
        searchQuery.value = newQ || ''
        selectedCategory.value = newCat || ''
        selectedSort.value = newSort || 'relevance'
        page.value = 1
        fetchLectures(true)
    }
)

// 컴포넌트 마운트 시 초기 검색 API 호출
onMounted(() => {
    fetchLectures()
})

const isDropdownOpen = ref(false)

const sortOptions = [
    { value: 'relevance', label: '정확도순' },
    { value: 'popular', label: '인기순' },
    { value: 'newest', label: '최신순' },
    { value: 'price_desc', label: '높은 가격순' },
    { value: 'price_asc', label: '낮은 가격순' },
]

const toggleDropdown = () => {
    isDropdownOpen.value = !isDropdownOpen.value
}

// const selectSort = (value) => {
//     selectedSort.value = value
//     isDropdownOpen.value = false
//     fetchLectures() // API 호출
// }

function toggleTag(tag) {
    if (isTagSelected(tag)) {
        selectedTags.value = selectedTags.value.filter((t) => t !== tag)
    } else {
        selectedTags.value.push(tag)
    }
    page.value = 1
    // 태그 변경 시 presetTags는 그대로 유지
    fetchLectures(false)
}

const selectSort = (value) => {
    selectedSort.value = value
    isDropdownOpen.value = false
    // 정렬 변경 시 presetTags 업데이트 없이 강의만 새로 불러옴
    fetchLectures(false)
}

// LectureSearchPage.vue
// LectureSearchPage.vue (쿼리 변화 감지 watcher)
watch(
    () => [route.query.q, route.query.category, route.query.sort, route.query.ts],
    ([newQ, newCat, newSort, newTs], [oldQ, oldCat, oldSort, oldTs]) => {
        // 새 검색이 시작되면 기존 태그 선택 상태 초기화
        selectedTags.value = []

        // 검색어가 있으면 검색어 우선, 없으면 카테고리 기준
        if (newQ && newQ.trim() !== '') {
            searchQuery.value = newQ
            selectedCategory.value = ''
        } else {
            searchQuery.value = ''
            selectedCategory.value = newCat || ''
        }

        selectedSort.value = newSort || 'relevance'
        page.value = 1

        // 새 검색이므로 presetTags 업데이트 플래그 리셋
        isPresetTagsInitialized.value = false

        fetchLectures(true)
    }
)
</script>
