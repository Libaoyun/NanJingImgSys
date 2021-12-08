import fetch from '@/api/request'

// 查询列表 
export function apiGetExpenseList(data) {
  return fetch({
    url: '/rdexpense/itemExpenses/queryList',
    method: 'post',
    data
  })
}

// 列表 废除
export function apiExpenseAbolish(data) {
  return fetch({
    url: '/rdexpense/itemExpenses/abolish',
    method: 'post',
    data
  })
}

// 列表 删除
export function apiExpenseDelete(data) {
  return fetch({
    url: '/rdexpense/itemExpenses/delete',
    method: 'post',
    data
  })
}

// 列表 导出Excel
export function apiExpenseExportExcel(data) {
  return fetch({
    url: '/rdexpense/itemExpenses/exportExcel',
    method: 'post',
    data
  })
}

// 列表 导出PDF
export function apiExpenseExportPdf(data) {
  return fetch({
    url: '/rdexpense/itemExpenses/exportPdf',
    method: 'post',
    data
  })
}

// detail
export function apiExpenseDetail(data) {
  return fetch({
    url: '/rdexpense/itemExpenses/queryDetail',
    method: 'post',
    data
  })
}

// 审批(同意/回退)
export function apiExpenseApproval(data) {
  return fetch({
    url: '/rdexpense/itemExpenses/approve',
    method: 'post',
    data
  })
}

// 新增或编辑
export function apiExpenseSaveOrUpdate(data) {
  return fetch({
    url: '/rdexpense/itemExpenses/saveOrUpdate',
    method: 'post',
    data
  })
}

// 提交
export function apiExpenseSubmit(data) {
  return fetch({
    url: '/rdexpense/itemExpenses/submit',
    method: 'post',
    data
  })
}

// 根据一级科目&二级科目查询支出预算相关数据
export function apiGetExpenseBudget(data) {
  return fetch({
    url: '/rdexpense/itemExpenses/queryBudgetAccumulated',
    method: 'post',
    data
  })
}