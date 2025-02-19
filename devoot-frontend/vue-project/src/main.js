import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import { useUserStore } from '@/stores/user' // 유저 스토어 가져오기
import './input.css' // Tailwind CSS 추가

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)

const userStore = useUserStore()

// 로그인 상태 유지: fetchUser() 실행 후 앱 마운트
userStore.fetchUser().then(() => {
    app.use(router)
    app.mount('#app')
})
