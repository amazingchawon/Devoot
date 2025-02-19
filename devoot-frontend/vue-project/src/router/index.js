import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            component: () => import('@/views/HomePage.vue'),
        },
        {
            path: '/lecture',
            children: [
                {
                    path: '',
                    name: 'lectureSearch',
                    component: () => import('@/views/Lecture/LectureSearchPage.vue'),
                },
                {
                    path: ':id',
                    name: 'lectureDetail',
                    component: () => import('@/views/Lecture/LectureDetailPage.vue'),
                },
            ],
        },
        {
            path: '/timeline',
            name: 'timeline',
            component: () => import('@/views/TimelinePage.vue'),
        },
        {
            path: '/profile',
            children: [
                {
                    path: ':id',
                    name: 'profile',
                    component: () => import('@/views/ProfilePage.vue'),
                    // meta: { requiresAuth: true }, // 로그인 필요
                },
                {
                    path: 'edit',
                    name: 'profileEdit',
                    component: () => import('@/views/ProfileEditPage.vue'),
                    // meta: { requiresAuth: true }, // 로그인 필요
                },
            ],
        },
        {
            path: '/login',
            name: 'login',
            component: () => import('@/views/LogInPage.vue'),
        },
    ],
})

// 라우트 가드 추가
router.beforeEach((to, from, next) => {
    const userStore = useUserStore()

    // 로그인이 필요한 페이지인지 확인
    if (to.meta.requiresAuth && !userStore.isAuthenticated) {
        console.warn('🚨 인증되지 않은 사용자가 접근을 시도함:', to.fullPath)
        alert('로그인이 필요한 페이지입니다.')
        next({ name: 'login' }) // 로그인 페이지로 이동
    } else {
        next() // 정상적으로 이동
    }
})

export default router
