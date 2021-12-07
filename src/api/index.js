import Vue from 'vue';
import * as appApi from './modules/app';
import * as attachmentApi from './modules/attachment';
import * as routerMenuApi from './modules/systemSetting/routerMenu';
import * as userApi from './modules/systemSetting/user';
import * as organizationApi from './modules/systemSetting/organization';
import * as projectApi from './modules/systemSetting/project';
import * as dictionaryApi from './modules/systemSetting/dictionary';
import * as onlineManualsApi from './modules/systemSetting/onlineManuals';
import * as operationLogApi from './modules/systemSetting/operationLog';
import * as ruleConfigurationApi from './modules/systemSetting/ruleConfiguration';
import * as authorizationApi from './modules/systemSetting/authorization';
import * as contractApi from './modules/implementationManagement/contract';
import * as disclosureApi from './modules/implementationManagement/disclosure';
import * as reportApi from './modules/implementationManagement/report';
import * as setProjectApplyApi from './modules/setProject/setProjectApply';
import * as checkFinalApi from './modules/checkManagement/checkFinal'

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
    reportApi,
    setProjectApplyApi,
    checkFinalApi,
);

Vue.prototype.$API = api
