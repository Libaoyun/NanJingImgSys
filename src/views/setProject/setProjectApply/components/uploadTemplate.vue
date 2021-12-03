<template>
    <el-upload
        class="upload-template"
        action="/"
        ref="upload" 
        :http-request="handleUpolad" 
        accept=".xls,.xlsx" 
        :show-file-list="false"
    >
        <el-button size="mini" type="primary" icon="el-icon-plus" :loading="loadingBtn === 1">{{btnName}}</el-button>
    </el-upload>
</template>

<script>
import { Component, Vue, Prop } from 'vue-property-decorator'
@Component({
    name: 'uploadTemplate'
})
export default class UploadTemplate extends Vue {
    @Prop() importUrl
    @Prop({default:'导入模板'}) btnName
    loadingBtn = 0
    fileList = []

    handleUpolad(file) {
        var fd = new FormData();
        fd.append('file',file.file);
        fd.append('creatorOrgId',this.$store.getters.currentOrganization.organizationId);
        fd.append('creatorOrgName',this.$store.getters.currentOrganization.organizationName);
        fd.append('menuCode',this.MENU_CODE_LIST.setProjectApplyList);
        fd.append('createUser',this.$store.getters.userInfo?.userName);
        fd.append('createUserId',this.$store.getters.userInfo?.userCode);
        this.loadingBtn = 1
        this.$API[this.importUrl](fd).then(res=>{
            this.loadingBtn = 0
            this.$emit('importBtn',res.data)
            return true;
        }).catch(err=>{
             this.loadingBtn = 0
        })
    }
}
</script>

<style lang="scss" scoped>
.upload-template{
    display: inline-block;
    margin-left: 6px;
    margin-right: 10px;
}
</style>
