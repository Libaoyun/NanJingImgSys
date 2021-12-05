<!--
* @description
* @fileName contractList.vue
* @author Miaoxy
* @date 2021/11/24 21:04:00
!-->
<template>
  <div class="wrapper">
    <!-- 按钮组版块 -->
    <div class="global-btn-group">
      <el-button
        type="primary"
        size="small"
        icon="iconfont icon-icon_add"
        @click="createBtn"
        v-checkPermission="'create'"
        >新建</el-button
      >
      <el-button
        size="small"
        icon="iconfont icon-bianji1"
        @click="editBtn"
        v-checkPermission="'edit'"
        >编辑</el-button
      >
      <loading-btn
        size="small"
        icon="iconfont icon-icon-delete"
        @click="deleteBtn(1)"
        :loading="loadingBtn"
        v-checkPermission="'delete'"
        >删除</loading-btn
      >
      <el-dropdown style="margin:0 10px">
        <el-button size="small" icon="iconfont icon-daochu">
          导出<i class="el-icon-arrow-down el-icon--right"></i>
        </el-button>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item @click.native="exportPdfBtn">PDF</el-dropdown-item>
          <el-dropdown-item @click.native="exportExcelBtn"
            >Excel</el-dropdown-item
          >
        </el-dropdown-menu>
      </el-dropdown>
      <el-button size="small" icon="iconfont icon-dayinji_o" @click="printBtn"
        >打印</el-button
      >
      <el-button
        size="small"
        icon="iconfont icon-icon_refresh"
        @click="refreshBtn"
        >刷新</el-button
      >
      <div class="search iconfont icon-sousuo" @click="openSearch">搜索</div>
    </div>
    <!-- 表格版块 -->
    <el-table
      ref="tableData"
      :data="tableData"
      :height="tableHeight"
      :border="tableConfig.border"
      v-loading="listLoading"
      :element-loading-spinner="tableConfig.loadingIcon"
      @filter-change="filterChange"
      @selection-change="tableSelectionChange"
      class="global-table-default"
      style="width: 100%;"
    >
      <el-table-column
        type="selection"
        width="55"
        align="center"
      ></el-table-column>
      <el-table-column label="序号" type="index" width="55" align="center">
        <template slot-scope="scope">
          <span>{{
            (listQuery.page - 1) * listQuery.limit + scope.$index + 1
          }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="serialNumber"
        label="合同编号"
        width="180"
        align="center"
        :show-overflow-tooltip="true"
      ></el-table-column>
      <el-table-column
        prop="projectName"
        label="项目名称"
        width="180"
        align="center"
        :show-overflow-tooltip="true"
      ></el-table-column>
      <el-table-column
        prop="secretsCode"
        label="密级"
        width="100"
        align="center"
        :show-overflow-tooltip="true"
      ></el-table-column>
      <el-table-column
        prop="contractStatusCode"
        label="合同状态"
        width="100"
        align="center"
        :show-overflow-tooltip="true"
      ></el-table-column>
      <el-table-column
        prop="projectLeaderName"
        label="项目负责人"
        width="100"
        align="center"
        :show-overflow-tooltip="true"
      ></el-table-column>
      <el-table-column
        prop="leaderPostId"
        label="岗位"
        width="100"
        align="center"
        :show-overflow-tooltip="true"
      ></el-table-column>
      <el-table-column
        prop="unitName"
        label="所属单位名称"
        width="200"
        align="center"
        :show-overflow-tooltip="true"
      ></el-table-column>
      <el-table-column
        prop="startDate"
        label="起始年度"
        width="100"
        align="center"
        :show-overflow-tooltip="true"
      >
        <template slot-scope="scope">
          {{ scope.row.createTime | formatTime('yyyy-MM-dd') }}
        </template>
      </el-table-column>
      <el-table-column
        prop="endDate"
        label="结束年度"
        width="100"
        align="center"
        :show-overflow-tooltip="true"
      >
        <template slot-scope="scope">
          {{ scope.row.createTime | formatTime('yyyy-MM-dd') }}
        </template>
      </el-table-column>
      <el-table-column
        prop="creatorUserName"
        label="编制人"
        width="100"
        align="center"
        :show-overflow-tooltip="true"
      ></el-table-column>
      <el-table-column
        prop="createdDate"
        label="创建日期"
        width="180"
        align="center"
        :show-overflow-tooltip="true"
      ></el-table-column>
<!--      <el-table-column-->
<!--        prop="updataTime"-->
<!--        label="更新日期"-->
<!--        width="180"-->
<!--        align="center"-->
<!--        :show-overflow-tooltip="true"-->
<!--      ></el-table-column>-->
    </el-table>
    <div class="pagination-wrapper">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="listQuery.page"
        :page-sizes="tableConfig.pageSizeList"
        :page-size="listQuery.limit"
        :layout="tableConfig.layout"
        :total="total"
      >
      </el-pagination>
    </div>
    <search
      :searchDialog.sync="searchDialog"
      @searchSubmit="searchSubmit"
      :searchParams.sync="searchParams"
    ></search>
  </div>
</template>

<script>
import { Component, Vue, mixins } from 'vue-property-decorator'
import tableMixin from '@/mixins/tableMixin'
import search from './table/search'
import dictionaryMixin from '@/mixins/dictionaryMixin'
import uploadExcel from '@/components/uploadExcel'
import {apiDeleteContract} from "../../../api/modules/implementationManagement/contract";

@Component({
  name: 'contractList',
  components: {
    search,
    uploadExcel,
  },
})
export default class extends tableMixin {
  tableData = []
  filterParams = {}
  listLoading = false
  selected = []
  total = 0
  searchDialog = false
  loadingBtn = 0
  searchParams = {}

  created() {
    this.getContractList()
    //添加一个api方法
  }
  //复选框选中的id值
  get idList() {
    var list = []
    this.selected.forEach((item) => {
      list.push(item.id)
    })
    return list
  }
  // 查询外部合同列表
  getContractList() {
    var params = {}
    params.pageNum = this.listQuery.page // 页码
    params.pageSize = this.listQuery.limit // 每页数量
    params = Object.assign(params, this.searchParams, this.filterParams)
    this.listLoading = true
    this.$API.apiGetContractList(params).then((res) => {
        this.listLoading = false
        if (res.data) {
          this.tableData = res.data.list
          this.total = res.data.total || 0
        }
      })
      .catch((err) => {
        this.listLoading = false
        this.$message({
          type: 'error',
          message: err,
        })
      })
  }
  // 新建
  createBtn() {
    this.$router.push({ name: 'contractNew', params: { initData: true } })
  }
  // 编辑
  editBtn() {
    if (this.selected.length !== 1) {
      this.$message({
        type: 'info',
        message: '请选择一条记录!',
      })
      return
    }
    this.$router.push({name: 'contractEdit', params: {
        contractInfo: this.selected[0],
      },
    })
  }
  // 删除
  deleteBtn(loadingBtnIndex) {
    if (this.selected.length == 0) {
      this.$message({
        type: 'info',
        message: '请至少选择一条记录!',
      })
      return
    }
    this.$confirm('您确认删除已选择的单据么?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
      .then((e) => {
        const params = {
          creatorOrgId:this.$store.getters.currentOrganization.organizationId,
          creatorOrgName:this.$store.getters.currentOrganization.organizationName,
          menuCode: this.MENU_CODE_LIST.contractList,
          idList: this.idList,
        }
        this.loadingBtn = loadingBtnIndex
        this.$API.apiDeleteContract(params).then((res) => {
            this.loadingBtn = 0
            this.$message({
              type: 'success',
              message: '删除成功!',
            })
            this.resetPageNum()
            this.getContractList()
          })
          .catch(() => {
            this.loadingBtn = 0
          })
      })
      .catch(() => {
        this.loadingBtn = 0
      })
  }
  // 导出excel
  exportExcelBtn() {
    var data = {
      idList: this.idList,
      menuCode: this.MENU_CODE_LIST.contractList,
      creatorOrgId:this.$store.getters.currentOrganization.organizationId,
      creatorOrgName:this.$store.getters.currentOrganization.organizationName,
    }
    this.EXPORT_FILE(this.selected, 'excel', {
      url: '/rdexpense/projContractSign/exportExcel',
      data,
    })
  }
  // 导出pdf
  exportPdfBtn() {
    var data = {
      creatorOrgId:this.$store.getters.currentOrganization.organizationId,
      creatorOrgName:this.$store.getters.currentOrganization.organizationName,
      idList: this.idList,
      menuCode: this.MENU_CODE_LIST.contractList,
    }
    this.EXPORT_FILE(this.selected, 'pdf', {
      url: '/rdexpense/projContractSign/exportPdf',
      data,
    })
  }
  // 打印
  printBtn() {
    var data = {
      creatorOrgId:this.$store.getters.currentOrganization.organizationId,
      creatorOrgName:this.$store.getters.currentOrganization.organizationName,
      idList: this.idList,
      menuCode: this.MENU_CODE_LIST.contractList,
    }
    this.EXPORT_FILE(this.selected, 'print', {
      url: '/rdexpense/projContractSign/exportPdf',
      data,
    })
  }
  // 刷新
  refreshBtn() {
    if (!this.listLoading) {
      this.listQuery.page = 1
      // 清除侧边栏搜索条件
      this.searchParams = {}
      // 清除表格筛选条件
      this.$refs.tableData.clearFilter()
      this.filterParams = {}
      this.getContractList()
    }
  }
  // 搜索
  openSearch() {
    this.searchDialog = true
  }
  searchSubmit() {
    this.listQuery.page = 1
    this.getContractList()
  }
  // 表格：表头筛选条件变化时触发
  filterChange(value) {
    this.filterParams = value
    this.listQuery.page = 1
    this.getContractList()
  }
  // 表格：复选框变化时触发,删除编辑
  tableSelectionChange(value) {
    this.selected = value
  }
  // 表格分页：每页显示条数变化触发
  handleSizeChange(value) {
    this.listQuery.page = 1
    this.listQuery.limit = value
    this.getContractList()
  }
  // 表格分页：当前页变化触发
  handleCurrentChange(value) {
    this.listQuery.page = value
    this.getContractList()
  }
}
</script>

<style lang="scss" scoped>
.wrapper {
  position: relative;
  height: calc(100vh - 130px);
  background-color: #f8f8f8;
  padding: 15px;
  .pagination-wrapper {
    position: absolute;
    bottom: 15px;
    display: flex;
    width: 100%;
    justify-content: center;
  }
}
</style>
