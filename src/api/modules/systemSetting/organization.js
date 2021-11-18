import fetch from '@/api/request'

// 显示组织树
export function apiGetOrganizationTree(data) {
    return fetch({
      url: '/rdexpense/department/queryTree',
      method: 'post',
      data
    })
}

// 新增部门
export function apiAddDepartment(data) {
    return fetch({
      url: '/rdexpense/department/addDepartment',
      method: 'post',
      data
    })
}

// 新增职务
export function apiAddPost(data) {
    return fetch({
      url: '/rdexpense/department/addPost',
      method: 'post',
      data
    })
}

// 删除树节点
export function apiDeleteOrgNode(data) {
    return fetch({
      url: '/rdexpense/department/deleteOrgNode',
      method: 'post',
      data
    })
}

// 查询部门详情
export function apiGetDepartmentDetail(data) {
    return fetch({
      url: '/rdexpense/department/getDepartment',
      method: 'post',
      data
    })
}

// 查询职务详情
export function apiGetPostDetail(data) {
    return fetch({
      url: '/rdexpense/department/getPost',
      method: 'post',
      data
    })
}

// 编辑部门
export function apiUpdateDepartment(data) {
    return fetch({
      url: '/rdexpense/department/updateDepartment',
      method: 'post',
      data
    })
}

// 编辑职务
export function apiUpdatePost(data) {
    return fetch({
      url: '/rdexpense/department/updatePost',
      method: 'post',
      data
    })
}

// 拖拽树
export function apiUpdateOTree(data) {
    return fetch({
      url: '/rdexpense/department/updateOTree',
      method: 'post',
      data
    })
}