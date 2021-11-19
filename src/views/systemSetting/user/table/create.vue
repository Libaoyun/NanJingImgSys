<template>
    <div style="padding-bottom: 46px">
        <card-global>
            <div>
                <el-form ref="doForm" :inline="true" :rules="formRules" :model="baseInfo" size="mini" label-position="right" label-width="80px">
                    <el-form-item label="编号:" prop="userCode">
                        <el-input v-model="baseInfo.userCode" placeholder="请输入编号"></el-input>
                    </el-form-item>
                    <el-form-item label="姓名:" prop="userName">
                        <el-input  v-model="baseInfo.userName" placeholder="请输入姓名"></el-input>
                    </el-form-item>
                    <el-form-item label="英文名:" prop="englishUserName">
                        <el-input  v-model="baseInfo.englishUserName" placeholder="请输入英文名"></el-input>
                    </el-form-item>
                    <el-form-item label="性别:" prop="gender">
                        <el-select v-model="baseInfo.gender" placeholder="请选择性别">
                            <el-option value="未知"></el-option>
                            <el-option value="男"></el-option>
                            <el-option value="女"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="出生日期:" prop="birthDate">
                        <el-date-picker v-model="baseInfo.birthDate" value-format="yyyy-MM-dd" type="date" placeholder="请选择日期"></el-date-picker>
                    </el-form-item>
                    <el-form-item label="身高:" prop="height">
                        <el-input  v-model="baseInfo.height" placeholder="请输入身高"></el-input>
                    </el-form-item>
                    <el-form-item label="学历:" prop="education">
                        <el-select v-model="baseInfo.education" placeholder="请选择学历">
                            <el-option value="未知"></el-option>
                            <el-option value="小学"></el-option>
                            <el-option value="初中"></el-option>
                            <el-option value="高中"></el-option>
                            <el-option value="专科"></el-option>
                            <el-option value="本科"></el-option>
                            <el-option value="硕士"></el-option>
                            <el-option value="博士"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="婚姻状况:" prop="maritalStatus">
                        <el-switch v-model="baseInfo.maritalStatus" active-value="1" inactive-value="0"></el-switch>
                    </el-form-item>
                    <el-form-item label="血型:" prop="bloodType">
                        <el-select v-model="baseInfo.bloodType" placeholder="请选择血型">
                            <el-option value="未知"></el-option>
                            <el-option value="A"></el-option>
                            <el-option value="B"></el-option>
                            <el-option value="AB"></el-option>
                            <el-option value="O"></el-option>
                        </el-select>
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
            <i class="el-icon-plus add-detail-btn" @click="addDepartmentDetail('detailForm')"></i>
            <el-form ref="detailForm" :inline="true" :rules="formRules" :model="baseInfo.detailForm" size="mini" label-position="right" label-width="80px"  :show-message="false">
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
                            <el-form-item :prop="'departmentList.'+scope.$index+'.departmentCode'" :rules="formRules['departmentCode']">
                                <el-input readonly v-model="scope.row.departmentName" @focus="chooseDepartment(scope)"></el-input>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="postCode" label="职务" width="120" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'departmentList.'+scope.$index+'.postCode'" :rules="formRules['postCode']">
                                <el-input readonly v-model="scope.row.postName" @focus="choosePosition(scope)"></el-input>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" width="60" align="center">
                        <template slot-scope="scope">
                            <el-popconfirm title="是否要删除此行？" @confirm="deleteDepartmentDetail(scope)" placement="top" cancelButtonType="plain">
                                <i slot="reference" class="el-icon-delete delete-detail-btn"></i>
                            </el-popconfirm>
                        </template>
                    </el-table-column>
                </el-table>
            </el-form>
        </div>
        <card-global cardTitle='其他信息' style="margin-top:15px">
            <el-form ref="otherForm" :inline="true" :rules="formRules" :model="baseInfo" size="mini" label-position="right" label-width="80px">
                <el-form-item label="移动电话:" prop="mobilePhone">
                    <el-input v-model="baseInfo.mobilePhone" placeholder="请输入移动电话"></el-input>
                </el-form-item>
                <el-form-item label="办公电话:" prop="officeTelephone">
                    <el-input  v-model="baseInfo.officeTelephone" placeholder="请输入办公电话"></el-input>
                </el-form-item>
                <el-form-item label="电子邮箱:" prop="email">
                    <el-input  v-model="baseInfo.email" placeholder="请输入电子邮箱"></el-input>
                </el-form-item>
                <el-form-item label="传真:" prop="fax">
                    <el-input  v-model="baseInfo.fax" placeholder="请输入传真"></el-input>
                </el-form-item>
                <el-form-item label="用户状态:" prop="employeeStatus">
                    <el-select v-model="baseInfo.employeeStatus" placeholder="请选择用户状态">
                        <el-option value="在职"></el-option>
                        <el-option value="离职"></el-option>
                        <el-option value="退休"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="用户类型:" prop="employeeType">
                    <el-select v-model="baseInfo.employeeType" placeholder="请选择用户类型">
                        <el-option value="正式"></el-option>
                        <el-option value="试用期"></el-option>
                        <el-option value="实习生"></el-option>
                        <el-option value="临时用户"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="参工日期:" prop="participationDate">
                    <el-date-picker v-model="baseInfo.participationDate" value-format="yyyy-MM-dd" type="date" placeholder="请选择日期"></el-date-picker>
                </el-form-item>
                <el-form-item label="入职日期:" prop="entryDate">
                    <el-date-picker v-model="baseInfo.entryDate" value-format="yyyy-MM-dd" type="date" placeholder="请选择日期"></el-date-picker>
                </el-form-item>
                <el-form-item label="转正日期:" prop="confirmationDate">
                    <el-date-picker v-model="baseInfo.confirmationDate" value-format="yyyy-MM-dd" type="date" placeholder="请选择日期"></el-date-picker>
                </el-form-item>
                <el-form-item label="离退日期:" prop="leaveDate">
                    <el-date-picker v-model="baseInfo.leaveDate" value-format="yyyy-MM-dd" type="date" placeholder="请选择日期"></el-date-picker>
                </el-form-item>
                <el-form-item label="国籍:" prop="nationality">
                    <el-input  v-model="baseInfo.nationality" placeholder="请输入国籍"></el-input>
                </el-form-item>
                <el-form-item label="籍贯:" prop="nativePlace">
                    <el-input  v-model="baseInfo.nativePlace" placeholder="请输入籍贯"></el-input>
                </el-form-item>
                <el-form-item label="民族:" prop="nation">
                    <el-input  v-model="baseInfo.nation" placeholder="请输入民族"></el-input>
                </el-form-item>
                <el-form-item label="宗教:" prop="religion">
                    <el-input  v-model="baseInfo.religion" placeholder="请输入宗教"></el-input>
                </el-form-item>
            </el-form>
        </card-global>
        <!-- 附件版块 -->
        <upload-approval-global type="create" ref="uploadApprovalGlobal" :fileList="baseInfo.attachmentList"></upload-approval-global>
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
import $alert from '../alert'

@Component({
    name: 'userNew',
    components: {
    }
})
   
export default class extends tableMixin {
    loadingBtn = 0;
    baseInfo = this.getBaseInfo()
    dialogImageUrl = ''
    dialogVisible = false
    disabled = false
    isShow = true
    formRules = {
        userCode: [{ required: true, message: '请输入编号', trigger: 'change' }],
        userName: [{ required: true, message: '请输入姓名', trigger: 'change' }],
        departmentCode: [{ required: true, message: '请选择部门', trigger: 'change' }],
        postCode: [{ required: true, message: '请选择职务', trigger: 'change' }]
    }
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
           if(this.$route.params.initData){
               this.initData()
           }
        }
    }

    // 初始化新建数据
    initData() {
        this.$refs['doForm'].resetFields();
        this.$refs['detailForm'].resetFields();
        this.$refs['otherForm'].resetFields();
        this.baseInfo = Object.assign(this.baseInfo,this.getBaseInfo())
    }
    addDepartmentDetail(formName) {
        this.$refs[formName].validate(valid => {
            if (valid) {
                this.baseInfo.detailForm.departmentList.push({
                    departmentName:'',
                    departmentCode:'',
                    postName:'',
                    postCode:''
                })
            } else {
              return false
            }
        })
    }
    deleteDepartmentDetail(scope) {
        if(this.baseInfo.detailForm.departmentList.length === 1) {
            this.$message('至少填写一条数据')
            return
        }
        this.baseInfo.detailForm.departmentList.splice(scope.$index, 1)
    }
    handleRemove(file) {
        console.log(file);
        this.$refs['my-upload'].clearFiles();
        this.isShow = true
    }
    handlePictureCardPreview(file) {
        this.dialogImageUrl = file.url;
        this.dialogVisible = true;
    }
    changeFile(file) {
        this.isShow = false
    }

    // 选择部门
    chooseDepartment(scope) {
        $alert.alertDepartmentTree().then((res)=>{
            this.$set(this.baseInfo.detailForm.departmentList[scope.$index],'departmentName',res.orgName)
            this.$set(this.baseInfo.detailForm.departmentList[scope.$index],'departmentCode',res.orgId)
        })
    }
    // 选择职务
    choosePosition(scope) {
        console.log(scope)
        if(!scope.row.departmentCode) {
            this.$message({message:'请先选择部门'})
            return
        }
        $alert.alertPositionTree(scope.row.departmentCode).then(res=>{
            this.$set(this.baseInfo.detailForm.departmentList[scope.$index],'postName',res.orgName)
            this.$set(this.baseInfo.detailForm.departmentList[scope.$index],'postCode',res.orgId)
        })
    }
    formatSendData(data) {
        data = JSON.parse(JSON.stringify(data));
        data.creatorOrgId = this.$store.getters.currentOrganization.organizationId,
        data.creatorOrgName = this.$store.getters.currentOrganization.organizationName,
        data.menuCode = this.MENU_CODE_LIST.userList
        data.departmentList = data.detailForm.departmentList
        delete data.detailForm
        return data
    }
    // 保存按钮
    saveBtn(){
        const _self = this
        let formArr = ['doForm','detailForm']
        let resultArr = []
        formArr.forEach(item => { //根据表单的ref校验
            resultArr.push(checkForm(_self,item))
        })
        Promise.all(resultArr).then(() => {
            let params = this.formatSendData(this.baseInfo);
            this.$confirm('确定保存当前表单?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
                }).then(() => {
                    this.baseInfo.attachmentList = this.$refs.uploadApprovalGlobal.getFileList();
                    this.loadingBtn = 1;
                    this.$API.apiCreateUser(params).then(res=>{
                        this.loadingBtn = 0;
                        this.$message({
                            type: 'success',
                            message: '提交成功!'
                        });
                        this.$store.commit('DELETE_TAB', this.$route.path);
                        this.$router.push({ name: 'userList'})
                    }).catch(() => {
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
                this.$router.push({ name: 'userList'})
            })
    }
}
</script>

<style lang="scss" scoped>
/deep/.el-upload--picture-card {
    width: 120px;
    height: 120px;
    line-height: 120px;
}
.isShow {
    /deep/ .el-upload--picture-card {
        display: none;
    }
}
.user-card {
    /deep/ .card-global-form {
        display: flex;
    }
}
</style>
