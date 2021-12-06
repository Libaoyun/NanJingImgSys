import fetch from '@/api/request'

// 查询列表 
export function apiGetCheckFinalList(data) {
  return fetch({
    url: '/rdexpense/itemClosureCheck/queryItemClosureCheckList',
    method: 'post',
    data
  })
}

// 列表 废除
export function apiCheckFinalAbolish(data) {
  return fetch({
    url: '/rdexpense/itemClosureCheck/abolish',
    method: 'post',
    data
  })
}

// 列表 删除
export function apiCheckFinalDelete(data) {
  return fetch({
    url: '/rdexpense/itemClosureCheck/delete',
    method: 'post',
    data
  })
}

// 列表 导出Excel
export function apiCheckFinalExportExcel(data) {
  return fetch({
    url: '/rdexpense/itemClosureCheck/exportExcel',
    method: 'post',
    data
  })
}

// 列表 导出PDF
export function apiCheckFinalExportPdf(data) {
  return fetch({
    url: '/rdexpense/itemClosureCheck/exportPdf',
    method: 'post',
    data
  })
}

// 查询已审批完成的研发项目申请列表
export function apiCheckFinalProjectList(data) {
  return fetch({
    url: '/rdexpense/itemClosureCheck/queryApplyList',
    method: 'post',
    data
  })
}

// 查询已审批完成的研发项目的人员信息列表
export function apiCheckFinalUserList(data) {
  return fetch({
    url: '/rdexpense/itemClosureCheck/queryApplyUserList',
    method: 'post',
    data
  })
}

// detail
export function apiCheckFinalDetail(data) {
  return fetch({
    url: '/rdexpense/itemClosureCheck/queryDetail',
    method: 'post',
    data
  })
}

// 审批(同意/回退)
export function apiCheckFinalApproval(data) {
  return fetch({
    url: '/rdexpense/itemClosureCheck/approve',
    method: 'post',
    data
  })
}

// 新增或编辑
export function apiCheckFinalSaveOrUpdate(data) {
  return fetch({
    url: '/rdexpense/itemClosureCheck/saveOrUpdate',
    method: 'post',
    data
  })
}

// 提交
export function apiCheckFinalSubmit(data) {
  return fetch({
    url: '/rdexpense/itemClosureCheck/submit',
    method: 'post',
    data
  })
}