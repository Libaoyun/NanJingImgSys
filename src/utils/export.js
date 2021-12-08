import { judgeBtn } from './judgeBtn.js'
import { apiExportFile } from '@/api/modules/app.js'
const FileSaver = require('file-saver')

const MIMETYPE = {
    'excel':'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    'pdf':'application/pdf',
    'word': 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    'zip' : 'application/zip'
}
/**
 * 
 * @param {*} selected  当前表格选中的数据
 * @param {*} fileType  导出的类型 ‘excel’，‘pdf’ : 打印特殊处理print
 * @param {*} {url,data}  接口名，参数值
 */
export function exportFile(selected,fileType,{url,data}, isAllSelected=false){
    return new Promise((resolve,reject) => {
        if(isAllSelected || judgeBtn(selected,'export') ){
            console.log(data)
            apiExportFile({url,data}).then(res=>{
                var fileName = '导出失败';
                try {
                    fileName = decodeURIComponent(res.headers['content-disposition'].split('filename=')[1]);
                } catch (error) {
                    console.log('没有获取到文件名');
                }
                if(fileType === 'print'){
                    var blob = new Blob([res.data],{type:MIMETYPE['pdf']});
                    var href = URL.createObjectURL(blob);
                    window.open('/static/pdf/web/viewer.html?file='+encodeURIComponent(href));
                }else{
                    // var blob = new Blob([res.data],{type:MIMETYPE[fileType]});
                    // var href = URL.createObjectURL(blob);
                    // console.log(href,window.location.host)
                    // var a = document.createElement('a');
                    // var event = new MouseEvent('click');
                    // a.download = fileName;
                    // a.href = href;
                    // a.dispatchEvent(event)
                    FileSaver.saveAs(new Blob([res.data], { type: MIMETYPE[fileType] }), fileName)
                }
                resolve()
            })
            .catch((error)=>{
                console.log(error)
                reject()
            })
    
        }
    })

    
}
