<template>
    <div class="authorizationList">
        <div class="left">
            <h4>
                <div>工程树列表</div>
            </h4>
            <el-input placeholder="请输入工程名称" prefix-icon="el-icon-search" v-model="filterTreeText"></el-input>
            <el-tree 
            :data="projectTree" 
            :props="projectProps" 
            @node-click="handleProjectNodeClick"
            highlight-current
            :default-expand-all="true"
            node-key="scopeCode"
            ref="projectTree"
            :expand-on-click-node="false"
            v-loading="loadingTree"
            element-loading-spinner="el-icon-loading"
            :filter-node-method="filterProjectTreeNode"
            >
                <span class="span-ellipsis" slot-scope="{ node }">
                    <span :title="node.label">{{ node.label }}</span>
                </span>
            </el-tree>
        </div>
        <div class="right">
            <h4>授权管理</h4>
            <div class="btn-group">
                <!--v-checkPermission="'create'"-->
                <el-button icon="el-icon-coordinate" size="mini" type="primary" @click="addUser">添加授权人员</el-button>
                <el-button icon="el-icon-coordinate" size="mini" type="primary" @click="addPosition">添加授权岗位</el-button>
                <el-button icon="el-icon-delete" size="mini" type="danger" @click="bathDeleteUser">删除授权对象</el-button>
            </div>
            <el-table
                :data="userList"
                :max-height="tableConfig.maxHeight"
                :border="tableConfig.border"
                :element-loading-spinner="tableConfig.loadingIcon"
                v-loading="listLoading"
                @selection-change="tableSelectionChange"
                class="global-table-default"
                style="width: calc(100% - 3px)">
                <el-table-column type="selection" width="55" align="center" ></el-table-column>
                <el-table-column label="序号" type="index" width="50" align="center">
                    <template slot-scope="scope">
                        <span>{{(listQuery.page-1)*listQuery.limit + scope.$index + 1}}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="authName" label="授权对象" width="100" align="center" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="userFlagName" label="类型" width="80" align="center" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="userOrganizationName" label="所属组织" align="center" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="scopeName" label="权限工程" width="300" align="center" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column label="操作" width="120" :show-overflow-tooltip="true" align="center">
                    <template slot-scope="scope">
                        <el-link type="primary" :underline="false" @click="updateMenuAuth(scope.row)" :disabled="scope.row.userId == $store.getters.userInfo.id">授权</el-link>
                        <el-divider direction="vertical"></el-divider>
                        <el-popconfirm title="是否要删除此对象？" @onConfirm="deleteUser(scope)" placement="top" cancelButtonType="plain" :disabled="scope.row.userId == $store.getters.userInfo.id">
                            <el-link type="danger" :underline="false" slot="reference" :disabled="scope.row.userId == $store.getters.userInfo.id">删除</el-link>
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
        <!-- 添加用户授权 -->
        <auth-user :userDialog.sync="userDialog" v-if="userDialog" :project="project" @addAuthUser="addAuthUser"></auth-user>
        <!-- 添加岗位授权 -->
        <auth-position :positionDialog.sync="positionDialog" v-if="positionDialog" :project="project"></auth-position>
        <menu-auth :menuAuthDialog.sync="menuAuthDialog" v-if="menuAuthDialog" :editParams="editParams" :project="project"></menu-auth>
    </div>
</template>

<script>
import { Component, Vue, Watch } from 'vue-property-decorator'
import tableMixin from '@/mixins/tableMixin'
import authUser from './table/authUser'
import authPosition from './table/authPosition'
import menuAuth from './table/menuAuth'
import eventBus from '@/utils/eventBus.js'

@Component({
    name: 'authorizationList',
    components: {
        authUser,
        authPosition,
        menuAuth
    }
})
export default class extends tableMixin {
    loadingTree = false;
    listLoading = false;
     // 当前选中的工程
    project = [];
    // 编辑授权params
    editParams = {};
    filterTreeText= ''
    projectTree = []
    defaultExpandedKeys = []
    projectProps= {
        children: 'children',
        label: 'simpleName'
    }
    userList = []
    total = 0
    userDialog = false
    positionDialog = false
    selected = []
    menuAuthDialog = false
    currentProject = null

    @Watch('filterTreeText')
    filter(val){
        this.$refs.projectTree.filter(val);
    }
    created(){
        eventBus.$on('refreshList',()=>{
            this.getAuthProjectUser();
        })
    }
    mounted() {
        this.getProjectTree()
    }
    beforeDestroy(){
        eventBus.$off('refreshList');
    }
    // 获取工程树
    getProjectTree(){
        // this.loadingTree = true;
        // this.$API.apiGetProjectTree().then((res)=>{
        //     res.data && res.data.forEach(item=>{
        //         item.simpleName = item.simpleName?item.simpleName:item.scopeName;
        //         item.children && item.children.forEach(child=>{
        //             child.simpleName = child.simpleName?child.simpleName:child.scopeName;
        //         })
        //     })
        //     this.projectTree = res.data
        //     if(res.data.length > 0){
        //         // :default-expanded-keys="defaultExpandedKeys"
        //         this.defaultExpandedKeys = [res.data[0].scopeCode]
        //     }
        //     this.loadingTree = false;
        // }).catch((err)=>{
        //     this.loadingTree = false;
        // })

        const data = [
            {
                hrCode: "[{\"orgid\":\"101093\",\"providerId\":\"crcc11\"}]",
                id: "a0eed590e4de4c46876144968be76e8b",
                ifUse: 1,
                ifVirtual: 0,
                isProject: "0",
                orgCode: "000010000100011",
                orgCreditCode: "914201006667974746",
                orgCreditId: "1018",
                orgFullName: "中铁十一局集团城市轨道工程有限公司",
                orgLevel: "2",
                orgName: "城轨公司",
                parentId: "0addc89ce0c84b56b7260adbaf04fda7",
                scopeCode: "000010000100011",
                scopeName: "中铁十一局集团城市轨道工程有限公司",
                sortNo: "00011",
                treeSort: 13,
                children:[
                    {
                        actualBeginDate: "2018-05-20",
                        areaHQCode: "AHQ_11",
                        areaHQName: "中南指挥部",
                        auditTime: "2020-04-29 14:12:45",
                        biddingContractAmount: "19400.00",
                        biddingTechnologyManager: "中铁十一局城轨公司福州滨海快线2标2工区项目部项目领导项目副经理袁旭东",
                        biddingUnit: "中铁十一局集团城市轨道工程有限公司",
                        biddingUnitCode: "914201006667974746",
                        biddingUnitManager: "中铁十一局城轨公司总部物资管理部（物资集采中心）副部长涂庭婧",
                        biddingUnitOut: true,
                        biddingUnitSimpleName: "城市轨道工程有限公司",
                        buildOrg: "武汉地铁集团有限公司",
                        buildOrgPhone: "13147160710",
                        code: "CRCC1100CG0320200010",
                        constructionUnit: "中铁十一局集团城市轨道工程有限公司",
                        constructionUnitCode: "crcc11:101093",
                        contractBeginDate: "2018-01-15",
                        contractEndDate: "2019-09-15",
                        designeOrg: "北京城建设计发展集团股份有限公司",
                        designeOrgPhone: "13720352372",
                        effectiveContractAmount: "17637.00",
                        engLocationCity: "27305151",
                        engLocationProvince: "896897",
                        engOutline: "老关村车辆段站主体及附属基坑临近各类管线、高压线塔基、民居；220KV高压线斜跨主体基坑及2座附属基坑；基坑开挖范围多为淤泥质粉质粘土，基坑变形量大环保工作压力大、文明施工要求高；协调工作繁杂；周边建（构）筑物、管线情况复杂；基坑开挖风险较高。",
                        hasChildren: 0,
                        id: "860d825f41064ce3a2e91fdd1dd97493",
                        isProject: "1",
                        manageMode: "selfTube",
                        manageModeName: "自管",
                        name: "武汉市轨道交通16号线一期工程老关村车辆段站及老关村站土建预埋工程",
                        orgNode: "000010000100011",
                        orgNodeName: "城轨公司",
                        parentId: "a0eed590e4de4c46876144968be76e8b",
                        planEndDate: "2020-08-31",
                        projectCharacter: "zbProject|zb-sgzcb",
                        projectCharacterName: "总包项目|施工总承包",
                        projectChiefEngineer: "中铁十一局城轨公司武汉16号线老关村车辆段站项目部项目领导总工程师黄冉",
                        projectDeptDetailAddress: "湖北省武汉市蔡甸区沌口路邦士润滑油对面",
                        projectManager: "中铁十一局城轨公司武汉16号线老关村车辆段站项目部项目领导项目经理李永刚",
                        projectManagerUnit: "中铁十一局集团城轨公司武汉16号线老关村车辆段站项目部",
                        projectManagerUnitCode: "crcc11:1508457",
                        scopeCode: "CRCC1100CG0320200010",
                        scopeName: "武汉市轨道交通16号线一期工程老关村车辆段站及老关村站土建预埋工程",
                        simpleName: "武汉老关村项目部",
                        status: "building",
                        statusName: "在建",
                        superviseOrg: "湖北建盛工程管理有限公司",
                        superviseOrgPhone: "15337164181",
                        technologyManagerOut: false,
                        type: "CG03",
                        unitManagerOut: false,
                        unitSimpleName: "城轨公司",
                        updateTime: "2020-09-29 23:04:51"
                    }
                ]
            }
        ]
            data && data.forEach(item=>{
                item.simpleName = item.simpleName?item.simpleName:item.scopeName;
                item.children && item.children.forEach(child=>{
                    child.simpleName = child.simpleName?child.simpleName:child.scopeName;
                })
            })
            this.projectTree = data
            if(data.length > 0){
                this.defaultExpandedKeys = [data[0].scopeCode]
            }
    }
    // 获取已授权工程树用户apiGetAuthProjectUser
    getAuthProjectUser(){
        // let params = {
        //     pageNum: this.listQuery.page,
        //     pageSize: this.listQuery.limit,
        //     scopeCode: this.currentProject.scopeCode,
        // }
        // this.listLoading = true
        // this.$API.apiGetAuthProjectUser(params).then((res)=>{
        //     this.listLoading = false
        //     this.userList = res.data.list
        //     this.total = res.data.total || 0;
        // }).catch((err)=>{
        //     this.listLoading = false
        // })

        this.userList = [
            {
                authName: "童慧瑶",
                authStatus: null,
                id: 23510,
                scopeCode: "CRCC1100CG0320200010",
                scopeName: "武汉老关村项目部",
                userFlag: "0",
                userFlagName: "人员",
                userId: "5184991",
                userOrganizationCode: "1/101093/1508064/1508082/1508256",
                userOrganizationName: "中铁十一局/城轨公司/南通1号线4标项目部/财务部/部员"
            }
        ]
    }
    // 添加授权人员
    addAuthUser(selected) {
        // todo
    }
    // 删除已授权用户
    deleteAuthProjectUser(params) {
        this.$API.apiDeleteAuthProjectUser(params).then((res)=>{
            this.listQuery.page = 1
            this.$message({
              type: 'success',
              message: '删除成功!'
            });
            this.getAuthProjectUser()
        }).catch((err)=>{

        })
    }
    filterProjectTreeNode(value, data){
        if (!value) return true;
        return data.simpleName.indexOf(value) !== -1;
    }
    handleProjectNodeClick(data) {
        this.currentProject = data
        var project = [];
        !project[0] && (project[0]={});
        project[0].scopeCode = data.scopeCode || '';
        project[0].scopeName = data.simpleName || data.scopeName || '';
        project[0].isProject = data.isProject || '';
        this.project = project;
        console.log(this.project)
        this.getAuthProjectUser()
    }
    // 删除授权对象
    deleteUser(data){
        let params = {
            scopeCode: this.currentProject.scopeCode,
            userList: [data.row.userId]
        }
        this.deleteAuthProjectUser(params)
    }
    // 批量删除授权对象
    bathDeleteUser() {
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
          let params = {
                scopeCode: this.currentProject.scopeCode,
                userList: []
            }
            for(let i in this.selected){
                params.userList.push(this.selected[i].userId)
            }
            this.deleteAuthProjectUser(params)
        }).catch(() => {      
        });
    }
    tableSelectionChange(value){
        this.selected = value
    }
    // 表格分页：每页显示条数变化触发
    handleSizeChange(value) {
        this.listQuery.page = 1;
        this.listQuery.limit = value
        this.getAuthProjectUser()
    }
    // 表格分页：当前页变化触发
    handleCurrentChange(value) {
        this.listQuery.page = value
        this.getAuthProjectUser()
    }
    // 清空节点
    changeActiveAuthBtn() {
        this.$refs.projectTree.setCheckedKeys([]);
    }
    // 添加用户授权
    addUser(){
        if(this.project.length == 0){
            this.$message({
                type: 'info',
                message: '请选择工程树!'
            })
            return
        }
        this.userDialog = true
    }
     // 添加岗位授权
    addPosition() {
        // console.log(this.$refs.projectTree.getCheckedNodes());
        if(this.project.length == 0){
            this.$message({
                type: 'info',
                message: '请选择工程树!'
            })
            return
        }
        this.positionDialog = true
    }
    // 功能菜单授权
    updateMenuAuth(value){
        let params = {
            scopeCode: value.scopeCode,
            userFlag: value.userFlag,
            userId: value.userId
        }
        this.editParams = params;
        this.menuAuthDialog = true
        // this.$refs.menuAuth.getAuthUserMenu(params)
    }
}
</script>

<style lang="scss" scoped>
.authorizationList{
    display: flex;
    min-height: calc(100vh - 130px);
    background-color: #fff;
    h4{
        margin-top: 0;
        display: flex;
        justify-content: space-between;
    }
}
.left{
    width:320px;
    height: calc(100vh - 130px);
    overflow: auto;
    padding: 15px;
    border-right: 1px #EBEEF5 solid;
    .el-tree {
        font-size: 14px;
    }
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
    flex: 1;
    padding: 15px;
    .btn-group{
        margin-bottom: 30px;
    }
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
