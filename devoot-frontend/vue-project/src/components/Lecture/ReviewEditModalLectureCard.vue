<template>
    <div
        v-if="lecture"
        class="bg-white flex w-full h-[6rem] border border-gray-200 rounded-lg overflow-hidden"
    >
        <!-- Thumbnail Container -->
        <div class="w-[7.5rem] h-full bg-gray-300 flex-shrink-0 relative">
            <img :src="lecture.imgUrl" alt="강의 썸네일" class="w-full h-full" />
        </div>

        <!-- Info Container -->
        <div class="flex flex-col w-full h-full gap-2 px-3 py-2">
            <!-- Title Section -->
            <div class="flex items-center justify-between w-full h-full gap-x-0.5">
                <div class="flex flex-col justify-center w-full h-full">
                    <p class="text-gray-400 text-caption-sm">{{ lecture.sourceName }}</p>
                    <p
                        class="text-black cursor-pointer text-overflow text-body"
                        :title="lecture.title"
                    >
                        {{ lecture.title }}
                    </p>
                </div>
                <!-- 관심 강의 추가 -->
                <div @click="toggleBookmark()">
                    <component
                        :is="isBookmarked ? BookmarkFill : BookmarkDefault"
                        class="w-6 h-6 cursor-pointer text-primary-500"
                    />
                </div>
            </div>
            <!-- Tag Section -->
            <!-- <div class="flex gap-1.5 w-full">
                <div
                    v-for="tag in lecture.tags.split(',')"
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
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { addBookmark, removeBookmark } from '@/helpers/lecture' // API 함수 가져오기

import BookmarkFill from '@/assets/icons/bookmark_filled.svg'
import BookmarkDefault from '@/assets/icons/bookmark_default.svg'

const userStore = useUserStore() // Pinia 스토어 가져오기

const props = defineProps({
    lecture: {
        type: Object,
        required: true,
    },
})

// 북마크 관련
// lecture의 isBookmarked 상태를 반응형으로 유지
const isBookmarked = computed(() => props.lecture.isBookmarked)

const toggleBookmark = async (lectureId) => {
    console.log('버튼클릭됨!!!!!')

    try {
        if (isBookmarked.value) {
            // api 요청
            await removeBookmark(userStore.token, userStore.userId, lectureId)
            console.log('북마크 제거 완료')
        } else {
            // api 요청
            await addBookmark(userStore.token, userStore.userId, lectureId)
            console.log('북마크 추가 완료')
        }
        isBookmarked.value = !isBookmarked.value
    } catch (error) {
        console.error('API 요청 실패', error)
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
