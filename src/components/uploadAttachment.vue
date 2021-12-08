<!-- 
附件上传
-->
<template>
    <div class="uploadAttachment">
        <card-global cardTitle="附件上传">
            <el-upload
                v-if="!onlyView"
                :limit="fileLimit"
                :on-exceed="handleExceed"
                class="upload"
                action="/"
                :file-list="fileList"
                ref="upload"
                :before-upload="beforeUpload"
                :http-request="handleUpolad"
                :show-file-list="false"
                drag
                multiple
            >
                <i class="el-icon-upload"></i>
                <div class="el-upload__text">点击或拖拽进行文件上传</div>
            </el-upload>
            <div class="dowloadAll">
                <el-button size="mini" type="primary" :disabled="!hasSelected" :loading="downLoading" @click="downloadAllSelected">批量下载</el-button>
                <span v-if="hasSelected">选中{{selected.length}}条</span>
            </div>
            <el-table
                ref="tableData"
                :data="fileList"
                :max-height="tableConfig.maxHeight"
                :border="tableConfig.border"
                class="global-table-default"
                style="width: 100%;padding-top:5px"
                @selection-change="tableSelectionChange"
            >
                <el-table-column type="selection" width="55" align="center"></el-table-column>
                <el-table-column label="序号" type="index" width="50" align="center">
                    <template slot-scope="scope">
                        <span>{{(listQuery.page - 1) * listQuery.limit + scope.$index + 1}}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="fileName" label="附件名称" width="400" align="center" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="fileSize" label="附件大小" width="200" align="center" :show-overflow-tooltip="true">
                    <template slot-scope="props">
                        {{ props.row.fileSize | formatSize }}
                    </template>
                </el-table-column>
                <el-table-column prop="uploadUserName" label="上传人" width="200" align="center" :show-overflow-tooltip="true">
                    <template slot-scope="props">
                        {{ props.row.uploadUserName ? props.row.uploadUserName : $store.getters.userInfo.userName }}
                </template>
                </el-table-column>
                <el-table-column prop="uploadProgress" label="上传进度" width="200" align="center" :show-overflow-tooltip="true">
                    <template slot-scope="props">
                        <el-progress
                            :percentage="props.row.uploadProgress"
                            v-if="props.row.uploadProgress !== undefined"
                            color="#5cb87a"
                        ></el-progress>
                        <span v-else>上传成功</span>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="120" align="center" :show-overflow-tooltip="true">
                    <template slot-scope="scope">
                        <template v-if="scope.row.uploadProgress === undefined">
                            <i
                                class="el-icon-download downloadBtn"
                                @click="downloadFile(scope)"
                            ></i>
                            <el-popconfirm
                                title="是否要删除此行？"
                                @confirm="deleteDetail(scope)"
                                placement="top"
                                cancelButtonType="plain"
                                v-if="!onlyView"
                            >
                                <i slot="reference" class="el-icon-delete deleteBtn"></i>
                            </el-popconfirm>
                        </template>
                    </template>
                </el-table-column>
            </el-table>
        </card-global>
    </div>
</template>

<script>
import { Component, Vue, Prop, Emit } from 'vue-property-decorator'
import tableMixin from '@/mixins/tableMixin'
const FileSaver = require('file-saver')

@Component({
    name: 'uploadAttachment',
    components: {
    }
})
export default class UploadAttachment extends tableMixin {

    @Prop({
        default: () => {
        return [];
        },
    }) fileList;
    @Prop({default:false}) onlyView
    @Prop() menuCode
    // 文件个数
    fileLimit = 10;
    // 文件大小限制
    fileMaxSize = 10 * 1024 * 1024;
    // 批量下载
    downLoading = false
    selected = []

    get hasSelected() {
      return this.selected.length > 0;
    }

      // 文件个数上传限制
    handleExceed() {
        this.$message({
            type: 'info',
            message: '最多只允许上传10个附件！',
        });
    }

      // 上传
    beforeUpload(file) {
        if (file.size <= this.fileMaxSize) {
            this.fileList.push(this.getFile(file));
        } else {
            this.$message({
                type: 'info',
                message: '单个文件最大不超过10MB',
            });
            return false;
        }
    }

    // 获取一个新的文件对象
    getFile(file) {
        return {
            fileName: file.name,
            fileSize: file.size,
            uploadProgress: 0,
            uid: file.uid,
        };
    }

      // 自定义上传
    handleUpolad(file) {
        var fd = new FormData();
        fd.append('file', file.file);
        this.$API.apiUploadFile(fd, (progressEvent) => {
            this.onUploadProgress(progressEvent, file.file.uid);
        }).then((res) => {
            this.fileList.some((item, index) => {
                if (item.uid == file.file.uid) {
                    this.fileList.splice(index, 1, res.data);
                return true;
                }
            });
        }).catch((err) => {
            this.fileList.some((item, index) => {
                if (item.uid == file.file.uid) {
                    this.fileList.splice(index, 1);
                return true;
                }
            });
        });
    }

    onUploadProgress(progressEvent, uid) {
        let percent = ((progressEvent.loaded / progressEvent.total) * 100) | 0; //调用onProgress方法来显示进度条，需要传递个对象 percent为进度值
            this.fileList.some((item, index) => {
            if (item.uid == uid) {
                    this.$set(item, 'uploadProgress', percent);
                return true;
            }
        });
    }
    // 下载
    downloadFile(scope) {
        var a = document.createElement('a');
        // var event = new MouseEvent('click')
        a.download = scope.row.fileName;
        a.href = scope.row.url;
        a.target = '_blank';
        a.click();
    }

    // 批量下载
    downloadAllSelected() {
        this.downLoading = true;
        const details = this.selected.map(it=>{
            return {file_name:it.fileName,file_path:it.filePath}
        })
        const data = {
            details,
            menuCode:this.menuCode
        }
        this.EXPORT_FILE([],'zip',{url:'/rdexpense/file/download',data},true).finally(()=>{
            this.downLoading = false;
            this.$refs.tableData.clearSelection();
        });
    }

    deleteDetail(scope) {
        if (scope.row.businessId) {
            this.$API.apiDeleteFile({businessId: scope.row.businessId, id: scope.row.id}).then((res) => {
                this.fileList.splice(scope.$index, 1);
            });
        } else {
            this.fileList.splice(scope.$index, 1);
        }
    }

      // 获取文件
    getFileList() {
        var fileList = [];
        console.log('this.fileList', this.fileList);
        this.fileList.forEach((item) => {
            fileList.push({
                id: item.id,
                businessId: item.businessId,
                fileExt: item.fileExt,
                fileName: item.fileName,
                filePath: item.filePath,
                fileSize: item.fileSize,
                type: 1,
                uploadUserId: this.$store.getters.userInfo.id,
                uploadUserName: this.$store.getters.userInfo.name,
            });
        });
        return fileList;
    }

    // 表格：复选框变化时触发,删除编辑
    tableSelectionChange(value){
        this.selected = value
    }

}
</script>

<style lang="scss" scoped>
@import '@/assets/scss/element-variables.scss';
/deep/{
    .upload {
        
        margin-bottom: 20px;
        .el-upload {
            
            width: 100%;
            .el-upload-dragger {
                background-color: #fafafa;
                width: 100%;
                height: 120px;
                .el-icon-upload {
                    margin-top: 15px;
                }
                .el-upload__text {
                    font-size: 15px;
                }
            }
        }
    }
}
.downloadBtn, .deleteBtn{
    font-size: 14px;
    cursor: pointer;
}
.downloadBtn{
    color: #1abc9c;
    margin-right: 8px;
    margin-left: 8px;
}
.deleteBtn{
    color: $--color-danger; 
}
.dowloadAll {
    margin-bottom: 16px;
    display: flex;
    align-items: center;
    span {
        font-size: 14px;
        margin-left: 10px;
    }
}
</style>
