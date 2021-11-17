<template>
    <div style="padding-bottom: 46px">
        <card-global>
            <el-form ref="doForm" :inline="true" :rules="formRules" :model="baseInfo" size="mini" label-position="right" label-width="80px">
                <el-form-item label="编号:" prop="val1">
                    <el-input v-model="baseInfo.val1" placeholder="请输入编号"></el-input>
                </el-form-item>
                <el-form-item label="姓名:" prop="val2">
                    <el-input  v-model="baseInfo.val2" placeholder="请输入姓名"></el-input>
                </el-form-item>
                <el-form-item label="英文名:" prop="val3">
                    <el-input  v-model="baseInfo.val3" placeholder="请输入英文名"></el-input>
                </el-form-item>
                <el-form-item label="性别:" prop="val4">
                    <el-select v-model="baseInfo.val4" placeholder="请选择性别">
                        <el-option value="未知"></el-option>
                        <el-option value="男"></el-option>
                        <el-option value="女"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="出生日期:" prop="val5">
                    <el-date-picker v-model="baseInfo.val5" value-format="yyyy-MM-dd" type="date" placeholder="请选择日期"></el-date-picker>
                </el-form-item>
                <el-form-item label="身高:" prop="val6">
                    <el-input  v-model="baseInfo.val6" placeholder="请输入身高"></el-input>
                </el-form-item>
                <el-form-item label="学历:" prop="val7">
                    <el-select v-model="baseInfo.val7" placeholder="请选择学历">
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
                <el-form-item label="婚姻状况:" prop="val8">
                    <el-switch v-model="baseInfo.val8"></el-switch>
                </el-form-item>
                <el-form-item label="血型:" prop="val9">
                    <el-select v-model="baseInfo.val9" placeholder="请选择血型">
                        <el-option value="未知"></el-option>
                        <el-option value="A"></el-option>
                        <el-option value="B"></el-option>
                        <el-option value="AB"></el-option>
                        <el-option value="O"></el-option>
                    </el-select>
                </el-form-item>
            </el-form>
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
                    <el-table-column prop="department" label="部门" width="120" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'departmentList.'+scope.$index+'.department'" :rules="formRules['department']">
                                <el-select v-model="scope.row.department">
                                    <el-option value="1"></el-option>
                                </el-select>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column prop="position" label="职务" width="120" align="center">
                        <template slot-scope="scope">
                            <el-form-item :prop="'departmentList.'+scope.$index+'.position'" :rules="formRules['position']">
                                <el-select v-model="scope.row.position">
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
                <el-form-item label="移动电话:" prop="val10">
                    <el-input v-model="baseInfo.val10" placeholder="请输入移动电话"></el-input>
                </el-form-item>
                <el-form-item label="办公电话:" prop="val11">
                    <el-input  v-model="baseInfo.val11" placeholder="请输入办公电话"></el-input>
                </el-form-item>
                <el-form-item label="电子邮箱:" prop="val12">
                    <el-input  v-model="baseInfo.val12" placeholder="请输入电子邮箱"></el-input>
                </el-form-item>
                <el-form-item label="传真:" prop="val13">
                    <el-input  v-model="baseInfo.val13" placeholder="请输入传真"></el-input>
                </el-form-item>
                <el-form-item label="员工状态:" prop="val14">
                    <el-select v-model="baseInfo.val14" placeholder="请选择员工状态">
                        <el-option value="在职"></el-option>
                        <el-option value="离职"></el-option>
                        <el-option value="退休"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="员工类型:" prop="val15">
                    <el-select v-model="baseInfo.val15" placeholder="请选择员工类型">
                        <el-option value="正式"></el-option>
                        <el-option value="试用期"></el-option>
                        <el-option value="实习生"></el-option>
                        <el-option value="临时员工"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="参工日期:" prop="val16">
                    <el-date-picker v-model="baseInfo.val16" value-format="yyyy-MM-dd" type="date" placeholder="请选择日期"></el-date-picker>
                </el-form-item>
                <el-form-item label="入职日期:" prop="val17">
                    <el-date-picker v-model="baseInfo.val17" value-format="yyyy-MM-dd" type="date" placeholder="请选择日期"></el-date-picker>
                </el-form-item>
                <el-form-item label="转正日期:" prop="val18">
                    <el-date-picker v-model="baseInfo.val18" value-format="yyyy-MM-dd" type="date" placeholder="请选择日期"></el-date-picker>
                </el-form-item>
                <el-form-item label="离退日期:" prop="val19">
                    <el-date-picker v-model="baseInfo.val19" value-format="yyyy-MM-dd" type="date" placeholder="请选择日期"></el-date-picker>
                </el-form-item>
                <el-form-item label="国籍:" prop="val20">
                    <el-input  v-model="baseInfo.val20" placeholder="请输入国籍"></el-input>
                </el-form-item>
                <el-form-item label="籍贯:" prop="val21">
                    <el-input  v-model="baseInfo.val21" placeholder="请输入籍贯"></el-input>
                </el-form-item>
                <el-form-item label="民族:" prop="val22">
                    <el-input  v-model="baseInfo.val22" placeholder="请输入民族"></el-input>
                </el-form-item>
                <el-form-item label="宗教:" prop="val23">
                    <el-input  v-model="baseInfo.val23" placeholder="请输入宗教"></el-input>
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
    name: 'edit',
    components: {
    }
})
   
export default class extends tableMixin {
    loadingBtn = 0;
    baseInfo = this.getBaseInfo()
    formRules = {
        val1: [{ required: true, message: '请输入编号', trigger: 'change' }],
        val2: [{ required: true, message: '请输入姓名', trigger: 'change' }],
        department: [{ required: true, message: '请选择部门', trigger: 'change' }],
        position: [{ required: true, message: '请选择职务', trigger: 'change' }]
    }
    // 设置空数据
    getBaseInfo(){
       return {
            val1: '',
            val2: '',
            val3: '',
            val4: '',
            val5: '',
            val6: '',
            val7: '',
            val8: '',
            val9: '',
            val10: '',
            val11: '',
            val12: '',
            val13: '',
            val14: '',
            val15: '',
            val16: '',
            val17: '',
            val18: '',
            val19: '',
            val20: '',
            val21: '',
            val22: '',
            val23: '',
            detailForm:{
                departmentList:[
                    {
                        department:'',
                        position:''
                    }
                ]
            }
        } 
    }
    activated() {
        if(Object.keys(this.$route.params).length > 0){
           if(this.$route.params.businessId){
               this.initData()
               this.getDetail(this.$route.params.businessId);
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

    getDetail(id) {
        
    }
    addDepartmentDetail(formName) {
        this.$refs[formName].validate(valid => {
            if (valid) {
                this.baseInfo.detailForm.departmentList.push({
                    department:'',
                    position:''
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
                    this.$API.apiEditUser(this.baseInfo).then(res=>{
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

</style>
