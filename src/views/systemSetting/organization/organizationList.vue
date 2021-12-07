<template>
    <div class="organization">
        <div class="left">
            <h4>
                <div>组织管理</div>
                <!-- <div>
                    <i @click.stop="addGroup" class="el-icon-plus addProject"></i>
                </div> -->
            </h4>
            <el-input placeholder="请输入名称" prefix-icon="el-icon-search" v-model="filterTreeText"></el-input>
            <el-tree 
            :data="projectTree" 
            draggable
            :allow-drop="allowDrop"
            :allow-drag="allowDrag"
            @node-drop="nodeDrop"
            :props="projectProps" 
            @node-click="handleProjectNodeClick"
            :default-expanded-keys = "defaultExpandedKeys"
            @node-expand="nodeExpand"
            @node-collapse="nodeCollapse"
            node-key="id"
            ref="projectTree"
            v-loading="loadingTree"
            element-loading-spinner="el-icon-loading"
            :filter-node-method="filterProjectTreeNode"
            >
                <span class="custom-tree-node" slot-scope="{ node, data }">
                    <span class="span-ellipsis">
                        <span :title="node.label">
                            <i class="iconfont icon-ban" style="font-size:14px;color:red;font-weight:bold" v-if="!data.status"></i>
                            {{ node.label }}
                        </span>
                    </span>
                    <span class="node_edit">
                        <i @click.stop="update(node,data)" class="el-icon-edit" v-if="['-1','-2','1','2'].includes(data.orgType)"></i>
                        <i @click.stop="remove(node,data)" class="el-icon-delete" v-if="['-1','-2','1','2'].includes(data.orgType)"></i>
                        <i @click.stop="append(node,data)" class="el-icon-plus" v-if="['-1','-2','0','1','2'].includes(data.orgType)"></i>
                    </span>
                </span>
            </el-tree>
        </div>
    </div>
</template>

<script>
import { Component, Vue, Watch } from 'vue-property-decorator'
import tableMixin from '@/mixins/tableMixin'
import $alert from './alert'

@Component({
    name: 'organizationList',
    components: {
    
    }
})
export default class extends tableMixin {
    // 过滤文字
    filterTreeText = ''
    @Watch('filterTreeText')
    filterText(val) {
        this.$refs.projectTree.filter(val);
      }
    defaultExpandedKeys = []
    // 树loading
    loadingTree = false
    // 工程树列表
    projectTree = []
    // 树属性
    projectProps = {
        children:'children',
        label:'orgName'
    }

    mounted(){
        this.getOrganizationTree();
    }
    // 获取组织树
    getOrganizationTree(){
        let params = {
            menuCode: this.MENU_CODE_LIST.organizationList
        }
        this.loadingTree = true;
        this.$API.apiGetOrganizationTree(params).then(res=>{
            this.projectTree = res.data || [];
            if(res.data.length > 0){
                this.defaultExpandedKeys = [res.data[0].id]
            }
            this.loadingTree = false
        }).catch(()=>{
            this.loadingTree = false;
        })
    }
    // 点击树
    handleProjectNodeClick(data){
        // console.log(data);
        // if(data.type == 1){
        //     console.log('盾构队');
        // }else if(data.type == '2'){
        //     console.log('区间')
        // }else if(data.type == '3'){
        //     console.log(data.name);
        // }
    }

    allowDrop(draggingNode,dropNode,type){
        if(draggingNode.data.parentId === dropNode.data.parentId){
            return type == 'next' || type == 'prev'
        }else if(draggingNode.data.parentId === dropNode.data.id){
            return type == 'inner'
        }else{
            return false;
        }
    }
    allowDrag(node){
        return node.data.orgType < 2;
    }
    

    nodeDrop(Node){
        const params = {
            data:this.projectTree,
            menuCode:this.MENU_CODE_LIST.organizationList,
            creatorOrgId:this.$store.getters.currentOrganization.organizationId,
            creatorOrgName:this.$store.getters.currentOrganization.organizationName,
        }
        this.$API.apiUpdateOTree(params).then(res=>{
            this.nodeExpand(Node.data)
            // console.log(res);
            this.$message({
                type:'success',
                message:'更新成功!'
            })
            this.getOrganizationTree();
        }).catch(()=>{
            this.getOrganizationTree();
        })
    }

    // 树过滤方法
    filterProjectTreeNode(value,data){
        if (!value) return true;
        return data.orgName.indexOf(value) !== -1;
    }
    nodeExpand(data){
        let flag = this.defaultExpandedKeys.some(item=>{
            if(item == data.id){
                return true;
            }
        })
        if(!flag){
            this.defaultExpandedKeys.push(data.id);
        }
    }
    nodeCollapse(data){
        let collapseIndex = this.getCloseNode(data);
        let defaultExpandedKeys = [];
        this.defaultExpandedKeys.forEach((item,index)=>{
            if(!collapseIndex.includes(item)){
                defaultExpandedKeys.push(item)
            }
        })
        this.defaultExpandedKeys = defaultExpandedKeys;
    }
    // 循环获取需要关闭的节点
    getCloseNode(data){
        let collapseIndex = [];
        collapseIndex.push(data.id);
        if(data.children && data.children.length > 0){
            data.children.forEach(item=>{
                collapseIndex.push(...this.getCloseNode(item))
            })
        }
        return collapseIndex;
    }
    // 新增公司
    addGroup(){
        $alert.alertAddCompany().then(res=>{
            this.getOrganizationTree();
        }).catch(err=>{})
    }

    // 添加
    append(node,data){
        // 添加部门/职务
        $alert.alertAddDepartPosition({ parentId: data.orgId }).then(res=>{
            this.nodeExpand(data);
            this.getOrganizationTree();
        }).catch(()=>{})
    }

    // 删除
    remove(node,data){
        this.$confirm('确认删除该节点?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
            const params = {
                creatorOrgId:this.$store.getters.currentOrganization.organizationId,
                creatorOrgName:this.$store.getters.currentOrganization.organizationName,
                menuCode:this.MENU_CODE_LIST.organizationList,
                orgId:data.orgId
            }
            this.$API.apiDeleteOrgNode(params).then(()=>{
                this.$message({
                    type: 'success',
                    message: '删除成功!'
                });
                this.nodeCollapse(data);
                this.getOrganizationTree();
            })
        }).catch(() => {
                   
        });
    }

    // 编辑
    update(node,data){
        if(data.orgType == 0){
            // 编辑公司
            $alert.alertUpdateCompany(data).then(res=>{
                this.nodeExpand(data);
                this.getOrganizationTree();
            }).catch(err=>{})
        }else {
            // 编辑部门/职务
            $alert.alertUpdateDepartPosition(data).then(res=>{
                this.nodeExpand(data);
                this.getOrganizationTree();
            }).catch(err=>{})
        }
    }
}
</script>

<style lang="scss" scoped>
@import '@/assets/scss/element-variables.scss';
.organization{
    display: flex;
    height: calc(100vh - 130px);
    background-color: #fff;
    h4{
        display: flex;
        justify-content: space-between;
        margin-top: 0;
    }
    .addProject{
        &:hover{
            cursor: pointer;
            color:$--color-primary;
        }
    }
    .custom-tree-node {
        width: 100%;
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
    
}
.left{
    width:420px;
    height: 100%;
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
    &::v-deep{
        .el-tree{
            height: calc(100% - 82px);
            overflow: auto;
            margin: 0 -15px;
            padding: 0 15px;
        }
    }
}
.right{
    flex: 1;
    padding: 15px;
    .btn-group{
        margin-bottom: 30px;
    }
    position: relative;
    height: calc(100vh - 120px);
    background-color: #fff;
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
