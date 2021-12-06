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
                <el-form-item label="单据变好" prop="serialNumber">
                    <el-input v-model.trim="searchParams.serialNumber" placeholder="请输入流水号"></el-input>
                </el-form-item>
                <el-form-item label="成果名称" prop="creatorOrgName">
                    <el-input v-model.trim="searchParams.creatorOrgName" placeholder="请输入编制单位" clearable></el-input>
                </el-form-item>
                <el-form-item label="审批人" prop="creatorUserName">
                    <el-input v-model.trim="searchParams.creatorUserName" placeholder="请输入编制人" clearable></el-input>
                </el-form-item>
                <el-form-item label="申报人" prop="creatorUserName">
                    <el-input v-model.trim="searchParams.creatorUserName" placeholder="请输入编制人" clearable></el-input>
                </el-form-item>
                <el-form-item label="申请评审开始日期" prop="startDate">
                    <el-date-picker v-model="searchParams.startDate" value-format="yyyy-MM-dd" type="date" placeholder="请选择开始日期" clearable></el-date-picker>
                </el-form-item>
                <el-form-item label="申请评审结束日期" prop="endDate">
                    <el-date-picker v-model="searchParams.endDate" value-format="yyyy-MM-dd" type="date" placeholder="请选择结束日期" clearable></el-date-picker>
                </el-form-item>
                <el-form-item label="负责人" prop="creatorUserName">
                    <el-input v-model.trim="searchParams.creatorUserName" placeholder="请输入编制人" clearable></el-input>
                </el-form-item>
                <el-form-item label="岗位" prop="demandUnit">
                    <el-input v-model.trim="searchParams.demandUnit" placeholder="请输入需求单位" clearable></el-input>
                </el-form-item>
                <el-form-item label="起始年度" prop="startDate">
                    <el-date-picker v-model="searchParams.startDate" value-format="yyyy-MM-dd" type="date" placeholder="请选择开始日期" clearable></el-date-picker>
                </el-form-item>
                <el-form-item label="结束年度" prop="endDate">
                    <el-date-picker v-model="searchParams.endDate" value-format="yyyy-MM-dd" type="date" placeholder="请选择结束日期" clearable></el-date-picker>
                </el-form-item>
                <el-form-item label="任务来源" prop="demandUnit">
                    <el-input v-model.trim="searchParams.demandUnit" placeholder="请输入需求单位" clearable></el-input>
                </el-form-item>
                <el-form-item label="成果内容简介" prop="demandUnit">
                    <el-input v-model.trim="searchParams.demandUnit" placeholder="请输入需求单位" clearable></el-input>
                </el-form-item>
                <el-form-item label="编制人" prop="demandUnit">
                    <el-input v-model.trim="searchParams.demandUnit" placeholder="请输入需求单位" clearable></el-input>
                </el-form-item>
                <el-form-item label="创建日期开始日期" prop="startDate">
                    <el-date-picker v-model="searchParams.startDate" value-format="yyyy-MM-dd" type="date" placeholder="请选择开始日期" clearable></el-date-picker>
                </el-form-item>
                <el-form-item label="创建日期结束日期" prop="endDate">
                    <el-date-picker v-model="searchParams.endDate" value-format="yyyy-MM-dd" type="date" placeholder="请选择结束日期" clearable></el-date-picker>
                </el-form-item>
                <el-form-item label="更新日期开始日期" prop="startDate">
                    <el-date-picker v-model="searchParams.startDate" value-format="yyyy-MM-dd" type="date" placeholder="请选择开始日期" clearable></el-date-picker>
                </el-form-item>
                <el-form-item label="更新日期结束日期" prop="endDate">
                    <el-date-picker v-model="searchParams.endDate" value-format="yyyy-MM-dd" type="date" placeholder="请选择结束日期" clearable></el-date-picker>
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

    checkTime = (rule,value,callback)=>{
        let searchParams = this.$parent.searchParams;
        if(!searchParams.startDate || !searchParams.endDate){
            this.$refs.search.clearValidate('startDate')
            this.$refs.search.clearValidate('endDate')
            callback();
        }else{
            var startDate = new Date(searchParams.startDate);
            var endDate = new Date(searchParams.endDate);
            if(startDate>endDate){
                callback(new Error('开始时间不能大于结束时间'))
            }else{
                this.$refs.search.clearValidate('startDate')
                this.$refs.search.clearValidate('endDate')
                callback();
            }
        }
    }
    rules = {
        startDate:[
            { validator: this.checkTime, trigger: 'change'}
        ],
        endDate:[
            { validator: this.checkTime, trigger: 'change'}
        ]
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
