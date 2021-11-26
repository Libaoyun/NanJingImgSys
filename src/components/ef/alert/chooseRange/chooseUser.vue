<template>
    <el-dialog 
        title="选择人员"
        :visible.sync="dialog"
        :close-on-click-modal="false"
        custom-class="global-dialog-default"
        @close="closeDialog"
        width="1000px">
        <div class="content">
            <div class="left">
                <el-input placeholder="请输入名称" v-model="filterTreeText" prefix-icon="el-icon-search"></el-input>
                <el-tree 
                v-loading="loadingTree"
                :data="projectTree" 
                :props="projectProps" 
                @node-click="handleProjectNodeClick"
                :expand-on-click-node="false"
                highlight-current
                :default-expanded-keys="defaultExpandedKeys"
                node-key="id"
                ref="projectTree"
                :filter-node-method="filterProjectTreeNode"
                element-loading-spinner="el-icon-loading"
                ></el-tree>
            </div>
            <div class="right">
                <div class="search-group">
                    <el-input placeholder="姓名" v-model="search.userName"></el-input>
                    <div class="searchBtn iconfont icon-sousuo" @click="listQuery.page = 1;getStationUserList()"></div>
                </div>
                <el-table
                v-loading="listLoading"
                :data="userList"
                :max-height="395"
                :element-loading-spinner="tableConfig.loadingIcon"
                :border="tableConfig.border"
                class="global-table-default"
                @selection-change="tableSelectionChange"
                style="width: 100%;">
                    <el-table-column type="selection" width="55" align="center" ></el-table-column>
                    <el-table-column label="序号" type="index" width="50" align="center">
                        <template slot-scope="scope">
                            <span>{{(listQuery.page-1)*listQuery.limit + scope.$index + 1}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="userName" label="姓名" align="center" :show-overflow-tooltip="true" width="100"></el-table-column>
                    <el-table-column prop="gender" label="性别" align="center" :show-overflow-tooltip="true" width="60"></el-table-column>
                    <el-table-column prop="departmentName" label="所属组织" align="center" :show-overflow-tooltip="true">
                        <template slot-scope="scope">
                            {{scope.row.departmentName+ '/' + scope.row.postName}}
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
        </div>
        <span slot="footer" class="dialog-footer">
            <el-button size="mini" @click="closeDialog">取消</el-button>
            <el-button type="primary" size="mini" @click="choosePerson" :loading="btnLoading == 1">选择</el-button>
        </span>
    </el-dialog>
</template>

<script>
import { Component, Vue, Prop, Watch } from 'vue-property-decorator'
import tableMixin from '@/mixins/tableMixin'

@Component({
    name: 'handleUser',
    components:{
    }
})
export default class HandleUser extends tableMixin {
    dialog = false
    project = {}

    loadingTree = false;  
    listLoading = false;
    filterTreeText= ''
    projectTree = []
    defaultExpandedKeys = []
    projectProps= {
        children: 'children',
        label: 'orgName'
    }
    userList = []
    total = 0
    search={
        userName: ''
    }
    menuAuthDialog = false
    currentProject = null
    selected = []
    btnLoading = 0

    @Watch('filterTreeText')
    filter(val){
        this.$refs.projectTree.filter(val);
    }

    init() {
        this.selected = []
        this.getStationTreeList()
    }

    // 获取组织列表
    getStationTreeList() {
        this.loadingTree = true;
        this.$API.apiGetOrganizationTree().then((res)=>{
            this.loadingTree = false;
            this.projectTree = res.data
            if(res.data.length > 0){
                this.defaultExpandedKeys = [res.data[0].id]
            }
        }).catch((err)=>{
            this.loadingTree = false;
        })
    }
    // 获取组织用户
    getStationUserList() {
        this.listLoading = true
        let params = {
            pageNum: this.listQuery.page,
            pageSize: this.listQuery.limit,
            orgId: this.currentProject ? this.currentProject.orgId : '',
        }
        params = Object.assign(params,this.search);
        this.$API.apiGetHandleRangeUser(params).then((res)=>{
            this.listLoading = false
            this.userList = res.data.list || []
            this.total = res.data.total || 0
        }).catch((err)=>{
            this.listLoading = false
        })
    }
    choosePerson() {
        if(this.selected.length === 0){
            this.$message({
                type: 'info',
                message: '请至少选择一条记录!'
            })
            return
        }
        this.promise.resolve(this.selected)
        this.dialog = false
    }
    closeDialog() {
        this.dialog = false
    }
    filterProjectTreeNode(value, data){
        if (!value) return true;
        return data.orgName.indexOf(value) !== -1;
    }
    handleProjectNodeClick(data) {
        this.currentProject = data
        this.getStationUserList()
    }
    // 表格：复选框变化时触发,删除编辑
    tableSelectionChange(value){
        this.selected = value
    }
    // 表格分页：每页显示条数变化触发
    handleSizeChange(value) {
        this.listQuery.page = 1;
        this.listQuery.limit = value
        this.getStationUserList()
    }
    // 表格分页：当前页变化触发
    handleCurrentChange(value) {
        this.listQuery.page = value
        this.getStationUserList()
    }
}
</script>

<style lang="scss" scoped>
.content{
    display: flex;
    height: 100%;
    width: 100%;
    .left{
        width: 300px;
        padding: 15px;
        height: 470px;
        overflow: auto;
        border: 1px #ddd solid;
        .el-input{
            width: 90%;
            margin-left: 5%;
            margin-bottom: 15px;
            &::v-deep{
                .el-input__inner{
                    height: 30px;
                    line-height: 30px;
                    border-radius: 20px;
                }
                .el-input__icon{
                    line-height: 33px;
                }
            }
        }
    }
    .right{
        position: relative;
        flex: 1;
        padding: 15px;
        height: 470px;
        margin-left: 15px;
        border: 1px #ddd solid;
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
        .el-table {
            height: 355px;
        }
        .pagination-wrapper{
            position: absolute;
            left: 0;
            bottom: 15px;
            display: flex;
            width: 100%;
            justify-content: center;
        }
        .clickedColor{
            color: #ccc;
        }
    }
}
</style>
