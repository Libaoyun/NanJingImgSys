import fetch from '@/api/request'

// 新增经济交底书
export function apiAddDisclosure(data) {
  return fetch({
    url: '/rdexpense/disclosure/addDisclosurePaper',
    method: 'post',
    data,
  })
}

// 删除经济交底书
export function apiDeleteDisclosure(data) {
  return fetch({
    url: '/rdexpense/disclosure/deleteDisclosurePaper',
    method: 'post',
    data,
  })
}

// 查询经济交底书详情
export function apiGetDisclosureDetail(data) {
  return fetch({
    url: '/rdexpense/disclosure/getDisclosurePaperDetail',
    method: 'post',
    data,
  })
}

// 查询经济交底书列表
export function apiGetDisclosureList(data) {
  return fetch({
    url: '/rdexpense/disclosure/queryDisclosureList',
    method: 'post',
    data,
  })
}

// 编辑经济交底书
export function apiUpdateDisclosure(data) {
  return fetch({
    url: '/rdexpense/disclosure/updateDisclosurePaper',
    method: 'post',
    data,
  })
}

//下载经济交底书模板
export function apiDownloadDisclosureTemplate(data) {
  return fetch({
    url: '/rdexpense/disclosure/downloadDisclosureTemplate',
    method: 'post',
    data,
  })
}
