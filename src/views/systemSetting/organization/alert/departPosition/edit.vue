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
        ref="dictionaryForm"
        :inline="true"
        :rules="formRules"
        :model="alertStateX"
        size="mini"
        label-position="right"
        label-width="120px"
      >
        <el-form-item label="类型:" class="large">
          <el-select v-model="type" placeholder="请选择类型" @change="changeType">
            <el-option value="bm" label="部门"></el-option>
            <el-option value="zw" label="职务"></el-option>
          </el-select>
        </el-form-item>
        <el-divider></el-divider>
        <div v-show="type === 'bm'">
          <el-form-item label="编码:" prop="index1">
            <el-input v-model="alertStateX.index1" placeholder="请输入编码"></el-input>
          </el-form-item>
          <el-form-item label="部门名称:" prop="index2">
            <el-input v-model="alertStateX.index2" placeholder="请输入部门名称"></el-input>
          </el-form-item>
          <el-form-item label="部门简称:">
            <el-input v-model="alertStateX.index3" placeholder="请输入部门简称"></el-input>
          </el-form-item>
          <el-form-item label="是否启用:" prop="isValid">
            <el-select v-model="alertStateX.isValid" placeholder="请选择">
                <el-option label="启用" :value="1"></el-option>
                <el-option label="禁用" :value="0"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="部门类型:">
            <el-select v-model="alertStateX.index4" placeholder="请选择部门类型">
              <el-option value="bm" label="部门"></el-option>
              <el-option value="zw" label="职务"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="部门级别:">
            <el-select v-model="alertStateX.index4" placeholder="请选择部门级别">
              <el-option value="bm" label="部门"></el-option>
              <el-option value="zw" label="职务"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="传真:">
            <el-input v-model="alertStateX.index4" placeholder="请输入传真"></el-input>
          </el-form-item>
          <el-form-item label="内线电话:">
            <el-input v-model="alertStateX.index4" placeholder="请输入内线电话"></el-input>
          </el-form-item>
          <el-form-item label="外线电话:">
            <el-input v-model="alertStateX.index4" placeholder="请输入外线电话"></el-input>
          </el-form-item>
          <el-form-item label="部门描述" class="large">
            <el-input type="textarea" v-model="alertStateX.index4" placeholder="请输入部门描述"></el-input>
          </el-form-item>
          <el-form-item label="部门职责" class="large">
            <el-input type="textarea" v-model="alertStateX.index4" placeholder="请输入部门职责"></el-input>
          </el-form-item>
          <el-form-item label="定编人数:">
            <el-input v-model="alertStateX.index4" placeholder="请输入定编人数"></el-input>
          </el-form-item>
          <el-form-item label="是否公司:">
            <el-switch v-model="alertStateX.index4"></el-switch>
          </el-form-item>
        </div>
        <div v-show="type === 'zw'">
          <el-form-item label="编码:" prop="index11">
            <el-input v-model="alertStateX.index11" placeholder="请输入编码"></el-input>
          </el-form-item>
          <el-form-item label="职务名称:" prop="index12">
            <el-input v-model="alertStateX.index12" placeholder="请输入职务名称"></el-input>
          </el-form-item>
          <el-form-item label="职务等级:">
            <el-select v-model="alertStateX.index4" placeholder="请选择职务等级">
              <el-option value="bm" label="部门"></el-option>
              <el-option value="zw" label="职务"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="职务类型:">
            <el-select v-model="alertStateX.index4" placeholder="请选择职务类型">
              <el-option value="bm" label="部门"></el-option>
              <el-option value="zw" label="职务"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="是否启用:" prop="isValid">
            <el-select v-model="alertStateX.isValid" placeholder="请选择">
                <el-option label="启用" :value="1"></el-option>
                <el-option label="禁用" :value="0"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="备注" class="large">
            <el-input type="textarea" v-model="alertStateX.index4" placeholder="请输入备注"></el-input>
          </el-form-item>
        </div>
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
  name: 'editDepartPosition'
})
export default class editDepartPosition extends dictionaryMixin {
  dialog = false
  loadingBtn = 0
  type = 'bm'

  init(){
  }

  alertStateX = {
    index1:'',
    index2: '',
    index3:'',
    index4:'',
    index11:'',
    index12:''
  }
  formRules = {
    index1: [
      { required: true, message: '请输入编码', trigger: 'change' }
    ],
    index2: [
      { required: true, message: '请输入部门名称', trigger: 'change' }
    ],
    index11: [
      { required: true, message: '请输入编码', trigger: 'change' }
    ],
    index12: [
      { required: true, message: '请输入职务名称', trigger: 'change' }
    ],
  }

  changeType(e) {
    this.$refs['dictionaryForm'].resetFields();
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
          this.alertStateX,
          this.params
        )
        this.loadingBtn = loadingBtn;
        this.$API.apiCreateDepartPositionOrg(params).then(res=>{
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
