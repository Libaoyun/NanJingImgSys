import Vue from 'vue'
import * as appApi from './modules/app'
import * as attachmentApi from './modules/attachment'
import * as routerMenuApi from './modules/systemSetting/routerMenu'
import * as userApi from './modules/systemSetting/user'
import * as organizationApi from './modules/systemSetting/organization'
import * as projectApi from './modules/systemSetting/project'
import * as dictionaryApi from './modules/systemSetting/dictionary'
import * as onlineManualsApi from './modules/systemSetting/onlineManuals'

let api = Object.assign(
    appApi,
    attachmentApi,
    routerMenuApi,
    userApi,
    organizationApi,
    projectApi,
    dictionaryApi,
    onlineManualsApi
)

Vue.prototype.$API = api