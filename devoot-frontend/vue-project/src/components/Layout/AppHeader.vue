<!-- src/components/Layout/AppHeader.vue -->
<template>
    <header class="flex items-center justify-between w-full h-20 bg-white">
        <!-- type이 'lecture'인 경우 -->
        <template v-if="type === 'lecture'">
            <!-- 외부 클릭 감지를 위한 컨테이너에 ref 추가 -->
            <div class="relative flex" ref="categoryContainer">
                <!-- 카테고리 버튼 -->
                <button
                    class="header-button"
                    aria-label="카테고리 선택"
                    @click="toggleCategoryDropdown"
                >
                    <CategoryIcon class="w-6 h-6 mr-2.5" />
                    <p class="flex-1">카테고리</p>
                </button>

                <!-- 카테고리 드롭다운 -->
                <CategoryDropDown
                    v-if="isCategoryDropdownVisible"
                    class="absolute left-0 z-30 mt-2 overflow-hidden rounded-lg shadow-lg top-full w-44"
                    @closeDropdown="closeCategoryDropdown"
                />
            </div>

            <!-- 검색창 -->
            <div class="relative w-full max-w-[47.5rem] h-11">
                <input
                    type="text"
                    v-model="searchQuery"
                    placeholder="강의명, 강사명, 키워드 검색"
                    class="w-full h-full px-4 pr-10 text-gray-300 bg-gray-100 border border-gray-200 rounded-lg text-body focus:outline-none"
                    @keyup.enter="executeSearch"
                />
                <!-- 검색 아이콘 -->
                <SearchIcon
                    class="absolute w-6 h-6 text-black transform -translate-y-1/2 cursor-pointer top-1/2 right-3"
                    @click="executeSearch"
                />
            </div>
        </template>

        <!-- type이 'user'인 경우 -->
        <template v-else-if="type === 'user'">
            <div>
                <!-- 사용자 검색 버튼 -->
                <button class="header-button" aria-label="사용자 검색" @click="openUserSearchModal">
                    <UserSearchIcon class="w-6 h-6 mr-2.5" />
                    <p class="flex-1">사용자 검색</p>
                </button>
            </div>

            <!-- 사용자 검색 모달 -->
            <UserSearchModal :is-open="isUserSearchModalOpen" @close="closeUserSearchModal" />
        </template>

        <!-- 오른쪽: 알림 버튼 (공통) - 로그인 상태에 따라 visibility 조정 -->
        <div
            class="relative flex items-center ml-6"
            :style="{ visibility: userStore.isAuthenticated ? 'visible' : 'hidden' }"
        >
            <button
                class="relative inline-flex items-center justify-center w-10 h-10 transition-all duration-150 rounded-full hover:bg-gray-100"
                aria-label="알림"
                :class="{ 'translate-y-[2px]': isClicked }"
                @click="
                    () => {
                        handleClick()
                        openNotificationModal()
                    }
                "
            >
                <!-- 알림 여부에 따라 아이콘 변경 -->
                <component
                    :is="hasNotifications ? BellNotificationIcon : BellIcon"
                    class="w-6 h-6 transition-transform duration-150"
                    :class="{ 'translate-y-[2px]': isClicked }"
                />
            </button>

            <!-- 알림 모달 -->
            <NotificationModal
                :is-open="isNotificationModalOpen"
                :token="userStore.token"
                class="absolute right-0 mt-2 top-full"
                @close="closeNotificationModal"
            />
        </div>
    </header>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { hasUnread } from '@/helpers/notification'

import CategoryIcon from '@/assets/icons/category.svg'
import CategoryDropDown from '@/components/Common/CategoryDropDown.vue'
import SearchIcon from '@/assets/icons/search.svg'
import UserSearchIcon from '@/assets/icons/user_search.svg'
import BellIcon from '@/assets/icons/bell.svg'
import BellNotificationIcon from '@/assets/icons/bell_notification.svg'
import NotificationModal from '@/components/Common/NotificationModal.vue'
import UserSearchModal from '@/components/Common/UserSearchModal.vue'

// Props 정의
defineProps({
    type: {
        type: String,
        required: false,
        default: 'lecture',
    },
})

// 상태 관리 변수
const searchQuery = ref('')
const isCategoryDropdownVisible = ref(false)
const isNotificationModalOpen = ref(false)
const hasNotifications = ref(false)
const isUserSearchModalOpen = ref(false)
const isClicked = ref(false)

const userStore = useUserStore()
const router = useRouter()

// 카테고리 드롭다운 토글
const toggleCategoryDropdown = () => {
    isCategoryDropdownVisible.value = !isCategoryDropdownVisible.value
}

// // 카테고리 드롭다운 닫기
// const closeCategoryDropdown = () => {
//     isCategoryDropdownVisible.value = false
// }

// 카테고리 드롭다운 닫기 (카테고리 선택 시 검색어 초기화)
const closeCategoryDropdown = () => {
    isCategoryDropdownVisible.value = false
    searchQuery.value = '' // 카테고리 선택 시 검색어 초기화
}

// 사용자 검색 모달 열기
const openUserSearchModal = () => {
    isUserSearchModalOpen.value = true
    console.log('사용자 모달 열림')
}

// 사용자 검색 모달 닫기
const closeUserSearchModal = () => {
    isUserSearchModalOpen.value = false
}

// // 검색 실행 함수 (빈 입력이어도 검색 실행)
// const executeSearch = () => {
//     const trimmedQuery = searchQuery.value.trim()
//     router.push({ path: '/lecture', query: { q: trimmedQuery } })
//     searchQuery.value = ''
// }

// 검색 실행 함수 (검색 후 검색어 유지)
const executeSearch = () => {
    const trimmedQuery = searchQuery.value.trim()
    router.push({ path: '/lecture', query: { q: trimmedQuery } })
}

// 알림 존재 여부 확인 후 아이콘 변경
const updateHasUnread = async () => {
    if (!userStore.token) return

    try {
        const response = await hasUnread(userStore.token)
        hasNotifications.value = response.data
        console.log('🔔 읽지 않은 알림 존재 여부:', response.data)
    } catch (error) {
        console.error('❌ 읽지 않은 알림 존재 여부 확인 실패:', error)
    }
}

// 알림 모달 열기
const openNotificationModal = () => {
    hasNotifications.value = false
    isNotificationModalOpen.value = true
}

// 알림 모달 닫기
const closeNotificationModal = () => {
    isNotificationModalOpen.value = false
}

// 알림 모달 열기 애니메이션
const handleClick = () => {
    isClicked.value = true
    setTimeout(() => {
        isClicked.value = false // 0.2초 후 원래 위치로 복귀
    }, 200)
}

// 카테고리 드롭다운 외부 클릭 감지를 위한 ref
const categoryContainer = ref(null)

// 외부 클릭 이벤트 핸들러
const handleClickOutside = (event) => {
    if (
        isCategoryDropdownVisible.value &&
        categoryContainer.value &&
        !categoryContainer.value.contains(event.target)
    ) {
        closeCategoryDropdown()
    }
}

// 컴포넌트 마운트 시 외부 클릭 이벤트 등록
onMounted(() => {
    document.addEventListener('click', handleClickOutside)
    updateHasUnread()
})

// 컴포넌트 언마운트 시 이벤트 제거
onUnmounted(() => {
    document.removeEventListener('click', handleClickOutside)
})

watch(
    () => userStore.token,
    () => {
        updateHasUnread()
    }
)
</script>

<style scoped></style>
