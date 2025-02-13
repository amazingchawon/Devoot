<template>
    <div class="p-4">
        <h1 class="mb-4 text-2xl font-bold">타임라인</h1>
        <div class="flex flex-col gap-4">
            <TimeLineCard
                v-for="(activity, index) in activities"
                :key="index"
                :profileId="activity.profileId"
                :type="activity.type"
                :userName="activity.userName"
                :userImage="activity.userImage"
                :lectureTitle="activity.lectureTitle"
                :lectureId="activity.lectureId"
                :imageUrl="activity.imageUrl"
                :tags="activity.tags"
                :beforeStatus="activity.beforeStatus"
                :afterStatus="activity.afterStatus"
                :footprints="activity.footprints"
                :date="activity.date"
            />
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, watchEffect } from 'vue'
import { useUserStore } from '@/stores/user'
import TimeLineCard from '@/components/Timeline/TimeLineCard.vue'
import { fetchTimelineList } from '@/helpers/timeline'

const activities = ref([])
const userStore = useUserStore()

onMounted(async () => {
    await userStore.fetchUser()
    console.log('유저 정보 fetch 완료')
    console.log(userStore.token)
})

const fetchActivities = async () => {
    const token = userStore.token
    console.log('🔑 현재 토큰:', token)

    try {
        const response = await fetchTimelineList(token)
        console.log('✅ API 응답 데이터:', response.data)

        activities.value = response.data.content.map((item) => ({
            profileId: item.user?.id ?? '', // 사용자 프로필 ID 추가 (없으면 빈 문자열)
            type: mapType(item),
            userName: item.user?.nickname ?? '알 수 없는 사용자',
            userImage: item.user?.imageUrl ?? '/src/assets/icons/default-thumbnail.png',
            lectureTitle: item.log?.lecture?.name ?? '제목 없음',
            lectureId: item.log?.lecture?.id ?? '', // 강의 ID 추가 (없으면 빈 문자열)
            imageUrl: item.log?.lecture?.imageUrl ?? '/src/assets/icons/default-thumbnail.png',
            tags: item.log?.lecture?.tags
                ? item.log.lecture.tags.split(',').map((tag) => tag.trim())
                : [],
            beforeStatus: mapStatus(item.log?.beforeStatus),
            afterStatus: mapStatus(item.log?.afterStatus),
            footprints: item.log?.footprints ?? [],
            date: item.createdAt,
        }))
    } catch (error) {
        console.error('❌ [타임라인 오류] 데이터를 가져오는 중 문제가 발생했습니다:', error)
    }
}

// 토큰이 존재할 때만 fetchActivities 호출
watchEffect(() => {
    if (!userStore.token) return
    fetchActivities()
})

// `BOOKMARK`와 `TODO` 타입을 매핑
const mapType = (item) => {
    if (item.type === 'BOOKMARK') {
        return item.log?.beforeStatus == null ? 'new-lecture-interest' : 'lecture-status-change'
    } else if (item.type === 'TODO') {
        return 'footprint-added'
    }
    return 'unknown'
}

// 상태 코드(1, 2, 3)를 문자열로 변환
const mapStatus = (status) => {
    switch (status) {
        case 1:
            return '수강 전'
        case 2:
            return '수강 중'
        case 3:
            return '수강 완료'
        default:
            return ''
    }
}
</script>
