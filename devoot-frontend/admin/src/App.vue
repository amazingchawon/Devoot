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

onMounted(async () => {
    await userStore.fetchUser() // 로그인 정보가 설정될 때까지 기다림
})
</script>

<style scoped></style>
