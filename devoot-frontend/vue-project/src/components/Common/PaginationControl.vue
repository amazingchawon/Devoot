<template>
    <div class="flex items-center justify-center gap-2">
        <button :disabled="currentPage === 1" class="navigate-button" @click="changePage(1)">
            <NavigateFirst class="w-4 h-4" />
        </button>
        <button
            :disabled="currentPage === 1"
            class="navigate-button"
            @click="changePage(currentPage - 1)"
        >
            <NavigatePrev class="w-4 h-4" />
        </button>

        <span v-for="page in visiblePages" :key="page">
            <button
                :class="['page-button', { active: page === currentPage }]"
                @click="changePage(page)"
            >
                {{ page }}
            </button>
        </span>

        <button
            :disabled="currentPage === totalPages"
            class="navigate-button"
            @click="changePage(currentPage + 1)"
        >
            <NavigateNext class="w-4 h-4" />
        </button>
        <button
            :disabled="currentPage === totalPages"
            class="navigate-button"
            @click="changePage(totalPages)"
        >
            <NavigateLast class="w-4 h-4" />
        </button>
    </div>
</template>

<script setup>
import { computed } from 'vue'
import NavigateFirst from '@/assets/icons/pagenation_first.svg'
import NavigateLast from '@/assets/icons/pagenation_last.svg'
import NavigatePrev from '@/assets/icons/pagenation_prev.svg'
import NavigateNext from '@/assets/icons/pagnation_next.svg'

const props = defineProps({
    currentPage: {
        type: Number,
        required: true,
    },
    totalPages: {
        type: Number,
        required: true,
    },
})

const emit = defineEmits(['page-changed'])

const changePage = (page) => {
    if (page >= 1 && page <= props.totalPages) {
        emit('page-changed', page)
    }
}

// 표시할 페이지 목록 계산
const visiblePages = computed(() => {
    const pages = []
    const startPage = Math.max(1, props.currentPage - 2)
    const endPage = Math.min(props.totalPages, startPage + 4)

    for (let i = startPage; i <= endPage; i++) {
        pages.push(i)
    }
    return pages
})
</script>

<style scoped>
.navigate-button {
    @apply flex justify-center w-8 h-8 bg-white border border-gray-200 rounded-lg place-items-center disabled:opacity-50 disabled:cursor-not-allowed;
}

.page-button {
    @apply w-8 h-8 border border-gray-200 rounded-lg text-black;
}

.page-button.active {
    @apply bg-blue-500 text-white font-bold;
}
</style>
