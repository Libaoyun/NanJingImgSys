<template>
    <div class="wrapper">
        <!-- 按钮组版块 -->
        <div class="global-btn-group">
            <el-button type="primary" size="small" icon="iconfont icon-icon_add" @click="createBtn" v-checkPermission="'create'">新建</el-button>
            <!-- <upload-excel @afterUploadExcel="afterUploadExcel" :excelColumnName="excelColumnName"></upload-excel> -->
            <el-button size="small" icon="iconfont icon-bianji1" @click="editBtn" v-checkPermission="'edit'">编辑</el-button>
            <loading-btn size="small" icon="iconfont icon-icon-delete" @click="deleteBtn(1)" :loading="loadingBtn" v-checkPermission="'delete'">删除</loading-btn>
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
            <el-tooltip placement="top">
                <div slot="content">重置说明：<br/>默认密码为：CRCC123456</div>
                <el-button size="small" icon="iconfont icon-zhongzhimima" @click="resetPassWordBtn" v-checkPermission="'create'">重置密码</el-button>
            </el-tooltip>
            <el-button size="small" icon="iconfont icon-icon_refresh" @click="refreshBtn">刷新</el-button>
            <div class="search iconfont icon-sousuo" @click="openSearch"> 搜索</div>
        </div>
        <!-- 表格版块 -->
        <el-table
            ref="tableData"
            :data="tableData"
            :row-key="getRowKeys"
            :height="tableHeight"
            :border="tableConfig.border"
            v-loading="listLoading"
            :element-loading-spinner="tableConfig.loadingIcon"
            @filter-change="filterChange"
            @selection-change="tableSelectionChange"
            class="global-table-default"
            style="width: 100%;">
            <el-table-column type="selection" width="55" align="center" :reserve-selection="true"></el-table-column>
            <el-table-column fixed label="序号" type="index" width="55" align="center">
                <template slot-scope="scope">
                <span>{{(listQuery.page-1)*listQuery.limit + scope.$index + 1}}</span>
                </template>
            </el-table-column>
            <el-table-column fixed prop="userCode" label="用户编号" width="180" align="center" :show-overflow-tooltip="true">
                <template slot-scope="props">
                    <el-button type="text" size="small" @click="detailBtn(props.row)">{{props.row.userCode}}</el-button>
                </template>
            </el-table-column>
            <el-table-column fixed prop="userName" label="用户名称" width="120" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="mobilePhone" label="手机号码" width="140" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="departmentName" label="所属部门" width="140" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="postName" label="所属职务" width="140" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="education" label="学历" width="100" column-key="educationCode" :filters="educationList" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="employeeStatus" label="员工状态" width="100" column-key="employeeStatusCode" :filters="employeeStatusList" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="employeeType" label="员工类型" width="100" column-key="employeeTypeCode" :filters="employeeTypeList" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="createUser" label="创建人" width="100" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="updateUser" label="更新人" width="100" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="updateTime" label="更新时间" width="180" align="center" :show-overflow-tooltip="true"></el-table-column>
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
import uploadExcel from '@/components/uploadExcel'

@Component({
    name: 'userList',
    components: {
        search,
        uploadExcel
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
    excelColumnName = [
        {value:'serialNumber',label:'序号'},
        {value:'userCode',label:'用户编号'},
        {value:'userName',label:'用户名称'},
        {value:'mobilePhone',label:'手机号码'},
        {value:'departmentName',label:'所属部门'},
        {value:'postName',label:'所属职务'},
        {value:'education',label:'学历'},
        {value:'employeeStatus',label:'员工状态'},
        {value:'employeeType',label:'员工类型'},
        {value:'createUser',label:'创建人'},
        {value:'createTime',label:'创建时间'},
        {value:'updateUser',label:'更新人'},
        {value:'updateTime',label:'更新时间'},
    ]

    created() {
        this.getEducationList()
        this.getEmployeeStatusList()
        this.getEmployeeTypeList()


        this.getUserList();
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
            list.push(item.id)
        })
        return list;
    };
    getRowKeys(row) {
        return row.id
    }
    // 查询外部用户列表
    getUserList(){
        var params = {};
        params.pageNum = this.listQuery.page; // 页码
        params.pageSize = this.listQuery.limit; // 每页数量
        params = Object.assign(params,this.searchParams,this.filterParams);
        this.listLoading = true
        this.$API.apiGetUserList(params).then(res=>{
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
        this.$router.push({ name: 'userNew',params:{initData: true}})
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
        this.$router.push({ name: 'userEdit',params:{
            userInfo:this.selected[0]
        }})
    }
    // 详情
    detailBtn(data) {
        this.$router.push({ name: 'userDetail',params:{
            userInfo:data
        }})
    }
    // 删除
    deleteBtn(loadingBtnIndex){
        if(this.selected.length == 0){
            this.$message({
                type: 'info',
                message: '请至少选择一条记录!'
            })
            return
        }
        this.$confirm('确定删除已选择的记录, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then((e) => {
            const params = {
                idList:this.idList,
                creatorOrgId : this.$store.getters.currentOrganization.organizationId,
                creatorOrgName : this.$store.getters.currentOrganization.organizationName,
                menuCode : this.MENU_CODE_LIST.userList
            }
          this.loadingBtn = loadingBtnIndex
          this.$API.apiDeleteUser(params).then(res=>{
            this.loadingBtn = 0
            this.$message({
              type: 'success',
              message: '删除成功!'
            });
            this.resetPageNum();
            this.$refs.tableData.clearSelection();
            this.getUserList();
          }).catch(()=>{
              this.loadingBtn = 0;
          })
        }).catch(() => {    
          this.loadingBtn = 0     
        });
    }
    afterUploadExcel(res){
        console.log(res);
        var data = {
            creatorOrgId : this.$store.getters.currentOrganization.organizationId,
            creatorOrgName : this.$store.getters.currentOrganization.organizationName,
            idList:res,
            menuCode:this.MENU_CODE_LIST.userList
        };
        this.$API.apiImportEquipmentDepreciation(data).then(res=>{
            this.$message({
                type: 'success',
                message: '导入成功!'
            });
            this.refreshBtn();
        })
    }
    // 导出excel
    exportExcelBtn(){
        var data = {
            creatorOrgId : this.$store.getters.currentOrganization.organizationId,
            creatorOrgName : this.$store.getters.currentOrganization.organizationName,
            idList:this.idList,
            menuCode:this.MENU_CODE_LIST.userList
        };
        this.EXPORT_FILE(this.selected,'excel',{url:'/rdexpense/user/exportExcel',data});
    }
    // 导出pdf
    exportPdfBtn(){
        var data = {
            creatorOrgId : this.$store.getters.currentOrganization.organizationId,
            creatorOrgName : this.$store.getters.currentOrganization.organizationName,
            idList:this.idList,
            menuCode:this.MENU_CODE_LIST.userList
        }
        this.EXPORT_FILE(this.selected,'pdf',{url:'/rdexpense/user/exportPDF',data});
    }
    // 打印
    printBtn(){
        var data = {
            creatorOrgId : this.$store.getters.currentOrganization.organizationId,
            creatorOrgName : this.$store.getters.currentOrganization.organizationName,
            idList:this.idList,
            menuCode:this.MENU_CODE_LIST.userList
        }
        this.EXPORT_FILE(this.selected,'print',{url:'/rdexpense/user/exportPDF',data});
    }
    // 重置密码
    resetPassWordBtn() {
        if(this.selected.length == 0){
            this.$message({
                type: 'info',
                message: '请至少选择一条记录!'
            })
            return
        }
        this.$confirm('确定重置已选择的用户密码, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then((e) => {
            var data = {
                creatorOrgId : this.$store.getters.currentOrganization.organizationId,
                creatorOrgName : this.$store.getters.currentOrganization.organizationName,
                idList:this.idList,
                menuCode:this.MENU_CODE_LIST.userList
            }
            this.$API.apiResetPassword(data).then(res=>{
                this.$message({
                    type:'success',
                    message:'重置成功'
                })
            })
        })
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
            this.getUserList();
        }
    }
    // 搜索
    openSearch(){
        this.searchDialog = true
    }
    searchSubmit(){
        this.listQuery.page = 1
        this.getUserList();
    }
    // 表格：表头筛选条件变化时触发
    filterChange(value){
        this.filterParams = value;
        this.listQuery.page = 1;
        this.getUserList();
    }
    // 表格：复选框变化时触发,删除编辑
    tableSelectionChange(value){
        this.selected = value
    }
    // 表格分页：每页显示条数变化触发
    handleSizeChange(value) {
        this.listQuery.page = 1;
        this.listQuery.limit = value;
        this.getUserList();
    }
    // 表格分页：当前页变化触发
    handleCurrentChange(value) {
        this.listQuery.page = value;
        this.getUserList();
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
