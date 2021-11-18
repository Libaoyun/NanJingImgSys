<template>
    <div class="manuals">
        <div class="left">
            <h4>功能菜单树列表</h4>
            <div class="row">
              <el-input placeholder="请输入功能菜单名称" prefix-icon="el-icon-search" v-model="filterTreeText"></el-input>
              <el-button v-checkPermission="'edit'" type="primary" icon="el-icon-plus" @click="addTreeNode" circle size="mini"></el-button>
            </div>
            <el-tree 
            :data="projectTree" 
            :props="projectProps" 
            @node-click="handleTreeNodeClick"
            highlight-current
            node-key="nodeId"
            ref="projectTree"
            :expand-on-click-node="false"
            :default-expanded-keys="defaultExpandedKeys"
            accordion
            v-loading="loadingTree"
            element-loading-spinner="el-icon-loading"
            :filter-node-method="filterProjectTreeNode"
            >
                <span class="custom-tree-node" slot-scope="{ node, data }">
                <span>{{ node.label }}</span>
                <span class="node_edit" v-if="data.nodeType==='2'&&data.nodeId!=='-10000'&&data.children===null">
                    <i class="el-icon-minus" @click.stop="deleteTreeNode(node,data)"></i>
                    <i class="el-icon-edit" @click.stop="updateTreeNode(node,data)" v-if="data.max!=1"></i>
                </span>
            </span>
            </el-tree>
        </div>
        <div class="right">
            <h4>流程配置</h4>
            <div class="btn-group">
              <el-button v-checkPermission="'edit'" icon="el-icon-plus" :disabled="addFileDisabled" size="mini" type="primary" @click="createDialog=true">添加文件</el-button>
            </div>
            <el-table
                :data="fileList"
                :max-height="tableConfig.maxHeight"
                :border="tableConfig.border"
                v-loading="listLoading"
                :element-loading-spinner="tableConfig.loadingIcon"
                class="global-table-default"
                style="width: calc(100% - 3px)">
                <el-table-column label="序号" type="index" width="50" align="center">
                    <template slot-scope="scope">
                        <span>{{(listQuery.page-1)*listQuery.limit + scope.$index + 1}}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="fileType" label="文件类型" width="60" align="center" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="fileName" label="文件名称" width="150" align="center" :show-overflow-tooltip="true">
                </el-table-column>
                <el-table-column prop="remark" label="描述信息" width="150" align="center" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="creatorUserName" label="作者" width="80" align="center" :show-overflow-tooltip="true">
                    <template slot-scope="props">{{ props.row.creatorUserName ? props.row.creatorUserName : $store.getters.userInfo.name}}</template>
                </el-table-column>
                <el-table-column label="操作" width="120" :show-overflow-tooltip="false" align="center">
                    <template slot-scope="scope">
                        <el-link type="primary" :underline="false" @click="clickCurrentRow(scope.row,'detail')">查看</el-link>
                        <el-divider direction="vertical"></el-divider>
                        <el-link type="primary" :underline="false" @click="downLoad(scope.row)">下载</el-link>
                        <span v-checkPermission="'edit'">
                            <el-divider direction="vertical"></el-divider>
                            <el-link type="primary" :underline="false" @click="clickCurrentRow(scope.row,'edit')">编辑</el-link>
                            <el-divider direction="vertical"></el-divider>
                            <el-popconfirm title="是否要删除此文件？" @confirm="deleteFile(scope)" placement="top" cancelButtonType="plain">
                                <el-link type="danger" :underline="false" slot="reference">删除</el-link>
                            </el-popconfirm>
                        </span>
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
        <create v-if="createDialog" :createDialog.sync="createDialog" @createBookFile="createBookFile"></create>
        <edit v-if="editDialog" :editDialog.sync="editDialog" @editBookFile="editBookFile" :fileInfo="selectedFileInfo"></edit>
        <detail v-if="detailDialog" :detailDialog.sync="detailDialog" :fileInfo="selectedFileInfo"></detail>
    </div>
</template>

<script>
import { Component, Vue, Watch } from 'vue-property-decorator'
import tableMixin from '@/mixins/tableMixin'
import create from './table/create'
import edit from './table/edit'
import detail from './table/detail'

@Component({
    name: 'onlineManualsList',
    components: {
        create,
        edit,
        detail
    }
})
export default class extends tableMixin {
    createDialog = false
    editDialog = false
    detailDialog = false
    selectedFileInfo = {
        fileName: '',
        fileType: '文档',
        remark: '',
        fileList:[]
    }
    defaultExpandedKeys = [];
    addFileDisabled = true
    fileList = []

    loadingTree = false;
    total = 0;
    listLoading = false;
    filterTreeText= ''
    projectTree = []
    projectProps= {
        children: 'children',
        label: 'nodeName'
    }
    selected = [];
    @Watch('filterTreeText')
    filter(val){
        this.$refs.projectTree.filter(val);
    }
    created(){
        this.getTreeNode();
    }
    // 获取菜单树
    getTreeNode(){
        this.$set(this,'defaultExpandedKeys',['-10000'])
        // this.loadingTree = true;
        // this.$API.apiGetTreeNode().then(res=>{
        //     this.projectTree = res.data;
        //     this.formatTreeData(res.data)
        //     this.loadingTree = false;
        // }).catch(()=>{
        //     this.loadingTree = false;
        // })

        const data = [
            {
                nodeId:'26',
                nodeName:'设备调拨',
                nodeType:'1',
                children:[
                    {
                        children: null,
                        nodeId: "32",
                        nodeName: "设备需求登记",
                        nodeType: "1"
                    }
                ]
            },
            {
                nodeId: "-10000",
                nodeName: "其他",
                nodeType: "2",
                children:[
                    {
                        children: null,
                        nodeId: "91007",
                        nodeName: "自定义节点35",
                        nodeType: "2"
                    }
                ]
            }
        ]
        this.projectTree = data;
        this.formatTreeData(data)
    }
    formatTreeData(tree) {
        if(tree&&tree.length) {
            for (let i = 0; i < tree.length; i++) {
                const node = tree[i];
                if(node.children&&node.children.length>0 || node.nodeId==='-10000') {
                    this.$set(node,'disabled',true)
                    this.formatTreeData(node.children)
                }
            }
        }
    }
    // 新增节点
    addTreeNode() {
        const params = {
            menuCode:this.MENU_CODE_LIST.onlineManuals,
            nodeName:'自定义节点'
        }
        this.$API.apiAddTreeNode(params).then((res) => {
            this.getTreeNode()
        }).catch((err) => {
            
        });
    }
    // 删除节点
    deleteTreeNode(node,data) {
        this.$confirm("确定删除当前节点, 是否继续?", "提示", {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning"
        }).then(()=>{
            let params = {
                menuCode:this.MENU_CODE_LIST.onlineManuals,
                nodeId:data.nodeId
            }
            this.$API.apiDeleteTreeNode(params).then(res=>{
                this.$message({
                    type:'success',
                    message:'删除成功'
                })
                this.listQuery.page = 1;
                this.getTreeNode();
            }).catch(()=>{
            })
        })

    }
    // 编辑树节点
    updateTreeNode(node,data) {
        this.$prompt('请输入节点名称', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          inputPattern: /^.{1,20}$/,
          inputErrorMessage: '请输入节点名称',
          inputValue:data.nodeName
        }).then(({ value }) => {
            const params = {
                menuCode:this.MENU_CODE_LIST.onlineManuals,
                nodeId:data.nodeId,
                nodeName:value
            }
            this.$API.apiUpdateTreeNode(params).then(res=>{
                this.$message({
                    type:'success',
                    message:'编辑成功'
                })
                this.listQuery.page = 1;
                this.getTreeNode();
            })

        }).catch(() => {});
    }
    // 获取树节点下的的用户手册
    getSelectNodeBooksByNodeId(){
        this.listLoading = true;
        const params = {
            pageNum:this.listQuery.page,
            pageSize:this.listQuery.limit,
            nodeId:this.selected.nodeId
        }
        this.$API.apiGetFileList(params).then(res=>{
            this.listLoading = false;
            if(res.data){
                this.fileList = res.data.list || [];
                this.total = res.data.total;
            }
        }).catch(()=>{
            this.listLoading = false;
        })
    }
    filterProjectTreeNode(value, data){
        if (!value) return true;
        return data.nodeName.indexOf(value) !== -1;
    }
    // 点击树节点
    handleTreeNodeClick(data) {
        this.selected = data;
        if(data.children && data.children.length > 0 || data.nodeId==='-10000'){
            this.addFileDisabled = true
            this.fileList = []
        }else{
            this.addFileDisabled = false
            this.getSelectNodeBooksByNodeId();
        }
        this.resetPageNum();
    }
    // 删除文件
    deleteFile(scope){
        if(scope && scope.row && scope.row.businessId){
            let params = {
                menuCode:this.MENU_CODE_LIST.onlineManuals,
                businessIdList:[scope.row.businessId]
            }
            this.$API.apiDeleteBoookFile(params).then(res=>{
                this.$message({
                    type:'success',
                    message:'删除成功'
                })
                this.fileList.splice(scope.$index,1);
                this.resetPageNum();
                this.getSelectNodeBooksByNodeId();
            })
        }
    }
    // 表格分页：每页显示条数变化触发
    handleSizeChange(value) {
        this.listQuery.page = 1;
        this.listQuery.limit = value;
        this.getSelectNodeBooksByNodeId();
    }
    // 表格分页：当前页变化触发
    handleCurrentChange(value) {
        this.listQuery.page = value;
        this.getSelectNodeBooksByNodeId();
    }
    // 下载
    downLoad(data) {
        var a = document.createElement('a')
        a.download = data.fileName
        a.href = data.fileUrl;
        a.target = '_blank'
        a.click()
    }
    clickCurrentRow(data,type) {
        data.fileList = [{
            id: data.id,
            businessId:data.businessId,
            fileExt: data.fileExt,
            fileName: data.fileOriginalName,
            filePath: data.fileUrl,
            fileSize: data.fileSize,
            type: 1,
            uploadUserId:this.$store.getters.userInfo.id,
            uploadUserName:this.$store.getters.userInfo.name
        }]
        this.selectedFileInfo = data
        if(type=='detail') {
            this.detailDialog = true
        }else if(type=='edit') {
            this.editDialog = true
        }
    }
    // 新增
    createBookFile(data) {
        data.menuCode = this.MENU_CODE_LIST.onlineManuals
        data.nodeId = this.selected.nodeId
        this.$API.apiAddBoookFile(data).then(res=>{
            this.$message({
                type:'success',
                message:'新增成功'
            })
            this.createDialog = false;
            this.listQuery.page = 1;
            this.getSelectNodeBooksByNodeId();
        }).catch(()=>{})
    }
    // 编辑
    editBookFile(data) {
        data.menuCode = this.MENU_CODE_LIST.onlineManuals
        data.nodeId = this.selected.nodeId
        console.log('拿到的数据:',data);
        this.$API.apiUpdateBoookFile(data).then(res=>{
            this.$message({
                type:'success',
                message:'编辑成功'
            })
            this.editDialog = false;
            this.listQuery.page = 1;
            this.getSelectNodeBooksByNodeId();
        }).catch(()=>{
        })
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
.manuals{
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
    .row {
      display: flex;
      width: 100%;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 15px;
    }
    .el-input{
        width: 80%;
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
