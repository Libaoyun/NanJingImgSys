<template>
  <div class="page">
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
          <el-form-item label="项目名称" prop="projectName">
            <el-input
              v-model="baseInfo.projectName"
              placeholder="点击选择审批完成的研发项目"
              readonly
              @click.native="chooseProject"
            ></el-input>
          </el-form-item>
          <el-form-item label="项目负责人" prop="applyUserName">
            <el-input v-model="baseInfo.applyUserName" placeholder="自动带入" disabled></el-input>
          </el-form-item>
          <el-form-item label="所属月份" prop="belongingMonth">
            <el-date-picker v-model="baseInfo.belongingMonth" value-format="yyyy-MM" type="month" placeholder="选择或自动带入"></el-date-picker>
          </el-form-item>
        </el-form>
      </div>
    </card-global>

    <card-global cardTitle="支出事项">
      <el-form
        ref="expenseItemsForm"
        :inline="true"
        :rules="expenseItemsRules"
        :model="baseInfo.expenseItems"
        size="small"
        label-position="right"
        label-width="100px"
        label-suffix=":"
      >
        <el-form-item label="一级科目" prop="firstSubjectCode">
          <el-select v-model="baseInfo.expenseItems.firstSubjectCode" placeholder="请选择" @change="handleChangeFirstSubject">
            <el-option
              v-for="item in firstSubjectList"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="二级科目" prop="secondarySubjectCode">
          <el-select v-model="baseInfo.expenseItems.secondarySubjectCode" placeholder="请选择" @change="handleChangeSecondarySubject">
            <el-option
              v-for="item in secondarySubjectList"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <div class="col2">
          <el-form-item label="支出依据" prop="payNoted">
            <el-input
              type="textarea"
              v-model="baseInfo.expenseItems.payNoted"
              maxlength="200" rows="5" resize="none"
            ></el-input>
          </el-form-item>
        </div>
      </el-form>
    </card-global>

    <card-global cardTitle="支出预算">
      <el-form
        ref="expenseBudgetForm"
        :inline="true"
        :rules="expenseBudgetRules"
        :model="baseInfo.expenseBudget"
        size="small"
        label-position="right"
        label-width="100px"
        label-suffix=":"
      >
        <el-form-item label="预算总额" prop="budgetAmount">
          <el-input
            type="text"
            v-model="baseInfo.expenseBudget.budgetAmount"
            disabled
            placeholder="自动带出"
          ></el-input>
        </el-form-item>
        <el-form-item label="已累计支出" prop="accumulatedExpenditure">
          <el-input
            type="text"
            v-model="baseInfo.expenseBudget.accumulatedExpenditure"
            disabled
            placeholder="自动带出"
          ></el-input>
        </el-form-item>
        <el-form-item label="预算结余" prop="budgetBalance">
          <el-input
            type="text"
            v-model="baseInfo.expenseBudget.budgetBalance"
            disabled
            placeholder="自动带入"
          ></el-input>
        </el-form-item>
        <el-form-item label="本次金额(元)" prop="amount">
          <el-input
            type="text"
            v-model="baseInfo.expenseBudget.amount"
            :validate-event='true'
            @change="handleCalculate"
            placeholder="请输入本次申请列销金额"
          ></el-input>
        </el-form-item>
        <el-form-item label="结余金额(元)" prop="balanceAmount">
          <el-input
            type="text"
            v-model="baseInfo.expenseBudget.balanceAmount"
            disabled
            placeholder="自动计算"
          ></el-input>
        </el-form-item>
      </el-form>
    </card-global>

     <card-global cardTitle="研发项目课题组申报意见">
      <el-form
        ref="opinionForm"
        :inline="true"
        :rules="baseFormRules"
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
            ></el-input>
          </el-form-item>
        </div>
      </el-form>
    </card-global>

    
    <!-- 附件版块 -->
    <upload-attachment ref="uploadAttachment" :fileList="baseInfo.attachmentList" :menuCode="MENU_CODE_LIST.expenseList"></upload-attachment>
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
import { Component, Mixins, Vue, Watch } from "vue-property-decorator";
import tableMixin from "@/mixins/tableMixin";
import dictionaryMixin from '@/mixins/dictionaryMixin'
import { checkForm } from "@/utils/index";
import $alert from '../alert'
@Component({
  name: "edit",
  components: {},
})
export default class extends Mixins(tableMixin,dictionaryMixin) {
  loadingBtn = 0;
  baseInfo = this.getBaseInfo();
  // 基本信息表单校验规则
  baseFormRules = {
    projectName: [
      { required: true, message: "点击选择审批完成的研发项目", trigger: "change" }
    ],
    belongingMonth: [
      { required: true, message: "请选择所属月份", trigger: "change" }
    ],
    remark: [
      { required: true, message: "请输入", trigger: "change" }
    ]
  }
  // 支出事项表单检验规则
  expenseItemsRules = {
    firstSubjectCode: [
      { required: true, message: "请选择", trigger: "change" }
    ],
    secondarySubjectCode: [
      { required: true, message: "请选择", trigger: "change" }
    ],
    payNoted: [
      { required: true, message: "请输入", trigger: "change" }
    ]
  }
  // 支出预算表单检验规则
  expenseBudgetRules = {
    budgetAmount: [
      { required: true, message: "请输入", trigger: "change" }
    ],
    accumulatedExpenditure: [
      { required: true, message: "请输入", trigger: "change" }
    ],
    budgetBalance: [
      { required: true, message: "请输入", trigger: "change" }
    ],
    amount: [
      { required: true, message: "请输入", trigger: "change" },
      { pattern: /^[1-9]\d*\.?\d{0,2}$/, message: '请输入正确的格式', trigger: "change"}
    ],
    balanceAmount: [
      { required: true, message: "请输入", trigger: "change" }
    ]
  }


  // 设置空数据
  getBaseInfo() {
    return {
      createUser: this.$store.getters.userInfo?.userName, // 创建人
      createUserId: this.$store.getters.userInfo?.userCode, // 结题申报人ID
      createTime: new Date(), // 创建时间
      projectName: '', // 项目名称
      applyUserName: '', // 项目负责人
      belongingMonth: '', // 所属月份
      remark: '', // 研发项目课题组申报意见
      projectApplyMainId: '',
      // 支出事项
      expenseItems: {
        firstSubject: '', // 一级科目
        firstSubjectCode: '',
        secondarySubject: '', // 二级科目
        secondarySubjectCode: '',
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
  created () {
    // 获取一级科目
    this.getFirstSubjectList()
  }
  activated() {
    if (Object.keys(this.$route.params).length > 0) {
      if (this.$route.params.businessId) {
        this.initData(this.$route.params.businessId);
      }
    }
  }

  // 初始化新建数据
  initData(businessId) {
    this.$refs.baseForm?.resetFields();
    this.$refs.expenseItemsForm?.resetFields();
    this.$refs.expenseBudgetForm?.resetFields();
    this.$refs.opinionForm?.resetFields();
    this.$API.apiExpenseDetail({businessId}).then((res) => {
      this.baseInfo = Object.assign(this.baseInfo, this.getBaseInfo(), res.data)
      // 支出事项
      this.baseInfo.expenseItems.firstSubject = res.data.firstSubject
      this.baseInfo.expenseItems.firstSubjectCode = res.data.firstSubjectCode
      this.baseInfo.expenseItems.secondarySubject = res.data.secondarySubject
      this.baseInfo.expenseItems.secondarySubjectCode = res.data.secondarySubjectCode
      this.baseInfo.expenseItems.payNoted = res.data.payNoted
      // 支出预算
      this.baseInfo.expenseBudget.budgetAmount = res.data.budgetAmount
      this.baseInfo.expenseBudget.accumulatedExpenditure = res.data.accumulatedExpenditure
      this.baseInfo.expenseBudget.budgetBalance = res.data.budgetBalance
      this.baseInfo.expenseBudget.amount = res.data.amount
      this.baseInfo.expenseBudget.balanceAmount = res.data.balanceAmount

      // 获取二级科目
      this.getSecondarySubjectList(this.baseInfo.firstSubjectCode)
    })
  }
  // 格式化保存提交的数据
  formatSendData(data) {
    data = JSON.parse(JSON.stringify(data))
    
    data.creatorOrgId = this.$store.getters.currentOrganization.organizationId
    data.creatorOrgName = this.$store.getters.currentOrganization.organizationName
    data.menuCode = this.MENU_CODE_LIST.expenseList
    // 支出事项
    data.firstSubject = data.expenseItems.firstSubject 
    data.firstSubjectCode = data.expenseItems.firstSubjectCode
    data.secondarySubject = data.expenseItems.secondarySubject 
    data.secondarySubjectCode = data.expenseItems.secondarySubjectCode
    data.payNoted = data.expenseItems.payNoted 
    delete data.expenseItems
    // 支出预算
    data.budgetAmount = data.expenseBudget.budgetAmount 
    data.accumulatedExpenditure = data.expenseBudget.accumulatedExpenditure 
    data.budgetBalance = data.expenseBudget.budgetBalance 
    data.amount = data.expenseBudget.amount 
    data.balanceAmount = data.expenseBudget.balanceAmount 
    delete data.expenseBudget

    data.attachmentList = this.$refs.uploadAttachment.getFileList();
    return data
  }
  // 保存按钮
  saveBtn(loadIndex) {
    const _self = this;
    const params = this.formatSendData(this.baseInfo)
    this.loadingBtn = loadIndex;
    this.$API
      .apiExpenseSaveOrUpdate(params)
      .then((res) => {
        this.loadingBtn = 0;
        this.$message({
          type: "success",
          message: "保存成功!",
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
    let formArr = ["baseForm", 'expenseItemsForm', 'expenseBudgetForm'];
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
          .apiExpenseSubmit(params)
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
  // 选择一级科目
  handleChangeFirstSubject(value) {
    this.baseInfo.expenseItems.secondarySubject = ''
    this.baseInfo.expenseItems.secondarySubjectCode = ''
    this.baseInfo.expenseItems.payNoted = ''

    this.baseInfo.expenseItems.firstSubject = this.GET_DICTIONARY_TEXT(this.firstSubjectList,value)
    // 根据一级科目获取二级科目
    this.getSecondarySubjectList(value)
  }
  // 选择二级科目查询预算
  handleChangeSecondarySubject(value) {
    this.baseInfo.expenseItems.secondarySubject = this.GET_DICTIONARY_TEXT(this.secondarySubjectList,value)
    // 支出依据 = 自动带入‘项目名称’研发项目+‘二级科目’支出
    this.$set(this.baseInfo.expenseItems, 'payNoted', `${this.baseInfo.expenseItems.firstSubject}研发项目${this.baseInfo.expenseItems.secondarySubject}支出`)
    this.getExpenseBudget()
  }
  // 根据一级科目&二级科目&项目ID查询相关费用
  getExpenseBudget() {
    const { secondarySubjectCode } = this.baseInfo.expenseItems
    const { projectApplyMainId } = this.baseInfo
    if(!secondarySubjectCode || !projectApplyMainId) return
    const params = {
      creatorOrgId: this.$store.getters.currentOrganization.organizationId, // 编制单位ID
      projectApplyMainId: projectApplyMainId,
      secondarySubjectCode: secondarySubjectCode
    }
    this.$API.apiGetExpenseBudget(params).then((res) => {
      this.$set(this.baseInfo.expenseBudget, 'budgetAmount', res.data.budgetAmount||0)
      this.$set(this.baseInfo.expenseBudget, 'accumulatedExpenditure', res.data.accumulatedExpenditure||0)
      const { budgetAmount, accumulatedExpenditure } = this.baseInfo.expenseBudget
      const budgetBalance = (Number(budgetAmount) - Number(accumulatedExpenditure)).toFixed(2)
      this.$set(this.baseInfo.expenseBudget, 'budgetBalance', budgetBalance)
    })
  }
  // 根据输入的本次金额 计算结余金额： 结余金额 = 预算结余-本次金额
  handleCalculate(value) {
    const { budgetBalance } = this.baseInfo.expenseBudget
    const balanceAmount = (Number(budgetBalance)-Number(value)).toFixed(2)
    this.$set(this.baseInfo.expenseBudget, 'balanceAmount', balanceAmount)
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
    this.$router.push({ name: "expenseList", params: { refresh: bool } });
  }
  // 选择项目名称
  chooseProject () {
    $alert.alertProjects().then(data => {
      this.$set(this.baseInfo, "projectName", data.projectName); // 项目名称
      this.$set(this.baseInfo, "applyUserName", data.applyUserName); // 项目负责人
      this.$set(this.baseInfo, 'applyUserId', data.applyUserId); // 项目负责人ID 
      this.$set(this.baseInfo, 'projectApplyMainId', data.businessId) // 项目ID
      this.$set(this.baseInfo, 'startYear', data.startYear)
      this.getExpenseBudget()
    });
  }
}
</script>

<style lang="scss" scoped>
.page {
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
