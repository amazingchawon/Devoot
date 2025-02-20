<template>
    <div class="flex flex-col gap-4">
        <div class="content-center h-20 text-h3">강의 등록 요청</div>

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
                    <input
                        v-model="curriculumText"
                        type="text"
                        class="w-full px-6 py-3 placeholder-gray-300 bg-white border border-gray-200 rounded-lg h-fit focus:outline-primary-200"
                        placeholder='{"section1": "내용1", "section2": "내용2"}'
                        @input="updateCurriculum"
                    />
                </div>
            </div>

            <div class="flex justify-end gap-2">
                <button
                    class="inline-flex items-center justify-center px-3 py-1 text-black bg-gray-100 border border-gray-200 rounded text-body"
                    @click="goToAddRequestPage"
                >
                    취소
                </button>
                <button
                    class="inline-flex items-center justify-center px-3 py-1 text-white rounded bg-primary-500 text-body"
                    @click="registerLecture"
                >
                    등록
                </button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import { addLecture } from '@/helpers/lecture'

const userStore = useUserStore() // Pinia 스토어 가져오기
const router = useRouter() // Vue Router 사용

const lectureTitle = ref('') // 제목
const lecturerName = ref('') // 강의자
const imageUrl = ref('') // 이미지 링크
const lectureUrl = ref('') // 강의 링크
const originalPrice = ref('') // 정가
const discountPrice = ref('') // 할인가
const category = ref('') // 마지막 업데이트 날짜
const domainName = ref('') // 도메인 이름
const tags = ref('') // 태그 (배열)
const curriculum = ref({}) // 커리큘럼 (배열)
const curriculumText = ref('')

const updateCurriculum = () => {
    try {
        curriculum.value = JSON.parse(curriculumText.value)
    } catch {
        console.warn('⚠️ 올바른 JSON 형식이 아닙니다.')
    }
}

// 강의 요청 등록
const registerLecture = async () => {
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

        console.log('📌 전송할 강의 데이터:', lectureData) // ✅ 디버깅 로그

        await addLecture(lectureData, userStore.token)
        // console.log('성공!!')
        router.push('/add/request')
    } catch (error) {
        console.error('🚨 강의 등록:', error)
    }
}

// request 페이지로 이동(취소 버튼 눌렀을 때)
const goToAddRequestPage = () => {
    router.push('/add/request') // 🔥 해당 경로로 이동
}
</script>

<style scoped></style>
