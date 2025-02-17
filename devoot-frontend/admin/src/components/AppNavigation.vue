<template>
    <div
        id="navigation"
        class="bg-gray-100 fixed top-0 left-0 z-50 w-[4.5rem] lg:w-[13.5rem] flex flex-col h-screen border-r border-gray-200 ease-in-out transition-all duration-500"
    >
        <!-- Logo -->
        <div
            id="logo"
            class="flex flex-row items-center justify-center h-20 gap-3 cursor-pointer lg:justify-normal lg:px-6"
        >
            <!-- 로고 아이콘 (크기 고정) -->
            <div class="flex flex-col items-center justify-center gap-1">
                <Logo class="w-8 h-8 text-primary-500 shrink-0" />
                <p
                    class="px-2 py-1 bg-white rounded-full whitespace-nowrap w-fit h-fit text-primary-500 text-caption-sm"
                >
                    관리자
                </p>
            </div>

            <!-- 텍스트 영역 -->
            <div class="hidden lg:block">
                <div class="flex flex-col gap-1">
                    <p class="text-h1">개발바닥</p>
                    <p class="text-gray-300 text-caption">관리자 페이지</p>
                </div>
            </div>
        </div>

        <!-- Navigation Menu -->
        <div class="flex flex-col flex-1">
            <nav class="flex flex-col flex-1 py-3">
                <RouterLink
                    v-for="(item, index) in menuItems"
                    :key="index"
                    :to="item.path"
                    class="flex flex-row items-center gap-5 px-6 py-4 text-gray-600 transition-all rounded-md lg:px-6 hover:bg-gray-200 hover:text-primary-500"
                    active-class="text-primary-500"
                >
                    <component :is="item.icon" class="w-6 h-6" />
                    <span class="hidden text-left text-body lg:block">{{ item.label }}</span>
                </RouterLink>
            </nav>

            <div class="flex-1"></div>

            <!-- 로그인/로그아웃 버튼 -->
            <nav class="py-3">
                <button
                    v-if="userStore.isAuthenticated"
                    class="flex flex-row items-center gap-5 px-0 py-4 transition-all rounded-md lg:px-9 hover:bg-gray-200 hover:text-primary-500"
                    @click="logout"
                >
                    <Logout class="w-6 h-6" />
                    <span class="hidden text-left text-body lg:block">로그아웃</span>
                </button>
                <RouterLink
                    v-else
                    to="/login"
                    class="flex flex-row items-center gap-5 px-0 py-4 transition-all rounded-md lg:px-9 hover:bg-gray-200 hover:text-primary-500"
                >
                    <LogIn class="w-6 h-6" />
                    <span class="hidden text-left text-body lg:block">로그인</span>
                </RouterLink>
            </nav>
        </div>
    </div>
</template>

<script setup>
import { ref, markRaw } from 'vue'
import { RouterLink } from 'vue-router'
import { useUserStore } from '@/stores/user' // Pinia의 userStore 가져오기
import Logo from '@/assets/icons/logo.svg'
import Edit from '@/assets/icons/edit.svg'
import Report from '@/assets/icons/report.svg'
import Plus from '@/assets/icons/plus.svg'
import Error from '@/assets/icons/error.svg'
import LogIn from '@/assets/icons/login.svg'
import Logout from '@/assets/icons/logout.svg'

const userStore = useUserStore() // Pinia 상태 관리

const menuItems = ref([
    { label: '강의 수정', path: '/edit/request', icon: markRaw(Edit) },
    { label: '강의 등록', path: '/add/request', icon: markRaw(Plus) },
    { label: '신고 관리', path: '/admin/report', icon: markRaw(Report) },
    { label: '오류 관리', path: '/admin/error', icon: markRaw(Error) },
])

// 로그아웃 함수
const logout = async () => {
    await userStore.logout()
    alert('로그아웃 되었습니다.')
}
</script>

<style scoped></style>
