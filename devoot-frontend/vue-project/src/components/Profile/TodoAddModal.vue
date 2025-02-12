<template>
    <!-- 전체 컨테이너: 가운데 정렬, 배경색 적용, 너비 지정 -->
    <div
        class="flex flex-col items-center bg-white w-[1054px] h-fit gap-6 p-6 border border-gray-200 rounded-[20px]"
    >
        <!-- 상단 강의 추가 섹션 -->
        <div class="flex items-center justify-between w-full text-black text-h3">
            <p>어떤 강의를 추가하시겠어요?</p>
            <div class="flex items-center gap-2">
                <div
                    :class="[
                        isButtonClicked
                            ? 'cursor-pointer button-primary'
                            : 'cursor-pointer button-line',
                    ]"
                    @click="submitTodo"
                >
                    강의 추가
                </div>
                <Delete class="w-6 h-6 bg-white cursor-pointer" @click="$emit('close')" />
            </div>
        </div>
        <!-- 날짜 선택 및 강의 목록 컨테이너 -->
        <div class="flex flex-col gap-y-2.5 w-[58.25rem]">
            <div class="relative w-fit">
                <div
                    class="flex items-center border border-gray-200 w-full h-9 gap-x-2 px-[0.75rem] rounded cursor-pointer"
                    @click="toggleCalendarDropdown"
                >
                    <p class="text-body-bold">{{ formattedDate }}</p>
                    <NavigateDown class="w-5 h-5" />
                </div>

                <!-- 📌 캘린더 컨테이너를 `absolute`로 설정 -->

                <div
                    class="absolute left-0 z-50 top-full w-fit min-w-[450px] transform: scale(0.10)"
                >
                    <TodoAddModalCalendar
                        v-if="isCalendarDropdownOpen"
                        @select-date="selectDate"
                        @click-outside="closeCalendarDropdown"
                        class="bg-white border border-gray-200 rounded-lg shadow-lg"
                    />
                </div>
            </div>
            <!-- 강의 선택 및 선택된 강의 컨테이너 -->
            <div
                class="flex w-full h-fit rounded-[20px] overflow-hidden bg-gray-100 border border-gray-200"
            >
                <div class="w-[29.125rem] h-[240.8px] overflow-y-auto">
                    <!-- 나중에 :class에서 siteName대신 id로 바꾸기-->
                    <div
                        v-for="lectureData in todoStore.inprogressLectures"
                        :key="lectureData.id"
                        class="flex flex-col h-auto gap-1 px-4 py-3 border-b border-gray-200"
                        :class="{
                            'bg-primary-100': selectedLectureId === lectureData.id,
                            'bg-white': selectedLectureId !== lectureData.id,
                        }"
                        @click="selectLecture(lectureData)"
                    >
                        <p class="text-gray-300 text-caption-sm">
                            {{ lectureData.lecture.sourceName }}
                        </p>
                        <p
                            class="overflow-hidden text-black cursor-pointer text-body text-ellipsis whitespace-nowrap"
                            :title="lectureData.lecture.name"
                        >
                            {{ lectureData.lecture.name }}
                        </p>
                    </div>
                </div>
                <!-- 선택된 강의 목록 (오른쪽 영역) -->
                <div class="w-[29.125rem] h-[240.8px] overflow-y-auto">
                    <div
                        v-for="(subLecture, index) in filteredSubLectures"
                        :key="index"
                        class="flex flex-col gap-1 px-4 py-3 border-b border-l border-gray-200"
                        :class="{
                            'bg-primary-100': subLectureId === index,
                            'bg-white': subLectureId !== index,
                        }"
                        @click="selectsubLecture(subLecture, index)"
                    >
                        <p class="text-gray-300 text-caption-sm">{{ index + 1 }}강</p>
                        <p
                            class="overflow-hidden text-black cursor-pointer text-body text-ellipsis whitespace-nowrap selectLecture"
                        >
                            {{ subLecture.title }}
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import TodoAddModalCalendar from './TodoAddModalCalendar.vue'
import Delete from '@/assets/icons/delete.svg'
import NavigateDown from '@/assets/icons/navigate_down.svg'
import { ref, computed, watch, onMounted } from 'vue'
import { useTodoStore } from '@/stores/todo'
import { useUserStore } from '@/stores/user'
// import { getInprogressLecture } from '@/stores/todoStore';
const userStore = useUserStore()
const todoStore = useTodoStore() // Pinia 스토어 가져오기
// 강의 추가 버튼 상태 관리
const isButtonClicked = ref(false)

// 날짜 설정(오늘 날짜 디폴트 설정)
const today = new Date()
const formattedToday = today.toISOString().split('T')[0] // "YYYY-MM-DD" 형식으로 변환
const selectedDate = ref(formattedToday) // 기본 날짜를 오늘 날짜로 설정

// 선택한 날짜 업데이트
const selectDate = (date) => {
    if (!(date instanceof Date)) {
        date = new Date(date) // 문자열인 경우 Date 객체로 변환
    }

    const formattedDate = date.toISOString().split('T')[0] // "YYYY-MM-DD" 형식으로 변환

    // console.log('📌 변환된 날짜 (YYYY-MM-DD):', formattedDate)

    selectedDate.value = formattedDate // 변환된 날짜 저장
    isCalendarDropdownOpen.value = false // 캘린더 닫기
}

// 템플릿용 selectedDate 변환
const formattedDate = computed(() => {
    if (!selectedDate.value) return ''
    const [year, month, day] = selectedDate.value.split('-')
    return `${parseInt(month)}월 ${parseInt(day)}일`
})

// 캘린더 드롭다운 상태 관리
const isCalendarDropdownOpen = ref(false) // 드롭다운 상태

// 📌 드롭다운 열기/닫기 함수
const toggleCalendarDropdown = () => {
    isCalendarDropdownOpen.value = !isCalendarDropdownOpen.value
}

// 대강의 목록(mount될 때 저장)
const lectures = computed(() => todoStore.inprogressLectures)

// 선택한 대강의 ID, 이름, url
const selectedLectureId = ref(null)
const selectedLectureName = ref(null)
const selectedLectureURL = ref(null)

// 선택한 subLecture 목록 (배열로 관리)
const selectedSubLectures = ref(null)

// 선택한 subLecture ID, 이름
const subLectureId = ref(null)
const subLectureName = ref(null)

// 대강의 선택 / id와 이름 저장
const selectLecture = (lecture) => {
    // console.log(lecture)
    // selectedLecture.value = lecture
    selectedLectureId.value = lecture.id
    selectedLectureName.value = lecture.lecture.name
    selectedLectureURL.value = lecture.lecture.sourceName
    selectedSubLectures.value = null // 대강의 변경 시 subLecture 초기화
    subLectureId.value = null // ✅ 선택된 subLecture도 초기화!
    // console.log('filteredSubLectures', filteredSubLectures.value)
}

// 선택한 대강의에 해당하는 subLectures 가져오기 (computed 활용),
// selectedLectureId에 해당하는 subLectures를 찾아 반환
// selectedLectureId, lecture 데이터가 바뀌면 자동으로 그 강의의 subLectures를 업데이트
const filteredSubLectures = computed(() => {
    const selectedLecture = lectures.value.find((lecture) => lecture.id === selectedLectureId.value)

    if (!selectedLecture || !selectedLecture.lecture.curriculum) return []

    // curriculum 객체를 배열로 변환 후 subLectures 배열만 가져옴
    return Object.values(selectedLecture.lecture.curriculum).flatMap(
        (curriculumItem) => curriculumItem.subLectures
    )
})

// 📌 `filteredSubLectures`가 변경될 때 자동으로 `selectedSubLectures` 업데이트
watch(filteredSubLectures, (newSubLectures) => {
    // console.log('📌 `filteredSubLectures` 변경 감지:', newSubLectures)
    selectedSubLectures.value = newSubLectures
})

// subLecture 선택
const selectsubLecture = (subLecture, index) => {
    subLectureId.value = index // 클릭한 강의 인덱스 저장
    subLectureName.value = subLecture.title
    isButtonClicked.value = true
    // console.log('서브강의 이름', subLectureName.value)
    // console.log('선택된 sublecture 인덱스:', subLectureId.value)
}

// 📌 Todo 추가 요청
const submitTodo = async () => {
    const todoData = {
        lectureId: selectedLectureId.value,
        // lectureId: 14000,
        lectureName: selectedLectureName.value,
        subLectureName: subLectureName.value,
        sourceUrl: selectedLectureURL.value,
        date: selectedDate.value,
        finished: false,
    }

    // console.log('tododata', todoData)

    try {
        await todoStore.addTodo(todoData, userStore.token, userStore.userId) // 📌 Pinia Store의 addTodo 실행
        selectedLectureId.value = null
        subLectureId.value = null
        alert('할 일이 추가되었습니다!')
        isButtonClicked.value = !isButtonClicked.value
    } catch (error) {
        console.error('🚨 할 일 추가 실패:', error)
    }
}

watch(
    () => [userStore.token, userStore.userId],
    async ([newToken, newUserId]) => {
        if (newToken && newUserId) {
            console.log('✅ 토큰과 userId가 준비되었습니다.')
            await todoStore.getInprogressLecture(newToken, newUserId)
        }
    },
    { immediate: true }
)
// onMounted(() => {
//     todoStore.getInprogressLecture() // 컴포넌트가 로드될 때 JSON 데이터 가져오기
// })
</script>

<style></style>
