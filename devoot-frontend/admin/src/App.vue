<template>
    <div class="flex h-scree">
        <!-- 네비게이션 바 -->
        <AppNavigation class="fixed z-50" />

        <!-- 메인 컨텐츠 영역 -->
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
import AppNavigation from '@/components/AppNavigation.vue'
</script>

<style scoped></style>
