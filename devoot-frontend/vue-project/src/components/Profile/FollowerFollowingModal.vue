<template>
    <div
        v-if="isOpen"
        class="fixed inset-0 z-40 flex items-center justify-center bg-black bg-opacity-50"
        @click="closeModal"
    >
        <div class="z-50 bg-white shadow-lg rounded-lg w-[300px] h-[400px] p-4" @click.stop>
            <div class="flex flex-col h-full">
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
                        @click="navigateToProfile(user)"
                        class="flex items-center py-2 space-x-3 border-b cursor-pointer last:border-none hover:bg-gray-100"
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
import { ref, watchEffect, onMounted, watch } from 'vue'
import { searchUsers } from '@/helpers/api'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router' // 추가: 라우터 사용
import { readFollowers, readFollowings } from '@/helpers/follow'
const router = useRouter() // 라우터 인스턴스 생성

const props = defineProps({
    isOpen: Boolean,
    userId: {
        type: String,
        required: true,
    },
    type: {
        type: String, // 'follower' 또는 'following' 값을 받을 예정
        required: true,
    },
})
const emit = defineEmits(['close'])

const searchQuery = ref('')
// const users = ref([])
// 기본 이미지 URL (안정적인 URL 사용)
const defaultImage = 'https://placehold.co/40x40'

const userStore = useUserStore()
// const router = useRouter() // 라우터 인스턴스 생성

const closeModal = () => {
    emit('close')
}

const navigateToProfile = (user) => {
    // 예시: 라우트 경로가 /profile/:profileId 인 경우
    router.push({ path: `/profile/${user.profileId}` })
    closeModal()
}

const users = ref([]) // 팔로워 목록 저장

watch(
    () => [userStore.token, props.userId, props.type],
    async ([newToken, newUserId, newType]) => {
        if (newToken && newUserId) {
            try {
                let result
                console.log('newType', newType)
                if (newType === 'follower') {
                    console.log('📌 팔로워 목록 조회')
                    console.log('타입좀 찍어보자', newType)
                    result = await readFollowers(newToken, newUserId)
                } else {
                    console.log('📌 팔로잉 목록 조회')
                    result = await readFollowings(newToken, newUserId)
                }

                users.value = result.content || [] // ✅ 데이터 저장
            } catch (error) {
                console.error('❌ 목록 조회 실패:', error)
            }
        }
    },
    { immediate: true }
)
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
