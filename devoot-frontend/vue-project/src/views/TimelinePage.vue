<template>
    <div>
        <h1 class="mb-4 text-2xl text-h1">타임라인</h1>
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
                :sourceName="activity.sourceName"
                :sourceUrl="activity.sourceUrl"
                :isBookmarked="activity.isBookmarked"
                :bookmarkId="activity.bookmarkId"
                :subLectureName="activity.subLectureName"
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

// ---------------------------------------------------------------------
// USE_DUMMY_DATA 플래그
// true이면 public/timeline_dummy_data.json (더미 데이터) 사용
// API 완성 후에는 false로 설정하거나 더미 데이터 관련 코드를 주석 처리하세요.
// ---------------------------------------------------------------------
const USE_DUMMY_DATA = false

// ---------------------------------------------------------------------
// API/더미 데이터를 카드에서 사용할 형식으로 매핑하는 함수
// ---------------------------------------------------------------------
function mapActivity(item) {
    const isBookmark = item.type === 'BOOKMARK'
    // TODO 타입은 item.log.todo, BOOKMARK 타입은 item.log.lecture 사용
    const lectureData = isBookmark ? item.log?.lecture : item.log?.todo

    return {
        profileId: item.user?.profileId ?? '',
        type: mapType(item),
        userName: item.user?.nickname ?? '알 수 없는 사용자',
        userImage: item.user?.imageUrl ?? '/src/assets/icons/default-thumbnail.png',
        // BOOKMARK: lectureData.name, TODO: lectureData.lectureName
        lectureTitle: lectureData?.name || lectureData?.lectureName || '제목 없음',
        // lectureId를 문자열로 변환
        lectureId: isBookmark
            ? lectureData?.id
                ? lectureData.id.toString()
                : ''
            : lectureData?.lectureId
              ? lectureData.lectureId.toString()
              : '',
        // BOOKMARK는 강의 썸네일 사용, TODO는 기본 이미지 사용
        imageUrl: isBookmark
            ? lectureData?.imageUrl || '/src/assets/icons/default-thumbnail.png'
            : '/src/assets/icons/default-thumbnail.png',
        // BOOKMARK인 경우 tags 분리, TODO는 빈 배열
        tags:
            isBookmark && lectureData?.tags
                ? lectureData.tags.split(',').map((tag) => tag.trim())
                : [],
        // BOOKMARK 타입에만 상태 정보
        beforeStatus: isBookmark ? mapStatus(item.log?.beforeStatus) : '',
        afterStatus: isBookmark ? mapStatus(item.log?.afterStatus) : '',
        footprints: item.log?.footprints ?? [],
        date: item.createdAt,
        // BOOKMARK인 경우 sourceName, TODO인 경우 sourceUrl
        sourceName: isBookmark ? (lectureData?.sourceName ?? '') : '',
        sourceUrl: !isBookmark ? (lectureData?.sourceUrl ?? '') : '',
        // 북마크 관련: 더미 데이터에는 이 정보가 없으므로 기본값 사용
        isBookmarked: isBookmark ? lectureData?.isBookmarked || false : false,
        bookmarkId: isBookmark ? lectureData?.bookmarkId || null : null,
        // TODO 타입이면 subLectureName, BOOKMARK이면 빈 문자열
        subLectureName: isBookmark ? '' : (lectureData?.subLectureName ?? ''),
    }
}

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

// ---------------------------------------------------------------------
// API 데이터를 불러오는 함수
// ---------------------------------------------------------------------
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

// ---------------------------------------------------------------------
// 더미 데이터를 불러오는 함수
// TODO: API 완성 후 이 부분을 주석 처리하거나 제거하세요.
// ---------------------------------------------------------------------
async function loadDataFromDummy() {
    try {
        const res = await fetch('/timeline_dummy_data.json')
        const dummyData = await res.json()
        console.log('더미 데이터 응답:', dummyData)
        activities.value = dummyData.content.map(mapActivity)
    } catch (error) {
        console.error('더미 데이터 로드 실패:', error)
    }
}

onMounted(async () => {
    await userStore.fetchUser()
    console.log('유저 정보 fetch 완료')
})

watchEffect(() => {
    if (!userStore.token) return
    if (USE_DUMMY_DATA) {
        loadDataFromDummy()
    } else {
        loadDataFromAPI()
    }
})
</script>
