<template>
  <el-dialog
    title="编辑字典"
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
        <el-form-item label="字典ID:" prop="dicTypeId">
          <el-input v-model="dictionary.dicTypeId" placeholder="请输入字典ID" disabled></el-input>
        </el-form-item>
        <el-form-item label="字典名称:" prop="dicTypeName">
          <el-input v-model="dictionary.dicTypeName" placeholder="请输入字典名称"></el-input>
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
  name: 'addDictionary'
})
export default class addDictionary extends dictionaryMixin {
  dialog = false
  loadingBtn = 0

  init(){
  }

  dictionary = {}
  formRules = {
    dicTypeName: [
      { required: true, message: '请输入字典名称', trigger: 'change' },
      { max:10, message: '字典名称不超过10个字符', trigger: 'change' } 
    ],
    dicTypeId: [
      { required: true, message: '请输入字典ID', trigger: 'change' },
      { pattern:/^\d{1,10}$/,message:'字典ID只能为数字且个数不超过10',trigger:'change'}  
    ]
  }

  

  closeDialog () {
    // this.promise.reject();
    this.$refs['dictionaryForm'].resetFields();
    this.dialog = false;
  }
  
  submitBtn (loadingBtn) {
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
        this.$API.apiUpdateDictionaryType(params).then(res=>{
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
    height: 200px;

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
