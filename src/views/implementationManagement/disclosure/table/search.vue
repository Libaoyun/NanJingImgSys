<!--
* @description
* @fileName search.vue
* @author Miaoxy
* @date 2021/11/24 21:04:16
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
        <el-form-item label="单据编号" prop="serialNumber">
          <el-input
              v-model.trim="searchParams.serialNumber"
              placeholder="请输入单据编号"
          ></el-input>
        </el-form-item>
        <el-form-item label="项目名称" prop="projectName">
          <el-input
              v-model.trim="searchParams.projectName"
              placeholder="请输入项目名称"
          ></el-input>
        </el-form-item>
        <el-form-item label="项目负责人" prop="projectLeaderName">
          <el-input
              v-model.trim="searchParams.projectLeaderName"
              placeholder="请输入项目负责人"
          ></el-input>
        </el-form-item>
        <el-form-item label="联系电话" prop="contactNumber">
          <el-input
              v-model.trim="searchParams.contactNumber"
              placeholder="请输入联系电话"
          ></el-input>
        </el-form-item>
        <el-form-item label="交底书编制人" prop="contactNumber">
          <el-input
              v-model.trim="searchParams.contactNumber"
              placeholder="请输入交底书编制人"
          ></el-input>
        </el-form-item>
        <el-form-item label="编制时间" prop="contactNumber">
          <el-date-picker
              v-model="searchParams.contactNumber"
              value-format="yyyy-MM-dd"
              type="date"
              placeholder="请选择编制时间"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="创建人" prop="contactNumber">
          <el-input
              v-model.trim="searchParams.contactNumber"
              placeholder="请输入创建人"
          ></el-input>
        </el-form-item>
        <el-form-item label="创建时间" prop="contactNumber">
          <el-date-picker
              v-model="searchParams.contactNumber"
              value-format="yyyy-MM-dd"
              type="date"
              placeholder="请选择创建时间"
          ></el-date-picker>
        </el-form-item>
        <!--        <el-form-item label="更新时间" prop="updateTime">-->
        <!--          <el-date-picker-->
        <!--            v-model="searchParams.updateTime"-->
        <!--            value-format="yyyy-MM-dd"-->
        <!--            type="date"-->
        <!--            placeholder="请选择更新时间"-->
        <!--          ></el-date-picker>-->
        <!--        </el-form-item>-->
      </el-form>
      <div style="text-align:right;margin-top:10px">
        <el-button size="small" @click="resetForm">重置</el-button>
        <el-button size="small" type="primary" @click="searchSubmit"
        >搜索
        </el-button
        >
      </div>
    </div>
  </el-drawer>
</template>

<script>
import {Component, Vue, Prop} from 'vue-property-decorator';

@Component({
  name: 'search',
})
export default class extends Vue {
  @Prop() searchDialog;
  @Prop() searchParams;

  resetForm() {
    this.$emit('update:searchParams', {});
  }

  searchSubmit() {
    this.$emit('searchSubmit');
    this.$emit('update:searchDialog', false);
  }
}
</script>

<style lang="scss" scoped></style>
