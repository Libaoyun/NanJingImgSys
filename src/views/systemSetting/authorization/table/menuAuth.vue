<template>
    <el-dialog 
        title="功能菜单授权"
        :visible="menuAuthDialog"
        :close-on-click-modal="false"
        custom-class="global-dialog-default"
        @close="closeDialog"
        append-to-body
        width="500px">
        <!-- 这里的插槽会替换title显示的内容 -->
        <div slot="title">
            <span>功能菜单授权</span>
            <el-popover
                placement="bottom-start"
                title="权限的定义"
                width="480"
                trigger="hover">
                <div style="font-size: 13px;line-height:26px">
                    <div>① 查询：组合权限 (查看、导出、打印、刷新)</div>
                    <div>② 新建：组合权限 (查看、新建、编辑、提交、撤销、导出、打印、刷新)</div>
                    <div>③ 编辑：组合权限 (查看、编辑、删除、提交、撤销、导出、打印、刷新)</div>
                    <div>④ 审批：组合权限 (查看、审批、提交、撤销、导出、打印、刷新)</div>
                </div>
                <i class="el-icon-question" slot="reference"></i>
            </el-popover>
        </div>
        <div class="content">
            <div class="left">
                <el-tree 
                :data="projectTree" 
                :props="projectProps" 
                show-checkbox
                v-loading="loadingTree"
                :default-expanded-keys="[1,13,15]"
                :default-checked-keys="defaultCheckedKeys"
                element-loading-spinner="el-icon-loading"
                node-key="menuCode"
                ref="projectTree"
                ></el-tree>
            </div>
        </div>
        <span slot="footer" class="dialog-footer">
            <el-button size="mini" @click="closeDialog">取消</el-button>
            <el-button size="mini" type="primary" @click="submit" :loading="loadingBtn == 1">确定</el-button>
        </span> 
    </el-dialog>
</template>

<script>
import { Component, Vue, Prop, Watch } from 'vue-property-decorator'
import tableMixin from '@/mixins/tableMixin'
import { filterCheckedTree } from '@/utils/index'
import eventBus from '@/utils/eventBus.js'

@Component({
    name: 'menuAuth'
})
export default class extends tableMixin {
    @Prop() menuAuthDialog
    @Prop() editParams
    @Prop() authParams

    loadingBtn = 0;
    loadingTree = false
    projectTree = [
        // { id: 1, label: '首页' },
        // { id: 2, label: '设备调拨' ,children:[
        //     { id: 21, label: '设备需求登记' ,parentId:2,children:[
        //         { id: 211, label: '查询',parentId:21 },
        //         { id: 212, label: '新建' ,selected:true,parentId:21},
        //         { id: 213, label: '编辑',parentId:21 },
        //         { id: 214, label: '审批' ,parentId:21},
        //     ]},
        //     { id: 22, label: '设备调拨' ,selected:true,parentId:2,children:[
        //         { id: 221, label: '查询' ,parentId:22},
        //         { id: 222, label: '新建（仅公司用户）' ,parentId:22},
        //         { id: 223, label: '编辑（仅公司用户）' ,parentId:22},
        //         { id: 224, label: '审批' ,parentId:22},
        //     ]},
        //     { id: 23, label: '设备闲置预警',parentId:2 ,children:[
        //         { id: 231, label: '查询' ,selected:true,parentId:23},
        //         { id: 232, label: '编辑' ,parentId:23},
        //     ]}
        // ]},
        // { id: 3, label: '业务登记' },
        // { id: 4, label: '设备使用' },
        // { id: 5, label: '配件材料管理' },
        // { id: 6, label: '车辆管理' },
        // { id: 7, label: '成本中心' },
        // { id: 8, label: '报表中心' },
        // { id: 9, label: '系统管理' },
    ]
    // projectTree = []
    projectProps= {
        children: 'children',
        label: 'title'
    }
    defaultCheckedKeys = []
    created(){
       this.getAuthUserMenu();
    }
    mounted() {
        // this.defaultCheckedKeys = filterCheckedTree(this.projectTree,[])
        // console.log('defaultCheckedKeys',this.defaultCheckedKeys)
    }
    closeDialog() {
        this.$emit('update:menuAuthDialog', false);
    }
    getAuthUserMenu(){
        this.loadingTree = true;
        this.$API.apiGetAuthUserMenu(this.editParams).then((res)=>{
            this.projectTree = res.data;
            this.defaultCheckedKeys = filterCheckedTree(this.projectTree,[])
            this.loadingTree = false;
        }).catch((err)=>{
            this.loadingTree = false;
        })
    }
    // 获取授权的菜单按钮
    getMenuButton(){
        // 拼接需要的数据结构
        var selected = this.$refs.projectTree.getCheckedNodes() || [];
        var halfSelected = this.$refs.projectTree.getHalfCheckedNodes() || [];
        var result = [...selected,...halfSelected];
        var params = [];
        result.forEach(item=>{
            params.push({
                authorityButtonCode:item.authorityButtonCode|| '',
                menuCode:item.authorityButtonCode?item.parentCode : item.menuCode
            })
        })
        return params;
        // var temp = {};
        // selected.forEach(item => {
        //     temp[item.menuCode] = item; 
        // });
        // var menuButton = [];
        // var result = {};
        // selected.forEach(item=>{
        //     if(!item.children || item.children.length == 0){
        //         let parentCode = item.parentCode;
        //         result[parentCode] || (result[parentCode]=[]);
        //         result[parentCode].push(item.menuCode);
        //     }
        // })
        // for(var key in result){
        //     var item = menuButton[menuButton.length] = {};
        //     item.authorityButtonCode = result[key];
        //     item.menuCode = key;
        // }
        // return menuButton;
    }
    submit() {
        if(this.$refs.projectTree.getCheckedNodes().length == 0){
            this.$message({
                type: 'info',
                message: '请选择功能菜单!'
            })
            return
        }
        // console.log(this.authParams);
        if(this.editParams){
            // 提交编辑
            var params = {};
            params.menuButton = this.getMenuButton();
            params = Object.assign(params,this.editParams);
            this.loadingBtn = 1;
            this.$API.apiUpdateAuthUserMenu(params).then(res=>{
                this.$message({
                    type:'success',
                    message:'授权成功!'
                })
                this.loadingBtn = 0;
                this.$emit('update:menuAuthDialog',false);
            }).catch(()=>{
                this.loadingBtn = 0;
            })
        }else{
            
            // 新增
            var params = Object.assign(this.authParams,{menuButton:this.getMenuButton()});
            this.loadingBtn = 1;
            this.$API.apiCreateAuthorizeUser(params).then(res=>{
                this.loadingBtn = 0;
                this.$message({
                    type:'success',
                    message:'授权成功!'
                })
                eventBus.$emit('refreshList');
                this.$emit('update:menuAuthDialog',false);
            }).catch(()=>{
                this.loadingBtn = 0;
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
        border: 1px #ddd solid;
    }
}
</style>
