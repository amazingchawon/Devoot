<template>
    <div
        v-if="lecture"
        class="flex w-full h-20 overflow-hidden bg-white border border-gray-200 rounded-lg"
    >
        <!-- Thumbnail Container -->
        <div class="w-[7.5rem] h-full bg-gray-300 flex-shrink-0 relative">
            <img
                :src="lecture.imgUrl"
                alt="강의 썸네일"
                class="w-full h-full"
                @click="closeModal"
            />
        </div>

        <!-- Info Container -->
        <div class="flex flex-col w-full h-full px-3 py-2">
            <!-- Title Section -->
            <div class="flex items-center justify-between w-full gap-x-0.5">
                <div class="flex flex-col justify-center w-full h-full" @click="closeModal">
                    <p class="text-gray-400 text-caption-sm">{{ lecture.sourceName }}</p>
                    <p class="text-black cursor-pointer text-overflow text-body">
                        {{ lecture.name }}
                    </p>
                </div>
                <!-- 관심 강의 추가 -->
                <div @click="toggleBookmark">
                    <component
                        :is="isBookmarked ? BookmarkFill : BookmarkDefault"
                        class="w-6 h-6 cursor-pointer text-primary-500"
                    />
                </div>
            </div>
            <div class="flex-1"></div>
            <!-- Tag Section -->
            <div v-if="lecture.tags" class="flex gap-1.5 w-full" @click="closeModal">
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
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { addBookmark, removeBookmark } from '@/helpers/lecture' // API 함수 가져오기

import BookmarkFill from '@/assets/icons/bookmark_filled.svg'
import BookmarkDefault from '@/assets/icons/bookmark_default.svg'

// 사용자 token 및 profileId(userId)를 가져오기 위해 store 사용
const userStore = useUserStore()

const props = defineProps({
    lecture: {
        type: Object,
        required: true,
    },
    lectureIdInt: {
        type: Number,
        default: null,
    },
})

//===========================
// 북마크 관련
//===========================
const isBookmarked = ref(props.lecture?.isBookmarked || false)
const bookmarkId = computed(() => props.lecture.bookmarkId || null)

// props.lecture가 변경될 때만 isBookmarked를 업데이트
watch(
    () => props.lecture?.isBookmarked,
    (newValue) => {
        if (newValue !== undefined) {
            isBookmarked.value = newValue
        }
    }
)

const toggleBookmark = async () => {
    console.log('버튼클릭됨!!!!!')
    if (userStore.token) {
        console.log('토큰 있음')
    }
    try {
        if (isBookmarked.value) {
            // api 요청
            await removeBookmark(userStore.token, userStore.userId, bookmarkId.value)
            console.log('북마크 제거 완료')
        } else {
            // api 요청
            await addBookmark(userStore.token, userStore.userId, props.lectureIdInt)
            console.log('북마크 추가 완료')
        }
        isBookmarked.value = !isBookmarked.value
    } catch (error) {
        console.error('API 요청 실패', error)
    }
}

//===========================
// 강의 상세 페이지로 이동 (모달 닫기)
//===========================
const emit = defineEmits(['closeModal']) // 부모로 이벤트 전달

const closeModal = () => {
    emit('closeModal') // 부모 컴포넌트에서 모달 닫기 실행
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
