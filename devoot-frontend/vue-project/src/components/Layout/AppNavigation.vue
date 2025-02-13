<template>
    <div
        id="navigation"
        class="fixed top-0 left-0 z-50 w-[4.5rem] lg:w-[13.5rem] flex flex-col h-screen bg-white border-r border-gray-200 ease-in-out transition-all duration-500"
    >
        <!-- 로고 -->
        <div id="logo" class="flex flex-row items-center justify-center h-20 gap-3">
            <Logo class="w-8 h-8" />
            <div class="hidden text-h1 lg:block">개발바닥</div>
        </div>

        <!-- 메뉴 -->
        <div id="menu-container" class="flex flex-col flex-1 py-3">
            <!-- 서비스 관련 메뉴 -->
            <div id="menu">
                <div
                    v-for="(menu, index) in serviceMenu"
                    :key="index"
                    class="flex flex-row items-center justify-center gap-5 px-0 py-4 cursor-pointer lg:justify-start lg:px-9 hover:text-black"
                    :class="selectedMenu === menu.name ? 'text-black' : 'text-gray-300'"
                    @click="navigateTo(menu)"
                >
                    <component
                        :is="selectedMenu === menu.name ? menu.iconFill : menu.iconDefault"
                        class="w-6 h-6"
                    />
                    <p class="hidden text-left text-body lg:block">
                        {{ menu.label }}
                    </p>
                </div>
            </div>

            <!-- 간격용 -->
            <div class="flex-1"></div>

            <!-- 회원 관련 메뉴 -->
            <div id="member-menu">
                <!-- 로그인하지 않은 경우: 로그인/회원가입 버튼 표시 -->
                <div
                    v-if="!userStore.isAuthenticated"
                    class="flex flex-row items-center justify-center gap-5 px-0 py-4 cursor-pointer lg:justify-start lg:px-9 hover:text-black"
                    :class="selectedMenu === 'login' ? 'text-black' : 'text-gray-300'"
                    @click="navigateTo({ name: 'login', routeName: 'login' })"
                >
                    <LogIn class="w-6 h-6" />
                    <p class="hidden text-left text-body lg:block">로그인/회원가입</p>
                </div>

                <!-- 로그인한 경우: 회원정보 수정 & 로그아웃 버튼 표시 -->
                <div v-else>
                    <div
                        class="flex flex-row items-center justify-center gap-5 px-0 py-4 cursor-pointer lg:justify-start lg:px-9 hover:text-black"
                        :class="selectedMenu === 'edit-profile' ? 'text-black' : 'text-gray-300'"
                        @click="navigateTo({ name: 'profileEdit', routeName: 'profileEdit' })"
                    >
                        <UpdateProfile class="w-6 h-6" />
                        <p class="hidden text-left text-body lg:block">회원정보 수정</p>
                    </div>

                    <div
                        class="flex flex-row items-center justify-center gap-5 px-0 py-4 cursor-pointer lg:justify-start lg:px-9 hover:text-black"
                        :class="selectedMenu === 'logout' ? 'text-black' : 'text-gray-300'"
                        @click="logout"
                    >
                        <LogOut class="w-6 h-6" />
                        <p class="hidden text-left text-body lg:block">로그아웃</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, watch, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user' // Pinia store 가져오기

import Logo from '@/assets/icons/logo.svg'
import HomeDefault from '@/assets/icons/home_default.svg'
import HomeFill from '@/assets/icons/home_fill.svg'
import LectureDefault from '@/assets/icons/lecture_default.svg'
import LectureFill from '@/assets/icons/lecture_fill.svg'
import TimelineDefault from '@/assets/icons/timeline_default.svg'
import TimelineFill from '@/assets/icons/timeline_fill.svg'
import ProfileDefault from '@/assets/icons/profile_default.svg'
import ProfileFill from '@/assets/icons/profile_fill.svg'
import LogIn from '@/assets/icons/login.svg'
import UpdateProfile from '@/assets/icons/update_profile.svg'
import LogOut from '@/assets/icons/logout.svg'

// 라우터 사용
const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 서비스 메뉴 항목 배열
const serviceMenu = [
    { name: 'home', label: '홈', routeName: 'home', iconDefault: HomeDefault, iconFill: HomeFill },
    {
        name: 'lecture',
        label: '강의',
        routeName: 'lectureSearch', // index.js에서 설정한 name
        iconDefault: LectureDefault,
        iconFill: LectureFill,
    },
    {
        name: 'timeline',
        label: '타임라인',
        routeName: 'timeline',
        iconDefault: TimelineDefault,
        iconFill: TimelineFill,
    },
    {
        name: 'profile',
        label: '프로필',
        routeName: 'profile',
        iconDefault: ProfileDefault,
        iconFill: ProfileFill,
    },
]

// 회원 관련 메뉴 항목 배열
const memberMenu = [
    { name: 'login', label: '로그인/회원가입', routeName: 'login', icon: LogIn },
    {
        name: 'edit-profile',
        label: '회원정보 수정',
        routeName: 'profileEdit',
        icon: UpdateProfile,
    },
    { name: 'logout', label: '로그아웃', routeName: 'home', icon: LogOut }, // 로그아웃 후 홈으로 이동
]

// 선택된 메뉴를 저장하는 변수 (기본값: 'home')
const selectedMenu = ref('home')

// 사용자 ID (변수)
const userId = computed(() => userStore.userId)

// 현재 라우트에 따라 selectedMenu 업데이트
const updateSelectedMenu = () => {
    console.log('현재 라우트:', route.path, '이름:', route.name)

    const allMenus = [...serviceMenu, ...memberMenu]
    const matchedMenu = allMenus.find((menu) => menu.routeName === route.name)

    selectedMenu.value = matchedMenu ? matchedMenu.name : 'home'

    console.log('선택된 메뉴:', selectedMenu.value)
}

// watch를 사용해 라우트 변경 시 메뉴 선택 상태 업데이트
watch(route, updateSelectedMenu, { immediate: true })

// 로그아웃 함수
const logout = async () => {
    console.log('로그아웃 시작')
    await userStore.logout()
    console.log('로그아웃 완료')
    selectedMenu.value = 'home'
    router.push({ name: 'home' }) // 홈으로 이동
}

// 메뉴를 선택했을 때 호출되는 함수
const navigateTo = (menu) => {
    selectedMenu.value = menu.name
    if (menu.routeName === 'profile') {
        router.push({ name: menu.routeName, params: { id: userId.value } })
    } else {
        router.push({ name: menu.routeName })
    }
}

// 컴포넌트가 마운트될 때 초기 선택 상태 설정
onMounted(updateSelectedMenu)
</script>

<style scoped></style>
