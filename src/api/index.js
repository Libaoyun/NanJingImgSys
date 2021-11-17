import Vue from 'vue'
import * as appApi from './modules/app'
import * as routerMenuApi from './modules/systemSetting/routerMenu'
import * as userApi from './modules/systemSetting/user'

let api = Object.assign(
    appApi,
    routerMenuApi,
    userApi
)

Vue.prototype.$API = api