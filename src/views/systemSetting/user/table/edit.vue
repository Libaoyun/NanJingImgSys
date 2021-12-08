<template>
    <div style="padding-bottom: 46px">
        <card-global>
            <div>
                <el-form ref="doForm" :inline="true" :rules="formRules" :model="baseInfo" size="mini" label-position="right" label-width="80px">
                    <el-form-item label="用户编号:" prop="userCode">
                        <el-input v-model="baseInfo.userCode" disabled></el-input>
                    </el-form-item>
                    <el-form-item label="用户名称:" prop="userName">
                        <el-input  v-model="baseInfo.userName" placeholder="请输入用户名称"></el-input>
                    </el-form-item>
                    <el-form-item label="性别:" prop="genderCode">
                        <el-select v-model="baseInfo.genderCode" placeholder="请选择性别" @change="baseInfo.gender = GET_DICTIONARY_TEXT(genderList,baseInfo.genderCode)">
                            <el-option v-for="item in genderList" :label="item.label" :value="item.value" :key='item.value'></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="出生日期:" prop="birthDate">
                        <el-date-picker v-model="baseInfo.birthDate" value-format="yyyy-MM-dd" type="date" placeholder="请选择日期"></el-date-picker>
                    </el-form-item>
                    <el-form-item label="身高:" prop="height">
                        <el-input maxlength="30" v-model="baseInfo.height" placeholder="请输入身高"></el-input>
                    </el-form-item>
                    <el-form-item label="学历:" prop="educationCode">
                        <el-select v-model="baseInfo.educationCode" placeholder="请选择学历" @change="baseInfo.education = GET_DICTIONARY_TEXT(educationList,baseInfo.educationCode)">
                            <el-option v-for="item in educationList" :label="item.label" :value="item.value" :key='item.value'></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="是否已婚:" prop="maritalStatus">
                        <el-select v-model="baseInfo.maritalStatus" placeholder="请选择">
                            <el-option label="是" value="1"></el-option>
                            <el-option label="否" value="0"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="血型:" prop="bloodTypeCode">
                        <el-select v-model="baseInfo.bloodTypeCode" placeholder="请选择血型" @change="baseInfo.bloodType = GET_DICTIONARY_TEXT(bloodTypeList,baseInfo.bloodTypeCode)">
                            <el-option v-for="item in bloodTypeList" :label="item.label" :value="item.value" :key='item.value'></el-option>
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
                <el-form-item label="手机号码:" prop="mobilePhone">
                    <el-input v-model="baseInfo.mobilePhone" placeholder="请输入移动电话"></el-input>
                </el-form-item>
                <el-form-item label="电子邮箱:" prop="email">
                    <el-input  v-model="baseInfo.email" placeholder="请输入电子邮箱"></el-input>
                </el-form-item>
                <el-form-item label="其他:" prop="fax">
                    <el-input maxlength="30" v-model="baseInfo.fax" placeholder="请输入其他联系方式"></el-input>
                </el-form-item>
                <el-form-item label="员工状态:" prop="employeeStatusCode">
                    <el-select v-model="baseInfo.employeeStatusCode" placeholder="请选择员工状态" @change="baseInfo.employeeStatus = GET_DICTIONARY_TEXT(employeeStatusList,baseInfo.employeeStatusCode)">
                        <el-option v-for="item in employeeStatusList" :label="item.label" :value="item.value" :key='item.value'></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="员工类型:" prop="employeeTypeCode">
                    <el-select v-model="baseInfo.employeeTypeCode" placeholder="请选择员工类型" @change="baseInfo.employeeType = GET_DICTIONARY_TEXT(employeeTypeList,baseInfo.employeeTypeCode)">
                        <el-option v-for="item in employeeTypeList" :label="item.label" :value="item.value" :key='item.value'></el-option>
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
                    <el-input maxlength="30" v-model="baseInfo.nationality" placeholder="请输入国籍"></el-input>
                </el-form-item>
                <el-form-item label="籍贯:" prop="nativePlace">
                    <el-input maxlength="30" v-model="baseInfo.nativePlace" placeholder="请输入籍贯"></el-input>
                </el-form-item>
                <el-form-item label="民族:" prop="nation">
                    <el-input maxlength="30" v-model="baseInfo.nation" placeholder="请输入民族"></el-input>
                </el-form-item>
                <el-form-item label="宗教:" prop="religion">
                    <el-input maxlength="30" v-model="baseInfo.religion" placeholder="请输入宗教"></el-input>
                </el-form-item>
            </el-form>
        </card-global>
        <!-- 附件版块 -->
        <upload-attachment ref="uploadAttachment" :fileList="baseInfo.attachmentList" :menuCode="MENU_CODE_LIST.userList"></upload-attachment>
        <div class="global-fixBottom-actionBtn">
            <el-button size="mini" @click="backBtn">返回</el-button>
            <loading-btn size="mini" type="primary" @click="saveBtn" :loading="loadingBtn">保存</loading-btn>
        </div>
    </div>
</template>

<script>
import { Component,Mixins, Vue, Watch } from 'vue-property-decorator'
import tableMixin from '@/mixins/tableMixin'
import dictionaryMixin from '@/mixins/dictionaryMixin'
import { checkForm } from '@/utils/index'
import $alert from '../alert'

@Component({
    name: 'userEdit',
    components: {
    }
})
   
export default class extends Mixins(tableMixin,dictionaryMixin) {
    loadingBtn = 0;
    baseInfo = this.getBaseInfo()
    dialogImageUrl = ''
    dialogVisible = false
    disabled = false
    isShow = true
    formRules = {
        userCode: [{ required: true, message: '请输入用户编号', trigger: 'change' }],
        userName: [{ required: true, message: '请输入用户名称', trigger: 'change' }],
        mobilePhone:[
            {
                pattern: /^[1]([3-9])[0-9]{9}$/,
                message: "请输入正确的手机号码",
                trigger: "change"
            }
        ],
        email:[
            {
                pattern:/^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/,
                message:'请输入正确的邮箱',
                trigger:'change'
            }
        ],
        departmentCode: [{ required: true, message: '请选择部门', trigger: 'change' }],
        postCode: [{ required: true, message: '请选择职务', trigger: 'change' }]
    }
    // 设置空数据
    getBaseInfo(){
       return {
            userCode: '',
            userName: '',
            englishUserName: '',
            genderCode: '',
            gender:'',
            birthDate: '',
            height: '',
            education:'',
            educationCode: '',
            maritalStatus: '',
            bloodType:'',
            bloodTypeCode: '',
            mobilePhone: '',
            officeTelephone: '',
            email: '',
            fax: '',
            employeeStatus:'',
            employeeStatusCode: '',
            employeeType:'',
            employeeTypeCode: '',
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
    created() {
        this.getGenderList()
        this.getEducationList()
        this.getBloodTypeList()
        this.getEmployeeStatusList()
        this.getEmployeeTypeList()
    }
    activated() {
        if(Object.keys(this.$route.params).length > 0){
            this.getDetail(this.$route.params.userInfo)
        }
    }

    // 初始化新建数据
    getDetail(userInfo) {
        let formArr = ['doForm','detailForm','otherForm'];
        formArr.forEach((item)=>{
            this.$refs[item].resetFields();
        })
        this.$API.apiGetUserDetail({id:userInfo.id}).then(res=>{
            this.baseInfo = Object.assign(this.baseInfo,this.getBaseInfo(),res.data);
            this.baseInfo.detailForm.departmentList = res.data.departmentList
            this.baseInfo.attachmentList = this.baseInfo.attachmentList || []
        })
        
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
            this.$set(this.baseInfo.detailForm.departmentList[scope.$index],'postName','')
            this.$set(this.baseInfo.detailForm.departmentList[scope.$index],'postCode','')
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
                    this.baseInfo.attachmentList = this.$refs.uploadAttachment.getFileList();
                    this.loadingBtn = 1;
                    this.$API.apiUpdateUser(params).then(res=>{
                        this.loadingBtn = 0;
                        this.$message({
                            type: 'success',
                            message: '提交成功!'
                        });
                        this.$store.commit('DELETE_TAB', this.$route.path);
                        this.$router.push({ name: 'userList',params:{refresh:true}})
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
