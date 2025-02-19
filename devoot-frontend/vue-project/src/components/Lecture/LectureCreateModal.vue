<template>
    <div class="fixed inset-0 z-40 flex items-center justify-center bg-black bg-opacity-50">
        <div class="flex flex-col px-9 py-8 gap-6 w-[41rem] rounded-[20px] bg-white">
            <div class="flex items-center justify-between">
                <p class="text-h2">강의 등록</p>
                <Delete class="w-6 h-6 cursor-pointer" @click="closeLectureCreateModal" />
            </div>

            <input
                v-model="lectureLink"
                type="text"
                class="w-full px-6 py-4 bg-gray-100 border rounded-lg h-fit focus:outline-primary-200 placeholder:text-body placeholder:text-gray-300"
                placeholder="원하는 강의의 링크를 입력해주세요."
            />

            <div class="flex justify-end">
                <button class="button-primary" @click="handleLectureSubmit">요청하기</button>
            </div>
        </div>
    </div>
</template>

<script setup>
import Delete from '@/assets/icons/delete.svg'
import { ref } from 'vue'
import { useUserStore } from '@/stores/user'
import { registerLecture } from '@/helpers/lecture'

const userStore = useUserStore()

//===========================
// 모달 상태 관리
//===========================
const emit = defineEmits(['close'])
const closeLectureCreateModal = () => {
    emit('close')
}

//===========================
// 강의 등록 요청 함수
//===========================
const lectureLink = ref('')
const handleLectureSubmit = async () => {
    if (!lectureLink.value.trim()) {
        alert('링크를 입력해주세요!')
        return
    }

    try {
        await registerLecture(lectureLink.value, userStore.token)
        alert('강의 등록 요청이 완료되었습니다.')
        lectureLink.value = '' // 입력값 초기화
        closeLectureCreateModal() // 모달 닫기
    } catch {
        alert('강의 등록 중 오류가 발생했습니다.')
    }
}
</script>

<style></style>
