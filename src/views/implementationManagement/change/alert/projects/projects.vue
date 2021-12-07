<!--管理单位弹窗-->
<template>
  <el-dialog
    title="请选择项目"
    :visible="projectsDialog"
    :close-on-click-modal="false"
    custom-class="global-dialog-default"
    width="1000px"
    @close="closeDialog"
    class="projects"
  >
    <div class="content">
      <div class="right">
        <div class="search-group">
          <el-input
            placeholder="申请单号"
            v-model.trim="search.serialNumber"
            @keyup.enter.native="
              listQuery.page = 1;
              getProjectList();
            "
          ></el-input>
          <el-input
            placeholder="项目名称"
            v-model.trim="search.projectName"
            @keyup.enter.native="
              listQuery.page = 1;
              getProjectList();
            "
          ></el-input>
          <el-input
            placeholder="申请人"
            v-model.trim="search.applyUserName"
            @keyup.enter.native="
              listQuery.page = 1;
              getProjectList();
            "
          ></el-input>
          <div
            class="searchBtn iconfont iconsearch1"
            @click="
              listQuery.page = 1;
              getProjectList();
            "
          ></div>
        </div>
        <el-table
          :data="tableData"
          :max-height="350"
          :border="tableConfig.border"
          v-loading="listLoading"
          :element-loading-spinner="tableConfig.loadingIcon"
          class="global-table-default"
          highlight-current-row
          @current-change="chooseTable"
          @row-dblclick="chooseProject"
          @filter-change="filterChange"
          ref="singleTable"
          style="width: 100%"
        >
          <el-table-column label="" width="35">
            <template slot-scope="scope">
              <el-radio v-model="tableRadio" :label="scope.row"
                ><i></i
              ></el-radio>
            </template>
          </el-table-column>
          <el-table-column label="序号" type="index" width="50" align="center">
            <template slot-scope="scope">
              <span>{{scope.$index + 1}}</span>
            </template>
          </el-table-column>
          <el-table-column width="150" prop="serialNumber" label="申请单号" align="center" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column width="150" prop="projectName" label="项目名称" align="center" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column width="100" prop="applyUserName" label="申请人" align="center" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column width="120" prop="postName" label="岗位" align="center" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column width="120" prop="telephone" label="电话" align="center" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column width="120" prop="unitName" label="所属单位名称" align="center" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column width="100" prop="projectType" column-key="projectTypeCodeList" :filters="projectTypeList" label="项目类型" align="center" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column width="150" prop="researchContents" label="研究内容提要" align="center" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column width="150" prop="applyAmount" label="申请经费（万元）" align="center" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column width="100" prop="startYear" label="起始年度" align="center" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column width="100" prop="endYear" label="结束年度" align="center" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column width="100" prop="professionalCategory" label="专业类别" column-key="professionalCategoryCodeList" :filters="professionalCategroyList" align="center" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column width="100" prop="createUser" label="编制人" align="center" :show-overflow-tooltip="true"></el-table-column>
        </el-table>
        <div class="pagination-wrapper">
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="listQuery.page" 
            :page-sizes="tableConfig.pageSizeList" 
            :page-size="listQuery.limit"
            :layout="tableConfig.layout"
            :total="total">
          </el-pagination>
        </div>
      </div>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button size="mini" @click="closeDialog">取消</el-button>
      <el-button size="mini" type="primary" @click="chooseProject"
        >确定</el-button
      >
    </span>
  </el-dialog>
</template>

<script>
import { Component,Mixins, Vue, Prop, Watch } from "vue-property-decorator";
import tableMixin from "@/mixins/tableMixin";
import dictionaryMixin from '@/mixins/dictionaryMixin'
import store from "@/store";

@Component({
  name: "projects",
})
export default class projects extends Mixins(tableMixin,dictionaryMixin) {
  projectsDialog = false;
  listLoading = false;
  tableData = [];
  filterParams = {}
  total = 0;
  search = {
    serialNumbe: '',
    projectName: '',
    applyUserName: ''
  };
  tableRadio = null;
  init() {
    this.tableData = [];
    this.total = 0;
    this.search.serialNumbe = "";
    this.search.projectName = "";
    this.search.applyUserName = "";
    this.tableRadio = null;
    this.listQuery.page = 1;
    this.listQuery.limit = 20;
    this.getProjectTypeList() // 项目类型
    this.getProfessionalCategroyList() // 专业类别
    this.getProjectList();
  }
  // 查询项目部
  getProjectList() {
    this.listLoading = true;
    var data = {};
    data.pageNum = this.listQuery.page;
    data.pageSize = this.listQuery.limit;
    data.creatorOrgId = store.getters.currentOrganization?.organizationId;
    data.creatorOrgName = store.getters.currentOrganization.organizationName;
    data = Object.assign(data, this.search, this.filterParams)
    this.$API
      .apiCheckFinalProjectList(data)
      .then((res) => {
        this.listLoading = false;
        if (res.data) {
          this.tableData = res.data.list;
          this.total = res.data.total;
        }
      })
      .catch(() => {
        this.listLoading = true;
      });
  }
  deleteEmptyElm(obj) {
    for(const key in obj) {
      if(obj.hasOwnProperty(key)) {
        if(['',null,undefined].includes(obj[key]) || obj[key] instanceof Array) {
          delete obj[key]
        }
      }
    }
    return obj
  }
  closeDialog() {
    this.projectsDialog = false;
    this.search = {}
    // 清除表格筛选条件
    this.filterParams = {};

    this.$refs.singleTable.setCurrentRow();
  }
  // 表格：表头筛选条件变化时触发
  filterChange(value){
    this.filterParams = value;
    this.listQuery.page = 1;
    this.getProjectList();
  }
  // 表格分页：每页显示条数变化触发
  handleSizeChange(value) {
    this.listQuery.page = 1;
    this.listQuery.limit = value;
    this.getProjectList();
  }
  // 表格分页：当前页变化触发
  handleCurrentChange(value) {
    this.listQuery.page = value;
    this.getProjectList();
  }
  //选中当前行
  chooseTable(value) {
    this.tableRadio = value;
  }
  chooseProject() {
    if (this.tableRadio) {
      this.promise.resolve(this.tableRadio);
      this.closeDialog();
    } else {
      this.$message({
        type: "info",
        message: "请选择项目",
      });
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/assets/scss/element-variables.scss";
.content {
  display: flex;
  height: 100%;
  width: 100%;
  .right {
    position: relative;
    flex: 1;
    // padding: 15px;
    height: 470px;
    // border: 1px #ddd solid;
    width: 100%;
    .search-group {
      margin-bottom: 15px;
      text-align: right;
      .el-input {
        display: inline-block;
        width: 100px;
        margin-right: 10px;
        &::v-deep {
          .el-input__inner {
            // width: 150px;
            height: 30px;
            line-height: 30px;
          }
        }
      }
      .el-select {
        width: 150px;
        margin-right: 10px;
      }
      .searchBtn {
        display: inline-block;
        font-size: 16px;
        cursor: pointer;
        &:hover {
          color: $--color-primary;
        }
      }
    }
    .pagination-wrapper {
      position: absolute;
      left: 0;
      bottom: 15px;
      display: flex;
      width: 100%;
      justify-content: center;
    }
  }
}
</style>
