<template>
    <div class="col-span-4 overflow-hidden border-gray-200 lg:col-span-3 h-80 rounded-2xl">
        <!-- 강의 썸네일 (고정된 높이 설정) -->
        <div class="w-full h-40 bg-gray-200">
            <img
                :src="lecture.imageUrl"
                alt="Lecture Thumbnail"
                class="object-cover w-full h-full"
            />
        </div>

        <!-- 강의 정보 -->
        <div class="flex flex-col flex-1 gap-2 px-4 py-3 bg-white rounded-b-2xl">
            <div class="flex flex-col gap-1">
                <!-- 강의 플랫폼 및 강사명 -->
                <div class="flex items-center justify-between text-caption">
                    <div class="flex flex-row items-center justify-center gap-1">
                        <p class="text-black">{{ lecture.sourceName }}</p>
                        <LinkExternal class="w-3 h-3 text-gray-300" />
                    </div>
                    <span class="text-gray-300">{{ lecture.lecturer }}</span>
                </div>

                <!-- 강의 제목 -->
                <h3 class="text-black truncate text-body-bold">
                    {{ lecture.name }}
                </h3>

                <!-- 별점 -->
                <div class="flex flex-row items-center gap-1">
                    <Star class="w-4 h-4 text-yellow-300" />
                    <span class="text-black text-caption">{{ lecture.rating || '평점 없음' }}</span>
                </div>
            </div>

            <div class="flex flex-col gap-1">
                <!-- 가격 정보 -->
                <div id="price-section" class="flex flex-col items-end gap-1">
                    <p v-if="showOriginalPrice" class="text-gray-300 line-through text-body">
                        ₩ {{ formattedOriginalPrice }}
                    </p>
                    <div class="flex flex-row gap-2 text-body-bold">
                        <p v-if="isDiscounted" class="text-red-500">할인중</p>
                        <p>{{ formattedCurrentPrice }}</p>
                    </div>
                </div>
            </div>

            <!-- 태그 리스트 -->
            <div class="flex flex-wrap gap-1">
                <span
                    v-for="tag in tagList"
                    :key="tag"
                    class="px-2 py-1 text-gray-400 bg-gray-100 rounded-full text-caption"
                >
                    # {{ tag }}
                </span>
            </div>
        </div>
    </div>
</template>

<script setup>
import { computed } from 'vue'
import Star from '@/assets/icons/star_filled.svg'
import LinkExternal from '@/assets/icons/link_external.svg'

const props = defineProps({
    lecture: {
        type: Object,
        default: () => ({}),
    },
})

// 태그 리스트 변환
const tagList = computed(() => props.lecture.tags?.split(',').map((tag) => tag.trim()) || [])

// 가격 관련 계산
const originalPrice = computed(() => props.lecture.originPrice || 0)
const currentPrice = computed(() => props.lecture.currentPrice || 0)
const isDiscounted = computed(
    () => originalPrice.value > 0 && originalPrice.value !== currentPrice.value
)
const showOriginalPrice = computed(() => isDiscounted.value)
const formattedOriginalPrice = computed(() => originalPrice.value.toLocaleString())
const formattedCurrentPrice = computed(() => {
    if (originalPrice.value === 0 || currentPrice.value === 0) {
        return '무료'
    }
    return `₩ ${currentPrice.value.toLocaleString()}`
})
</script>

<style scoped></style>
