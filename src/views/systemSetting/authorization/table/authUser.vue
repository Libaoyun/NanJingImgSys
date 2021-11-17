<template>
    <el-dialog 
        title="添加授权人员"
        :visible="userDialog"
        :close-on-click-modal="false"
        custom-class="global-dialog-default"
        @close="closeDialog"
        width="1000px">
        <div class="content">
            <div class="right">
                <div class="search-group">
                    <el-input placeholder="姓名" v-model="search.name"></el-input>
                    <div class="searchBtn iconfont iconsearch1" @click="listQuery.page = 1;getStationUserList()"></div>
                </div>
                <el-table
                v-loading="listLoading"
                :data="userList"
                :max-height="395"
                :element-loading-spinner="tableConfig.loadingIcon"
                :border="tableConfig.border"
                @selection-change="tableSelectionChange"
                class="global-table-default"
                style="width: 100%;">
                    <el-table-column type="selection" width="55" align="center" ></el-table-column>
                    <el-table-column label="序号" type="index" width="50" align="center">
                        <template slot-scope="scope">
                            <span>{{(listQuery.page-1)*listQuery.limit + scope.$index + 1}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="name" label="姓名" align="center" :show-overflow-tooltip="true" width="100"></el-table-column>
                    <el-table-column prop="gender" label="性别" align="center" :show-overflow-tooltip="true" width="60">
                        <template slot-scope="props">
                            <!--1是男2是女-->
                            {{props.row.gender==1?'男':'女'}}
                        </template>
                    </el-table-column>
                    <el-table-column prop="positionPath" label="所属组织" align="center" :show-overflow-tooltip="true"></el-table-column>
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
        </div>
        <span slot="footer" class="dialog-footer">
            <el-button size="mini" @click="closeDialog">取消</el-button>
            <el-button size="mini" type="primary" @click="addAuthUser">确定</el-button>
        </span>
        <menu-auth :menuAuthDialog.sync="menuAuthDialog" v-if="menuAuthDialog" :authParams="authParams"></menu-auth>
    </el-dialog>
</template>

<script>
import { Component, Vue, Prop, Watch } from 'vue-property-decorator'
import tableMixin from '@/mixins/tableMixin'
import menuAuth from './menuAuth'

@Component({
    name: 'authUser',
    components:{
        menuAuth
    }
})
export default class extends tableMixin {
    @Prop() userDialog
    @Prop() project

    get authParams(){
        return {
            project:this.project,
            userFlag:0,
            userId:this.clickedId,
            station:{
                stationCode:this.currentProject ? this.currentProject.code : '',
                stationName:this.currentProject ? this.currentProject.name : '',
            }
        }
    }   
    listLoading = false;
    userList = []
    total = 0
    search={
        name: ''
    }
    clickedId = null
    menuAuthDialog = false
    currentProject = null
    selected = []

    mounted() {
        this.getStationUserList()
    }

    // 获取岗位用户
    getStationUserList() {
        // this.listLoading = true
        // let params = {
        //     departmentType: this.currentProject ? this.currentProject.type : '',
        //     stationId: this.currentProject ? this.currentProject.id : ''
        // }
        // params = Object.assign(params,this.search);
        // this.$API.apiGetStationUser(params).then((res)=>{
        //     this.listLoading = false
        //     this.userList = res.data
        //     this.total = res.data.length
        // }).catch((err)=>{
        //     this.listLoading = false
        // })

        this.userList = [
            {
                catagory: 101301,
                gender: 1,
                id: 462218,
                mainPosition: true,
                name: "陈柯",
                order: 3,
                positionPath: "中铁十一局/城轨公司/总部/领导人员/党委副书记、工会主席",
                positionStatus: 101401
            }
        ]
    }
    // 表格：复选框变化时触发,删除编辑
    tableSelectionChange(value){
        this.selected = value
    }
    addAuthUser() {
        if(this.selected.length === 0) {
            this.$message({
                message:'请选择授权人员'
            })
            return
        }
        this.$emit('addAuthUser',this.selected)
    }
    closeDialog() {
        this.$emit('update:userDialog', false)
        this.clickedId = null
    }
    // 表格分页：每页显示条数变化触发
    handleSizeChange(value) {
        this.listQuery.limit = value
    }
    // 表格分页：当前页变化触发
    handleCurrentChange(value) {
        this.listQuery.page = value
    }
    // 授权+点击后文字颜色变化
    openMenuAuth(scope){
        this.clickedId = scope.row.id
        this.menuAuthDialog = true
    }
}
</script>

<style lang="scss" scoped>
.content{
    display: flex;
    height: 100%;
    width: 100%;
    .left{
        width: 300px;
        padding: 15px;
        height: 470px;
        overflow: auto;
        border: 1px #ddd solid;
        .el-input{
            width: 90%;
            margin-left: 5%;
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
    .right{
        position: relative;
        flex: 1;
        padding: 15px;
        height: 470px;
        margin-left: 15px;
        border: 1px #ddd solid;
        .search-group{
            margin-bottom: 15px;
            text-align: right;
            .el-input{
                display: inline-block;
                width: 150px;
                margin-right: 10px;
                &::v-deep{
                    .el-input__inner{
                        width: 150px;
                        height: 30px;
                        line-height: 30px;
                    }
                }
                
            }
            .searchBtn{
                display: inline-block;
                font-size: 16px;
                cursor: pointer;
            }
            
        }
        .pagination-wrapper{
            position: absolute;
            left: 0;
            bottom: 15px;
            display: flex;
            width: 100%;
            justify-content: center;
        }
        .clickedColor{
            color: #ccc;
        }
    }
}
</style>
