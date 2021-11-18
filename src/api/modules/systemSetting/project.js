import fetch from '@/api/request'

// 新增项目
export function apiAddProject(data) {
    return fetch({
      url: '/rdexpense/organization/addOrganization',
      method: 'post',
      data
    })
}

// 删除项目
export function apiDeleteProject(data) {
    return fetch({
      url: '/rdexpense/organization/deleteOrganization',
      method: 'post',
      data
    })
}

// 查询项目详情
export function apiGetProjectDetail(data) {
    return fetch({
      url: '/rdexpense/organization/getOrganizationDetail',
      method: 'post',
      data
    })
}

// 查询项目列表
export function apiGetProjectList(data) {
    return fetch({
      url: '/rdexpense/organization/queryOrganizationList',
      method: 'post',
      data
    })
}

// 编辑项目
export function apiUpdateProject(data) {
    return fetch({
      url: '/rdexpense/organization/updateOrganization',
      method: 'post',
      data
    })
}