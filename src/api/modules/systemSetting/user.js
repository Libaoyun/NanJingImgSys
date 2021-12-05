import fetch from '@/api/request'

// 新增用户
export function apiCreateUser(data) {
  return fetch({
    url: '/rdexpense/user/addUser',
    method: 'post',
    data,
  })
}

// 删除用户
export function apiDeleteUser(data) {
  return fetch({
    url: '/rdexpense/user/deleteUser',
    method: 'post',
    data,
  })
}

// 查询职务
export function apiGetPosition(data) {
  return fetch({
    url: '/rdexpense/user/getPost',
    method: 'post',
    data,
  })
}

// 查询用户详情
export function apiGetUserDetail(data) {
  return fetch({
    url: '/rdexpense/user/getUserDetail',
    method: 'post',
    data,
  })
}

// 查询用户详情
export function apiGetUserList(data) {
  return fetch({
    url: '/rdexpense/user/queryUserList',
    method: 'post',
    data,
  })
}

// 密码重置
export function apiResetPassword(data) {
  return fetch({
    url: '/rdexpense/user/resetPassword',
    method: 'post',
    data,
  })
}

// 更新密码
export function apiUpdatePassword(data) {
  return fetch({
    url: '/rdexpense/user/updatePassword',
    method: 'post',
    data,
  })
}

// 密码重置
export function apiUpdateUser(data) {
  return fetch({
    url: '/rdexpense/user/updateUser',
    method: 'post',
    data,
  })
}
