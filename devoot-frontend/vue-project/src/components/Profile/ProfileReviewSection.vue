<template>
    <div class="flex flex-col w-full p-6 gap-6 max-h-[400px] overflow-y-auto">
        <ProfileReviewCard
            v-for="(review, index) in reviewData.content"
            :key="index"
            :review="review"
        />
    </div>
</template>

<script setup>
import ProfileReviewCard from './ProfileReviewCard.vue'
import { ref, onMounted, watch } from 'vue'
import axios from 'axios'
import { useUserStore } from '@/stores/user'

defineProps({
    userId: String,
    token: String,
})

const userStore = useUserStore() // Pinia 스토어 가져오기
const reviewData = ref([])
const loadUserReviews = async (token, userId) => {
    try {
        const mock_server_url = 'http://localhost:8080'
        // const profileId = 'l3olvy' // 여기에 실제 사용자 ID를 넣어야 함
        // const profileId = userStore.userId // 여기에 실제 사용자 ID를 넣어야 함
        const API_URL = `${mock_server_url}/api/users/${userId}/reviews`
        // const token = 'asdfasdfasdf' // 여기에 Bearer 토큰을 넣어야 함

        const response = await axios.get(API_URL, {
            headers: {
                'Content-Type': 'application/json', //필수 헤더 추가
                Authorization: `Bearer ${token}`, // Bearer 토큰 추가
            },
        })

        reviewData.value = response.data
        // console.log('유저리뷰', reviewData.value)
        // console.log('API_URL', API_URL)
    } catch (error) {
        console.error('에러:', error)
    }
}

watch(
    () => [userStore.token, userStore.userId], // ✅ 두 값을 동시에 감시
    async ([newToken, newUserId]) => {
        if (newToken && newUserId) {
            // 두 값이 모두 존재할 때만 실행
            // console.log('✅ 토큰과 userId가 준비되었습니다.')
            await loadUserReviews(newToken, newUserId)
        }
    },
    { immediate: true } // 이미 값이 존재할 경우 즉시 실행
)

// onMounted(() => {
//     loadUserReviews() // JSON 데이터 가져오기
// })
</script>

<style></style>
