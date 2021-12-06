<template>
    <el-dialog 
        title="选择部门"
        :visible.sync="dialog"
        :close-on-click-modal="false"
        custom-class="global-dialog-default"
        @close="closeDialog"
        width="600px">
        <div class="content">
            <div class="left">
                <el-input placeholder="请输入组织名称" v-model="filterTreeText" prefix-icon="el-icon-search"></el-input>
                <el-tree 
                :data="projectTree" 
                :props="projectProps" 
                highlight-current
                :default-expanded-keys="defaultExpandedKeys"
                node-key="id"
                show-checkbox
                @check="checkNode"
                ref="projectTree"
                :filter-node-method="filterProjectTreeNode"
                v-loading="loadingTree"
                element-loading-spinner="el-icon-loading"
                ></el-tree>
            </div>
        </div>
        <span slot="footer" class="dialog-footer">
            <el-button size="mini" @click="closeDialog">取消</el-button>
            <el-button size="mini" type="primary" @click="choosePosition">选择</el-button>
        </span>
    </el-dialog>
</template>

<script>
import { Component, Vue, Prop, Watch } from 'vue-property-decorator'
import tableMixin from '@/mixins/tableMixin'

@Component({
    name: 'handlePost',
    components:{
    }
})
export default class HandlePost extends tableMixin {
    dialog = false
    project = {}

    loadingTree = false;
    filterTreeText= ''
    projectTree = []
    selectedPosition = [];
    defaultExpandedKeys = []
    projectProps= {
        children: 'children',
        label: 'orgName',
        disabled:(data,node) => {
            return false
        }
    }
    menuAuthDialog = false
    selected = []

    @Watch('filterTreeText')
    filter(val){
        this.$refs.projectTree.filter(val);
    }
   
    init() {
        this.getStationTreeList()
    }

    // 获取岗位列表
    getStationTreeList() {
        this.loadingTree = true
        this.$API.apiGetOrganizationTree().then((res)=>{
            this.loadingTree = false
            this.projectTree = res.data
            if(res.data.length > 0){
                this.defaultExpandedKeys = [res.data[0].id]
            }
        }).catch((err)=>{
            this.loadingTree = false
        })
    }
    checkNode(node,selectedNodes) {
        this.selected = selectedNodes.checkedNodes || []
        this.selected.filter(item=>item.orgType == '2')
    }
    choosePosition() {
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
}
</script>

<style lang="scss" scoped>
.content{
    .left{
        padding: 15px;
        height: 470px;
        overflow: auto;
        .el-input{
            width: 100%;
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
}
</style>
