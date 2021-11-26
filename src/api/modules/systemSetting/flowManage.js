import fetch from '@/api/request'

// 新增流程
export function apiAddFlow(data) {
    return fetch({
      url: '/rdexpense/flow/addFlow',
      method: 'post',
      data
    })
}

// 删除流程
export function apiDeleteFlow(data) {
    return fetch({
      url: '/rdexpense/flow/deleteFlow',
      method: 'post',
      data
    })
}

// 查看流程
export function apiGetFlow(data) {
    return fetch({
      url: '/rdexpense/flow/getFlow',
      method: 'post',
      data
    })
}

// 编辑流程
export function apiUpdateFlow(data) {
    return fetch({
      url: '/rdexpense/flow/updateFlow',
      method: 'post',
      data
    })
}

// 查询审批人
export function apiGetHandleRangeUser(data) {
    return fetch({
      url: '/rdexpense/flow/queryFlowUser',
      method: 'post',
      data
    })
}