<template>
    <el-dialog 
        title="编辑文件"
        :visible="editDialog"
        :close-on-click-modal="false"
        @close="closeDialog"
        width="500px">
        <div class="content">
            <el-form ref="fileInfoForm" :rules="formRules" :model="fileInfo" size="mini" label-position="right" label-width="100px">
                <el-form-item label="文件类型:" prop="fileType">
                    <el-select v-model="fileInfo.fileType" placeholder="请选择文件类型">
                        <el-option v-for="item in fileTypeSelect" :key="item" :label="item" :value="item"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="文件名称:" prop="fileName">
                    <el-input  v-model.trim="fileInfo.fileName" placeholder="请输入文件名称"></el-input>
                </el-form-item>
                <el-form-item label="描述信息:" prop="remark">
                    <el-input  v-model="fileInfo.remark" placeholder="请输入与文件名称对应的描述信息" type="textarea"></el-input>
                </el-form-item>
                <el-form-item label="选择文件:" prop="fileList">
                    <el-upload class="upload" action="/" :file-list="fileInfo.fileList" :on-change="uploadChange"  ref="upload" :before-upload="beforeUpload" :http-request="handleUpolad"  :show-file-list="false">
                        <el-button size="mini" type="primary">选择文件</el-button>
                    </el-upload>  
                    <span>{{uploadName||'未选择'}}</span>
                </el-form-item>
            </el-form>
        </div>
        <span slot="footer" class="dialog-footer">
            <el-button size="mini" @click="closeDialog">取消</el-button>
            <el-button size="mini" type="primary" @click="submitBtn" :loading="loadingBtn == 2">确定</el-button>
        </span> 
    </el-dialog>
</template>

<script>
import { Component, Vue, Prop, Watch } from 'vue-property-decorator'
import tableMixin from '@/mixins/tableMixin'

@Component({
    name: 'edit'
})
export default class extends tableMixin {
    @Prop() editDialog
    @Prop() fileInfo
    @Prop() loadingBtn
    fileMaxSize = 10 * 1024 * 1024;
    // 下拉选择流程
    fileTypeSelect = ['文档','链接','视频','其他'];
    uploadName = ''
    created () {
        this.uploadName = this.fileInfo.fileList[0].fileName
    }
    
    validateFile = (rule,value,callback)=>{
        if(value.length===0){
            callback(new Error('请上传文件'))
        }else{
            callback()
        }
        callback();
    }
    formRules = {
        fileType:[
            { required: true, message: '请选择文件类型', trigger: 'change' },
        ],
        fileName: [
            { required: true, message: '请输入文件名称', trigger: 'change' },
            { max: 64, message: '文件名称不超过64个字符', trigger: 'change' },
        ],
        remark: [
            { required: false, message: '请输入文件名称', trigger: 'change' },
            { max: 256, message: '描述信息不超过128个字符', trigger: 'change' },
        ],
        fileList:[
            { required: true, message: '请上传文件', trigger: 'change' },
            { validator: this.validateFile, trigger: 'change' }
        ]
    }

    closeDialog() {
        this.$emit('update:editDialog',false);
        this.$refs['fileInfoForm'].resetFields();
    }
    submitBtn() {
        this.$refs['fileInfoForm'].validate((valid) => {
          if (valid) {
            this.$emit('editBookFile',this.fileInfo);
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
