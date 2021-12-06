<template>
  <div class="create-page">
    <card-global>
      <div>
        <el-form
          ref="baseForm"
          :inline="true"
          :model="baseInfo"
          size="small"
          label-position="right"
          label-width="100px"
          label-suffix=":"
          class="base-form"
        >
          <el-form-item label="单据编号" prop="orgNumber">
            <span>{{ baseInfo.orgNumber }}</span>
          </el-form-item>
          <el-form-item label="创建人" prop="createUser">
            <span>{{ baseInfo.createUser }}</span>
          </el-form-item>
          <el-form-item label="创建时间" prop="createTime">
            <span>{{ baseInfo.createTime | formatTime('yyyy-MM-dd') }}</span>
          </el-form-item>
          <el-form-item label="成果名称" prop="achievementName">
            <span>{{ baseInfo.achievementName }}</span>
          </el-form-item>
          <el-form-item label="项目负责人" prop="projectLeader">
            <span>{{ baseInfo.projectLeader }}</span>
          </el-form-item>
          <el-form-item label="负责人岗位" prop="leadingCadre">
            {{ baseInfo.leadingCadre }}
          </el-form-item>
          <el-form-item label="联系电话" prop="phone">
            <span>{{ baseInfo.phone }}</span>
          </el-form-item>
          <el-form-item label="起始年度" prop="startYear">
            {{ baseInfo.startYear }}
          </el-form-item>
          <el-form-item label="结束年度" prop="endYear">
            {{ baseInfo.endYear }}
          </el-form-item>
          <el-form-item label="结题申报人" prop="declarant">
            {{ baseInfo.declarant }}
          </el-form-item>
          <el-form-item label="申请评审日期" prop="reviewDate">
            {{ baseInfo.reviewDate }}
          </el-form-item>
        </el-form>
      </div>
    </card-global>

    <card-global cardTitle="结题验收说明">
      <el-form
        ref="checkForm"
        :inline="true"
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
            disabled
            v-model="baseInfo.checkInfo.achievementInfo"
          ></el-input>
        </el-form-item>
        <el-form-item label="经济技术文件目录及提供单位" prop="catalogueCompany">
          <el-input
            type="textarea"
            disabled
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
    <upload-approval-global type="detail" ref="uploadApprovalGlobal" :fileList="baseInfo.attachmentList"></upload-approval-global>
    <div class="global-fixBottom-actionBtn">
      <el-button size="mini" @click="backBtn">返回</el-button>
    </div>
  </div>
</template>

<script>
import { Component, Vue, Watch } from "vue-property-decorator";
import tableMixin from "@/mixins/tableMixin";
@Component({
  name: "detail",
  components: {},
})
export default class extends tableMixin {
  loadingBtn = 0;
  baseInfo = this.getBaseInfo();
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
      if (this.$route.params.recordId) {
        this.initData();
        this.getRecordDetail(this.$route.params.recordId);
      }
    }
  }

  // 初始化新建数据
  initData() {
    this.$refs["baseForm"].resetFields();
    this.$refs["checkForm"].resetFields();
    this.baseInfo = Object.assign(this.baseInfo, this.getBaseInfo());
  }
  getRecordDetail(recordId) {
    this.baseInfo = {
      orgNumber: 'ABCDDDD20211202', // 单据编号
      createUser: this.$store.getters.userInfo?.userName, // 创建人
      createTime: new Date(), // 创建时间
      achievementName: '地铁车站防水技术研究2', // 成果名称
      projectLeader: '慕踊前', // 项目负责人
      leadingCadre: '项目总工', // 负责人岗位
      phone: '13888888888', // 联系电话
      startYear: '2021-01-01', // 起始年度
      endYear: '2021-01-01', // 结束年度
      declarant: this.$store.getters.userInfo?.userName, // 结题申报人
      reviewDate: '2021-01-01', // 申请评审日期
      // 结题验收说明
      checkInfo: {
        achievementInfo: '成果内容简介', // 成果内容简介
        catalogueCompany: '经济技术文件目录及提供单位', // 经济技术文件目录及提供单位
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
    }
  }
  // 返回按钮
  backBtn() {
    this.$store.commit("DELETE_TAB", this.$route.path);
    this.$router.push({ name: "checkFinalList" });
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
