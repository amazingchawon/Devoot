<template>
    <div class="flex flex-col gap-4">
        <div class="content-center h-20 text-h3">댓글 관리</div>

        <div
            id="reported-user-list"
            class="flex flex-col gap-4 p-6 bg-white border-gray-200 rounded-2xl"
        >
            <div class="text-h3">댓글 신고된 유저 목록</div>

            <!-- 🚨 에러 메시지 표시 -->
            <div
                v-if="errorMessage"
                class="p-3 text-red-600 bg-red-100 border border-red-400 rounded"
            >
                {{ errorMessage }}
            </div>

            <!-- 관리자 목록을 표로 표시 -->
            <table v-else-if="reportedUsers.length > 0" class="w-full border text-body">
                <thead>
                    <tr class="text-left bg-gray-100">
                        <th class="w-1/4 p-3 border-b border-gray-200">유저 아이디</th>
                        <!-- 40% -->
                        <th class="w-3/4 p-3 border-b border-gray-200">유저 닉네임</th>
                        <!-- 60% -->
                    </tr>
                </thead>
                <tbody>
                    <tr
                        v-for="user in reportedUsers"
                        :key="user.id"
                        class="border-b border-gray-200 hover:bg-gray-50"
                    >
                        <td class="w-2/5 p-3">
                            <div class="flex items-center gap-3">
                                <img
                                    :src="user.imageUrl"
                                    alt="프로필 이미지"
                                    class="w-8 h-8 border border-gray-200 rounded-full"
                                />
                                <p class="truncate text-primary-500">{{ user.profileId }}</p>
                            </div>
                        </td>
                        <td class="w-3/5 p-3 truncate">
                            <p class="text-black">{{ user.nickname }}</p>
                        </td>
                    </tr>
                </tbody>
            </table>

            <div v-else class="text-gray-500">3번 이상 신고된 유저가 없습니다.</div>

            <!-- ✅ 페이지네이션 컴포넌트 추가 -->
            <PagenationControl
                :currentPage="pageIndex"
                :totalPages="totalPages"
                @page-changed="handlePageChange"
            />
        </div>
    </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getReportedUsers } from '@/helpers/api'
import PagenationControl from '@/components/PagenationControl.vue'

const errorMessage = ref(null)
const userStore = useUserStore()
const reportedUsers = ref([])
const pageIndex = ref(1)
const size = ref(10)
const totalPages = ref(1)

// 신고된 유저 가져오기
const fetchReportedUsers = async () => {
    try {
        if (!userStore.token) return
        const response = await getReportedUsers(userStore.token, pageIndex.value, size.value)
        reportedUsers.value = response.data.content
        totalPages.value = response.data.totalPages
    } catch (error) {
        if (error.response?.status === 403) {
            errorMessage.value = '🚨 관리자 계정이 아닙니다. 관리자 페이지에 접근이 불가능합니다.'
        } else {
            errorMessage.value = '🚨 데이터를 불러오는 중 오류가 발생했습니다.'
        }
        console.error('🚨 대시보드 API 요청 실패:', error)
    }
}

// 페이지 변경 핸들러 (페이지네이션에서 호출됨)
const handlePageChange = (newPage) => {
    pageIndex.value = newPage
    fetchReportedUsers() // 페이지 변경 시 새로운 데이터 가져오기
}

// token이 설정될 때 API 요청 실행
watch(
    () => userStore.token,
    (newToken) => {
        if (newToken) {
            fetchReportedUsers()
        }
    }
)
// 페이지가 마운트될 때 실행 (만약 token이 이미 존재하면 바로 실행)
onMounted(() => {
    if (userStore.token) {
        fetchReportedUsers()
    }
})
</script>

<style scoped></style>
