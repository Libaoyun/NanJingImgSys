import Vue from 'vue'
import * as appApi from './modules/app'
import * as attachmentApi from './modules/attachment'
import * as routerMenuApi from './modules/systemSetting/routerMenu'
import * as userApi from './modules/systemSetting/user'
import * as organizationApi from './modules/systemSetting/organization'
import * as projectApi from './modules/systemSetting/project'
import * as dictionaryApi from './modules/systemSetting/dictionary'
import * as onlineManualsApi from './modules/systemSetting/onlineManuals'
import * as operationLogApi from './modules/systemSetting/operationLog'
import * as ruleConfigurationApi from './modules/systemSetting/ruleConfiguration'
import * as authorizationApi from './modules/systemSetting/authorization'
<<<<<<< HEAD
import * as contractApi from './modules/implementationManagement/contract'
import * as disclosureApi from './modules/implementationManagement/disclosure'
import * as reportApi from './modules/implementationManagement/report'

let api = Object.assign(
  appApi,
  attachmentApi,
  routerMenuApi,
  userApi,
  organizationApi,
  projectApi,
  dictionaryApi,
  onlineManualsApi,
  operationLogApi,
  ruleConfigurationApi,
  authorizationApi,
  contractApi,
  disclosureApi,
  reportApi
=======
import * as flowManageApi from './modules/systemSetting/flowManage'
import * as setProjectApplyApi from './modules/setProject/setProjectApply'

let api = Object.assign(
    appApi,
    attachmentApi,
    routerMenuApi,
    userApi,
    organizationApi,
    projectApi,
    dictionaryApi,
    onlineManualsApi,
    operationLogApi,
    ruleConfigurationApi,
    authorizationApi,
    flowManageApi,
    setProjectApplyApi,
>>>>>>> d942d378c84dec860bf8c2030c2b5581dc226d25
)

Vue.prototype.$API = api
