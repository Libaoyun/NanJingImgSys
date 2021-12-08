<template>
    <div class="wrapper">
        <!-- 按钮组版块 -->
        <div class="global-btn-group">
            <el-button type="primary" size="small" icon="iconfont icon-icon_add" @click="createBtn" v-checkPermission="'create'">新建</el-button>
            <el-button size="small" icon="iconfont icon-bianji1" @click="editBtn" v-checkPermission="'edit'">编辑</el-button>
            <loading-btn size="small" icon="iconfont icon-icon-delete" @click="deleteBtn(1)" :loading="loadingBtn" v-checkPermission="'delete'">删除</loading-btn>
            <loading-btn size="small" icon="iconfont" @click="generateBtn" :loading="loadingBtn" v-checkPermission="'delete'">生成费用支出申请</loading-btn>
            <!-- <loading-btn size="small" icon="iconfont icon-feichux" @click="abolishBtn(3)" :loading="loadingBtn" v-checkPermission="'submit'">废除</loading-btn> -->
            <el-dropdown style="margin:0 10px">
                <el-button size="small" icon="iconfont icon-daochu">
                    导出<i class="el-icon-arrow-down el-icon--right"></i>
                </el-button>
                <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item  @click.native="exportPdfBtn">PDF</el-dropdown-item>
                    <el-dropdown-item  @click.native="exportExcelBtn">Excel</el-dropdown-item>
                    <el-dropdown-item  @click.native="exportWordBtn">Word</el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
            <el-button size="small" icon="iconfont icon-icon_refresh" @click="refreshBtn">刷新</el-button>
            <loading-btn type="primary" size="small" class="el-icon-download downloadBtn" @click="downloadBtn" v-checkPermission="'create'">下载模板</loading-btn>
            <div class="search iconfont icon-sousuo" @click="openSearch"> 搜索</div>
        </div>
        <!-- 表格版块 -->
        <el-table
            ref="tableData"
            :data="tableData"
            row-key="id"
            :height="tableHeight"
            :border="tableConfig.border"
            v-loading="listLoading"
            :element-loading-spinner="tableConfig.loadingIcon"
            @filter-change="filterChange"
            @selection-change="tableSelectionChange"
            class="global-table-default"
            style="width: 100%;">
            <el-table-column type="selection" width="55" align="center" :reserve-selection="true"></el-table-column>
            <el-table-column label="序号" type="index" width="55" align="center">
                <template slot-scope="scope">
                <span>{{(listQuery.page-1)*listQuery.limit + scope.$index + 1}}</span>
                </template>
            </el-table-column>
            <el-table-column prop="serialNumber" label="单据编号" width="180" align="center" :show-overflow-tooltip="true">
                <template slot-scope="props">
                    <el-button type="text" size="small" @click="detailBtn(props.row)">{{props.row.serialNumber}}</el-button>
                </template>
            </el-table-column>
            <el-table-column prop="projectName" label="项目名称" width="180" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="processName" label="审批状态" column-key="statusList" :filters="approvalStatusList" width="100" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="approveUserName" label="当前审批人" width="100" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="applyUserName" label="考勤月份" width="140" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="postName" label="项目负责人" width="100" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="unitName" label="负责单位" width="140" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="createUser" label="编制人" width="100" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="createTime" label="创建日期" width="180" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="updateTime" label="更新日期" width="180" align="center" :show-overflow-tooltip="true"></el-table-column>
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
    name: 'attendanceList',
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
        // 数据字典
    }
    mounted() {
        // this.getAttendanceList();
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
    getAttendanceList(){
        var params = {};
        params.pageNum = this.listQuery.page; // 页码
        params.pageSize = this.listQuery.limit; // 每页数量
        params.creatorOrgId = this.$store.getters.currentOrganization?.organizationId;
        params = Object.assign(params,this.searchParams,this.filterParams);
        this.listLoading = true
        this.$API.apiGetAttendanceList(params).then(res=>{
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
        this.$router.push({ name: 'attendanceNew',params:{initData: true}})
    }
    // 编辑
    editBtn(){
        if(this.JUDGE_BTN(this.selected, 'edit')){
            this.$router.push({ name: 'attendanceEdit',params:{
                businessId:this.selected[0].businessId
            }})
        }
    }
    // 详情
    detailBtn(data) {
        this.$router.push({ name: 'attendanceDetail',params:{
            businessId:data.businessId,
            ids:{
                waitId:data.waitId,
                processInstId:data.processInstId,
                serialNumber:data.serialNumber
            },
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
                    businessIdList:this.idList,
                    creatorOrgId : this.$store.getters.currentOrganization.organizationId,
                    creatorOrgName : this.$store.getters.currentOrganization.organizationName,
                    menuCode : this.MENU_CODE_LIST.attendanceList
                }
            this.loadingBtn = loadingBtnIndex
            this.$API.apiDeleteAttendance(params).then(res=>{
                this.loadingBtn = 0
                this.$message({
                type: 'success',
                message: '删除成功!'
                });
                this.resetPageNum();
                this.$refs.tableData.clearSelection();
                this.getAttendanceList();
            }).catch(()=>{
                this.loadingBtn = 0;
            })
            }).catch(() => {    
                this.loadingBtn = 0     
            });
        }
        
    }
    // 下载模板
    downloadBtn() {
        const data = {
            fileFlag:1
        }
        this.EXPORT_FILE([],'excel',{url:'/rdexpense/file/templateDownLoad',data},true);
    }
    // 导出excel
    exportExcelBtn(){
        var data = {
            creatorOrgId : this.$store.getters.currentOrganization.organizationId,
            creatorOrgName : this.$store.getters.currentOrganization.organizationName,
            businessIdList:this.idList,
            menuCode:this.MENU_CODE_LIST.attendanceList
        };
        this.EXPORT_FILE(this.selected,'excel',{url:'/rdexpense/projectApply/exportExcel',data});
    }
    // 导出pdf
    exportPdfBtn(){
        var data = {
            creatorOrgId : this.$store.getters.currentOrganization.organizationId,
            creatorOrgName : this.$store.getters.currentOrganization.organizationName,
            businessIdList:this.idList,
            menuCode:this.MENU_CODE_LIST.attendanceList
        }
        this.EXPORT_FILE(this.selected,'pdf',{url:'/rdexpense/projectApply/exportPdf',data});
    }
    // 导出word
    exportWordBtn(){
        var data = {
            creatorOrgId : this.$store.getters.currentOrganization.organizationId,
            creatorOrgName : this.$store.getters.currentOrganization.organizationName,
            businessIdList:this.idList,
            menuCode:this.MENU_CODE_LIST.attendanceList
        }
        this.EXPORT_FILE(this.selected,'word',{url:'/rdexpense/projectApply/exportWord',data});
    }
    // 打印
    printBtn(){
        var data = {
            creatorOrgId : this.$store.getters.currentOrganization.organizationId,
            creatorOrgName : this.$store.getters.currentOrganization.organizationName,
            businessIdList:this.idList,
            menuCode:this.MENU_CODE_LIST.attendanceList
        }
        this.EXPORT_FILE(this.selected,'print',{url:'/rdexpense/projectApply/exportPdf',data});
    }
    // 废除
    abolishBtn(loadingBtnIndex) {

    }
    // 生成费用支出申请
    generateBtn() {
        
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
            // 清除多选表格选中
            this.$refs.tableData.clearSelection();
            this.getAttendanceList();
        }
    }
    // 搜索
    openSearch(){
        this.searchDialog = true
    }
    searchSubmit(){
        this.listQuery.page = 1
        this.getAttendanceList();
    }
    // 表格：表头筛选条件变化时触发
    filterChange(value){
        this.filterParams = value;
        this.listQuery.page = 1;
        this.getAttendanceList();
    }
    // 表格：复选框变化时触发,删除编辑
    tableSelectionChange(value){
        this.selected = value
    }
    // 表格分页：每页显示条数变化触发
    handleSizeChange(value) {
        this.listQuery.page = 1;
        this.listQuery.limit = value;
        this.getAttendanceList();
    }
    // 表格分页：当前页变化触发
    handleCurrentChange(value) {
        this.listQuery.page = value;
        this.getAttendanceList();
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
