<template>
    <div
        v-if="lecture"
        class="flex w-full h-20 overflow-hidden bg-white border border-gray-200 rounded-lg"
    >
        <!-- Thumbnail Container -->
        <div class="w-[7.5rem] h-full bg-gray-300 flex-shrink-0 relative">
            <img
                :src="
                    lecture.imgUrl ||
                    'https://devoot-profile-image.s3.ap-northeast-2.amazonaws.com/profile/default_image.png'
                "
                alt="강의 썸네일"
                class="w-full h-full"
                @click="closeModal"
            />
        </div>

        <!-- Info Container -->
        <div class="flex flex-col w-full h-full px-3 py-2 cursor-pointer">
            <!-- Title Section -->
            <div class="flex flex-col justify-center w-full h-full" @click="closeModal">
                <p class="text-gray-400 text-caption-sm">{{ lecture.sourceName }}</p>
                <p
                    class="w-full max-w-md min-w-0 overflow-hidden text-black cursor-default text-ellipsis whitespace-nowrap"
                    :title="lecture.name"
                >
                    {{ lecture.name }}
                </p>
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
                        class="overflow-hidden cursor-default text-ellipsis whitespace-nowrap"
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
defineProps({
    lecture: {
        type: Object,
        required: true,
    },
})

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
