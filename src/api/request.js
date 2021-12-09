import Vue from 'vue'
import axios from 'axios'
import store from '@/store'
import { Message } from 'element-ui'
import { ACCESS_TOKEN } from '@/utils/storage'

const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API, // url = base url + request url
  timeout: 200000
  // withCredentials: true // send cookies when cross-domain requests
})
let allowTips = true
// Request interceptors
service.interceptors.request.use(
  (config) => {
    // console.log(config)
    // Add X-Access-Token header to every request, you can add other custom headers here
    if (Vue.ls.get(ACCESS_TOKEN)) {
      config.headers['token'] = Vue.ls.get(ACCESS_TOKEN)
    }
    if(!config.params){config.params={}}
    config.params['t'] = new Date().getTime()

    return config
  },
  (error) => {
    Promise.reject(error)
  }
)

// Response interceptors
service.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code == 0 || res.status == 0) {
      allowTips = true
      return response.data
    } else {
      allowTips && Message({
        message: res.msg || 'Error',
        type: 'error',
        duration: 5 * 1000
      })
      let reloginErrorList = [20060,20012]
      if(reloginErrorList.includes(res.code)){
        allowTips && store.dispatch('Logout',{reLogin:true})
        allowTips = false
      }
      return Promise.reject(new Error(res.msg || 'Error'))
    }
  },
  (error) => {
    let msg = null
    if(error.code == 'ECONNABORTED'){
      msg = '请求超时！请稍后再试'
    }
    Message({
      message: msg || error.message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default service
