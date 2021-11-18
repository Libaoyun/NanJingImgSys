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
            <el-table-column prop="orgName" label="项目名称" width="100" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="status" label="是否启用" width="120" align="center" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                    {{['否','是'][scope.row.status]}}
                </template>
            </el-table-column>
            <el-table-column prop="createUser" label="创建人" width="100" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="140" align="center" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                    {{ scope.row.createTime | formatDate }}
                </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" width="130" align="center" :show-overflow-tooltip="true"></el-table-column>
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
import $alert from './alert'
import search from './table/search'

@Component({
    name: 'projectList',
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
        this.getProjectList();
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
    getProjectList(){
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
        $alert.alertAddProject().then(()=>{
            this.getProjectList()
        })
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
        $alert.alertUpdateProject(this.selected[0]).then(()=>{
            this.getProjectList()
        })
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
                creatorOrgId:this.$store.getters.currentOrganization.organizationId,
                creatorOrgName:this.$store.getters.currentOrganization.organizationName,
                menuCode:this.MENU_CODE_LIST.projectList,
                idList:this.idList
            }
          this.loadingBtn = loadingBtnIndex
          this.$API.apiDeleteProject(params).then(res=>{
            this.loadingBtn = 0
            this.$message({
              type: 'success',
              message: '删除成功!'
            });
            this.resetPageNum();
            this.getProjectList();
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
            this.getProjectList();
        }
    }
    // 搜索
    openSearch(){
        this.searchDialog = true
    }
    searchSubmit(){
        this.listQuery.page = 1
        this.getProjectList();
    }
    // 表格：表头筛选条件变化时触发
    filterChange(value){
        this.filterParams = value;
        this.listQuery.page = 1;
        this.getProjectList();
    }
    // 表格：复选框变化时触发,删除编辑
    tableSelectionChange(value){
        this.selected = value
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
