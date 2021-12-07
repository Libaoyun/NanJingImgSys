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
          <el-form-item label="所属月份" prop="postName">
            {{baseInfo.month}}
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
          <el-form-item label="支出依据" prop="expenditureBasis">
            <el-input
              type="textarea"
              v-model="baseInfo.expenseItems.expenditureBasis"
              maxlength="500"
              disabled
              show-word-limit
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
        <el-form-item label="预算总额" prop="amount">
          {{baseInfo.expenseBudget.amount}}
        </el-form-item>
        <el-form-item label="已累计支出" prop="accuExpenditure">
          {{baseInfo.expenseBudget.accuExpenditure}}
        </el-form-item>
        <el-form-item label="预算结余" prop="budgetBalance">
          {{baseInfo.expenseBudget.budgetBalance}}
        </el-form-item>
        <el-form-item label="本次金额(元)" prop="money">
          {{baseInfo.expenseBudget.money}}
        </el-form-item>
        <el-form-item label="结余金额(元)" prop="balanceAmount">
          {{baseInfo.expenseBudget.balanceAmount}}
        </el-form-item>
      </el-form>
    </card-global>

     <card-global cardTitle="研发项目课题组申报意见">
      <el-form
        ref="opinion"
        :inline="true"
        :model="baseInfo"
        size="small"
        label-position="right"
        label-width="100px"
        label-suffix=":"
      >
        <div class="col2">
          <el-form-item label="申报意见" prop="opinion">
            <el-input
              type="textarea"
              v-model="baseInfo.opinion"
              placeholder="请输入研发项目课题组申报意见"
              maxlength="500"
              disabled
              show-word-limit
            ></el-input>
          </el-form-item>
        </div>
      </el-form>
    </card-global>

    
    <!-- 附件上传 -->
    <upload-approval-global type="approval" ref="uploadApprovalGlobal" :fileList="baseInfo.attachmentList"></upload-approval-global>
    <!--审批-->
    <approval-global ref="approval"></approval-global>
    <div class="global-fixBottom-actionBtn">
      <el-button size="mini" @click="backBtn">返回</el-button>
      <loading-btn
        size="mini"
        type="primary"
        @click="reject(2)"
        :loading="loadingBtn"
        >回退</loading-btn
      >
      <loading-btn
        size="mini"
        type="primary"
        @click="resolve(1)"
        :loading="loadingBtn"
        >同意</loading-btn
      >
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

  // 设置空数据
  getBaseInfo() {
    return {
      serialNumber: this.$store.getters.currentOrganization?.organizationId, // 单据编号
      createUser: this.$store.getters.userInfo?.userName, // 创建人
      createUserId: this.$store.getters.userInfo?.userCode, // 结题申报人ID
      createTime: new Date(), // 创建时间
      projectName: '', // 项目名称
      applyUserName: '', // 项目负责人
      month: '', // 所属月份
      opinion: '', // 研发项目课题组申报意见
      // 支出事项
      expenseItems: {
        firstSubject: '', // 一级科目
        secondarySubject: '', // 二级科目
        expenditureBasis: '', // 支出依据
      },
      // 支出预算
      expenseBudget: {
        amount: '',// 预算总额
        accuExpenditure: '',// 已累计支出
        budgetBalance: '',//预算结余
        money: '',// 本次金额
        balanceAmount: '',// 结余金额
      },
      
      // 附件
      attachmentList: [],
    };
  }
  
  activated() {
    if (Object.keys(this.$route.params).length > 0) {
      if (this.$route.params.businessId) {
        this.initData();
        this.getRecordDetail(this.$route.params.businessId);
        this.$refs.approval.restData();
      }
    }
  }

  // 初始化新建数据
  initData() {
    this.baseInfo = Object.assign(this.baseInfo, this.getBaseInfo());
  }
  getRecordDetail(businessId) {
    // this.$API.apiCheckFinalDetail({businessId}).then((res) => {
    //   this.baseInfo = Object.assign(this.baseInfo, this.getBaseInfo(), res.data)
    //   this.baseInfo.expenseItems.projectAbstract = res.data.projectAbstract
    //   this.baseInfo.expenseItems.directoryAndUnit = res.data.directoryAndUnit
    //   !res.data.attachmentList && (this.baseInfo.attachmentList = [])
    // })
    this.baseInfo = {
      serialNumber: 111, // 单据编号
      createUser: '222', // 创建人
      createUserId: 333, // 结题申报人ID
      createTime: new Date(), // 创建时间
      projectName: '项目名称', // 项目名称
      applyUserName: '项目负责人', // 项目负责人
      month: '12', // 所属月份
      opinion: '研发项目课题组申报意见', // 研发项目课题组申报意见
      // 支出事项
      expenseItems: {
        firstSubject: '一级科目', // 一级科目
        secondarySubject: '二级科目', // 二级科目
        expenditureBasis: '支出依据', // 支出依据
      },
      // 支出预算
      expenseBudget: {
        amount: '12.22',// 预算总额
        accuExpenditure: '22.22',// 已累计支出
        budgetBalance: '33.33',//预算结余
        money: '44.44',// 本次金额
        balanceAmount: '55.55',// 结余金额
      },
      
      // 附件
      attachmentList: [],
    }
  }
  // 审批回退 (拒绝)
  reject(loadingBtn) {
    // 审批意见校验拒绝
    this.$refs.approval.isCheckComplete().then(remark => {
      this.loadingBtn = loadingBtn;
      var params = {
        menuCode: this.MENU_CODE_LIST.expenseList,
        noted: remark,
        processInstId: this.baseInfo.processInstId
      };
      // this.$API
      //   .apiRejectMaterialRegister(params)
      //   .then(res => {
      //     this.$message({
      //       type: "success",
      //       message: "审批成功"
      //     });
      //     this.backBtn();
      //     this.loadingBtn = 0;
      //   })
      //   .catch(err => {
      //     this.loadingBtn = 0;
      //   });
    });
  }
  // 审批同意
  resolve(loadingBtn) {
    // 审批意见校验通过
    this.$refs.approval.isCheckComplete().then(remark => {
      this.loadingBtn = loadingBtn;
      var params = {
        menuCode: this.MENU_CODE_LIST.expenseList,
        noted: remark,
        processInstId: this.baseInfo.processInstId
      };
      // this.$API
      //   .apiApproveMaterialRegister(params)
      //   .then(res => {
      //     this.$message({
      //       type: "success",
      //       message: "审批成功"
      //     });
      //     this.backBtn();
      //     this.loadingBtn = 0;
      //   })
      //   .catch(err => {
      //     this.loadingBtn = 0;
      //   });
    });
  }
  // 返回按钮
  backBtn() {
    this.$store.commit("DELETE_TAB", this.$route.path);
    this.$router.push({ name: "expenseList" });
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
