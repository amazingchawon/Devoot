<template>
    <div
        v-if="isOpen"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50"
    >
        <div class="p-5 bg-white rounded-lg shadow-lg w-80 md:w-96">
            <!-- 헤더 -->
            <div class="flex items-center justify-between mb-4">
                <h2 class="text-lg font-bold">알림 보기</h2>
                <button @click="closeModal" class="text-xl text-gray-500 hover:text-black">
                    ×
                </button>
            </div>

            <!-- 알림 목록 -->
            <div v-if="notifications.length === 0" class="text-center text-gray-500">
                새로운 알림이 없습니다.
            </div>

            <ul v-else class="space-y-4">
                <li
                    v-for="(notification, index) in notifications"
                    :key="notification.id"
                    class="flex flex-col items-center p-4 bg-gray-100 rounded-lg"
                >
                    <!-- 위쪽: 프로필 이미지 & 텍스트 -->
                    <div class="flex items-center space-x-3">
                        <!-- 프로필 이미지 -->
                        <img
                            :src="notification.fromUserImageUrl"
                            alt="User Image"
                            class="w-[2.5rem] h-[2.5rem] bg-gray-300 rounded-full"
                        />
                        <!-- 알림 내용 -->
                        <div class="text-body">
                            <p>
                                <!-- 닉네임 클릭 시 프로필 페이지로 이동 후 모달 닫기 -->
                                <span
                                    class="cursor-pointer text-body-bold hover:underline"
                                    @click="goToProfile(notification.fromUserProfileId)"
                                >
                                    {{ notification.fromUserNickname }}
                                </span>
                                님이
                            </p>
                            <p class="text-body">
                                {{
                                    notification.pending
                                        ? '팔로우 요청을 보냈습니다.'
                                        : '팔로우하기 시작했습니다.'
                                }}
                            </p>
                        </div>
                    </div>

                    <!-- 아래쪽: 팔로우 요청 수락 버튼 -->
                    <button
                        v-if="notification.pending"
                        @click="acceptFollow(notification.followId)"
                        class="w-full px-4 py-2 mt-3 text-center text-white bg-blue-500 rounded-lg text-body-bold hover:bg-blue-600"
                    >
                        팔로우 수락
                    </button>
                </li>
            </ul>
        </div>
    </div>
</template>

<script setup>
import { ref, defineProps, defineEmits, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getNotifications } from '@/helpers/notification'
import { acceptFollowRequest } from '@/helpers/follow' // ✅ 새로 추가된 API 요청 함수

const props = defineProps({
    isOpen: Boolean, // 모달 열림 상태
    token: String, // 사용자 토큰
})

const emit = defineEmits(['close']) // 부모 컴포넌트에 close 이벤트 전달

const router = useRouter() // Vue Router 인스턴스 사용

const notifications = ref([]) // 알림 목록
const isLoading = ref(true) // 로딩 상태

// 모달을 닫는 함수
const closeModal = () => {
    emit('close') // 부모 컴포넌트로 모달 닫기 이벤트 전달
}

// 알림 데이터 로딩 함수
const loadNotifications = async () => {
    if (!props.token) {
        console.error('❌ 토큰이 존재하지 않음!')
        return
    }

    console.log('🔄 알림 데이터 로딩 중...')

    try {
        const data = await getNotifications(props.token)
        notifications.value = data.content
        console.log('✅ 불러온 알림 목록:', notifications.value)
    } catch (error) {
        console.error('❌ 알림 로딩 실패:', error)
    }
}

// ✅ 팔로우 수락 함수 (API 요청을 follow.js에서 가져옴)
const acceptFollow = async (followId) => {
    try {
        await acceptFollowRequest(props.token, followId)
        await loadNotifications() // 팔로우 수락 후 알림 목록 업데이트
    } catch (error) {
        console.error('❌ 팔로우 수락 요청 실패:', error)
    }
}

// 닉네임 클릭 시 프로필 페이지 이동 후 모달 닫기
const goToProfile = (profileId) => {
    if (!profileId) {
        console.error('❌ 프로필 ID가 없음!')
        return
    }
    console.log(`🔗 프로필 페이지로 이동: /profile/${profileId}`)

    // 페이지 이동
    router.push(`/profile/${profileId}`)

    // 모달 닫기
    closeModal()
}

// 컴포넌트가 마운트될 때 알림을 로드
onMounted(() => {
    if (props.token) {
        loadNotifications()
    }
})

// **모달이 열릴 때마다 알림 데이터 로드**
watch(
    () => props.isOpen,
    (newVal) => {
        if (newVal) {
            console.log('📌 모달 열림 - 알림 데이터 로드 실행')
            loadNotifications()
        }
    }
)
</script>
