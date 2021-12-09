<template>
    <div class="wrapper">
        <!-- 按钮组版块 -->
        <div class="global-btn-group">
            <el-button type="primary" size="small" icon="iconfont icon-icon_add" @click="createBtn" v-checkPermission="'create'">新建</el-button>
            <el-button size="small" icon="iconfont icon-bianji1" @click="editBtn" v-checkPermission="'edit'">编辑</el-button>
            <loading-btn size="small" icon="iconfont icon-icon-delete" @click="deleteBtn(1)" :loading="loadingBtn" v-checkPermission="'delete'">删除</loading-btn>
            <!-- <el-dropdown style="margin:0 10px">
                <el-button size="small" icon="iconfont icon-daochu">
                    导出<i class="el-icon-arrow-down el-icon--right"></i>
                </el-button>
                <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item  @click.native="exportPdfBtn">PDF</el-dropdown-item>
                    <el-dropdown-item  @click.native="exportExcelBtn">Excel</el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
            <el-button size="small" icon="iconfont icon-dayinji_o" @click="printBtn">打印</el-button> -->
            <el-button size="small" icon="iconfont icon-icon_refresh" @click="refreshBtn">刷新</el-button>
            <!-- <div class="search iconfont icon-sousuo" @click="openSearch"> 搜索</div> -->
        </div>
        <!-- 表格版块 -->
        <el-table
            ref="tableData"
            :data="tableData"
            row-key="id"
            :height="tableHeight"
            :border="tableConfig.border"
            v-loading="listLoading"
            :element-loading-spinner="tableConfig.loadingIcon"
            @filter-change="filterChange"
            @selection-change="tableSelectionChange"
            class="global-table-default"
            style="width: 100%;">
            <el-table-column type="selection" width="55" align="center" :reserve-selection="true"></el-table-column>
            <el-table-column label="序号" type="index" width="55" align="center">
                <template slot-scope="scope">
                <span>{{(listQuery.page-1)*listQuery.limit + scope.$index + 1}}</span>
                </template>
            </el-table-column>
            <el-table-column prop="fileName" label="附件名称" width="180" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="fileFormat" label="文件格式" width="180" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="fileSize" label="文件大小" width="180" align="center" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                    {{scope.row.fileSize | formatSize}}
                </template>
            </el-table-column>
            <el-table-column prop="status" label="是否启用" width="100" align="center" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                    {{['否','是'][scope.row.status]}}
                </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" width="100" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="createUser" label="编制人" width="100" align="center" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column prop="createTime" label="编制时间" width="180" align="center" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                    {{ scope.row.createTime | formatDate }}
                </template>
            </el-table-column>
            <el-table-column label="操作" width="90" align="center">
                <template slot-scope="scope">
                    <el-button type="text" class="el-icon-download downloadBtn" @click="downloadFile(scope)">下载</el-button>
                </template>
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
                :total="total">
            </el-pagination>
        </div>
        <!-- <search :searchDialog.sync="searchDialog" @searchSubmit="searchSubmit" :searchParams.sync="searchParams"></search>   -->
        <create v-if="createDialog" :createDialog.sync="createDialog" @createBookFile="createBookFile"></create>
        <edit v-if="editDialog" :editDialog.sync="editDialog" @editBookFile="editBookFile" :fileInfo="selectedFileInfo"></edit>
    </div>
</template>

<script>
import { Component, Vue, mixins } from 'vue-property-decorator'
import tableMixin from '@/mixins/tableMixin'
import create from './table/create.vue'
import edit from './table/edit.vue'
// import search from './table/search'

@Component({
    name: 'onlineManualsList',
    components: {
        // search,
        create,
        edit
    }
})
export default class extends tableMixin {
    tableData = []
    filterParams = {}
    listLoading = false
    selected = []
    total = 0
    searchDialog = false
    loadingBtn = 0
    searchParams = {}
    createDialog = false
    editDialog = false
    selectedFileInfo = {
        status:'1',
        remark: '',
        fileList:[]
    }

    created() {
        this.getFileList();
    }
    //复选框选中的id值
    get idList(){
        var list = [];
        this.selected.forEach(item=>{
            list.push(item.businessId)
        })
        return list;
    };
    getFileList(){
        var params = {};
        params.pageNum = this.listQuery.page; // 页码
        params.pageSize = this.listQuery.limit; // 每页数量
        params = Object.assign(params,this.searchParams,this.filterParams);
        this.listLoading = true
        this.$API.apiGetFileList(params).then(res=>{
            this.listLoading = false
            if(res.data){
                this.tableData = res.data.list;
                this.total = res.data.total || 0;
            }
        }).catch(err=>{
            this.listLoading = false;
            this.$message({
                type:'error',
                message:err
            })
        })
    }
    createBtn() {
        this.createDialog = true
    }
    editBtn() {
        if(this.selected.length !== 1){
            this.$message({
                type: 'info',
                message: '请选择一条记录!'
            })
            return
        }
        this.selectedFileInfo = JSON.parse(JSON.stringify(this.selected[0]))
        this.selectedFileInfo.fileList = [
            {
                businessId:this.selectedFileInfo.businessId,
                fileExt:this.selectedFileInfo.fileExt,
                fileName:this.selectedFileInfo.fileName,
                filePath:this.selectedFileInfo.filePath,
                fileSize:this.selectedFileInfo.fileSize,
                id:this.selectedFileInfo.id,
                type:this.selectedFileInfo.type,
                uploadUserId:this.selectedFileInfo.uploadUserId,
                uploadUserName:this.selectedFileInfo.uploadUserName,
                url:this.selectedFileInfo.url,
            }
        ]
        this.editDialog = true
    }
    // 新增
    createBookFile(data) {
        data.menuCode = this.MENU_CODE_LIST.onlineManualsList
        data.creatorOrgId = this.$store.getters.currentOrganization.organizationId,
        data.creatorOrgName = this.$store.getters.currentOrganization.organizationName,
        this.$API.apiAddBoookFile(data).then(res=>{
            this.$message({
                type:'success',
                message:'新增成功'
            })
            this.createDialog = false;
            this.listQuery.page = 1;
            this.getFileList()
        }).catch(()=>{})
    }
    // 编辑
    editBookFile(data) {
        data.menuCode = this.MENU_CODE_LIST.onlineManualsList
        data.creatorOrgId = this.$store.getters.currentOrganization.organizationId,
        data.creatorOrgName = this.$store.getters.currentOrganization.organizationName,
        console.log('拿到的数据:',data);
        this.$API.apiUpdateBoookFile(data).then(res=>{
            this.$message({
                type:'success',
                message:'编辑成功'
            })
            this.editDialog = false;
            this.listQuery.page = 1;
            this.getFileList()
        }).catch(()=>{
        })
    }
    // 删除
    deleteBtn(loadingBtnIndex){
        if(this.selected.length == 0){
            this.$message({
                type: 'info',
                message: '请至少选择一条记录!'
            })
            return
        }
        this.$confirm('确定删除已选择的记录, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then((e) => {
            const params = {
                creatorOrgId:this.$store.getters.currentOrganization.organizationId,
                creatorOrgName:this.$store.getters.currentOrganization.organizationName,
                menuCode:this.MENU_CODE_LIST.onlineManualsList,
                businessIdList:this.idList
            }
          this.loadingBtn = loadingBtnIndex
          this.$API.apiDeleteBoookFile(params).then(res=>{
            this.loadingBtn = 0
            this.$message({
              type: 'success',
              message: '删除成功!'
            });
            this.resetPageNum();
            this.$refs.tableData.clearSelection();
            this.getFileList();
          }).catch(()=>{
              this.loadingBtn = 0;
          })
        }).catch(() => {    
          this.loadingBtn = 0     
        });
    }
    // 导出excel
    exportExcelBtn(){
        var data = {
            creatorOrgId : this.$store.getters.currentOrganization.organizationId,
            creatorOrgName : this.$store.getters.currentOrganization.organizationName,
            idList:this.idList,
            menuCode:this.MENU_CODE_LIST.onlineManualsList
        };
        this.EXPORT_FILE(this.selected,'excel',{url:'/rdexpense/organization/exportExcel',data});
    }
    // 导出pdf
    exportPdfBtn(){
        var data = {
            creatorOrgId : this.$store.getters.currentOrganization.organizationId,
            creatorOrgName : this.$store.getters.currentOrganization.organizationName,
            idList:this.idList,
            menuCode:this.MENU_CODE_LIST.onlineManualsList
        }
        this.EXPORT_FILE(this.selected,'pdf',{url:'/rdexpense/organization/exportPDF',data});
    }
    // 打印
    printBtn(){
        var data = {
            creatorOrgId : this.$store.getters.currentOrganization.organizationId,
            creatorOrgName : this.$store.getters.currentOrganization.organizationName,
            idList:this.idList,
            menuCode:this.MENU_CODE_LIST.onlineManualsList
        }
        this.EXPORT_FILE(this.selected,'print',{url:'/rdexpense/organization/exportPDF',data});
    }
    // 下载
    downloadFile(scope){
        var a = document.createElement('a')
        // var event = new MouseEvent('click')
        a.download = scope.row.fileName
        a.href = scope.row.fileUrl;
        a.target = '_blank'
        a.click()
    }
    // 刷新
    refreshBtn(){
        if(!this.listLoading){
            this.listQuery.page = 1;
            // 清除侧边栏搜索条件
            this.searchParams = {};
            // 清除表格筛选条件
            this.$refs.tableData.clearFilter();
            this.filterParams = {};
            // 清除多选表格选中
            this.$refs.tableData.clearSelection();
            this.getFileList();
        }
    }
    // 搜索
    openSearch(){
        this.searchDialog = true
    }
    searchSubmit(){
        this.listQuery.page = 1
        this.getFileList();
    }
    // 表格：表头筛选条件变化时触发
    filterChange(value){
        this.filterParams = Object.assign(this.filterParams,value);
        this.listQuery.page = 1;
        this.getFileList();
    }
    // 表格：复选框变化时触发,删除编辑
    tableSelectionChange(value){
        this.selected = value
    }
    // 表格分页：每页显示条数变化触发
    handleSizeChange(value) {
        this.listQuery.page = 1;
        this.listQuery.limit = value;
        this.getFileList();
    }
    // 表格分页：当前页变化触发
    handleCurrentChange(value) {
        this.listQuery.page = value;
        this.getFileList();
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
