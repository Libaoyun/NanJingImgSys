<template>
    <el-drawer
        title=""
        :visible="searchDialog"
        direction="rtl"
        custom-class="search-drawer"
        size="250px"
        @close="$emit('update:searchDialog',false)"
        ref="drawer">
        <span slot="title"><i class="iconfont iconsearch1"></i> 搜索</span>
        <div>
            <el-form ref="search" :model="searchParams" :rules="rules" :inline="true" size="mini" label-width="70px" label-position="right">
                <el-form-item label="单据编号" prop="serialNumber">
                    <el-input v-model.trim="searchParams.serialNumber" placeholder="请输入单据编号"></el-input>
                </el-form-item>
                <el-form-item label="项目名称" prop="projectName">
                    <el-input v-model.trim="searchParams.projectName" placeholder="请输入项目名称" clearable></el-input>
                </el-form-item>
                <el-form-item label="申请人" prop="applyUser">
                    <el-input v-model.trim="searchParams.approveUserName" placeholder="请输入申请人" clearable></el-input>
                </el-form-item>
                <el-form-item label="支出依据" prop="createUser">
                    <el-input v-model.trim="searchParams.createUser" placeholder="请输入申报人" clearable></el-input>
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
import { checkForm } from '@/utils/index'
@Component({
    name: 'search'
})
export default class extends Vue {
    @Prop() searchDialog
    @Prop() searchParams

    rules = {
    }
    resetForm(){
        this.$emit('update:searchParams',{});
    }
    searchSubmit() {
        checkForm(this,'search').then(()=>{
            this.$emit('searchSubmit');
            this.$emit('update:searchDialog',false)
        }).catch(()=>{
            
        })
        
    }
    
}
</script>

<style lang="scss" scoped>
</style>
