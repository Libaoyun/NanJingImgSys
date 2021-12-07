import fetch from '@/api/request'

// create update delete getxxxInfo getxxxList
// 查询列表 
export function apiGetSetProjectApplyList(data) {
    return fetch({
      url: '/rdexpense/projectApply/queryList',
      method: 'post',
      data
    })
}

// 立项申请(新建/提交)
export function apiCreateSetProjectApply(data) {
    return fetch({
      url: '/rdexpense/projectApply/add',
      method: 'post',
      data
    })
}

// 立项申请(编辑/提交)
export function apiUpdateSetProjectApply(data) {
    return fetch({
      url: '/rdexpense/projectApply/update',
      method: 'post',
      data
    })
}

// 导入拨款计划
export function apiUploadAppropriation(data) {
    return fetch({
      url: '/rdexpense/projectApply/uploadAppropriation',
      method: 'post',
      data
    })
}

// 导入经费预算
export function apiUploadBudget(data) {
    return fetch({
      url: '/rdexpense/projectApply/uploadBudget',
      method: 'post',
      data
    })
}

// 导入主信息
export function apiUploadMain(data) {
    return fetch({
      url: '/rdexpense/projectApply/uploadMain',
      method: 'post',
      data
    })
}

// 导入经费预算（每月预算）
export function apiUploadMonth(data) {
    return fetch({
      url: '/rdexpense/projectApply/uploadMonth',
      method: 'post',
      data
    })
}

// 导入进度计划
export function apiUploadProgress(data) {
    return fetch({
      url: '/rdexpense/projectApply/uploadProgress',
      method: 'post',
      data
    })
}

// 导入立项调研信息
export function apiUploadSurvey(data) {
    return fetch({
      url: '/rdexpense/projectApply/uploadSurvey',
      method: 'post',
      data
    })
}

// 导入参加单位
export function apiUploadUnit(data) {
    return fetch({
      url: '/rdexpense/projectApply/uploadUnit',
      method: 'post',
      data
    })
}

// 导入研究人员（初始）
export function apiUploadUser(data) {
    return fetch({
      url: '/rdexpense/projectApply/uploadUser',
      method: 'post',
      data
    })
}

// 导入全部数据
export function apiUploadAll(data) {
    return fetch({
      url: '/rdexpense/projectApply/uploadAll',
      method: 'post',
      data
    })
}

// 列表提交
export function apiSubmitSetProjectApply(data) {
    return fetch({
      url: '/rdexpense/projectApply/submit',
      method: 'post',
      data
    })
}

// 查询申请详情
export function apiGetSetProjectApplyDetail(data) {
    return fetch({
      url: '/rdexpense/projectApply/queryDetail',
      method: 'post',
      data
    })
}

// 删除申请
export function apiDeleteSetProjectApply(data) {
    return fetch({
      url: '/rdexpense/projectApply/delete',
      method: 'post',
      data
    })
}

// 审批(同意/回退)
export function apiApprovalSetProjectApply(data) {
    return fetch({
      url: '/rdexpense/projectApply/approve',
      method: 'post',
      data
    })
}

// 废除
export function apiAbolishSetProjectApply(data) {
    return fetch({
      url: '/rdexpense/projectApply/abolish',
      method: 'post',
      data
    })
}

// 下载模板
export function apiDownloadSetProjectApply(data) {
    return fetch({
      url: '/rdexpense/file/templateDownLoad',
      method: 'post',
      data
    })
}