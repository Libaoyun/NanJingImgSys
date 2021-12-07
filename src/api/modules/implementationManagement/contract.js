import fetch from '@/api/request';

// 新增合同
export function apiAddContract(data) {
  return fetch({
    url: '/rdexpense/projContractSign/addContract',
    method: 'post',
    data,
  });
}

// 删除合同
export function apiDeleteContract(data) {
  return fetch({
    url: '/rdexpense/projContractSign/deleteContract',
    method: 'post',
    data,
  });
}

// 查询合同列表
export function apiGetContractList(data) {
  return fetch({
    url: '/rdexpense/projContractSign/queryProjContractList',
    method: 'post',
    data,
  });
}

// 编辑合同
export function apiUpdateContract(data) {
  return fetch({
    url: '/rdexpense/projContractSign/updateContract',
    method: 'post',
    data,
  });
}

//导出excel
export function apiExportExcel(data) {
  return fetch({
    url: '/rdexpense/projContractSign/exportExcel',
    method: 'post',
    data,
  });
}

//导出pdf
export function apiExportPdf(data) {
  return fetch({
    url: '/rdexpense/projContractSign/exportPdf',
    method: 'post',
    data,
  });
}
