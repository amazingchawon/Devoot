<template>
    <div class="border border-gray-200 rounded-[1rem] p-[1.5rem] flex flex-col gap-6 col-span-12">
        <!-- 상단 영역 -->
        <div class="flex items-center justify-between w-full">
            <!-- 왼쪽 텍스트 -->
            <p class="text-h3">내 발자국</p>
            <!-- 오른쪽 년도 선택 -->
            <div class="flex items-center gap-2 text-black">
                <NavigateLeft
                    class="w-[1.125rem] h-[1.125rem] cursor-pointer"
                    @click="navigateYear(-1)"
                />
                <span class="text-body">{{ year }}</span>
                <NavigateRight
                    class="w-[1.125rem] h-[1.125rem] cursor-pointer"
                    @click="navigateYear(1)"
                />
            </div>
        </div>

        <!-- 하단 잔디 영역 -->
        <div class="flex flex-1 w-full gap-2">
            <div class="inline-flex flex-col justify-between pt-4 text-gray-300 text-caption-sm">
                <span>sun</span>
                <span>wed</span>
                <span>sat</span>
            </div>
            <div class="flex flex-col flex-1 gap-2">
                <div class="text-gray-300 text-caption-sm">
                    <p class="flex justify-between text-center">
                        <span class="w-5">jan</span>
                        <span class="w-5">feb</span>
                        <span class="w-5">mar</span>
                        <span class="w-5">apr</span>
                        <span class="w-5">may</span>
                        <span class="w-5">jun</span>
                        <span class="w-5">jul</span>
                        <span class="w-5">aug</span>
                        <span class="w-5">sep</span>
                        <span class="w-5">oct</span>
                        <span class="w-5">nov</span>
                        <span class="w-5">dec</span>
                        <span class="w-5"></span>
                    </p>
                </div>

                <div class="grid gap-0 grid-cols-[repeat(53,1fr)]">
                    <div
                        v-for="(column, columnIndex) in calendarData"
                        :key="columnIndex"
                        class="flex flex-col"
                    >
                        <div
                            v-for="(day, rowIndex) in column"
                            :key="rowIndex"
                            class="contribution-wrapper"
                        >
                            <!-- 모든 데이터를 렌더링 -->
                            <div
                                :data-date="day.date"
                                :data-level="day.level"
                                :class="{
                                    'ContributionCalendar-day': !day.empty,
                                    'empty-cell': day.empty,
                                }"
                                tabindex="0"
                                role="gridcell"
                            >
                                <!-- 데이터가 있는 경우 FootPrint 렌더링 -->
                                <div class="relative group">
                                    <FootPrint
                                        v-if="day.date"
                                        :key="day.date"
                                        class="w-3.5 h-3.5 rotate-45"
                                        :class="`text-primary-${day.level}00`"
                                    />
                                    <div
                                        v-if="day.date"
                                        id="tooltip-default"
                                        role="tooltip"
                                        class="flex whitespace-nowrap flex-col items-center absolute left-1/2 bottom-full translate-x-[-50%] translate-y-[-10px] z-50 invisible group-hover:visible opacity-0 group-hover:opacity-100 transition-opacity duration-300 bg-gray-100 text-black text-caption-sm rounded-lg px-2 py-2 shadow-lg w-auto text-center space-y-1"
                                    >
                                        <span>{{ day.date }}</span>
                                        <span>완료: {{ day.cnt }}개</span>
                                        <div class="tooltip-arrow"></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import NavigateLeft from '@/assets/icons/navigate_left.svg'
import NavigateRight from '@/assets/icons/navigate_right.svg'
import FootPrint from '@/assets/icons/footprint.svg'
import { ref, computed, watch, onMounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useTodoStore } from '@/stores/todo'
import { getContributions, getLevel } from '@/helpers/todo'

const props = defineProps({
    userId: {
        type: String,
        required: true,
    },
})
const route = useRoute()
const userStore = useUserStore() // Pinia 스토어 가져오기
const todoStore = useTodoStore()
// const contributions = computed(() => todoStore.contributions);

// 0. 상태 변수 정의
const year = computed(() => todoStore.year) // ✅ store에서 year 사용

// 년도 조절 함수
const navigateYear = (offset) => {
    todoStore.navigateYear(offset) // ✅ 연도 변경 시 store에서 관리
}

// 1. 데이터와 상태를 정의
const contributions = computed(() => todoStore.contributions)

// 데이터 로드
const loadContributions = async () => {
    try {
        const response = await getContributions(year.value, userStore.token, route.params.id)
        const data = response.data.map((item) => ({
            ...item,
            level: getLevel(item.cnt),
        }))
        // console.log('🛠 변환된 data 확인:', data) // level이 추가되었는지 확인
        todoStore.updateContributions([...data])

        // DOM 업데이트가 완료된 후 스타일 재적용
        await nextTick(() => {
            document.querySelectorAll('.ContributionCalendar-day').forEach((el) => {
                const level = el.getAttribute('data-level')
                if (level) {
                    el.classList.remove('text-black')
                    el.classList.add(`text-primary-${level}00`)
                }
            })
        })
    } catch (error) {
        console.error('❌ 잔디 conrtribution 개수 불러오기 실패:', error)
    }
}

// 4. 특정 날짜의 주차(week index)를 계산하는 함수
const getWeekIndex = (date) => {
    const start = new Date(date.getFullYear(), 0, 1) // 해당 연도의 1월 1일
    const dayOffset = start.getDay() // 1월 1일의 요일 (일요일: 0, 월요일: 1, ...)
    const daysSinceStart = Math.floor((date - start) / (24 * 60 * 60 * 1000)) // 1월 1일부터 해당 날짜까지 경과한 일 수
    return Math.floor((daysSinceStart + dayOffset) / 7) // 전체 경과 일수와 요일을 기반으로 주차 계산
}

// 5. 캘린더 데이터를 계산하는 computed 속성
const calendarData = computed(() => {
    // 캘린더의 기본 구조 생성 (53주 x 7일)
    const columns = Array.from(
        { length: 53 },
        () => Array.from({ length: 7 }, () => ({ empty: false })) // 초기값은 모두 빈 칸으로 설정
    )

    // 1월 1일 및 12월 31일 계산
    const firstDate = new Date(`${year.value}-01-01`) // 선택된 연도의 1월 1일
    const lastDate = new Date(`${year.value}-12-31`) // 선택된 연도의 12월 31일
    const firstDayOfWeek = firstDate.getDay() // 1월 1일의 요일

    // 0주차 데이터 처리 (캘린더의 첫 번째 주)
    for (let i = 0; i < 7; i++) {
        if (i < firstDayOfWeek) {
            // 1월 1일 이전의 날짜는 빈 칸으로 유지
            columns[0][i] = { empty: true }
        } else {
            // 1월 1일부터의 날짜 데이터를 생성
            const dayOffset = i - firstDayOfWeek // 요일에 따른 날짜 차이 계산
            const date = new Date(firstDate) // 1월 1일의 복사본 생성
            date.setDate(firstDate.getDate() + dayOffset) // 날짜를 조정
        }
    }

    // 1월 1일부터 12월 31일까지 모든 날짜를 `empty: false`로 설정
    let currentDate = new Date(firstDate) // 현재 날짜를 1월 1일부터 시작
    while (currentDate <= lastDate) {
        // const dayOfWeek = currentDate.getDay()
        const weekIndex = getWeekIndex(currentDate)

        if (!columns[weekIndex]) {
            columns[weekIndex] = Array.from({ length: 7 }, () => ({ empty: true }))
        }

        // 다음 날짜로 이동
        currentDate.setDate(currentDate.getDate() + 1)
    }

    // contributions 데이터 반영
    todoStore.contributions.forEach((day) => {
        const date = new Date(day.date) // 기여도 데이터의 날짜
        const dayOfWeek = date.getDay() // 해당 날짜의 요일
        const weekIndex = getWeekIndex(date) // 해당 날짜의 주차 계산

        // 0주차는 이미 처리되었으므로, 이후 주차에 데이터를 추가
        const columnIndex = weekIndex === 0 ? 0 : weekIndex
        if (!columns[columnIndex]) {
            columns[columnIndex] = Array.from({ length: 7 }, () => ({ empty: true }))
        }
        columns[columnIndex][dayOfWeek] = { ...day, empty: false } // 해당 날짜 데이터를 추가
    })

    // 12월 31일 이후를 빈 칸으로 설정
    const lastWeekIndex = getWeekIndex(lastDate)
    const lastDayOfWeek = lastDate.getDay()
    for (let i = lastDayOfWeek + 1; i < 7; i++) {
        columns[lastWeekIndex][i] = { empty: true }
    }

    // console.log(columns) // 계산된 캘린더 데이터를 콘솔에 출력
    return columns // 최종적으로 계산된 캘린더 데이터를 반환
})
watch(
    () => [year.value, userStore.token, props.userId, contributions],
    async ([newYear, newToken, newUserId]) => {
        if (newYear && newToken && newUserId) {
            // console.log('✅ 모든 값이 준비되었습니다:', newYear, newToken, newUserId)
            // contributions.value = [] // 기존 데이터 초기화
            await loadContributions(year.value, userStore.token, route.params.id)
        }
    },
    { immediate: true }
)

// 6. `onMounted`로 데이터 로드
onMounted(() => {
    if (!year.value) {
        year.value = new Date().getFullYear() // ✅ `year.value`가 없으면 현재 연도로 초기화
    }
})
</script>

<style>
.ContributionCalendar-day {
    @apply flex justify-center items-center rounded-full bg-gray-100 w-5 h-5;
}

.empty-cell {
    @apply bg-transparent border-none w-5 h-5;
}

.tooltip-arrow {
    @apply absolute left-1/2 top-full translate-x-[-50%] translate-y-[-4px] w-0 h-0 border-t-[4px] border-l-[6px] border-r-[6px] border-t-gray-100 border-l-transparent border-r-transparent;
}
</style>
