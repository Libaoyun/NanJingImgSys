import Vue from 'vue'
import router from './router'
import store from './store'
import { ACCESS_TOKEN } from '@/utils/storage'
import { resetRouter } from '@/router/index'

const whiteList = ['/login', '/resetPassword'] // 不重定向白名单
router.beforeEach((to, from, next) => {
  if (whiteList.indexOf(to.path) !== -1) {
    // 在免登录白名单，直接进入
    next()
  } else if (Vue.ls.get(ACCESS_TOKEN)) {
    // 判断是否有token
    if (!store.getters.userInfo) {
      store.dispatch('GetUserInfo', Vue.ls.get(ACCESS_TOKEN)).then((res) => {
        console.log(res)
        resetRouter(res.data).then(() => {
          next({ ...to, replace: true })
        })
      })
    } else {
      next()
    }
  } else {
    next('/login') // 否则全部重定向到登录页
  }
})

// 设置标题
router.afterEach((to, from) => {
  var title = '研究开发费用管理系统'
  to.meta && to.meta.title && (title = to.meta.title + '-' + title)
  document.title = title
})
