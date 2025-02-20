<template>
    <div class="flex flex-col gap-4">
        <div class="content-center h-20 text-h3">강의 수정</div>

        <div class="flex flex-col gap-4 p-6 bg-white border-gray-200 rounded-2xl">
            <div class="text-h3">강의 정보 입력</div>

            <!-- 입력 폼 -->
            <div class="grid grid-cols-1 gap-4">
                <div class="flex flex-col gap-1">
                    <label class="text-gray-600 text-body">카테고리</label>
                    <input
                        v-model="category"
                        type="text"
                        class="w-full px-6 py-3 placeholder-gray-300 bg-white border border-gray-200 rounded-lg h-fit focus:outline-primary-200"
                        placeholder="카테고리를 입력하세요."
                    />
                </div>
                <div class="flex flex-col gap-1">
                    <label class="text-gray-600 text-body">제목</label>
                    <input
                        v-model="lectureTitle"
                        type="text"
                        class="w-full px-6 py-3 placeholder-gray-300 bg-white border border-gray-200 rounded-lg h-fit focus:outline-primary-200"
                        placeholder="강의 제목을 입력하세요."
                    />
                </div>
                <div class="flex flex-col gap-1">
                    <label class="text-gray-600 text-body">강의자</label>
                    <input
                        v-model="lecturerName"
                        type="text"
                        class="w-full px-6 py-3 placeholder-gray-300 bg-white border border-gray-200 rounded-lg h-fit focus:outline-primary-200"
                        placeholder="강의자를 입력하세요."
                    />
                </div>
                <div class="flex flex-col gap-1">
                    <label class="text-gray-600 text-body">이미지 링크</label>
                    <input
                        v-model="imageUrl"
                        type="text"
                        class="w-full px-6 py-3 placeholder-gray-300 bg-white border border-gray-200 rounded-lg h-fit focus:outline-primary-200"
                        placeholder="이미지 링크를 입력하세요."
                    />
                </div>
                <div class="flex flex-col gap-1">
                    <label class="text-gray-600 text-body">강의 링크</label>
                    <input
                        v-model="lectureUrl"
                        type="text"
                        class="w-full px-6 py-3 placeholder-gray-300 bg-white border border-gray-200 rounded-lg h-fit focus:outline-primary-200"
                        placeholder="강의 링크를 입력하세요."
                    />
                </div>
                <div class="grid grid-cols-2 gap-4">
                    <div class="flex flex-col gap-1">
                        <label class="text-gray-600 text-body">정가</label>
                        <input
                            v-model="originalPrice"
                            type="text"
                            class="w-full px-6 py-3 placeholder-gray-300 bg-white border border-gray-200 rounded-lg h-fit focus:outline-primary-200"
                            placeholder="정가를 입력하세요."
                        />
                    </div>
                    <div class="flex flex-col gap-1">
                        <label class="text-gray-600 text-body">할인가</label>
                        <input
                            v-model="discountPrice"
                            type="text"
                            class="w-full px-6 py-3 placeholder-gray-300 bg-white border border-gray-200 rounded-lg h-fit focus:outline-primary-200"
                            placeholder="할인가를 입력하세요."
                        />
                    </div>
                </div>
                <div class="flex flex-col gap-1">
                    <label class="text-gray-600 text-body">도메인 이름</label>
                    <input
                        v-model="domainName"
                        type="text"
                        class="w-full px-6 py-3 placeholder-gray-300 bg-white border border-gray-200 rounded-lg h-fit focus:outline-primary-200"
                        placeholder="도메인 이름을 입력하세요."
                    />
                </div>
                <div class="flex flex-col gap-1">
                    <label class="text-gray-600 text-body">태그</label>
                    <input
                        v-model="tags"
                        type="text"
                        class="w-full px-6 py-3 placeholder-gray-300 bg-white border border-gray-200 rounded-lg h-fit focus:outline-primary-200"
                        placeholder="태그를 입력하세요."
                    />
                </div>
                <div class="flex flex-col gap-1">
                    <label class="text-gray-600 text-body">커리큘럼</label>
                    <textarea
                        v-model="curriculumString"
                        class="w-full px-6 py-3 placeholder-gray-300 bg-white border border-gray-200 rounded-lg h-fit focus:outline-primary-200"
                        placeholder='{"1": {"majorTitle": "섹션 1", "subLectures": [{"title": "강의 제목", "time": "00:00"}]}}'
                        rows="6"
                    />
                </div>
            </div>

            <div class="flex justify-end gap-2">
                <button
                    class="inline-flex items-center justify-center px-3 py-1 text-white bg-red-500 rounded cursor-pointer w-fit h-fit"
                    @click="removeEditRequest"
                >
                    삭제
                </button>
                <button
                    class="inline-flex items-center justify-center px-3 py-1 text-white rounded bg-primary-500 text-body"
                    @click="updateLecture"
                >
                    수정
                </button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, watchEffect } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRoute } from 'vue-router'
import { useRouter } from 'vue-router'
import { getLecture, deleteLecture, editLecture } from '@/helpers/lecture'

const userStore = useUserStore() // Pinia 스토어 가져오기
const route = useRoute()
const router = useRouter() // Vue Router 사용
const requestId = ref(route.query.requestId)
console.log('requestID', requestId.value)

// 내용 불러오기
const lectureData = ref([])
const lectureId = ref('')
const loadLectureData = async () => {
    // console.log(userStore.token)
    try {
        if (!requestId.value || !userStore.token) {
            console.error('🚨 requestId 또는 토큰이 없습니다.')
            return
        }
        const response = await getLecture(requestId.value, userStore.token)
        lectureData.value = response.data
        console.log(lectureData.value)
        lectureId.value = lectureData.value.lecture.id
        lectureTitle.value = lectureData.value.lecture.name // 제목
        lecturerName.value = lectureData.value.lecture.lecturer // 강의자
        imageUrl.value = lectureData.value.lecture.imgUrl // 이미지 링크
        lectureUrl.value = lectureData.value.lecture.sourceUrl // 강의 링크
        originalPrice.value = lectureData.value.lecture.originPrice // 정가
        discountPrice.value = lectureData.value.lecture.currentPrice // 할인가
        category.value = lectureData.value.lecture.category // 마지막 업데이트 날짜
        domainName.value = lectureData.value.lecture.sourceName // 도메인 이름
        tags.value = lectureData.value.lecture.tags // 태그 (배열)
        curriculum.value = lectureData.value.lecture.curriculum // ✅ JSON 변환
    } catch (error) {
        console.error('🚨 강의 불러오기 요청 실패:', error)
    }
}

onMounted(loadLectureData)

const lectureTitle = ref('')
const lecturerName = ref('')
const imageUrl = ref('')
const lectureUrl = ref('')
const originalPrice = ref('')
const discountPrice = ref('')
const category = ref('')
const domainName = ref('')
const tags = ref('')
const curriculum = ref({})

// 강의 수정
const updateLecture = async () => {
    try {
        const lectureData = {
            category: category.value, // 필요에 따라 선택적으로 설정
            tags: tags.value, // ✅ 태그 배열을 문자열로 변환
            name: lectureTitle.value, // ✅ 제목
            lecturer: lecturerName.value, // ✅ 강의자
            currentPrice: discountPrice.value ? Number(discountPrice.value) : null,
            originPrice: originalPrice.value ? Number(originalPrice.value) : null, // ✅ 숫자로 변환
            sourceName: domainName.value, // ✅ 도메인 이름
            sourceUrl: lectureUrl.value, // ✅ 강의 링크
            imgUrl: imageUrl.value, // ✅ 이미지 링크
            curriculum: curriculum.value, // ✅ 커리큘럼 변환 함수 사용
        }

        console.log('📌 수정된 데이터:', lectureData) // ✅ 디버깅 로그

        await editLecture(lectureId.value, lectureData, userStore.token)
        alert('강의가 수정되었습니다.')
        router.push('/add/request')
    } catch (error) {
        console.error('🚨 강의 등록:', error)
    }
}

// 강의 삭제
const removeEditRequest = async () => {
    try {
        if (!window.confirm('강의 수정 요청을 삭제하시겠습니까?')) {
            return // ❌ 사용자가 취소하면 삭제 중단
        }

        await deleteLecture(requestId.value, userStore.token)
        alert('삭제가 완료되었습니다.')
        router.push('/edit/request')
    } catch (error) {
        console.error('🚨 강의 삭제 요청 실패:', error)
    }
}

watchEffect(() => {
    if (requestId.value) {
        console.log('📌 requestId 변경 감지:', requestId.value)
        loadLectureData()
    }
})

import { computed } from 'vue'

const curriculumString = computed({
    get: () => JSON.stringify(curriculum.value, null, 2), // ✅ JSON 문자열로 변환
    set: (value) => {
        try {
            curriculum.value = JSON.parse(value) // ✅ 문자열을 다시 객체로 변환
        } catch (e) {
            console.error('🚨 JSON 파싱 오류:', e)
        }
    },
})
</script>

<style scoped></style>
