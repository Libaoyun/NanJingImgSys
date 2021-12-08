<template>
    <div class="approval" v-if="processInstId">
        <card-global cardTitle="审批流程">
            <el-tabs v-model="activeName" @tab-click="changeActive">
                <el-tab-pane label="附件上传" name="first" v-if="hasAttach">
                    <!-- todo -->
                </el-tab-pane>
                <el-tab-pane label="审批记录" name="second">
                    <el-table
                        :data="approvalRecordList"
                        :border="tableConfig.border"
                        class="global-table-default"
                        style="width: 100%">
                        <el-table-column prop="approveUserName" label="审批人" width="100" align="center"></el-table-column>
                        <el-table-column prop="approveComment" label="审批意见" width="300" align="center"></el-table-column>
                        <el-table-column prop="approveNodeName" label="审批环节" width="200" align="center"></el-table-column>
                        <el-table-column prop="departmentName" label="审批人岗位" width="300" align="center"></el-table-column>
                        <el-table-column prop="approveEndTime" label="审批时间" width="200" align="center"></el-table-column>
                    </el-table>
                </el-tab-pane>
                <el-tab-pane label="流程图" name="third">
                    <div class="panel-view">
                        <panel-view :serialNumber="serialNumber" ref="flowPanel"></panel-view>
                    </div>
                </el-tab-pane>
            </el-tabs>
        </card-global>
         <card-global cardTitle="审批" v-if="type === 'approval'">
            <el-form ref="approvalForm" :rules="approvalFormRules" :model="approvalForm" label-position="right" label-width="80px">
                <el-form-item label="意见栏:" prop="remark">
                    <el-input type="textarea" v-model="approvalForm.remark" placeholder="请在此处进行意见批复，谢谢！"></el-input>
                </el-form-item>
            </el-form>
        </card-global>
    </div>
</template>

<script>
import { Component, Vue, Prop, Emit } from 'vue-property-decorator'
import { checkForm } from '@/utils/index'
import CardGlobal from './cardGlobal.vue'
import tableMixin from '@/mixins/tableMixin'
import PanelView from './ef/panel-view'
@Component({
    name: 'approvalGlobal',
    components: {
        CardGlobal,
        PanelView,
    }
})  
export default class extends tableMixin {
    @Prop({default:'approval'}) type
    @Prop({default:false}) hasAttach

    serialNumber = null
    processInstId = null

    activeName = this.hasAttach ? 'first' : 'second'
    approvalRecordList = []
    approvalFormRules = {
        remark:[
            { required:true,message:'请输入审批意见',trigger:'change'},
            { max:1000,message:'审批意见不超过1000个字',trigger:'change'}
        ]
    }
    approvalForm = {
        remark:''
    }

    mounted() {
    }
    activated() {
        this.activeName = this.hasAttach ? 'first' : 'second'
        this.approvalRecordList = []
        this.$refs['approvalForm']?.resetFields()
    }

    getApprovalRecordList(processInstId,serialNumber) {
        this.serialNumber = serialNumber
        this.processInstId = processInstId
        this.approvalRecordList = []
        if(!processInstId) return;
        this.$API.apiGetApprovalRecordList({processInstId}).then(res=>{
            this.approvalRecordList = res.data
        })
    }
    changeActive(e) {
        if(this.activeName === 'third') {
            if(!this.serialNumber) return;
            this.$refs.flowPanel.initData()
        }
    }

    isCheckComplete(){
        // 异步判断校验是否通过
        return new Promise((resolve,reject)=>{
            checkForm(this,'approvalForm').then(()=>{
                resolve(this.approvalForm.remark);
            }).catch(()=>{
                this.$message({
                    type: 'warning',
                    message: '请将信息填写完整！'
                });
                reject();
            })
        })
    }

    restData() {
        console.log('222')
        this.$refs.approvalForm.resetFields()
    }
    
}
</script>

<style lang="scss" scoped>
.approval {
    &::v-deep{
        .card-global .card-global-form .el-form-item{
            width: 100%;
        }
    }
    .panel-view {
        height: 400px;
    }
}
</style>