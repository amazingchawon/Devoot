import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'dashBoard',
            component: () => import('@/views/DashBoardPage.vue'),
        },
        {
            path: '/add/lecture',
            name: 'addLecture',
            component: () => import('@/views/AddLecturePage.vue'),
        },
        {
            path: '/add/request',
            name: 'addRequest',
            component: () => import('@/views/AddRequestPage.vue'),
        },
        {
            path: '/edit/lecture',
            name: 'editLecture',
            component: () => import('@/views/EditLecturePage.vue'),
        },
        {
            path: '/edit/request',
            name: 'editRequest',
            component: () => import('@/views/EditRequestPage.vue'),
        },
        {
            path: '/user/report',
            name: 'reportedUser',
            component: () => import('@/views/ReportedUserPage.vue'),
        },
        {
            path: '/user/admin',
            name: 'reportedUserAdmin',
            component: () => import('@/views/ReportedUserAdminPage.vue'),
        },
        {
            path: '/login',
            name: 'login',
            component: () => import('@/views/LogInPage.vue'),
        },
    ],
})

export default router
