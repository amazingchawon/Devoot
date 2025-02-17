<template>
    <div class="lecture-search-page">
        <div class="flex flex-col">
            <!-- 검색결과 표시 -->
            <div>
                <p class="mb-6 text-h1" v-if="searchQuery">{{ searchQuery }}에 대한 검색 결과</p>
                <p class="mb-6 text-h1" v-if="selectedCategory">
                    "{{ selectedCategory }}" 카테고리에 대한 검색 결과
                </p>
            </div>

            <!-- 태그들 -->
            <div class="flex flex-col w-full gap-12">
                <div class="flex-1">
                    <div
                        class="flex flex-wrap w-full gap-2 p-4"
                        :class="{ 'border-red-500': tagsError }"
                    >
                        <!-- 선택된 태그 먼저 표시 -->
                        <button
                            v-for="tag in sortedTags"
                            :key="tag"
                            :class="{
                                'tag-gray': !isTagSelected(tag),
                                'tag-primary': isTagSelected(tag),
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

            <!-- 강의 정렬 드롭다운 -->
            <!-- 드롭다운 감싸는 컨테이너 -->
            <div class="relative flex justify-end">
                <!-- 버튼 (드롭다운 토글) -->
                <div
                    class="flex items-center border border-gray-200 h-9 gap-x-2 px-[0.75rem] rounded cursor-pointer w-fit"
                    @click="toggleDropdown"
                >
                    <p class="text-body-bold">{{ selectedCategory || '정렬 기준' }}</p>
                    <NavigateDown class="w-5 h-5" />
                </div>

                <!-- 드롭다운 리스트 -->
                <ul
                    v-if="isDropdownOpen"
                    class="absolute right-0 top-full mt-2 bg-white border rounded shadow-lg w-[10.75rem] z-50"
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
        </div>

        <div v-if="filteredLectures.length" class="lecture-list">
            <LectureCard
                v-for="lecture in filteredLectures"
                :key="lecture.id"
                :id="lecture.id"
                :name="lecture.name"
                :lecturer="lecture.lecturer"
                :platform="'인프런'"
                :imageUrl="lecture.imageUrl"
                :tags="lecture.tags"
                :currentPrice="lecture.currentPrice"
                :originalPrice="lecture.originalPrice"
                :rating="lecture.rating"
                :reviewCount="lecture.reviewCount"
                :isBookmarkedProp="lecture.isBookmarked"
            />
        </div>

        <p v-else>검색 결과가 없습니다.</p>

        <!-- 강의 등록 요청 -->
        <div class="flex flex-row items-center justify-end gap-4">
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

// 상태 변수
const route = useRoute()
const searchQuery = ref(route.query.q || '')
const selectedCategory = ref(route.query.option || '')
const lectures = ref([])
const filteredLectures = ref([])

// JSON 데이터 Fetch
const fetchLectures = async () => {
    try {
        const response = await fetch('/lecturecard_dummy_data.json')
        if (!response.ok) throw new Error('데이터를 불러오는데 실패했습니다.')
        const data = await response.json()
        lectures.value = data
        filterLectures() // 초기 필터링
    } catch (error) {
        console.error(error.message)
    }
}

// 검색어 및 카테고리에 따라 강의를 필터링
const filterLectures = () => {
    filteredLectures.value = lectures.value.filter((lecture) => {
        const matchesQuery =
            !searchQuery.value.trim() ||
            lecture.name.includes(searchQuery.value) ||
            lecture.tags.some((tag) => tag.includes(searchQuery.value))
        const matchesCategory = !selectedCategory.value || lecture.option === selectedCategory.value
        return matchesQuery && matchesCategory
    })
}

// 검색어 및 카테고리 변경 감지
watch(
    () => [route.query.q, route.query.option],
    ([newQuery, newCategory]) => {
        searchQuery.value = newQuery || ''
        selectedCategory.value = newCategory || ''
        filterLectures()
    }
)

// 컴포넌트 마운트 시 데이터 로드
onMounted(() => {
    fetchLectures()
})

//===========================
// 태그 데이터
//===========================
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
const selectedTags = ref([]) // 선택된 태그를 담는 배열

// 태그 선택/취소 토글
function toggleTag(tag) {
    if (isTagSelected(tag)) {
        // 선택된 태그를 클릭하면 선택 취소
        selectedTags.value = selectedTags.value.filter((t) => t !== tag)
    } else if (selectedTags.value.length < 5) {
        // 선택되지 않은 태그 추가
        selectedTags.value.push(tag)
        // tagsError.value = false
    }
}

// 태그가 선택되었는지 확인
function isTagSelected(tag) {
    return selectedTags.value.includes(tag)
}

// 선택된 태그를 앞에 정렬
const sortedTags = computed(() => {
    return [
        ...selectedTags.value, // 선택된 태그
        ...allTags.filter((tag) => !selectedTags.value.includes(tag)), // 선택되지 않은 태그
    ]
})

//===========================
// 강의 정렬 드롭다운
//===========================

// 카테고리 목록 정의
const sortingOptions = ['최신순', '인기순', '낮은 가격순', '높은 가격순']
// 드롭다운 상태 관리
const isDropdownOpen = ref(false)
// const selectedCategory = ref("");

// 드롭다운 토글 함수
const toggleDropdown = () => {
    isDropdownOpen.value = !isDropdownOpen.value
}

// 선택한 정렬 옵션 업데이트 후 드롭다운 닫기
const selectOption = (option) => {
    selectedCategory.value = option
    isDropdownOpen.value = false // 드롭다운 닫기
}

// 바깥 영역 클릭 시 드롭다운 닫기 (옵션)
document.addEventListener('click', (event) => {
    if (!event.target.closest('.relative')) {
        isDropdownOpen.value = false
    }
})

//===========================
// 강의 등록 요청
//===========================

const isLectureCreateModalOpen = ref(false)

// 모달 열기 함수
const openLectureCreateModal = () => {
    isLectureCreateModalOpen.value = true
}

// 모달 닫기 함수
const closeLectureCreateModal = () => {
    isLectureCreateModalOpen.value = false
}
</script>

<style scoped>
.lecture-search-page {
    padding: 20px;
}

.lecture-list {
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
}
</style>
