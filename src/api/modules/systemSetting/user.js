import fetch from '@/api/request'

// 新增员工 
export function apiCreateUser(data) {
    return fetch({
      url: '/ocloudware/user/addUser',
      method: 'post',
      data
    })
}

// 删除员工 
export function apiDeleteUser(data) {
    return fetch({
      url: '/ocloudware/user/deleteUser',
      method: 'post',
      data
    })
}

// 查询职务 
export function apiGetPosition(data) {
    return fetch({
      url: '/ocloudware/user/getPost',
      method: 'post',
      data
    })
}

// 查询员工详情
export function apiGetUserDetail(data) {
    return fetch({
      url: '/ocloudware/user/getUserDetail',
      method: 'post',
      data
    })
}

// 查询员工详情
export function apiGetUserList(data) {
    return fetch({
      url: '/ocloudware/user/queryUserList',
      method: 'post',
      data
    })
}

// 密码重置
export function apiResetPassword(data) {
    return fetch({
      url: '/ocloudware/user/resetPassword',
      method: 'post',
      data
    })
}

// 更新密码
export function apiUpdatePassword(data) {
    return fetch({
      url: '/ocloudware/user/updatePassword',
      method: 'post',
      data
    })
}

// 密码重置
export function apiUpdateUser(data) {
    return fetch({
      url: '/ocloudware/user/updateUser',
      method: 'post',
      data
    })
}