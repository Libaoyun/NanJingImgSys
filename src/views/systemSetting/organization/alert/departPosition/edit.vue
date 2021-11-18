<template>
  <el-dialog
    title="编辑部门/职务"
    :visible.sync="dialog"
    :close-on-click-modal="false"
    custom-class="global-dialog-default"
    @close="closeDialog"
    width="800px"
  >
    <div class="content">
      <el-form 
        :inline="true"
        size="mini"
        label-position="right"
        label-width="120px">
        <el-form-item label="类型:" class="large">
            <el-select v-model="type" placeholder="请选择类型" @change="changeType" disabled>
              <el-option value="bm" label="部门"></el-option>
              <el-option value="zw" label="职务"></el-option>
            </el-select>
        </el-form-item>
      </el-form>
      <el-divider></el-divider>
      <div v-show="type === 'bm'">
        <el-form
          ref="dictionaryForm_bm"
          :inline="true"
          :rules="formRules_bm"
          :model="alertStateX_bm"
          size="mini"
          label-position="right"
          label-width="120px"
        >
          <el-form-item label="编码:" prop="departmentCode">
              <el-input v-model="alertStateX_bm.departmentCode" placeholder="请输入编码"></el-input>
            </el-form-item>
            <el-form-item label="部门名称:" prop="orgName">
              <el-input v-model="alertStateX_bm.orgName" placeholder="请输入部门名称"></el-input>
            </el-form-item>
            <el-form-item label="部门简称:" prop="departmentSimpleName">
              <el-input v-model="alertStateX_bm.departmentSimpleName" placeholder="请输入部门简称"></el-input>
            </el-form-item>
            <el-form-item label="是否启用:" prop="status">
              <el-select v-model="alertStateX_bm.status" placeholder="请选择">
                  <el-option label="启用" value="1"></el-option>
                  <el-option label="禁用" value="0"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="部门类型:" prop="departmentType">
              <el-select v-model="alertStateX_bm.departmentType" placeholder="请选择部门类型">
                <el-option value="bm" label="部门"></el-option>
                <el-option value="zw" label="职务"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="部门级别:" prop="departmentLevelCode">
              <el-select v-model="alertStateX_bm.departmentLevelCode" placeholder="请选择部门级别">
                <el-option value="bm" label="部门"></el-option>
                <el-option value="zw" label="职务"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="传真:" prop="fax">
              <el-input v-model="alertStateX_bm.fax" placeholder="请输入传真"></el-input>
            </el-form-item>
            <el-form-item label="内线电话:" prop="inTelephone">
              <el-input v-model="alertStateX_bm.inTelephone" placeholder="请输入内线电话"></el-input>
            </el-form-item>
            <el-form-item label="外线电话:" prop="outTelephone">
              <el-input v-model="alertStateX_bm.outTelephone" placeholder="请输入外线电话"></el-input>
            </el-form-item>
            <el-form-item label="部门描述" class="large" prop="departmentRemark">
              <el-input type="textarea" v-model="alertStateX_bm.departmentRemark" placeholder="请输入部门描述"></el-input>
            </el-form-item>
            <el-form-item label="部门职责" class="large" prop="departmentDuty">
              <el-input type="textarea" v-model="alertStateX_bm.departmentDuty" placeholder="请输入部门职责"></el-input>
            </el-form-item>
            <el-form-item label="定编人数:" prop="departmentPeople">
              <el-input v-model="alertStateX_bm.departmentPeople" placeholder="请输入定编人数"></el-input>
            </el-form-item>
        </el-form>
      </div>
      <div v-show="type === 'zw'">
        <el-form
          ref="dictionaryForm_zw"
          :inline="true"
          :rules="formRules_zw"
          :model="alertStateX_zw"
          size="mini"
          label-position="right"
          label-width="120px"
        >
          <el-form-item label="编码:" prop="postCode">
            <el-input v-model="alertStateX_zw.postCode" placeholder="请输入编码"></el-input>
          </el-form-item>
          <el-form-item label="职务名称:" prop="orgName">
            <el-input v-model="alertStateX_zw.orgName" placeholder="请输入职务名称"></el-input>
          </el-form-item>
          <el-form-item label="职务等级:" prop="postLevelCode">
            <el-select v-model="alertStateX_zw.postLevelCode" placeholder="请选择职务等级">
              <el-option value="bm" label="部门"></el-option>
              <el-option value="zw" label="职务"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="职务类型:" prop="postTypeCode">
            <el-select v-model="alertStateX_zw.postTypeCode" placeholder="请选择职务类型">
              <el-option value="bm" label="部门"></el-option>
              <el-option value="zw" label="职务"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="是否启用:" prop="status">
            <el-select v-model="alertStateX_zw.status" placeholder="请选择">
                <el-option label="启用" value="1"></el-option>
                <el-option label="禁用" value="0"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="备注" class="large" prop="remark">
            <el-input type="textarea" v-model="alertStateX_zw.remark" placeholder="请输入备注"></el-input>
          </el-form-item>
        </el-form>
      </div>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button size="mini" @click="closeDialog">取消</el-button>
      <el-button size="mini" type="primary" @click="submitBtn(1)" :loading="loadingBtn == 1">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { Component, Vue, Prop } from 'vue-property-decorator'
import dictionaryMixin from '@/mixins/dictionaryMixin'
import store from '@/store'

@Component({
  name: 'editDepartPosition'
})
export default class editDepartPosition extends dictionaryMixin {
  dialog = false
  loadingBtn = 0
  type = 'bm'
  alertStateX_bm = {}
  alertStateX_zw = {}

  init(){
    this.alertStateX_bm = {
      departmentCode:'',
      orgName: '',
      departmentDuty:'',
      departmentLevel:'',
      departmentLevelCode:'',
      departmentPeople:'',
      departmentRemark:'',
      departmentSimpleName:'',
      departmentType:'',
      departmentTypeCode:'',
      fax:'',
      inTelephone:'',
      outTelephone:'',
      status:'1'
    }
    this.alertStateX_zw = {
      orgName:'',
      postCode:'',
      postLevel:'',
      postLevelCode:'',
      postType:'',
      postTypeCode:'',
      remark:'',
      status:'1'
    }
    this.getDetail()
  }
  formRules_bm = {
    departmentCode: [
      { required: true, message: '请输入编码', trigger: 'change' }
    ],
    orgName: [
      { required: true, message: '请输入部门名称', trigger: 'change' }
    ]
  }
  formRules_zw = {
    postCode: [
      { required: true, message: '请输入编码', trigger: 'change' }
    ],
    orgName: [
      { required: true, message: '请输入职务名称', trigger: 'change' }
    ],
  }

  getDetail() {
    this.type = this.params.orgType == '1' ? 'bm' : 'zw'
    console.log(this.params)
    if(this.type == 'bm') {
      this.$API.apiGetDepartmentDetail({orgId:this.params.orgId}).then(res=>{
        this.alertStateX_bm = Object.assign({},this.alertStateX_bm,res.data)
      })
    } else if(this.type == 'zw') {
      this.$API.apiGetPostDetail({orgId:this.params.orgId}).then(res=>{
        this.alertStateX_zw = Object.assign({},this.alertStateX_zw,res.data)
      })
    }
  }

  changeType(e) {
    this.$refs['dictionaryForm_bm'].resetFields();
    this.$refs['dictionaryForm_zw'].resetFields();
  }

  closeDialog () {
    this.$refs['dictionaryForm_bm'].resetFields();
    this.$refs['dictionaryForm_zw'].resetFields();
    this.dialog = false;
  }
  
  submitBtn (loadingBtn) {
    if(this.type == 'bm') {
      this.$refs['dictionaryForm_bm'].validate((valid) => {
        if (valid) {
            let params = Object.assign(
              this.params,
               this.alertStateX_bm,
              {
                creatorOrgId:this.$store.getters.currentOrganization.organizationId,
                creatorOrgName:this.$store.getters.currentOrganization.organizationName,
                menuCode:this.MENU_CODE_LIST.organizationList,
              }
            )
            this.loadingBtn = loadingBtn;
            this.$API.apiUpdateDepartment(params).then(res=>{
              this.loadingBtn = 0;
              this.$message({
                type: 'success',
                message: '更新成功!'
              });
              this.promise.resolve();
              this.$refs['dictionaryForm_bm'].resetFields();
              this.dialog = false;
            }).catch(()=>{
              this.loadingBtn = 0;
            })
          } else {
        return false;
      }
    });
   } else if(this.type === 'zw') {
     this.$refs['dictionaryForm_zw'].validate((valid) => {
        if (valid) {
            let params = Object.assign(
              this.params,
              this.alertStateX_zw,
              {
                creatorOrgId:this.$store.getters.currentOrganization.organizationId,
                creatorOrgName:this.$store.getters.currentOrganization.organizationName,
                menuCode:this.MENU_CODE_LIST.organizationList,
              }
            )
            this.loadingBtn = loadingBtn;
            this.$API.apiUpdatePost(params).then(res=>{
              this.loadingBtn = 0;
              this.$message({
                type: 'success',
                message: '更新成功!'
              });
              this.promise.resolve();
              this.$refs['dictionaryForm_zw'].resetFields();
              this.dialog = false;
            }).catch(()=>{
              this.loadingBtn = 0;
            })
          } else {
        return false;
      }
    });
   }
        
      
  }
}
</script>

<style lang="scss" scoped>
::v-deep {
  .global-dialog-default {
    .el-dialog__body {
      height: 500px;
      overflow: auto;
    }
    .el-form-item.el-form-item--mini {
      width: 45%;
      .el-select {
        width: 100%;
      }
      .el-form-item__content {
        width: calc(100% - 120px);
      }
      &.large {
        width: 100%;
        .el-select {
          width: 223px;
        }
        .el-form-item__content {
          width: calc(100% - 186px);
        }
      }
    }
    .el-divider--horizontal {
      margin: 0 0 18px 0;
    }
  }
}
</style>
