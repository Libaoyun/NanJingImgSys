<template>
    <div class="wrapper">
        <!-- 按钮组版块 -->
        <div class="global-btn-group">
            <el-button type="primary" size="small" icon="iconfont icon-icon_add" @click="createBtn" v-checkPermission="'create'">新建</el-button>
            <el-button size="small" icon="iconfont icon-bianji1" @click="editBtn" v-checkPermission="'edit'">编辑</el-button>
            <loading-btn size="small" icon="iconfont icon-icon-delete" @click="deleteBtn(1)" :loading="loadingBtn" v-checkPermission="'delete'">删除</loading-btn>
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
            <el-table-column fixed prop="userCode" label="编号" width="150" align="center" :show-overflow-tooltip="true">
                <template slot-scope="props">
                    <el-button type="text" size="small" @click="detailBtn(props.row)">{{props.row.userCode}}</el-button>
                </template>
            </el-table-column>
            <el-table-column fixed prop="userName" label="姓名" width="100" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="englishUserName" label="英文名" width="100" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="departmentName" label="部门" width="100" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="postName" label="职务" width="100" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="gender" label="性别" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="birthDate" label="出生日期" width="140" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="height" label="身高" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="education" label="学历" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="maritalStatus" label="婚姻状况" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                    {{['否','是'][scope.row.maritalStatus]}}
                </template>
            </el-table-column>
            <el-table-column prop="bloodType" label="血型" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="mobilePhone" label="移动电话" width="140" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="officeTelephone" label="办公电话" width="140" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="email" label="电子邮箱" width="140" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="fax" label="传真" width="140" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="employeeStatus" label="员工状态" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="employeeType" label="员工类型" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="participationDate" label="参工日期" width="140" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="entryDate" label="入职日期" width="140" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="confirmationDate" label="转正日期" width="140" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="leaveDate" label="离退日期" width="140" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="nationality" label="国籍" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="nativePlace" label="籍贯" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="nation" label="民族" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="religion	" label="宗教" align="center" :show-overflow-tooltip="true"></el-table-column>
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
import { Component, Vue, mixins } from 'vue-property-decorator'
import tableMixin from '@/mixins/tableMixin'
import search from './table/search'

@Component({
    name: 'userList',
    components: {
        search,
    }
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
        this.getUserList();
    }
    //复选框选中的id值
    get idList(){
        var list = [];
        this.selected.forEach(item=>{
            list.push(item.id)
        })
        return list;
    };
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
            userId:this.selected[0].id
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
          this.loadingBtn = loadingBtnIndex
          this.$API.apiDeleteUser({idList:this.idList}).then(res=>{
            this.loadingBtn = 0
            this.$message({
              type: 'success',
              message: '删除成功!'
            });
            this.resetPageNum();
            this.getUserList();
          }).catch(()=>{
              this.loadingBtn = 0;
          })
        }).catch(() => {    
          this.loadingBtn = 0     
        });
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
