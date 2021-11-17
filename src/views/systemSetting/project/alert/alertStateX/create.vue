<template>
  <el-dialog
    title="新增项目"
    :visible.sync="dialog"
    :close-on-click-modal="false"
    custom-class="global-dialog-default dialog-fixed-scroll"
    @close="closeDialog"
    width="800px"
  >
    <div class="content">
      <el-form
        ref="dictionaryForm"
        :rules="formRules"
        :inline="true"
        :model="alertStateX"
        size="mini"
        label-position="left"
        label-width="80px"
      >
        <el-form-item label="项目名称" prop="name">
          <el-input v-model="alertStateX.name" placeholder="请输入项目名称"></el-input>
        </el-form-item>
        <el-form-item label="上级部门" prop="sjbm">
            <el-select v-model="alertStateX.sjbm">
                <el-option value="1"></el-option>
            </el-select>
        </el-form-item>
        <el-form-item label="是否启用:" prop="isValid">
            <el-select v-model="alertStateX.isValid" placeholder="请选择">
                <el-option label="启用" :value="1"></el-option>
                <el-option label="禁用" :value="0"></el-option>
            </el-select>
        </el-form-item>
        <el-form-item label="项目描述" class="large">
          <el-input type="textarea" v-model="alertStateX.val3" placeholder="请输入项目描述"></el-input>
        </el-form-item>
        <el-form-item label="备注" class="large">
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
  name: 'addProject'
})
export default class addProject extends dictionaryMixin {
  dialog = false
  loadingBtn = 0

  init(){
  }

  alertStateX = {
    name:'',
    status: 1,
    remark:'',
  }
  formRules = {
    name: [
      { required: true, message: '请输入项目名称', trigger: 'change' },
      { max:30, message: '项目名称不超过30个字符', trigger: 'change' } 
    ],
    sjbm: [
      { required: true, message: '请选择上级部门', trigger: 'change' }
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
          this.alertStateX,
          this.params
        )
        this.loadingBtn = loadingBtn;
        this.$API.apiCreateProjectOrg(params).then(res=>{
          this.loadingBtn = 0;
          this.$message({
            type: 'success',
            message: '新增成功!'
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
</style>
