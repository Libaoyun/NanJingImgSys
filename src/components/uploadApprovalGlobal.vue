<!--
    附件：传参  type 新建(create)编辑(edit)详情(detail)审批(approval)
              fileList  附件记录 默认是[]
         返回值  调取组件getFileList方法获取处理好的附件数据[]
    
    审批记录

-->

<template>
    <div style="position:relative">
        <el-tabs v-model="activeName" type="card" class="upload-el-tabs global-card-default">
            <el-tab-pane name="upload">
                <span slot="label">
                    附件列表 <el-tooltip class="item" effect="dark" content="附件要求：请上传与本页相关的附件材料" placement="top-start">
                        <i class="iconfont iconhelp"></i> 
                    </el-tooltip>
                </span>
                
                <el-table
                    :data="fileList"
                    :max-height="tableConfig.maxHeight"
                    :border="tableConfig.border"
                    class="global-table-default"
                    style="width: 100%;padding-top:5px">
                    <el-table-column label="序号" type="index" width="50" align="center">
                        <template slot-scope="scope">
                            <span>{{(listQuery.page-1)*listQuery.limit + scope.$index + 1}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="fileName" label="附件名称" width="400" align="center" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="fileSize" label="大小" width="200" align="center" :show-overflow-tooltip="true">
                        <template slot-scope="props">{{props.row.fileSize | formatSize}}</template>
                    </el-table-column>
                    <el-table-column prop="uploadUserName" label="上传人" align="center" :show-overflow-tooltip="true">
                        <template slot-scope="props">{{ props.row.uploadUserName ? props.row.uploadUserName : $store.getters.userInfo.userName}}</template>
                    </el-table-column>
                    <el-table-column prop="uploadProgress" label="上传进度" align="center" :show-overflow-tooltip="true" width="200px" v-if="isAdd">
                        <template slot-scope="props">
                            <el-progress :percentage="props.row.uploadProgress" v-if="props.row.uploadProgress !== undefined" color="#5cb87a"></el-progress>
                            <span v-else>上传成功</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" width="120" :show-overflow-tooltip="true" align="center">
                        <template slot-scope="scope">
                            <template v-if="scope.row.uploadProgress === undefined">
                                <!-- <i class="iconfont iconbrowse previewBtn" @click="previewFile"></i> -->
                                <i class="el-icon-download downloadBtn" @click="downloadFile(scope)"></i>
                                <el-popconfirm title="是否要删除此行？" @confirm="deleteDetail(scope)" placement="top" cancelButtonType="plain"  v-if="isAdd">
                                    <i slot="reference" class="el-icon-delete deleteBtn"></i>
                                </el-popconfirm>
                            </template>
                        </template>
                    </el-table-column>
                </el-table>
            </el-tab-pane>
            <el-tab-pane name="approval" v-if="isShowApproval">
                <span slot="label">
                    审批记录
                </span>
                <el-table
                    :data="approvalList"
                    :max-height="tableConfig.maxHeight"
                    :border="tableConfig.border"
                    class="global-table-default"
                    style="width: 100%;padding-top:5px">
                    <el-table-column label="序号" type="index" width="50" align="center">
                        <template slot-scope="scope">
                            <span>{{(listQuery.page-1)*listQuery.limit + scope.$index + 1}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="approvalTime" label="记录时间" width="400" align="center" :show-overflow-tooltip="true">
                    </el-table-column>
                    <el-table-column prop="activityInstName" label="流程节点" width="100" align="center" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="userName" label="经办人" align="center" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="processName" label="状态" align="center" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="noted" label="审批意见" align="center" :show-overflow-tooltip="true"></el-table-column>
                </el-table>
            </el-tab-pane>
        </el-tabs>
        <div class="upload-btn-group">
            <template v-if="activeName=='upload'">
                <template v-if="isAdd">
                    <el-upload :limit="fileLimit" :on-exceed="handleExceed" class="upload" action="/" :file-list="fileList"  ref="upload" :before-upload="beforeUpload" :http-request="handleUpolad"  :show-file-list="false">
                        <span><i class="el-icon-plus"></i></span>
                    </el-upload>
                </template>
            </template>
        </div>
    </div>
</template>

<script>
import { Component, Vue, Prop, Emit, Watch } from 'vue-property-decorator'
import tableMixin from '@/mixins/tableMixin'
@Component({
    name: 'uploadApprovalGlobal'
})
export default class extends tableMixin {
    @Prop({default:'create'}) type
    @Prop({default:()=>{return []}}) fileList
    @Prop({default:false}) onlyUpload

    activated() {
        this.activeName = 'upload';
    }
    // 备注字数
    remarkLength = 20
    // 文件个数
    fileLimit = 10
    // 文件大小限制
    fileMaxSize = 10 * 1024 * 1024;
    // tab页的key
    activeName = 'upload'
    // 获取审批记录
    getApproveNote(menuCode,processInstId){
        this.approvalList = [];
        if(!menuCode || !processInstId){
            return;
        }
        this.$API.apiGetApproveNote({menuCode,processInstId}).then(res=>{
            this.approvalList = res.data;
        })
    }
    deleteDetail(scope){
        console.log(scope)
        if(scope.row.businessId){
            this.$API.apiDeleteFile({businessId:scope.row.businessId,id:scope.row.id}).then(res=>{
                this.fileList.splice(scope.$index,1);
            })
        }else{
            this.fileList.splice(scope.$index,1);
        }
    }


    approvalList = []
    // 是否能添加附件
    get isAdd(){
        return ['create','edit'].includes(this.type);
    }
    // 是否展示审批列表
    get isShowApproval(){
        return ['approval','detail'].includes(this.type) && !this.onlyUpload;
    }
    // 获取一个新的文件对象
    getFile(file){
        return {
            fileName:file.name,
            fileSize:file.size,
            uploadProgress:0,
            uid:file.uid
        }
    }
    // 上传
    beforeUpload(file){
        if(file.size<=this.fileMaxSize){
            this.fileList.push(this.getFile(file));
        }else{
            this.$message({
                type:'info',
                message:'单个文件最大不超过10MB'
            })
            return false;
        }
        
    }
    // 自定义上传
    handleUpolad(file){
        var fd = new FormData();
        fd.append('file',file.file);
        this.$API.apiUploadFile(fd,(progressEvent)=>{this.onUploadProgress(progressEvent,file.file.uid)}).then(res=>{
            this.fileList.some((item,index)=>{
                if(item.uid == file.file.uid){
                    this.fileList.splice(index,1,res.data);
                    return true;
                }
            })
        }).catch(err=>{
            this.fileList.some((item,index)=>{
                if(item.uid == file.file.uid){
                    this.fileList.splice(index,1);
                    return true;
                }
            })
        })
    }

    onUploadProgress(progressEvent,uid)  {
      let percent=(progressEvent.loaded / progressEvent.total * 100) | 0 //调用onProgress方法来显示进度条，需要传递个对象 percent为进度值
       this.fileList.some((item,index)=>{
           if(item.uid == uid){
               this.$set(item,'uploadProgress',percent);
               return true;
           }
       })    
    }
    // 下载
    downloadFile(scope){
        var a = document.createElement('a')
        // var event = new MouseEvent('click')
        a.download = scope.row.fileName
        a.href = scope.row.url;
        a.target = '_blank'
        a.click()
    }
    // 文件个数上传限制
    handleExceed(){
        this.$message({
            type:'info',
            message:'最多只允许上传10个附件！'
        })
    }

    // 获取文件
    getFileList(){
        var fileList = [];
        console.log('this.fileList',this.fileList)
        this.fileList.forEach(item=>{
            fileList.push({
                id: item.id,
                businessId:item.businessId,
                fileExt: item.fileExt,
                fileName: item.fileName,
                filePath: item.filePath,
                fileSize: item.fileSize,
                type: 1,
                uploadUserId:this.$store.getters.userInfo.id,
                uploadUserName:this.$store.getters.userInfo.name
            })
        })
        return fileList;
    }
}
</script>

<style lang="scss" scoped>
.upload-el-tabs{
    margin-bottom: 15px;
}
.upload-btn-group{
    position: absolute;
    top: 15px;
    right: 20px;
    i{
        font-size: 18px;
    }
}
</style>
