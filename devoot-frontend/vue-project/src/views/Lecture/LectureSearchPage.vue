<template>
    <div class="lecture-search-page">
        <p class="mb-6 text-h1" v-if="searchQuery">{{ searchQuery }}에 대한 검색 결과</p>
        <p class="mb-6 text-h1" v-if="selectedCategory">
            "{{ selectedCategory }}" 카테고리에 대한 검색 결과
        </p>

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
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import LectureCard from '@/components/Lecture/LectureCard.vue'
import LectureCreateModal from '@/components/Lecture/LectureCreateModal.vue'

// 상태 변수
const route = useRoute()
const searchQuery = ref(route.query.q || '')
const selectedCategory = ref(route.query.category || '')
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
        const matchesCategory =
            !selectedCategory.value || lecture.category === selectedCategory.value
        return matchesQuery && matchesCategory
    })
}

// 검색어 및 카테고리 변경 감지
watch(
    () => [route.query.q, route.query.category],
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
// 강의 등록
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
