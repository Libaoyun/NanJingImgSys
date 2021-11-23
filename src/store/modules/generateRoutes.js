import { constantRoutes } from '@/router/modules/constantRoutes'
import { apiRouteList } from '@/router/modules/asyncRoutes'
import { filterAsyncRouter, deepClone } from '@/utils/filterAsyncRouter';
import { apiGenerateRoute } from '@/api/modules/app';
import { Loading } from 'element-ui';

const generateRoutes = {
  state: {
    routers: constantRoutes,
    addRouters: []
  },
  mutations: {
    SET_ROUTERS: (state, routers) => {
      state.addRouters = routers
      state.routers = constantRoutes.concat(routers)
    }
  },
  actions: {
    GenerateRoutes ({ commit }, data) {
        return new Promise((resolve,reject) => {
          const loading = Loading.service({
            lock: true,
            text: '正在加载中...',
            background: 'rgba(255, 255, 255,.5)'
          });
          apiGenerateRoute().then(res => {
                res.data&&res.data.forEach(par=>{
                  var isOverview = false;
                  isOverview = par.children&&par.children.some(child=>{
                    return child.path == 'overview'
                  })
                  if(isOverview){
                    par.redirect = 'overview'
                  }
                })
                // var list = deepClone(apiRouteList)
                let getRouter = filterAsyncRouter(res.data)
                let errorPage = { path: '*', redirect: '/', hidden: true }
                getRouter.push(errorPage)
                commit('SET_ROUTERS', getRouter)
                loading.close();
                resolve(res)
            }).catch(error => {
                loading.close();
                reject(error)
            })
        })
    }
  }
}

export default generateRoutes
