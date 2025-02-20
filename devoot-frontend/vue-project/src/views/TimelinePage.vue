<template>
    <div class="flex flex-col gap-4 timeline-container" ref="timelineContainer">
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

        <div v-if="loading" class="flex items-center py-4 text-center text-h3">
            Loading...
            <LogoIcon class="w-4 h-4 text-primary-500" />
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { fetchTimelineList } from '@/helpers/timeline'
import TimeLineCard from '@/components/Timeline/TimeLineCard.vue'
import LogoIcon from '@/assets/icons/logo.svg'

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
    const isTodo = item.type === 'TODO'

    // 공통적으로 lecture 관련 데이터를 가져옴
    const lectureData = isBookmark ? item.log?.lecture : item.log?.todo

    return {
        profileId: item.user?.profileId ?? '',
        type: mapType(item),
        userName: item.user?.nickname ?? '알 수 없는 사용자',
        userImage:
            item.user?.imageUrl ??
            'https://devoot-profile-image.s3.ap-northeast-2.amazonaws.com/profile/default_image.png',
        lectureTitle: lectureData?.name || lectureData?.lectureName || '제목 없음',
        lectureId: (isBookmark ? lectureData?.id : lectureData?.lectureId)?.toString() || '', // BOOKMARK: id, TODO: lectureId
        imageUrl:
            lectureData?.imageUrl ||
            'https://devoot-profile-image.s3.ap-northeast-2.amazonaws.com/profile/default_image.png',
        tags:
            isBookmark && lectureData?.tags
                ? lectureData.tags.split(',').map((tag) => tag.trim())
                : [],
        beforeStatus: isBookmark ? mapStatus(item.log?.beforeStatus) : '',
        afterStatus: isBookmark ? mapStatus(item.log?.afterStatus) : '',
        footprints: isTodo ? (item.log?.footprints ?? []) : [],
        date: item.createdAt,
        sourceName: isBookmark ? (lectureData?.sourceName ?? '') : '',
        sourceUrl: lectureData?.sourceUrl ?? '',
        isBookmarked: isBookmark ? lectureData?.isBookmarked || false : false,
        bookmarkId: isBookmark ? lectureData?.bookmarkId || null : null,
        subLectureName: lectureData?.subLectureName ?? '',
    }
}

async function loadMoreData() {
    if (!hasMore.value || loading.value) return

    loading.value = true
    const token = userStore.token
    const requestUrl = `/api/timeline?page=${page.value}&size=10`
    console.log(`📡 요청 URL: ${requestUrl}`)
    try {
        // 현재 페이지 번호(page.value)를 인자로 전달
        const response = await fetchTimelineList(token, page.value)
        if (response.data.content.length > 0) {
            activities.value.push(...response.data.content.map(mapActivity))
            page.value++
            console.log(`페이지 ${page.value - 1} 데이터 로드 완료`)
        } else {
            hasMore.value = false
            console.log('추가 데이터 없음')
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
    console.log('스크롤 이벤트 발생:', { scrollTop, clientHeight, scrollHeight })
    if (scrollTop + clientHeight >= scrollHeight - 10) {
        console.log('바닥에 도달함 - loadMoreData 호출')
        loadMoreData()
    }
}

onMounted(async () => {
    await userStore.fetchUser()
    console.log('유저 정보 fetch 완료')
    if (USE_DUMMY_DATA) {
        // 더미 데이터를 사용하는 경우의 로직 처리
        // loadDataFromDummy()
    } else {
        loadMoreData()
    }
    console.log('timelineContainer:', timelineContainer.value)
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
/* 스크롤이 발생하려면 높이가 제한되어야 합니다. */
.timeline-container {
    overflow-y: auto;
    max-height: 80vh;
    /* 필요 시 height를 고정값으로 설정해 테스트 해보세요.
       height: 80vh;
    */
}
</style>
