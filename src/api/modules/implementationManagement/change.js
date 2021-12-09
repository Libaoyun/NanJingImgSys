import fetch from '@/api/request'

// 查询列表 
export function apiGetChangeList(data) {
  return fetch({
    url: '/rdexpense/itemChange/queryList',
    method: 'post',
    data
  })
}

// 列表 废除
export function apiChangeAbolish(data) {
  return fetch({
    url: '/rdexpense/itemChange/abolish',
    method: 'post',
    data
  })
}

// 列表 删除
export function apiChangeDelete(data) {
  return fetch({
    url: '/rdexpense/itemChange/delete',
    method: 'post',
    data
  })
}

// 列表 导出Excel
export function apiChangeExportExcel(data) {
  return fetch({
    url: '/rdexpense/itemChange/exportExcel',
    method: 'post',
    data
  })
}

// 列表 导出PDF
export function apiChangeExportPdf(data) {
  return fetch({
    url: '/rdexpense/itemChange/exportPdf',
    method: 'post',
    data
  })
}

// detail
export function apiChangeDetail(data) {
  return fetch({
    url: '/rdexpense/itemChange/queryDetail',
    method: 'post',
    data
  })
}

// 审批(同意/回退)
export function apiChangeApproval(data) {
  return fetch({
    url: '/rdexpense/itemChange/approve',
    method: 'post',
    data
  })
}

// 新增或编辑
export function apiChangeSaveOrUpdate(data) {
  return fetch({
    url: '/rdexpense/itemChange/saveOrUpdate',
    method: 'post',
    data
  })
}

// 提交
export function apiChangeSubmit(data) {
  return fetch({
    url: '/rdexpense/itemChange/submit',
    method: 'post',
    data
  })
}

// 查询经费预算
export function apiGetChangeBudget(data) {
  return fetch({
    url: '/rdexpense/itemChange/queryBudget',
    method: 'post',
    data
  })
}

