<template>
    <div class="wrapper">
        <el-form class="firstRule" :model="realNameRule" ref="realNameRule" :rules="rules" :inline="true" :show-message="false" :hide-required-asterisk="true">
            <el-switch class="switchStyle" active-text="ON" inactive-text="OFF" active-value="1" inactive-value="0" v-model="realNameRule.ruleList[0].status" @change="update()"></el-switch>
            <el-form-item label="1.单次材料申领金额不得超过" prop="ruleValue1">
                <el-input-number size="small" v-model.number="realNameRule.ruleList[0].ruleValue" @change="update()" :controls="false"></el-input-number>
                <span class="tips">万元。</span>
            </el-form-item>
        </el-form>
        <el-form class="secondRule" :model="realNameRule" ref="realNameRule" :rules="rules" :inline="true" :show-message="false" :hide-required-asterisk="true">
            <el-switch class="switchStyle" active-text="ON" inactive-text="OFF" active-value="1" inactive-value="0" v-model="realNameRule.ruleList[1].status" @change="changeType"></el-switch>
            <div style="padding-left:40px">
                <div>2.项目经费预算各项比例：</div>
                <el-form-item label="材料费用不得高于" prop="ruleValue2">
                    <span v-html="'\u00a0\u00a0\u00a0\u00a0'"></span>
                    <el-input-number size="small" v-model.number="realNameRule.ruleList[1].ruleValue" @change="update()" :controls="false"></el-input-number>
                    <span class="tips">%</span>
                </el-form-item>
                <el-form-item label="机械使用费不得低于" prop="ruleValue3">
                    <el-input-number size="small" v-model.number="realNameRule.ruleList[2].ruleValue" @change="update()" :controls="false"></el-input-number>
                    <span class="tips">%</span>
                </el-form-item>
                <el-form-item label="人工费用不得低于" prop="ruleValue4">
                    <span v-html="'\u00a0\u00a0\u00a0\u00a0'"></span>
                    <el-input-number size="small" v-model.number="realNameRule.ruleList[3].ruleValue" @change="update()" :controls="false"></el-input-number>
                    <span class="tips">%</span>
                </el-form-item>
                <el-form-item label="其他费用不得高于" prop="ruleValue5">
                    <span v-html="'\u00a0\u00a0\u00a0\u00a0'"></span>
                    <el-input-number size="small" v-model.number="realNameRule.ruleList[4].ruleValue" @change="update()" :controls="false"></el-input-number>
                    <span class="tips">%</span>
                </el-form-item>
            </div>
            
        </el-form>
    </div>
</template>

<script>
import { Component, Vue } from 'vue-property-decorator'
import { checkForm } from '@/utils/index'

@Component({
    name: 'ruleConfigurationList',
    components: {
    }
})
export default class ruleConfiguration{
    // 校验规则
    rules = {
    }
    realNameRule = {
        ruleList:[
            {
                ruleType:'1',
                status:'1',
                ruleValue:'',
            },
            {
                ruleType:'2',
                status:'1',
                ruleValue:'',
            },
            {
                ruleType:'3',
                status:'1',
                ruleValue:'',
            },
            {
                ruleType:'4',
                status:'1',
                ruleValue:'',
            },
            {
                ruleType:'5',
                status:'1',
                ruleValue:'',
            }
        ]
    }
    validValues = {
        ruleList:[
            {
                ruleType:'1',
                status:'1',
                ruleValue:'',
            },
            {
                ruleType:'2',
                status:'1',
                ruleValue:'',
            },
            {
                ruleType:'3',
                status:'1',
                ruleValue:'',
            },
            {
                ruleType:'4',
                status:'1',
                ruleValue:'',
            },
            {
                ruleType:'5',
                status:'1',
                ruleValue:'',
            }
        ]
    }
    created(){
        this.getRuleConfiguration();
    }
    // 列表查询
    getRuleConfiguration(){
        this.$API.apiGetRuleConfiguration().then(res=>{
            res.data.forEach((item,index)=>{
                // for(var key in item){
                //     item[key] = Number(item[key]);
                // }
                this.$set(this.realNameRule.ruleList,index,item)
            })
            console.log(this.realNameRule)
        })
    }
    changeType(e) {
        this.$set(this.realNameRule.ruleList[2],'status',e)
        this.$set(this.realNameRule.ruleList[3],'status',e)
        this.$set(this.realNameRule.ruleList[4],'status',e)
        this.update()
    }

    // 修改值
    update(value){
        let _self = this;
        let formArr = ['realNameRule'];
        let resultArr = []
        formArr.forEach(item => { //根据表单的ref校验
            resultArr.push(checkForm(_self,item))
        })
        Promise.all(resultArr).then(() => {
            var params = Object.assign({},this.realNameRule);
            params.creatorOrgId = this.$store.getters.currentOrganization.organizationId,
            params.creatorOrgName = this.$store.getters.currentOrganization.organizationName,
            params.menuCode = this.MENU_CODE_LIST.ruleConfigurationList
            console.log(params)
            this.$API.apiUpdateRuleConfiguration(params).then(res=>{
                this.$message({
                    type: 'success',
                    message: '修改成功!'
                });
                this.validValues = Object.assign(this.validValues,this.realNameRule);
            }).catch(()=>{
                this.realNameRule = Object.assign(this.realNameRule,this.validValues);
            })
        }).catch(()=>{
            this.realNameRule = Object.assign(this.realNameRule,this.validValues);
        })
    }
}
</script>

<style lang="scss" scoped>
.wrapper{
    position: relative;
    height: calc(100vh - 130px);
    background-color: #fff;
    padding: 15px;
    &::v-deep{
        padding: 20px; 
        .el-form--inline {
            display: flex;
            &.firstRule {
                align-items: center;
            }
        }
        .el-form>div{
            margin-bottom: 20px;
        }
        .el-form-item{
            width: 100%;
            padding-left: 40px;
            .el-form-item__label{
                font-size: 16px;
            }
            .el-form-item__content{
                // width: 100px;
                >.el-input{
                    width: 80px;
                    display: inline-block;
                    margin-right: 10px;
                }
                .tips{
                    margin-left: 20px;
                }
            }
        }

        .switchStyle .el-switch__label {
            position: absolute;
            display: none;
            color: #fff;
        }
        .switchStyle .el-switch__label--left {
            z-index: 9;
            left: 16px;
        }
        .switchStyle .el-switch__label--right {
            z-index: 9;
            left: -6px;
        }
        .switchStyle .el-switch__label.is-active {
            display: block;
        }
        .switchStyle.el-switch .el-switch__core,
        .el-switch .el-switch__label {
            width: 50px !important;
        }
    }
}
</style>
