<template>
    <div class="authorizationList">
        <div class="left">
            <h4>
                <div>项目树列表</div>
            </h4>
            <el-input placeholder="请输入工程名称" prefix-icon="el-icon-search" v-model="filterTreeText"></el-input>
            <el-tree 
            :data="projectTree" 
            :props="projectProps" 
            @node-click="handleProjectNodeClick"
            highlight-current
            :default-expand-all="true"
            node-key="organizationId"
            ref="projectTree"
            :expand-on-click-node="false"
            v-loading="loadingTree"
            element-loading-spinner="el-icon-loading"
            :filter-node-method="filterProjectTreeNode"
            >
                <span class="span-ellipsis" slot-scope="{ node }">
                    <span :title="node.label">{{ node.label }}</span>
                </span>
            </el-tree>
        </div>
        <div class="right">
            <h4>授权管理</h4>
            <div class="btn-group">
                <!--v-checkPermission="'create'"-->
                <el-button icon="el-icon-coordinate" size="mini" type="primary" @click="addUser">添加授权人员</el-button>
                <el-button icon="el-icon-coordinate" size="mini" type="primary" @click="addPosition">添加授权岗位</el-button>
                <el-button icon="el-icon-delete" size="mini" type="danger" @click="bathDeleteUser">删除授权对象</el-button>
            </div>
            <div class="search-group">
                <el-input placeholder="授权对象" v-model="search.authName"></el-input>
                <div class="searchBtn iconfont icon-sousuo" @click="refreshBtn"></div>
            </div>
            <el-table
                ref="tableData"
                :data="userList"
                row-key="userId"
                :max-height="tableConfig.maxHeight"
                :border="tableConfig.border"
                :element-loading-spinner="tableConfig.loadingIcon"
                v-loading="listLoading"
                @selection-change="tableSelectionChange"
                class="global-table-default"
                style="width: calc(100% - 3px)">
                <el-table-column type="selection" width="55" align="center" :reserve-selection="true"></el-table-column>
                <el-table-column label="序号" type="index" width="50" align="center">
                    <template slot-scope="scope">
                        <span>{{(listQuery.page-1)*listQuery.limit + scope.$index + 1}}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="authName" label="授权对象" width="100" align="center" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="userFlag" label="类型" width="80" align="center" :show-overflow-tooltip="true">
                    <template slot-scope="scope">
                        {{['人员','职务'][scope.row.userFlag]}}
                    </template>
                </el-table-column>
                <el-table-column prop="departmentName" label="所属组织" width="300" align="center" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="authStatus" label="授权状态" width="100" align="center" :show-overflow-tooltip="true">
                    <template slot-scope="scope">
                        {{['未授权','已授权'][scope.row.authStatus]}}
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="120" :show-overflow-tooltip="true" align="center">
                    <template slot-scope="scope">
                        <el-link type="primary" :underline="false" @click="updateMenuAuth(scope.row)" :disabled="scope.row.userId == $store.getters.userInfo.userCode">授权</el-link>
                        <el-divider direction="vertical"></el-divider>
                        <el-popconfirm title="是否要删除此对象？" @confirm="deleteUser(scope)" placement="top" cancelButtonType="plain" :disabled="scope.row.userId == $store.getters.userInfo.userCode">
                            <el-link type="danger" :underline="false" slot="reference" :disabled="scope.row.userId == $store.getters.userInfo.userCode">删除</el-link>
                        </el-popconfirm>
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
        </div>
        <!-- 添加用户授权 -->
        <auth-user :userDialog.sync="userDialog" v-if="userDialog" :project="project" @addAuthUser="addAuthUser"></auth-user>
        <!-- 添加岗位授权 -->
        <auth-position :positionDialog.sync="positionDialog" v-if="positionDialog" :project="project" @addAuthPosition="addAuthPosition"></auth-position>
        <menu-auth :menuAuthDialog.sync="menuAuthDialog" v-if="menuAuthDialog" :editParams="editParams" :project="project" @menuAuth="menuAuth"></menu-auth>
    </div>
</template>

<script>
import { Component, Vue, Watch } from 'vue-property-decorator'
import tableMixin from '@/mixins/tableMixin'
import authUser from './table/authUser'
import authPosition from './table/authPosition'
import menuAuth from './table/menuAuth'
import eventBus from '@/utils/eventBus.js'

@Component({
    name: 'authorizationList',
    components: {
        authUser,
        authPosition,
        menuAuth
    }
})
export default class extends tableMixin {
    loadingTree = false;
    listLoading = false;
     // 当前选中的工程
    project = [];
    // 编辑授权params
    editParams = {};
    filterTreeText= ''
    projectTree = []
    defaultExpandedKeys = []
    projectProps= {
        children: 'children',
        label: 'organizationName'
    }
    userList = []
    total = 0
    search = {
        authName:''
    }
    userDialog = false
    positionDialog = false
    selected = []
    menuAuthDialog = false
    currentProject = null

    @Watch('filterTreeText')
    filter(val){
        this.$refs.projectTree.filter(val);
    }
    created(){
        eventBus.$on('refreshList',()=>{
            this.refreshBtn();
        })
    }
    mounted() {
        this.getProjectTree()
    }
    beforeDestroy(){
        eventBus.$off('refreshList');
    }
    // 获取项目树
    getProjectTree(){
        this.loadingTree = true;
        this.$API.apiGetProjectTree().then((res)=>{
            res.data && res.data.forEach(item=>{
                item.children = []
            })
            this.projectTree = res.data
            if(res.data.length > 0){
                this.defaultExpandedKeys = [res.data[0].organizationId]
            }
            this.loadingTree = false;
        }).catch((err)=>{
            this.loadingTree = false;
        })
    }
    // 获取已授权项目树用户apiGetAuthProjectUser
    getAuthProjectUser(){
        let params = {
            pageNum: this.listQuery.page,
            pageSize: this.listQuery.limit,
            organizationId: this.currentProject.organizationId,
        }
        params = Object.assign(params,this.search)
        this.listLoading = true
        this.$API.apiGetAuthProjectUser(params).then((res)=>{
            this.listLoading = false
            this.userList = res.data.list
            this.total = res.data.total || 0;
        }).catch((err)=>{
            this.listLoading = false
        })
    }
    // 添加授权人员
    addAuthUser(selected) {
        this.userDialog = false
        this.refreshBtn()
    }
    // 添加授权岗位
    addAuthPosition(selected) {
        this.positionDialog = false
        this.refreshBtn()
    }
    // 授权
    menuAuth() {
        this.menuAuthDialog = false
        this.getAuthProjectUser()
    }
    // 删除已授权用户
    deleteAuthProjectUser(params) {
        this.$API.apiDeleteAuthProjectUser(params).then((res)=>{
            this.listQuery.page = 1
            this.$message({
              type: 'success',
              message: '删除成功!'
            });
            this.$refs.tableData.clearSelection();
            this.getAuthProjectUser()
        }).catch((err)=>{

        })
    }
    refreshBtn() {
        this.listQuery.page = 1;
        this.$refs.tableData.clearSelection();
        this.getAuthProjectUser()
    }
    filterProjectTreeNode(value, data){
        if (!value) return true;
        return data.organizationName.indexOf(value) !== -1;
    }
    handleProjectNodeClick(data) {
        this.currentProject = data
        var project = [];
        !project[0] && (project[0]={});
        project[0].organizationId = data.organizationId || '';
        project[0].organizationName = data.organizationName || '';
        this.project = project;
        console.log(this.project)
        this.refreshBtn()
    }
    // 删除授权对象
    deleteUser(data){
        let params = {
            organizationId: this.currentProject.organizationId,
            userList: [data.row.userId],
            menuCode:this.MENU_CODE_LIST.authorizationList,
            creatorOrgId : this.$store.getters.currentOrganization.organizationId,
            creatorOrgName : this.$store.getters.currentOrganization.organizationName,
        }
        this.deleteAuthProjectUser(params)
    }
    // 批量删除授权对象
    bathDeleteUser() {
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
          let params = {
                organizationId: this.currentProject.organizationId,
                userList: [],
                menuCode:this.MENU_CODE_LIST.authorizationList,
                creatorOrgId : this.$store.getters.currentOrganization.organizationId,
                creatorOrgName : this.$store.getters.currentOrganization.organizationName,
            }
            for(let i in this.selected){
                params.userList.push(this.selected[i].userId)
            }
            this.deleteAuthProjectUser(params)
        }).catch(() => {      
        });
    }
    tableSelectionChange(value){
        this.selected = value
    }
    // 表格分页：每页显示条数变化触发
    handleSizeChange(value) {
        this.listQuery.page = 1;
        this.listQuery.limit = value
        this.getAuthProjectUser()
    }
    // 表格分页：当前页变化触发
    handleCurrentChange(value) {
        this.listQuery.page = value
        this.getAuthProjectUser()
    }
    // 清空节点
    changeActiveAuthBtn() {
        this.$refs.projectTree.setCheckedKeys([]);
    }
    // 添加用户授权
    addUser(){
        if(this.project.length == 0){
            this.$message({
                type: 'info',
                message: '请选择项目树!'
            })
            return
        }
        this.userDialog = true
    }
     // 添加岗位授权
    addPosition() {
        // console.log(this.$refs.projectTree.getCheckedNodes());
        if(this.project.length == 0){
            this.$message({
                type: 'info',
                message: '请选择项目树!'
            })
            return
        }
        this.positionDialog = true
    }
    // 功能菜单授权
    updateMenuAuth(value){
        let params = {
            organizationId: this.currentProject.organizationId,
            userFlag: value.userFlag,
            userId: value.userId
        }
        this.editParams = params;
        this.menuAuthDialog = true
        // this.$refs.menuAuth.getAuthUserMenu(params)
    }
}
</script>

<style lang="scss" scoped>
.authorizationList{
    display: flex;
    min-height: calc(100vh - 130px);
    background-color: #fff;
    h4{
        margin-top: 0;
        display: flex;
        justify-content: space-between;
    }
}
.left{
    width:320px;
    height: calc(100vh - 130px);
    overflow: auto;
    padding: 15px;
    border-right: 1px #EBEEF5 solid;
    .el-tree {
        font-size: 14px;
    }
    .el-input{
        width: 100%;
        margin-bottom: 15px;
        &::v-deep{
            .el-input__inner{
                height: 30px;
                line-height: 30px;
                border-radius: 50px;
            }
            .el-input__icon{
                line-height: 30px;
            }
        }
    }
}
.right{
    flex: 1;
    padding: 15px;
    .btn-group{
        // margin-bottom: 15px;
    }
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
    .search-group{
        margin-bottom: 15px;
        text-align: right;
        .el-input{
            display: inline-block;
            width: 150px;
            margin-right: 10px;
            &::v-deep{
                .el-input__inner{
                    width: 150px;
                    height: 30px;
                    line-height: 30px;
                }
            }
            
        }
        .searchBtn{
            display: inline-block;
            font-size: 16px;
            cursor: pointer;
        }
        
    }
}
</style>
