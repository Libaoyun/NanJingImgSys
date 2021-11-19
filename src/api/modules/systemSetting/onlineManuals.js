import fetch from '@/api/request'
// 查询树节点
export function apiGetTreeNode() {
  return fetch({
    url: '/rdexpense/template/searchNode',
    method: 'get',
  })
}
// 查询列表
export function apiGetFileList(data) {
  return fetch({
    url: '/rdexpense/template/searchRecord',
    method: 'post',
    data
  })
}
// 新增节点
export function apiAddTreeNode(data) {
  return fetch({
    url: '/rdexpense/template/addNode',
    method: 'post',
    data
  })
}
// 删除节点
export function apiDeleteTreeNode(data) {
  return fetch({
    url: '/rdexpense/template/deleteNode',
    method: 'post',
    data
  })
}
// 编辑节点
export function apiUpdateTreeNode(data) {
  return fetch({
    url: '/rdexpense/template/updateNode',
    method: 'post',
    data
  })
}
// 新增文件
export function apiAddBoookFile(data) {
  return fetch({
    url: '/rdexpense/template/addRecord',
    method: 'post',
    data
  })
}
// 编辑文件
export function apiUpdateBoookFile(data) {
  return fetch({
    url: '/rdexpense/template/updateRecord',
    method: 'post',
    data
  })
}
// 查看文件
export function apiGetBoookFileDetail(data) {
  return fetch({
    url: '/rdexpense/template/recordDetail',
    method: 'post',
    data
  })
}
// 删除文件
export function apiDeleteBoookFile(data) {
  return fetch({
    url: '/rdexpense/template/deleteRecord',
    method: 'post',
    data
  })
}