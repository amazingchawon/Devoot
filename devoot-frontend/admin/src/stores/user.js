// src\stores\user.js

import { defineStore } from 'pinia'
import {
    auth,
    googleProvider,
    githubProvider,
    signInWithPopup,
    setPersistence,
    browserLocalPersistence,
    onAuthStateChanged,
} from '@/firebase'
import { getUserInfo } from '@/helpers/api' // API 함수 불러오기
import router from '../router'

export const useUserStore = defineStore('user', {
    state: () => ({
        user: null, // 사용자 정보
        token: null, // Firebase 토큰
        userId: null, // 사용자 ID
    }),

    getters: {
        isAuthenticated: (state) => !!state.token, // 로그인 여부 -> 동적으로 계산
    },

    actions: {
        // 구글 로그인
        async loginWithGoogle() {
            try {
                await setPersistence(auth, browserLocalPersistence) // 로그인 유지
                const result = await signInWithPopup(auth, googleProvider)

                // 여기에 방어 코드 추가
                if (!result || !result.user) {
                    console.warn('🚨 로그인 취소 또는 중단됨 (result 또는 result.user 없음)')
                    return null
                }

                // Firebase에서 받아온 사용자 정보 저장
                this.user = result.user
                this.token = await result.user.getIdToken(true)

                // API에서 유저 정보 가져오기
                try {
                    const res = await getUserInfo(this.token)
                    this.userId = res.data.profileId
                    return true
                } catch (apiError) {
                    console.error('🚨 유저 정보 가져오기 실패:', apiError)
                    return false
                }
            } catch (error) {
                if (
                    error.code === 'auth/popup-closed-by-user' ||
                    error.code === 'auth/cancelled-popup-request'
                ) {
                    console.warn('🚨 사용자가 로그인 팝업을 닫았습니다. (catch 블록)')
                    return null
                }

                console.error('🚨 Firebase 로그인 실패:', error)
                return false
            }
        },

        // 깃허브 로그인
        async loginWithGithub() {
            try {
                await setPersistence(auth, browserLocalPersistence) // 로그인 유지
                const result = await signInWithPopup(auth, githubProvider)

                // 여기에 방어 코드 추가
                if (!result || !result.user) {
                    console.warn('🚨 로그인 취소 또는 중단됨 (result 또는 result.user 없음)')
                    return null
                }

                // Firebase에서 받아온 사용자 정보 저장
                this.user = result.user
                this.token = await result.user.getIdToken(true)

                // API에서 유저 정보 가져오기
                try {
                    const res = await getUserInfo(this.token)
                    this.userId = res.data.profileId
                    return true
                } catch (apiError) {
                    console.error('🚨 유저 정보 가져오기 실패:', apiError)
                    return false
                }
            } catch (error) {
                if (
                    error.code === 'auth/popup-closed-by-user' ||
                    error.code === 'auth/cancelled-popup-request'
                ) {
                    console.warn('🚨 사용자가 로그인 팝업을 닫았습니다. (catch 블록)')
                    return null
                }

                console.error('🚨 Firebase 로그인 실패:', error)
                return false
            }
        },

        async logout() {
            await auth.signOut()
            this.user = null
            this.token = null
            this.userId = null
            router.push({ name: 'dashBoard' }) // 홈 페이지로 이동
        },

        // 로그인 유지 기능 추가
        async fetchUser() {
            onAuthStateChanged(auth, async (user) => {
                if (user) {
                    this.user = user
                    this.token = await user.getIdToken(true)
                    // console.log('dfkslfjalsjfklsajfklsjflasfj')
                    // console.log('dfkslfjalsjfklsajfklsjflasfj', this.token)

                    // API에서 추가 유저 정보 가져오기
                    try {
                        const res = await getUserInfo(this.token)
                        this.userId = res.data.profileId // 로그인 유지 시에도 userId 설정
                    } catch (error) {
                        console.error('🚨 유저 정보 가져오기 실패:', error)
                    }
                } else {
                    this.user = null
                    this.token = null
                    this.userId = null // 로그아웃 시 userId 초기화
                }
            })
        },
    },
})
