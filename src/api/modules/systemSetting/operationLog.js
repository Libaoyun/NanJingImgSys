import fetch from '@/api/request'

// 获取操作日志
export function apiGetOperationLog(data) {
    return fetch({
      url: '/rdexpense/operationLog/queryOperationLogList',
      method: 'post',
      data
    })
}