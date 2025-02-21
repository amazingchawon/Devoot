<template>
    <div class="grid col-span-12 pb-20 place-items-center">
        <div class="w-full max-w-3xl">
            <!-- 프로필 이미지 (파일 업로드) -->
            <div id="profile-edit" class="flex flex-col items-center w-full gap-4 mb-6">
                <!-- 프로필 이미지 -->
                <div class="overflow-hidden rounded-full w-36 h-36">
                    <img
                        :src="
                            profileImage ||
                            'https://devoot-profile-image.s3.ap-northeast-2.amazonaws.com/profile/default_image.png'
                        "
                        alt="프로필 이미지"
                        class="object-cover w-full h-full border border-gray-200"
                    />
                </div>
                <!-- 파일 선택 버튼 -->
                <label for="file-upload" class="button-primary">파일 선택</label>
                <input
                    id="file-upload"
                    type="file"
                    accept="image/*"
                    class="hidden"
                    @change="onFileChange"
                />
                <!-- 파일 형태 안내 -->
                <p class="text-gray-300 text-caption-sm">
                    이미지 파일은 .jpg, .jpeg, .png, .gif 형식만 업로드할 수 있습니다.
                </p>
            </div>

            <hr class="mb-4" />

            <!-- 이메일 (Firebase 로그인에서 획득) -->
            <div id="email-edit" class="mb-6">
                <div class="flex flex-row items-center gap-12">
                    <div class="flex-1">
                        <label for="email" class="w-full h-8 text-body">이메일</label>
                        <p v-if="isEmailStored" class="text-gray-300 text-caption">
                            이메일은 수정할 수 없습니다.
                        </p>
                        <p v-else class="text-gray-300 text-caption">
                            이메일은 추후 변경 불가 합니다.
                        </p>
                    </div>
                    <div class="flex-1">
                        <input
                            id="email"
                            v-model="email"
                            type="text"
                            placeholder="example@gmail.com"
                            class="w-full h-8 p-3 placeholder-gray-200 border border-gray-200 rounded focus:bg-gray-100 text-body focus:border-2 focus:border-primary-500 focus:outline-none"
                            :class="{
                                'bg-gray-100 cursor-not-allowed text-gray-400': isEmailStored,
                                'border-red-500': emailError,
                            }"
                            :readonly="isEmailStored"
                            :disabled="isEmailStored"
                        />
                        <p v-if="emailError" class="text-red-500 text-caption">
                            이메일을 입력해주세요!
                        </p>
                    </div>
                </div>
            </div>

            <!-- 아이디(profileId) -->
            <div id="id-edit" class="mb-6">
                <div class="flex flex-row items-center gap-12">
                    <div class="flex-1">
                        <label for="id" class="w-full h-8 text-body">아이디</label>
                    </div>
                    <div class="flex-1">
                        <div class="flex flex-row items-center">
                            <input
                                id="id"
                                v-model="id"
                                type="text"
                                placeholder="아이디를 입력해주세요 (6-20자)"
                                class="w-full h-8 p-3 placeholder-gray-200 border border-gray-200 rounded-r-none focus:bg-gray-100 rounded-l-md text-body focus:border-2 focus:border-primary-500 focus:outline-none"
                                :class="{ 'border-red-500': idError }"
                            />
                            <button
                                class="w-auto h-8 px-3 text-gray-300 border border-l-0 border-gray-200 rounded-l-none text-caption whitespace-nowrap rounded-r-md hover:bg-gray-200 hover:text-primary-500"
                                @click="checkId"
                            >
                                중복확인
                            </button>
                        </div>
                        <!-- 중복 확인 결과 표시 -->
                        <p
                            v-if="idCheckResult === 'available'"
                            class="text-primary-500 text-caption"
                        >
                            사용 가능한 아이디입니다.
                        </p>
                        <p v-if="idCheckResult === 'unavailable'" class="text-red-500 text-caption">
                            사용 불가능한 아이디입니다.
                        </p>
                        <!-- 아이디 필수 -->
                        <p v-if="idError" class="text-red-500 text-caption">
                            아이디를 입력해주세요!
                        </p>
                    </div>
                </div>
            </div>

            <!-- 닉네임 -->
            <div id="nickname-edit" class="mb-6">
                <div class="flex flex-row items-center gap-12">
                    <div class="flex-1">
                        <label for="nickname" class="w-full h-8 text-body">닉네임</label>
                    </div>
                    <div class="flex-1">
                        <input
                            id="nickname"
                            v-model="nickname"
                            type="text"
                            placeholder="닉네임을 입력해주세요 (6-20자)"
                            class="w-full h-8 p-3 placeholder-gray-200 border border-gray-200 rounded-md focus:bg-gray-100 text-body focus:border-2 focus:border-primary-500 focus:outline-none"
                        />
                    </div>
                </div>
            </div>

            <hr class="mb-4" />

            <!-- 계정 공개 여부 -->
            <div id="public-edit" class="mb-6">
                <div class="flex flex-row items-center gap-12">
                    <div class="flex-1">
                        <div class="w-full text-body">계정 공개 범위</div>
                        <p class="text-gray-300 text-caption">
                            비공개 상태인 경우, 회원님이 승인한 팔로워만 프로필을 볼 수 있습니다.
                        </p>
                    </div>
                    <div class="flex-1">
                        <div
                            class="flex flex-row items-center border border-gray-200 rounded-md"
                            :class="{ 'border-red-500': isPublicError }"
                        >
                            <button
                                :class="{
                                    'bg-primary-100 text-primary-500': isPublic === true, // ✅ 공개 선택 시 파란색
                                    'text-black': isPublic !== true,
                                }"
                                class="w-full h-8 border text-caption rounded-l-md hover:bg-gray-200 focus:bg-primary-100 focus:text-primary-500"
                                @click="togglePublic(true)"
                            >
                                공개
                            </button>
                            <button
                                :class="{
                                    'bg-primary-100 text-primary-500': isPublic === false, // ✅ 비공개 선택 시 파란색
                                    'text-black': isPublic !== false,
                                }"
                                class="w-full h-8 border text-caption rounded-r-md hover:bg-gray-200 focus:bg-primary-100 focus:text-primary-500"
                                @click="togglePublic(false)"
                            >
                                비공개
                            </button>
                        </div>
                        <p v-if="isPublicError" class="text-red-500 text-caption">
                            계정 공개 여부를 선택해주세요!
                        </p>
                    </div>
                </div>
            </div>

            <!-- 링크(제목, URL) -->
            <div id="link-edit" class="mb-6">
                <div class="flex flex-row gap-12">
                    <div class="flex-1">
                        <div class="w-full text-body">링크</div>
                        <p class="text-gray-300 text-caption">
                            다른 사용자들에게 공유할 SNS 주소를 설정해주세요.
                        </p>
                    </div>
                    <div id="link" class="flex flex-col flex-1 gap-6">
                        <div class="flex flex-col gap-1">
                            <label for="link-title" class="text-gray-400 text-caption">제목</label>
                            <input
                                id="link-title"
                                v-model="linkTitle"
                                type="text"
                                placeholder="링크 제목을 입력해주세요"
                                class="w-full h-8 p-3 placeholder-gray-200 border border-gray-200 rounded-md focus:bg-gray-100 text-body focus:border-2 focus:border-primary-500 focus:outline-none"
                            />
                        </div>
                        <!-- 올바른 형태의 URL인지 검증 필요 -->
                        <div class="flex flex-col gap-1">
                            <label for="link-url" class="text-gray-400 text-caption">URL</label>
                            <input
                                id="link-url"
                                v-model="linkURL"
                                type="text"
                                placeholder="URL 주소를 입력해주세요"
                                class="w-full h-8 p-3 placeholder-gray-200 border border-gray-200 rounded-md focus:bg-gray-100 text-body focus:border-2 focus:border-primary-500 focus:outline-none"
                            />
                        </div>
                    </div>
                </div>
            </div>

            <!-- 관심 태그 (최대 5개) -->
            <div id="link-edit" class="mb-6">
                <div class="flex flex-row gap-12">
                    <div class="flex-1">
                        <div class="w-full text-body">관심 태그</div>
                        <p class="text-gray-300 text-caption">
                            관심 있는 기술 태그를 선택해주세요. <br />
                            (최대 5개)
                        </p>
                    </div>
                    <div class="flex-1">
                        <div
                            class="flex flex-wrap w-full gap-2 p-4 border border-gray-200 rounded-md"
                            :class="{ 'border-red-500': tagsError }"
                        >
                            <!-- 선택된 태그 먼저 표시 -->
                            <button
                                v-for="tag in sortedTags"
                                :key="tag"
                                :disabled="!isTagSelected(tag) && selectedTags.length >= 5"
                                :class="{
                                    'tag-gray': !isTagSelected(tag),
                                    'tag-primary': isTagSelected(tag),
                                }"
                                :title="
                                    !isTagSelected(tag) && selectedTags.length >= 5
                                        ? '최대 5개까지 선택 가능합니다.'
                                        : ''
                                "
                                @click="toggleTag(tag)"
                            >
                                <div class="flex flex-row items-center gap-1">
                                    {{ tag }}
                                    <div v-if="isTagSelected(tag)">
                                        <Delete class="w-3 h-3" />
                                    </div>
                                </div>
                            </button>
                        </div>
                        <p v-if="tagsError" class="text-red-500 text-caption">
                            최소 1개의 태그를 선택해주세요!
                        </p>
                    </div>
                </div>
            </div>

            <hr class="mb-4" />

            <!-- 저장 버튼 -->
            <div class="flex flex-row gap-2">
                <div class="flex-1"></div>
                <button v-if="isNewUser" class="button-primary" @click="saveProfile">
                    회원가입
                </button>
                <button v-else class="button-primary" @click="saveProfile">변경사항 저장</button>
                <button class="button-gray">취소</button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useUserStore } from '@/stores/user' // Pinia 스토어 가져오기
import {
    getUserInfo,
    updateUserInfo,
    registerUser,
    checkProfileId,
    checkProfileIdAuthenticated,
} from '@/helpers/api' // API 함수 가져오기

import Delete from '@/assets/icons/delete.svg'
import router from '@/router'

const userStore = useUserStore()
const isNewUser = ref(false) // 회원가입 모드 여부
const originalId = ref('') // 기존 아이디 저장 변수
const idCheckResult = ref('') // 중복 검사 결과 저장 변수
const watchEnabled = ref(false) // watch 활성화 여부 플래그
const isEmailStored = ref(false) // 이메일 수정 불가 여부

// 프로필 데이터
const profileImage = ref(
    'https://devoot-profile-image.s3.ap-northeast-2.amazonaws.com/profile/default_image.png'
)
const email = ref('')
const id = ref('')
const nickname = ref('')
const isPublic = ref(true)
const linkTitle = ref('')
const linkURL = ref('')
const selectedTags = ref([]) // 선택된 태그를 담는 배열

// =================================================
// 사용자 정보 가져오기
// =================================================
onMounted(async () => {
    email.value = userStore.user.email // Firebase 이메일 정보 가져오기
    isEmailStored.value = !!userStore.user.email // DB에서 가져온 이메일이 있으면 true

    if (userStore.token) {
        try {
            const response = await getUserInfo(userStore.token)
            const data = response.data

            // 기존 회원 데이터 설정
            email.value = data.email || userStore.user.email
            originalId.value = data.profileId || ''
            id.value = originalId.value
            nickname.value = data.nickname || ''
            isPublic.value = data.isPublic ?? true
            profileImage.value =
                data.imageUrl ||
                'https://devoot-profile-image.s3.ap-northeast-2.amazonaws.com/profile/default_image.png'
            selectedTags.value = data.tags ? data.tags.split(',') : []

            isEmailStored.value = !!data.email || !!userStore.user.email // DB에서 가져온 이메일이 있으면 true

            if (data.links) {
                const parsedLinks = JSON.parse(data.links)
                linkTitle.value = parsedLinks.title || ''
                linkURL.value = parsedLinks.url || ''
            }

            isNewUser.value = false // 기존 유저 → 정상 처리
        } catch (error) {
            // 404 오류 시 회원가입 진행
            if (error.response?.status === 404) {
                console.warn('🚨 회원 정보 없음 → 회원가입 진행.')
                isNewUser.value = true
                return
            }

            // 기타 오류 처리
            console.error('🚨 유저 정보를 불러오는 중 문제가 발생했습니다:', error)
            alert('유저 정보를 불러오는 중 문제가 발생했습니다.')
        }
    }

    if (!isNewUser.value) {
        idCheckResult.value = 'available' // 기존 회원은 중복 검사 필요 없음
    }

    watchEnabled.value = true // 데이터 로딩 후 watch 활성화
})

//=================================================
// 아이디 중복 확인
//=================================================
// 입력값 변경 시 에러 초기화
watch(id, (newValue) => {
    if (!watchEnabled.value) return // 데이터 로딩 후에만 watch 실행

    // 기존 아이디와 동일하면 중복 검사 결과 유지
    if (newValue === originalId.value) {
        return
    }

    // ✅ 아이디가 변경된 경우에만 중복 검사 초기화
    idCheckResult.value = ''
})

const checkId = async () => {
    if (!id.value) {
        alert('아이디를 입력해주세요.')
        return
    }

    try {
        let response
        if (isNewUser.value) {
            // 회원가입 모드용 중복확인 API 호출
            response = await checkProfileId(userStore.token, id.value)
        } else {
            // 수정 모드용 중복확인 API 호출
            response = await checkProfileIdAuthenticated(userStore.token, id.value)
        }

        idCheckResult.value = response.data ? 'available' : 'unavailable'
    } catch (error) {
        console.error('아이디 중복 확인 오류:', error)
        alert('아이디 중복 확인 중 문제가 발생했습니다.')
    }
}

// =================================================
// 프로필 이미지
// =================================================
const fileName = ref(null)
const selectedFile = ref(null)

// 파일 변경 핸들러
const onFileChange = (event) => {
    const file = event.target.files[0]
    if (file) {
        selectedFile.value = file // 선택한 파일 저장
        fileName.value = file.name

        // 이미지 미리보기 생성
        const reader = new FileReader()
        reader.onload = (e) => {
            profileImage.value = e.target.result
        }
        reader.readAsDataURL(file)
    }
}

//=================================================
// 계정 공개 범위
//=================================================
const togglePublic = (value) => {
    isPublic.value = value // 선택한 값으로 변경
}

//=================================================
// 태그
//=================================================

// 태그 데이터
const allTags = [
    'HTML',
    'JavaScript',
    'Java',
    'Ruby',
    'TypeScript',
    'Swift',
    'Kotlin',
    'Python',
    'C',
    'C++',
    'C#',
    'Go',
    'Scala',
    'Dart',
    'MySQL',
    'Oracle',
    'Markdown',
    '데이터 분석',
    '데이터 엔지니어링',
    '딥러닝/머신러닝',
    '컴퓨터 비전',
    '자연어 처리',
    '시스템/운영체제',
    '블록체인',
    '컴퓨터 구조',
    '임베디드/IoT',
    '반도체',
    '로보공학',
    'UX/UI',
]

// 태그 선택/취소 토글
function toggleTag(tag) {
    if (isTagSelected(tag)) {
        // 선택된 태그를 클릭하면 선택 취소
        selectedTags.value = selectedTags.value.filter((t) => t !== tag)
    } else if (selectedTags.value.length < 5) {
        // 선택되지 않은 태그 추가
        selectedTags.value.push(tag)
        tagsError.value = false
    }
}

// 태그가 선택되었는지 확인
function isTagSelected(tag) {
    return selectedTags.value.includes(tag)
}

// 선택된 태그를 앞에 정렬
const sortedTags = computed(() => {
    return [
        ...selectedTags.value, // 선택된 태그
        ...allTags.filter((tag) => !selectedTags.value.includes(tag)), // 선택되지 않은 태그
    ]
})

//================================
// 저장
//================================

// 에러 관련 변수
const emailError = ref(false)
const idError = ref(false)
const tagsError = ref(false)
const isPublicError = ref(false)
const errorMessage = ref('') // 전체 에러 메시지

// 저장 버튼 클릭시 API 호출
const saveProfile = async () => {
    errorMessage.value = '' // 초기화

    // 필수 입력값 검증
    emailError.value = !email.value
    idError.value = id.value.trim() === ''
    idError.value = !id.value
    tagsError.value = selectedTags.value.length === 0
    isPublicError.value = isPublic.value === null

    if (emailError.value || idError.value || tagsError.value || isPublicError.value) {
        errorMessage.value = '필수 입력 항목을 확인해주세요!'
        return
    }

    // 아이디 중복 검사 여부 검증
    if (idCheckResult.value !== 'available') {
        alert('아이디 중복 검사를 완료해주세요!')
        return
    }

    // JSON 데이터를 Blob으로 변환하여 추가
    const updatedProfile = {
        profileId: id.value,
        nickname: nickname.value,
        email: email.value,
        isPublic: isPublic.value,
        tags: selectedTags.value.join(','),
        links:
            linkTitle.value.trim() || linkURL.value.trim()
                ? JSON.stringify({ title: linkTitle.value, url: linkURL.value })
                : '',
    }

    // FormData 객체 생성
    const formData = new FormData()
    formData.append(
        'user',
        new Blob([JSON.stringify(updatedProfile)], { type: 'application/json' })
    )

    // 프로필 이미지 파일 추가 (선택된 경우)
    if (selectedFile.value) {
        formData.append('file', selectedFile.value)
    }

    try {
        if (isNewUser.value) {
            await registerUser(userStore.token, formData) // 회원가입 API 호출
            await router.replace({ name: 'home' }) // ✅ 뒤로 가기 방지
        } else {
            // JSON 데이터 확인
            console.log('📌 저장할 프로필 데이터:', updatedProfile)

            // FormData 내용 확인 (JSON 데이터)
            console.log('📌 FormData - user:', await formData.get('user').text())

            await updateUserInfo(userStore.token, formData) // 프로필 수정 API 호출
            alert('프로필이 성공적으로 업데이트되었습니다!')
        }

        isNewUser.value = false // 회원가입이든 수정이든 완료 후 기존 유저 모드 유지
    } catch (error) {
        if (error.type === 'VALIDATION_ERROR') {
            const fieldErrors = error.errors || {}
            errorMessage.value = '잘못된 입력이 있습니다. 확인해주세요.'

            if (fieldErrors.email) {
                emailError.value = true
                console.log('🚨 이메일 오류:', fieldErrors.email)
            }
            if (fieldErrors.profileId) {
                idError.value = true
                console.log('🚨 아이디 오류:', fieldErrors.profileId)
            }
            if (fieldErrors.nickname) {
                console.log('🚨 닉네임 오류:', fieldErrors.nickname)
                alert(fieldErrors.nickname[0]) // 닉네임 길이 초과 등 경고창 띄우기
            }
            if (fieldErrors.isPublic) {
                isPublicError.value = true
            }
            if (fieldErrors.links) {
                console.log('🚨 링크 오류:', fieldErrors.links)
                alert('링크 정보가 올바르지 않습니다. 다시 확인해주세요!')
            }
            if (fieldErrors.tags) {
                tagsError.value = true
                alert('태그는 1개 이상 5개 이하로 선택해야 합니다!')
            }
        } else if (error.type === 'S3_ERROR') {
            console.error('🚨 S3 업로드 실패:', error.message)
            alert('이미지 업로드에 실패했습니다. 올바른 이미지 파일을 업로드해주세요.')
        } else {
            errorMessage.value = '프로필 업데이트에 실패했습니다. 다시 시도해주세요.'
        }
    }
}
</script>

<style scoped></style>
