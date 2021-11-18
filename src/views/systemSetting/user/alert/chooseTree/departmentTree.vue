<template>
    <el-dialog
        title="选择部门"
        :visible.sync="dialog"
        :close-on-click-modal="false"
        custom-class="global-dialog-default dialog-fixed-scroll"
        @close="closeDialog"
        width="600px"
    >
        <div class="organization">
            <div class="left">
                <el-input placeholder="请输入名称" prefix-icon="el-icon-search" v-model="filterTreeText"></el-input>
                <el-tree 
                :data="projectTree"
                :props="projectProps" 
                @node-click="handleProjectNodeClick"
                highlight-current
                :default-expanded-keys = "defaultExpandedKeys"
                node-key="id"
                ref="projectTree"
                :expand-on-click-node="false"
                v-loading="loadingTree"
                element-loading-spinner="el-icon-loading"
                :filter-node-method="filterProjectTreeNode"
                >
                    <span class="custom-tree-node" slot-scope="{ node, data }">
                        <span :class="{'disabled':data.orgType!='1'}">{{node.label}}</span>
                    </span>
                </el-tree>
            </div>
        </div>
        <span slot="footer" class="dialog-footer">
        <el-button size="mini" @click="closeDialog">取消</el-button>
        <el-button size="mini" type="primary" @click="submitBtn">确定</el-button>
        </span>
    </el-dialog>
</template>

<script>
import { Component, Vue, Watch } from 'vue-property-decorator'
import tableMixin from '@/mixins/tableMixin'

@Component({
    name: 'departmentTree',
    components: {
    
    }
})
export default class extends tableMixin {
    dialog = false
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
        label:'orgName',
    }

    init(){
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
            console.log(this.projectTree)
            this.loadingTree = false
        }).catch(()=>{
            this.loadingTree = false;
        })
    }
    // 点击树
    handleProjectNodeClick(data){
        this.selected = data
    }

    // 树过滤方法
    filterProjectTreeNode(value,data){
        if (!value) return true;
        return data.orgName.indexOf(value) !== -1;
    }
    closeDialog () {
        this.dialog = false;
    }
    submitBtn () {
        if(this.selected.orgType != '1') {
            this.$message({
                message:'请选择一个部门'
            })
            return
        }
        this.promise.resolve(this.selected);
        this.dialog = false
    }
}
</script>

<style lang="scss" scoped>
@import '@/assets/scss/element-variables.scss';
.organization{
    display: flex;
    // height: calc(100vh - 80px);
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
        .disabled {
            color: #aaa;
            cursor: not-allowed;
        }
        span>{
            i{
                margin-left: 10px;
            }
        }
    }
    
}
.left{
    width:100%;
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
</style>