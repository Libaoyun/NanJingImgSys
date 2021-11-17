<template>
    <div class="approval">
         <card-global cardTitle="审批">
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
@Component({
    name: 'approvalGlobal'
})
export default class extends Vue {
    approvalFormRules = {
        remark:[
            { required:true,message:'请输入审批意见',trigger:'change'},
            { max:100,message:'审批意见不超过100个字',trigger:'change'}
        ]
    }
    approvalForm = {
        remark:''
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
}
</style>