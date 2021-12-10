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
          <el-form-item label="联系电话" prop="telephone">
            <el-input v-model="baseInfo.telephone" placeholder="自动带入" disabled></el-input>
          </el-form-item>
          <el-form-item label="变更类型" prop="changeTypeCode">
            <el-select v-model="baseInfo.changeTypeCode" placeholder="请选择" @change="handleChangeType">
              <!-- <el-option label="周期变更" value="1"></el-option>
              <el-option label="预算变更" value="2"></el-option>
              <el-option label="人员变更" value="3"></el-option> -->
              <el-option v-for="item in changeTypeList" :label="item.label" :value="item.value" :key="item.value"/>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </card-global>

    <card-global cardTitle="变更说明">
      <el-form
        ref="changeForm"
        :inline="true"
        :rules="changeFormRules"
        :model="baseInfo.changeInfo"
        size="mini"
        label-position="left"
        label-suffix=":"
      >
        <el-form-item label="要求变更的原项目相关部分内容" prop="partContent" class="large">
          <el-input type="textarea" v-model="baseInfo.changeInfo.partContent" minlength="200" rows="5" resize="none"></el-input>
        </el-form-item>
        <el-form-item label="要求变更的内容或建议" prop="changeAdvise" class="large">
          <el-input type="textarea" v-model="baseInfo.changeInfo.changeAdvise" minlength="200" rows="5" resize="none"></el-input>
        </el-form-item>
        <el-form-item label="变更理由" prop="changeReason" class="large">
          <el-input type="textarea" v-model="baseInfo.changeInfo.changeReason" minlength="200" rows="5" resize="none"></el-input>
        </el-form-item>
        <el-form-item label="项目实施情况" prop="implementation" class="large">
          <el-input type="textarea" v-model="baseInfo.changeInfo.implementation" minlength="200" rows="5" resize="none"></el-input>
        </el-form-item>
        <el-form-item label="经费使用情况" prop="fundsUse" class="large">
          <el-input type="textarea" v-model="baseInfo.changeInfo.fundsUse" minlength="200" rows="5" resize="none"></el-input>
        </el-form-item>
      </el-form>
    </card-global>

    <card-global cardTitle="变更明细" type="table">
      <!-- 周期变更 -->
      <div key="1" v-if="baseInfo.changeTypeCode === '1'">
        <el-form
          ref="cycleForm"
          :inline="true"
          :rules="cycleFormRules"
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
              <template slot-scope="scope">
                <el-form-item
                  v-if="scope.$index===1"
                  style="width: 100%"
                  :prop="'cycleList.'+scope.$index+'.startYear'"
                  :rules="cycleFormRules['startYear']"
                  :ref="'startYear'+scope.$index"
                >
                  <el-date-picker
                    v-model="scope.row.startYear"
                    value-format="yyyy-MM-dd"
                    type="date"
                    placeholder="请选择开始年度"
                  ></el-date-picker>
                </el-form-item>
                <span v-else>{{ scope.row.startYear }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="endYear" label="结束年度" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-form-item
                  v-if="scope.$index===1"
                  style="width: 100%"
                  :prop="'cycleList.'+scope.$index+'.endYear'"
                  :rules="cycleFormRules['endYear']"
                  :ref="'endYear'+scope.$index"
                >
                  <el-date-picker
                    v-model="scope.row.endYear"
                    value-format="yyyy-MM-dd"
                    type="date"
                    placeholder="请选择结束年度"
                  ></el-date-picker>
                </el-form-item>
                <span v-else>{{ scope.row.endYear }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="cycleUpdateUser" label="变更人" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="cycleUpdateTime" label="变更时间" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <span>{{ scope.row.cycleUpdateTime | formatTime('yyyy-MM-dd')}}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-form>
      </div>
      <!-- 预算变更 -->
      <div key="2" v-if="baseInfo.changeTypeCode === '2'">
        <el-form 
          ref="budgetForm" 
          :inline="true" 
          :rules="budgetFormRules" 
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
                  <template slot-scope="scope">
                    <el-form-item v-if="scope.$index<=6" :prop="'budgetDetailList.'+scope.$index+'.sourceBudget'" :rules="budgetFormRules['sourceBudget']">
                      <el-input-number :controls="false" :precision="2" v-model="scope.row.sourceBudget" @change="getSummary(scope, 'source')"></el-input-number>
                    </el-form-item>
                  </template>
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
                  <template slot-scope="scope">
                    <el-form-item :prop="'budgetDetailList.'+scope.$index+'.expenseBudget'" :rules="budgetFormRules['expenseBudget']">
                      <el-input-number :controls="false" :precision="2" v-model="scope.row.expenseBudget" @change="getSummary(scope, 'expense')"></el-input-number>
                    </el-form-item>
                  </template>
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
          :rules="userFormRules"
          :model="baseInfo.detailForm"
          size="mini"
          label-position="right"
          :show-message="false"
        >
          <el-table
            :data="baseInfo.detailForm.userInfoList"
            :border="tableConfig.border"
            class="global-table-default"
            style="width: 100%;">
            <el-table-column label="序号" type="index" width="50" align="center">
              <template slot-scope="scope">
                <span>{{scope.$index + 1}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="userName" label="姓名" width="100" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-form-item v-if="scope.row.isOriginal==='1'" style="width: 100%" :prop="'userInfoList.'+scope.$index+'.userName'" :rules="userFormRules['userName']">
                  <el-input v-model="scope.row.userName" type="text" placeholder="姓名"></el-input>
                </el-form-item>
                <span v-else>{{scope.row.userName}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="idCard" label="身份证号码" width="140" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-form-item v-if="scope.row.isOriginal==='1'" style="width: 100%" :prop="'userInfoList.'+scope.$index+'.idCard'" :rules="userFormRules['idCard']">
                  <el-input v-model="scope.row.idCard" type="text" placeholder="身份证号码"></el-input>
                </el-form-item>
                <span v-else>{{scope.row.idCard}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="age" label="年龄" width="80" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-form-item v-if="scope.row.isOriginal==='1'" style="width: 100%" :prop="'userInfoList.'+scope.$index+'.age'" :rules="userFormRules['age']">
                  <el-input v-model="scope.row.age" type="text" placeholder="年龄"></el-input>
                </el-form-item>
                <span v-else>{{scope.row.age}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="gender" label="性别" width="120" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-form-item v-if="scope.row.isOriginal==='1'" style="width: 100%" :prop="'userInfoList.'+scope.$index+'.gender'" :rules="userFormRules['gender']">
                  <el-select v-model="scope.row.gender" placeholder="请选择">
                    <el-option label="男" value="男"></el-option>
                    <el-option label="女" value="女"></el-option>
                  </el-select>
                </el-form-item>
                <span v-else>{{scope.row.gender}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="education" label="学历" width="80" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-form-item v-if="scope.row.isOriginal==='1'" style="width: 100%" :prop="'userInfoList.'+scope.$index+'.education'" :rules="userFormRules['education']">
                  <el-input v-model="scope.row.education" type="text" placeholder="学历"></el-input>
                </el-form-item>
                <span v-else>{{scope.row.education}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="belongDepartment" label="所属部门" width="120" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-form-item v-if="scope.row.isOriginal==='1'" style="width: 100%" :prop="'userInfoList.'+scope.$index+'.belongDepartment'" :rules="userFormRules['belongDepartment']">
                  <el-input v-model="scope.row.belongDepartment" type="text" placeholder="所属部门"></el-input>
                </el-form-item>
                <span v-else>{{scope.row.belongDepartment}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="belongPost" label="职务职称" width="120" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-form-item v-if="scope.row.isOriginal==='1'" style="width: 100%" :prop="'userInfoList.'+scope.$index+'.belongPost'" :rules="userFormRules['belongPost']">
                  <el-input v-model="scope.row.belongPost" type="text" placeholder="职务职称"></el-input>
                </el-form-item>
                <span v-else>{{scope.row.belongPost}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="majorStudied" label="所学专业" width="120" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-form-item v-if="scope.row.isOriginal==='1'" style="width: 100%" :prop="'userInfoList.'+scope.$index+'.major'" :rules="userFormRules['major']">
                  <el-input v-model="scope.row.majorStudied" type="text" placeholder="所学专业"></el-input>
                </el-form-item>
                <span v-else>{{scope.row.majorStudied}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="majorWorked" label="现从事专业" width="120" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-form-item v-if="scope.row.isOriginal==='1'" style="width: 100%" :prop="'userInfoList.'+scope.$index+'.majorWorked'" :rules="userFormRules['majorWorked']">
                  <el-input v-model="scope.row.majorWorked" type="text" placeholder="现从事专业"></el-input>
                </el-form-item>
                <span v-else>{{scope.row.majorWorked}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="belongUnit" label="所在单位" width="120" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-form-item v-if="scope.row.isOriginal==='1'" style="width: 100%" :prop="'userInfoList.'+scope.$index+'.belongUnit'" :rules="userFormRules['belongUnit']">
                  <el-input v-model="scope.row.belongUnit" type="text" placeholder="所在单位"></el-input>
                </el-form-item>
                <span v-else>{{scope.row.belongUnit}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="taskDivision" label="研究任务及分工" width="140" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-form-item v-if="scope.row.isOriginal==='1'" style="width: 100%" :prop="'userInfoList.'+scope.$index+'.taskDivision'" :rules="userFormRules['taskDivision']">
                  <el-input v-model="scope.row.taskDivision" type="text" placeholder="研究任务及分工"></el-input>
                </el-form-item>
                <span v-else>{{scope.row.taskDivision}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="workRate" label="全时率" width="120" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-form-item v-if="scope.row.isOriginal==='1'" style="width: 100%" :prop="'userInfoList.'+scope.$index+'.workRate'" :rules="userFormRules['workRate']">
                  <el-input v-model="scope.row.workRate" type="text" placeholder="全时率"></el-input>
                </el-form-item>
                <span v-else>{{scope.row.workRate}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="telephone" label="联系电话" width="120" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-form-item v-if="scope.row.isOriginal==='1'" style="width: 100%" :prop="'userInfoList.'+scope.$index+'.telephone'" :rules="userFormRules['telephone']">
                  <el-input v-model="scope.row.telephone" type="text" placeholder="联系电话"></el-input>
                </el-form-item>
                <span v-else>{{scope.row.telephone}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="startDate" label="参与研发开始日期" width="170" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-form-item
                  style="width: 100%"
                  :prop="'userInfoList.'+scope.$index+'.startDate'"
                  :rules="userFormRules['startDate']"
                  :ref="'startDate'+scope.$index"
                >
                  <el-date-picker
                    v-model="scope.row.startDate"
                    value-format="yyyy-MM-dd"
                    type="date"
                    placeholder="参与研发开始日期"
                  ></el-date-picker>
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column prop="endDate" label="参与研发结束日期" width="170" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-form-item
                  style="width: 100%"
                  :prop="'userInfoList.'+scope.$index+'.endDate'"
                  :rules="userFormRules['endDate']"
                  :ref="'endDate'+scope.$index"
                >
                  <el-date-picker
                    v-model="scope.row.endDate"
                    value-format="yyyy-MM-dd"
                    type="date"
                    placeholder="参与研发结束日期"
                  ></el-date-picker>
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column prop="userStatus" label="状态" width="100" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-form-item
                  style="width: 100%"
                  :prop="'userInfoList.'+scope.$index+'.userStatus'"
                  :rules="userFormRules['userStatus']"
                >
                  <el-select v-model="scope.row.userStatus" placeholder="请选择">
                    <el-option label="正常" value="1"></el-option>
                    <el-option label="调离" value="2"></el-option>
                  </el-select>
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column prop="creatorUser" label="编制人" width="100" align="center" :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column prop="createTime" label="编制时间" width="180" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <span>{{scope.row.createTime | formatTime('yyyy-MM-dd')}}</span>
              </template>
            </el-table-column>
            <el-table-column fixed="right" label="操作" width="50">
              <template slot-scope="scope">
                <el-popconfirm title="是否要删除此行？" @confirm="handleDeleteRow(scope)" placement="top" cancelButtonType="plain">
                  <i v-if="scope.row.isOriginal==='1'" slot="reference" class="el-icon-delete delete-detail-btn"></i>
                </el-popconfirm>
              </template>
            </el-table-column>
          </el-table>
        </el-form>
        <el-button size="mini" icon="el-icon-plus" class="table-add-btn" @click="handleAddUser('userForm')">新增人员</el-button>
      </div>
    </card-global>
    <!-- 附件版块 -->
    <upload-attachment ref="uploadAttachment" :fileList="baseInfo.attachmentList" :menuCode="MENU_CODE_LIST.changeList"></upload-attachment>
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
  validateTime = (rule, value, callback)=>{
    // 尽量少用
    let validateIndex = rule.field.split('.')[1]
    const changeTypeCode = this.baseInfo.changeTypeCode
    let startDate, endDate;
    if(changeTypeCode === '1') {
      startDate = this.baseInfo.detailForm.cycleList[validateIndex].startYear
      endDate = this.baseInfo.detailForm.cycleList[validateIndex].endYear
    } else if(changeTypeCode === '3') {
      startDate = this.baseInfo.detailForm.userInfoList[validateIndex].startDate
      endDate = this.baseInfo.detailForm.userInfoList[validateIndex].endDate
    }
    if(!startDate || !endDate){
      callback();
    }else{
      var startTime = new Date(startDate);
      var endTime = new Date(endDate);
      if(startTime > endTime){
          callback(new Error())
      }else{
        // 清除日期校验
        if(changeTypeCode === '1') {
          this.$refs[`startYear${validateIndex}`].clearValidate()
          this.$refs[`endYear${validateIndex}`].clearValidate()
        }else if(changeTypeCode === '3') {
          this.$refs[`startDate${validateIndex}`].clearValidate()
          this.$refs[`endDate${validateIndex}`].clearValidate()
        }
        callback();
      }
    }
  }
  loadingBtn = 0;
  baseInfo = this.getBaseInfo();
  changeTypeList = [
    {label: '周期变更', value: '1'},
    {label: '预算变更', value: '2'},
    {label: '人员变更', value: '3'},
  ]
  // 基本信息  表单校验规则
  baseFormRules = {
    projectName: [
      { required: true, message: "点击选择审批完成的研发项目", trigger: "change" }
    ],
    changeTypeCode: [
      { required: true, message: "请选择变更类型", trigger: "change" }
    ]
  }
  // 变更说明  表单校验规则
  changeFormRules = {
    partContent: [
      { required: true, message: "请输入", trigger: "change" }
    ],
    changeAdvise: [
      { required: true, message: "请输入", trigger: "change" }
    ],
    changeReason: [
      { required: true, message: "请输入", trigger: "change" }
    ],
    implementation: [
      { required: true, message: "请输入", trigger: "change" }
    ],
    fundsUse: [
      { required: true, message: "请输入", trigger: "change" }
    ],
  }
  // 周期变更  表单校验规则
  cycleFormRules = {
    startYear: [
      { required: true, message: "请选择开始年度", trigger: "change" },
      { validator: this.validateTime, trigger: 'change'}
    ],
    endYear: [
      { required: true, message: "请选择结束年度", trigger: "change" },
      { validator: this.validateTime, trigger: 'change'}
    ],
  }
  // 预算变更  表单校验规则
  budgetFormRules = {
    sourceBudget: [
      { required: true, message: '', trigger: 'change' }
    ],
    expenseBudget: [
      { required: true, message: '', trigger: 'change' }
    ],
  }
  // 人员变更表单  表单校验规则
  userFormRules = {
    userName: [
      { required: true, message: "请输入姓名", trigger: "blur" }
    ],
    idCard: [
      { required: true, pattern:/^[1-9]\d{5}(18|19|20|(3\d))\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/,message: "请输入身份证号码", trigger: "blur" }
    ],
    age: [
      { required: true, message: "年龄", trigger: "change" }
    ],
    gender: [
      { required: true, message: "性别", trigger: "change" }
    ],
    education: [
      { required: true, message: "学历", trigger: "change" }
    ],
    belongDepartment: [
      { required: true, message: "所属部门", trigger: "change" }
    ],
    belongPost: [
      { required: true, message: "职务职称", trigger: "change" }
    ],
    majorStudied: [
      { required: true, message: "所学专业", trigger: "change" }
    ],
    majorWorked: [
      { required: true, message: "现从事专业", trigger: "change" }
    ],
    belongUnit: [
      { required: true, message: "所在单位", trigger: "change" }
    ],
    taskDivision: [
      { required: true, message: "研究任务及分工", trigger: "change" }
    ],
    workRate: [
      { required: true, message: "全时率", trigger: "change" }
    ],
    telephone: [
      {required:true,message:'请输入联系电话',trigger:'change'},
      {pattern: /^1[3456789]\d{9}$/, message: "请输入正确联系电话",trigger: "blur"},
    ],
    startDate: [
      { required: true, message: "参与研发开始日期", trigger: "change" },
      { validator: this.validateTime, trigger: 'change'}
    ],
    endDate: [
      { required: true, message: "参与研发结束日期", trigger: "change" },
      { validator: this.validateTime, trigger: 'change'}
    ],
    userStatus: [
      { required: true, message: "状态", trigger: "change" }
    ],
    creatorUser: [
      { required: true, message: "编制人", trigger: "change" }
    ],
    createTime: [
      { required: true, message: "编制时间", trigger: "change" }
    ],
  }
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
            cycleUpdateUser: this.$store.getters.userInfo?.userName,
            cycleUpdateUserId: this.$store.getters.userInfo?.userCode,
            cycleUpdateTime: new Date(),
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
    if (Object.keys(this.$route.params).length > 0) {
      if (this.$route.params.businessId) {
        this.initData(this.$route.params.businessId);
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

    })
  }
  // 格式化保存提交的数据
  formatSendData(data) {
    data = JSON.parse(JSON.stringify(data))
    
    data.creatorOrgId = this.$store.getters.currentOrganization.organizationId
    data.creatorOrgName = this.$store.getters.currentOrganization.organizationName
    data.menuCode = this.MENU_CODE_LIST.changeList

    data.changeType = this.changeTypeList.find(it=>it.value === data.changeTypeCode).label
    // 变更说明
    data.partContent = data.changeInfo.partContent
    data.changeAdvise = data.changeInfo.changeAdvise
    data.changeReason = data.changeInfo.changeReason
    data.implementation = data.changeInfo.implementation
    data.fundsUse = data.changeInfo.fundsUse
    delete data.changeInfo
    // 
    delete data.projectDataInfo

    if (data.changeTypeCode === '1') {
      // 周期变更
      data.cycleList = data.detailForm.cycleList
    } else if (data.changeTypeCode === '2') {
      // 预算变更
      data.budgetDetailList = data.detailForm.budgetDetailList
    } else if (data.changeTypeCode === '3') {
      // 人员变更
      data.userInfoList = data.detailForm.userInfoList
      // 最后一行人员信息姓名为空时，删除该行
      const lastRecord = data.userInfoList[data.userInfoList.length-1]
      if(!lastRecord.userName) data.userInfoList.splice(data.userInfoList.length-1, 1)
    }
    delete data.detailForm

    data.attachmentList = this.$refs.uploadAttachment.getFileList();
    return data
  }
  // 保存按钮
  saveBtn(loadIndex) {
    const params = this.formatSendData(this.baseInfo)
    this.loadingBtn = loadIndex;
    this.$API
      .apiChangeSaveOrUpdate(params)
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
    let formArr = ["baseForm", 'changeForm'];
    const { changeTypeCode } = this.baseInfo
    if (changeTypeCode === '1') formArr.push('cycleForm')
    else if (changeTypeCode === '2') formArr.push('budgetForm')
    else if (changeTypeCode === '3') formArr.push('userForm')
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
          .apiChangeSubmit(params)
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
    this.$router.push({ name: "changeList", params: { refresh: bool } });
  }
  // 选择项目名称
  chooseProject () {
    $alert.alertProjects().then(data => {
      this.$set(this.baseInfo, "projectName", data.projectName); // 项目名称
      this.$set(this.baseInfo, "applyUserName", data.applyUserName); // 项目负责人
      this.$set(this.baseInfo, 'applyUserId', data.applyUserId); // 项目负责人ID 
      this.$set(this.baseInfo, 'projectApplyMainId', data.businessId) // 项目ID
      this.$set(this.baseInfo, 'telephone', data.telephone) // 项目ID
      this.$set(this.baseInfo, 'projectDataInfo', data)
      this.getChangeDetail()
    });
  }
  // 回填周期变更数据
  getCycleFormData() {
    this.baseInfo.detailForm.cycleList = [
      {
        dataStatus: '0',
        startYear: this.baseInfo.projectDataInfo.startYear,
        endYear: this.baseInfo.projectDataInfo.endYear,
        cycleUpdateUser: this.baseInfo.projectDataInfo.cycleUpdateUser,
        cycleUpdateUserId: this.baseInfo.projectDataInfo.cycleUpdateUserId,
        cycleUpdateTime: this.baseInfo.projectDataInfo.cycleUpdateTime,
        editable: 0
      },
      {
        dataStatus: '1',
        startYear: this.baseInfo.projectDataInfo.startYear,
        endYear: this.baseInfo.projectDataInfo.endYear,
        cycleUpdateUser: this.$store.getters.userInfo?.userName,
        cycleUpdateUserId: this.$store.getters.userInfo?.userCode,
        cycleUpdateTime: new Date(),
        editable: 1
      }
    ]
  }
  // 用户变更-添加
  handleAddUser(formName) {
    const userTmpl = {
      isOriginal: '1',
      userName: '',
      idCard: '',
      age: '',
      gender: '',
      education: '',
      belongDepartment: '',
      belongPost: '',
      majorStudied: '',
      majorWorked: '',
      belongUnit: '',
      taskDivision: '',
      workRate: '',
      telephone: '',
      startDate: '',
      endDate: '',
      userStatus: '1',
      creatorUser: this.$store.getters.userInfo?.userName,
      createTime: new Date()
    }
    this.$refs[formName].validate(valid => {
      if (valid) {
        this.baseInfo.detailForm.userInfoList.push(userTmpl)
      } else {
        return false;
      }
    });
  }
  // 用户变更-删除某一行
  handleDeleteRow(scope) {
    this.baseInfo.detailForm.userInfoList.splice(scope.$index, 1)
  }
  // 选择变更类型
  handleChangeType(value) {
    this.baseInfo.detailForm.cycleList = []
    this.baseInfo.detailForm.budgetDetailList = []
    this.baseInfo.detailForm.userInfoList = []
    // API获取变更明细
    this.getChangeDetail()
  }
  // 获取变更明细
  getChangeDetail() {
    const { projectApplyMainId } = this.baseInfo
    if(!projectApplyMainId) return 
    switch(this.baseInfo.changeTypeCode) {
      case '1': 
        this.getCycleFormData()
        break
      case '2':
        const params = {
          creatorOrgId: this.$store.getters.currentOrganization.organizationId,
          projectApplyMainId
        }
        this.$API.apiGetChangeBudget(params).then((res) => {
          this.baseInfo.detailForm.budgetDetailList = res.data || []
        })
        break
      case '3':
        this.$API.apiCheckFinalUserList({ businessId: projectApplyMainId }).then((res) => {
          this.baseInfo.detailForm.userInfoList = res.data
        })
        break
      default:
        break
    }
  }
  // 来源预算合计
  getSummary(scope, type) {
    if (scope.$index === 0) return
    
    const data = JSON.parse(JSON.stringify(this.baseInfo.detailForm.budgetDetailList))
    data.splice(0,1)

    if (type === 'source') {
      let sourceSum = 0
      data.map(it=>{
        sourceSum += Number(it.sourceBudget)
      })
      this.baseInfo.detailForm.budgetDetailList[0].sourceBudget = sourceSum.toFixed(2)
    } else if (type === 'expense') {
      let expenseSum = 0
      data.map(it=>{
        expenseSum += Number(it.expenseBudget)
      })
      this.baseInfo.detailForm.budgetDetailList[0].expenseBudget = expenseSum.toFixed(2)
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
