<template>
    <div
        id="detail-header"
        class="flex w-full overflow-hidden border border-gray-200 h-fit rounded-2xl"
    >
        <!-- 강의 사진 -->
        <div id="lecture-image" class="w-[50%] min-w-[300px]">
            <img
                :src="
                    imageUrl ||
                    'https://devoot-profile-image.s3.ap-northeast-2.amazonaws.com/profile/default_image.png'
                "
                alt="강의 이미지"
                class="object-cover h-full"
            />
        </div>

        <!-- 강의 설명 -->
        <div
            id="lecture-detail"
            class="w-[50%] min-w-[200px] px-8 py-6 bg-white flex flex-col gap-3"
        >
            <div class="flex flex-col gap-3">
                <!-- 플랫폼/강사 명 -->
                <div class="flex flex-row text-body">
                    <div id="lecture-platform" class="flex felx-row gap-[2px] items-center">
                        <p id="platform-name">{{ platformName }}</p>
                        <a
                            :href="props.lecture.sourceUrl"
                            target="_blank"
                            rel="noopener noreferrer"
                            class="relative group"
                        >
                            <LinkExternal class="w-4 h-4 text-gray-300" />

                            <!-- 오른쪽에 툴팁 배치 -->
                            <span
                                class="absolute px-2 py-1 ml-2 text-xs text-white transition-opacity -translate-y-1/2 bg-gray-800 rounded opacity-0 left-full top-1/2 group-hover:opacity-100 whitespace-nowrap"
                            >
                                외부 사이트로 이동
                            </span>
                        </a>
                    </div>
                    <div class="flex-1"></div>
                    <div id="lecturer">{{ lecturer }}</div>
                </div>
                <!-- 제목 -->
                <div class="relative w-full group">
                    <div id="lecture-title" class="text-h3 lg:text-h1 line-clamp-3">
                        {{ title }}
                    </div>
                    <!-- 툴팁 -->
                    <div
                        class="absolute left-0 top-full mt-2 hidden w-[300px] rounded-lg bg-black px-3 py-2 text-white text-caption shadow-lg group-hover:block"
                    >
                        {{ title }}
                    </div>
                </div>
                <!-- 별점 -->
                <div id="rating" class="flex flex-row items-center gap-1">
                    <Star class="w-6 h-6 text-yellow-300" />
                    <p class="text-body-bold">{{ rating }}</p>
                </div>
                <!-- 태그 -->
                <!-- 태그 -->
                <div class="flex flex-row items-center gap-1">
                    <div v-for="(tag, index) in tagList" :key="index" class="tag-gray">
                        # {{ tag.trim() }}
                    </div>
                </div>
            </div>
            <div class="flex-1"></div>
            <div class="flex flex-col gap-3">
                <!-- 가격 정보 -->
                <div id="price-section" class="flex flex-col items-end gap-1">
                    <!-- 할인 중일 때만 원래 가격 (취소선) 표시 -->
                    <p v-if="showOriginalPrice" class="text-gray-300 line-through text-body">
                        ₩ {{ formattedOriginalPrice }}
                    </p>

                    <div class="flex flex-row gap-2 text-h3">
                        <p v-if="isDiscounted" class="text-red-500">할인중</p>
                        <p>{{ formattedCurrentPrice }}</p>
                    </div>
                </div>
                <!-- 관심 강의 추가 -->
                <button
                    class="flex flex-row w-full gap-2 px-4 py-4 border border-gray-200 rounded-lg"
                    @click="toggleBookmark"
                >
                    <component
                        :is="isBookmarked ? BookmarkFill : BookmarkDefault"
                        class="w-6 h-6 text-primary-500"
                    />
                    <p>{{ isBookmarked ? '관심 강의에서 제거하기' : '관심 강의에 추가하기' }}</p>
                </button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { addBookmark, removeBookmark } from '@/helpers/lecture'
import { useUserStore } from '@/stores/user'

import LinkExternal from '@/assets/icons/link_external.svg'
import Star from '@/assets/icons/star_filled.svg'
import BookmarkDefault from '@/assets/icons/bookmark_default.svg'
import BookmarkFill from '@/assets/icons/bookmark_filled.svg'

const props = defineProps({
    lecture: {
        type: Object,
        required: true,
    },
})

// ✅ lecture를 반응형 상태로 관리
const lectureData = ref(props.lecture)

// ✅ props.lecture가 변경될 때 lectureData 업데이트
watch(
    () => props.lecture,
    (newLecture) => {
        console.log('🔄 lecture 변경 감지, 업데이트 실행')
        lectureData.value = newLecture
    },
    { deep: true, immediate: true } // ✅ lecture 내부 속성까지 감지 + 즉시 실행
)

//===========================
// API 호출로 받아올 데이터 : lectureData를 활용한 computed 값 설정
//===========================
const imageUrl = computed(() => lectureData.value.imgUrl || null)
const platformName = computed(() => lectureData.value.sourceName || '알 수 없음')
const lecturer = computed(() => lectureData.value.lecturer || '강사 정보 없음')
const title = computed(() => lectureData.value.name || '제목 없음')
const rating = computed(() => lectureData.value.rating?.toFixed(1) || '0.0')
const tagList = computed(() => (lectureData.value.tags ? lectureData.value.tags.split(',') : []))

//===========================
// 가격 관련
//===========================
// 가격
const originalPrice = computed(() => lectureData.value.originPrice || 0)
const currentPrice = computed(() => lectureData.value.currentPrice || 0)
// 할인 중 여부 (originalPrice와 currentPrice가 다를 때)
const isDiscounted = computed(
    () => originalPrice.value > 0 && originalPrice.value !== currentPrice.value
)

// 원래 가격을 표시할지 여부
const showOriginalPrice = computed(() => isDiscounted.value)

// 1000 단위 콤마 추가된 가격 문자열을 computed에서 생성
const formattedOriginalPrice = computed(() => originalPrice.value.toLocaleString())

// 가격 텍스트 (무료 or 할인 가격)
const formattedCurrentPrice = computed(() => {
    if (originalPrice.value === 0 || currentPrice.value === 0) {
        return '무료'
    }
    return `₩ ${currentPrice.value.toLocaleString()}`
})

//===========================
// 북마크 관련
//===========================
const userStore = useUserStore()

// ✅ 북마크 상태 로컬 관리
const isBookmarked = ref(lectureData.value.isBookmarked || false)
const bookmarkId = ref(lectureData.value.bookmarkId || null)

// ✅ props 값 변경 시 동기화
watch(
    () => lectureData.value,
    (newLecture) => {
        isBookmarked.value = newLecture.isBookmarked || false
        bookmarkId.value = newLecture.bookmarkId || null
    },
    { deep: true, immediate: true }
)

const toggleBookmark = async () => {
    try {
        if (!isBookmarked.value) {
            const response = await addBookmark(
                userStore.token,
                userStore.userId,
                lectureData.value.id
            )
            bookmarkId.value = response.data.id
            console.log('✅ 북마크 추가 성공:', bookmarkId.value)
        } else {
            if (bookmarkId.value) {
                await removeBookmark(userStore.token, userStore.userId, bookmarkId.value)
                console.log('✅ 북마크 삭제 성공')
                bookmarkId.value = null
            }
        }
        // 북마크 상태 토글
        isBookmarked.value = !isBookmarked.value
    } catch (error) {
        console.error('❌ 북마크 토글 실패:', error)
    }
}
</script>

<style scoped></style>
