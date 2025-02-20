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

                await this.fetchUser() // ✅ 로그인 후 바로 fetchUser() 실행
                return true
            } catch (error) {
                console.error('🚨 Firebase 로그인 실패:', error)
                return false
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

                await this.fetchUser() // ✅ 로그인 후 바로 fetchUser() 실행
                return true
            } catch (error) {
                console.error('🚨 Firebase 로그인 실패:', error)
                return false
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
            return new Promise((resolve) => {
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

// 수정 전 기존 코드
// // src/stores/user.js

// import { defineStore } from 'pinia'
// import {
//     auth,
//     googleProvider,
//     githubProvider,
//     signInWithPopup,
//     setPersistence,
//     browserLocalPersistence,
//     onAuthStateChanged,
// } from '@/firebase'
// import { getUserInfo } from '@/helpers/api' // API 함수 불러오기
// import router from '../router'

// export const useUserStore = defineStore('user', {
//     state: () => ({
//         user: null, // 사용자 정보
//         token: null, // Firebase 토큰
//         userId: null, // 사용자 ID
//         userTags: null, // 사용자 태그
//         userNickname: null, // 사용자 닉네임
//     }),

//     getters: {
//         isAuthenticated: (state) => !!state.token, // 로그인 여부 -> 동적으로 계산
//     },

//     actions: {
//         // 구글 로그인
//         async loginWithGoogle() {
//             try {
//                 await setPersistence(auth, browserLocalPersistence) // 로그인 유지
//                 const result = await signInWithPopup(auth, googleProvider)

//                 // 방어 코드 추가
//                 if (!result || !result.user) {
//                     console.warn('🚨 로그인 취소 또는 중단됨 (result 또는 result.user 없음)')
//                     return null
//                 }

//                 // Firebase에서 받아온 사용자 정보 저장
//                 this.user = result.user
//                 this.token = await result.user.getIdToken(true)

//                 // API에서 유저 정보 가져오기
//                 try {
//                     const res = await getUserInfo(this.token)
//                     this.userId = res.data.profileId
//                     this.userTags = res.data.tags
//                     this.userNickname = res.data.nickname // ✅ 닉네임 저장
//                     return true
//                 } catch (apiError) {
//                     console.error('🚨 유저 정보 가져오기 실패:', apiError)
//                     return false
//                 }
//             } catch (error) {
//                 if (
//                     error.code === 'auth/popup-closed-by-user' ||
//                     error.code === 'auth/cancelled-popup-request'
//                 ) {
//                     console.warn('🚨 사용자가 로그인 팝업을 닫았습니다.')
//                     return null
//                 }

//                 console.error('🚨 Firebase 로그인 실패:', error)
//                 return false
//             }
//         },

//         // 깃허브 로그인
//         async loginWithGithub() {
//             try {
//                 await setPersistence(auth, browserLocalPersistence) // 로그인 유지
//                 const result = await signInWithPopup(auth, githubProvider)

//                 // 방어 코드 추가
//                 if (!result || !result.user) {
//                     console.warn('🚨 로그인 취소 또는 중단됨 (result 또는 result.user 없음)')
//                     return null
//                 }

//                 // Firebase에서 받아온 사용자 정보 저장
//                 this.user = result.user
//                 this.token = await result.user.getIdToken(true)

//                 // API에서 유저 정보 가져오기
//                 try {
//                     const res = await getUserInfo(this.token)
//                     this.userId = res.data.profileId
//                     this.userTags = res.data.tags
//                     this.userNickname = res.data.nickname // ✅ 닉네임 저장
//                     return true
//                 } catch (apiError) {
//                     console.error('🚨 유저 정보 가져오기 실패:', apiError)
//                     return false
//                 }
//             } catch (error) {
//                 if (
//                     error.code === 'auth/popup-closed-by-user' ||
//                     error.code === 'auth/cancelled-popup-request'
//                 ) {
//                     console.warn('🚨 사용자가 로그인 팝업을 닫았습니다.')
//                     return null
//                 }

//                 console.error('🚨 Firebase 로그인 실패:', error)
//                 return false
//             }
//         },

//         async logout() {
//             await auth.signOut()
//             this.user = null
//             this.token = null
//             this.userId = null
//             this.userTags = null
//             this.userNickname = null // ✅ 닉네임 초기화
//             router.push({ name: 'home' }) // 홈 페이지로 이동
//         },

//         // 로그인 유지 기능 추가
//         async fetchUser() {
//             onAuthStateChanged(auth, async (user) => {
//                 if (user) {
//                     this.user = user
//                     this.token = await user.getIdToken(true)

//                     // API에서 추가 유저 정보 가져오기
//                     try {
//                         const res = await getUserInfo(this.token)
//                         this.userId = res.data.profileId
//                         this.userTags = res.data.tags
//                         this.userNickname = res.data.nickname // ✅ 닉네임 저장
//                     } catch (error) {
//                         console.error('🚨 유저 정보 가져오기 실패:', error)
//                     }
//                 } else {
//                     this.user = null
//                     this.token = null
//                     this.userId = null
//                     this.userTags = null
//                     this.userNickname = null // ✅ 닉네임 초기화
//                 }
//             })
//         },
//     },
// })
