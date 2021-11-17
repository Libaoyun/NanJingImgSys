<template>
    <div class="dictionary">
        <div class="left">
            <h4>数据项</h4>
            <el-input placeholder="请输入关键字" prefix-icon="el-icon-search" v-model="filterTreeText"></el-input>
            <el-tree
            default-expand-all
            :data="projectTree" 
            :props="projectProps" 
            @node-click="handleProjectNodeClick"
            highlight-current
            node-key="dicTypeId"
            ref="projectTree"
            :expand-on-click-node="false"
            accordion
            v-loading="loadingTree"
            element-loading-spinner="el-icon-loading"
            :filter-node-method="filterProjectTreeNode"
            >
                <!-- 字典增加删除部分 -->
                <span class="custom-tree-node" slot-scope="{ node, data }">
                    <span>{{ node.label }}</span>
                    <span class="node_edit">
                        <template  v-if="data.dicTypeFlag != '1'">
                            <i @click.stop="addMenu(data)" class="icon-wenjianjia iconfont" title="新增菜单"></i>
                            <i @click.stop="addDictionary(data)" class="el-icon-plus" title="新增字典"></i>
                        </template>
                        <template v-if="data.dicTypeFlag">
                            <i @click.stop="update(data)" class="el-icon-edit" title="编辑"></i>
                            <i @click.stop="remove(data)" class="el-icon-minus" title="删除"></i>
                        </template>
                    </span>
                </span>
            </el-tree>
        </div>
        <div class="right">
            <h4>数据值</h4>
            <div class="btn-group">
                <el-button icon="el-icon-plus" size="mini" type="primary" @click="createDictionary" :disabled="dictionaryDisabled">新增</el-button>
            </div>
            <el-table
                :data="dictionaryList"
                :max-height="tableConfig.maxHeight"
                :border="tableConfig.border"
                v-loading="listLoading"
                :element-loading-spinner="tableConfig.loadingIcon"
                class="global-table-default"
                style="width: calc(100% - 3px)">
                <el-table-column label="序号" type="index" width="50" align="center">
                    <template slot-scope="scope">
                        <span>{{(listQuery.page-1)*listQuery.limit + scope.$index + 1}}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="dicEnumId" label="字典编码" align="center" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="dicEnumName" label="枚举值" align="center" :show-overflow-tooltip="true"> </el-table-column>
                <el-table-column prop="dicEnumName" label="字典类型ID" align="center" :show-overflow-tooltip="true"> 
                    <template>{{selected.dicTypeId}}</template>
                </el-table-column>
                <el-table-column prop="remark" label="描述" align="center" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="isValid" label="状态" align="center" :show-overflow-tooltip="true">
                    <template slot-scope="prop">
                        {{prop.row.isValid?'启用':'禁用'}}
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="120" :show-overflow-tooltip="true" align="center">
                    <template slot-scope="scope">
                        <el-link type="primary" :underline="false" @click="updateDictionary(scope.row)">编辑</el-link>
                        <el-divider direction="vertical"></el-divider>
                        <el-popconfirm title="是否要删除当前数据？" @onConfirm="deleteDictionary(scope)" placement="top" cancelButtonType="plain">
                            <el-link type="danger" :underline="false" slot="reference">删除</el-link>
                        </el-popconfirm>
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
        </div>
    </div>
</template>

<script>
import { Component, Vue, Watch } from 'vue-property-decorator'
import tableMixin from '@/mixins/tableMixin'
import { alertAddDictionary, alertAddMenu , alertAddDictionaryContent, alertUpdateDictionary, alertUpdateMenu, alertUpdateDictionaryContent } from './alert'

@Component({
    name: 'dictionaryList',
    components: {
    }
})
export default class extends tableMixin {
    loadingTree = false;
    total = 0;
    listLoading = false;
    loadingBtn = 0;
    filterTreeText= ''
    projectTree = []
    projectProps= {
        children: 'children',
        label: 'dicTypeName'
    }
    dictionaryList = []

    dictionaryDisabled = true

    selected = [];

    @Watch('filterTreeText')
    filter(val){
        this.$refs.projectTree.filter(val);
    }

    created(){
        // this.getDictionaryTree();
        this.projectTree = [
            {
                dicTypeName: '数据字典',
                children: [
                    {
                        children: [],
                        dicTypeFlag: "1",
                        dicTypeId: "1011",
                        dicTypeName: "操作类型",
                        dicTypeParentId: "0",
                        id: 104,
                        menuCode: null,
                        operatePath: null
                    } 
                ]
            }
        ];

    }
    
    // 获取菜单树
    getDictionaryTree(){
        this.loadingTree = true;
        this.$API.apiGetDictionaryTree().then(res=>{
            this.projectTree = [
                {
                    dicTypeName: '数据字典',
                    children: res.data
                }
            ];
            this.loadingTree = false;
        }).catch(()=>{
            this.loadingTree = false;
        })
    }
    // 获取菜单下的字典信息
    getDictionaryList(){
        this.listLoading = true;
        var params = {};
        params.dicTypeId = this.selected.dicTypeId;
        params.pageNum = this.listQuery.page;
        params.pageSize = this.listQuery.limit;
        this.$API.apiGetDictionaryList(params).then(res=>{
            this.listLoading = false;
            if(res.data){
                this.dictionaryList = res.data.list || [];
                this.total = res.data.total;
            }
        }).catch(()=>{
            this.listLoading = false;
        })
    }
    filterProjectTreeNode(value, data){
        if (!value) return true;
        return data.dicTypeName.indexOf(value) !== -1;
    }
    handleProjectNodeClick(data) {
        this.selected = data;
        if(data.dicTypeFlag != '1'){
            this.dictionaryDisabled = true
            this.dictionaryList = []
        }else{
            this.dictionaryDisabled = false
            this.getDictionaryList();
        }
    }
    // 表格分页：每页显示条数变化触发
    handleSizeChange(value) {
        this.listQuery.page = 1;
        this.listQuery.limit = value;
        this.getDictionaryList();
    }
    // 表格分页：当前页变化触发
    handleCurrentChange(value) {
        this.listQuery.page = value;
        this.getDictionaryList();
    }

    // 新增菜单
    addMenu(data){
        alertAddMenu({ dicTypeParentId:data.dicTypeId || 0 }).then(res=>{
            this.getDictionaryTree();
        }).catch(()=>{})
    }

    // 新增字典
    addDictionary(data){
        alertAddDictionary({ dicTypeParentId:data.dicTypeId || 0 }).then(res=>{
            this.getDictionaryTree();
        }).catch(()=>{})
    }

    // 更新
    update(data){
        if(data.dicTypeFlag == '0'){
            // 更新菜单
            alertUpdateMenu(JSON.parse(JSON.stringify(data))).then(res=>{
              this.getDictionaryTree();
            }).catch(()=>{})
        }else if(data.dicTypeFlag == '1'){
            // 更新字典
            alertUpdateDictionary(JSON.parse(JSON.stringify(data))).then(res=>{
                this.getDictionaryTree();
            }).catch(()=>{})
        }
    }

    // 删除
    remove(data){
        let prop = data.dicTypeFlag == '0' ? `确定删除菜单项${data.dicTypeName}, 是否继续?` : `确定删除数据项${data.dicTypeName}, 是否继续?`
        this.$confirm(prop, '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }).then((e) => {
            let params ={
                ids: [data.id],
                dicTypeId:data.dicTypeId,
                menuCode:this.MENU_CODE_LIST.dictionary,
                operatePath:this.$store.getters.operatePath
            };
            this.$API.apiDeleteDictionaryTree(params).then(res=>{
                this.$message({
                    type:'success',
                    message:'删除成功'
                })
                this.getDictionaryTree();
            })
        })
    }

    // 新增数据值
    createDictionary(){
        alertAddDictionaryContent(
            { dicTypeId: this.selected.dicTypeId,
              dicTypeName: this.selected.dicTypeName
            }).then(res=>{
            this.getDictionaryList();
        }).catch(()=>{})
    }

    // 编辑数据值
    updateDictionary(data){
        alertUpdateDictionaryContent(JSON.parse(JSON.stringify(data))).then(res=>{
            this.getDictionaryList();
        }).catch(()=>{})
    }

    // 删除数据值
    deleteDictionary(data){
        if(data && data.row && data.row.id){
            this.$API.apiDeleteDictionary({ids:[data.row.id],menuCode:this.MENU_CODE_LIST.dictionary,operatePath:this.$store.getters.operatePath}).then(res=>{
                this.$message({
                    type:'success',
                    message:'删除成功'
                })
                this.resetPageNum();
                this.getDictionaryList();
            })
        }
    }
}
</script>

<style lang="scss" scoped>
@import '@/assets/scss/element-variables.scss';
.custom-tree-node {
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
.dictionary{
    display: flex;
    min-height: calc(100vh - 80px);
    background-color: #fff;
    h4{
        margin-top: 0;
    }
}
.left{
    flex: 0 0 300px;
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
}
.right{
    width: calc(100% - 300px);
    padding: 15px;
    .btn-group{
        margin-bottom: 30px;
    }
}
</style>
