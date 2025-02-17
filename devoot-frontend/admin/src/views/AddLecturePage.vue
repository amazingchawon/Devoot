<!-- <template>
    <div class="flex flex-col gap-5 p-10">
        <p class="text-5xl font-bold">강의 등록 요청 목록</p>
        <div class="flex flex-col justify-center gap-3 bg-white">
            <div class="flex items-center justify-between bg-green-200">
                <div class="flex flex-col items-center justify-center">
                    <p class="text-2xl font-bold">강의 정보</p>
                </div>
                <div class="flex gap-2">
                    <button class="px-3 py-1 bg-red-500 rounded cursor-pointer w-fit h-fit">
                        취소
                    </button>
                    <button class="px-3 py-1 bg-blue-500 rounded" @click="registerLecture">
                        등록
                    </button>
                </div>
            </div>
            <div class="w-full border-t border-gray-300"></div>

            <div class="flex flex-col gap-2">
                <div class="flex flex-col gap-1">
                    <p>제목</p>
                    <input
                        v-model="lectureTitle"
                        type="text"
                        class="w-full px-6 py-4 bg-gray-100 border rounded-lg h-fit focus:outline-primary-200 placeholder:text-body placeholder:text-gray-300"
                    />
                </div>
                <div class="flex flex-col gap-1">
                    <p>강의자</p>
                    <input
                        v-model="lecturerName"
                        type="text"
                        class="w-full px-6 py-4 bg-gray-100 border rounded-lg h-fit focus:outline-primary-200 placeholder:text-body placeholder:text-gray-300"
                    />
                </div>
                <div class="flex flex-col gap-1">
                    <p>이미지 링크</p>
                    <input
                        v-model="imageUrl"
                        type="text"
                        class="w-full px-6 py-4 bg-gray-100 border rounded-lg h-fit focus:outline-primary-200 placeholder:text-body placeholder:text-gray-300"
                    />
                </div>
                <div class="flex flex-col gap-1">
                    <p>강의 링크</p>
                    <input
                        v-model="lectureUrl"
                        type="text"
                        class="w-full px-6 py-4 bg-gray-100 border rounded-lg h-fit focus:outline-primary-200 placeholder:text-body placeholder:text-gray-300"
                    />
                </div>
                <div class="flex flex-col gap-1">
                    <p>정가</p>
                    <input
                        v-model="originalPrice"
                        type="text"
                        class="w-full px-6 py-4 bg-gray-100 border rounded-lg h-fit focus:outline-primary-200 placeholder:text-body placeholder:text-gray-300"
                    />
                </div>
                <div class="flex flex-col gap-1">
                    <p>할인가</p>
                    <input
                        v-model="discountPrice"
                        type="text"
                        class="w-full px-6 py-4 bg-gray-100 border rounded-lg h-fit focus:outline-primary-200 placeholder:text-body placeholder:text-gray-300"
                    />
                </div>
                <div class="flex flex-col gap-1">
                    <p>마지막 업데이트 날짜</p>
                    <input
                        v-model="lastUpdatedDate"
                        type="text"
                        class="w-full px-6 py-4 bg-gray-100 border rounded-lg h-fit focus:outline-primary-200 placeholder:text-body placeholder:text-gray-300"
                    />
                </div>
                <div class="flex flex-col gap-1">
                    <p>도메인 이름</p>
                    <input
                        v-model="domainName"
                        type="text"
                        class="w-full px-6 py-4 bg-gray-100 border rounded-lg h-fit focus:outline-primary-200 placeholder:text-body placeholder:text-gray-300"
                    />
                </div>
                <div class="flex flex-col gap-1">
                    <p>태그</p>
                    <input
                        v-model="tags"
                        type="text"
                        class="w-full px-6 py-4 bg-gray-100 border rounded-lg h-fit focus:outline-primary-200 placeholder:text-body placeholder:text-gray-300"
                    />
                </div>
                <div class="flex flex-col gap-1">
                    <p>커리큘럼</p>
                    <input
                        v-model="curriculum"
                        type="text"
                        class="w-full px-6 py-4 bg-gray-100 border rounded-lg h-fit focus:outline-primary-200 placeholder:text-body placeholder:text-gray-300"
                    />
                </div>
            </div>
        </div>
    </div>
</template> -->

<script setup>
// import { ref } from 'vue'
// import { useUserStore } from '@/stores/user'
// import { addLecture } from '@/helpers/lecture'

// const userStore = useUserStore() // Pinia 스토어 가져오기

// const lectureTitle = ref('') // 제목
// const lecturerName = ref('') // 강의자
// const imageUrl = ref('') // 이미지 링크
// const lectureUrl = ref('') // 강의 링크
// const originalPrice = ref(null) // 정가
// const discountPrice = ref(null) // 할인가
// const lastUpdatedDate = ref('') // 마지막 업데이트 날짜
// const domainName = ref('') // 도메인 이름
// const tags = ref([]) // 태그 (배열)
// const curriculum = ref([]) // 커리큘럼 (배열)

// // 강의 요청 등록
// const registerLecture = async () => {
//     try {
//         await addLecture(userStore.token)
//     } catch (error) {
//         console.error('🚨 강의 등록:', error)
//     }
// }
</script>

<style scoped></style>
