<template>
    <div class="wrapper">
        <!-- 按钮组版块 -->
        <div class="global-btn-group">
            <el-button size="small" icon="iconfont iconrefresh" @click="refreshBtn">刷新</el-button>
            <div class="search iconfont iconsearch1" @click="openSearch"> 搜索</div>
        </div>
        <!-- 表格版块 -->
        <el-table
            ref="tableData"
            :data="tableData"
            :height="tableHeight"
            :border="tableConfig.border"
            v-loading="listLoading"
            :element-loading-spinner="tableConfig.loadingIcon"
            @selection-change="tableSelectionChange"
            class="global-table-default"
            style="width: 100%;">
            <el-table-column type="selection" width="55" align="center" ></el-table-column>
            <el-table-column label="序号" width="55" align="center" :show-overflow-tooltip="true">
                <template slot-scope="props">
                    <span>{{(listQuery.page-1)*listQuery.limit + props.$index + 1}}</span>
                </template>
            </el-table-column>
            <el-table-column prop="index1" label="用户ID" width="90" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="userOrgName" label="操作用户" width="350" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="userDepartName" label="操作模块" width="200" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="userPositionName" label="操作类型" width="150" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="loginIp" label="操作内容"  align="center" width="150" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="createTime" label="操作时间" width="150"  align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="operateType" label="客户端IP" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="operateContent" label="操作结果" width="300" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="userPositionName" label="结果提示" width="150" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="loginIp" label="创建人"  align="center" width="150" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="createTime" label="修改人" width="150"  align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="operateType" label="创建日期" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="operateContent" label="修改日期" width="300" align="center" :show-overflow-tooltip="true"></el-table-column>
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
        <search :searchDialog.sync="searchDialog" @searchSubmit="searchSubmit" :searchParams.sync="searchParams"></search>  
    </div>
</template>

<script>
import { Component, Vue, mixins } from 'vue-property-decorator'
import tableMixin from '@/mixins/tableMixin'
import search from './table/search'

@Component({
    name: 'operationLogList',
    components: {
        search
    }
})
export default class extends tableMixin {
    tableData = []
    listLoading = false
    selected = []
    total = 0
    searchDialog = false
    searchParams = {}
    created() {
        // this.getOperationLog();
    }
    // 查询外部用户列表
    getOperationLog(){
        var params = {};
        params.pageNum = this.listQuery.page; // 页码
        params.pageSize = this.listQuery.limit; // 每页数量
        params = Object.assign(params,this.searchParams);
        this.listLoading = true
        this.$API.apiGetOperationLog(params).then(res=>{
            this.listLoading = false
            if(res.data){
                this.tableData = res.data.list;
                this.total = res.data.total || 0;
            }
        }).catch(err=>{
            this.listLoading = false;
        })
    }

    // 刷新
    refreshBtn(){
        if(!this.listLoading){
            this.listQuery.page = 1;
            // 清除侧边栏搜索条件
            this.searchParams = {};
            // 清除表格筛选条件
            this.$refs.tableData.clearFilter();
            this.getOperationLog();
        }
    }
    // 搜索
    openSearch(){
        this.searchDialog = true
    }
    searchSubmit(){
        this.listQuery.page = 1
        this.getOperationLog();
    }
    // 表格：复选框变化时触发,删除编辑
    tableSelectionChange(value){
        this.selected = value
    }
    // 表格分页：每页显示条数变化触发
    handleSizeChange(value) {
        this.listQuery.page = 1;
        this.listQuery.limit = value;
        this.getOperationLog();
    }
    // 表格分页：当前页变化触发
    handleCurrentChange(value) {
        this.listQuery.page = value;
        this.getOperationLog();
    }
}
</script>

<style lang="scss" scoped>
    .wrapper{
        position: relative;
        height: calc(100vh - 130px);
        background-color: #f8f8f8;
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
