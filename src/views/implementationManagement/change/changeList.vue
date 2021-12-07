<template>
  <div class="wrapper">
    <!-- 按钮组版块 -->
    <div class="global-btn-group">
      <el-button type="primary" size="small" icon="iconfont icon-icon_add" @click="createBtn" v-checkPermission="'create'">新建</el-button>
      <el-button size="small" icon="iconfont icon-bianji1" @click="editBtn" v-checkPermission="'edit'">编辑</el-button>
      <loading-btn size="small" icon="iconfont icon-icon-delete" @click="deleteBtn(1)" :loading="loadingBtn" v-checkPermission="'delete'">删除</loading-btn>
      <loading-btn size="small" icon="iconfont icon-tijiaochenggong" @click="submitBtn(2)" v-checkPermission="'submit'" :loading="loadingBtn">提交</loading-btn>
      <loading-btn size="small" icon="iconfont icon-feichu" @click="backBtn(3)" v-checkPermission="'back'" :loading="loadingBtn">废除</loading-btn>
      <el-dropdown style="margin:0 10px">
        <el-button size="small" icon="iconfont icon-daochu">
          导出<i class="el-icon-arrow-down el-icon--right"></i>
        </el-button>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item  @click.native="exportPdfBtn">PDF</el-dropdown-item>
          <el-dropdown-item  @click.native="exportExcelBtn">Excel</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
      <el-button size="small" icon="iconfont icon-dayinji_o" @click="printBtn">打印</el-button>
      <el-button size="small" icon="iconfont icon-icon_refresh" @click="refreshBtn">刷新</el-button>
      <div class="search iconfont icon-sousuo" @click="openSearch"> 搜索</div>
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
      style="width: 100%;">
      <el-table-column type="selection" width="55" align="center" ></el-table-column>
      <el-table-column label="序号" type="index" width="55" align="center">
        <template slot-scope="scope">
          <span>{{(listQuery.page-1)*listQuery.limit + scope.$index + 1}}</span>
        </template>
      </el-table-column>
      <el-table-column prop="orgNumber" label="单据编号" width="180" align="center" :show-overflow-tooltip="true">
        <template slot-scope="props">
          <el-button type="text" size="small" @click="detailBtn(props.row)">{{props.row.orgNumber}}</el-button>
        </template>
      </el-table-column>
      <el-table-column prop="orgName" label="项目名称" width="180" align="center" :show-overflow-tooltip="true"></el-table-column>
      <el-table-column prop="status" label="单据状态" width="100" align="center" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          {{['否','是'][scope.row.status]}}
        </template>
      </el-table-column>
      <el-table-column prop="createUser" label="当前审批人" width="100" align="center" :show-overflow-tooltip="true"></el-table-column>
      <el-table-column prop="remark" label="变更类型" width="200" align="center" :show-overflow-tooltip="true"></el-table-column>
      <el-table-column prop="updateUser" label="项目负责人" width="100" align="center" :show-overflow-tooltip="true"></el-table-column>
      <el-table-column prop="remark" label="负责人联系电话" width="200" align="center" :show-overflow-tooltip="true"></el-table-column>
      <el-table-column prop="remark" label="编制人" width="200" align="center" :show-overflow-tooltip="true"></el-table-column>
      <el-table-column prop="updateTime" label="创建日期" width="180" align="center" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          {{ scope.row.createTime | formatDate }}
        </template>
      </el-table-column>
      <el-table-column prop="updateTime" label="更新日期" width="180" align="center" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          {{ scope.row.createTime | formatDate }}
        </template>
      </el-table-column>
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
    <search :searchDialog.sync="searchDialog" @searchSubmit="searchSubmit" :searchParams.sync="searchParams"></search>  
  </div>
</template>

<script>
import { Component, Mixins, Vue, mixins } from 'vue-property-decorator'
import tableMixin from '@/mixins/tableMixin'
import dictionaryMixin from '@/mixins/dictionaryMixin'
import search from './table/search'

@Component({
  name: 'changeList',
  components: {
    search,
  }
})
export default class extends Mixins(tableMixin,dictionaryMixin) {
  tableData = []
  filterParams = {}
  listLoading = false
  selected = []
  total = 0
  searchDialog = false
  loadingBtn = 0
  searchParams = {}

  created() {
    this.getApprovalStatusList()
  }
  mounted () {
    this.getChangeList()
  }
  activated() {
    if(Object.keys(this.$route.params).length > 0){
      if(this.$route.params.refresh){
        this.refreshBtn()
      }
    }
  }
  //复选框选中的id值
  get idList(){
    var list = [];
    this.selected.forEach(item=>{
      list.push(item.businessId)
    })
    return list;
  };
  // 查询外部用户列表
  getChangeList(){
    var params = {};
    params.pageNum = this.listQuery.page; // 页码
    params.pageSize = this.listQuery.limit; // 每页数量
    params = Object.assign(params,this.searchParams,this.filterParams);
    this.listLoading = true
    this.$API.apiGetProjectList(params).then(res=>{
      this.listLoading = false
      if(res.data){
        this.tableData = res.data.list;
        this.total = res.data.total || 0;
      }
    }).catch(err=>{
      this.listLoading = false;
      this.$message({
          type:'error',
          message:err
      })
    })
  }
  // 新建
  createBtn(){
    this.$router.push({ name: 'changeNew',params:{initData: true}})
  }
  // 编辑
  editBtn(){
    if(this.selected.length !== 1){
      this.$message({
          type: 'info',
          message: '请选择一条记录!'
      })
      return
    }
    this.$router.push({ name: 'changeEdit',params:{
      businessId:this.selected[0].businessId
    }})
  }
  // 详情
  detailBtn(data) {
    this.$router.push({ name: 'changeDetail',params:{
      record:data
    }})
  }
  // 删除
  deleteBtn(loadingBtnIndex){
    if(this.JUDGE_BTN(this.selected, 'delete')){
      this.$confirm('确定删除已选择的记录, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then((e) => {
        const params = {
          creatorOrgId:this.$store.getters.currentOrganization.organizationId,
          creatorOrgName:this.$store.getters.currentOrganization.organizationName,
          menuCode:this.MENU_CODE_LIST.checkFinalList,
          idList:this.idList
        }
        this.loadingBtn = loadingBtnIndex
        this.$API.apiCheckFinalDelete(params).then(res=>{
          this.loadingBtn = 0
          this.$message({
            type: 'success',
            message: '删除成功!'
          });
          this.resetPageNum();
          this.getCheckFinalList();
        }).catch(()=>{
            this.loadingBtn = 0;
        })
      }).catch(() => {    
        this.loadingBtn = 0     
      });
    }
  }
  // 提交
  submitBtn(loadingBtnIndex) {
    if(this.JUDGE_BTN(this.selected, 'submit')){
      this.$confirm('确定提交已选择的记录, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then((e) => {
        this.loadingBtn = loadingBtnIndex
        var params = {
          businessId:this.idList[0],
          menuCode:this.MENU_CODE_LIST.checkFinalList,
          creatorOrgId: this.$store.getters.currentOrganization.organizationId,
          creatorOrgName: this.$store.getters.currentOrganization.organizationName,
          flag: 1
        }
        this.$API.apiCheckFinalSubmit(params).then(res=>{
          this.loadingBtn = 0
          this.$message({
            type: 'success',
            message: '提交成功!'
          });
          this.getCheckFinalList();
        }).catch(()=>{
          this.loadingBtn = 0;
        })
      }).catch(() => {    
        this.loadingBtn = 0     
      });
    }
  }
  // 废除
  backBtn(loadingBtnIndex) {

  }
  // 导出excel
  exportExcelBtn(){
    var data = {
      creatorOrgId : this.$store.getters.currentOrganization.organizationId,
      creatorOrgName : this.$store.getters.currentOrganization.organizationName,
      businessIdList:this.idList,
      menuCode:this.MENU_CODE_LIST.changeList
    };
    this.EXPORT_FILE(this.selected,'excel',{url:'/rdexpense/organization/exportExcel',data});
  }
  // 导出pdf
  exportPdfBtn(){
    var data = {
      creatorOrgId : this.$store.getters.currentOrganization.organizationId,
      creatorOrgName : this.$store.getters.currentOrganization.organizationName,
      businessIdList:this.idList,
      menuCode:this.MENU_CODE_LIST.changeList
    }
    this.EXPORT_FILE(this.selected,'pdf',{url:'/rdexpense/organization/exportPDF',data});
  }
  // 打印
  printBtn(){
    var data = {
      creatorOrgId : this.$store.getters.currentOrganization.organizationId,
      creatorOrgName : this.$store.getters.currentOrganization.organizationName,
      businessIdList:this.idList,
      menuCode:this.MENU_CODE_LIST.changeList
    }
    this.EXPORT_FILE(this.selected,'print',{url:'/rdexpense/organization/exportPDF',data});
  }
  // 刷新
  refreshBtn(){
    if(!this.listLoading){
      this.listQuery.page = 1;
      // 清除侧边栏搜索条件
      this.searchParams = {};
      // 清除表格筛选条件
      this.$refs.tableData.clearFilter();
      this.filterParams = {};
      this.getChangeList();
    }
  }
  // 搜索
  openSearch(){
    this.searchDialog = true
  }
  searchSubmit(){
    this.listQuery.page = 1
    this.getChangeList();
  }
  // 表格：表头筛选条件变化时触发
  filterChange(value){
    this.filterParams = value;
    this.listQuery.page = 1;
    this.getChangeList();
  }
  // 表格：复选框变化时触发,删除编辑
  tableSelectionChange(value){
    this.selected = value
  }
  // 表格分页：每页显示条数变化触发
  handleSizeChange(value) {
    this.listQuery.page = 1;
    this.listQuery.limit = value;
    this.getChangeList();
  }
  // 表格分页：当前页变化触发
  handleCurrentChange(value) {
    this.listQuery.page = value;
    this.getChangeList();
  }
}
</script>

<style lang="scss" scoped>
  .wrapper{
    position: relative;
    height: calc(100vh - 130px);
    background-color: #f8f8f8;
    padding: 15px;
    .pagination-wrapper{
      position: absolute;
      bottom: 15px;
      display: flex;
      width: 100%;
      justify-content: center;
    }
  }
</style>
