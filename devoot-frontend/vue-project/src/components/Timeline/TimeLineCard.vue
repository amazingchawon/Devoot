<template>
    <!-- (1) 발자국(footprint-added) 타입 UI -->
    <div
      v-if="type === 'footprint-added'"
      class="p-6 mb-4 bg-white border border-gray-200 rounded-2xl w-[72rem] flex flex-col gap-4 cursor-pointer"
      @click="goToProfile(profileId)"
    >
      <!-- 상단 영역: 아바타 + "발자국이 추가되었습니다" + 아이콘 + 날짜 -->
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-3">
          <img
            :src="userImage"
            alt="프로필 사진"
            class="object-cover w-10 h-10 bg-gray-200 rounded-full"
          />
          <!-- 한 줄에 사용자명, 문구, 아이콘 -->
          <p class="flex items-center gap-2 text-body">
            <span class="text-body-bold">{{ userName }}</span>
            님의 발자국이 추가되었습니다
            <FootprintIcon class="w-4 h-4 text-primary-500" />
          </p>
        </div>
        <span class="text-gray-400 text-caption">
          {{ formattedDate }}
        </span>
      </div>
  
      <!-- 하단 영역: 강의 정보 (좌: 강의제목 / 우: 하위 강의명) -->
      <div class="flex items-center justify-between h-[4.25rem] px-6 py-3 border border-gray-200 rounded-lg">
        <div class="flex items-center gap-4">
            <p class="text-body-bold">
          {{ lectureTitle }}  
        </p>
        <p class="text-body">-</p>
        <p class="text-body">
         {{ subLectureName }}
        </p>
        </div>
        <button @click.stop="toggleBookmark" class="p-2 focus:outline-none">
            <component
            :is="isBookmarked ? BookmarkFill : BookmarkDefault"
            class="w-6 h-6 text-primary-500"
            />
        </button>
      </div>
    </div>
  
    <!-- (2) 그 외 타입(BOOKMARK 관련) UI: 기존 코드 유지 -->
    <div
      v-else
      class="p-6 mb-4 bg-white border border-gray-200 rounded-2xl w-[72rem]"
    >
      <!-- 카드 헤더 -->
      <div class="flex items-center justify-between">
        <div class="flex items-center">
          <img
            :src="userImage"
            alt="프로필 사진"
            class="object-cover w-10 h-10 bg-gray-200 rounded-full"
          />
          <p
            class="flex items-center ml-3 text-sm text-gray-700 cursor-pointer"
            @click="goToProfile(profileId)"
          >
            <div class="text-body-bold">{{ userName }}</div>
            <div v-if="type === 'lecture-status-change'">
              님이
              <span
                v-if="beforeStatus"
                class="h-6 px-2 py-1 bg-gray-100 rounded-full"
                :class="statusColor(beforeStatus)"
              >
                {{ beforeStatus }}
              </span>
              <span v-if="beforeStatus" class="text-body-bold"> &gt; </span>
              <span
                v-if="afterStatus"
                class="h-6 px-2 py-1 bg-gray-100 rounded-full"
                :class="statusColor(afterStatus)"
              >
                {{ afterStatus }}
              </span>
              강의 상태를 변경하였습니다.
            </div>
            <div v-else-if="type === 'new-lecture-interest'">
              님이 새로운 강의에 관심을 가지기 시작했습니다.
            </div>
          </p>
        </div>
        <span class="text-gray-400 text-caption">{{ formattedDate }}</span>
      </div>
  
      <!-- 강의 정보 (BOOKMARK & TODO) -->
      <div
        class="flex mt-4 border border-gray-200 cursor-pointer rounded-xl"
        @click="goToLecture(lectureId)"
      >
        <img
          :src="imageUrl"
          alt="강의 썸네일"
          class="object-cover rounded-l-xl w-[16.75rem] h-[10rem]"
        />
        <div class="flex flex-col w-full h-full mx-6 mt-5">
          <!-- 사이트명(sourceName) 표시 -->
          <div class="mb-1 text-gray-400 text-body" v-if="sourceName">
            {{ sourceName }}
          </div>
  
          <!-- 강의 타이틀 + 북마크 아이콘 (아이콘만 표시) -->
          <div class="flex items-center justify-between">
            <p class="text-h3">{{ lectureTitle }}</p>
            <button @click.stop="toggleBookmark" class="p-2 focus:outline-none">
              <component
                :is="isBookmarked ? BookmarkFill : BookmarkDefault"
                class="w-6 h-6 text-primary-500"
              />
            </button>
          </div>
  
          <!-- 태그 영역 -->
          <div v-if="tags.length" class="flex mb-5 space-x-2 mt-7">
            <span v-for="(tag, index) in tags" :key="index" class="tag-gray">
              #{{ tag }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { computed, ref, onMounted } from 'vue'
  import { useRouter } from 'vue-router'
  import axios from 'axios'
  import BookmarkDefault from '@/assets/icons/bookmark_default.svg'
  import BookmarkFill from '@/assets/icons/bookmark_filled.svg'
  import FootprintIcon from '@/assets/icons/footprint.svg'
  import { addBookmark, removeBookmark } from '@/helpers/lecture'
  import { useUserStore } from '@/stores/user'
  
  const props = defineProps({
    profileId: { type: String, required: true, default: '' },
    lectureId: { type: String, required: true, default: '' },
    type: { type: String, required: true },
    userName: { type: String, required: true },
    userImage: { type: String, default: '/src/assets/icons/default-thumbnail.png' },
    lectureTitle: { type: String, default: '제목 없음' },
    imageUrl: { type: String, default: '/src/assets/icons/default-thumbnail.png' },
    sourceName: { type: String, default: '' },
    tags: { type: Array, default: () => [] },
    beforeStatus: { type: String, default: '' },
    afterStatus: { type: String, default: '' },
    date: { type: String, required: true },
    // 'footprint-added'일 때 하위 강의명
    subLectureName: { type: String, default: '' },
    // 북마크 관련
    isBookmarked: { type: Boolean, default: false },
    bookmarkId: { type: [Number, String], default: null },
  })
  
  const router = useRouter()
  const userStore = useUserStore()
  
  function goToProfile(profileId) {
    if (!profileId) {
      console.error('❌ 프로필 ID가 없음!')
      return
    }
    router.push(`/profile/${profileId}`)
  }
  
  function goToLecture(lectureId) {
    if (!lectureId) {
      console.error('❌ 강의 ID가 없음!')
      return
    }
    router.push(`/lecture/${lectureId}`)
  }
  
  function statusColor(status) {
    switch (status) {
      case '수강 전': return 'text-gray-400'
      case '수강 중': return 'text-[#FDE03A]'
      case '수강 완료': return 'text-[#0EDB8C]'
      default: return ''
    }
  }
  
  const formattedDate = computed(() => {
    return new Date(props.date).toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
    })
  })
  
  // 북마크 관련
  const isBookmarked = ref(props.isBookmarked)
  const bookmarkId = ref(props.bookmarkId)
  
  async function fetchBookmarkStatus() {
    try {
  
      const response = await axios.get(`/api/users/${userStore.userId}/bookmarks`, {
        headers: { Authorization: `Bearer ${userStore.token}` },
      })
      const todo = response.data.todo || []
      const inProgress = response.data['in-progress'] || []
      const done = response.data.done || []
      const allBookmarks = [...todo, ...inProgress, ...done]
  
      const found = allBookmarks.find(
        (b) => b.lecture && b.lecture.id.toString() === props.lectureId.toString()
      )
      if (found) {
        isBookmarked.value = true
        bookmarkId.value = found.id
      } else {
        isBookmarked.value = false
        bookmarkId.value = null
      }
    } catch (error) {
      console.error('북마크 상태를 가져오는데 실패:', error)
    }
  }
  
  onMounted(() => {
    fetchBookmarkStatus()
  })
  
  async function toggleBookmark() {
    try {
  
      if (!isBookmarked.value) {
        const response = await addBookmark(userStore.token, userStore.userId, props.lectureId)
        bookmarkId.value = response.data.id
        console.log('✅ 북마크 추가 성공:', bookmarkId.value)
      } else {
        if (bookmarkId.value) {
          await removeBookmark(userStore.token, userStore.userId, bookmarkId.value)
          console.log('✅ 북마크 삭제 성공')
          bookmarkId.value = null
        }
      }
      isBookmarked.value = !isBookmarked.value
    } catch (error) {
      console.error('❌ 북마크 토글 실패:', error)
    }
  }
  </script>
  