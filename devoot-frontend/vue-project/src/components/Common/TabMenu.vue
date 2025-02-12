<template>
    <div class="relative grid col-12">
        <div class="flex flex-row items-center border-b border-gray-200 h-11">
            <!-- 왼쪽 탭 -->
            <div class="flex items-center justify-center flex-1 h-full">
                <div
                    class="relative flex items-center justify-center h-full cursor-pointer text-h3"
                    :class="{ 'text-primary-500': modelValue === 'left' }"
                    @click="$emit('update:modelValue', 'left')"
                >
                    <span>{{ tabLeft }}</span>
                    <div
                        v-if="modelValue === 'left'"
                        class="absolute bottom-[-1px] w-full h-[2px] bg-primary-500"
                    ></div>
                </div>
            </div>
            <!-- 중앙 구분선 -->
            <div class="h-full border border-l border-gray-200"></div>
            <!-- 오른쪽 탭 -->
            <div class="flex items-center justify-center flex-1 h-full">
                <div
                    class="relative flex items-center justify-center h-full cursor-pointer text-h3"
                    :class="{ 'text-primary-500': modelValue === 'right' }"
                    @click="$emit('update:modelValue', 'right')"
                >
                    <span>{{ tabRight }}</span>
                    <div
                        v-if="modelValue === 'right'"
                        class="absolute bottom-[-1px] w-full h-[2px] bg-primary-500"
                    ></div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
// 부모 컴포넌트에서 받을 props
defineProps({
    tabLeft: {
        type: String,
        required: true,
    },
    tabRight: {
        type: String,
        required: true,
    },
    modelValue: {
        type: String,
        required: true,
    },
})

// 부모 컴포넌트에 이벤트 전달
defineEmits(['update:modelValue'])
</script>

<style scoped></style>
