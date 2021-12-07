<!--
* @description
* @fileName edit.vue
* @author Miaoxy
* @date 2021/11/24 21:04:13
!-->
<template>
  <div style="padding-bottom: 46px;" class="contract">
    <card-global
        class="global-card-default"
        cardTitle="项目技术经济交底书-修改"
        style="height:300px"
    >
      <div style="margin-top:20px;">
        <el-form
            ref="doForm"
            :inline="true"
            :rules="formRules"
            :model="baseInfo"
            size="mini"
            label-position="right"
            label-width="100px"
        >
          <el-form-item label="单据编号" prop="serialNumber">
            <el-input v-model="baseInfo.serialNumber" disabled></el-input>
          </el-form-item>
          <el-form-item label="创建人" prop="creatorUserName">
            <el-input :value="baseInfo.creatorUserName" disabled></el-input>
          </el-form-item>
          <el-form-item label="创建时间" prop="createdDate">
            <el-input :value="baseInfo.createdDate | formatTime('yyyy-MM-dd')" disabled></el-input>
          </el-form-item>
          <el-form-item label="项目名称" prop="projectName">
            <el-input v-model="baseInfo.projectName" placeholder="请输入项目名称"></el-input>
          </el-form-item>
          <el-form-item label="所属单位名称" prop="unitName">
            <el-input v-model="baseInfo.unitName" placeholder="自动带入"></el-input>
          </el-form-item>
          <el-form-item label="项目负责人" prop="projectLeaderName">
            <el-input v-model="baseInfo.projectLeaderName" placeholder="自动带入"></el-input>
          </el-form-item>
          <el-form-item label="岗位" prop="leaderPostName">
            <el-input v-model="baseInfo.leaderPostName" placeholder="自动带入"></el-input>
          </el-form-item>
          <el-form-item label="联系电话" prop="contactNumber">
            <el-input v-model="baseInfo.contactNumber" placeholder="自动带入"></el-input>
          </el-form-item>
          <el-form-item label="交底书编制人" prop="preparedName">
            <el-input v-model="baseInfo.preparedName" placeholder="点击弹窗选择人员"></el-input>
          </el-form-item>
          <el-form-item label="编制日期" prop="preparedDate">
            <el-date-picker
                type="date"
                placeholder="点击选择"
                v-model="baseInfo.preparedDate"
                value-format="MM-dd"
                format="MM-dd"
            >
            </el-date-picker>
          </el-form-item>
          <el-form-item label="编制年度" prop="preparedYear">
            <el-date-picker
                type="date"
                placeholder="点击选择"
                v-model="baseInfo.preparedYear"
                value-format="yyyy"
                format="yyyy"
            >
            </el-date-picker>
          </el-form-item>
          <el-form-item label="编制季度" prop="preparedQuarter">
            <el-select v-model="baseInfo.preparedQuarter" placeholder="请选择">
              <el-option label="第一季度" value="1"></el-option>
              <el-option label="第二季度" value="2"></el-option>
              <el-option label="第三季度" value="3"></el-option>
              <el-option label="第四季度" value="4"></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </card-global>
    <card-global
        class="global-card-default"
        cardTitle="交底书内容信息"
        style="height:1800px"
    >
      <el-form
          ref="doForm"
          :inline="false"
          :rules="formRules"
          :model="baseInfo"
          size="mini"
          label-position="left"
      >
        <el-form-item
            label="一、本期主要研发内容季关键（核心）技术及主要创新点："
            prop="keyTechnology"
        >
          <el-input
              :rows="10"
              v-model="baseInfo.keyTechnology"
              placeholder="内容不得少于200字"
              type="textarea"
              style="width:1000px;"
              resize="none"
          ></el-input>
        </el-form-item>
        <el-form-item
            label="
            二、本期工作（进度）安排：
            "
            prop="workPlan"
        >
          <el-input
              :rows="10"
              v-model="baseInfo.workPlan"
              placeholder="内容不得少于200字"
              type="textarea"
              style="width:1000px;"
              resize="none"
          ></el-input>
        </el-form-item>
        <el-form
            prop="concreteArrangement"
            ref="doForm"
            :inline="false"
            :rules="formRules"
            :model="baseInfo"
            size="mini"
            label-position="left"
        >
          <el-form-item
              label="
            三、本期研究开发具体安排：
            "
          ></el-form-item>

          <el-form-item
              label="
            （一）人工安排：
            "
              prop="artificialPlan"
          >
            <el-input
                :rows="10"
                v-model="baseInfo.artificialPlan"
                placeholder="内容不得少于200字"
                type="textarea"
                style="width:1000px;"
                resize="none"
            ></el-input>
          </el-form-item>
          <el-form-item
              label="
            （二）研发仪器设备安排：
            "
              prop="equipmentPlan"
          >
            <el-input
                :rows="10"
                v-model="baseInfo.equipmentPlan"
                placeholder="内容不得少于200字"
                type="textarea"
                style="width:1000px;"
                resize="none"
            ></el-input>
          </el-form-item>
          <el-form-item
              label="
            （三）材料、燃料、动力费用投入安排：
            "
              prop="costPlan"
          >
            <el-input
                :rows="10"
                v-model="baseInfo.costPlan"
                placeholder="内容不得少于200字"
                type="textarea"
                style="width:1000px;"
                resize="none"
            ></el-input>
          </el-form-item>
          <el-form-item
              label="
            （四）模具、工艺装备开发及制造及设备调试安排：
            "
              prop="testPlan"
          >
            <el-input
                :rows="10"
                v-model="baseInfo.testPlan"
                placeholder="内容不得少于200字"
                type="textarea"
                style="width:1000px;"
                resize="none"
            ></el-input>
          </el-form-item>
          <el-form-item
              label="
            （五）其他：
            "
              prop="otherPlan	"
          >
            <el-input
                :rows="10"
                v-model="baseInfo.otherPlan	"
                placeholder="内容不得少于200字"
                type="textarea"
                style="width:1000px;"
                resize="none"
            ></el-input>
          </el-form-item>
        </el-form>
      </el-form>
    </card-global>
    <card-global
        class="global-card-default"
        cardTitle="附件上传"
        style="padding-top: 0px;height: 800px"
    >
      <el-card
          body-style="
          height:200px;
          width:100%;
          display:flex;
          justify-content:center;
          align-items:center;
          padding-bottom:20px;"
          @click="handleUpload()"
      >
        <el-upload
            drag
            action="https://jsonplaceholder.typicode.com/posts/"
            multiple>
          <i class="el-icon-upload"></i>
          <div>将文件拖到此处，或<em>点击上传</em></div>
        </el-upload>
      </el-card>

      <el-button
          style="margin-top:20px"
          :disabled="isUpdata"
          @click="downFiles()"
      ><span>批量下载</span>
      </el-button>
      <upload-approval-global
          type="edit"
          ref="uploadApprovalGlobal"
          :fileList="files"
      ></upload-approval-global>
    </card-global>
    <div class="global-fixBottom-actionBtn">
      <el-button size="mini" @click="backBtn">返回</el-button>
      <loading-btn
          size="mini"
          type="primary"
          @click="saveBtn"
          :loading="loadingBtn"
      >保存
      </loading-btn
      >
    </div>
  </div>
</template>

<script>
import {Component, Prop, Vue, Watch} from 'vue-property-decorator';
import tableMixin from '@/mixins/tableMixin';
import {checkForm} from '@/utils/index';
import {apiUploadFile} from "../../../../api/modules/attachment";

@Component({
  name: 'contractNew',
  components: {},
})
export default class extends tableMixin {
  loadingBtn = 0;
  baseInfo = this.getBaseInfo();
  formRules = {
    projectName: [
      {required: true, message: '请输入项目名称', trigger: 'change'},
      {max: 30, message: '项目名称不超过30个字符', trigger: 'change'},
    ],
    contactNumber: [
      {required: true, message: '请输入联系电话', trigger: 'change'},
      {max: 20, message: '联系电话不超过20个字符', trigger: 'change'},
    ],
    preparedName: [
      {required: true, message: '请输入交底书编制人', trigger: 'change'},
    ],
    preparedDate: [{required: true, message: '请输入编制日期', trigger: 'change'}],
    preparedYear: [{required: true, message: '请输入编制年度', trigger: 'change'}],
    workPlan: [
      {required: true, message: '本期工作（进度）安排', trigger: 'change'},
      {min: 200, message: '本期工作（进度）安排不少于200个字符', trigger: 'change'},
    ],
    artificialPlan: [
      {required: true, message: '请输入人工安排', trigger: 'change'},
      {min: 200, message: '人工安排不少于200个字符', trigger: 'change'},
    ],
    equipmentPlan: [
      {required: true, message: '请输入设备安排', trigger: 'change'},
      {min: 200, message: '设备安排不少于200个字符', trigger: 'change'},
    ],
    costPlan: [
      {required: true, message: '请输入费用安排', trigger: 'change'},
      {min: 200, message: '费用安排不少于200个字符', trigger: 'change'},
    ],
    testPlan: [
      {required: true, message: '请输入调试安排', trigger: 'change'},
      {min: 200, message: '调试安排不少于200个字符', trigger: 'change'},
    ],
    otherPlan: [
      {required: true, message: '请输入其他安排', trigger: 'change'},
      {min: 200, message: '联系电话不超过200个字符', trigger: 'change'},
    ],
  };
  isUpdata = true;
  files = [{}];

  //是否可以点击下载文件按钮
  set() {
    if (this.files !== null) {
      this.isUpdata = false;
    } else {
      this.isUpdata = true;
    }
  }

  //下载文件按钮
  downFiles() {
    this.$API
        .apiDownloadFile()
        .then((res) => {
          this.loadingBtn = 0;
          this.$message({
            type: 'success',
            message: '下载成功!',
          });
        })
        .catch(() => {
          this.loadingBtn = 0;
        });
  }

  // 设置空数据
  getBaseInfo() {
    return {
      serialNumber: '',
      creatorUserName: '',
      createdDate: '',
      projectName: '',
      unitName: '',
      projectLeaderName: '',
      leaderPostName: '',
      contactNumber: '',
      preparedName: '',
      preparedDate: '',
      preparedYear: '',
      preparedQuarter: '',
      //textarea
      keyTechnology: '',
      workPlan: '',
      artificialPlan: '',
      equipmentPlan: '',
      costPlan: '',
      testPlan: '',
      otherPlan: '',
      id: '',
      attachmentList: [
        {
          businessId: '',
          fileExt: '',
          fileName: '',
          filePath: '',
          fileSize: '',
          id: '',
          type: '',
          uploadUserId: '',
          uploadUserName: '',
          url: '',
        }
      ],
    };
  }

  activated() {
    if (Object.keys(this.$route.params).length > 0) {
      this.getDetail(this.$route.params.disclosureInfo);
    }
  }

  getDetail(data) {
    this.$refs['doForm'].resetFields();
    const params = {
      id: data.id
    };
    this.baseInfo.id = data.id;
    console.log(this.baseInfo.id);
    this.$API.apiGetDisclosureDetail(params).then(res => {
      this.baseInfo = Object.assign(this.baseInfo, res.data);
      console.log(this.baseInfo);
    });

  }

  // 保存按钮
  saveBtn() {
    const _self = this;
    let formArr = ['doForm'];
    let resultArr = [];
    formArr.forEach(item => { //根据表单的ref校验
      resultArr.push(checkForm(_self, item));
    });
    Promise.all(resultArr).then(() => {
      this.$confirm('确定保存当前表单?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 调接口
        let params = Object.assign(
            {
              creatorOrgId: this.$store.getters.currentOrganization.organizationId,
              creatorOrgName: this.$store.getters.currentOrganization.organizationName,
              menuCode: this.MENU_CODE_LIST.disclosureList,
            },
            this.baseInfo,
        );
        this.loadingBtn = 1;
        this.$API.apiUpdateDisclosure(params).then(res => {
          this.loadingBtn = 0;
          this.$message({
            type: 'success',
            message: '编辑成功!'
          });
          this.$store.commit('DELETE_TAB', this.$route.path);
          this.$router.push({name: 'disclosureList'});
        }).catch(() => {
          this.loadingBtn = 0;
        });
      });
    });
  }

  // 返回按钮
  backBtn() {
    this.$confirm('未保存的数据将丢失，是否返回?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      this.$store.commit('DELETE_TAB', this.$route.path);
      this.$router.push({name: 'disclosureList'});
    });
  }

  // 自定义上传
  handleUpload(file) {
    var fd = new FormData();
    fd.append('file', file.file);
    this.$API
        .apiUploadFile(fd, (progressEvent) => {
          this.onUploadProgress(progressEvent, file.file.uid);
        })
        .then((res) => {
          this.fileList.some((item, index) => {
            if (item.uid == file.file.uid) {
              this.fileList.splice(index, 1, res.data);
              return true;
            }
          });
        })
        .catch((err) => {
          this.fileList.some((item, index) => {
            if (item.uid == file.file.uid) {
              this.fileList.splice(index, 1);
              return true;
            }
          });
        });
  }

}
</script>

<style lang="scss" scoped>
@import '@/assets/scss/element-variables.scss';

/deep/ .card-global {
  height: calc(100vh - 190px);
}

.el-form {
  /deep/ .el-form-item.large {
    width: 100%;
  }

  .el-form-item--mini {
    width: 33% !important;
  }
}
</style>
