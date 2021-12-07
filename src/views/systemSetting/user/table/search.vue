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
                <el-form-item label="用户编号" prop="userCode">
                    <el-input v-model.trim="searchParams.userCode" placeholder="请输入用户编号"></el-input>
                </el-form-item>
                <el-form-item label="用户名称" prop="userName">
                    <el-input v-model.trim="searchParams.userName" placeholder="请输入用户名称"></el-input>
                </el-form-item>
                <el-form-item label="手机号码" prop="mobilePhone">
                    <el-input v-model.trim="searchParams.mobilePhone" placeholder="请输入手机号码"></el-input>
                </el-form-item>
                <el-form-item label="所属部门" prop="departmentName">
                    <el-input v-model.trim="searchParams.departmentName" placeholder="请输入所属部门"></el-input>
                </el-form-item>
                <el-form-item label="所属职务" prop="postName">
                    <el-input v-model.trim="searchParams.postName" placeholder="请输入所属职务"></el-input>
                </el-form-item>
                <el-form-item label="创建人" prop="selectCreateUser">
                    <el-input v-model.trim="searchParams.selectCreateUser" placeholder="请输入创建人"></el-input>
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
