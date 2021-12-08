<template>
    <el-dialog 
        title="功能菜单授权"
        :visible="menuAuthDialog"
        :close-on-click-modal="false"
        custom-class="global-dialog-default"
        @close="closeDialog"
        append-to-body
        width="1000px">
        <div class="content">
            <el-table
                :data="tableData"
                style="width: 100%;margin-bottom: 20px;"
                row-key="id"
                class="global-table-default"
                border
                v-loading="loadingTree"
                default-expand-all
                :tree-props="{children: 'children', hasChildren: 'hasChildren'}">
                <el-table-column prop="title" label="授权菜单" width="300">
                    
                </el-table-column>
                <el-table-column prop="edit" label="编辑" align="center">
                    <template slot-scope="scope">
                        <el-checkbox 
                            v-if="scope.row.comButton.includes(menuButtons[0]) && scope.row.parentCode"
                            v-model="scope.row.selected[menuButtons[0]]"
                            @change="changeChild(scope.row,menuButtons[0])"
                        ></el-checkbox>
                        <el-checkbox 
                            v-if="!scope.row.parentCode"
                            :indeterminate="scope.row.isIndeterminate[menuButtons[0]]"
                            v-model="scope.row.selected[menuButtons[0]]"
                            @change="changeParent($event,scope.row,menuButtons[0])"
                        ></el-checkbox>
                    </template>
                </el-table-column>
                <el-table-column prop="new" label="删除" align="center">
                    <template slot-scope="scope">
                        <el-checkbox 
                            v-if="scope.row.comButton.includes(menuButtons[1]) && scope.row.parentCode"
                            v-model="scope.row.selected[menuButtons[1]]"
                            @change="changeChild(scope.row,menuButtons[1])"
                        ></el-checkbox>
                        <el-checkbox 
                            v-if="!scope.row.parentCode"
                            :indeterminate="scope.row.isIndeterminate[menuButtons[1]]"
                            v-model="scope.row.selected[menuButtons[1]]"
                            @change="changeParent($event,scope.row,menuButtons[1])"
                        ></el-checkbox>
                    </template>
                </el-table-column>
                <el-table-column prop="approval" label="审批" align="center">
                    <template slot-scope="scope">
                        <el-checkbox 
                            v-if="scope.row.comButton.includes(menuButtons[2]) && scope.row.parentCode"
                            v-model="scope.row.selected[menuButtons[2]]"
                            @change="changeChild(scope.row,menuButtons[2])"
                        ></el-checkbox>
                        <el-checkbox 
                            v-if="!scope.row.parentCode"
                            :indeterminate="scope.row.isIndeterminate[menuButtons[2]]"
                            v-model="scope.row.selected[menuButtons[2]]"
                            @change="changeParent($event,scope.row,menuButtons[2])"
                        ></el-checkbox>
                    </template>
                </el-table-column>
                <el-table-column prop="detail" label="查看" align="center">
                    <template slot-scope="scope">
                        <el-checkbox 
                            v-if="scope.row.comButton.includes(menuButtons[3]) && scope.row.parentCode"
                            v-model="scope.row.selected[menuButtons[3]]"
                            @change="changeChild(scope.row,menuButtons[3])"
                        ></el-checkbox>
                        <el-checkbox 
                            v-if="!scope.row.parentCode"
                            :indeterminate="scope.row.isIndeterminate[menuButtons[3]]"
                            v-model="scope.row.selected[menuButtons[3]]"
                            @change="changeParent($event,scope.row,menuButtons[3])"
                        ></el-checkbox>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <span slot="footer" class="dialog-footer">
            <el-button size="mini" @click="closeDialog">取消</el-button>
            <el-button size="mini" type="primary" @click="submit" :loading="loadingBtn == 1">授权</el-button>
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

    loadingBtn = 0;
    loadingTree = false
    tableData = []
    menuButtons = ['a10002','a10001','a10004','a10003']
    menuButtonParams = []
    created(){
        this.getRouterMenuTree()
    }
    mounted() {
    }
    getRouterMenuTree() {
        let data = {
            menuCode:this.MENU_CODE_LIST.routerMenuList
        }
        this.loadingTree = true
        this.$API.apiGetRouterMenuTree(data).then(res=>{
            this.tableData = this.filterTree(res.data)
            this.getAuthUserMenu()
        }).catch(()=>{
            this.loadingTree = false
        })
    }
    getAuthUserMenu() {
        const data = {
            organizationId:this.editParams.organizationId,
            userId:this.editParams.userId
        }
        this.$API.apiGetAuthUserMenu(data).then(res=>{
            this.loadingTree = false
            res.data.forEach(item=>{
                var obj = this.findOne(this.tableData,item.menuCode)
                obj?.comButton.split(',').forEach((o,index)=>{
                    if(o && item.authorityButtonCode.includes(o)) {
                        obj.selected[o] = true
                    }
                })
            })
            // 勾选父级
            this.tableData.forEach(item=>{
                this.changeChild({parentCode:item.menuCode},this.menuButtons[0])
                this.changeChild({parentCode:item.menuCode},this.menuButtons[1])
                this.changeChild({parentCode:item.menuCode},this.menuButtons[2])
                this.changeChild({parentCode:item.menuCode},this.menuButtons[3])
            })
        }).catch(()=>{
            this.loadingTree = false
        })
    }
    findOne(objects, menuCode) {
        const queue = [...objects]
        while (queue.length) {
            const o = queue.shift()
            if (o.menuCode == menuCode) return o
            queue.push(...(o.children || []))
        }
    }
    filterTree(arr) {
        const newArr = arr.filter(item => item.hidden === '0')
        return newArr.map(item => {
            item.selected = {}
            item.isIndeterminate = {}
            item.comButton.split(',').forEach(c=>{
                if(c) {
                    item.selected[c] = false
                    item.isIndeterminate[c] = false
                }
            })
            if (item.children) {
                item.children = this.filterTree(item.children)
            }
            return item
        })
    }
    changeParent(e,row,menuCode) {
        row.isIndeterminate[menuCode] = false
        if(e) {
            let filterChild = row.children.filter(item=>item.comButton.includes(menuCode))
            filterChild.forEach(item=>{
                item.selected[menuCode] = true
            })
        }else {
            let filterChild = row.children.filter(item=>item.comButton.includes(menuCode))
            filterChild.forEach(item=>{
                item.selected[menuCode] = false
            })
        }
    }
    changeChild(row,menuCode) {
        let parentData = this.tableData.filter(item=>item.menuCode == row.parentCode)[0]
        
        let curFilter = parentData.children.filter(item=>item.comButton&&item.comButton.includes(menuCode))
        const allSelected = curFilter.every(item=>{
            return item.selected[menuCode]
        })
        const allNotSelected = curFilter.every(item=>{
            return !item.selected[menuCode]
        })
        if(allSelected) {
            parentData.selected[menuCode] = true
            parentData.isIndeterminate[menuCode] = false
        }else if(allNotSelected) {
            parentData.selected[menuCode] = false
            parentData.isIndeterminate[menuCode] = false
        }else {
            parentData.isIndeterminate[menuCode] = true
        }
    }
    closeDialog() {
        this.$emit('update:menuAuthDialog', false);
    }
    getParams(arr) {
        const newArr = arr.filter(item => item.hidden === '0')
        newArr.forEach(item => {
            let flag = Object.keys(item.selected).some(v=>item.selected[v])
            if(flag) {
                this.menuButtonParams.push({
                    authorityButtonCode:Object.keys(item.selected).filter(v=>item.selected[v]).join(','),
                    menuCode:item.menuCode
                })
            }
            if (item.children) {
                item.children = this.getParams(item.children)
            }
        })
    }
    submit() {
        var data = JSON.parse(JSON.stringify(this.tableData))
        this.menuButtonParams = []
        this.getParams(data)
        if(this.menuButtonParams.length<=0) {
            this.$message({
                message:'请先选择授权菜单'
            })
            return
        }
        const params = {
            menuCode:this.MENU_CODE_LIST.authorizationList,
            creatorOrgId : this.$store.getters.currentOrganization.organizationId,
            creatorOrgName : this.$store.getters.currentOrganization.organizationName,
            organizationId:this.editParams.organizationId,
            userFlag:this.editParams.userFlag,
            userId:this.editParams.userId,
            menuButton:this.menuButtonParams
        }
        this.loadingBtn = 1
        this.$API.apiUpdateAuthUserMenu(params).then(res=>{
            this.loadingBtn = 0
            this.$message({
                type:'success',
                message:'授权成功'
            })
            this.$emit('menuAuth')
        }).catch(()=>{
            this.loadingBtn = 0
        })
    }
}
</script>

<style lang="scss" scoped>
/deep/.el-dialog{
    height: auto;
    .el-dialog__body {
        height: 70vh;
        overflow: auto;
    }
}
</style>
