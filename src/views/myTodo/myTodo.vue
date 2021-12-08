<template>
    <div class="wrapper">
          <el-tabs v-model="activeName" @tab-click="tabClick">
            <el-tab-pane label="待办列表" name="first">
                <!-- 表格版块 -->
                <el-table
                    ref="tableData"
                    :data="tableTodoData"
                    :height="tableHeight"
                    :border="tableConfig.border"
                    v-loading="listLoading"
                    :element-loading-spinner="tableConfig.loadingIcon"
                    class="global-table-default"
                    style="width: 100%;">
                    <el-table-column label="序号" type="index" width="50" align="center">
                        <template slot-scope="scope">
                            <span>{{(listQuery.page-1)*listQuery.limit + scope.$index + 1}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="serialNumber" label="单据编号" width="150" align="center" :show-overflow-tooltip="true">
                        <template slot-scope="props">
                            <el-button type="text" size="small" @click="approveBtn(props.row)">{{props.row.serialNumber}}</el-button>
                        </template>
                    </el-table-column>
                    <el-table-column prop="titleName" label="所属模块" width="200" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="createUser" label="发起人" width="80" align="center" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="createTime" label="发起时间" width="150" align="center" :show-overflow-tooltip="true">
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
                        :total="todoTotal">
                    </el-pagination>
                </div>
            </el-tab-pane>
            <el-tab-pane label="已办列表" name="second">
                <!-- 表格版块 -->
                <el-table
                    ref="tableData"
                    :data="tableDoneData"
                    :height="tableHeight"
                    :border="tableConfig.border"
                    v-loading="listLoading"
                    :element-loading-spinner="tableConfig.loadingIcon"
                    class="global-table-default"
                    style="width: 100%;">
                    <el-table-column label="序号" type="index" width="50" align="center">
                        <template slot-scope="scope">
                            <span>{{(listQuery.page-1)*listQuery.limit + scope.$index + 1}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="serialNumber" label="流水号" width="150" align="center" :show-overflow-tooltip="true">
                        <template slot-scope="props">
                            <el-button type="text" size="small" @click="detailBtn(props.row)">{{props.row.serialNumber}}</el-button>
                        </template>
                    </el-table-column>
                    <el-table-column prop="titleName" label="任务分类" width="200" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="createUser" label="发起人" width="80" align="center" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="createTime" label="记录时间" width="180" align="center" :show-overflow-tooltip="true">
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
                        :total="doneTotal">
                    </el-pagination>
                </div>
            </el-tab-pane>
        </el-tabs>
        
    </div>
</template>

<script>
import { Component, Vue, mixins } from 'vue-property-decorator'
import tableMixin from '@/mixins/tableMixin'

@Component({
    name: 'userList',
    components: {
    }
})
export default class extends tableMixin {
    tableTodoData = []
    tableDoneData = []
    listLoading = false
    todoTotal = 0
    doneTotal = 0
    searchDialog = false
    loadingBtn = 0
    activeName="first"

    created() {
        this.getList();
    }
    // 查询外部用户列表
    getList(){
        if(this.activeName == 'first'){
            this.getTodoList()
        }else{
            this.getDoneList()
        }
    }
    getTodoList() {
        let params = {};
        params.pageNum = this.listQuery.page; // 页码
        params.pageSize = this.listQuery.limit; // 每页数量
        this.listLoading = true
        this.$API.apiGetTodoList(params).then(res=>{
            this.listLoading = false;
            this.tableTodoData = res.data.list
            this.todoTotal = res.data.total
        }).catch(err=>{
            this.listLoading = false;
        })
    }
    getDoneList() {
        let params = {};
        params.pageNum = this.listQuery.page; // 页码
        params.pageSize = this.listQuery.limit; // 每页数量
        this.listLoading = true
        this.$API.apiGetDoneList(params).then(res=>{
            this.listLoading = false
            this.tableDoneData = res.data.list
            this.doneTotal = res.data.total
        }).catch(err=>{
            this.listLoading = false;
        })
    }
    tabClick() {
        this.listQuery.page = 1;
        this.getList();
    }
    approveBtn(data){
        this.$router.push({name: data.approveName, params: {
                routerName: 'myTodo',
                businessId: data.businessId,
                waitId:data.waitId,
            }
        })
    }
    detailBtn(data){
        this.$router.push({name: data.detailName, params: {
                routerName: 'myTodo',
                businessId: data.businessId,
            }
        })
    }
    // 表格分页：每页显示条数变化触发
    handleSizeChange(value) {
        this.listQuery.page = 1;
        this.listQuery.limit = value;
        this.getList();
    }
    // 表格分页：当前页变化触发
    handleCurrentChange(value) {
        this.listQuery.page = value;
        this.getList();
    }
}
</script>

<style lang="scss" scoped>
    .wrapper{
        position: relative;
        height: calc(100vh - 130px);
        background-color: #f8f8f8;
        padding: 15px;
        padding-top: 0;
        ::v-deep{
            .el-tabs__content{
                overflow: visible;
            }
        }
        
        .pagination-wrapper{
            position: absolute;
            bottom: -43px;
            display: flex;
            width: 100%;
            justify-content: center;
        }
    }
</style>
