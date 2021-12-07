<template>
    <el-drawer
        title=""
        :visible="searchDialog"
        direction="rtl"
        custom-class="search-drawer"
        size="250px"
        @close="$emit('update:searchDialog',false)"
        ref="drawer">
        <span slot="title"><i class="iconfont icon-sousuo"></i> 搜索</span>
        <div>
            <el-form :model="searchParams" :inline="true" size="mini" label-width="70px" label-position="right">
                <el-form-item label="申请单号" prop="serialNumber">
                    <el-input v-model.trim="searchParams.serialNumber" placeholder="请输入申请单号"></el-input>
                </el-form-item>
                <el-form-item label="项目名称" prop="projectName">
                    <el-input v-model.trim="searchParams.projectName" placeholder="请输入项目名称"></el-input>
                </el-form-item>
                <el-form-item label="审批人" prop="approveUserName">
                    <el-input v-model.trim="searchParams.approveUserName" placeholder="请输入当前审批人"></el-input>
                </el-form-item>
                <el-form-item label="申请人" prop="applyUserName">
                    <el-input v-model.trim="searchParams.applyUserName" placeholder="请输入申请人"></el-input>
                </el-form-item>
                <el-form-item label="岗位" prop="postName">
                    <el-input v-model.trim="searchParams.postName" placeholder="请输入岗位"></el-input>
                </el-form-item>
                <el-form-item label="所属单位" prop="unitName">
                    <el-input v-model.trim="searchParams.unitName" placeholder="请输入所属单位名称"></el-input>
                </el-form-item>
                <el-form-item label="研究内容" prop="researchContents">
                    <el-input v-model.trim="searchParams.researchContents" placeholder="请输入研究内容提要"></el-input>
                </el-form-item>
                <el-form-item label="编制人" prop="creatorUserName">
                    <el-input v-model.trim="searchParams.creatorUserName" placeholder="请输入编制人"></el-input>
                </el-form-item>
                <el-form-item label="创建日期" prop="createTime">
                    <el-date-picker
                        v-model="searchParams.createTime"
                        value-format="yyyy-MM-dd"
                        type="date"
                        placeholder="请选择创建日期"
                    ></el-date-picker>
                </el-form-item>
                <el-form-item label="更新日期" prop="updateTime">
                    <el-date-picker
                        v-model="searchParams.updateTime"
                        value-format="yyyy-MM-dd"
                        type="date"
                        placeholder="请选择更新日期"
                    ></el-date-picker>
                </el-form-item>
            </el-form>
            <div style="text-align:right;margin-top:10px">
                <el-button size="small" @click="resetForm">重置</el-button>
                <el-button size="small" type="primary" @click="searchSubmit">搜索</el-button>
            </div>
        </div>
    </el-drawer>
</template>

<script>
import { Component, Vue, Prop } from 'vue-property-decorator'

@Component({
    name: 'search'
})
export default class extends Vue {
    @Prop() searchDialog
    @Prop() searchParams

    resetForm(){
        this.$emit('update:searchParams',{});
    }
    searchSubmit() {
        this.$emit('searchSubmit');
        this.$emit('update:searchDialog',false)
    }
    
}
</script>

<style lang="scss" scoped>
</style>
