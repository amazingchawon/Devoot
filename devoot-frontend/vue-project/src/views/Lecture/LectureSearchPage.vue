<!-- src/views/LectureSearchPage.vue -->
<template>
    <div class="lecture-search-page">
        <!-- 검색 결과 헤더 영역 -->
        <div class="mb-6">
            <!-- 검색어가 있을 경우 결과 제목 표시 -->
            <p class="text-h1" v-if="searchQuery">"{{ searchQuery }}" 에 대한 검색 결과</p>
            <!-- 카테고리(필터)가 있을 경우 결과 제목 표시 -->
            <p class="text-h1" v-else-if="selectedCategory">
                "{{ selectedCategory }}" 카테고리에 대한 검색 결과
            </p>
            <!-- 검색어와 카테고리 모두 없을 경우 전체 강의 목록 표시 -->
            <p class="text-h1" v-else>전체 강의</p>
            <span>({{ totalElements }})</span>
        </div>

        <!-- 태그 선택 영역 -->
        <div class="flex flex-col w-full gap-12 mb-6">
            <div class="flex-1">
                <!-- 태그 버튼들을 감싸는 영역 (최대 5개 선택 가능) -->
                <div
                    class="flex flex-wrap w-full gap-2 p-4 border"
                    :class="{ 'border-red-500': tagsError }"
                >
                    <!-- 선택된 태그가 앞쪽에 오도록 정렬된 태그 배열 -->
                    <button
                        v-for="tag in sortedTags"
                        :key="tag"
                        type="button"
                        :class="{
                            'tag-gray py-1 px-2 rounded': !isTagSelected(tag),
                            'tag-primary py-1 px-2 rounded': isTagSelected(tag),
                        }"
                        :title="
                            !isTagSelected(tag) && selectedTags.length >= 5
                                ? '최대 5개까지 선택 가능합니다.'
                                : ''
                        "
                        @click="toggleTag(tag)"
                    >
                        <div class="flex flex-row items-center gap-1">
                            {{ tag }}
                        </div>
                    </button>
                </div>
            </div>
        </div>

        <!-- 정렬 드롭다운 영역 -->
        <div class="relative flex justify-end mb-6">
            <!-- 드롭다운 토글 버튼: 현재 선택된 정렬 옵션(한글 텍스트) 표시 -->
            <div
                class="flex items-center justify-between w-32 px-3 text-black bg-white border border-gray-200 rounded-lg cursor-pointer h-9 gap-x-2 text-body"
                @click="toggleDropdown"
            >
                <p class="text-body-bold">
                    {{ selectedSort ? selectedSort : '정렬 기준' }}
                </p>
                <NavigateDown class="w-5 h-5" />
            </div>
            <!-- 드롭다운 옵션 리스트 -->
            <ul
                v-if="isDropdownOpen"
                class="absolute right-0 z-50 w-32 mt-2 bg-white border rounded-lg shadow-lg top-full"
            >
                <li
                    v-for="(option, index) in sortingOptions"
                    :key="index"
                    class="flex items-center justify-start w-full h-[2.25rem] px-4 text-body text-gray-700 hover:bg-gray-100 cursor-pointer"
                    @click="selectOption(option)"
                >
                    {{ option }}
                </li>
            </ul>
        </div>

        <!-- 강의 목록 영역 -->
        <!-- 외부 컨테이너에 pb-4와 overflow-x-auto 적용하여 화면 너비가 부족할 경우 수평 스크롤이 생깁니다. -->
        <div v-if="lectures.length" class="pb-4 overflow-x-auto">
            <!-- 내부 그리드 컨테이너:
                 - min-w-max: 내부 콘텐츠의 전체 너비를 유지 (카드 4개 + gap)
                 - grid-cols-4: 1행에 4개씩 배치
                 - gap-x-6: 좌우 간격 24px
                 - gap-y-4: 상하 간격 16px
            -->
            <div class="grid grid-cols-4 min-w-max gap-x-6 gap-y-4">
                <LectureCard
                    v-for="(lecture, index) in lectures"
                    :key="lecture.id || index"
                    :id="lecture.id || index"
                    :name="lecture.name"
                    :lecturer="lecture.lecturer"
                    :platform="lecture.platform"
                    :imageUrl="lecture.imageUrl"
                    :tags="lecture.tags"
                    :currentPrice="lecture.currentPrice"
                    :originalPrice="lecture.originalPrice"
                    :rating="lecture.rating"
                    :reviewCount="lecture.reviewCount"
                    :isBookmarked="lecture.isBookmarked"
                />
            </div>
        </div>
        <p v-else>검색 결과가 없습니다.</p>

        <!-- 페이지네이션 영역 -->
        <div v-if="totalElements > 0" class="mt-6">
            <!-- PaginationControl 컴포넌트에 currentPage와 totalPages 전달 -->
            <PaginationControl
                :currentPage="page"
                :totalPages="totalPages"
                @page-changed="onPageChange"
            />
        </div>

        <!-- 강의 등록 요청 영역 -->
        <div class="flex flex-row items-center justify-end gap-4 mt-6">
            <p class="text-gray-300 text-caption">등록하고 싶은 강의가 있으신가요?</p>
            <button class="flex flex-row gap-2 button-gray" @click="openLectureCreateModal">
                <p class="text-gray-500 text-caption">등록하기</p>
            </button>
        </div>
        <LectureCreateModal v-if="isLectureCreateModalOpen" @close="closeLectureCreateModal" />
    </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRoute } from 'vue-router'
import NavigateDown from '@/assets/icons/navigate_down.svg'
import LectureCard from '@/components/Lecture/LectureCard.vue'
import LectureCreateModal from '@/components/Lecture/LectureCreateModal.vue'
import PaginationControl from '@/components/Common/PaginationControl.vue'
import { searchLectures } from '@/helpers/lecture.js'

/**
 * URL 쿼리에서 초기 검색어, 카테고리 및 정렬 옵션을 받아옵니다.
 * - 검색어는 q 파라미터, 카테고리는 category 파라미터, 정렬은 sort 파라미터로 받습니다.
 */
const route = useRoute()
const searchQuery = ref(route.query.q || '')
const selectedCategory = ref(route.query.category || '')
// selectedSort: 드롭다운에서 선택된 정렬 옵션 (한글 표기)
const selectedSort = ref(route.query.sort ? mapSortToKorean(route.query.sort) : '최신순')

// 강의 데이터 배열 (API 응답으로 채워짐)
const lectures = ref([])

// 페이지네이션 관련 상태
const page = ref(1)
const size = ref(20)
const totalElements = ref(0)
const totalPages = ref(0)

// 태그 관련 상태 변수
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
const selectedTags = ref([]) // 사용자가 선택한 태그 배열
const tagsError = ref(false) // 선택 태그가 5개 초과할 때 에러 처리

// 정렬 옵션 목록 (한글 텍스트)
const sortingOptions = ['최신순', '인기순', '낮은 가격순', '높은 가격순']

// 정렬 옵션 한글과 API 파라미터 값 간의 매핑
const sortMapping = {
    최신순: 'newest',
    인기순: 'popular',
    '낮은 가격순': 'price_asc',
    '높은 가격순': 'price_desc',
}

// 역매핑: API 정렬 값 → 한글 텍스트 (초기 URL 파라미터 반영용)
function mapSortToKorean(apiSort) {
    for (const [kr, apiVal] of Object.entries(sortMapping)) {
        if (apiVal === apiSort) return kr
    }
    return '최신순'
}

// 드롭다운 상태
const isDropdownOpen = ref(false)

// API 호출: 강의 검색
const fetchLectures = async () => {
    try {
        // API 요청에 사용할 쿼리 파라미터 구성
        const params = {
            // 카테고리 필터 (값이 있을 경우만 전송)
            category: selectedCategory.value || undefined,
            // 태그 필터: 선택된 태그가 있으면 콤마로 구분된 문자열 전송
            tag: selectedTags.value.length > 0 ? selectedTags.value.join(',') : undefined,
            // 정렬: 드롭다운 선택값을 매핑하여 API에 전달 (기본값 'newest')
            sort: sortMapping[selectedSort.value] || 'newest',
            // 검색어
            query: searchQuery.value || undefined,
            page: page.value,
            size: size.value,
        }
        // 호출되는 API와 파라미터는 axios 요청 인터셉터에서 콘솔에 출력됩니다.
        const response = await searchLectures(params)
        // 응답 예시의 totalElements, totalPages 및 content 배열 처리
        totalElements.value = response.data.totalElements
        totalPages.value = response.data.totalPages
        lectures.value = response.data.content.map((item, index) => ({
            id: item.id || index, // id가 없으면 index 사용
            name: item.name,
            lecturer: item.lecturer,
            platform: item.sourceName || '인프런',
            imageUrl: item.imageUrl,
            // API의 tags는 문자열이므로 콤마로 분리하여 배열로 변환
            tags: item.tags ? item.tags.split(',').map((tag) => tag.trim()) : [],
            currentPrice: Number(item.currentPrice),
            originalPrice: Number(item.originPrice),
            rating: Number(item.rating),
            reviewCount: item.reviewCnt,
            isBookmarked: false,
        }))
    } catch (error) {
        console.error('강의 검색 API 호출 실패:', error)
    }
}

// 페이지 변경 이벤트 핸들러
const onPageChange = (newPage) => {
    page.value = newPage
    fetchLectures()
}

// 태그 선택/취소 토글: 최대 5개 선택 가능하도록 함
function toggleTag(tag) {
    if (isTagSelected(tag)) {
        selectedTags.value = selectedTags.value.filter((t) => t !== tag)
    } else {
        if (selectedTags.value.length < 5) {
            selectedTags.value.push(tag)
            tagsError.value = false
        } else {
            tagsError.value = true
        }
    }
    // 태그 변경 시 재검색 및 페이지 1로 초기화
    page.value = 1
    fetchLectures()
}

// 체크: 해당 태그가 선택되었는지
function isTagSelected(tag) {
    return selectedTags.value.includes(tag)
}

// 정렬 옵션: 선택된 태그는 앞쪽에 정렬
const sortedTags = computed(() => {
    return [...selectedTags.value, ...allTags.filter((tag) => !selectedTags.value.includes(tag))]
})

// 드롭다운 토글 함수
const toggleDropdown = () => {
    isDropdownOpen.value = !isDropdownOpen.value
}

// 정렬 옵션 선택 함수
const selectOption = (option) => {
    selectedSort.value = option
    isDropdownOpen.value = false
    // 정렬 옵션 변경 시 페이지 1로 초기화 후 재검색
    page.value = 1
    fetchLectures()
}

// 강의 등록 모달 관련 상태 및 함수
const isLectureCreateModalOpen = ref(false)
const openLectureCreateModal = () => {
    isLectureCreateModalOpen.value = true
}
const closeLectureCreateModal = () => {
    isLectureCreateModalOpen.value = false
}

// URL 쿼리(라우트) 변화 감지: 검색어, 카테고리, 정렬 옵션 변경 시 재검색 및 페이지 초기화
watch(
    () => [route.query.q, route.query.category, route.query.sort],
    ([newQ, newCat, newSort]) => {
        searchQuery.value = newQ || ''
        selectedCategory.value = newCat || ''
        // newSort은 API 정렬 값 → 한글로 변환 후 저장
        selectedSort.value = newSort ? mapSortToKorean(newSort) : '최신순'
        page.value = 1
        fetchLectures()
    }
)

// 컴포넌트 마운트 시 초기 검색 API 호출
onMounted(() => {
    fetchLectures()
})
</script>

<style scoped>
.lecture-search-page {
    /* 페이지 여백 등 필요에 따라 추가 */
}
</style>
