import fetch from '@/api/request'

// create update delete getxxxInfo getxxxList
// 查询规则配置列表
export function apiGetRuleConfiguration(params) {
    return fetch({
      url: '/rdexpense/rule/getRuleConfig',
      method: 'get',
      params
    })
  }

// 修改规则配置
export function apiUpdateRuleConfiguration(data) {
    return fetch({
      url: '/rdexpense/rule/saveRuleConfig',
      method: 'post',
      data
    })
  }


