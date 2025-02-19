<!-- src\App.vue -->
<template>
    <div class="flex h-screen">
        <!-- 네비게이션 바 -->
        <AppNavigation class="fixed z-50" />

        <!-- 전체 컨테이너 -->
        <div class="relative flex flex-col flex-1 ml-[4.5rem] lg:ml-[13.5rem] px-9">
            <!-- 헤더 -->
            <AppHeader v-if="shouldShowHeader" class="z-40 mt-5 mb-5" :type="headerType" />

            <!-- 메인 컨텐츠 영역 -->
            <div
                id="container"
                class="flex-1 grid w-full max-w-[1440px] mx-auto grid-cols-12 gap-6 relative custom-scrollbar mt-9"
            >
                <router-view class="col-span-12" />
            </div>
        </div>
    </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from './stores/user'

import AppNavigation from './components/Layout/AppNavigation.vue'
import AppHeader from './components/Layout/AppHeader.vue'

const userStore = useUserStore()

onMounted(async () => {
    await userStore.fetchUser()
})

const route = useRoute()

const headerType = computed(() => {
    const path = route.path
    if (path === '/' || path.startsWith('/lecture')) {
        return 'lecture'
    } else if (path.startsWith('/timeline') || path.startsWith('/profile')) {
        return 'user'
    }
    return 'default'
})

const shouldShowHeader = computed(() => {
    const hiddenPaths = ['/login', '/profile/:id/edit']
    const currentPath = route.path
    return !hiddenPaths.some((hiddenPath) => {
        const regex = new RegExp(`^${hiddenPath.replace(':id', '[^/]+')}$`)
        return regex.test(currentPath)
    })
})
</script>

<style scoped>
html,
body {
    height: 100%;
    overflow: auto;
}

#container {
    min-height: 100vh;
}

router-view {
    flex: 1;
    display: flex;
    flex-direction: column;
}
</style>
