import fetch from '../request'
import axios from 'axios'
import Vue from 'vue'
import { ACCESS_TOKEN } from '@/utils/storage'

// 用户登录
export function apiUserLogin(data) {
    return fetch({
        url: '/rdexpense/login/externalUser',
        method: 'post',
        data,
    });
}

// 用户退出
export function apiLogout(data) {
  return fetch({
      url: '/rdexpense/login/logout',
      method: 'post',
      data,
  })
}

// 获取用户信息
export function apiGetUserInfo(data) {
  return fetch({
      url: '/rdexpense/login/getUserInfo',
      method: 'post',
      data,
  })
}

// 获取路由菜单
export function apiGenerateRoute(params) {
    return fetch({
        url: '/rdexpense/menu/queryRoutingMenuTree',
        method: 'get',
        params,
    });
}

// 导出excel，pdf,合同
export function apiExportFile({url, data}) {
    return axios({
        headers: {
            token: Vue.ls.get(ACCESS_TOKEN),
        },
        responseType: 'blob',
        url: url,
        method: 'post',
        data,
    });
}
// 获取数据字典数据
export function apiGetDictionariesList(data) {
    return fetch({
        url: '/rdexpense/dataDictionary/queryPullDownDictionariesList',
        method: 'post',
        data,
    });
}
