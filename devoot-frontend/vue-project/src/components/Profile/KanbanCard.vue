<template>
    <div
        v-if="lecture"
        class="bg-white flex w-full h-[6rem] border border-gray-200 rounded-lg overflow-hidden"
    >
        <!-- Thumbnail Container -->
        <div class="w-[7.5rem] h-full bg-gray-300 flex-shrink-0 relative">
            <img :src="lecture.lecture.imgUrl" alt="강의 썸네일" class="w-full h-full" />
            <Move class="absolute w-6 h-6 text-white top-[33.6px]" />
        </div>

        <!-- Info Container -->
        <div class="flex flex-col w-full h-full gap-2 px-3 py-2">
            <!-- Title Section -->
            <div class="flex items-center justify-between w-full h-full gap-x-0.5">
                <div class="flex flex-col justify-center w-full h-full">
                    <p class="text-gray-400 text-caption-sm">{{ lecture.lecture.sourceName }}</p>
                    <p
                        class="text-black cursor-pointer text-overflow text-body"
                        :title="lecture.lecture.name"
                    >
                        {{ lecture.lecture.name }}
                    </p>
                </div>
                <!-- 관심 강의 추가 -->
                <div @click="toggleBookmark(lecture.id)">
                    <component
                        :is="isBookmarked ? BookmarkFill : BookmarkDefault"
                        class="w-6 h-6 cursor-pointer text-primary-500"
                    />
                </div>
            </div>
            <!-- Tag Section -->
            <!-- <div class="flex gap-1.5 w-full">
                <div
                    v-for="tag in lecture.lecture.tags.split(',')"
                    :key="tag"
                    class="inline-flex gap-1 text-caption-sm tag-gray max-w-[60px]"
                >
                    <p>#</p>
                    <p
                        class="overflow-hidden cursor-pointer text-ellipsis whitespace-nowrap"
                        :title="tag"
                    >
                        {{ tag }}
                    </p>
                </div>
            </div> -->
        </div>
    </div>
</template>

<script setup>
import { ref, defineProps } from 'vue'
import { useUserStore } from '@/stores/user'
import { addBookmark, removeBookmark } from '@/helpers/lecture' // API 함수 가져오기

import BookmarkFill from '@/assets/icons/bookmark_filled.svg'
import BookmarkDefault from '@/assets/icons/bookmark_default.svg'
import Move from '@/assets/icons/move.svg'
import { ref, defineProps, watch } from 'vue'
import axios from 'axios'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore() // Pinia 스토어 가져오기
defineProps({
    lecture: {
        type: Object,
        required: true,
    },
})

watch(
    () => [userStore.token, userStore.userId], // ✅ 두 값을 동시에 감시
    async ([newToken, newUserId]) => {
        if (newToken && newUserId) {
            // 두 값이 모두 존재할 때만 실행
            // console.log('✅ 토큰과 userId가 준비되었습니다.')
            // await deleteBookmark(newToken, newUserId)
            // await addBookmark(newToken, newUserId)
        }
    },
    { immediate: true } // 이미 값이 존재할 경우 즉시 실행
)

const isBookmark = ref(true)

const deleteBookmark = async (token, userId, lectureId) => {
    try {
        const mock_server_url = 'http://localhost:8080'
        // const profileId = 'l3olvy' // 여기에 실제 사용자 ID를 넣어야 함
        // const profileId = user.userId // 여기에 실제 사용자 ID를 넣어야 함

        const bookmarkId = lectureId
        const API_URL = `${mock_server_url}/api/users/${userId}}/bookmarks/${bookmarkId}`
        // const token = 'asdfasdfasdf' // 여기에 Bearer 토큰을 넣어야 함

        const response = await axios.delete(API_URL, {
            headers: {
                'Content-Type': 'application/json', //필수 헤더 추가
                Authorization: `Bearer ${token}`, // 토큰 추가
            },
        })
        isBookmark.value = !isBookmark.value
        // console.log('강의', lectureId)
    } catch (error) {
        console.error('에러:', error)
    }
}

const addBookmark = async (token, userId, lectureId) => {
    try {
        const mock_server_url = 'http://localhost:8080'
        // const profileId = 'l3olvy' // 여기에 실제 사용자 ID를 넣어야 함
        // const profileId = userStore.userId // 여기에 실제 사용자 ID를 넣어야 함
        // console.log(profileId)

        const API_URL = `${mock_server_url}/api/users/${userId}}/bookmarks/`
        // const token = 'asdfasdfasdf' // 여기에 Bearer 토큰을 넣어야 함

        const response = await axios.post(
            API_URL,
            {
                lectureId: lectureId,
            },
            {
                headers: {
                    'Content-Type': 'application/json', //필수 헤더 추가
                    Authorization: `Bearer ${token}`, // 필요 시 Bearer 토큰 추가
                },
            }
        )
        isBookmark.value = !isBookmark.value
        // console.log('응답', response)
    } catch (error) {
        console.error('에러:', error)
    }
}
</script>

<style>
.text-overflow {
    text-overflow: ellipsis;
    overflow: hidden;
    word-break: break-word;

    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
}
</style>
