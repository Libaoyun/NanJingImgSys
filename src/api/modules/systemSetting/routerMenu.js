import fetch from '@/api/request'

// 查询菜单 
export function apiGetRouterMenuTree(data) {
    return fetch({
      url: '/ocloudware/menuMangement/queryTree',
      method: 'post',
      data
    })
}

// 查询节点
export function apiGetNodeTree(data) {
    return fetch({
      url: '/ocloudware/menuMangement/queryNode',
      method: 'post',
      data
    })
}

// 新增节点
export function apiCreateNode(data) {
    return fetch({
      url: '/ocloudware/menuMangement/saveNode',
      method: 'post',
      data
    })
}

// 更新节点
export function apiUpdateNode(data) {
    return fetch({
      url: '/ocloudware/menuMangement/updateNode',
      method: 'post',
      data
    })
}

// 更新菜单树形
export function apiUpdateTree(data) {
    return fetch({
      url: '/ocloudware/menuMangement/updateTree',
      method: 'post',
      data
    })
}

// 删除节点
export function apiDeleteNode(data) {
    return fetch({
      url: '/ocloudware/menuMangement/deleteNode',
      method: 'post',
      data
    })
}