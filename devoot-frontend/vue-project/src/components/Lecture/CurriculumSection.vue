<template>
    <div id="curriculum-section" class="w-full">
        <div v-for="(section, sectionIndex) in sections" id="arcodian" :key="sectionIndex">
            <!-- 강의 섹션 헤더 -->
            <div
                id="arcodian-header"
                class="flex flex-row items-center h-12 gap-3 px-3 bg-gray-100 cursor-pointer lg:px-6 text-h3"
                @click="toggleAccordion(sectionIndex)"
            >
                <NavigateUp
                    class="w-6 h-6 transition-transform duration-300 cursor-pointer"
                    :class="{ 'rotate-180': openSections.includes(sectionIndex) }"
                />
                <p>{{ section.number }}</p>
                <p class="min-w-0 truncate">{{ section.title }}</p>
                <div class="flex-1"></div>
                <p class="min-w-0 truncate text-body">총 {{ section.totalLectures }}강</p>
            </div>

            <!-- 강의 리스트 -->
            <ul
                ref="accordionContents"
                class="overflow-hidden transition-all duration-300 ease-in-out"
                :style="{
                    maxHeight: openSections.includes(sectionIndex)
                        ? accordionHeights[sectionIndex] + 'px'
                        : '0px',
                    opacity: openSections.includes(sectionIndex) ? '1' : '0',
                }"
            >
                <li
                    v-for="(subLecture, lectureIndex) in section.lectures"
                    :key="lectureIndex"
                    class="flex flex-row items-center gap-3 px-12 py-3 transition-all border-b border-gray-200 lg:px-24 text-body"
                >
                    <p>{{ subLecture.number }}강</p>
                    <p>{{ subLecture.title }}</p>
                    <div class="flex-1"></div>
                    <p>{{ subLecture.time }}</p>
                </li>
            </ul>
        </div>
    </div>
</template>

<script setup>
import { ref, nextTick, computed } from 'vue'
import NavigateUp from '@/assets/icons/navigate_up.svg'

const props = defineProps({
    lecture: {
        type: Object,
        required: true,
    },
})

// 여러 개 열릴 수 있도록 배열 사용
const openSections = ref([]) // 어떤 섹션이 열려 있는지 추적하는 배열열

// 각 아코디언 콘텐츠의 높이를 저장할 배열
const accordionHeights = ref([])

// DOM 요소 참조
const accordionContents = ref([])

// 아코디언 토글 함수
const toggleAccordion = async (index) => {
    if (openSections.value.includes(index)) {
        // 이미 열려 있다면 닫기
        openSections.value = openSections.value.filter((i) => i !== index)
    } else {
        // 새로 열기
        openSections.value.push(index)

        // 높이 측정을 위해 nextTick 사용 (DOM 업데이트 후 실행)
        await nextTick()
        const el = accordionContents.value[index]
        if (el) {
            accordionHeights.value[index] = el.scrollHeight
        }
    }
}

// API에서 받은 curriculum을 sections 형식으로 변환
const sections = computed(() => {
    if (!props.lecture.curriculum) return []

    try {
        const curriculum = JSON.parse(props.lecture.curriculum.replace(/'/g, '"')) // JSON 변환
        return Object.keys(curriculum).map((key, curriculumindex) => ({
            number: key,
            title: curriculum[key].majorTitle,
            totalLectures: curriculum[key].subLectures.length,
            lectures: curriculum[key].subLectures.map((lecture, lectureIndex) => ({
                number: lectureIndex + 1,
                title: lecture.title,
                time: lecture.time,
            })),
        }))
    } catch (error) {
        console.error('❌ curriculum 파싱 오류:', error)
        return []
    }
})
</script>

<style scoped></style>
