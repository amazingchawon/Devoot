<template>
    <div class="flex h-screen">
        <!-- 네비게이션 바 -->
        <AppNavigation class="fixed z-50" />

        <div class="relative flex flex-col flex-1 ml-[4.5rem] lg:ml-[13.5rem] bg-gray-100">
            <!-- 컨테이너 -->
            <div
                id="container"
                class="flex-1 grid w-full max-w-[1440px] mx-auto grid-cols-12 gap-6 overflow-y-auto px-9"
            >
                <router-view class="col-span-12" />
            </div>
        </div>
    </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/user'

import AppNavigation from '@/components/AppNavigation.vue'

const userStore = useUserStore()

onMounted(() => {
    userStore.fetchUser() // 앱이 실행될 때 로그인 유지 확인
})
</script>

<style scoped></style>
