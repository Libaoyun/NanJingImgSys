<template>
    <el-dialog 
        title="新增菜单"
        :visible="createDialog"
        :close-on-click-modal="false"
        custom-class="global-dialog-default"
        @close="closeDialog"
        width="800px">
        <div class="content">
            <el-form ref="routerMenuForm"  :model="routerMenuInfo" :rules="routerMenuFormRule" size="mini" label-position="left" label-width="120px">
                <el-form-item label="菜单编码:" prop="menuCode">
                    <el-input  v-model.trim="routerMenuInfo.menuCode"></el-input>
                </el-form-item>
                <el-form-item label="菜单名称:" prop="title">
                    <el-input  v-model.trim="routerMenuInfo.title"></el-input>
                </el-form-item>
                <el-form-item label="路由名称:" prop="name">
                    <el-input  v-model.trim="routerMenuInfo.name"></el-input>
                </el-form-item>
                <el-form-item label="菜单路径:" prop="path">
                    <el-input  v-model.trim="routerMenuInfo.path"></el-input>
                </el-form-item>
                <el-form-item label="路由组件:" prop="component">
                    <el-input v-model.trim="routerMenuInfo.component"></el-input>
                </el-form-item>
                <el-form-item label="图标:" prop="icon">
                    <el-input v-model.trim="routerMenuInfo.icon"></el-input>
                </el-form-item>
                <el-form-item label="是否显示:" prop="hidden">
                    <el-select v-model="routerMenuInfo.hidden">
                        <el-option label="是" :value="0"></el-option>
                        <el-option label="否" :value="1"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="是否缓存:" prop="keepAlive">
                    <el-select v-model="routerMenuInfo.keepAlive">
                        <el-option label="是" :value="1"></el-option>
                        <el-option label="否" :value="0"></el-option>
                    </el-select>
                </el-form-item>


                <el-form-item label="是否列表:" prop="pathRouting">
                    <el-select v-model="routerMenuInfo.pathRouting" @change="routerMenuInfo.pathRouting==1?$set(routerMenuInfo,'comButton',''): $set(routerMenuInfo,'comButton',[])">
                        <el-option label="是" :value="0"></el-option>
                        <el-option label="否" :value="1"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="组合按钮:" prop="comButton" v-if="routerMenuInfo.pathRouting === 0">
                    <el-checkbox-group v-model="routerMenuInfo.comButton">
                        <el-checkbox label="a10001">新建</el-checkbox>
                        <el-checkbox label="a10002">编辑</el-checkbox>
                        <el-checkbox label="a10003">详情</el-checkbox>
                        <el-checkbox label="a10004">审批</el-checkbox>
                    </el-checkbox-group>
                </el-form-item>
                <el-form-item label="按钮:" prop="comButton" v-else-if="routerMenuInfo.pathRouting === 1">
                    <el-select v-model="routerMenuInfo.comButton">
                        <el-option label="新建" value="a10001"></el-option>
                        <el-option label="编辑" value="a10002"></el-option>
                        <el-option label="详情" value="a10003"></el-option>
                        <el-option label="审批" value="a10004"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="子级菜单:" prop="noDropdown">
                    <el-select v-model="routerMenuInfo.noDropDown">
                        <el-option label="无" :value="1"></el-option>
                        <el-option label="有" :value="0"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="是否为审批页面:" prop="isApprove">
                    <el-select v-model="routerMenuInfo.isApprove">
                        <el-option label="是" value="1"></el-option>
                        <el-option label="否" value="0"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="是否关联工作流:" prop="relateFlow">
                    <el-select v-model="routerMenuInfo.relateFlow">
                        <el-option label="是" :value="1"></el-option>
                        <el-option label="否" :value="0"></el-option>
                    </el-select>
                </el-form-item>
            </el-form>
        </div>
        <span slot="footer" class="dialog-footer">
            <el-button size="mini" @click="closeDialog">取消</el-button>
            <el-button size="mini" type="primary" @click="submitBtn" :loading="loadingBtn == 1">确定</el-button>
        </span> 
    </el-dialog>
</template>

<script>
import { Component, Vue, Prop, Watch } from 'vue-property-decorator'
import tableMixin from '@/mixins/tableMixin'

@Component({
    name: 'create'
})
export default class extends tableMixin {
    @Prop() createDialog
    @Prop() loadingBtn
    
    routerMenuFormRule = {
        menuCode:[
            {
                pattern: /^\d{0,9}$/,
                message: "菜单编码只能为数值",
                trigger: "blur",
            }
        ]
    };
    routerMenuInfo={
        menuCode:'',
        title:'',
        name:'',
        path:'',
        component:'',
        icon:'',
        hidden:'',
        keepAlive:'',
        pathRouting:'',
        comButton:'',
        noDropdown:'',
        isApprove:'',
        relateFlow:''
    };
    closeDialog() {
        this.$emit('update:createDialog',false);
        this.$refs['routerMenuForm'].resetFields();
    }
    submitBtn() {
        this.$refs['routerMenuForm'].validate((valid) => {
          if (valid) {
            this.$emit('createRouterMenu',this.routerMenuInfo);
          } else {
            return false;
          }
        });
    }
}
</script>

<style lang="scss" scoped>
</style>
