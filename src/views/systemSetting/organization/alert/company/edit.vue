<template>
  <el-dialog
    title="编辑公司"
    :visible.sync="dialog"
    :close-on-click-modal="false"
    custom-class="global-dialog-default"
    @close="closeDialog"
    width="400px"
  >
    <div class="content">
      <el-form
        ref="dictionaryForm"
        :rules="formRules"
        :model="alertStateX"
        size="mini"
        label-position="left"
        label-width="80px"
      >
        <el-form-item label="公司名称" prop="name">
          <el-input v-model="alertStateX.name" placeholder="请输入公司名称"></el-input>
        </el-form-item>
        <el-form-item label="是否启用:" prop="isValid">
          <el-select v-model="alertStateX.isValid" placeholder="请选择">
              <el-option label="启用" :value="1"></el-option>
              <el-option label="禁用" :value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input type="textarea" v-model="alertStateX.remark" placeholder="请输入备注"></el-input>
        </el-form-item>
      </el-form>
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
  name: 'editCompany'
})
export default class editCompany extends dictionaryMixin {
  dialog = false
  loadingBtn = 0

  init(){
  }

  alertStateX = {
  }
  formRules = {
    name: [
      { required: true, message: '请输入公司名称', trigger: 'change' },
      { max:30, message: '公司名称不超过30个字符', trigger: 'change' } 
    ],
    status: [
      { required: true, message: '请选择公司状态', trigger: 'change' }
    ],
  }

  

  closeDialog () {
    this.$refs['dictionaryForm'].resetFields();
    this.dialog = false;
  }
  
  submitBtn (loadingBtn) {
    this.$refs['dictionaryForm'].validate((valid) => {
      if (valid) {
        // 调接口
        let params = Object.assign(
          {
            menuCode:this.MENU_CODE_LIST.organization,
            operatePath:store.getters.operatePath
          },
          this.alertStateX
        )
        this.loadingBtn = loadingBtn;
        this.$API.apiUpdateCompanyOrg(params).then(res=>{
          this.loadingBtn = 0;
          this.$message({
            type: 'success',
            message: '编辑成功!'
          });
          this.promise.resolve();
          this.$refs['dictionaryForm'].resetFields();
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
</script>

<style lang="scss" scoped>
::v-deep {
  .global-dialog-default {
    position: relative;
    height: 300px;

    .dialog-footer {
      position: absolute;
      right: 10px;
      bottom:20px;
    }
    .el-select.el-select--mini{
      width: 100%;
    }
  }
}
</style>
