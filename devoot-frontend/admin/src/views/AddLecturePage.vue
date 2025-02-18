<template>
    <div class="flex flex-col gap-5 p-10">
        <p class="text-5xl font-bold">강의 등록 요청 목록</p>
        <div class="flex flex-col justify-center gap-3">
            <div class="flex items-center justify-between">
                <div class="flex flex-col items-center justify-center">
                    <p class="text-2xl font-bold">강의 정보</p>
                </div>
                <div class="flex gap-2">
                    <button
                        class="px-3 py-1 bg-red-500 rounded cursor-pointer w-fit h-fit"
                        @click="goToAddRequestPage"
                    >
                        취소
                    </button>
                    <button class="px-3 py-1 bg-blue-500 rounded" @click="registerLecture">
                        등록
                    </button>
                </div>
            </div>
            <div class="w-full border-t border-gray-300"></div>

            <!-- 입력 폼 -->
            <div class="flex flex-col gap-2">
                <div class="flex flex-col gap-1">
                    <p>카테고리</p>
                    <input
                        v-model="category"
                        type="text"
                        class="w-full px-6 py-4 bg-white border border-gray-200 rounded-lg h-fit focus:outline-primary-200 placeholder:text-body placeholder:text-gray-300"
                    />
                </div>
                <div class="flex flex-col gap-1">
                    <p>제목</p>
                    <input
                        v-model="lectureTitle"
                        type="text"
                        class="w-full px-6 py-4 bg-white border border-gray-200 rounded-lg h-fit focus:outline-primary-200 placeholder:text-body placeholder:text-gray-300"
                    />
                </div>
                <div class="flex flex-col gap-1">
                    <p>강의자</p>
                    <input
                        v-model="lecturerName"
                        type="text"
                        class="w-full px-6 py-4 bg-white border border-gray-200 rounded-lg h-fit focus:outline-primary-200 placeholder:text-body placeholder:text-gray-300"
                    />
                </div>
                <div class="flex flex-col gap-1">
                    <p>이미지 링크</p>
                    <input
                        v-model="imageUrl"
                        type="text"
                        class="w-full px-6 py-4 bg-white border border-gray-200 rounded-lg h-fit focus:outline-primary-200 placeholder:text-body placeholder:text-gray-300"
                    />
                </div>
                <div class="flex flex-col gap-1">
                    <p>강의 링크</p>
                    <input
                        v-model="lectureUrl"
                        type="text"
                        class="w-full px-6 py-4 bg-white border border-gray-200 rounded-lg h-fit focus:outline-primary-200 placeholder:text-body placeholder:text-gray-300"
                    />
                </div>
                <div class="flex flex-col gap-1">
                    <p>정가</p>
                    <input
                        v-model="originalPrice"
                        type="text"
                        class="w-full px-6 py-4 bg-white border border-gray-200 rounded-lg h-fit focus:outline-primary-200 placeholder:text-body placeholder:text-gray-300"
                    />
                </div>
                <div class="flex flex-col gap-1">
                    <p>할인가</p>
                    <input
                        v-model="discountPrice"
                        type="text"
                        class="w-full px-6 py-4 bg-white border border-gray-200 rounded-lg h-fit focus:outline-primary-200 placeholder:text-body placeholder:text-gray-300"
                    />
                </div>

                <div class="flex flex-col gap-1">
                    <p>도메인 이름</p>
                    <input
                        v-model="domainName"
                        type="text"
                        class="w-full px-6 py-4 bg-white border border-gray-200 rounded-lg h-fit focus:outline-primary-200 placeholder:text-body placeholder:text-gray-300"
                    />
                </div>
                <div class="flex flex-col gap-1">
                    <p>태그</p>
                    <input
                        v-model="tags"
                        type="text"
                        class="w-full px-6 py-4 bg-white border border-gray-200 rounded-lg h-fit focus:outline-primary-200 placeholder:text-body placeholder:text-gray-300"
                    />
                </div>
                <div class="flex flex-col gap-1">
                    <p>커리큘럼</p>
                    <input
                        v-model="curriculum"
                        type="text"
                        class="w-full px-6 py-4 bg-white border border-gray-200 rounded-lg h-fit focus:outline-primary-200 placeholder:text-body placeholder:text-gray-300"
                    />
                </div>
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

console.log('📌 전송할 강의 데이터:', userStore.token) // ✅ 디버깅 로그
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
        console.log('성공!!')
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
