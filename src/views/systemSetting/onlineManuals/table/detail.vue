<template>
    <el-dialog 
        title="查看文件"
        :visible="detailDialog"
        :close-on-click-modal="false"
        @close="closeDialog"
        width="500px">
        <div class="content">
            <el-form ref="fileInfoForm" :model="fileInfo" size="mini" label-position="right" label-width="100px">
                <el-form-item label="文件类型:">
                  <span>{{fileInfo.fileType}}</span>
                </el-form-item>
                <el-form-item label="文件名称:">
                  <span>{{fileInfo.fileName}}</span>
                </el-form-item>
                <el-form-item label="描述信息:">
                  <span>{{fileInfo.remark}}</span>
                </el-form-item>
                <el-form-item label="文件:">
                  <a target="_blank" :href="fileInfo.fileUrl">{{fileInfo.fileList[0].fileName}}</a>
                </el-form-item>
            </el-form>
        </div>
        <span slot="footer" class="dialog-footer">
            <el-button size="mini" @click="closeDialog">返回</el-button>
        </span> 
    </el-dialog>
</template>

<script>
import { Component, Vue, Prop, Watch } from 'vue-property-decorator'
import tableMixin from '@/mixins/tableMixin'

@Component({
    name: 'detail'
})
export default class extends tableMixin {
    @Prop() detailDialog
    @Prop() fileInfo
    @Prop() loadingBtn
    fileMaxSize = 10 * 1024 * 1024;
    closeDialog() {
        this.$emit('update:detailDialog',false);
    }
}
</script>

<style lang="scss" scoped>
</style>
