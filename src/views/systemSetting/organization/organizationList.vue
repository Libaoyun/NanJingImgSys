<template>
    <div class="organization">
        <div class="left">
            <h4>
                <div>组织管理</div>
                <div>
                    <i @click.stop="addGroup" class="el-icon-plus addProject"></i>
                </div>
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
            highlight-current
            :default-expanded-keys = "defaultExpandedKeys"
            @node-expand="nodeExpand"
            @node-collapse="nodeCollapse"
            node-key="code"
            ref="projectTree"
            :expand-on-click-node="false"
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
                        <i @click.stop="update(node,data)" class="el-icon-edit"  v-if="[-1,-2,0,1,2].includes(data.orgType)"></i>
                        <i @click.stop="remove(node,data)" class="el-icon-delete" v-if="[-1,-2,0,1,2].includes(data.orgType)"></i>
                        <i @click.stop="append(node,data)" class="el-icon-plus" v-if="[-1,-2,0,1].includes(data.orgType)"></i>
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
    name: 'organization',
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
        children:'childNode',
        label:'name'
    }

    created(){
        // this.getOrganizationTree();
    }
    // 获取组织树
    getOrganizationTree(){
        let params = {
            menuCode: this.MENU_CODE_LIST.organization
        }
        this.loadingTree = true;
        this.$API.apiGetOrganizationTree(params).then(res=>{
            this.projectTree = res.data || [];
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
        if(draggingNode.data.pId === dropNode.data.pId){
            return type == 'next' || type == 'prev'
        }else if(draggingNode.data.pId === dropNode.data.code){
            return type == 'inner'
        }else{
            return false;
        }
    }
    allowDrag(node){
        return node.data.orgType < 2;
    }
    

    nodeDrop(Node){
        this.$API.apiUpdateSysOrgTree({data:this.projectTree}).then(res=>{
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
        return data.name.indexOf(value) !== -1;
    }
    nodeExpand(data){
        let flag = this.defaultExpandedKeys.some(item=>{
            if(item == data.code){
                return true;
            }
        })
        if(!flag){
            this.defaultExpandedKeys.push(data.code);
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
        collapseIndex.push(data.code);
        if(data.childNode && data.childNode.length > 0){
            data.childNode.forEach(item=>{
                collapseIndex.push(...this.getCloseNode(item))
            })
        }
        return collapseIndex;
    }
    // 新增项目
    addGroup(){
        $alert.alertAddDepartPosition().then(res=>{
            this.getOrganizationTree();
        }).catch(err=>{})
    }

    // 添加
    append(node,data){
        if(data.orgType == -2){
            // 添加公司
            alertAddCompany({ pId: data.code },data.status).then(res=>{
                this.nodeExpand(data);
                this.getOrganizationTree();
            }).catch(()=>{})
        }else if(data.orgType == -1){
            // 添加项目
            alertAddProject({ pId: data.code },data.status).then(res=>{
                this.nodeExpand(data);
                this.getOrganizationTree();
            }).catch(()=>{})
        }else if(data.orgType == 0){
            // 添加盾构队
            alertAddShield({ pId: data.code },data.status).then(res=>{
                this.nodeExpand(data);
                this.getOrganizationTree();
            }).catch(()=>{})
        }else if (data.orgType == 1){
            // 添加区间
            alertAddInterval({ pId: data.code },data.status).then(res=>{
                this.nodeExpand(data);
                this.getOrganizationTree();
            }).catch(()=>{})
        }
    }

    // 删除
    remove(node,data){
        alertDeleteBox(data).then(res=>{
            this.nodeCollapse(data);
            this.getOrganizationTree();
        }).catch(()=>{}) 
    }

    // 编辑
    update(node,data){
        if(data.orgType == -2){
            // 编辑集团
            let project = {
                code: data.code,
                name: data.name,
                status: data.status,
                cityName: data.cityName,
            }
            alertUpdateGroup(project).then(res=>{
                this.nodeExpand(data);
                this.getOrganizationTree();
            }).catch(err=>{})
        }else if(data.orgType == -1){
            // 编辑公司
            let project = {
                code: data.code,
                name: data.name,
                status: data.status,
                cityName: data.cityName,
                pId: data.pId
            }
            alertUpdateCompany(project).then(res=>{
                this.nodeExpand(data);
                this.getOrganizationTree();
            }).catch(err=>{})
        }else if(data.orgType == 0){
            // 编辑项目
            let project = {
                xCoordinate: data.xCoordinate,
                yCoordinate: data.yCoordinate,
                code: data.code,
                name: data.name,
                pId: data.pId,
                status: data.status,
                cityName: data.cityName,
                flag: data.flag
            }
            alertUpdateProject(project).then(res=>{
                this.nodeExpand(data);
                this.getOrganizationTree();
            }).catch(err=>{})
        }else if (data.orgType == 1){
            // 编辑盾构队
           let shield = {
               code: data.code,
               name: data.name,
               status: data.status,
               pId: data.pId,
               dingdingCode: data.dingdingCode,
               financeCode: data.financeCode,
               dingdingName: data.dingdingName,
               financeName: data.financeName
           }
           alertUpdateShield(shield,node.parent.data.status).then(res=>{
                this.nodeExpand(data);
                this.getOrganizationTree();
            }).catch(()=>{})
        }else{
            // 编辑区间
            let interval = {
                leftLine: 1,
                rightLine: 1,
                flatSection: 1,
                name: data.name,
                status: data.status,
                pId: data.pId, 
                code:data.code,
                // dingdingCode: data.dingdingCode,
                // financeCode: data.financeCode
            }
            if(data.childNode && data.childNode.length>0){
                data.childNode.forEach(item => {
                    if(item.orgType == '3'){
                        interval.leftLine = item.status
                    }else if(item.orgType == '4'){
                        interval.rightLine = item.status
                    }else if(item.orgType == '5'){
                        interval.flatSection = item.status
                    }
                });
            } 
            alertUpdateInterval(interval,node.parent.data.status).then(res=>{
                this.nodeExpand(data);
                this.getOrganizationTree();
            }).catch(()=>{})
        }
    }
}
</script>

<style lang="scss" scoped>
@import '@/assets/scss/element-variables.scss';
.organization{
    display: flex;
    height: calc(100vh - 80px);
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
