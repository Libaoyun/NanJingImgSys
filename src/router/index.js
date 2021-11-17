import Vue from 'vue'
import VueRouter from 'vue-router'
import { constantRoutes } from './modules/constantRoutes'
import store from '@/store'

Vue.use(VueRouter)

// 解决控制台路由报错
const routerPush = VueRouter.prototype.push
  VueRouter.prototype.push = function push(location) {
  return routerPush.call(this, location).catch(error=> error)
}

const createRouter = () =>
  new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    scrollBehavior: () => ({ y: 0 }),
    routes: constantRoutes
  })

// 重置路由，不然退出直接登录会有重复路由
export function resetRouter (data) {
  return new Promise((resolve)=>{
    if(data){
      store.dispatch('GenerateRoutes', data).then(() => {
        const newRouter = createRouter()
        newRouter.addRoutes(store.getters.addRouters)
        router.matcher = newRouter.matcher
        resolve();
      })
    }else{
      // 为了切换用户角色后，首页重定向，要先清除之前的路由
      const newRouter = createRouter()
      router.matcher = newRouter.matcher
      resolve();
    }
  })
}

const router = createRouter();


export default router
