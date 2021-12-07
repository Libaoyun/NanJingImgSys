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
          <el-form-item label="单据编号" prop="orgNumber">
            {{ baseInfo.orgNumber }}
          </el-form-item>
          <el-form-item label="创建人" prop="createUser">
            {{ baseInfo.createUser }}
          </el-form-item>
          <el-form-item label="创建时间" prop="createTime">
            {{ baseInfo.createTime | formatTime('yyyy-MM-dd') }}
          </el-form-item>
          <el-form-item label="项目名称" prop="projectName">
            {{ baseInfo.projectName }}
          </el-form-item>
           <el-form-item label="项目负责人" prop="projectLeader">
            {{ baseInfo.projectLeader }}
          </el-form-item>
          <el-form-item label="联系电话" prop="phone">
            {{ baseInfo.phone }}
          </el-form-item>
          <el-form-item label="变更类型" prop="changeType">
           {{ baseInfo.changeType }}
          </el-form-item>
        </el-form>
      </div>
    </card-global>

    <card-global cardTitle="变更说明">
      <el-form
        ref="changeForm"
        :inline="true"
        :model="baseInfo.changeInfo"
        size="small"
        label-position="left"
        label-width="100%"
        label-suffix=":"
        class="check-form"
      >
        <el-form-item label="要求变更的原项目相关部分内容" prop="originalProjectCnt">
          <el-input
            type="textarea"
            disabled
            v-model="baseInfo.changeInfo.originalProjectCnt"
          ></el-input>
        </el-form-item>
        <el-form-item label="要求变更的内容或建议" prop="advise">
          <el-input
            type="textarea"
            disabled
            v-model="baseInfo.changeInfo.advise"
          ></el-input>
        </el-form-item>
        <el-form-item label="变更理由" prop="reason">
          <el-input
            type="textarea"
            disabled
            v-model="baseInfo.changeInfo.reason"
          ></el-input>
        </el-form-item>
        <el-form-item label="项目实施情况" prop="implementationSituation">
          <el-input
            type="textarea"
            disabled
            v-model="baseInfo.changeInfo.implementationSituation"
          ></el-input>
        </el-form-item>
        <el-form-item label="经费使用情况" prop="feeUse">
          <el-input
            type="textarea"
            disabled
            v-model="baseInfo.changeInfo.feeUse"
          ></el-input>
        </el-form-item>
      </el-form>
    </card-global>

    <card-global cardTitle="变更明细">
      <!-- 周期变更 -->
      <template v-if="baseInfo.changeType === '1'">
        <el-table
          :data="baseInfo.detailForm.detailList"
          :border="tableConfig.border"
          class="global-table-default"
          style="width: 100%;">
          <el-table-column label="序号" type="index" width="50" align="center">
            <template slot-scope="scope">
              <span>{{scope.$index + 1}}</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="数据状态" align="center" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column prop="startYear" label="开始年度" align="center" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column prop="endYear" label="结束年度" align="center" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column prop="changeUser" label="变更人" align="center" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column prop="changeTime" label="变更时间" align="center" :show-overflow-tooltip="true"></el-table-column>
        </el-table>
      </template>
      <!-- 预算变更 -->
      <template v-else-if="baseInfo.changeType === '2'">222</template>
      <!-- 人员变更 -->
      <template v-else-if="baseInfo.changeType === '3'">
        <el-table
          :data="baseInfo.detailForm.detailList"
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
          <el-table-column prop="age" label="年龄" width="80" align="center" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column prop="sex" label="性别" width="120" align="center" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column prop="education" label="学历" width="80" align="center" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column prop="department" label="所属部门" width="120" align="center" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column prop="job" label="职务职称" width="120" align="center" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column prop="major" label="所学专业" width="120" align="center" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column prop="work" label="现从事专业" width="120" align="center" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column prop="company" label="所在单位" width="120" align="center" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column prop="task" label="研究任务及分工" width="140" align="center" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column prop="fullTimeRate" label="全时率" width="120" align="center" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column prop="phone" label="联系电话" width="120" align="center" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column prop="startDate" label="参与研发开始日期" width="170" align="center" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column prop="endDate" label="参与研发结束日期" width="170" align="center" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column prop="status" label="状态" width="100" align="center" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column prop="creator" label="编制人" width="100" align="center" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column prop="createdTime" label="编制时间" width="180" align="center" :show-overflow-tooltip="true"></el-table-column>
        </el-table>
      </template>
      <template v-else>444</template>
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
  
  activated() {
    if(Object.keys(this.$route.params).length > 0){
      if(this.$route.params.businessId){
        this.initData(this.$route.params.businessId)
      }
      if(this.$route.params.routerName){
        this.routerName = this.$route.params.routerName
      }else{
        this.routerName = 'changeList'
      }
      Object.assign(this,this.$route.params.ids)
    }
  }
  // 初始化新建数据
  initData(businessId) {
    this.$refs["baseForm"].resetFields();
    this.$refs["changeForm"].resetFields();
    this.$API.apiCheckFinalDetail({businessId}).then((res) => {
      this.baseInfo = Object.assign(this.baseInfo, this.getBaseInfo(), res.data)
      this.baseInfo.checkInfo.projectAbstract = res.data.projectAbstract
      this.baseInfo.checkInfo.directoryAndUnit = res.data.directoryAndUnit
      !res.data.attachmentList && (this.baseInfo.attachmentList = [])
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
  // 设置空数据
  getBaseInfo() {
    return {
      orgNumber: this.$store.getters.currentOrganization?.organizationId, // 单据编号
      createUser: this.$store.getters.userInfo?.userName, // 创建人
      createTime: new Date(), // 创建时间
      projectName: '', // 项目名称
      projectLeader: '', // 项目负责人
      phone: '', // 联系电话
      changeType: '', // 变更类型

      // 变更说明
      changeInfo: {
        originalProjectCnt: '', // 要求变更的原项目相关部分内容
        advise: '', // 要求变更的内容或建议
        reason: '', // 变更理由
        implementationSituation: '', // 项目实施情况
        feeUse: '', // 经费使用情况
      },
      // 变更明细
      detailForm: {
        detailList:[]
      },
      // 附件
      attachmentList: [],
    };
  }
}
</script>

<style lang="scss" scoped>
.page {
  width: 100%;
  height: 100%;
  padding-bottom: 46px;
  .check-form /deep/ .el-form-item{
    width: 100%!important;
  }
  .global-table-default {
    /deep/ .el-form-item__content{
      width: 100%!important;
    }
    /deep/ .el-form-item {
      margin-bottom: 0!important;
    }
  }
}
</style>
