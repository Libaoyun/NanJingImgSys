import Vue from 'vue'
import Vuex from 'vuex'
import sidebar from './modules/sidebar'
import generateRoutes from './modules/generateRoutes'
import multiTab from './modules/multiTab'
import user from './modules/user'
import getters from './getters'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
  },
  mutations: {
  },
  actions: {
  },
  modules: {
    sidebar,
    generateRoutes,
    user,
    multiTab
  },
  getters
})
