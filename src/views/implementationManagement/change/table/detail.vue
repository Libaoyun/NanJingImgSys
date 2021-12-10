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
            {{baseInfo.serialNumber}}
          </el-form-item>
          <el-form-item label="创建人" prop="createUser">
            {{baseInfo.createUser}}
          </el-form-item>
          <el-form-item label="创建时间" prop="createTime">
            {{baseInfo.createTime | formatTime('yyyy-MM-dd')}}
          </el-form-item>
          <el-form-item label="项目名称" prop="projectName">
            {{baseInfo.projectName}}
          </el-form-item>
           <el-form-item label="项目负责人" prop="applyUserName">
            {{baseInfo.applyUserName}}
          </el-form-item>
          <el-form-item label="联系电话" prop="telephone">
            {{baseInfo.telephone}}
          </el-form-item>
          <el-form-item label="变更类型" prop="changeTypeCode">
            {{baseInfo.changeType}}
          </el-form-item>
        </el-form>
      </div>
    </card-global>

    <card-global cardTitle="变更说明">
      <el-form
        ref="changeForm"
        :inline="true"
        :model="baseInfo.changeInfo"
        size="mini"
        label-position="left"
        label-suffix=":"
      >
        <el-form-item label="要求变更的原项目相关部分内容" prop="partContent" class="large">
          <el-input type="textarea" disabled v-model="baseInfo.changeInfo.partContent" minlength="200" rows="5" resize="none"></el-input>
        </el-form-item>
        <el-form-item label="要求变更的内容或建议" prop="changeAdvise" class="large">
          <el-input type="textarea" disabled v-model="baseInfo.changeInfo.changeAdvise" minlength="200" rows="5" resize="none"></el-input>
        </el-form-item>
        <el-form-item label="变更理由" prop="changeReason" class="large">
          <el-input type="textarea" disabled v-model="baseInfo.changeInfo.changeReason" minlength="200" rows="5" resize="none"></el-input>
        </el-form-item>
        <el-form-item label="项目实施情况" prop="implementation" class="large">
          <el-input type="textarea" disabled v-model="baseInfo.changeInfo.implementation" minlength="200" rows="5" resize="none"></el-input>
        </el-form-item>
        <el-form-item label="经费使用情况" prop="fundsUse" class="large">
          <el-input type="textarea" disabled v-model="baseInfo.changeInfo.fundsUse" minlength="200" rows="5" resize="none"></el-input>
        </el-form-item>
      </el-form>
    </card-global>

    <card-global cardTitle="变更明细" type="table">
      <!-- 周期变更 -->
      <div key="1" v-if="baseInfo.changeTypeCode === '1'">
        <el-form
          ref="cycleForm"
          :inline="true"
          :model="baseInfo.detailForm"
          size="mini"
          label-position="right"
          :show-message="false"
        >
          <el-table
            :data="baseInfo.detailForm.cycleList"
            :border="tableConfig.border"
            class="global-table-default"
            style="width: 100%;">
            <el-table-column label="序号" type="index" width="50" align="center">
              <template slot-scope="scope">
                <span>{{scope.$index + 1}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="dataStatus" label="数据状态" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <span>{{scope.row.dataStatus==='0'?'变更前':'变更后'}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="startYear" label="开始年度" align="center" :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column prop="endYear" label="结束年度" align="center" :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column prop="cycleUpdateUser" label="变更人" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="cycleUpdateTime" label="变更时间" align="center" :show-overflow-tooltip="true"></el-table-column>
          </el-table>
        </el-form>
      </div>
      <!-- 预算变更 -->
      <div key="2" v-if="baseInfo.changeTypeCode === '2'">
        <el-form 
          ref="budgetForm" 
          :inline="true" 
          :model="baseInfo.detailForm" 
          size="mini" 
          :show-message="false">
            <el-table
              :data="baseInfo.detailForm.budgetDetailList"
              :border="tableConfig.border"
              class="global-table-default"
              style="width: 100%">
              <el-table-column label="经费来源预算" align="center">
                <el-table-column prop="sourceAccount" label="科目" align="center">
                  <template slot-scope="scope">
                    <el-form-item :prop="'budgetDetailList.'+scope.$index+'.sourceAccount'">
                      <span>{{scope.row.sourceAccount}}</span>
                    </el-form-item>
                  </template>
                </el-table-column>
                <el-table-column prop="sourceBudget" label="预算数 (万元)" align="center">
                </el-table-column>
              </el-table-column>
              
              <el-table-column label="经费支出预算" align="center">
                <el-table-column prop="expenseAccount" label="科目" align="center">
                  <template slot-scope="scope">
                    <el-form-item :prop="'budgetDetailList.'+scope.$index+'.expenseAccount'">
                      <span>{{scope.row.expenseAccount}}</span>
                    </el-form-item>
                  </template>
                </el-table-column>
                <el-table-column prop="expenseBudget" label="预算数 (万元)" align="center">
                </el-table-column>
              </el-table-column>
            </el-table>
        </el-form>
      </div>
      <!-- 人员变更 -->
      <div key="3" v-if="baseInfo.changeTypeCode === '3'">
        <el-form
          ref="userForm"
          :inline="true"
          :model="baseInfo.detailForm"
          size="mini"
          label-position="right"
          :show-message="false"
        >
          <el-table
            :data="baseInfo.detailForm.userInfoList"
            :border="tableConfig.border"
            :row-style="setTableRowStyle"
            class="global-table-default"
            style="width: 100%;">
            <el-table-column label="序号" type="index" width="50" align="center">
              <template slot-scope="scope">
                <span>{{scope.$index + 1}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="userName" label="姓名" width="100" align="center" :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column prop="idCard" label="身份证号码" width="140" align="center" :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column prop="age" label="年龄" width="80" align="center" :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column prop="gender" label="性别" width="120" align="center" :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column prop="education" label="学历" width="80" align="center" :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column prop="belongDepartment" label="所属部门" width="120" align="center" :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column prop="belongPost" label="职务职称" width="120" align="center" :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column prop="majorStudied" label="所学专业" width="120" align="center" :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column prop="majorWorked" label="现从事专业" width="120" align="center" :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column prop="belongUnit" label="所在单位" width="120" align="center" :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column prop="taskDivision" label="研究任务及分工" width="140" align="center" :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column prop="workRate" label="全时率" width="120" align="center" :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column prop="telephone" label="联系电话" width="120" align="center" :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column prop="startDate" label="参与研发开始日期" width="170" align="center" :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column prop="endDate" label="参与研发结束日期" width="170" align="center" :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column prop="userStatus" label="状态" width="100" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                {{scope.row.userStatus==='1'?'正常':'调离'}}
              </template>
            </el-table-column>
            <el-table-column prop="creatorUser" label="编制人" width="100" align="center" :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column prop="createTime" label="编制时间" width="180" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <span>{{scope.row.createTime | formatTime('yyyy-MM-dd')}}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-form>
      </div>
    </card-global>
    <!-- 审批 -->
    <approval-global type="detail" ref="approvalGlobal"></approval-global>
    <!-- 附件版块 -->
    <upload-attachment ref="uploadAttachment" :fileList="baseInfo.attachmentList" :onlyView="true" :menuCode="MENU_CODE_LIST.changeList"></upload-attachment>
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
      createUser: this.$store.getters.userInfo?.userName, // 创建人
      createTime: new Date(), // 创建时间
      projectName: '', // 项目名称
      applyUserName: '', // 项目负责人
      applyUserId: '',
      projectApplyMainId: '',
      telephone: '', // 联系电话
      changeTypeCode: '1', // 变更类型
      changeType: '',

      // 变更说明
      changeInfo: {
        partContent: '', // 要求变更的原项目相关部分内容
        changeAdvise: '', // 要求变更的内容或建议
        changeReason: '', // 变更理由
        implementation: '', // 项目实施情况
        fundsUse: '', // 经费使用情况
      },
      // 变更明细
      detailForm: {
        cycleList:[ // 周期变更
          {
            dataStatus: '0',
            startYear: '',
            endYear: '',
            cycleUpdateUser: '',
            cycleUpdateUserId: '',
            cycleUpdateTime: '',
            editable: 0
          },
          {
            dataStatus: '1',
            startYear: '',
            endYear: '',
            cycleUpdateUser: '',
            cycleUpdateUserId: '',
            cycleUpdateTime: '',
            editable: 1
          }
        ], 
        budgetDetailList: [], // 预算变更
        userInfoList: [], // 人员变更
      },
      // 选择的项目
      projectDataInfo: {},
      // 附件
      attachmentList: [],
    };
  }
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
    }
  }
  
  // 初始化新建数据
  initData(businessId) {
    this.$refs.baseForm?.resetFields();
    this.$refs.changeForm?.resetFields();
    this.$refs.cycleForm?.resetFields(); // 周期变更
    this.$refs.budgetForm?.resetFields(); // 预算变更
    this.$refs.userForm?.resetFields(); // 人员变更
    this.$API.apiChangeDetail({businessId}).then((res) => {
      this.baseInfo = Object.assign(this.baseInfo, this.getBaseInfo(), res.data)
      
      // 变更说明
      this.baseInfo.changeInfo.partContent = res.data.partContent
      this.baseInfo.changeInfo.changeAdvise = res.data.changeAdvise
      this.baseInfo.changeInfo.changeReason = res.data.changeReason
      this.baseInfo.changeInfo.implementation = res.data.implementation
      this.baseInfo.changeInfo.fundsUse = res.data.fundsUse

      // 
      if (res.data.changeTypeCode === '1') {
        this.baseInfo.detailForm.cycleList = res.data.cycleList
      } else if (res.data.changeTypeCode === '2') {
        this.baseInfo.detailForm.budgetDetailList = res.data.budgetDetailList
      } else if (res.data.changeTypeCode === '3') {
        this.baseInfo.detailForm.userInfoList = res.data.userInfoList
      }
      
      !res.data.attachmentList && (this.baseInfo.attachmentList = [])

      // 查看审批流程
      this.$refs.approvalGlobal.getApprovalRecordList(this.baseInfo.processInstId,this.baseInfo.serialNumber)
    })
  }
  // 返回按钮
  backBtn() {
    this.resetData()
  }
  resetData(bool) {
    this.$store.commit("DELETE_TAB", this.$route.path);
    this.$router.push({ name: "changeList", params: { refresh: bool } });
  }
  // 设置表格某一行的背景色
  setTableRowStyle ({row, rowIndex}) {
    if (row.userStatus === '2') {
      return { backgroundColor: '#8080805c' }
    }
  }
}
</script>

<style lang="scss" scoped>
.page {
  width: 100%;
  height: 100%;
  padding-bottom: 46px;
  .el-form {
    /deep/ .el-form-item.large {
        width: 66.6%;
        &.inline {
            width: 33.3%;
        }
        .el-form-item__label {
            width: 100% !important;
            text-align: left;
            display: block;
        }
        .el-form-item__content {
            margin-left: 80px;
        }
        .el-textarea__inner {
            &::placeholder {
                text-align: center;
                line-height: 90px;
            }
        }
    }
  }   
}
</style>
