import Vue from 'vue'
import * as appApi from './modules/app'
import * as routerMenuApi from './modules/systemSetting/routerMenu'
import * as userApi from './modules/systemSetting/user'
import * as organizationApi from './modules/systemSetting/organization'
import * as projectApi from './modules/systemSetting/project'

let api = Object.assign(
    appApi,
    routerMenuApi,
    userApi,
    organizationApi,
    projectApi
)

Vue.prototype.$API = api