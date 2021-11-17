<template>
    <el-upload
        class="upload-excel"
        action=""
        :on-change="handleChange"
        :on-remove="handleRemove"
        :file-list="fileListUpload"
        :limit="1"
        accept=".xls,.xlsx"
        :auto-upload="false"
        :show-file-list="false">
        <el-button size="small" icon="el-icon-upload">导入</el-button>
    </el-upload>
</template>

<script>
import { Component, Vue, Prop } from 'vue-property-decorator'
import { fomatFloat } from '@/utils/index'
@Component({
    name: 'uploadExcel'
})
export default class extends Vue {
    @Prop() excelColumnName;
    @Prop({default:1}) titleLevel  // 几级头部（默认一级）
    @Prop({default:true}) isHandle //是否处理outdata

    fileListUpload= []
    fileTemp = null

    handleChange(file, fileList){
        this.fileTemp = file.raw
        this.fileListUpload = []
        console.log('this.fileTemp',file,this.fileTemp)
        if(this.fileTemp){
            this.importExcel(this.fileTemp)
            // if((this.fileTemp.type == 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet') || (this.fileTemp.type == 'application/vnd.ms-excel')){
            //     this.importExcel(this.fileTemp)
            // } else {
            //     this.$message({
            //         type:'warning',
            //         message:'附件格式错误！'
            //     })
            // }
        } else {
            this.$message({
                type:'warning',
                message:'请上传附件！'
            })
        }
    }
    handleRemove(file,fileList){
        this.fileTemp = null
    }
    importExcel(obj) {
        let _this = this;
        // 通过DOM取文件数据
        this.file = obj
        var rABS = false; //是否将文件读取为二进制字符串
        var f = this.file;
        var reader = new FileReader();
        //if (!FileReader.prototype.readAsBinaryString) {
        FileReader.prototype.readAsBinaryString = function(f) {
            var binary = "";
            var rABS = false; //是否将文件读取为二进制字符串
            var pt = this;
            var wb; //读取完成的数据
            var outdata;
            var reader = new FileReader();
            reader.onload = function(e) {
                var bytes = new Uint8Array(reader.result);
                var length = bytes.byteLength;
                for(var i = 0; i < length; i++) {
                    binary += String.fromCharCode(bytes[i]);
                }
                var XLSX = require('xlsx');
                if(rABS) {
                    wb = XLSX.read(btoa(fixdata(binary)), { //手动转化
                        type: 'base64'
                    });
                } else {
                    wb = XLSX.read(binary, {
                        type: 'binary'
                    });
                }
                outdata = XLSX.utils.sheet_to_json(wb.Sheets[wb.SheetNames[0]]);//outdata就是你想要的东西
                this.da = [...outdata]
                console.log(_this.titleLevel)
                let titleArr = this.da.slice(0,_this.titleLevel);
                let title = {};
                titleArr.forEach(item=>{
                    title = Object.assign(title,item);
                })
                console.log('title',title)
                this.da = this.da.slice(_this.titleLevel,this.da.length);
                console.log('da',this.da)
                let arr = [];
                this.da.forEach(item=>{
                    var obj = {};
                    for(var key in item){
                        obj[title[key]] = item[key];
                    }
                    arr.push(obj);
                })
                console.log('arr',arr)
                let result = []
                var filnalArr = _this.isHandle ? arr : outdata
                filnalArr.forEach(v => {
                    let obj = {}
                    for(let i in _this.excelColumnName){
                       obj[_this.excelColumnName[i].value] = v[_this.excelColumnName[i].label]
                    }
                    result.push(obj)
                })
                // 对小数位超过八位的小数进行四舍五入处理
                // var reg = new RegExp(/^\d+\.?\d*$/);
                // result.forEach(item => {
                //     for(let i in item){
                //         if(reg.test(item[i])){
                //             item[i] = fomatFloat(item[i])
                //         }
                //     }
                // })
                console.log('result',result)
                _this.$emit('afterUploadExcel',result)
                return result
        }
        reader.readAsArrayBuffer(f);
    }
    
    if(rABS) {
        reader.readAsArrayBuffer(f);
    } else {
        reader.readAsBinaryString(f);
    }
}
}
</script>

<style lang="scss" scoped>
.upload-excel{
    display: inline-block;
    margin-left: 6px;
    margin-right: 10px;
}
</style>
