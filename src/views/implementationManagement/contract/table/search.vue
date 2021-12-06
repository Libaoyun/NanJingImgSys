<!--
* @description 
* @fileName search.vue
* @author Miaoxy
* @date 2021/11/24 21:03:56
!-->
<template>
  <el-drawer
    title=""
    :visible="searchDialog"
    direction="rtl"
    custom-class="search-drawer"
    size="250px"
    @close="$emit('update:searchDialog', false)"
    ref="drawer"
  >
    <span slot="title"><i class="iconfont icon-sousuo"></i> 搜索</span>
    <div>
      <el-form
        :model="searchParams"
        :inline="true"
        size="mini"
        label-width="70px"
        label-position="right"
      >
        <el-form-item label="合同编号" prop="conId">
          <el-input
            v-model.trim="searchParams.conId"
            placeholder="请输入合同编号"
          ></el-input>
        </el-form-item>
        <el-form-item label="项目名称" prop="orgName">
          <el-input
            v-model.trim="searchParams.orgName"
            placeholder="请输入项目名称"
          ></el-input>
        </el-form-item>
        <el-form-item label="密级" prop="secretLevel">
          <el-input
            v-model.trim="searchParams.secretLevel"
            placeholder="请输入密级"
          ></el-input>
        </el-form-item>
        <el-form-item label="合同状态" prop="conStatus">
          <el-input
            v-model.trim="searchParams.conStatus"
            placeholder="请输入合同状态"
          ></el-input>
        </el-form-item>
        <el-form-item label="负责人" prop="leader">
          <el-input
            v-model.trim="searchParams.leader"
            placeholder="请输入项目负责人"
          ></el-input>
        </el-form-item>
        <el-form-item label="所属单位" prop="companyName">
          <el-input
            v-model.trim="searchParams.companyName"
            placeholder="请输入所属单位"
          ></el-input>
        </el-form-item>
        <el-form-item label="起始年度" prop="startYear">
          <el-date-picker
            v-model="searchParams.startYear"
            value-format="yyyy-MM-dd"
            type="date"
            placeholder="请选择起始年度"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="编制人" prop="compiler">
          <el-input
            v-model.trim="searchParams.compiler"
            placeholder="请输入编制人"
          ></el-input>
        </el-form-item>
        <el-form-item label="创建日期" prop="createTime">
          <el-date-picker
            v-model="searchParams.createTime"
            value-format="yyyy-MM-dd"
            type="date"
            placeholder="请选择创建日期"
          ></el-date-picker>
        </el-form-item>
      </el-form>
      <div style="text-align:right;margin-top:10px">
        <el-button size="small" @click="resetForm">重置</el-button>
        <el-button size="small" type="primary" @click="searchSubmit"
          >搜索</el-button
        >
      </div>
    </div>
  </el-drawer>
</template>

<script>
import { Component, Vue, Prop } from 'vue-property-decorator'

@Component({
  name: 'search',
})
export default class extends Vue {
  @Prop() searchDialog
  @Prop() searchParams

  resetForm() {
    this.$emit('update:searchParams', {})
  }
  searchSubmit() {
    this.$emit('searchSubmit')
    this.$emit('update:searchDialog', false)
  }
}
</script>

<style lang="scss" scoped></style>
