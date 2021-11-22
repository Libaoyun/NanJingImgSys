import fetch from '@/api/request'

// create update delete getxxxInfo getxxxList
// 查询授权项目 
export function apiGetProjectTree() {
    return fetch({
      url: '/rdexpense/permission/queryOrganization',
      method: 'get'
    })
}
// 查询授权组织 
export function apiGetOrganizationTree() {
  return fetch({
    url: '/rdexpense/permission/queryDeptTree',
    method: 'get'
  })
}

// 获取已授权工程树用户
export function apiGetAuthProjectUser(data) {
    return fetch({
      url: '/rdexpense/permission/queryAuthorizedUser',
      method: 'post',
      data
    })
}

// 获取未授权工程树用户
export function apiGetStationUser(data) {
  return fetch({
    url: '/rdexpense/permission/queryUser',
    method: 'post',
    data
  })
}

// 删除已授权工程树用户
export function apiDeleteAuthProjectUser(data) {
    return fetch({
      url: '/rdexpense/permission/deleteAuthorization',
      method: 'post',
      data
    })
}

// 新增授权对象
export function apiAddStationUser(data) {
  return fetch({
    url: '/rdexpense/permission/addAuthorization',
    method: 'post',
    data
  })
}

// 查询用户授权菜单
export function apiGetAuthUserMenu(data) {
  return fetch({
    url: '/rdexpense/permission/queryAuthMenu',
    method: 'post',
    data
  })
}
// 查询全部菜单
export function apiGetAllMenu(data) {
  return fetch({
    url: '/rdexpense/permission/queryAuthMenu',
    method: 'post',
    data
  })
}

// 编辑用户授权菜单
export function apiUpdateAuthUserMenu(data) {
  return fetch({
    url: '/rdexpense/permission/updateAuthMenu',
    method: 'post',
    data
  })
}

// 新增授权
export function apiCreateAuthorizeUser(data){
  return fetch({
    url: '/urequipment/permission/saveAuthorizeUser',
    method: 'post',
    data
  })
}