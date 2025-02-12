<template>
    <div v-if="isOpen" class="fixed inset-0 z-40" @click="closeModal">
        <div
            class="absolute top-20 left-[13.5rem] z-50 bg-white shadow-lg rounded-lg w-[22.9375rem] h-[calc(100vh-5rem)]"
            @click.stop
        >
            <div class="flex flex-col h-full p-4">
                <div class="flex items-center justify-between mb-4">
                    <h2 class="text-base font-bold">사용자 검색</h2>
                    <button @click="closeModal" class="text-2xl text-gray-500 hover:text-black">
                        &times;
                    </button>
                </div>
                <div class="relative">
                    <input
                        v-model="searchQuery"
                        type="text"
                        placeholder="사용자 검색"
                        class="w-full py-2 pl-4 pr-8 text-sm border rounded bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                </div>
                <ul class="flex-1 mt-4 overflow-y-auto no-scrollbar">
                    <li
                        v-for="user in users"
                        :key="user.id"
                        class="flex items-center py-2 space-x-3 border-b cursor-pointer last:border-none hover:bg-gray-100"
                        @click="navigateToProfile(user)"
                    >
                        <img
                            :src="user.imageUrl || defaultImage"
                            alt="profile"
                            class="object-cover w-10 h-10 rounded-full"
                        />
                        <div class="flex flex-col">
                            <p class="text-sm font-medium">{{ user.profileId }}</p>
                            <p class="text-xs text-gray-500">{{ user.nickname }}</p>
                        </div>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, watchEffect, onMounted } from 'vue'
import { searchUsers } from '@/helpers/api'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router' // 추가: 라우터 사용

const props = defineProps({
    isOpen: Boolean,
})
const emit = defineEmits(['close'])

const searchQuery = ref('')
const users = ref([])
// 기본 이미지 URL (안정적인 URL 사용)
const defaultImage = 'https://placehold.co/40x40'

const userStore = useUserStore()
const router = useRouter() // 라우터 인스턴스 생성

watchEffect(async () => {
    if (searchQuery.value.trim()) {
        try {
            console.log('검색어:', searchQuery.value)
            if (!userStore.token) {
                console.error('❌ 토큰이 없습니다. 로그인 상태를 확인하세요.')
                return
            }
            const result = await searchUsers(userStore.token, searchQuery.value)
            console.log('API 검색 결과:', result)
            // 응답 객체의 content 배열을 사용합니다.
            users.value = result.content || []
        } catch (error) {
            console.error('사용자 검색 오류:', error)
        }
    } else {
        users.value = []
    }
})

const closeModal = () => {
    emit('close')
}

const navigateToProfile = (user) => {
    // 예시: 라우트 경로가 /profile/:profileId 인 경우
    router.push({ path: `/profile/${user.profileId}` })
}

onMounted(() => {
    console.log('현재 Firebase 토큰:', userStore.token)
})
</script>

<style scoped>
.no-scrollbar::-webkit-scrollbar {
    display: none;
}
.no-scrollbar {
    scrollbar-width: none;
    -ms-overflow-style: none;
}
</style>
