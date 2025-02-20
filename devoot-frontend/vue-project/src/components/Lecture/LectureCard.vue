<!-- src/components/Lecture/LectureCard.vue -->
<template>
    <!-- 카드 컨테이너: 고정 너비(w-[16.875rem])와 고정 높이(h-80)를 유지 -->
    <!-- 전체 카드 클릭 시 goToDetailPage 함수 호출 -->
    <div
        @click="goToDetailPage"
        class="w-[16.875rem] h-80 bg-white border-[0.063rem] border-gray-100 rounded-[1.25rem] relative flex flex-col cursor-pointer"
    >
        <!-- 강의 썸네일 -->
        <div class="w-full h-[9.5rem] bg-gray-200 rounded-t-[1.25rem]">
            <img
                :src="imageUrl"
                alt="Lecture Thumbnail"
                class="w-full h-full object-cover rounded-t-[1.25rem]"
            />
        </div>

        <!-- 강의 정보 -->
        <div class="flex-1 px-4 my-3">
            <!-- 플랫폼 및 강사명 -->
            <div class="flex items-center justify-between mb-1 text-gray-300 text-caption">
                <span>{{ lecturer }}</span>
                <!-- sourceUrl이 있을 경우 a 태그로 감싸 클릭 시 이동 (클릭 시 부모 클릭 이벤트 전파 방지) -->
                <template v-if="sourceUrl">
                    <a
                        @click.stop
                        :href="sourceUrl"
                        target="_blank"
                        rel="noopener noreferrer"
                        class="flex flex-row items-center cursor-pointer"
                    >
                        <span class="text-caption">{{ platform }}</span>
                        <LinkIcon class="w-3 h-3 ml-1 text-gray-300" />
                    </a>
                </template>
                <!-- sourceUrl이 없으면 일반 div로 표시 -->
                <template v-else>
                    <div class="flex flex-row items-center">
                        <span class="text-caption">{{ platform }}</span>
                        <LinkIcon class="w-3 h-3 ml-1 text-gray-300" />
                    </div>
                </template>
            </div>

            <!-- 강의 제목 -->
            <p
                class="mb-1 leading-tight text-black text-body-bold"
                style="
                    height: 2.125rem;
                    overflow: hidden;
                    display: -webkit-box;
                    -webkit-line-clamp: 2;
                    -webkit-box-orient: vertical;
                    text-overflow: ellipsis;
                "
            >
                {{ name }}
            </p>

            <!-- 별점 및 리뷰 수 -->
            <div class="flex items-center mb-1 text-black text-caption">
                <StarFilledIcon class="w-4 h-4 mr-1 text-[#FDE03A]" />
                <span>{{ rating }}</span>
                <ReviewIcon class="w-4 h-4 ml-3 mr-1 text-gray-300" />
                <span>{{ reviewCount }}</span>
            </div>

            <!-- 가격 정보 -->
            <div class="text-right">
                <!-- 원래 가격 (취소선) -->
                <span
                    v-if="isDiscounted && !isFree"
                    class="block text-gray-300 line-through text-caption"
                >
                    ₩{{ formatPrice(originalPrice) }}
                </span>

                <!-- 할인중 / 무료 / 정상가 표시 -->
                <div v-if="isDiscounted" class="text-red-500 text-body-bold">
                    할인중 <span class="ml-2 text-black">₩{{ formatPrice(currentPrice) }}</span>
                </div>
                <div v-else-if="isFree" class="text-red-500 text-body-bold">
                    무료 <span class="ml-2 text-black">₩0</span>
                </div>
                <div v-else class="text-black text-body-bold">₩{{ formatPrice(currentPrice) }}</div>
            </div>

            <!-- 태그 리스트 -->
            <div class="flex gap-[4px] mt-1">
                <span
                    v-for="(tag, index) in limitedTags"
                    :key="index"
                    class="overflow-hidden cursor-pointer tag-gray text-ellipsis whitespace-nowrap"
                    :title="tag"
                >
                    #{{ tag }}
                </span>
            </div>
        </div>
    </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import StarFilledIcon from '@/assets/icons/star_filled.svg'
import ReviewIcon from '@/assets/icons/review.svg'
import LinkIcon from '@/assets/icons/link_external.svg'

// props 정의
const props = defineProps({
    id: { type: Number, required: true },
    name: { type: String, required: true },
    lecturer: { type: String, required: true },
    platform: { type: String, default: '인프런' },
    imageUrl: { type: String, required: true },
    tags: { type: Array, default: () => [] },
    currentPrice: { type: Number, required: true },
    originalPrice: { type: Number, required: true },
    rating: { type: Number, default: 0 },
    reviewCount: { type: Number, default: 0 },
    isBookmarked: { type: Boolean, default: false },
    sourceUrl: { type: String, default: '' },
})

const isDiscounted = computed(
    () => props.currentPrice !== props.originalPrice && props.currentPrice > 0
)
const isFree = computed(() => props.currentPrice === 0)
const limitedTags = computed(() => props.tags.slice(0, 3))

function formatPrice(price) {
    return price.toLocaleString()
}

// 라우터 인스턴스 사용
const router = useRouter()

// 강의 카드 클릭 시 LectureDetailPage로 이동 (라우터 네임 및 파라미터는 실제 라우터 설정에 맞게 조정)
function goToDetailPage() {
    // 예를 들어, /lecture/9 형태의 경로로 이동
    router.push(`/lecture/${props.id}`)
}
</script>

<style scoped></style>
