<template>
    <div v-if="userStore.user">
        <div v-if="ProfileData" class="flex relative flex-col gap-y-8 min-w-[1150px]">
            <div class="flex justify-center col-span-12 gap-7 pb-11">
                <div class="p-3 w-fit h-fit">
                    <img
                        :src="ProfileData.imageUrl"
                        alt="이미지"
                        class="bg-gray-200 w-[144px] h-[144px] rounded-full border border-gray-200"
                    />
                </div>
                <div class="flex flex-col w-[760px] gap-8 px-8 py-4">
                    <div class="flex justify-between w-full">
                        <div class="flex flex-col flex-1 gap-2 h-fit">
                            <p class="flex items-center h-8 text-h3">{{ ProfileData.profileId }}</p>
                            <p class="flex items-center h-6 text-body">
                                {{ ProfileData.nickname }}
                            </p>
                        </div>
                        <div class="flex flex-col gap-2">
                            <div class="flex gap-3">
                                <div class="flex items-center w-[378px] h-[32px] gap-6">
                                    <div class="flex items-center gap-2">
                                        <p class="text-gray-400 text-caption">북마크한 강의 수</p>
                                        <p class="text-body-bold">
                                            {{
                                                ProfileData.bookmarkCnt > 99
                                                    ? '99+'
                                                    : ProfileData.bookmarkCnt
                                            }}
                                        </p>
                                    </div>
                                    <div
                                        class="flex items-center gap-2 cursor-pointer"
                                        @click="openModal('follower')"
                                    >
                                        <p class="text-gray-400 text-caption">팔로워</p>
                                        <p class="text-body-bold">
                                            {{
                                                ProfileData.followerCnt > 99
                                                    ? '99+'
                                                    : ProfileData.followerCnt
                                            }}
                                        </p>
                                    </div>
                                    <div
                                        class="flex items-center gap-2 cursor-pointer"
                                        @click="openModal('following')"
                                    >
                                        <p class="text-gray-400 text-caption">팔로잉</p>
                                        <p class="cursor-pointer text-body-bold">
                                            {{
                                                ProfileData.followingCnt > 99
                                                    ? '99+'
                                                    : ProfileData.followingCnt
                                            }}
                                        </p>
                                    </div>
                                    <FollowerFollowingModal
                                        v-if="isModalOpen"
                                        :type="modalType"
                                        :users="modalType === 'follower' ? followers : followings"
                                        :user-id="route.params.id"
                                        :isOpen="isModalOpen"
                                        @close="isModalOpen = false"
                                    />
                                </div>
                                <button
                                    v-if="ProfileData?.followStatus !== null"
                                    :class="{
                                        'button-primary':
                                            ProfileData?.followStatus === 'NOTFOLLOWING',
                                        'button-gray': ProfileData?.followStatus === 'FOLLOWING',
                                        'button-gray cursor-default':
                                            ProfileData?.followStatus === 'PENDING',
                                    }"
                                    @click="
                                        handleFollowClick(route.params.id, ProfileData.followId)
                                    "
                                >
                                    {{
                                        ProfileData?.followStatus === 'NOTFOLLOWING'
                                            ? '팔로우'
                                            : ProfileData?.followStatus === 'FOLLOWING'
                                              ? '팔로우 취소'
                                              : ProfileData?.followStatus === 'PENDING'
                                                ? '요청 대기중'
                                                : ''
                                    }}
                                </button>
                            </div>
                            <div
                                v-if="ProfileData?.links?.url"
                                class="flex gap-[6px] items-center h-6 text-gray-400 cursor-pointer text-caption"
                            >
                                <Link class="w-4 h-4 text-gray-400" />
                                <a v-if="ProfileData?.links?.url" :href="ProfileData.links.url">
                                    {{ ProfileData.links.title }}
                                </a>
                            </div>
                        </div>
                    </div>
                    <!-- Tag Section -->
                    <div class="flex gap-1.5 w-full">
                        <div
                            v-for="tag in (ProfileData?.tags || '').split(',')"
                            :key="tag"
                            class="inline-flex gap-1 text-caption-sm tag-gray"
                        >
                            <p>#</p>
                            <p
                                class="overflow-hidden cursor-default text-ellipsis whitespace-nowrap"
                                :title="tag"
                            >
                                {{ tag }}
                            </p>
                        </div>
                    </div>
                </div>
            </div>
            <ProfileContribution v-if="userToken && isProfileVisible" :user-id="route.params.id" />
            <TodoList
                v-if="userToken && isProfileVisible"
                :user-id="route.params.id"
                :follow-status="ProfileData.followStatus"
                @open-add-modal="isAddModalOpen = true"
            />

            <!-- 할 일 추가하기 모달 (TodoList 아래에 위치) -->

            <TodoAddModal
                v-if="isAddModalOpen && userToken && isProfileVisible"
                :user-id="route.params.id"
                @close="isAddModalOpen = false"
            />

            <div v-if="userToken && isProfileVisible" class="border border-gray-200 rounded-[20px]">
                <TabMenu v-model="selectedTab" tab-left="북마크한 강의" tab-right="작성한 리뷰" />
                <KanbanSection
                    v-if="userToken && userData && selectedTab === 'left'"
                    :user-id="route.params.id"
                    @closeModal="handleCloseModal"
                />
                <ProfileReviewSection
                    v-if="userToken && userData && selectedTab === 'right'"
                    :user-id="route.params.id"
                    :reviews="userReviews.content || []"
                    @edit-review="openReviewModal"
                    @delete-review="deleteReview"
                />
                <ProfileReviewEditModal
                    v-if="isReviewModalOpen"
                    :review="reviewForEdit"
                    @close-modal="handleCloseModal"
                    @close="isReviewModalOpen = false"
                    @update-reviews="loadUserReviews(userStore.token, userStore.userId)"
                />
            </div>
        </div>
    </div>
</template>

<script setup>
import Link from '@/assets/icons/link.svg'
import TabMenu from '@/components/Common/TabMenu.vue'
import FollowerFollowingModal from '@/components/Profile/FollowerFollowingModal.vue'
import ProfileReviewEditModal from '@/components/Profile/ProfileReviewEditModal.vue'
import ProfileContribution from '@/components/Profile/ProfileContribution.vue'
import KanbanSection from '@/components/Profile/KanbanSection.vue'
import TodoAddModal from '@/components/Profile/TodoAddModal.vue'
import TodoList from '@/components/Profile/TodoList.vue'
import ProfileReviewSection from '@/components/Profile/ProfileReviewSection.vue'
import { deleteLectureReview } from '@/helpers/lecture'
import { useUserStore } from '@/stores/user'
import { useRoute } from 'vue-router' // ✅ useRoute 훅 불러오기
import { ref, computed, watch, watchEffect } from 'vue'
import { sendFollowRequest, cancelFollowRequest } from '@/helpers/follow'
import { getUserDatas, getUserReviews } from '@/helpers/profile'

const userStore = useUserStore() // Pinia 스토어 가져오기
const route = useRoute() // ✅ 라우트 정보 가져오기

defineProps({
    reviews: {
        type: Array,
        required: false, // 필수가 아니게 설정
        default: () => [], // 기본값을 빈 배열로 설정
    },
})

//===============================================
// 팔로워/팔로잉 모달 관련 API
//===============================================
const isModalOpen = ref(false)
const modalType = ref(null) // 초기값 follower

const openModal = (type) => {
    modalType.value = type
    isModalOpen.value = true
    // console.log(modalType.value)
}

//===============================================
// 팔로워/팔로잉 요청 API
//===============================================

// 팔로우 버튼 클릭시 요청 함수
const handleFollowClick = async (userId, followId) => {
    try {
        if (ProfileData.value.followStatus === 'NOTFOLLOWING') {
            console.log('팔로우 요청 중...')
            const response = await sendFollowRequest(userStore.token, userId) // 팔로우 요청 함수 호출

            ProfileData.value.followId = response.data.followId // followId 저장
            if (ProfileData.value.isPublic) {
                ProfileData.value.followStatus = 'FOLLOWING' // 상태 업데이트
            } else {
                ProfileData.value.followStatus = 'PENDING' // 상태 업데이트
            }
        } else if (ProfileData.value.followStatus === 'FOLLOWING') {
            console.log('팔로우 취소 요청 중...')
            await cancelFollowRequest(userStore.token, followId) // 팔로우 취소 함수 호출
            ProfileData.value.followStatus = 'NOTFOLLOWING' // 상태 업데이트
        }
    } catch (error) {
        console.error('❌ 팔로우/취소 요청 중 오류 발생:', error)
    }
}

//===============================================
// 프로필 데이터 관련 API
//===============================================

// 사용자 정보 가져오기
const userId = computed(() => userStore.userId)
const userData = computed(() => userStore.user)
const userToken = computed(() => userStore.token)
const isLoaded = ref(false)

const ProfileData = ref({})
const isMyProfile = ref(false)

// 프로필 데이터 불러오기
const loadProfileDatas = async () => {
    try {
        const response = await getUserDatas(userStore.token, route.params.id)
        ProfileData.value = response.data
        console.log(ProfileData.value)
    } catch (error) {
        console.error('❌ 팔로워 정보 에러 발생:', error)
    }
}

// 프로필 페이지 렌더링 조건
const isProfileVisible = ref(false)

watchEffect(() => {
    if (ProfileData.value) {
        isProfileVisible.value =
            isMyProfile.value ||
            ProfileData.value.isPublic ||
            (!ProfileData.value.isPublic && ProfileData.value.followStatus === 'FOLLOWING')

        // console.log('📌 프로필 보이는 상태:', isProfileVisible.value)
        // console.log('내프로필인가?', isMyProfile.value)
        // console.log('공개계정인가??', ProfileData.value.isPublic)
        // console.log('팔로잉관계가 뭐임??', ProfileData.value.followStatus)
        // console.log('볼 수 있나?', isProfileVisible.value)
    }
})

// 사용자 정보와 토큰의 상태 변화를 감지
watch(
    () => [userData.value, userToken.value, userId.value, route.params.id], // ✅ 여러 값을 동시에 감시
    async ([newUser, newToken, newUserId, newId]) => {
        if (newUser && newToken && newUserId && newId) {
            // console.log('✅ 사용자 정보와 토큰이 준비되었습니다.')
            // console.log('유저데이터:', newUser)
            isMyProfile.value = newUserId === newId // ✅ 여기서 isMyProfile 설정
            // console.log('유저토큰:', newToken)
            // console.log('유저아이디:', newUserId)

            if (!isLoaded.value) {
                await loadProfileDatas() // ✅ 토큰을 전달해서 데이터 로드
            }
        }
    },
    { immediate: true } // ✅ 초기값도 확인
)

//===============================================
// 투두 관련 API
//===============================================
const isAddModalOpen = ref(false) // TodoAddModal 상태 관리

//===============================================
// 칸반, 리뷰 관련 API
//===============================================
const selectedTab = ref('left') // TabMenu 관리, 기본값: 칸반 섹션

// 프로필 리뷰 모달
// 모달 열기
const isReviewModalOpen = ref(false)

const reviewForEdit = ref(null) // 수정할 리뷰 데이터

const openReviewModal = (review) => {
    reviewForEdit.value = review // 수정할 리뷰 데이터 저장
    isReviewModalOpen.value = true // 모달 열기
}

const handleCloseModal = () => {
    isReviewModalOpen.value = false // 모달 닫기
    console.log('모달 닫기 이벤트가 ProfilePage에서 처리되었습니다.')
    console.log(reviewForEdit.value)
}

const userReviews = ref([])
const loadUserReviews = async () => {
    try {
        const response = await getUserReviews(userStore.token, route.params.id)
        userReviews.value = response.data
    } catch (error) {
        console.error('에러:', error)
    }
}

// 리뷰 삭제
const deleteReview = async (review) => {
    const isConfirmed = window.confirm('리뷰를 삭제하시겠습니까?')
    console.log('삭제하려는 리뷰 ID:', review) // 확인용 로그
    if (isConfirmed) {
        try {
            await deleteLectureReview(userStore.token, review.id)
            console.log('✅ 리뷰 삭제 성공')
            alert('리뷰가 삭제되었습니다.')
            loadUserReviews(userStore.token, userStore.userId)
        } catch (error) {
            console.error('❌ 리뷰 삭제 중 오류 발생:', error)
            alert('삭제에 실패했습니다. 나중에 다시 시도해주세요.')
        }
    }
}

watch(
    () => [userStore.token, userStore.userId], // ✅ 두 값을 동시에 감시
    async ([newToken, newUserId]) => {
        if (newToken && newUserId) {
            await loadUserReviews(newToken, newUserId)
        }
    },
    { immediate: true } // 이미 값이 존재할 경우 즉시 실행
)

// const followers = ref([]) // 팔로워 목록
</script>

<style scoped></style>
<style scoped></style>
