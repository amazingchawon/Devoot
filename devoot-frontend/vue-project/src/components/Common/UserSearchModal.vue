<template>
    <transition name="slide">
        <div v-if="isOpen" class="fixed inset-0 z-40" @click.self="closeModal">
            <div
                class="absolute top-20 left-[4.5rem] lg:left-[13.5rem] z-50 bg-white shadow-xl border border-l-0 border-gray-200 rounded-r-lg w-[22.9375rem] h-[calc(100vh-5rem)]"
            >
                <!-- 헤더 -->
                <div class="flex flex-col h-full p-6 ml-3">
                    <div class="flex items-center justify-between mb-4">
                        <p class="text-h2">사용자 검색</p>
                        <button class="text-h2" @click="closeModal">&times;</button>
                    </div>

                    <!-- 검색창 -->
                    <div class="relative h-10">
                        <input
                            v-model="searchQuery"
                            type="text"
                            placeholder="검색"
                            class="w-full py-2 pl-4 pr-8 text-gray-300 border rounded-lg text-body bg-gray-50 focus:outline-none focus:ring-2 focus:ring-primary-500"
                        />
                    </div>

                    <!-- 검색 결과 -->
                    <ul class="flex-1 mt-8 overflow-y-auto no-scrollbar">
                        <li
                            v-for="user in users"
                            :key="user.id"
                            class="flex items-center p-2 space-x-3 border-b cursor-pointer last:border-none hover:bg-gray-100"
                            @click="navigateToProfile(user)"
                        >
                            <img
                                :src="
                                    user.imageUrl ||
                                    'https://devoot-profile-image.s3.ap-northeast-2.amazonaws.com/profile/default_image.png'
                                "
                                alt="profile"
                                class="object-cover w-10 h-10 rounded-full"
                            />

                            <div class="flex flex-col">
                                <p class="text-body-bold">{{ user.profileId }}</p>
                                <p class="text-body">{{ user.nickname }}</p>
                            </div>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </transition>
</template>

<script setup>
import { ref, watchEffect, onMounted } from 'vue'
import { searchUsers } from '@/helpers/api'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'

const props = defineProps({
    isOpen: Boolean,
})
const emit = defineEmits(['close'])

const searchQuery = ref('')
const users = ref([])
const defaultImage = 'https://placehold.co/40x40'

const userStore = useUserStore()
const router = useRouter()

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
    router.push({ path: `/profile/${user.profileId}` })
    closeModal() // 프로필 이동 후 모달 닫기
}

onMounted(() => {
    // console.log('현재 Firebase 토큰:', userStore.token)
})
</script>

<style scoped>
/* 모달 슬라이드 애니메이션 */
.slide-enter-active,
.slide-leave-active {
    transition:
        transform 0.4s ease-out,
        opacity 0.35s ease-out;
}
.slide-enter-from {
    transform: translateX(-30%);
    opacity: 0.2;
}
.slide-enter-to {
    transform: translateX(0);
    opacity: 1;
}
.slide-leave-from {
    transform: translateX(0);
    opacity: 1;
}
.slide-leave-to {
    transform: translateX(-30%);
    opacity: 0;
}

/* 스크롤바 숨기기 */
.no-scrollbar::-webkit-scrollbar {
    display: none;
}
.no-scrollbar {
    scrollbar-width: none;
    -ms-overflow-style: none;
}
</style>
