// src/stores/user.js

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
        userTags: null, // 사용자 태그
        userNickname: null, // 사용자 닉네임
        isUserLoaded: false, // ✅ 유저 정보 로드 여부 추가
    }),

    getters: {
        isAuthenticated: (state) => !!state.token, // 로그인 여부 -> 동적으로 계산
    },

    actions: {
        // 구글 로그인
        async loginWithGoogle() {
            try {
                await setPersistence(auth, browserLocalPersistence)
                const result = await signInWithPopup(auth, googleProvider)

                if (!result || !result.user) {
                    console.warn('🚨 로그인 취소 또는 중단됨 (result 또는 result.user 없음)')
                    return null
                }

                this.user = result.user
                this.token = await result.user.getIdToken(true)

                try {
                    await this.fetchUser() // ✅ 로그인 후 fetchUser() 실행
                    return true // fetchUser 성공하면 true 반환
                } catch (error) {
                    console.error('🚨 유저 정보 가져오기 실패, 회원가입 필요:', error)
                    return false // fetchUser 실패하면 false 반환
                }
            } catch (error) {
                console.error('🚨 Firebase 로그인 실패:', error)
                return null
            }
        },
        // 깃허브 로그인
        async loginWithGithub() {
            try {
                await setPersistence(auth, browserLocalPersistence)
                const result = await signInWithPopup(auth, githubProvider)

                if (!result || !result.user) {
                    console.warn('🚨 로그인 취소 또는 중단됨 (result 또는 result.user 없음)')
                    return null
                }

                this.user = result.user
                this.token = await result.user.getIdToken(true)

                try {
                    await this.fetchUser() // ✅ 로그인 후 fetchUser() 실행
                    return true // fetchUser 성공하면 true 반환
                } catch (error) {
                    console.error('🚨 유저 정보 가져오기 실패, 회원가입 필요:', error)
                    return false // fetchUser 실패하면 false 반환
                }
            } catch (error) {
                console.error('🚨 Firebase 로그인 실패:', error)
                return null
            }
        },

        async logout() {
            await auth.signOut()
            this.user = null
            this.token = null
            this.userId = null
            this.userTags = null
            this.userNickname = null
            this.isUserLoaded = false // ✅ 유저 정보 초기화
            router.push({ name: 'home' })
        },

        // 로그인 유지 기능 추가
        async fetchUser() {
            return new Promise((resolve, reject) => {
                onAuthStateChanged(auth, async (user) => {
                    if (user) {
                        this.user = user
                        this.token = await user.getIdToken(true)

                        try {
                            const res = await getUserInfo(this.token)
                            this.userId = res.data.profileId
                            this.userTags = res.data.tags
                            this.userNickname = res.data.nickname
                            this.isUserLoaded = true // ✅ 유저 정보 로드 완료
                            console.log('✅ 유저 정보 불러옴:', this.userTags)
                        } catch (error) {
                            console.error('🚨 유저 정보 가져오기 실패:', error)
                            reject(error) // 실패 시 reject()
                        }
                    } else {
                        this.user = null
                        this.token = null
                        this.userId = null
                        this.userTags = null
                        this.userNickname = null
                        this.isUserLoaded = false
                    }
                    resolve() // ✅ fetchUser()가 완료되었음을 알림
                })
            })
        },
    },
})
