<template>
    <div class="flex items-center justify-center min-h-screen">
        <div class="flex flex-col gap-4 p-16 bg-white border border-gray-200 rounded-2xl">
            <!-- 로고 -->
            <div id="logo" class="flex flex-row items-center gap-3">
                <Logo class="w-8 h-8 text-primary-500" />
                <div class="text-h1">개발바닥</div>
                <p
                    class="px-2 py-1 bg-gray-100 rounded-full whitespace-nowrap w-fit h-fit text-primary-500 text-caption-sm"
                >
                    관리자
                </p>
            </div>

            <!-- 멘트 -->
            <div class="text-gray-300 text-h2">
                개발의 모든 것, <br />
                개발바닥에서 시작해보세요!
            </div>

            <!-- 소셜 로그인 버튼 -->
            <div class="flex flex-row gap-4">
                <button
                    class="flex flex-row items-center justify-center w-full h-12 gap-4 text-black border border-gray-200 rounded-lg hover:bg-gray-200"
                    @click="handleGoogleLogin"
                >
                    <Google class="w-6 h-6" />
                    <span class="text-body-bold">Google로 로그인</span>
                </button>
            </div>
            <div class="flex flex-row gap-4">
                <button
                    class="flex flex-row items-center justify-center w-full h-12 gap-4 text-black border border-gray-200 rounded-lg hover:bg-gray-200"
                    @click="handleGithubLogin"
                >
                    <Github class="w-6 h-6" />
                    <span class="text-body-bold">GitHub로 로그인</span>
                </button>
            </div>
        </div>
    </div>
</template>

<script setup>
import Logo from '@/assets/icons/logo.svg'
import Google from '@/assets/icons/google.svg'
import Github from '@/assets/icons/github.svg'

import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()

const handleGoogleLogin = async () => {
    const loginResult = await userStore.loginWithGoogle()

    if (loginResult === null) {
        console.log('로그인 창이 닫혔으므로 아무 작업도 하지 않음')
        return // 로그인 창 닫힘 -> 아무 동작도 안 함
    } else {
        router.replace({ name: 'dashBoard' }) // 메인 페이지로 이동
    }
    console.log('google Login')
}

const handleGithubLogin = async () => {
    const loginResult = await userStore.loginWithGithub()

    if (loginResult === null) {
        console.log('로그인 창이 닫혔으므로 아무 작업도 하지 않음')
        return // 로그인 창 닫힘 -> 아무 동작도 안 함
    } else {
        router.replace({ name: 'dashBoard' }) // 메인 페이지로 이동
    }
    console.log('Github Login')
}
</script>

<style scoped></style>
