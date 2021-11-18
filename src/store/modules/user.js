import Vue from 'vue'
import { Message } from 'element-ui'
import { ACCESS_TOKEN } from '@/utils/storage'
import { resetRouter } from '@/router/index'
import { apiUserLogin, apiGetUserInfo } from '@/api/modules/app'

const user = {
    state: {
        token: '',
        userInfo: null,
        currentOrganization: null
    },
    mutations: {
        SET_TOKEN: (state, data) => {
            state.token = data
        },
        SET_USERINFO: (state, data) => {
            state.userInfo = data
        },
        SET_CURRENT_ORGANIZATION: (state, data) => {
            state.currentOrganization = data
        }
    },
    actions: {
        Login ({ commit }, userInfo) {
            return new Promise((resolve, reject) => {
                apiUserLogin(userInfo).then(res => {
                    Vue.ls.set(ACCESS_TOKEN, res.data.token || 'token', 24 * 60 * 60 * 1000)
                    console.log(Vue.ls.get(ACCESS_TOKEN))
                    commit('SET_TOKEN', res.data.token)
                    resolve(res)
                }).catch(error => {
                    console.log('login',error)
                    reject(error)
                })
            })
        },
        GetUserInfo ({ commit }) {
            return new Promise((resolve, reject) => {
                apiGetUserInfo().then(res=>{
                    commit('SET_USERINFO', res.data.data)
                    resolve(res.data)
                }).catch(error => {
                    reject(error)
                })
            })
        },
        Logout ({ commit }) {
            return new Promise((resolve, reject) => {
            //     logout().then(res => {
            //         Vue.ls.remove(ACCESS_TOKEN)
            //         Vue.ls.clear()
            //         resolve()
            //   }).catch(error => {
            //     reject(error)
            //   })
                Vue.ls.remove(ACCESS_TOKEN)
                Vue.ls.clear()
                resetRouter()
                resolve()
            })
        }
    }
  }
  
  export default user
  