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
            <el-form ref="search" :model="searchParams" :rules="rules" :inline="true" size="mini" label-width="90px" label-position="right">
                <el-form-item label="单据编号" prop="serialNumber">
                    <el-input v-model.trim="searchParams.serialNumber" placeholder="请输入单据编号"></el-input>
                </el-form-item>
                <el-form-item label="成果名称" prop="jobTitle">
                    <el-input v-model.trim="searchParams.jobTitle" placeholder="请输入成果名称" clearable></el-input>
                </el-form-item>
                <el-form-item label="审批人" prop="approveUserName">
                    <el-input v-model.trim="searchParams.approveUserName" placeholder="请输入审批人" clearable></el-input>
                </el-form-item>
                <el-form-item label="申报人" prop="createUser">
                    <el-input v-model.trim="searchParams.createUser" placeholder="请输入申报人" clearable></el-input>
                </el-form-item>
                <el-form-item label="申请评审日期" prop="createdDate">
                    <el-date-picker v-model="searchParams.createdDate" value-format="yyyy-MM-dd" type="date" placeholder="申请评审日期" clearable></el-date-picker>
                </el-form-item>
                <el-form-item label="项目负责人" prop="applyUserName">
                    <el-input v-model.trim="searchParams.applyUserName" placeholder="请输入项目负责人" clearable></el-input>
                </el-form-item>
                <el-form-item label="岗位" prop="postName">
                    <el-input v-model.trim="searchParams.postName" placeholder="请输入岗位" clearable></el-input>
                </el-form-item>
                <el-form-item label="起始年度" prop="startYear">
                    <el-date-picker v-model="searchParams.startYear" value-format="yyyy-MM-dd" type="date" placeholder="请选择起始年度" clearable></el-date-picker>
                </el-form-item>
                <el-form-item label="结束年度" prop="endYear">
                    <el-date-picker v-model="searchParams.endYear" value-format="yyyy-MM-dd" type="date" placeholder="请选择结束年度" clearable></el-date-picker>
                </el-form-item>
                <el-form-item label="成果内容简介" prop="projectAbstract">
                    <el-input v-model.trim="searchParams.projectAbstract" placeholder="请输入成果内容简介" clearable></el-input>
                </el-form-item>
                <el-form-item label="编制人" prop="createUser">
                    <el-input v-model.trim="searchParams.createUser" placeholder="请输入编制人" clearable></el-input>
                </el-form-item>
                <el-form-item label="创建日期" prop="createTime">
                    <el-date-picker v-model="searchParams.createTime" value-format="yyyy-MM-dd" type="date" placeholder="请选择创建日期" clearable></el-date-picker>
                </el-form-item>
                <el-form-item label="更新日期" prop="updateTime">
                    <el-date-picker v-model="searchParams.updateTime" value-format="yyyy-MM-dd" type="date" placeholder="请选择更新日期" clearable></el-date-picker>
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

    // checkTime = (rule,value,callback)=>{
    //     let searchParams = this.$parent.searchParams;
    //     if(!searchParams.startDate || !searchParams.endDate){
    //         this.$refs.search.clearValidate('startDate')
    //         this.$refs.search.clearValidate('endDate')
    //         callback();
    //     }else{
    //         var startDate = new Date(searchParams.startDate);
    //         var endDate = new Date(searchParams.endDate);
    //         if(startDate>endDate){
    //             callback(new Error('开始时间不能大于结束时间'))
    //         }else{
    //             this.$refs.search.clearValidate('startDate')
    //             this.$refs.search.clearValidate('endDate')
    //             callback();
    //         }
    //     }
    // }
    rules = {
        // startDate:[
        //     { validator: this.checkTime, trigger: 'change'}
        // ],
        // endDate:[
        //     { validator: this.checkTime, trigger: 'change'}
        // ]
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
