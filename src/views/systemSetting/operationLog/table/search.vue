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
            <el-form ref="search" :rules="rules" :model="searchParams" :inline="true" size="mini" label-width="70px" label-position="right">
                <el-form-item label="用户名" prop="userName">
                    <el-input v-model.trim="searchParams.userName" placeholder="请输入用户名"></el-input>
                </el-form-item>
                <el-form-item label="所属组织" prop="userOrgName">
                    <el-input v-model.trim="searchParams.userOrgName" placeholder="请输入所属组织"></el-input>
                </el-form-item>
                <el-form-item label="所属部门" prop="userDepartName">
                    <el-input v-model.trim="searchParams.userDepartName" placeholder="请输入所属部门"></el-input>
                </el-form-item>
                <el-form-item label="所属职务" prop="userPositionName">
                    <el-input v-model.trim="searchParams.userPositionName" placeholder="请输入所属职务"></el-input>
                </el-form-item>
                <el-form-item label="IP" prop="loginIp">
                    <el-input v-model.trim="searchParams.loginIp" placeholder="请输入IP"></el-input>
                </el-form-item>
                <el-form-item label="开始日期:" prop="beginTime">
                    <el-date-picker v-model="searchParams.beginTime" value-format="yyyy-MM-dd" type="date" placeholder="请输入开始日期"></el-date-picker>
                </el-form-item>
                <el-form-item label="结束日期:" prop="endTime">
                    <el-date-picker v-model="searchParams.endTime" value-format="yyyy-MM-dd" type="date" placeholder="请输入结束日期"></el-date-picker>
                </el-form-item>
                <el-form-item label="操作类型" prop="operateType">
                    <el-select v-model="searchParams.operateType">
                        <el-option v-for="item in operateTypeList" :label="item.label" :value="item.value" :key="item.value"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="操作内容" prop="operateContent">
                    <el-input v-model.trim="searchParams.operateContent" placeholder="请输入操作内容"></el-input>
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
import dictionaryMixin from '@/mixins/dictionaryMixin'

@Component({
    name: 'search'
})
export default class extends dictionaryMixin {
    @Prop() searchDialog
    @Prop() searchParams

    checkTime = (rule,value,callback)=>{
        let searchParams = this.$parent.searchParams;
        if(!searchParams.beginTime || !searchParams.endTime){
            this.$refs.search.clearValidate('beginTime')
            this.$refs.search.clearValidate('endTime')
            callback();
        }else{
            var beginTime = new Date(searchParams.beginTime);
            var endTime = new Date(searchParams.endTime);
            if(beginTime>endTime){
                callback(new Error('开始时间不能大于结束时间'))
            }else{
                this.$refs.search.clearValidate('beginTime')
                this.$refs.search.clearValidate('endTime')
                callback();
            }
        }
    }
    rules = {
        beginTime:[
            { validator: this.checkTime, trigger: 'change'}
        ],
        endTime:[
            { validator: this.checkTime, trigger: 'change'}
        ]
    }
    created() {
        this.getOperateTypeList()
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
