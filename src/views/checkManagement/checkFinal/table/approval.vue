<template>
  <div class="page">
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
          <el-form-item label="单据编号" prop="serialNumber">
            <span>{{ baseInfo.serialNumber }}</span>
          </el-form-item>
          <el-form-item label="创建人" prop="createUser">
            <span>{{ baseInfo.createUser }}</span>
          </el-form-item>
          <el-form-item label="创建时间" prop="createTime">
            <span>{{ baseInfo.createTime | formatTime('yyyy-MM-dd') }}</span>
          </el-form-item>
          <el-form-item label="成果名称" prop="jobTitle">
            <span>{{ baseInfo.jobTitle }}</span>
          </el-form-item>
          <el-form-item label="申请评审单位" prop="unitName">
            {{baseInfo.unitName}}
          </el-form-item>
          <el-form-item label="项目负责人" prop="applyUserName">
            <span>{{ baseInfo.applyUserName }}</span>
          </el-form-item>
          <el-form-item label="负责人岗位" prop="postName">
            {{ baseInfo.postName }}
          </el-form-item>
          <el-form-item label="联系电话" prop="telephone">
            <span>{{ baseInfo.telephone }}</span>
          </el-form-item>
          <el-form-item label="起始年度" prop="startYear">
            {{ baseInfo.startYear }}
          </el-form-item>
          <el-form-item label="结束年度" prop="endYear">
            {{ baseInfo.endYear }}
          </el-form-item>
          <el-form-item label="结题申报人" prop="createUser">
            {{ baseInfo.createUser }}
          </el-form-item>
          <el-form-item label="申请评审日期" prop="createdDate">
            {{ baseInfo.createdDate }}
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
        <el-form-item label="成果内容简介（用途、原理、关键技术、技术特征、国内外同类成果比较、效益分析等）" prop="projectAbstract">
          <el-input
            type="textarea"
            disabled
            v-model="baseInfo.checkInfo.projectAbstract"
            maxlength="200" rows="5" resize="none"
          ></el-input>
        </el-form-item>
        <el-form-item label="经济技术文件目录及提供单位" prop="directoryAndUnit">
          <el-input
            type="textarea"
            disabled
            v-model="baseInfo.checkInfo.directoryAndUnit"
            maxlength="200" rows="5" resize="none"
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
            {{ scope.row.createTime | formatDate }}
          </template>
        </el-table-column>

      </el-table>
    </card-global>
    <!--审批-->
    <approval-global type="approval" ref="approvalGlobal"></approval-global>
    <!-- 附件版块 -->
    <upload-attachment ref="uploadAttachment" :fileList="baseInfo.attachmentList" :onlyView="true" :menuCode="MENU_CODE_LIST.checkFinalList"></upload-attachment>
    <div class="global-fixBottom-actionBtn">
      <el-button size="mini" @click="backBtn">返回</el-button>
      <loading-btn class="primary" size="mini" @click="resolve(1)" type="primary" :loading="loadingBtn">同意</loading-btn>
      <loading-btn class="rejectOrigin" size="mini" @click="reject(3)" :loading="loadingBtn">退回发起人</loading-btn>
      <loading-btn size="mini" @click="reject(2)" :loading="loadingBtn">退回上节点</loading-btn>
    </div>
  </div>
</template>

<script>
import { Component, Vue, Watch } from "vue-property-decorator";
import tableMixin from "@/mixins/tableMixin";
@Component({
  name: "approval",
  components: {},
})
export default class extends tableMixin {
  loadingBtn = 0;
  baseInfo = this.getBaseInfo();
  // 设置空数据
  getBaseInfo() {
    return {
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
      projectApplyMainId: '',
      // 结题验收说明
      checkInfo: {
        achievementInfo: '', // 成果内容简介
        catalogueCompany: '', // 经济技术文件目录及提供单位
      },
      // 研究人员名单
      userInfoList: [],
      // 附件
      attachmentList: [],
    };
  }
  activated() {
    if(Object.keys(this.$route.params).length > 0){
      if(this.$route.params.businessId){
        this.waitId = this.$route.params.waitId
        this.initData(this.$route.params.businessId)
      }
      if(this.$route.params.routerName){
        this.routerName = this.$route.params.routerName
      }else{
        this.routerName = 'checkFinalList'
      }
    }
  }

  // 初始化新建数据
  initData(businessId) {
    this.$refs.baseForm?.resetFields();
    this.$refs.checkForm?.resetFields();
    this.$API.apiCheckFinalDetail({businessId}).then((res) => {
      this.baseInfo = Object.assign(this.baseInfo, this.getBaseInfo(), res.data)
      this.baseInfo.checkInfo.projectAbstract = res.data.projectAbstract
      this.baseInfo.checkInfo.directoryAndUnit = res.data.directoryAndUnit
      !res.data.attachmentList && (this.baseInfo.attachmentList = [])
      // 查看审批流程
      this.$refs.approvalGlobal.getApprovalRecordList(this.baseInfo.processInstId,this.baseInfo.serialNumber)
    })
  }
  
  resolve(loadingBtn){
    // 审批意见校验通过
    this.$refs.approvalGlobal.isCheckComplete().then((remark)=>{
      this.loadingBtn = loadingBtn;
      var params = {
        creatorOrgId : this.$store.getters.currentOrganization.organizationId,
        creatorOrgName : this.$store.getters.currentOrganization.organizationName,
        menuCode : this.MENU_CODE_LIST.checkFinalList,
        approveComment:remark,
        approveType:1,//1:同意 2:回退上一个节点 3：回退到发起人
        waitId:this.waitId
      }
      this.$API.apiCheckFinalApproval(params).then(res=>{
        this.$message({
            type:'success',
            message:'审批成功'
        })
        this.backBtn();
        this.loadingBtn = 0;
      }).catch(err=>{
        this.loadingBtn = 0;
      })
    })
  }
  // 退回上节点
  reject(loadingBtn){
    // 审批意见校验通过
    this.$refs.approvalGlobal.isCheckComplete().then((remark)=>{
      this.loadingBtn = loadingBtn;
      var params = {
        creatorOrgId : this.$store.getters.currentOrganization.organizationId,
        creatorOrgName : this.$store.getters.currentOrganization.organizationName,
        menuCode : this.MENU_CODE_LIST.checkFinalList,
        approveComment:remark,
        approveType:loadingBtn,//1:同意 2:回退上一个节点 3：回退到发起人
        waitId:this.waitId
      }
      this.$API.apiCheckFinalApproval(params).then(res=>{
        this.$message({
            type:'success',
            message:'审批成功'
        })
        this.backBtn();
        this.loadingBtn = 0;
      }).catch(err=>{
        this.loadingBtn = 0;
      })
    })
  }
  // 返回按钮
  backBtn() {
    this.resetData()
  }
  // 清空数据
  resetData(isRefresh) {
    this.$store.commit('DELETE_TAB', this.$route.path);
    this.$router.push({ name: this.routerName,params:{refresh:isRefresh}})
  }
}
</script>

<style lang="scss" scoped>
.page {
  width: 100%;
  height: 100%;
  padding-bottom: 46px;
  .check-form{
    /deep/ .el-form-item{
      width: 66.6%;
      .el-form-item__content {
        display: block;
        margin-left: 80px;
      }
    }
  }
}
</style>
