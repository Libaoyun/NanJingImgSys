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
          <el-form-item label="单据编号" prop="serialNumber">
            <el-input v-model="baseInfo.serialNumber" placeholder="自动生成" disabled></el-input>
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
          <el-form-item label="成果名称" prop="jobTitle">
            <el-input
              v-model="baseInfo.jobTitle"
              placeholder="点击选择审批完成的研发项目"
              readonly
              @click.native="chooseProject"
            ></el-input>
          </el-form-item>
          <el-form-item label="项目负责人" prop="applyUserName">
            <el-input v-model="baseInfo.applyUserName" placeholder="自动带入" disabled></el-input>
          </el-form-item>
          <el-form-item label="负责人岗位" prop="postName">
            <el-input v-model="baseInfo.postName" placeholder="自动带入" disabled></el-input>
          </el-form-item>
          <el-form-item label="联系电话" prop="telephone">
            <el-input v-model="baseInfo.telephone" placeholder="自动带入" disabled></el-input>
          </el-form-item>
          <el-form-item label="起始年度" prop="startYear">
            <el-input v-model="baseInfo.startYear" placeholder="自动带入" disabled></el-input>
          </el-form-item>
          <el-form-item label="结束年度" prop="endYear">
            <el-input v-model="baseInfo.endYear" placeholder="自动带入" disabled></el-input>
          </el-form-item>
          <el-form-item label="结题申报人" prop="createUser">
            <el-input v-model="baseInfo.createUser" disabled></el-input>
          </el-form-item>
          <el-form-item label="申请评审日期" prop="createdDate">
            <el-date-picker
              v-model="baseInfo.createdDate"
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
        <el-form-item label="成果内容简介（用途、原理、关键技术、技术特征、国内外同类成果比较、效益分析等）" prop="projectAbstract">
          <el-input
            type="textarea"
            v-model="baseInfo.checkInfo.projectAbstract"
          ></el-input>
        </el-form-item>
        <el-form-item label="经济技术文件目录及提供单位" prop="directoryAndUnit">
          <el-input
            type="textarea"
            v-model="baseInfo.checkInfo.directoryAndUnit"
          ></el-input>
        </el-form-item>
      </el-form>
    </card-global>

    <card-global cardTitle="研究人员名单">
      <el-table
        :data="baseInfo.userInfoList"
        :border="tableConfig.border"
        class="global-table-default"
        style="width: 100%;">
        <el-table-column label="序号" type="index" width="50" align="center">
          <template slot-scope="scope">
            <span>{{scope.$index + 1}}</span>
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="姓名" width="100" align="center" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="idCard" label="身份证号码" width="140" align="center" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="age" label="年龄" width="50" align="center" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="gender" label="性别" width="50" align="center" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="education" label="学历" width="80" align="center" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="belongDepartment" label="所属部门" width="100" align="center" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="belongPost" label="职务职称" width="100" align="center" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="majorStudied" label="所学专业" width="100" align="center" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="majorWorked" label="现从事专业" width="100" align="center" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="belongUnit" label="所在单位" width="100" align="center" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="taskDivision" label="研究任务及分工" width="140" align="center" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="workRate" label="全时率" width="80" align="center" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="telephone" label="联系电话" width="120" align="center" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="startDate" label="参与研发开始日期" width="140" align="center" :show-overflow-tooltip="true">
           <template slot-scope="scope">
            {{ scope.row.startDate | formatTime('yyyy-MM-dd') }}
          </template>
        </el-table-column>
        <el-table-column prop="endDate" label="参与研发结束日期" width="140" align="center" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            {{ scope.row.endDate | formatTime('yyyy-MM-dd') }}
          </template>
        </el-table-column>
        <el-table-column prop="creatorUser" label="编制人" width="100" align="center" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="createTime" label="编制时间" width="180" align="center" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            {{ scope.row.createTime | formatTime('yyyy-MM-dd') }}
          </template>
        </el-table-column>

      </el-table>
    </card-global>
    <!-- 附件上传 -->
    <upload-approval-global type="edit" ref="uploadApprovalGlobal" :fileList="baseInfo.attachmentList"></upload-approval-global>
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
  name: "edit",
  components: {},
})
export default class extends tableMixin {
  loadingBtn = 0;
  baseInfo = this.getBaseInfo();

  // 基本信息表单校验规则
  baseFormRules = {
    jobTitle: [
      { required: true, message: "点击选择审批完成的研发项目", trigger: "change" }
    ],
  }
  // 结题验收说明表单检验规则
  checkFormRules = {
    projectAbstract: [
      { required: true, message: "请输入", trigger: "change" }
    ],
    directoryAndUnit: [
      { required: true, message: "请输入", trigger: "change" }
    ]
  }
  // 设置空数据
  getBaseInfo() {
    return {
      serialNumber: this.$store.getters.currentOrganization?.organizationId, // 单据编号
      createUser: this.$store.getters.userInfo?.userName, // 创建人
      createUserId: this.$store.getters.userInfo?.userCode, // 结题申报人ID
      createTime: new Date(), // 创建时间
      jobTitle: '', // 成果名称
      applyUserName: '', // 项目负责人
      postName: '', // 负责人岗位
      telephone: '', // 联系电话
      startYear: '', // 起始年度
      endYear: '', // 结束年度
      createdDate: '', // 申请评审日期
      // 结题验收说明
      checkInfo: {
        projectAbstract: '', // 成果内容简介
        directoryAndUnit: '', // 经济技术文件目录及提供单位
      },
      // 研究人员名单
      userInfoList: [],
      // 附件
      attachmentList: [],
    };
  }
  activated() {
    if (Object.keys(this.$route.params).length > 0) {
      if (this.$route.params.businessId) {
        this.initData();
        this.getRecordDetail(this.$route.params.businessId);
      }
    }
  }

  // 初始化新建数据
  initData() {
    this.$refs["baseForm"].resetFields();
    this.$refs["checkForm"].resetFields();
    this.baseInfo = Object.assign(this.baseInfo, this.getBaseInfo());
  }
  getRecordDetail(businessId) {
    this.$API.apiCheckFinalDetail({businessId}).then((res) => {
      this.baseInfo = Object.assign(this.baseInfo, this.getBaseInfo(), res.data)
      this.baseInfo.checkInfo.projectAbstract = res.data.projectAbstract
      this.baseInfo.checkInfo.directoryAndUnit = res.data.directoryAndUnit
    })
  }
  // 格式化保存提交的数据
  formatSendData(data) {
    data = JSON.parse(JSON.stringify(data))
    
    data.creatorOrgId = this.$store.getters.currentOrganization.organizationId
    data.creatorOrgName = this.$store.getters.currentOrganization.organizationName
    data.menuCode = this.MENU_CODE_LIST.checkFinalList

    data.directoryAndUnit = data.checkInfo.directoryAndUnit // 经济技术文件目录及提供单位(长度：1024)
    data.projectAbstract = data.checkInfo.projectAbstract // 成果内容简介(长度：1024)
    delete data.checkInfo

    data.attachmentList = this.$refs.uploadApprovalGlobal.getFileList();
    return data
  }
  // 保存按钮
  saveBtn(loadIndex) {
    const _self = this;
    const params = this.formatSendData(this.baseInfo)
    this.loadingBtn = loadIndex;
    this.$API
      .apiCheckFinalSaveOrUpdate(params)
      .then((res) => {
        this.loadingBtn = 0;
        this.$message({
          type: "success",
          message: "编辑成功!",
        });
        this.resetData(true)
      })
      .finally(() => {
        this.loadingBtn = 0;
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
    const params = this.formatSendData(this.baseInfo)
    params.flag = 3
    Promise.all(resultArr).then(() => {
      this.$confirm("确定提交当前表单?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        this.loadingBtn = loadIndex;
        this.$API
          .apiCheckFinalSubmit(params)
          .then((res) => {
            this.$message({
              type: "success",
              message: "提交成功!",
            });
            this.resetData(true)
          })
          .finally(() => {
            this.loadingBtn = 0;
          });
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
      this.resetData()
    });
  }
  resetData(bool) {
    this.$store.commit("DELETE_TAB", this.$route.path);
    this.$router.push({ name: "checkFinalList", params: { refresh: bool } });
  }
  // 选择项目名称
  chooseProject () {
    $alert.alertProjects().then(data => {
      this.$set(this.baseInfo, "jobTitle", data.projectName); // 成果名称
      this.$set(this.baseInfo, "applyUserName", data.applyUserName); // 项目负责人
      this.$set(this.baseInfo, 'applyUserId', data.applyUserId); // 项目负责人ID 
      this.$set(this.baseInfo, "postName", data.postName); //负责人岗位
      this.$set(this.baseInfo, "telephone", data.telephone);
      this.$set(this.baseInfo, "startYear", data.startYear);
      this.$set(this.baseInfo, "endYear", data.endYear);
      this.$set(this.baseInfo, 'projectApplyMainId', data.businessId) // 项目ID
      
      this.userInfoList(data.businessId)
    });
  }
  // 获取研究人员名单
  userInfoList (businessId) {
    this.baseInfo.userInfoList = []
    this.$API.apiCheckFinalUserList({businessId}).then(res=>{
      if(res.data){
        this.baseInfo.userInfoList = res.data;
      }
    })
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
