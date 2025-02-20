<template>
    <div class="flex flex-col gap-4 overflow-y-auto max-height-80vh" ref="timelineContainer">
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
            :sourceName="activity.sourceName"
            :sourceUrl="activity.sourceUrl"
            :isBookmarked="activity.isBookmarked"
            :bookmarkId="activity.bookmarkId"
            :subLectureName="activity.subLectureName"
        />
        <div v-if="loading" class="py-4 text-center">Loading...</div>
    </div>
</template>

<script setup>
import { ref, onMounted, watchEffect, onUnmounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { fetchTimelineList } from '@/helpers/timeline'
import TimeLineCard from '@/components/Timeline/TimeLineCard.vue'

const activities = ref([])
const userStore = useUserStore()
const page = ref(1)
const loading = ref(false)
const hasMore = ref(true)
const timelineContainer = ref(null)
const USE_DUMMY_DATA = false

function mapType(item) {
    if (item.type === 'BOOKMARK') {
        return item.log?.beforeStatus == null ? 'new-lecture-interest' : 'lecture-status-change'
    } else if (item.type === 'TODO') {
        return 'footprint-added'
    }
    return 'unknown'
}

function mapStatus(status) {
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

function mapActivity(item) {
    const isBookmark = item.type === 'BOOKMARK'
    const lectureData = isBookmark ? item.log?.lecture : item.log?.todo

    return {
        profileId: item.user?.profileId ?? '',
        type: mapType(item),
        userName: item.user?.nickname ?? '알 수 없는 사용자',
        userImage: item.user?.imageUrl ?? '/src/assets/icons/default-thumbnail.png',
        lectureTitle: lectureData?.name || lectureData?.lectureName || '제목 없음',
        lectureId: lectureData?.id?.toString() || '',
        imageUrl: isBookmark
            ? lectureData?.imageUrl || '/src/assets/icons/default-thumbnail.png'
            : '/src/assets/icons/default-thumbnail.png',
        tags:
            isBookmark && lectureData?.tags
                ? lectureData.tags.split(',').map((tag) => tag.trim())
                : [],
        beforeStatus: isBookmark ? mapStatus(item.log?.beforeStatus) : '',
        afterStatus: isBookmark ? mapStatus(item.log?.afterStatus) : '',
        footprints: item.log?.footprints ?? [],
        date: item.createdAt,
        sourceName: isBookmark ? (lectureData?.sourceName ?? '') : '',
        sourceUrl: !isBookmark ? (lectureData?.sourceUrl ?? '') : '',
        isBookmarked: isBookmark ? lectureData?.isBookmarked || false : false,
        bookmarkId: isBookmark ? lectureData?.bookmarkId || null : null,
        subLectureName: isBookmark ? '' : (lectureData?.subLectureName ?? ''),
    }
}

async function loadDataFromAPI() {
    const token = userStore.token
    try {
        const response = await fetchTimelineList(token)
        console.log('API 데이터 응답:', response.data)
        activities.value = response.data.content.map(mapActivity)
    } catch (error) {
        console.error('API 데이터 로드 실패:', error)
    }
}

async function loadMoreData() {
    if (!hasMore.value || loading.value) return

    loading.value = true
    const token = userStore.token
    try {
        const response = await fetchTimelineList(token, page.value)
        if (response.data.content.length > 0) {
            activities.value.push(...response.data.content.map(mapActivity))
            page.value++
        } else {
            hasMore.value = false
        }
    } catch (error) {
        console.error('API 데이터 로드 실패:', error)
    } finally {
        loading.value = false
    }
}

function handleScroll() {
    if (!timelineContainer.value) return
    const { scrollTop, scrollHeight, clientHeight } = timelineContainer.value
    if (scrollTop + clientHeight >= scrollHeight - 10) {
        loadMoreData()
    }
}

onMounted(async () => {
    await userStore.fetchUser()
    console.log('유저 정보 fetch 완료')
    if (USE_DUMMY_DATA) {
        loadDataFromDummy()
    } else {
        loadDataFromAPI()
    }
    if (timelineContainer.value) {
        timelineContainer.value.addEventListener('scroll', handleScroll)
    }
})

onUnmounted(() => {
    if (timelineContainer.value) {
        timelineContainer.value.removeEventListener('scroll', handleScroll)
    }
})
</script>

<style scoped>
.timeline-container {
    overflow-y: auto;
    max-height: 80vh;
}
</style>
