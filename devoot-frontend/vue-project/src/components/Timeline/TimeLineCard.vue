<template>
    <div
        v-if="type === 'footprint-added'"
        class="p-6 mb-4 bg-white border border-gray-200 rounded-2xl w-[72rem] flex flex-col gap-4 cursor-pointer"
        @click="goToProfile(profileId)"
    >
        <div class="flex items-center justify-between">
            <div class="flex items-center gap-3">
                <img
                    :src="
                        userImage ||
                        'https://devoot-profile-image.s3.ap-northeast-2.amazonaws.com/profile/default_image.png'
                    "
                    alt="프로필 사진"
                    class="object-cover w-10 h-10 bg-gray-200 rounded-full"
                />

                <p class="flex items-center gap-1 text-body">
                    <span class="text-body-bold">{{ userName }}</span>
                    님이 발자국이 추가했습니다
                    <LogoIcon class="w-4 h-4 text-primary-500" />
                </p>
            </div>
            <span class="text-gray-400 text-caption">
                {{ formattedDate }}
            </span>
        </div>

        <div
            class="flex items-center justify-between h-[4.25rem] px-6 py-3 border border-gray-200 rounded-lg"
        >
            <div class="flex items-center gap-4">
                <p class="text-body-bold">
                    {{ lectureTitle }}
                </p>
                <p class="text-body">-</p>
                <p class="text-body">
                    {{ subLectureName }}
                </p>
            </div>
            <button class="p-2 focus:outline-none" @click.stop="toggleBookmark">
                <component
                    :is="isBookmarked ? BookmarkFill : BookmarkDefault"
                    class="w-6 h-6 text-primary-500"
                />
            </button>
        </div>
    </div>

    <div v-else class="p-6 mb-4 bg-white border border-gray-200 rounded-2xl w-[72rem]">
        <div class="flex items-center justify-between">
            <div class="flex items-center">
                <img
                    :src="
                        userImage ||
                        'https://devoot-profile-image.s3.ap-northeast-2.amazonaws.com/profile/default_image.png'
                    "
                    alt="프로필 사진"
                    class="object-cover w-10 h-10 bg-gray-200 rounded-full"
                />

                <div
                    class="flex items-center ml-3 text-sm text-gray-700 cursor-pointer"
                    @click="goToProfile(profileId)"
                >
                    <div class="text-body-bold">{{ userName }}</div>
                    <div v-if="type === 'lecture-status-change'" class="gap-1">
                        님이
                        <span
                            v-if="beforeStatus"
                            class="h-6 px-2 py-1 bg-gray-100 rounded-full"
                            :class="statusColor(beforeStatus)"
                        >
                            {{ beforeStatus }}
                        </span>
                        <span v-if="beforeStatus" class="text-body-bold"> &gt; </span>
                        <span
                            v-if="afterStatus"
                            class="h-6 px-2 py-1 bg-gray-100 rounded-full"
                            :class="statusColor(afterStatus)"
                        >
                            {{ afterStatus }}
                        </span>
                        강의 상태를 변경하였습니다.
                    </div>
                    <div v-else-if="type === 'new-lecture-interest'">
                        님이 새로운 강의에 관심을 가지기 시작했습니다.
                    </div>
                </div>
            </div>
            <span class="text-gray-400 text-caption">{{ formattedDate }}</span>
        </div>

        <div
            class="flex mt-4 border border-gray-200 cursor-pointer rounded-xl"
            @click="goToLecture(lectureId)"
        >
            <img
                :src="imageUrl"
                alt="강의 썸네일"
                class="object-cover rounded-l-xl w-[16.75rem] h-[10rem]"
            />
            <div class="flex flex-col justify-between w-full h-[10rem] px-6 py-5">
                <div class="flex flex-col justify-between h-full">
                    <!-- 위쪽: 사이트명, 강의제목, 북마크 -->
                    <div class="flex items-center justify-between">
                        <div>
                            <p class="mb-1 text-gray-400 text-body">{{ sourceName }}</p>
                            <p class="text-h3">{{ lectureTitle }}</p>
                        </div>
                        <button class="p-2 focus:outline-none" @click.stop="toggleBookmark">
                            <component
                                :is="isBookmarked ? BookmarkFill : BookmarkDefault"
                                class="w-6 h-6 text-primary-500"
                            />
                        </button>
                    </div>

                    <!-- 아래쪽: 태그 영역 -->
                    <div v-if="tags.length" class="flex space-x-2">
                        <span v-for="(tag, index) in tags" :key="index" class="tag-gray">
                            #{{ tag }}
                        </span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import BookmarkDefault from '@/assets/icons/bookmark_default.svg'
import BookmarkFill from '@/assets/icons/bookmark_filled.svg'
import LogoIcon from '@/assets/icons/logo.svg'
import { addBookmark, removeBookmark } from '@/helpers/lecture'
import { useUserStore } from '@/stores/user'

const props = defineProps({
    profileId: { type: String, required: true, default: '' },
    lectureId: { type: String, required: true, default: '' },
    type: { type: String, required: true },
    userName: { type: String, required: true },
    userImage: {
        type: String,
        default:
            'https://devoot-profile-image.s3.ap-northeast-2.amazonaws.com/profile/default_image.png',
    },
    lectureTitle: { type: String, default: '제목 없음' },
    imageUrl: {
        type: String,
        default:
            'https://devoot-profile-image.s3.ap-northeast-2.amazonaws.com/profile/default_image.png',
    },
    sourceName: { type: String, default: '' },
    tags: { type: Array, default: () => [] },
    beforeStatus: { type: String, default: '' },
    afterStatus: { type: String, default: '' },
    date: { type: String, required: true },
    // 'footprint-added'일 때 하위 강의명
    subLectureName: { type: String, default: '' },
    // 북마크 관련
    isBookmarked: { type: Boolean, default: false },
    bookmarkId: { type: [Number, String], default: null },
})

const router = useRouter()
const userStore = useUserStore()

function goToProfile(profileId) {
    if (!profileId) {
        console.error('❌ 프로필 ID가 없음!')
        return
    }
    router.push(`/profile/${profileId}`)
}

function goToLecture(lectureId) {
    if (!lectureId) {
        console.error('❌ 강의 ID가 없음!')
        return
    }
    router.push(`/lecture/${lectureId}`)
}

function statusColor(status) {
    switch (status) {
        case '수강 전':
            return 'text-gray-400'
        case '수강 중':
            return 'text-[#0EDB8C]'
        case '수강 완료':
            return 'text-primary-500'
        default:
            return ''
    }
}

const formattedDate = computed(() => {
    return new Date(props.date).toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
    })
})

// 북마크 관련
const isBookmarked = ref(props.isBookmarked)
const bookmarkId = ref(props.bookmarkId)

async function fetchBookmarkStatus() {
    try {
        const response = await axios.get(`/api/users/${userStore.userId}/bookmarks`, {
            headers: { Authorization: `Bearer ${userStore.token}` },
        })
        const todo = response.data.todo || []
        const inProgress = response.data['in-progress'] || []
        const done = response.data.done || []
        const allBookmarks = [...todo, ...inProgress, ...done]

        const found = allBookmarks.find(
            (b) => b.lecture && b.lecture.id.toString() === props.lectureId.toString()
        )
        if (found) {
            isBookmarked.value = true
            bookmarkId.value = found.id
        } else {
            isBookmarked.value = false
            bookmarkId.value = null
        }
    } catch (error) {
        console.error('북마크 상태를 가져오는데 실패:', error)
    }
}

onMounted(() => {
    fetchBookmarkStatus()
})

async function toggleBookmark() {
    try {
        if (!isBookmarked.value) {
            const response = await addBookmark(userStore.token, userStore.userId, props.lectureId)
            bookmarkId.value = response.data.id
            console.log('✅ 북마크 추가 성공:', bookmarkId.value)
        } else {
            if (bookmarkId.value) {
                await removeBookmark(userStore.token, userStore.userId, bookmarkId.value)
                console.log('✅ 북마크 삭제 성공')
                bookmarkId.value = null
            }
        }
        isBookmarked.value = !isBookmarked.value
    } catch (error) {
        console.error('❌ 북마크 토글 실패:', error)
    }
}
</script>
