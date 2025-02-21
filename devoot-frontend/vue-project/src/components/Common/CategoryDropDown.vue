<template>
    <ul class="bg-white border rounded-lg shadow-lg category-dropdown">
        <li
            v-for="(category, index) in categories"
            :key="index"
            class="flex items-center justify-start w-full px-4 cursor-pointer h-9 text-body hover:bg-gray-100"
            @click="selectCategory(category)"
        >
            {{ category }}
        </li>
    </ul>
</template>

<script setup>
import { useRouter } from 'vue-router'

// 카테고리 목록 정의
const categories = [
    '백엔드',
    '프론트엔드',
    '모바일 앱 개발',
    '프로그래밍 언어',
    '알고리즘/자료구조',
    '데이터베이스',
    '데브옵스/인프라',
    '게임 개발',
    '데이터 사이언스',
    '인공지능',
    '보안/네트워크',
    '하드웨어',
    '디자인/아트',
    '기타',
]

// Vue Router 사용
const router = useRouter()

// 이벤트 방출 정의
const emit = defineEmits(['closeDropdown'])

// // 선택된 카테고리를 처리하는 함수
// const selectCategory = (category) => {
//     // URL 업데이트
//     router.push({ path: '/lecture', query: { category } })

//     // 부모 컴포넌트로 드롭다운 닫기 이벤트 전달
//     emit('closeDropdown')
// }

const selectCategory = (category) => {
    // 타임스탬프(ts)를 추가해서 항상 URL이 변경되도록 함
    router.push({ path: '/lecture', query: { category, ts: Date.now() } })
    emit('closeDropdown')
}
</script>

<style scoped></style>
