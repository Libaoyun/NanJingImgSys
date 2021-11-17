import { apiRouteList } from '@/router/modules/asyncRoutes'
import { filterAsyncRouter, deepClone } from '@/utils/filterAsyncRouter'

const generateRoutes = {
  state: {
    addRouters: apiRouteList
  },
  mutations: {
    SET_ROUTERS: (state, routers) => {
      state.addRouters = routers
    }
  },
  actions: {
    GenerateRoutes ({ commit }, data) {
        return new Promise(resolve => {
          // 根据接口返回用户角色过滤
          var list = deepClone(apiRouteList)
          let getRouter = filterAsyncRouter(list)
          let errorPage = { path: '*', redirect: '/404', hidden: true }
          getRouter.push(errorPage)
          console.log(getRouter)
          commit('SET_ROUTERS', getRouter)
          resolve()
        })
    }
  }
}

export default generateRoutes
