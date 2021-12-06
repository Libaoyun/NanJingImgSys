<!--
* @description
* @fileName create.vue
* @author Miaoxy
* @date 2021/11/24 21:03:47
!-->
<template>
  <div style="padding-bottom: 46px;" class="contract">
    <card-global
        class="global-card-default"
        cardTitle="研发项目合同签订-新建"
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
          <el-form-item label="合同编号" prop="conId">
            <el-input
                v-model="baseInfo.conId" disabled
            ></el-input>
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
          <el-form-item label="项目名称" prop="orgName">
            <el-input
                v-model="baseInfo.orgName"
                placeholder="请输入项目名称"
            ></el-input>
          </el-form-item>
          <el-form-item label="所属单位名称" prop="companyName">
            <el-select v-model="baseInfo.companyName" placeholder="自动带入">
              <el-option
                  v-for="item in baseInfo.companyName"
                  :label="item"
                  value="item"
                  :key="item"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="项目负责人" prop="leader">
            <el-input
                :value="baseInfo.leader"
                placeholder="自动带入"
            ></el-input>
          </el-form-item>
          <el-form-item label="负责人岗位" prop="station">
            <el-input
                :value="baseInfo.station"
                placeholder="自动带入"
            ></el-input>
          </el-form-item>
          <el-form-item label="起始年度" prop="startYear">
            <el-input
                :value="baseInfo.startYear"
                placeholder="自动带入"
            ></el-input>
          </el-form-item>
          <el-form-item label="结束年度" prop="endYear">
            <el-input
                :value="baseInfo.endYear"
                placeholder="自动带入"
            ></el-input>
          </el-form-item>
          <el-form-item label="密级" prop="secretLevel">
            <el-select v-model="baseInfo.secretLevel" placeholder="请选择">
              <el-option
                  v-for="item of baseInfo.secretLevelList"
                  :label="item.label"
                  :value="item"
                  :key="item.label"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="合同状态" prop="conStatus">
            <el-select v-model="baseInfo.conStatus" placeholder="请选择">
              <el-option
                  v-for="item of baseInfo.conStatusList"
                  :label="item.label"
                  :value="item"
                  :key="item.label"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </card-global>
    <card-global
        class="global-card-default"
        cardTitle="附件上传"
        style="padding-top: 0px;height: 800px"
    >
      <el-card
          body-style="border:2px dashed #ddd;
          height:200px;
          display:flex;
          justify-content:center;
          align-items:center;
          padding-bottom:20px;"
          @click="handleUpload()"
      >
        <!-- 图片暂时缺省 -->
        <img src="" alt=""/>
        <span>点击或拖拽进行文件上传</span>
      </el-card>
      <el-button
          style="margin-top:20px"
          :disabled="isUpdata"
          @click="downFiles()"
      ><span>批量下载</span>
      </el-button>
      <upload-approval-global
          type="create"
          ref="uploadApprovalGlobal"
          :fileList="baseInfo.attachmentList"
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
import {Component, Vue, Watch} from 'vue-property-decorator';
import tableMixin from '@/mixins/tableMixin';
import {checkForm} from '@/utils/index';

@Component({
  name: 'contractNew',
  components: {},
})
export default class extends tableMixin {
  loadingBtn = 0;
  baseInfo = this.getBaseInfo();
  formRules = {
    orgName: [
      {required: true, message: '请输入项目名称', trigger: 'change'},
      {max: 30, message: '项目名称不超过30个字符', trigger: 'change'},
    ],
    conStatus: [
      {required: true, message: '请选择合同状态', trigger: 'change'},
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
      // orgNumber: this.$store.getters.currentContract?.contractId,
      conId: this.$store.getters.currentContract?.creatorConId,
      createUser: this.$store.getters.userInfo?.userName,
      createTime: new Date(),

      orgName: '',
      companyName: [{value: '第一工程有限公司'}],
      leader: '',
      station: '',
      startYear: '',
      endYear: '',
      secretLevelList: [
        {
          label: '保密'
        },
        {
          label: '公开'
        }
      ],
      secretLevel: '',
      conStatusList: [
        {
          label: '已审批'
        },
        {
          label: '已保存'
        }
      ],
      conStatus: '',
      remark: '',
    };
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
    this.$refs['doForm'].resetFields();
    this.baseInfo = Object.assign(this.baseInfo, this.getBaseInfo());
  }

  // 保存按钮
  saveBtn() {
    const _self = this;
    let formArr = ['doForm'];
    let resultArr = [];
    formArr.forEach((item) => {
      //根据表单的ref校验
      resultArr.push(checkForm(_self, item));
    });
    Promise.all(resultArr).then(() => {
      this.$confirm('确定保存当前表单?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        // 调接口
        let params = Object.assign(
            {
              creatorConId: this.$store.getters.currentContract.contractId,
              creatorOrgName: this.$store.getters.currentOrganization.organizationName,
              creatorConName: this.$store.getters.currentContract.contractName,
              menuCode: this.MENU_CODE_LIST.contractList,
            },
            this.baseInfo
        );
        this.loadingBtn = 1;
        this.$API.apiAddContract(params)
            .then((res) => {
              this.loadingBtn = 0;
              this.$message({
                type: 'success',
                message: '新增成功!',
              });
              this.$store.commit('DELETE_TAB', this.$route.path);
              this.$router.push({name: 'contractList'});
            })
            .catch(() => {
              this.loadingBtn = 0;
            });
      });
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

  // 返回按钮
  backBtn() {
    this.$confirm('未保存的数据将丢失，是否返回?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }).then(() => {
      this.$store.commit('DELETE_TAB', this.$route.path);
      this.$router.push({name: 'contractList'});
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
