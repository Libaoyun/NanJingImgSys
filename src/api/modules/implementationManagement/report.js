import fetch from '@/api/request'

// 新增项目进展报告
export function apiAddReport(data) {
  return fetch({
    url: '/rdexpense/report/addReport',
    method: 'post',
    data,
  })
}

// 删除项目进展报告
export function apiDeleteReport(data) {
  return fetch({
    url: '/rdexpense/progressreport/deleteReport',
    method: 'post',
    data,
  })
}

// 查询项目进展报告详情
export function apiGetReportDetail(data) {
  return fetch({
    url: '/rdexpense/report/getReportDetail',
    method: 'post',
    data,
  })
}

// 查询项目进展报告列表
export function apiGetReportList(data) {
  return fetch({
    url: '/rdexpense/progressreport/queryReportList',
    method: 'post',
    data,
  })
}

// 编辑项目进展报告
export function apiUpdateReport(data) {
  return fetch({
    url: '/rdexpense/report/updateReport',
    method: 'post',
    data,
  })
}

//下载项目进展报告模板
export function apiDownloadReportTemplate(data) {
  return fetch({
    url: '/rdexpense/progressreport/downloadTemplate',
    method: 'get',
    data,
  })
}
