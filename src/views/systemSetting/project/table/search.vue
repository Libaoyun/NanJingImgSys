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
                <el-form-item label="项目名称" prop="orgName">
                    <el-input v-model.trim="searchParams.orgName" placeholder="请输入项目名称"></el-input>
                </el-form-item>
                <el-form-item label="项目编号" prop="orgNumber">
                    <el-input v-model.trim="searchParams.orgNumber" placeholder="请输入项目编号"></el-input>
                </el-form-item>
                <el-form-item label="项目描述" prop="remark">
                    <el-input v-model.trim="searchParams.remark" placeholder="请输入项目描述"></el-input>
                </el-form-item>
                <el-form-item label="是否启用" prop="status">
                    <el-select v-model="searchParams.status">
                        <el-option label="是" value="1"></el-option>
                        <el-option label="否" value="0"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="创建人" prop="createUser">
                    <el-input v-model.trim="searchParams.createUser" placeholder="请输入创建人"></el-input>
                </el-form-item>
                <el-form-item label="创建时间" prop="createTime">
                    <el-date-picker
                        v-model="searchParams.createTime"
                        value-format="yyyy-MM-dd"
                        type="date"
                        placeholder="请选择创建时间"
                    ></el-date-picker>
                </el-form-item>
                <el-form-item label="更新人" prop="updateUser">
                    <el-input v-model.trim="searchParams.updateUser" placeholder="请输入更新人"></el-input>
                </el-form-item>
                <el-form-item label="更新时间" prop="updateTime">
                    <el-date-picker
                        v-model="searchParams.updateTime"
                        value-format="yyyy-MM-dd"
                        type="date"
                        placeholder="请选择更新时间"
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
