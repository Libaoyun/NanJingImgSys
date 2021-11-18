import fetch from '../request'
// 附件接口

// 上传附件
export function apiUploadFile(data, onUploadProgress){
  return fetch({
    url: '/rdexpense/file/upload',
    method: 'post',
    data,
    onUploadProgress
  })
}

// 删除附件
export function apiDeleteFile(data){
  return fetch({
    url: '/rdexpense/file/delete',
    method: 'post',
    data
  })
}

// 下载附件
export function apiDownloadFile(data){
  return fetch({
    url: '/rdexpense/file/templateDownLoad',
    method: 'post',
    data
  })
}

// // 查询附件
// export function apiGetFile(params){
//   return fetch({
//     url: '/rdexpense/file/queryFile',
//     method: 'get',
//     params
//   })
// }

// // 查询审批记录
// export function apiGetApproveNote(params){
//   return fetch({
//     url: '/rdexpense/flow/queryApproveNote',
//     method: 'get',
//     params
//   })
// }