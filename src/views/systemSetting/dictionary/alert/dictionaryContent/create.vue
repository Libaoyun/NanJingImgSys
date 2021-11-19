<template>
  <el-dialog
    title="添加枚举值"
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
        <el-form-item label="是否启用:" prop="isValid">
          <el-select v-model="dictionary.isValid" placeholder="请选择">
              <el-option label="启用" :value="1"></el-option>
              <el-option label="禁用" :value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="描述:" prop="remark">
          <el-input  v-model="dictionary.remark" placeholder="请输入描述" type="textarea"></el-input>
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

  dictionary = {
    dicEnumId:'',
    dicEnumName:'',
    isValid:1,
    remark:''
  }
  formRules = {
    dicEnumId: [{ required: true, message: '请输入字典编码', trigger: 'change' }],
    dicEnumName: [{ required: true, message: '请输入枚举值', trigger: 'change' }],
    isValid:[{ required: true, message: '请选择状态', trigger: 'change' }],
    remark:[{ required: true, message: '请输入描述', trigger: 'change' }]
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
        let params = Object.assign({
            dicTypeId: this.params.dicTypeId,
            dicTypeName: this.params.dicTypeName,
            menuCode:this.MENU_CODE_LIST.dictionaryList,
            creatorOrgId : this.$store.getters.currentOrganization.organizationId,
            creatorOrgName : this.$store.getters.currentOrganization.organizationName,
        },this.dictionary);

        this.$API.apiCreateDictionary(params).then(res=>{
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
::v-deep {
  .global-dialog-default {
    position: relative;
    height: 350px;

    .dialog-footer {
      position: absolute;
      right: 10px;
      top: 80%;
    }
    .el-select.el-select--mini{
      width: 100%;
    }
  }
}
</style>
