<template>
    <div style="padding-bottom: 46px">
        <card-global>
            <div>
                <el-form ref="doForm" :inline="true" :model="baseInfo" size="mini" label-position="right" label-width="80px">
                    <el-form-item label="编号:" prop="userCode">
                        <span class="static-value">{{baseInfo.userCode}}</span>
                    </el-form-item>
                    <el-form-item label="姓名:" prop="userName">
                        <span class="static-value">{{baseInfo.userName}}</span>
                    </el-form-item>
                    <el-form-item label="英文名:" prop="englishUserName">
                        <span class="static-value">{{baseInfo.englishUserName}}</span>
                    </el-form-item>
                    <el-form-item label="性别:" prop="gender">
                        <span class="static-value">{{baseInfo.gender}}</span>
                    </el-form-item>
                    <el-form-item label="出生日期:" prop="birthDate">
                        <span class="static-value">{{baseInfo.birthDate}}</span>
                    </el-form-item>
                    <el-form-item label="身高:" prop="height">
                        <span class="static-value">{{baseInfo.height}}</span>
                    </el-form-item>
                    <el-form-item label="学历:" prop="education">
                        <span class="static-value">{{baseInfo.education}}</span>
                    </el-form-item>
                    <el-form-item label="婚姻状况:" prop="maritalStatus">
                        <span class="static-value">{{['否','是'][baseInfo.maritalStatus]}}</span>
                    </el-form-item>
                    <el-form-item label="血型:" prop="bloodType">
                        <span class="static-value">{{baseInfo.bloodType}}</span>
                    </el-form-item>
                </el-form>
            </div>
        </card-global>
        <!-- 所属部门版块 -->
        <div class="global-card-default">
            <div class="title">
                <i class="iconfont iconcategory"></i>
                <span>所属部门</span>
            </div>
            <el-form ref="detailForm" :inline="true" :model="baseInfo.detailForm" size="mini" label-position="right" label-width="80px"  :show-message="false">
                <el-table
                    :data="baseInfo.detailForm.departmentList"
                    :border="tableConfig.border"
                    class="global-table-default"
                    style="width: 100%">
                    <el-table-column label="序号" type="index" width="50" align="center">
                        <template slot-scope="scope">
                            <span>{{scope.$index + 1}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="departmentCode" label="部门" width="120" align="center">
                        <template slot-scope="scope">
                            <el-form-item>
                                <span class="static-value">{{scope.row.departmentName}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="postCode" label="职务" width="120" align="center">
                        <template slot-scope="scope">
                            <el-form-item>
                                <span class="static-value">{{scope.row.postName}}</span>
                            </el-form-item>
                        </template>
                    </el-table-column>
                </el-table>
            </el-form>
        </div>
        <card-global cardTitle='其他信息' style="margin-top:15px">
            <el-form ref="otherForm" :inline="true" :model="baseInfo" size="mini" label-position="right" label-width="80px">
                <el-form-item label="移动电话:" prop="mobilePhone">
                    <span class="static-value">{{baseInfo.mobilePhone}}</span>
                </el-form-item>
                <el-form-item label="办公电话:" prop="officeTelephone">
                    <span class="static-value">{{baseInfo.officeTelephone}}</span>
                </el-form-item>
                <el-form-item label="电子邮箱:" prop="email">
                    <span class="static-value">{{baseInfo.email}}</span>
                </el-form-item>
                <el-form-item label="传真:" prop="fax">
                    <span class="static-value">{{baseInfo.fax}}</span>
                </el-form-item>
                <el-form-item label="用户状态:" prop="employeeStatus">
                    <span class="static-value">{{baseInfo.employeeStatus}}</span>
                </el-form-item>
                <el-form-item label="用户类型:" prop="employeeType">
                    <span class="static-value">{{baseInfo.employeeType}}</span>
                </el-form-item>
                <el-form-item label="参工日期:" prop="participationDate">
                    <span class="static-value">{{baseInfo.participationDate}}</span>
                </el-form-item>
                <el-form-item label="入职日期:" prop="entryDate">
                    <span class="static-value">{{baseInfo.entryDate}}</span>
                </el-form-item>
                <el-form-item label="转正日期:" prop="confirmationDate">
                    <span class="static-value">{{baseInfo.confirmationDate}}</span>
                </el-form-item>
                <el-form-item label="离退日期:" prop="leaveDate">
                    <span class="static-value">{{baseInfo.leaveDate}}</span>
                </el-form-item>
                <el-form-item label="国籍:" prop="nationality">
                    <span class="static-value">{{baseInfo.nationality}}</span>
                </el-form-item>
                <el-form-item label="籍贯:" prop="nativePlace">
                    <span class="static-value">{{baseInfo.nativePlace}}</span>
                </el-form-item>
                <el-form-item label="民族:" prop="nation">
                    <span class="static-value">{{baseInfo.nation}}</span>
                </el-form-item>
                <el-form-item label="宗教:" prop="religion">
                    <span class="static-value">{{baseInfo.religion}}</span>
                </el-form-item>
            </el-form>
        </card-global>
        <!-- 附件版块 -->
        <upload-approval-global
            type="detail"
            :onlyUpload="true"
            ref="uploadApprovalGlobal" 
            :fileList="baseInfo.attachmentList"
            ></upload-approval-global>

        <div class="global-fixBottom-actionBtn">
            <el-button size="mini" @click="backBtn">返回</el-button>
        </div>
    </div>
</template>

<script>
import { Component, Vue, Watch } from 'vue-property-decorator'
import tableMixin from '@/mixins/tableMixin'

@Component({
    name: 'userDetail',
    components: {
    }
})
export default class extends tableMixin {
    baseInfo = this.getBaseInfo()
    // 设置空数据
    getBaseInfo(){
       return {
            userCode: '',
            userName: '',
            englishUserName: '',
            gender: '',
            birthDate: '',
            height: '',
            education: '',
            maritalStatus: '0',
            bloodType: '',
            mobilePhone: '',
            officeTelephone: '',
            email: '',
            fax: '',
            employeeStatus: '',
            employeeType: '',
            participationDate: '',
            entryDate: '',
            confirmationDate: '',
            leaveDate: '',
            nationality: '',
            nativePlace: '',
            nation: '',
            religion: '',
            detailForm:{
                departmentList:[
                    {
                        departmentName:'',
                        departmentCode:'',
                        postName:'',
                        postCode:''
                    }
                ]
            },
            attachmentList: []
        } 
    }

    activated() {
        if(Object.keys(this.$route.params).length > 0){
           this.getDetail(this.$route.params.userInfo)
        }
    }
    // 初始化新建数据
    getDetail(userInfo) {
        this.$API.apiGetUserDetail({id:userInfo.id}).then(res=>{
            this.baseInfo = Object.assign(this.baseInfo,this.getBaseInfo(),res.data);
            this.baseInfo.detailForm.departmentList = res.data.departmentList
            this.baseInfo.attachmentList = this.baseInfo.attachmentList || []
            console.log(this.baseInfo)
        })
        
    }
    // 返回按钮
    backBtn(){
        this.$store.commit('DELETE_TAB', this.$route.path);
        this.$router.push({ name: 'userList'})
    }
}
</script>

<style lang="scss" scoped>

</style>
