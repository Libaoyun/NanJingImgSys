import fetch from '@/api/request'

// 获取数据字典列表数据
export function apiGetDictionaryList(data) {
    return fetch({
      url: '/rdexpense/dataDictionary/queryDictionaryList',
      method: 'post',
      data
    })
}

// 新增数据
export function apiCreateDictionary(data) {
    return fetch({
      url: '/rdexpense/dataDictionary/saveDictionary',
      method: 'post',
      data
    })
}

// 删除数据
export function apiDeleteDictionary(data) {
    return fetch({
      url: '/rdexpense/dataDictionary/deleteDictionary',
      method: 'post',
      data
    })
}

// 更新数据
export function apiUpdateDictionary(data) {
    return fetch({
      url: '/rdexpense/dataDictionary/updateDictionary',
      method: 'post',
      data
    })
}


// 新增字典类别
export function apiCreateDictionaryType(data) {
  return fetch({
    url: '/rdexpense/dataDictionary/saveDictionType',
    method: 'post',
    data
  })
}

// 查询数据字典类型表的数据
export function apiGetDictionaryTree() {
  return fetch({
    url: '/rdexpense/dataDictionary/queryDictionaryType',
    method: 'get',
  })
}

// 删除数据字典类别
export function apiDeleteDictionaryTree(data) {
  return fetch({
    url: '/rdexpense/dataDictionary/removeDictionType',
    method: 'post',
    data
  })
}

// 修改数据字典类别
export function apiUpdateDictionaryType(data) {
  return fetch({
    url: '/rdexpense/dataDictionary/modifyDictionType',
    method: 'post',
    data
  })
}