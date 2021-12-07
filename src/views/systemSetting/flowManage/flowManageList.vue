<template>
    <div class="organization">
        <div class="left">
            <h4>
                <div>流程管理</div>
            </h4>
            <el-tree 
            :data="projectTree" 
            :props="projectProps" 
            @node-click="handleProjectNodeClick"
            highlight-current
            node-key="menuCode"
            ref="projectTree"
            :default-expanded-keys="defaultExpandedKeys"
            :expand-on-click-node="false"
            v-loading="loadingTree"
            element-loading-spinner="el-icon-loading"
            >
            </el-tree>
        </div>
        <div class="right" v-show="isShow">
            <flow-panel :selected="selected" ref="flowPanel"></flow-panel>
        </div>
    </div>
</template>

<script>
import { Component, Vue, Watch } from 'vue-property-decorator'
import FlowPanel from '@/components/ef/panel'

@Component({
    name: 'organization',
    components: {
        FlowPanel
    }
})
export default class extends Vue {
    defaultExpandedKeys = []
    // 树loading
    loadingTree = false
    // 工程树列表
    projectTree = []
    // 树属性
    projectProps = {
        children:'children',
        label:'title'
    }
    selected = null;
    isShow = false

    created(){
        this.getRouterMenuTree();
    }
    // 获取菜单树
    getRouterMenuTree(){
        this.loadingTree = true;
        let data = {
            menuCode:this.MENU_CODE_LIST.routerMenuList
        }
        this.$API.apiGetRouterMenuTree(data).then(res=>{
            this.projectTree = this.filterTree(res.data);
            this.loadingTree = false;
        }).catch(()=>{
            this.loadingTree = false;
        })
    }
    filterTree(arr) {
        const newArr = arr.filter(item => item.hidden === '0')
        return newArr.map(item => {
            if (item.children) {
                item.children = this.filterTree(item.children)
            }
            return item
        })
    }
    // 点击树
    handleProjectNodeClick(data){
        this.isShow = true
        this.selected = data
        this.$refs.flowPanel.initData()
    }
}
</script>

<style lang="scss" scoped>
@import '@/assets/scss/element-variables.scss';
.organization{
    display: flex;
    height: calc(100vh - 130px);
    background-color: #fff;
    overflow: auto;
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
    width:250px;
    height: 100%;
    padding: 15px;
    flex-shrink: 0;
    border-right: 1px #EBEEF5 solid;
    overflow: auto;
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
    // min-width: 1200px;
    overflow: auto;
}
</style>
