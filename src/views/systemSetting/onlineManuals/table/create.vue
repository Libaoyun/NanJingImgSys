<template>
    <el-dialog 
        title="上传模板"
        :visible="createDialog"
        :close-on-click-modal="false"
        @close="closeDialog"
        width="500px">
        <div class="content">
            <el-form ref="fileInfoForm" :rules="formRules" :model="fileInfo" size="mini" label-position="right" label-width="100px">
                <el-form-item label="上传附件:" prop="fileList">
                    <el-upload class="upload" action="/" :file-list="fileInfo.fileList" :on-change="uploadChange"  ref="upload" :before-upload="beforeUpload" :http-request="handleUpolad"  :show-file-list="false">
                        <el-button size="mini" type="primary">+点击上传模板附件</el-button>
                    </el-upload>  
                    <span>{{uploadName||'未选择'}}</span>
                </el-form-item>
                <el-form-item label="备注:" prop="remark">
                    <el-input  v-model="fileInfo.remark" placeholder="请输入备注" type="textarea"></el-input>
                </el-form-item>
                <el-form-item label="是否启用:" prop="status">
                    <el-select v-model="fileInfo.status" placeholder="请选择">
                        <el-option label="启用" value="1"></el-option>
                        <el-option label="禁用" value="0"></el-option>
                    </el-select>
                </el-form-item>
            </el-form>
        </div>
        <span slot="footer" class="dialog-footer">
            <el-button size="mini" @click="closeDialog">取消</el-button>
            <el-button size="mini" type="primary" @click="submitBtn" :loading="loadingBtn == 1">确定</el-button>
        </span> 
    </el-dialog>
</template>

<script>
import { Component, Vue, Prop, Watch } from 'vue-property-decorator'
import tableMixin from '@/mixins/tableMixin'

@Component({
    name: 'create'
})
export default class extends tableMixin {
    @Prop() createDialog
    @Prop() loadingBtn
    fileMaxSize = 10 * 1024 * 1024;
    fileInfo = {
        status:'1',
        remark: '',
        fileList:[]
    }
    uploadName = ''
    validateFile = (rule,value,callback)=>{
        if(this.fileInfo.fileList.length===0){
            callback(new Error('请上传文件'))
        }else{
            callback()
        }
        callback();
    }
    formRules = {
        fileList:[
            { required: true, message: '请上传文件', trigger: 'change' },
            { validator: this.validateFile, trigger: 'change' }
        ]
    }

    closeDialog() {
        this.$emit('update:createDialog',false);
        this.$refs['fileInfoForm'].resetFields();
    }
    submitBtn() {
        this.$refs['fileInfoForm'].validate((valid) => {
          if (valid) {
            this.$emit('createBookFile',this.fileInfo);
          } else {
            return false;
          }
        });
    }
    // 文件个数上传限制
    handleExceed(){
        this.$message({
            type:'info',
            message:'最多只允许上传10个附件！'
        })
    }
    // 上传
    beforeUpload(file){
        if(file.size<=this.fileMaxSize){
            this.fileInfo.fileList[0] = this.getFile(file)
        }else{
            this.$message({
                type:'info',
                message:'单个文件最大不超过10MB'
            })
            return false;
        }
    }
    // 获取一个新的文件对象
    getFile(file){
        return {
            fileName:file.name,
            fileSize:file.size,
            creatorUserName:this.$store.getters.userInfo.name,
            uid:file.uid
        }
    }
    // 自定义上传
    handleUpolad(file){
        var fd = new FormData();
        fd.append('file',file.file);
        this.$API.apiUploadFile(fd).then(res=>{
            this.fileInfo.fileList[0] = res.data
            this.uploadName = res.data.fileName
            return true;
        }).catch(err=>{})
    }
    uploadChange (file,filelist) {
        if(filelist.length&&filelist.length>1) {
            filelist.splice(0,1);
        }else if(filelist.length&&filelist.length===1) {
            this.$refs.fileInfoForm.clearValidate(['fileList']);
        }
    }
}
</script>

<style lang="scss" scoped>
</style>
