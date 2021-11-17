<template>
    <el-dialog 
        title="添加岗位授权"
        :visible="positionDialog"
        :close-on-click-modal="false"
        custom-class="global-dialog-default"
        @close="closeDialog"
        width="600px">
        <div class="content">
            <div class="left">
                <el-input placeholder="请输入岗位名称" v-model="filterTreeText" prefix-icon="el-icon-search"></el-input>
                <el-tree 
                :data="projectTree" 
                :props="projectProps" 
                @node-click="handleProjectNodeClick"
                highlight-current
                :default-expanded-keys="defaultExpandedKeys"
                node-key="id"
                ref="projectTree"
                :filter-node-method="filterProjectTreeNode"
                v-loading="loadingTree"
                element-loading-spinner="el-icon-loading"
                ></el-tree>
            </div>
        </div>
        <span slot="footer" class="dialog-footer">
            <el-button size="mini" @click="closeDialog">关闭</el-button>
            <el-button size="mini" type="primary" @click="openMenuAuth">授权</el-button>
        </span>
        <menu-auth :menuAuthDialog.sync="menuAuthDialog" v-if="menuAuthDialog" :authParams="authParams"></menu-auth>
    </el-dialog>
</template>

<script>
import { Component, Vue, Prop, Watch } from 'vue-property-decorator'
import tableMixin from '@/mixins/tableMixin'
import menuAuth from './menuAuth'

@Component({
    name: 'authPosition',
    components:{
        menuAuth
    }
})
export default class extends tableMixin {
    @Prop() positionDialog
    @Prop() project

    loadingTree = false;
    get authParams(){
        return {
            project:this.project,
            userFlag:1,
            // userId:this.selectedPosition,
            position:[this.selectedPosition.id],
            station:{
                stationCode:this.selectedPosition ? this.selectedPosition.code : '',
                stationName:this.selectedPosition ? this.selectedPosition.name : '',
            }
        }
    }  
    filterTreeText= ''
    projectTree = []
    selectedPosition = [];
    defaultExpandedKeys = []
    projectProps= {
        children: 'children',
        label: 'name'
    }
    menuAuthDialog = false

    @Watch('filterTreeText')
    filter(val){
        this.$refs.projectTree.filter(val);
    }
   
    mounted() {
        this.getStationTreeList()
    }

    // 获取岗位列表
    getStationTreeList() {
        // this.loadingTree = true
        // this.$API.apiGetStationTree().then((res)=>{
        //     this.loadingTree = false
        //     this.projectTree = res.data
        //     if(res.data.length > 0){
        //         this.defaultExpandedKeys = [res.data[0].id]
        //     }
        // }).catch((err)=>{
        //     this.loadingTree = false
        // })
        const data = [
            {
                code: "000010001100010",
                fullname: "中铁十一局集团城市轨道工程有限公司",
                id: 101093,
                name: "城轨公司",
                order: 21,
                show: true,
                type: 1,
                virtual: false,
                children:[
                    {
                        code: "00001000110001001",
                        fullname: "中铁十一局集团城轨公司总部",
                        id: 101094,
                        name: "总部",
                        order: 1,
                        show: true,
                        type: 1,
                        virtual: true,
                        children:[]
                    }
                ]
            }
        ]
        this.projectTree = data
        if(data.length > 0){
            this.defaultExpandedKeys = [data[0].id]
        }
    }
    closeDialog() {
        this.$emit('update:positionDialog', false)
    }
    filterProjectTreeNode(value, data){
        if (!value) return true;
        return data.name.indexOf(value) !== -1;
    }
    handleProjectNodeClick(data) {
        this.selectedPosition = data;
    }
    // 授权+点击后文字颜色变化
    openMenuAuth(scope){
        if(this.$refs.projectTree.getCurrentNode() && this.selectedPosition.type == 3){
            this.menuAuthDialog = true
        }else{
            this.$message({
                type: 'info',
                message: '请选择岗位!'
            })
        }
        
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
