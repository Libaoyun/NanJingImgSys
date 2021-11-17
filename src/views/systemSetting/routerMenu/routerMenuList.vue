<template>
    <div class="workflow">
        <div class="left">
            <h4>功能菜单树列表</h4>
            <el-input placeholder="请输入功能菜单名称" prefix-icon="el-icon-search" v-model="filterTreeText"></el-input>
            <el-tree 
            :data="projectTree" 
            :props="projectProps" 
            @node-click="handleProjectNodeClick"
            highlight-current
            node-key="menuCode"
            ref="projectTree"
            :default-expanded-keys="defaultExpandedKeys"
            @node-drop="handleDrop"
            :draggable="true"
            :expand-on-click-node="false"
            accordion
            v-loading="loadingTree"
            element-loading-spinner="el-icon-loading"
            :filter-node-method="filterProjectTreeNode"
            >
            <span class="custom-tree-node" slot-scope="{ node, data }">
                <span>{{ node.label }}</span>
                <span class="node_edit">
                    <i @click.stop="append(data)" class="el-icon-plus"></i>
                    <i @click.stop="remove(node,data)" class="el-icon-minus" v-if="data.max!=1"></i>
                    <i @click.stop="update(node,data)" class="el-icon-edit" v-if="data.max!=1"></i>
                </span>
            </span>
            </el-tree>
        </div>
        <div class="right">
            <h4>菜单详情</h4>
            <el-table
                :data="workflowList"
                :max-height="tableConfig.maxHeight"
                :border="tableConfig.border"
                v-loading="listLoading"
                :element-loading-spinner="tableConfig.loadingIcon"
                class="global-table-default"
                style="width: calc(100% - 3px)">
                <el-table-column prop="menuCode" label="菜单编码" align="center" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="title" label="菜单名称" align="center" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="name" label="路由名称" align="center" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="path" label="菜单路径" align="center" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="component" label="路由组件" align="center" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="icon" label="图标" align="center" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="hidden" label="是否显示" align="center" :show-overflow-tooltip="true">
                    <template slot-scope="prop">
                        {{prop.row.hidden ? "不显示" : "显示"}}
                    </template>
                </el-table-column>
                <el-table-column prop="pathRouting" label="是否列表" align="center" :show-overflow-tooltip="true">
                    <template slot-scope="prop">
                        {{prop.row.pathRouting ? "否" : "是"}}
                    </template>
                </el-table-column>
                <el-table-column prop="comButton" label="按钮权限" align="center" :show-overflow-tooltip="true">
                    <template slot-scope="prop">
                        {{getText(prop.row.comButton)}}
                    </template>
                </el-table-column>
                <el-table-column prop="noDropdown" label="子级菜单" align="center" :show-overflow-tooltip="true">
                    <template slot-scope="prop">
                        {{prop.row.pathRouting ? "无" : "有"}}
                    </template>
                </el-table-column>
                <el-table-column prop="isApprove" label="是否为审批页面" align="center" :show-overflow-tooltip="true">
                    <template slot-scope="prop">
                        {{prop.row.isApprove ? "是" : "否"}}
                    </template>
                </el-table-column>
                <el-table-column prop="relateFlow" label="是否关联工作流" align="center" :show-overflow-tooltip="true">
                    <template slot-scope="prop">
                        {{prop.row.relateFlow ? "是" : "否"}}
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <create :createDialog.sync="createDialog" @createRouterMenu="createRouterMenu" :loadingBtn="loadingBtn"></create>
        <edit :editDialog.sync="editDialog" :routerMenuInfo="routerMenuInfo" @editRouterMenu="editRouterMenu" :loadingBtn="loadingBtn"></edit>
    </div>
</template>

<script>
import { Component, Vue, Watch } from 'vue-property-decorator'
import tableMixin from '@/mixins/tableMixin'
import create from './table/create'
import edit from './table/edit'
import axios from 'axios'

@Component({
    name: 'workflow',
    components: {
        create,
        edit
    }
})
export default class extends tableMixin {
    loadingTree = false;
    listLoading = false;
    loadingBtn = 0;
    defaultExpandedKeys = [];
    filterTreeText= ''
    projectTree = [
        {
            title:'功能菜单',
            children:[],
            max:1,
            menuCode:0
        }
    ]
    projectProps= {
        children: 'children',
        label: 'title'
    }
    @Watch('projectTree')
    changeprojectTree(){
        console.log(this.projectTree);
    }
    workflowList = []
    createDialog = false
    editDialog = false
    routerMenuInfo = {}
    selected = [];
    @Watch('filterTreeText')
    filter(val){
        this.$refs.projectTree.filter(val);
    }
    created(){
        this.getRouterMenuTree();
    }
    // 获取菜单树
    getRouterMenuTree(){
        this.loadingTree = true;
        let data = {
            menuCode:100
        }
        this.$API.apiGetRouterMenuTree(data).then(res=>{
            this.$set(this.projectTree[0],'children',res.data);
            this.loadingTree = false;
            this.$set(this,'defaultExpandedKeys',[this.selected.menuCode || 0])
        }).catch(()=>{
            this.$set(this,'defaultExpandedKeys',[this.selected.menuCode || 0])
            this.loadingTree = false;
        })
    }
    createRouterMenu(data){
        this.loadingBtn = 1;
        let params = Object.assign({
            menuType:'1',
            parentCode : this.selected.menuCode
        },data)
        Array.isArray(params.comButton) && (params.comButton = params.comButton.join(','));
        this.$API.apiCreateNode(params).then(res=>{
            console.log(res);
            this.createDialog = false;
            this.listQuery.page = 1;
            this.loadingBtn = 0;
            this.$message({
                type:'success',
                message:'新增成功'
            })
            this.getRouterMenuTree();
        }).catch(()=>{
            this.loadingBtn = 0;
        })
    }
    editRouterMenu(data){
        this.loadingBtn = 2;
        let params = Object.assign({},data);
        Array.isArray(params.comButton) && (params.comButton = params.comButton.join(','));
        this.$API.apiUpdateNode(params).then(res=>{
            this.loadingBtn = 0;
            this.$message({
                type:'success',
                message:'编辑成功'
            })
            this.editDialog = false;
            this.listQuery.page = 1;
             this.getRouterMenuTree();
        }).catch(()=>{
            this.loadingBtn = 0;
        })
    }
    filterProjectTreeNode(value, data){
        if (!value) return true;
        return data.title.indexOf(value) !== -1;
    }
    handleProjectNodeClick(data) {
        this.workflowList = [data];
    }
    update(node,data) {
        this.selected = data;
        this.editDialog = true
        this.routerMenuInfo = JSON.parse(JSON.stringify(data));
        this.routerMenuInfo.pathRouting == 0 && (this.routerMenuInfo.comButton = (this.routerMenuInfo.comButton ? this.routerMenuInfo.comButton.split(','): []))
    }

    append(data){
        this.selected = data;
        this.createDialog = true;
    }

    remove(node,data){
        this.$confirm("确定删除已选择的记录, 是否继续?", "提示", {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning"
        }).then(()=>{
            let params = {
                menuCode:data.menuCode
            }
            this.$API.apiDeleteNode(params).then(res=>{
                this.$message({
                    type:'success',
                    message:'删除成功'
                })
                this.listQuery.page = 1;
                this.loadingBtn = 0;
                this.getRouterMenuTree();
            }).catch(()=>{
                this.loadingBtn = 0;
            })
        })
    }

    // 拖拽
    handleDrop(){
        console.log(this.projectTree);
        let params = {
            menuCode:100,
            data:this.projectTree[0].children
        }
        this.$API.apiUpdateTree(params).then(res=>{
            this.$message({
                type:'success',
                message:'更新成功'
            })
            this.listQuery.page = 1;
            this.getRouterMenuTree();
        }).catch(()=>{
            this.$message({
                type:'error',
                message:'更新失败'
            })
            this.listQuery.page = 1;
            this.getRouterMenuTree();
        })
    }
    getText(array){
        var map = {
            "a10001":"新建",
            "a10002":"编辑",
            "a10003":"详情",
            "a10004":"审批"
        }
        if(!Array.isArray(array)){
            array = [array];
        }
        array = array.map(item => {
            return map[item]
        });
        return array.join(',');
    }
}
</script>

<style lang="scss" scoped>
@import '@/assets/scss/element-variables.scss';
.custom-tree-node {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 14px;
    padding-right: 8px;
    .node_edit{
        display: none;
        i{
            &:hover{
                color:$--color-primary;
            }
        }
    }
    &:hover{
        .node_edit{
            display: block;
        }
    }
    span>{
        i{
            margin-left: 10px;
        }
    }
  }
.workflow{
    display: flex;
    min-height: calc(100vh - 130px);
    background-color: #fff;
    h4{
        margin-top: 0;
    }
}
.left{
    flex: 0 0 300px;
    padding: 15px;
    border-right: 1px #EBEEF5 solid;
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
        margin-bottom: 30px;
    }
}
</style>
