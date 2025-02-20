<template>
    <div
        v-if="isOpen"
        class="absolute right-0 mt-2 w-[16.25rem] bg-white rounded-2xl border border-gray-200 shadow-lg ring-0 overflow-hidden"
        @click="closeModal"
    >
        <div class="px-4 py-6 bg-white rounded-lg shadow-lg w-[16.25rem]" @click.stop>
            <!-- 헤더 -->
            <div class="flex items-center justify-between mb-4">
                <p class="text-h2">알림 보기</p>
                <button class="text-h2" @click="closeModal">&times;</button>
            </div>

            <!-- 알림 목록 -->
            <div v-if="notifications.length === 0" class="text-center text-gray-500">
                새로운 알림이 없습니다.
            </div>

            <ul v-else class="space-y-4">
                <li
                    v-for="(notification, index) in notifications"
                    :key="notification.id"
                    class="flex flex-col items-center px-2 py-3 rounded-lg"
                >
                    <!-- 위쪽: 프로필 이미지 & 텍스트 -->
                    <div class="flex items-center space-x-3">
                        <!-- 프로필 이미지 -->
                        <img
                            :src="
                                notification.fromUserImageUrl !== null
                                    ? notification.fromUserImageUrl
                                    : 'https://devoot-profile-image.s3.ap-northeast-2.amazonaws.com/profile/default_image.png'
                            "
                            alt="User Image"
                            class="w-10 h-10 rounded-full"
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
                                        : '회원님을 팔로우하기 시작했습니다.'
                                }}
                            </p>
                        </div>
                    </div>

                    <!-- 아래쪽: 팔로우 요청 수락 버튼 -->
                    <button
                        v-if="notification.pending"
                        class="w-[13.25rem] h-6 mt-3 text-center text-white rounded-lg bg-primary-500 text-body-bold hover:bg-blue-600"
                        @click="acceptFollow(notification.followId)"
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
import { acceptFollowRequest } from '@/helpers/follow' // 새로 추가된 API 요청 함수

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

// 팔로우 수락 함수 (API 요청을 follow.js에서 가져옴)
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

// 모달이 열릴 때마다 알림 데이터 로드
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

<style scoped></style>
