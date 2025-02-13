<template>
    <div class="flex h-scree">
        <!-- 네비게이션 바 -->
        <AppNavigation class="fixed z-50" />

        <!-- 메인 컨텐츠 영역 -->
        <div class="relative flex flex-col flex-1 ml-[4.5rem] lg:ml-[13.5rem]">
            <!-- 헤더 -->
            <AppHeader v-if="shouldShowHeader" :type="headerType" />

            <!-- 컨테이너 -->
            <div
                id="container"
                class="flex-1 grid w-full max-w-[1440px] mx-auto grid-cols-12 gap-6 overflow-y-auto px-9 relative z-30"
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
// const isLoaded = ref(false) // 사용자 정보 로드 상태 변수

onMounted(async () => {
    await userStore.fetchUser()
    // isLoaded.value = true // 사용자 정보가 모두 로드된 후 isLoaded를 true로 설정
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

<style scoped></style>
