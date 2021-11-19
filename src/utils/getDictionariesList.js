
import { apiGetDictionariesList } from '@/api/modules/app';
/**
 * 
 * @param {*} dicTypeIds 字典编码
 * return 字典枚举值
 */
export function getDictionariesList(dicTypeIds){
    return new Promise((resolve,reject)=>{
        var statusFilterList = [];
        if(!dicTypeIds){
            resolve(statusFilterList);
        }
        if(!Array.isArray(dicTypeIds)){
            dicTypeIds = [dicTypeIds];
        }
        apiGetDictionariesList({dicTypeIds}).then(res=>{
            if(res.data){
                res.data.forEach((item)=>{
                    statusFilterList.push({
                        label: item.dicEnumName,
                        value: item.dicEnumId,
                        text: item.dicEnumName
                    })
                })
                resolve(statusFilterList);
            }
            reject('接口查询失败');
        })
    })
}

export function getDictionariesText(list,value){
    var result = '';
    if(Array.isArray(list)){
        list.some(item=>{
            if(item.value == value){
                result = item.text || item.label;
                return true;
            }
        })
    }
    return result;
}