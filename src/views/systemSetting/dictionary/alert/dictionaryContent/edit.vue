<template>
  <el-dialog
    title="编辑枚举值"
    :visible.sync="dialog"
    :close-on-click-modal="false"
    custom-class="global-dialog-default"
    @close="closeDialog"
    width="600px"
  >
    <div class="content">
      <el-form
        ref="dictionaryForm"
        :rules="formRules"
        :model="dictionary"
        size="mini"
        label-position="left"
        label-width="100px"
      >
        <el-form-item label="字典编码:" prop="dicEnumId">
          <el-input  v-model="dictionary.dicEnumId" placeholder="请输入字典编码"></el-input>
        </el-form-item>
        <el-form-item label="枚举值:" prop="dicEnumName">
          <el-input  v-model="dictionary.dicEnumName" placeholder="请输入枚举值"></el-input>
        </el-form-item>
        <el-form-item label="备注:" prop="remark">
          <el-input  v-model="dictionary.remark" placeholder="请输入备注" type="textarea"></el-input>
        </el-form-item>
        <el-form-item label="状态:" prop="isValid">
          <el-radio-group v-model="dictionary.isValid">
              <el-radio :label="1">启用</el-radio>
              <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
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
  name: 'addMenu'
})
export default class addMenu extends dictionaryMixin {
  dialog = false
  loadingBtn = 0

  init(){
  }

  dictionary = {}
  formRules = {
    dicEnumId: [{ required: true, message: '请输入字典编码', trigger: 'change' }],
    dicEnumName: [{ required: true, message: '请输入枚举值', trigger: 'change' }],
    isValid:[{ required: true, message: '请选择状态', trigger: 'change' }],
    remark:[{ required: true, message: '请输入备注', trigger: 'change' }],
  }

  

  closeDialog () {
    // this.promise.reject();
    this.$refs['dictionaryForm'].resetFields();
    this.dialog = false;
  }
  
  submitBtn (loadingBtn) {
    console.log(this.params);
    this.$refs['dictionaryForm'].validate((valid) => {
      if (valid) {
        // 调接口
        this.loadingBtn = loadingBtn;
        const data = {
          menuCode:this.MENU_CODE_LIST.dictionaryList,
          creatorOrgId : this.$store.getters.currentOrganization.organizationId,
          creatorOrgName : this.$store.getters.currentOrganization.organizationName,
        }
        let params = Object.assign({},this.dictionary,data);

        this.$API.apiUpdateDictionary(params).then(res=>{
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
    height: auto;
  }
}
</style>
