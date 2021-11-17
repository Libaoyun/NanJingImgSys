import fetch from '../request'


// 用户登录
export function apiUserLogin(data) {
    return fetch({
      url: '/ocloudware/login/externalUser',
      method: 'post',
      data
    })
}

// 获取用户信息
export function apiGetUserInfo(data) {
  return fetch({
    url: '/ocloudware/login/getUserInfo',
    method: 'post',
    data
  })
}

// 获取路由菜单
export function apiGenerateRoute (params) {
  return fetch({
      url: '/ocloudware/menu/queryRoutingMenuTree',
      method: 'get',
      params
  })
}