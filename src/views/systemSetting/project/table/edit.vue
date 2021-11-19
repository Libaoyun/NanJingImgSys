<template>
    <div style="padding-bottom: 46px">
        <card-global>
            <div>
                <el-form ref="doForm" :inline="true" :rules="formRules" :model="baseInfo" size="mini" label-position="right" label-width="80px">
                    <el-form-item label="项目编号" prop="orgNumber">
                        <el-input v-model="baseInfo.orgNumber" disabled></el-input>
                    </el-form-item>
                    <el-form-item label="创建人" prop="createUser">
                        <el-input v-model="baseInfo.createUser" disabled></el-input>
                    </el-form-item>
                    <el-form-item label="创建时间" prop="createTime">
                        <el-input :value="baseInfo.createTime | formatTime('yyyy-MM-dd')" disabled></el-input>
                    </el-form-item>
                    <el-form-item label="项目名称" prop="orgName">
                        <el-input v-model="baseInfo.orgName" placeholder="请输入项目名称"></el-input>
                    </el-form-item>
                    <el-form-item label="是否启用:" prop="status">
                        <el-select v-model="baseInfo.status" placeholder="请选择">
                            <el-option label="启用" value="1"></el-option>
                            <el-option label="禁用" value="0"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="项目描述" class="large" prop="remark">
                        <el-input type="textarea" v-model="baseInfo.remark" placeholder="请输入备注"></el-input>
                    </el-form-item>
                </el-form>
            </div>
        </card-global>
        <div class="global-fixBottom-actionBtn">
            <el-button size="mini" @click="backBtn">返回</el-button>
            <loading-btn size="mini" type="primary" @click="saveBtn" :loading="loadingBtn">保存</loading-btn>
        </div>
    </div>
</template>

<script>
import { Component, Vue, Watch } from 'vue-property-decorator'
import tableMixin from '@/mixins/tableMixin'
import { checkForm } from '@/utils/index'

@Component({
    name: 'projectEdit',
    components: {
    }
})
   
export default class extends tableMixin {
    loadingBtn = 0;
    baseInfo = this.getBaseInfo()
    formRules = {
        orgName: [
            { required: true, message: '请输入项目名称', trigger: 'change' },
            { max:30, message: '项目名称不超过30个字符', trigger: 'change' } 
        ],
    }
    // 设置空数据
    getBaseInfo(){
       return {
            orgNumber:'',
            createUser:'',
            createTime: '',

            orgName:'',
            status: '1',
            remark:'',
        } 
    }
    activated() {
        if(Object.keys(this.$route.params).length > 0){
           this.getDetail(this.$route.params.projectInfo)
        }
    }

    getDetail(data) {
        this.$refs['doForm'].resetFields();
        const params = {
            id:data.id
        }
        this.$API.apiGetProjectDetail(params).then(res=>{
            this.baseInfo = Object.assign(this.baseInfo,res.data)
        })
        
    }
    // 保存按钮
    saveBtn(){
        const _self = this
        let formArr = ['doForm']
        let resultArr = []
        formArr.forEach(item => { //根据表单的ref校验
            resultArr.push(checkForm(_self,item))
        })
        Promise.all(resultArr).then(() => {
            this.$confirm('确定保存当前表单?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
                }).then(() => {
                    // 调接口
                    let params = Object.assign(
                        {
                            creatorOrgId:this.$store.getters.currentOrganization.organizationId,
                            creatorOrgName:this.$store.getters.currentOrganization.organizationName,
                            menuCode:this.MENU_CODE_LIST.projectList
                        },
                        this.baseInfo,
                    )
                    this.loadingBtn = 1;
                    this.$API.apiUpdateProject(params).then(res=>{
                    this.loadingBtn = 0;
                    this.$message({
                        type: 'success',
                        message: '编辑成功!'
                    });
                    this.$store.commit('DELETE_TAB', this.$route.path);
                    this.$router.push({ name: 'projectList'})
                }).catch(()=>{
                this.loadingBtn = 0;
                })
            });
        })
    }
    // 返回按钮
    backBtn(){
        this.$confirm('未保存的数据将丢失，是否返回?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
            }).then(()=>{
                this.$store.commit('DELETE_TAB', this.$route.path);
                this.$router.push({ name: 'projectList'})
            })
    }
}
</script>

<style lang="scss" scoped>
/deep/.card-global {
    height: calc(100vh - 190px);
}
.el-form {
    /deep/ .el-form-item.large {
        width: 100%;
    }
}
</style>
