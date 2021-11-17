<template>
    <div style="padding-bottom: 46px">
        <card-global class="user-card">
            <div style="width:calc(100% - 140px)">
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
                        <el-switch v-model="baseInfo.maritalStatus"></el-switch>
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
            <div class="my-upload">
                <el-upload
                ref="my-upload"
                :on-change="changeFile"
                :class="{'isShow':!isShow}"
                action="#"
                list-type="picture-card"
                :limit="1"
                :auto-upload="false">
                    <i slot="default" class="el-icon-plus"></i>
                    <div slot="file" slot-scope="{file}">
                        <img
                            class="el-upload-list__item-thumbnail"
                            :src="file.url" alt=""
                        >
                        <span class="el-upload-list__item-actions">
                            <span class="el-upload-list__item-preview" @click="handlePictureCardPreview(file)">
                                <i class="el-icon-zoom-in"></i>
                            </span>
                            <span
                            v-if="!disabled"
                            class="el-upload-list__item-delete"
                            @click="handleRemove(file)"
                            >
                                <i class="el-icon-delete"></i>
                            </span>
                        </span>
                    </div>
                </el-upload>
                <el-dialog :visible.sync="dialogVisible">
                    <img width="100%" :src="dialogImageUrl" alt="">
                </el-dialog>
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
                                <el-select v-model="scope.row.departmentCode">
                                    <el-option value="1"></el-option>
                                </el-select>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="postCode" label="职务" width="120" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'departmentList.'+scope.$index+'.postCode'" :rules="formRules['postCode']">
                                <el-select v-model="scope.row.postCode">
                                    <el-option value="2"></el-option>
                                </el-select>
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
                <el-form-item label="员工状态:" prop="employeeStatus">
                    <el-select v-model="baseInfo.employeeStatus" placeholder="请选择员工状态">
                        <el-option value="在职"></el-option>
                        <el-option value="离职"></el-option>
                        <el-option value="退休"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="员工类型:" prop="employeeType">
                    <el-select v-model="baseInfo.employeeType" placeholder="请选择员工类型">
                        <el-option value="正式"></el-option>
                        <el-option value="试用期"></el-option>
                        <el-option value="实习生"></el-option>
                        <el-option value="临时员工"></el-option>
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
    name: 'create',
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
            maritalStatus: '',
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
                        departmentCode:'',
                        postCode:''
                    }
                ]
            }
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
                    departmentCode:'',
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
    // 保存按钮
    saveBtn(){
        const _self = this
        let formArr = ['doForm','detailForm']
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
                    this.loadingBtn = 1;
                    this.baseInfo.attachmentList = this.$refs.uploadApprovalGlobal.getFileList();
                    this.$API.apiCreateUser(this.baseInfo).then(res=>{
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
