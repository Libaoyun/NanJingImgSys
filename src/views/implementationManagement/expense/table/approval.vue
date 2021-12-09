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
          <el-form-item label="所属月份" prop="belongingMonth">
            {{baseInfo.belongingMonth}}
          </el-form-item>
        </el-form>
      </div>
    </card-global>

    <card-global cardTitle="支出事项">
      <el-form
        ref="expenseItemsForm"
        :inline="true"
        :model="baseInfo.expenseItems"
        size="small"
        label-position="right"
        label-width="100px"
        label-suffix=":"
      >
        <el-form-item label="一级科目" prop="firstSubject">
          {{baseInfo.expenseItems.firstSubject}}
        </el-form-item>
        <el-form-item label="二级科目" prop="secondarySubject">
          {{baseInfo.expenseItems.secondarySubject}}
        </el-form-item>
        <div class="col2">
          <el-form-item label="支出依据" prop="payNoted">
            <el-input
              type="textarea"
              v-model="baseInfo.expenseItems.payNoted"
              maxlength="200" rows="5" resize="none"
              disabled
            ></el-input>
          </el-form-item>
        </div>
      </el-form>
    </card-global>

    <card-global cardTitle="支出预算">
      <el-form
        ref="expenseBudgetForm"
        :inline="true"
        :model="baseInfo.expenseBudget"
        size="small"
        label-position="right"
        label-width="100px"
        label-suffix=":"
      >
        <el-form-item label="预算总额" prop="budgetAmount">
          {{baseInfo.expenseBudget.budgetAmount}}
        </el-form-item>
        <el-form-item label="已累计支出" prop="accumulatedExpenditure">
          {{baseInfo.expenseBudget.accumulatedExpenditure}}
        </el-form-item>
        <el-form-item label="预算结余" prop="budgetBalance">
          {{baseInfo.expenseBudget.budgetBalance}}
        </el-form-item>
        <el-form-item label="本次金额(元)" prop="amount">
          {{baseInfo.expenseBudget.amount}}
        </el-form-item>
        <el-form-item label="结余金额(元)" prop="balanceAmount">
          {{baseInfo.expenseBudget.balanceAmount}}
        </el-form-item>
      </el-form>
    </card-global>

    <card-global cardTitle="研发项目课题组申报意见">
      <el-form
        ref="remark"
        :inline="true"
        :model="baseInfo"
        size="small"
        label-position="right"
        label-width="100px"
        label-suffix=":"
      >
        <div class="col2">
          <el-form-item label="申报意见" prop="remark">
            <el-input
              type="textarea"
              v-model="baseInfo.remark"
              placeholder="请输入研发项目课题组申报意见"
              maxlength="200" rows="5" resize="none"
              disabled
            ></el-input>
          </el-form-item>
        </div>
      </el-form>
    </card-global>

    <!--审批-->
    <approval-global type="approval" ref="approvalGlobal"></approval-global>
    <!-- 附件版块 -->
    <upload-attachment ref="uploadAttachment" :fileList="baseInfo.attachmentList" :onlyView="true" :menuCode="MENU_CODE_LIST.expenseList"></upload-attachment>
    <div class="global-fixBottom-actionBtn">
      <el-button size="mini" @click="backBtn">返回</el-button>
      <loading-btn class="primary" size="mini" @click="resolve(1)" type="primary" :loading="loadingBtn">同意</loading-btn>
      <loading-btn class="rejectOrigin" size="mini" @click="reject(3)" :loading="loadingBtn">退回发起人</loading-btn>
      <loading-btn size="mini" @click="reject(2)" :loading="loadingBtn">退回上节点</loading-btn>
    </div>
  </div>
</template>

<script>
import { Component, Mixins, Vue, Watch } from "vue-property-decorator";
import tableMixin from "@/mixins/tableMixin";
import dictionaryMixin from '@/mixins/dictionaryMixin'
@Component({
  name: "approval",
  components: {},
})
export default class extends Mixins(tableMixin,dictionaryMixin) {
  loadingBtn = 0;
  baseInfo = this.getBaseInfo();
  processInstId = null
  serialNumber = null
  // 设置空数据
  getBaseInfo() {
    return {
      createUser: this.$store.getters.userInfo?.userName, // 创建人
      createUserId: this.$store.getters.userInfo?.userCode, // 结题申报人ID
      createTime: new Date(), // 创建时间
      projectName: '', // 项目名称
      applyUserName: '', // 项目负责人
      month: '', // 所属月份
      remark: '', // 研发项目课题组申报意见
      projectApplyMainId: '',
      // 支出事项
      expenseItems: {
        firstSubject: '', // 一级科目
        secondarySubject: '', // 二级科目
        payNoted: '', // 支出依据
      },
      // 支出预算
      expenseBudget: {
        budgetAmount: '',// 预算总额
        accumulatedExpenditure: '',// 已累计支出
        budgetBalance: '',//预算结余
        amount: '',// 本次金额
        balanceAmount: '',// 结余金额
      },
      
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
        this.routerName = 'expenseList'
      }
    }
  }

  // 初始化新建数据
  initData(businessId) {
    this.processInstId = null
    this.serialNumber = null
    this.$refs.baseForm?.resetFields();
    this.$refs.expenseItemsForm?.resetFields();
    this.$refs.expenseBudgetForm?.resetFields();

    this.$API.apiExpenseDetail({businessId}).then((res) => {
      this.baseInfo = Object.assign(this.baseInfo, this.getBaseInfo(), res.data)
      // 支出事项
      this.baseInfo.expenseItems.firstSubject = res.data.firstSubject
      this.baseInfo.expenseItems.secondarySubject = res.data.secondarySubject
      this.baseInfo.expenseItems.payNoted = res.data.payNoted
      // 支出预算
      this.baseInfo.expenseBudget.budgetAmount = res.data.budgetAmount
      this.baseInfo.expenseBudget.accumulatedExpenditure = res.data.accumulatedExpenditure
      this.baseInfo.expenseBudget.budgetBalance = res.data.budgetBalance
      this.baseInfo.expenseBudget.amount = res.data.amount
      this.baseInfo.expenseBudget.balanceAmount = res.data.balanceAmount
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
        menuCode : this.MENU_CODE_LIST.expenseList,
        approveComment:remark,
        approveType:1,//1:同意 2:回退上一个节点 3：回退到发起人
        waitId:this.waitId
      }
      this.$API.apiExpenseApproval(params).then(res=>{
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
        menuCode : this.MENU_CODE_LIST.expenseList,
        approveComment:remark,
        approveType:loadingBtn,//1:同意 2:回退上一个节点 3：回退到发起人
        waitId:this.waitId
      }
      this.$API.apiExpenseApproval(params).then(res=>{
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
.create-page {
  width: 100%;
  height: 100%;
  padding-bottom: 46px;
  .col2 {
    /deep/ .el-form-item {
      width: 66.6%
    }
  }
}
</style>
