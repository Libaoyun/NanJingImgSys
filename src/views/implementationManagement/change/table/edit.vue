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
          <el-form-item label="单据编号" prop="orgNumber">
            <el-input v-model="baseInfo.orgNumber" placeholder="自动生成" disabled></el-input>
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
           <el-form-item label="项目负责人" prop="projectLeader">
            <el-input v-model="baseInfo.projectLeader" placeholder="自动带入" disabled></el-input>
          </el-form-item>
          <el-form-item label="联系电话" prop="phone">
            <el-input v-model="baseInfo.phone" placeholder="自动带入" disabled></el-input>
          </el-form-item>
          <el-form-item label="变更类型" prop="changeType">
            <el-select v-model="baseInfo.changeType" placeholder="请选择" @change="handleChangeType">
              <el-option label="周期变更" value="1"></el-option>
              <el-option label="预算变更" value="2"></el-option>
              <el-option label="人员变更" value="3"></el-option>
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
        size="small"
        label-position="left"
        label-width="100%"
        label-suffix=":"
        class="check-form"
      >
        <el-form-item label="要求变更的原项目相关部分内容" prop="originalProjectCnt">
          <el-input
            type="textarea"
            v-model="baseInfo.changeInfo.originalProjectCnt"
          ></el-input>
        </el-form-item>
        <el-form-item label="要求变更的内容或建议" prop="advise">
          <el-input
            type="textarea"
            v-model="baseInfo.changeInfo.advise"
          ></el-input>
        </el-form-item>
        <el-form-item label="变更理由" prop="reason">
          <el-input
            type="textarea"
            v-model="baseInfo.changeInfo.reason"
          ></el-input>
        </el-form-item>
        <el-form-item label="项目实施情况" prop="implementationSituation">
          <el-input
            type="textarea"
            v-model="baseInfo.changeInfo.implementationSituation"
          ></el-input>
        </el-form-item>
        <el-form-item label="经费使用情况" prop="feeUse">
          <el-input
            type="textarea"
            v-model="baseInfo.changeInfo.feeUse"
          ></el-input>
        </el-form-item>
      </el-form>
    </card-global>

    <card-global cardTitle="变更明细">
      <!-- 周期变更 -->
      <template v-if="baseInfo.changeType === '1'">
        <el-form
          ref="detailForm"
          :inline="true"
          :rules="formRules"
          :model="baseInfo.detailForm"
          size="mini"
          label-position="right"
          :show-message="false"
        >
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
            <el-table-column prop="startYear" label="开始年度" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-form-item
                  v-if="scope.row.editable===1"
                  style="width: 100%"
                  :prop="'detailList.'+scope.$index+'.startYear'"
                  :rules="formRules['startYear']"
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
                  v-if="scope.row.editable===1"
                  style="width: 100%"
                  :prop="'detailList.'+scope.$index+'.endYear'"
                  :rules="formRules['endYear']"
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
            <el-table-column prop="changeUser" label="变更人" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="changeTime" label="变更时间" align="center" :show-overflow-tooltip="true"></el-table-column>
          </el-table>
        </el-form>
      </template>
      <!-- 预算变更 -->
      <template v-else-if="baseInfo.changeType === '2'">222</template>
      <!-- 人员变更 -->
      <template v-else-if="baseInfo.changeType === '3'">
        <el-form
          ref="detailForm"
          :inline="true"
          :rules="formRules"
          :model="baseInfo.detailForm"
          size="mini"
          label-position="right"
          :show-message="false"
        >
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
            <el-table-column prop="userName" label="姓名" width="100" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-form-item v-if="scope.row.isNew===1" style="width: 100%" :prop="'detailList.'+scope.$index+'.userName'" :rules="formRules['userName']">
                  <el-input v-model="scope.row.userName" type="text" placeholder="姓名"></el-input>
                </el-form-item>
                <span v-else>{{scope.row.userName}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="idcard" label="身份证号码" width="140" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-form-item v-if="scope.row.isNew===1" style="width: 100%" :prop="'detailList.'+scope.$index+'.idcard'" :rules="formRules['idcard']">
                  <el-input v-model="scope.row.idcard" type="text" placeholder="身份证号码"></el-input>
                </el-form-item>
                <span v-else>{{scope.row.idcard}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="age" label="年龄" width="80" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-form-item v-if="scope.row.isNew===1" style="width: 100%" :prop="'detailList.'+scope.$index+'.age'" :rules="formRules['age']">
                  <el-input v-model="scope.row.age" type="text" placeholder="年龄"></el-input>
                </el-form-item>
                <span v-else>{{scope.row.age}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="sex" label="性别" width="120" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-form-item v-if="scope.row.isNew===1" style="width: 100%" :prop="'detailList.'+scope.$index+'.sex'" :rules="formRules['sex']">
                  <el-select v-model="scope.row.sex" placeholder="请选择">
                    <el-option label="男" value="男"></el-option>
                    <el-option label="女" value="女"></el-option>
                  </el-select>
                </el-form-item>
                <span v-else>{{scope.row.sex}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="education" label="学历" width="80" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-form-item v-if="scope.row.isNew===1" style="width: 100%" :prop="'detailList.'+scope.$index+'.education'" :rules="formRules['education']">
                  <el-input v-model="scope.row.education" type="text" placeholder="学历"></el-input>
                </el-form-item>
                <span v-else>{{scope.row.education}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="department" label="所属部门" width="120" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-form-item v-if="scope.row.isNew===1" style="width: 100%" :prop="'detailList.'+scope.$index+'.department'" :rules="formRules['department']">
                  <el-input v-model="scope.row.department" type="text" placeholder="所属部门"></el-input>
                </el-form-item>
                <span v-else>{{scope.row.department}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="job" label="职务职称" width="120" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-form-item v-if="scope.row.isNew===1" style="width: 100%" :prop="'detailList.'+scope.$index+'.job'" :rules="formRules['job']">
                  <el-input v-model="scope.row.job" type="text" placeholder="职务职称"></el-input>
                </el-form-item>
                <span v-else>{{scope.row.job}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="major" label="所学专业" width="120" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-form-item v-if="scope.row.isNew===1" style="width: 100%" :prop="'detailList.'+scope.$index+'.major'" :rules="formRules['major']">
                  <el-input v-model="scope.row.major" type="text" placeholder="所学专业"></el-input>
                </el-form-item>
                <span v-else>{{scope.row.major}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="work" label="现从事专业" width="120" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-form-item v-if="scope.row.isNew===1" style="width: 100%" :prop="'detailList.'+scope.$index+'.work'" :rules="formRules['work']">
                  <el-input v-model="scope.row.work" type="text" placeholder="现从事专业"></el-input>
                </el-form-item>
                <span v-else>{{scope.row.work}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="company" label="所在单位" width="120" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-form-item v-if="scope.row.isNew===1" style="width: 100%" :prop="'detailList.'+scope.$index+'.company'" :rules="formRules['company']">
                  <el-input v-model="scope.row.company" type="text" placeholder="所在单位"></el-input>
                </el-form-item>
                <span v-else>{{scope.row.company}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="task" label="研究任务及分工" width="140" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-form-item v-if="scope.row.isNew===1" style="width: 100%" :prop="'detailList.'+scope.$index+'.task'" :rules="formRules['task']">
                  <el-input v-model="scope.row.task" type="text" placeholder="研究任务及分工"></el-input>
                </el-form-item>
                <span v-else>{{scope.row.task}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="fullTimeRate" label="全时率" width="120" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-form-item v-if="scope.row.isNew===1" style="width: 100%" :prop="'detailList.'+scope.$index+'.fullTimeRate'" :rules="formRules['fullTimeRate']">
                  <el-input v-model="scope.row.fullTimeRate" type="text" placeholder="全时率"></el-input>
                </el-form-item>
                <span v-else>{{scope.row.fullTimeRate}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="phone" label="联系电话" width="120" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-form-item v-if="scope.row.isNew===1" style="width: 100%" :prop="'detailList.'+scope.$index+'.phone'" :rules="formRules['phone']">
                  <el-input v-model="scope.row.phone" type="text" placeholder="联系电话"></el-input>
                </el-form-item>
                <span v-else>{{scope.row.phone}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="startDate" label="参与研发开始日期" width="170" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-form-item
                  style="width: 100%"
                  :prop="'detailList.'+scope.$index+'.startDate'"
                  :rules="formRules['startDate']"
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
                  :prop="'detailList.'+scope.$index+'.endDate'"
                  :rules="formRules['endDate']"
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
            <el-table-column prop="status" label="状态" width="100" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-form-item
                  style="width: 100%"
                  :prop="'detailList.'+scope.$index+'.status'"
                  :rules="formRules['status']"
                >
                  <el-select v-model="scope.row.status" placeholder="请选择">
                    <el-option label="正常" value="正常"></el-option>
                    <el-option label="调离" value="调离"></el-option>
                  </el-select>
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column prop="creator" label="编制人" width="100" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-form-item v-if="scope.row.isNew===1" style="width: 100%" :prop="'detailList.'+scope.$index+'.creator'" :rules="formRules['creator']">
                  <el-input v-model="scope.row.creator" type="text" placeholder="编制人"></el-input>
                </el-form-item>
                <span v-else>{{scope.row.creator}}</span>
              </template>
            </el-table-column>
            <el-table-column prop="createdTime" label="编制时间" width="180" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <el-form-item
                  v-if="scope.row.isNew===1"
                  style="width: 100%"
                  :prop="'detailList.'+scope.$index+'.createdTime'"
                  :rules="formRules['createdTime']"
                >
                  <el-date-picker
                    v-model="scope.row.createdTime"
                    value-format="yyyy-MM-dd"
                    type="date"
                    placeholder="编制时间"
                  ></el-date-picker>
                </el-form-item>
                <span v-else>{{scope.row.createdTime}}</span>
              </template>
            </el-table-column>
            <el-table-column fixed="right" label="操作" width="50">
              <template slot-scope="scope">
                <el-popconfirm title="是否要删除此行？" @confirm="handleDeleteRow(scope)" placement="top" cancelButtonType="plain">
                  <i v-if="scope.row.isNew===1" slot="reference" class="el-icon-delete delete-detail-btn"></i>
                </el-popconfirm>
              </template>
            </el-table-column>
          </el-table>
        </el-form>
        <el-button plain style="width:100%;" @click="handleAddUser('detailForm')">新增人员</el-button>
      </template>
      <template v-else>444</template>
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
  validateTime = (rule, value, callback)=>{
    // 尽量少用
    let validateIndex = rule.field.split('.')[1]
    const changeType = this.baseInfo.changeType
    let startDate, endDate;
    if(changeType === '1') {
      startDate = this.baseInfo.detailForm.detailList[validateIndex].startYear
      endDate = this.baseInfo.detailForm.detailList[validateIndex].endYear
    } else if(changeType === '3') {
      startDate = this.baseInfo.detailForm.detailList[validateIndex].startDate
      endDate = this.baseInfo.detailForm.detailList[validateIndex].endDate
    }
    if(!startDate || !endDate){
      callback();
    }else{
      var startTime = new Date(startDate );
      var endTime = new Date(endDate);
      if(startTime>endTime){
          callback(new Error())
      }else{
        // 清除日期校验
        if(changeType === '1') {
          this.$refs[`startYear${validateIndex}`].clearValidate()
          this.$refs[`endYear${validateIndex}`].clearValidate()
        }else if(changeType === '3') {
          this.$refs[`startDate${validateIndex}`].clearValidate()
          this.$refs[`endDate${validateIndex}`].clearValidate()
        }
        callback();
      }
    }
  }
  loadingBtn = 0;
  baseInfo = this.getBaseInfo();
  // 基本信息表单校验规则
  baseFormRules = {
    projectName: [
      { required: true, message: "点击选择审批完成的研发项目", trigger: "change" }
    ],
    changeType: [
      { required: true, message: "请选择变更类型", trigger: "change" }
    ]
  }
  // 变更说明
  changeFormRules = {
    originalProjectCnt: [
      { required: true, message: "请输入", trigger: "change" }
    ],
    advise: [
      { required: true, message: "请输入", trigger: "change" }
    ],
    reason: [
      { required: true, message: "请输入", trigger: "change" }
    ],
    implementationSituation: [
      { required: true, message: "请输入", trigger: "change" }
    ],
    feeUse: [
      { required: true, message: "请输入", trigger: "change" }
    ],
  }
  // 变更明细
  formRules = {
    startYear: [
      { required: true, message: "请选择开始年度", trigger: "change" },
      { validator: this.validateTime, trigger: 'change'}
    ],
    endYear: [
      { required: true, message: "请选择结束年度", trigger: "change" },
      { validator: this.validateTime, trigger: 'change'}
    ],

    // 人员变更表单
    userName: [
      { required: true, message: "请输入姓名", trigger: "blur" }
    ],
    idcard: [
      { required: true, pattern:/^[1-9]\d{5}(18|19|20|(3\d))\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/,message: "请输入身份证号码", trigger: "blur" }
    ],
    age: [
      { required: true, message: "年龄", trigger: "change" }
    ],
    sex: [
      { required: true, message: "性别", trigger: "change" }
    ],
    education: [
      { required: true, message: "学历", trigger: "change" }
    ],
    department: [
      { required: true, message: "所属部门", trigger: "change" }
    ],
    job: [
      { required: true, message: "职务职称", trigger: "change" }
    ],
    major: [
      { required: true, message: "所学专业", trigger: "change" }
    ],
    work: [
      { required: true, message: "现从事专业", trigger: "change" }
    ],
    company: [
      { required: true, message: "所在单位", trigger: "change" }
    ],
    task: [
      { required: true, message: "研究任务及分工", trigger: "change" }
    ],
    fullTimeRate: [
      { required: true, message: "全时率", trigger: "change" }
    ],
    phone: [
      {required:true,message:'请输入联系电话',trigger:'change'},
      {pattern: /^\d{11}$/, message: "请输入正确联系电话",trigger: "blur"},
    ],
    startDate: [
      { required: true, message: "参与研发开始日期", trigger: "change" },
      { validator: this.validateTime, trigger: 'change'}
    ],
    endDate: [
      { required: true, message: "参与研发结束日期", trigger: "change" },
      { validator: this.validateTime, trigger: 'change'}
    ],
    status: [
      { required: true, message: "状态", trigger: "change" }
    ],
    creator: [
      { required: true, message: "编制人", trigger: "change" }
    ],
    createdTime: [
      { required: true, message: "编制时间", trigger: "change" }
    ],
  }
  
  activated() {
    if (Object.keys(this.$route.params).length > 0) {
      if (this.$route.params.businessId) {
        this.initData(this.$route.params.businessId);
      }
    }
  }
  
  // 初始化新建数据
  initData() {
    this.$refs["baseForm"].resetFields();
    this.$refs["changeForm"].resetFields();
    this.$API.apiCheckFinalDetail({businessId}).then((res) => {
      this.baseInfo = Object.assign(this.baseInfo, this.getBaseInfo(), res.data)
      this.baseInfo.checkInfo.projectAbstract = res.data.projectAbstract
      this.baseInfo.checkInfo.directoryAndUnit = res.data.directoryAndUnit
      !res.data.attachmentList && (this.baseInfo.attachmentList = [])
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
    this.$router.push({ name: "changeList", params: { refresh: bool } });
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
    });
  }
  // 用户变更-添加
  handleAddUser(formName) {
    const userTmpl = {
      isNew: 1,
      userName: '',
      idcard: '',
      age: '',
      sex: '',
      education: '',
      department: '',
      job: '',
      major: '',
      work: '',
      company: '',
      task: '',
      fullTimeRate: '',
      phone: '',
      startDate: '',
      endDate: '',
      status: '正常',
      creator: '',
      createdTime: ''
    }
    this.$refs[formName].validate(valid => {
      if (valid) {
        this.baseInfo.detailForm.detailList.push(userTmpl)
      } else {
        return false;
      }
    });
  }
  // 用户变更-删除某一行
  handleDeleteRow(scope) {
    this.baseInfo.detailForm.detailList.splice(scope.$index, 1)
  }
  // 选择变更类型
  handleChangeType(value) {
    console.log('选择变更类型', value)
    // API获取变更明细
    this.getChangeDetail()
  }
  // 获取变更明细
  getChangeDetail() {
    this.baseInfo.detailForm.detailList = []
    switch(this.baseInfo.changeType) {
      case '1': 
        this.baseInfo.detailForm.detailList = [
          {
            status: '变更前',
            startYear: '2021-01-01',
            endYear: '2021-01-01',
            changeUser: '慕踊前',
            changeTime: '2021-12-31  18:00:00',
            editable: 0
          },
          {
            status: '变更后',
            startYear: '2021-01-01',
            endYear: '2021-01-01',
            changeUser: '',
            changeTime: '',
            editable: 1
          }
        ]
        break
      case '2':
        break
      case '3':
        this.baseInfo.detailForm.detailList = [
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
            status: '正常',
            creator: '慕踊前',
            createdTime: '2021-12-31  18:00:00'
          },
          {
            userName: 'Licy',
            idcard: '2121029102930102',
            age: '32',
            sex: '男',
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
            status: '正常',
            creator: '慕踊前',
            createdTime: '2021-12-31  18:00:00'
          }
        ]
        break
      default:
        break
    }
  }
}
</script>

<style lang="scss" scoped>
.page {
  width: 100%;
  height: 100%;
  padding-bottom: 46px;
  .base-form /deep/ .el-form-item{
    width: 33%!important;
  }
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
