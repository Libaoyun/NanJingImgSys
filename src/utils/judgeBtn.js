import { Message } from 'element-ui'
import store from '@/store'

/**
 * 
 * @param {*} data     表格选中数据
 * @param {*} type   按钮类型
 * 审批状态：
 * DICT10070001: 已保存
 * DICT10070002: 已提交
 * DICT10070003: 审批中
 * DICT10070004: 审批不通过
 * DICT10070005: 已完成
 */
export function judgeBtn(data, type){
    switch(type){
        case 'edit':
            return editBtn(data)
        case 'delete':
            return deleteBtn(data)
        case 'submit':
            return submitBtn(data)
        case 'back':
            return backBtn(data)
        case 'approve':
            return approveBtn(data)
        case 'export':
            return exportBtn(data)
        case 'con':
            return conBtn(data)
    }

}

// 编辑按钮
function editBtn(data){
    let flag = true
    if(data.length !== 1){
        Message({
            type: 'info',
            message: '请选择一条记录!'
        })
        flag = false
    }else{
        if(data[0].processStatus !== 'DICT10070001'){
            flag = false
            Message({
                type: 'info',
                message: '只有已保存的数据才可以编辑！'
            })
        }
    }
    return flag
}

// 删除按钮
function deleteBtn(data){
    let flag = true
    if(data.length == 0){
        Message({
            type: 'info',
            message: '请至少选择一条记录!'
        })
        flag = false
    }else{
        let statusFlag = data.some((item)=>{
            return item.processStatus !== 'DICT10070001'
        })
        if(statusFlag){
            Message({
                type: 'info',
                message: '只有已保存的数据才可以删除！'
            })
            flag = false
        }

    }
    return flag
}

// 提交按钮
function submitBtn(data){
    let flag = true
    if(data.length !== 1){
        Message({
            type: 'info',
            message: '请选择一条已保存记录进行提交!'
        })
        flag = false
    }else{
        if(data[0].processStatus !== 'DICT10070001'){
            flag = false
            Message({
                type: 'info',
                message: '请选择一条已保存记录进行提交！'
            })
        }
    }
    return flag
}

// 撤销按钮
function backBtn(data){
    let flag = true
    if(data.length !== 1){
        Message({
            type: 'info',
            message: '请选择一条记录!'
        })
        flag = false
    }else{
        if(store.getters.userInfo.id != data[0].creatorUserId){
            flag = false
            Message({
                type: 'info',
                message: '只有自己已提交的数据才可以撤销！'
            })
        }
        if(data[0].processStatus !== 'DICT10070002'){
            flag = false
            Message({
                type: 'info',
                message: '只有已提交的数据才可以撤销！'
            })
        }
    }
    return flag
}

// 审批按钮
function approveBtn(data){
    let flag = true
    if(data.length !== 1){
        Message({
            type: 'info',
            message: '请选择一条记录!'
        })
        flag = false
    }else{
        if (!data[0].nextParticipantId) {
            Message({
                type: 'info',
                message: '该记录已审批完成或无该记录审批权限!'
            })
            return
        }
        let arr = data[0].nextParticipantId.split(',')
        let statusFlag = arr.some((item)=>{
            return item == store.getters.userInfo.id
        })
        if(statusFlag){
            flag = true
        }else{
            flag = false
            Message({
                type: 'info',
                message: '无该记录审批权限!'
            })
        }
    }
    return flag
}

// 导出按钮
function exportBtn(data){
    let flag = true
    if(data.length == 0){
        Message({
            type: 'info',
            message: '请至少选择一条记录!'
        })
        flag = false
    }
    return flag
}

// 导出合同
function conBtn(data){
    let flag = true
    if(data.length !== 1){
        Message({
            type: 'info',
            message: '请选择一条记录!'
        })
        flag = false
    }
    return flag
}