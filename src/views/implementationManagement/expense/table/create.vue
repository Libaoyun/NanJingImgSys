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
          <el-form-item label="所属月份" prop="postName">
            <el-date-picker v-model="baseInfo.month" value-format="M" type="month" placeholder="选择或自动带入"></el-date-picker>
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
        <el-form-item label="一级科目" prop="firstSubject">
          <el-select v-model="baseInfo.expenseItems.firstSubject" placeholder="请选择">
            <el-option
              v-for="item in firstSubjectArr"
              :key="item.value"
              :label="item.name"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="二级科目" prop="secondarySubject">
          <el-select v-model="baseInfo.expenseItems.secondarySubject" placeholder="请选择">
            <el-option
              v-for="item in secondarySubjectArr"
              :key="item.value"
              :label="item.name"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <div class="col2">
          <el-form-item label="支出依据" prop="expenditureBasis">
            <el-input
              type="textarea"
              v-model="baseInfo.expenseItems.expenditureBasis"
              maxlength="500"
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
        :rules="expenseBudgetRules"
        :model="baseInfo.expenseBudget"
        size="small"
        label-position="right"
        label-width="100px"
        label-suffix=":"
      >
        <el-form-item label="预算总额" prop="amount">
          <el-input
            type="text"
            v-model="baseInfo.expenseBudget.amount"
            disabled
            placeholder="自动带出"
          ></el-input>
        </el-form-item>
        <el-form-item label="已累计支出" prop="accuExpenditure">
          <el-input
            type="text"
            v-model="baseInfo.expenseBudget.accuExpenditure"
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
        <el-form-item label="本次金额(元)" prop="money">
          <el-input
            type="text"
            v-model="baseInfo.expenseBudget.money"
            :validate-event='true'
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
        ref="opinion"
        :inline="true"
        :rules="baseFormRules"
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
              show-word-limit
            ></el-input>
          </el-form-item>
        </div>
      </el-form>
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
import { Component, Mixins, Vue, Watch } from "vue-property-decorator";
import tableMixin from "@/mixins/tableMixin";
import dictionaryMixin from '@/mixins/dictionaryMixin'
import { checkForm } from "@/utils/index";
import $alert from '../alert'
@Component({
  name: "create",
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
    opinion: [
      { required: true, message: "请输入", trigger: "change" }
    ]
  }
  // 支出事项表单检验规则
  expenseItemsRules = {
    firstSubject: [
      { required: true, message: "请输入", trigger: "change" }
    ],
    secondarySubject: [
      { required: true, message: "请输入", trigger: "change" }
    ],
    expenditureBasis: [
      { required: true, message: "请输入", trigger: "change" }
    ]
  }
  // 支出预算表单检验规则
  expenseBudgetRules = {
    amount: [
      { required: true, message: "请输入", trigger: "change" }
    ],
    accuExpenditure: [
      { required: true, message: "请输入", trigger: "change" }
    ],
    budgetBalance: [
      { required: true, message: "请输入", trigger: "change" }
    ],
    money: [
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
  created () {
    this.firstSubjectArr = [
      {name: '人员人工费用', value: '1'},
      {name: '人员人工费用2', value: '2'}
    ]
    this.secondarySubjectArr = [
      {name: '人员人工费用11', value: '1'},
      {name: '人员人工费用22', value: '2'}
    ]
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
    this.$refs["expenseItemsForm"].resetFields();
    this.$refs["expenseBudgetForm"].resetFields();
    this.baseInfo = Object.assign(this.baseInfo, this.getBaseInfo());
  }
  // 格式化保存提交的数据
  formatSendData(data) {
    data = JSON.parse(JSON.stringify(data))
    
    data.creatorOrgId = this.$store.getters.currentOrganization.organizationId
    data.creatorOrgName = this.$store.getters.currentOrganization.organizationName
    data.menuCode = this.MENU_CODE_LIST.expenseList

    data.directoryAndUnit = data.checkInfo.directoryAndUnit // 经济技术文件目录及提供单位(长度：1024)
    data.projectAbstract = data.checkInfo.projectAbstract // 成果内容简介(长度：1024)
    delete data.checkInfo

    data.attachmentList = this.$refs.uploadApprovalGlobal.getFileList();
    return data
  }
  // 保存按钮
  saveBtn(loadIndex) {
    return
    const _self = this;
    const params = this.formatSendData(this.baseInfo)
    this.loadingBtn = loadIndex;
    this.$API
      .apiCheckFinalSaveOrUpdate(params)
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
    return
    const _self = this;
    let formArr = ["baseForm", 'expenseItemsForm', 'expenseBudgetForm'];
    let resultArr = [];
    formArr.forEach((item) => {
      //根据表单的ref校验
      resultArr.push(checkForm(_self, item));
    });
    const params = this.formatSendData(this.baseInfo)
    params.flag = 2
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
    this.$router.push({ name: "expenseList", params: { refresh: bool } });
  }
  // 选择项目名称
  chooseProject () {
    $alert.alertProjects().then(data => {
      this.$set(this.baseInfo, "projectName", data.projectName); // 项目名称
      this.$set(this.baseInfo, "applyUserName", data.applyUserName); // 项目负责人
      this.$set(this.baseInfo, 'applyUserId', data.applyUserId); // 项目负责人ID 
      this.$set(this.baseInfo, 'projectApplyMainId', data.businessId) // 项目ID
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
