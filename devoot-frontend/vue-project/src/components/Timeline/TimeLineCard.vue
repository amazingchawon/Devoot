<template>
    <div class="p-4 mb-4 bg-white rounded-lg shadow-md">
        <!-- 카드 헤더 -->
        <div class="flex items-center justify-between">
            <div class="flex items-center">
                <img
                    :src="userImage"
                    alt="사용자 아바타"
                    class="object-cover w-10 h-10 bg-gray-200 rounded-full"
                />
                <p
                    class="flex items-center ml-3 text-sm text-gray-700 cursor-pointer"
                    @click="goToProfile(profileId)"
                >
                    <strong>{{ userName }}</strong>
                    <template v-if="type === 'lecture-status-change'">
                        님이
                        <span v-if="beforeStatus" class="px-2 py-1 bg-gray-100 rounded">
                            {{ beforeStatus }}
                        </span>
                        <span v-if="beforeStatus"> → </span>
                        <span v-if="afterStatus" class="px-2 py-1 bg-gray-100 rounded">
                            {{ afterStatus }}
                        </span>
                        강의 상태를 변경하였습니다.
                    </template>
                    <template v-else-if="type === 'new-lecture-interest'">
                        님이 새로운 강의에 관심을 가지기 시작했습니다.
                    </template>
                    <template v-else-if="type === 'footprint-added'">
                        님의 발자국이 추가되었습니다
                    </template>
                </p>
            </div>
            <span class="text-xs text-gray-400">{{ formattedDate }}</span>
        </div>

        <!-- 강의 정보 (BOOKMARK & TODO) -->
        <div class="flex items-center mt-4 rounded-lg bg-gray-50" @click="goToLecture(lectureId)">
            <img
                :src="imageUrl"
                alt="강의 썸네일"
                class="object-cover rounded-lg w-[16.75rem] h-[10rem]"
            />
            <div class="ml-4">
                <p class="text-sm font-semibold text-gray-800">{{ lectureTitle }}</p>
                <div v-if="tags.length" class="flex mt-2 space-x-2 text-xs text-gray-500">
                    <span v-for="(tag, index) in tags" :key="index">#{{ tag }}</span>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { useRouter } from 'vue-router'

export default {
    setup() {
        const router = useRouter()

        // 프로필 페이지 이동
        const goToProfile = (profileId) => {
            if (!profileId) {
                console.error('❌ 프로필 ID가 없음!')
                return
            }
            console.log(`🔗 프로필 페이지로 이동: /profile/${profileId}`)
            router.push(`/profile/${profileId}`)
        }

        // 강의 상세 페이지 이동
        const goToLecture = (lectureId) => {
            if (!lectureId) {
                console.error('❌ 강의 ID가 없음!')
                return
            }
            console.log(`🔗 강의 페이지로 이동: /lecture/${lectureId}`)
            router.push(`/lecture/${lectureId}`)
        }

        return { goToProfile, goToLecture }
    },
    props: {
        profileId: { type: String, required: true, default: '' },
        lectureId: { type: String, required: true, default: '' },
        type: { type: String, required: true },
        userName: { type: String, required: true },
        userImage: { type: String, default: '/src/assets/icons/default-thumbnail.png' },
        lectureTitle: { type: String, default: '제목 없음' },
        imageUrl: { type: String, default: '/src/assets/icons/default-thumbnail.png' },
        tags: { type: Array, default: () => [] },
        beforeStatus: { type: String, default: '' },
        afterStatus: { type: String, default: '' },
        date: { type: String, required: true },
    },
    computed: {
        formattedDate() {
            return new Date(this.date).toLocaleDateString('ko-KR', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
            })
        },
    },
}
</script>
