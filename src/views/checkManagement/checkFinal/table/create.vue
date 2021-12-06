<template>
  <div class="create-page">
    <card-global>
      <div>
        <el-form
          ref="baseForm"
          :inline="true"
          :rules="baseFormRules"
          :model="baseInfo"
          size="small"
          label-position="right"
          label-width="100px"
          label-suffix=":"
          class="base-form"
        >
          <el-form-item label="单据编号" prop="orgNumber">
            <el-input placeholder="自动生成" disabled></el-input>
          </el-form-item>
          <el-form-item label="创建人" prop="createUser">
            <el-input v-model="baseInfo.createUser" disabled></el-input>
          </el-form-item>
          <el-form-item label="创建时间" prop="createTime">
            <el-input
              :value="baseInfo.createTime | formatTime('yyyy-MM-dd')"
              disabled
            ></el-input>
          </el-form-item>
          <el-form-item label="成果名称" prop="achievementName">
            <el-input
              v-model="baseInfo.achievementName"
              placeholder="点击选择审批完成的研发项目"
              readonly
              @click.native="chooseProject"
            ></el-input>
          </el-form-item>
          <el-form-item label="项目负责人" prop="projectLeader">
            <el-input v-model="baseInfo.projectLeader" placeholder="自动带入" disabled></el-input>
          </el-form-item>
          <el-form-item label="负责人岗位" prop="leadingCadre">
            <el-input v-model="baseInfo.leadingCadre" placeholder="自动带入" disabled></el-input>
          </el-form-item>
          <el-form-item label="联系电话" prop="phone">
            <el-input v-model="baseInfo.phone" placeholder="自动带入" disabled></el-input>
          </el-form-item>
          <el-form-item label="起始年度" prop="startYear">
            <el-input v-model="baseInfo.startYear" placeholder="自动带入" disabled></el-input>
          </el-form-item>
          <el-form-item label="结束年度" prop="endYear">
            <el-input v-model="baseInfo.endYear" placeholder="自动带入" disabled></el-input>
          </el-form-item>
          <el-form-item label="结题申报人" prop="declarant">
            <el-input v-model="baseInfo.declarant" disabled></el-input>
          </el-form-item>
          <el-form-item label="申请评审日期" prop="reviewDate">
            <el-date-picker
              v-model="baseInfo.reviewDate"
              value-format="yyyy-MM-dd"
              type="date"
              placeholder="请选择申请评审日期"
            ></el-date-picker>
          </el-form-item>
        </el-form>
      </div>
    </card-global>

    <card-global cardTitle="结题验收说明">
      <el-form
        ref="checkForm"
        :inline="true"
        :rules="checkFormRules"
        :model="baseInfo.checkInfo"
        size="small"
        label-position="left"
        label-width="100%"
        label-suffix=":"
        class="check-form"
      >
        <el-form-item label="成果内容简介（用途、原理、关键技术、技术特征、国内外同类成果比较、效益分析等）" prop="achievementInfo">
          <el-input
            type="textarea"
            v-model="baseInfo.checkInfo.achievementInfo"
          ></el-input>
        </el-form-item>
        <el-form-item label="经济技术文件目录及提供单位" prop="catalogueCompany">
          <el-input
            type="textarea"
            v-model="baseInfo.checkInfo.catalogueCompany"
          ></el-input>
        </el-form-item>
      </el-form>
    </card-global>

    <card-global cardTitle="研究人员名单">
      <el-table
        :data="baseInfo.userList"
        :border="tableConfig.border"
        class="global-table-default"
        style="width: 100%;">
        <el-table-column label="序号" type="index" width="50" align="center">
          <template slot-scope="scope">
            <span>{{scope.$index + 1}}</span>
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="姓名" width="100" align="center" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="idcard" label="身份证号码" width="140" align="center" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="age" label="年龄" width="50" align="center" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="sex" label="性别" width="50" align="center" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="education" label="学历" width="80" align="center" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="department" label="所属部门" width="100" align="center" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="job" label="职务职称" width="100" align="center" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="major" label="所学专业" width="100" align="center" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="work" label="现从事专业" width="100" align="center" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="company" label="所在单位" width="100" align="center" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="task" label="研究任务及分工" width="140" align="center" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="fullTimeRate" label="全时率" width="80" align="center" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="phone" label="联系电话" width="120" align="center" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="startDate" label="参与研发开始日期" width="140" align="center" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="endDate" label="参与研发结束日期" width="140" align="center" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="producer" label="编制人" width="100" align="center" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="producerTime" label="编制时间" width="180" align="center" :show-overflow-tooltip="true"></el-table-column>

      </el-table>
    </card-global>
    <!-- 附件上传 -->
    <upload-approval-global type="create" ref="uploadApprovalGlobal" :fileList="baseInfo.attachmentList"></upload-approval-global>
    <div class="global-fixBottom-actionBtn">
      <el-button size="mini" @click="backBtn">返回</el-button>
      <loading-btn
        size="mini"
        type="primary"
        @click="saveBtn(2)"
        :loading="loadingBtn"
        >保存</loading-btn
      >
      <loading-btn
        size="mini"
        type="primary"
        @click="submitBtn(1)"
        :loading="loadingBtn"
        >提交</loading-btn
      >
    </div>
  </div>
</template>

<script>
import { Component, Vue, Watch } from "vue-property-decorator";
import tableMixin from "@/mixins/tableMixin";
import { checkForm } from "@/utils/index";
import $alert from '../alert'
@Component({
  name: "create",
  components: {},
})
export default class extends tableMixin {
  loadingBtn = 0;
  baseInfo = this.getBaseInfo();
  // 基本信息表单校验规则
  baseFormRules = {
    achievementName: [
      { required: true, message: "点击选择审批完成的研发项目", trigger: "change" }
    ],
  }
  // 结题验收说明表单检验规则
  checkFormRules = {
    achievementInfo: [
      { required: true, message: "请输入", trigger: "change" }
    ],
    catalogueCompany: [
      { required: true, message: "请输入", trigger: "change" }
    ]
  }
  // 设置空数据
  getBaseInfo() {
    return {
      orgNumber: this.$store.getters.currentOrganization?.organizationId, // 单据编号
      createUser: this.$store.getters.userInfo?.userName, // 创建人
      createTime: new Date(), // 创建时间
      achievementName: '', // 成果名称
      projectLeader: '', // 项目负责人
      leadingCadre: '', // 负责人岗位
      phone: '', // 联系电话
      startYear: '', // 起始年度
      endYear: '', // 结束年度
      declarant: this.$store.getters.userInfo?.userName, // 结题申报人
      reviewDate: '', // 申请评审日期
      // 结题验收说明
      checkInfo: {
        achievementInfo: '', // 成果内容简介
        catalogueCompany: '', // 经济技术文件目录及提供单位
      },
      // 研究人员名单
      userList: [
        {
          userName: '慕踊前',
          idcard: '2121029102930102',
          age: '32',
          sex: '女',
          education: '本科',
          department: '研发部',
          job: '部员',
          major: '土木工程',
          work: '技术管理',
          company: '省道464项目',
          task: '技术管理，现场技术管理，现场技术管理，现场',
          fullTimeRate: '100',
          phone: '18911113333',
          startDate: '2021-01-01',
          endDate: '2021-01-01',
          producer: '慕踊前',
          producerTime: '2021-12-31  18:00:00'
        }
      ],
      // 附件
      attachmentList: [],
    };
  }
  activated() {
    if (Object.keys(this.$route.params).length > 0) {
      if (this.$route.params.initData) {
        this.initData();
      }
    }
  }

  // 初始化新建数据
  initData() {
    this.$refs["baseForm"].resetFields();
    this.$refs["checkForm"].resetFields();
    this.baseInfo = Object.assign(this.baseInfo, this.getBaseInfo());
  }
  // 保存按钮
  saveBtn(loadIndex) {
    const _self = this;
    let formArr = ["baseForm", 'checkForm'];
    let resultArr = [];
    formArr.forEach((item) => {
      //根据表单的ref校验
      resultArr.push(checkForm(_self, item));
    });
    this.baseInfo.attachmentList = this.$refs.uploadApprovalGlobal.getFileList();
    Promise.all(resultArr).then(() => {
      this.$confirm("确定保存当前表单?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        // 调接口
        let params = Object.assign(
          {
            creatorOrgId:
              this.$store.getters.currentOrganization.organizationId,
            creatorOrgName:
              this.$store.getters.currentOrganization.organizationName,
            menuCode: this.MENU_CODE_LIST.checkFinalList,
          },
          this.baseInfo
        );
        console.log('保存数据：', params)
        // this.loadingBtn = loadIndex;
        // this.$API
        //   .apiAddProject(params)
        //   .then((res) => {
        //     this.$message({
        //       type: "success",
        //       message: "新增成功!",
        //     });
        //     this.$store.commit("DELETE_TAB", this.$route.path);
        //     this.$router.push({ name: "checkFinalList" });
        //   })
        //   .finally(() => {
        //     this.loadingBtn = 0;
        //   });
      });
    });
  }
  // 提交按钮
  submitBtn(loadIndex) {
    const _self = this;
    let formArr = ["baseForm", 'checkForm'];
    let resultArr = [];
    formArr.forEach((item) => {
      //根据表单的ref校验
      resultArr.push(checkForm(_self, item));
    });
    Promise.all(resultArr).then(() => {
      this.$confirm("确定提交当前表单?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        // API
      });
    });
  }
  // 返回按钮
  backBtn() {
    this.$confirm("未保存的数据将丢失，是否返回?", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    }).then(() => {
      this.$store.commit("DELETE_TAB", this.$route.path);
      this.$router.push({ name: "checkFinalList" });
    });
  }
  // 选择项目名称
  chooseProject () {
    $alert.alertProjects().then(data => {
      console.log('选择的项目数据', data)
      this.$set(this.baseInfo, "achievementName", data.projectName);
      this.$set(this.baseInfo, "projectLeader", data.applicant);
      this.$set(this.baseInfo, "leadingCadre", data.job);
      this.$set(this.baseInfo, "phone", data.phone);
      this.$set(this.baseInfo, "startYear", data.startYear);
      this.$set(this.baseInfo, "endYear", data.endYear);
    });
  }
}
</script>

<style lang="scss" scoped>
.create-page {
  width: 100%;
  height: 100%;
  padding-bottom: 46px;
  .base-form /deep/ .el-form-item{
    width: 33%!important;
  }
  .check-form /deep/ .el-form-item{
    width: 100%!important;
  }
}
</style>
