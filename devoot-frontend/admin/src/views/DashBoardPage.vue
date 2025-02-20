<template>
    <div class="flex flex-col gap-4">
        <div class="content-center h-20 text-h3">대시보드</div>

        <div
            id="admin-user-list"
            class="flex flex-col gap-4 p-6 bg-white border-gray-200 rounded-2xl"
        >
            <div class="text-h3">관리자 목록</div>

            <!-- 🚨 에러 메시지 표시 -->
            <div
                v-if="errorMessage"
                class="p-3 text-red-600 bg-red-100 border border-red-400 rounded"
            >
                {{ errorMessage }}
            </div>

            <!-- 관리자 목록을 표로 표시 -->
            <table
                v-else-if="adminUsers.length > 0"
                class="w-full overflow-hidden border border-collapse border-gray-300 rounded-2xl text-body"
            >
                <thead class="bg-primary-100">
                    <tr>
                        <th class="p-3">아이디</th>
                        <th class="p-3">이메일</th>
                        <th class="p-3">추가 일자</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="user in adminUsers" :key="user.id" class="text-center">
                        <td class="p-3 border border-gray-200">{{ user.profileId }}</td>
                        <td class="p-3 border border-gray-200">{{ user.email }}</td>
                        <td class="p-3 border border-gray-200">{{ user.createdAt }}</td>
                    </tr>
                </tbody>
            </table>

            <div v-else class="text-gray-500">관리자 데이터가 없습니다.</div>
        </div>
    </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useUserStore } from '@/stores/user'
import { getAdminUser } from '@/helpers/api'

const userStore = useUserStore()
const adminUsers = ref([]) // 관리자 목록
const errorMessage = ref(null) // 🚨 에러 메시지 저장

// 관리자 유저 가져오는 함수
const fetchAdminUser = async () => {
    if (!userStore.token) {
        console.log('토큰 없음. API 요청 안함')
        return
    }

    try {
        const response = await getAdminUser(userStore.token)
        adminUsers.value = response.data
        errorMessage.value = null // ✅ 성공하면 에러 메시지 초기화
    } catch (error) {
        if (error.response?.status === 403) {
            errorMessage.value = '🚨 관리자 계정이 아닙니다. 관리자 페이지에 접근이 불가능합니다.'
        } else {
            errorMessage.value = '🚨 데이터를 불러오는 중 오류가 발생했습니다.'
        }
        console.error('🚨 대시보드 API 요청 실패:', error)
    }
}

// 🔥 Token 값이 변경될 때 fetchAdminUser 실행
watch(
    () => userStore.token,
    (newToken) => {
        if (newToken) {
            fetchAdminUser()
        }
    },
    { immediate: true }
)
</script>
