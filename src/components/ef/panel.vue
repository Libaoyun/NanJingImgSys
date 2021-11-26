<template>
    <div v-if="easyFlowVisible" class="panel">
        <el-row>
            <!--顶部工具菜单-->
            <el-col :span="24">
                <div class="ef-tooltar">
                    <el-link type="primary" :underline="false">{{data.name}}</el-link>
                    <el-divider direction="vertical"></el-divider>
                    <el-button type="text" icon="el-icon-delete" size="large" @click="deleteElement" :disabled="!this.activeElement.type"></el-button>
                    <div class="button-groups">
                        <el-popconfirm title="确认将当前流程重置？" @confirm="resetCanvas" placement="top" cancelButtonType="plain">
                            <el-button slot="reference" size="mini">重置</el-button>
                        </el-popconfirm>
                        <el-popconfirm title="确认取消未保存操作？" @confirm="cancelCanvas" placement="top" cancelButtonType="plain">
                            <el-button slot="reference" size="mini" :disabled="!dataHasChange">取消</el-button>
                        </el-popconfirm>
                        <el-button size="mini" type="primary" @click="saveCanvas" :btnLoading="btnLoading" :disabled="!dataHasChange">保存</el-button>
                    </div>
                </div>
            </el-col>
        </el-row>
        <div class="main">
            <div class="main-left">
                <node-menu @addNode="addNode" ref="nodeMenu"></node-menu>
            </div>
            <div id="efContainer" ref="efContainer" class="container" v-flowDrag>
                <template v-for="node in data.nodeList">
                    <flow-node
                            :id="node.id"
                            :key="node.id"
                            :node="node"
                            :activeElement="activeElement"
                            @changeNodeSite="changeNodeSite"
                            @nodeRightMenu="nodeRightMenu"
                            @clickNode="clickNode"
                            @dblclickNode="dblclickNode"
                    >
                    </flow-node>
                </template>
                <!-- 给画布一个默认的宽度和高度 -->
                <div style="position:absolute;top: 2000px;left: 2000px;" v-html="'&nbsp;'"></div>
            </div>
            <!-- 右侧表单 -->
            <flow-node-form ref="nodeForm" :editDialog.sync="editDialog" @setLineLabel="setLineLabel" @repaintEverything="repaintEverything"></flow-node-form>
        </div>
    </div>

</template>

<script>
    import draggable from 'vuedraggable'
    // import { jsPlumb } from 'jsplumb'
    // 使用修改后的jsplumb
    import './jsplumb'
    import { easyFlowMixin } from '@/components/ef/mixins'
    import flowNode from '@/components/ef/node'
    import nodeMenu from '@/components/ef/node_menu'
    import FlowNodeForm from './node_form'
    import lodash from 'lodash'

    export default {
        data() {
            return {
                editDialog: false,//编辑栏是否显示
                // jsPlumb 实例
                jsPlumb: null,
                // 控制画布销毁
                easyFlowVisible: true,
                // 是否加载完毕标志位
                loadEasyFlowFinish: false,
                // 数据
                data: {

                },
                // 初始化数据
                originData: {
                    name:'',
                    lineList:[],
                    nodeList:[]
                },
                interfaceData : {},//接口返回数据
                // 激活的元素、可能是节点、可能是连线
                activeElement: {
                    // 可选值 node 、line
                    type: undefined,
                    // 节点ID
                    nodeId: undefined,
                    // 连线ID
                    sourceId: undefined,
                    targetId: undefined
                },
                zoom: 1,
                btnLoading:false,
                dataHasChange:false,//监听data是否变化
            }
        },
        watch:{
            data:{
                handler(val,oldVal) {
                    if(oldVal.nodeList.length===0 || val.nodeList.length===0) {
                        return
                    }
                    this.dataHasChange = true
                },
                deep:true
            }
        },
        // 一些基础配置移动该文件中
        mixins: [easyFlowMixin],
        components: {
            draggable, flowNode, nodeMenu, FlowNodeForm,
        },
        props:{
            selected:Object
        },
        directives: {
            'flowDrag': {
                bind(el, binding, vnode, oldNode) {
                    if (!binding) {
                        return
                    }
                    el.onmousedown = (e) => {
                        if (e.button == 2) {
                            // 右键不管
                            return
                        }
                        //  鼠标按下，计算当前原始距离可视区的高度
                        let disX = e.clientX
                        let disY = e.clientY
                        el.style.cursor = 'move'

                        document.onmousemove = function (e) {
                            // 移动时禁止默认事件
                            e.preventDefault()
                            const left = e.clientX - disX
                            disX = e.clientX
                            el.scrollLeft += -left

                            const top = e.clientY - disY
                            disY = e.clientY
                            el.scrollTop += -top
                        }

                        document.onmouseup = function (e) {
                            el.style.cursor = 'auto'
                            document.onmousemove = null
                            document.onmouseup = null
                        }
                    }
                }
            }
        },
        mounted() {
            
        },
        methods: {
            // 返回唯一标识
            getUUID() {
                return Math.random().toString(36).substr(3, 10)
            },
            initData(inData) {
                return new Promise((resolve,reject)=>{
                    this.jsPlumb = jsPlumb.getInstance()
                    this.$nextTick(() => {
                        // 默认加载流程A的数据、在这里可以根据具体的业务返回符合流程数据格式的数据即可
                        this.$API.apiGetFlow({menuCode:this.selected.menuCode}).then(res=>{
                            this.interfaceData = res.data
                            let getData = inData || (res.data.id ? res.data.flowContent : JSON.parse(JSON.stringify(this.originData)))
                            getData.name = getData.name || this.selected.title
                            getData.nodeList = getData.nodeList || []
                            getData.lineList = getData.lineList || []
                            this.dataReload(getData)
                            this.dataHasChange = false
                            console.log(this.data)
                            resolve()
                        }).catch(()=>{
                            reject()
                        })
                    })
                })
                
            },
            // 重置画布
            resetCanvas() {
                this.initData(JSON.parse(JSON.stringify(this.originData))).then(res=>{
                    this.dataHasChange = true
                    this.$message({
                        type:'success',
                        message:'已重置，保存后生效'
                    })
                })
            },
            // 取消画布
            cancelCanvas() {
                this.initData()
            },
            // 保存画布
            saveCanvas() {
                console.log(this.interfaceData)
                console.log(this.data)
                const flagStart = this.data.nodeList.some(item=>item.type==='start')
                const flagEnd = this.data.nodeList.some(item=>item.type==='end')
                const flagApproval = this.data.nodeList.some(item=>item.type==='other')
                if(this.data.nodeList.length > 0) {
                    if(!flagStart) {
                        this.$message({
                            message:'必须添加开始节点'
                        })
                        return
                    }
                    if(!flagEnd) {
                        this.$message({
                            message:'必须添加结束节点'
                        })
                        return
                    }
                    if(!flagApproval) {
                        this.$message({
                            message:'至少添加一个审批节点'
                        })
                        return
                    }
                    if(this.data.nodeList.length !== this.data.lineList.length+1) {
                        this.$message({
                            message:'节点之间必须有连线'
                        })
                        return
                    }
                }
                if(this.interfaceData.id) {
                    // 编辑
                    const params = {
                        menuCode:this.MENU_CODE_LIST.flowManageList,
                        creatorOrgId : this.$store.getters.currentOrganization.organizationId,
                        creatorOrgName : this.$store.getters.currentOrganization.organizationName,
                        id:this.interfaceData.id,
                        flowContent:this.data
                    }
                    this.btnLoading = true
                    this.$API.apiUpdateFlow(params).then(res=>{
                        this.btnLoading = false
                        this.dataHasChange = false
                        this.$message({
                            type:'success',
                            message:'保存成功'
                        })
                    }).catch(()=>{
                        this.btnLoading = false
                    })
                }else {
                    // 新增
                    const params = {
                        menuCode:this.MENU_CODE_LIST.flowManageList,
                        creatorOrgId : this.$store.getters.currentOrganization.organizationId,
                        creatorOrgName : this.$store.getters.currentOrganization.organizationName,
                        menuId:this.selected.menuCode,
                        flowContent:this.data
                    }
                    this.btnLoading = true
                    this.$API.apiAddFlow(params).then(res=>{
                        this.btnLoading = false
                        this.dataHasChange = false
                        this.interfaceData.id = res.data
                        this.$message({
                            type:'success',
                            message:'保存成功'
                        })
                    }).catch(()=>{
                        this.btnLoading = false
                    })
                }
                
            },
            jsPlumbInit() {
                this.jsPlumb.ready(() => {
                    // 导入默认配置
                    this.jsPlumb.importDefaults(this.jsplumbSetting)
                    // 会使整个jsPlumb立即重绘。
                    this.jsPlumb.setSuspendDrawing(false, true);
                    // 初始化节点
                    this.loadEasyFlow()
                    // 单点击了连接线, https://www.cnblogs.com/ysx215/p/7615677.html
                    this.jsPlumb.bind('click', (conn, originalEvent) => {
                        this.activeElement.type = 'line'
                        this.activeElement.sourceId = conn.sourceId
                        this.activeElement.targetId = conn.targetId
                    })
                    this.jsPlumb.bind('dblclick', (conn, originalEvent) => {
                        this.editDialog = true
                        this.activeElement.type = 'line'
                        this.activeElement.sourceId = conn.sourceId
                        this.activeElement.targetId = conn.targetId
                        this.$refs.nodeForm.lineInit({
                            from: conn.sourceId,
                            to: conn.targetId,
                            label: conn.getLabel()
                        })
                    })
                    // 连线
                    this.jsPlumb.bind("connection", (evt) => {
                        let from = evt.source.id
                        let to = evt.target.id
                        if (this.loadEasyFlowFinish) {
                            this.data.lineList.push({from: from, to: to})
                        }
                    })

                    // 删除连线回调
                    this.jsPlumb.bind("connectionDetached", (evt) => {
                        this.deleteLine(evt.sourceId, evt.targetId)
                    })

                    // 改变线的连接节点
                    this.jsPlumb.bind("connectionMoved", (evt) => {
                        this.changeLine(evt.originalSourceId, evt.originalTargetId)
                    })

                    // 连线右击
                    this.jsPlumb.bind("contextmenu", (evt) => {
                        console.log('contextmenu', evt)
                        evt.preventDefault();
                    })

                    // 连线
                    this.jsPlumb.bind("beforeDrop", (evt) => {
                        let from = evt.sourceId
                        let to = evt.targetId
                        if (from === to) {
                            this.$message.info('节点不支持连接自己')
                            return false
                        }
                        if (this.hasLine(from, to)) {
                            this.$message.info('该关系已存在,不允许重复创建')
                            return false
                        }
                        if (this.hashOppositeLine(from, to)) {
                            this.$message.info('不支持两个节点之间连线回环');
                            return false
                        }
                        if (this.hasMoreLine(from, to)) {
                            this.$message.info('不支持连接多个节点')
                            return false
                        }
                        // this.$message.success('连接成功')
                        return true
                    })

                    // beforeDetach
                    this.jsPlumb.bind("beforeDetach", (evt) => {
                        console.log('beforeDetach', evt)
                    })
                    this.jsPlumb.setContainer(this.$refs.efContainer)
                })
            },
            // 加载流程图
            loadEasyFlow() {
                // 初始化节点
                for (var i = 0; i < this.data.nodeList.length; i++) {
                    let node = this.data.nodeList[i]
                    // 设置源点，可以拖出线连接其他节点
                    this.jsPlumb.makeSource(node.id, lodash.merge(this.jsplumbSourceOptions, {}))
                    // 设置目标点，其他源点拖出的线可以连接该节点
                    this.jsPlumb.makeTarget(node.id, this.jsplumbTargetOptions)
                    if (!node.viewOnly) {
                        this.jsPlumb.draggable(node.id, {
                            containment: 'parent',
                            stop: function (el) {
                                // 拖拽节点结束后的对调
                                console.log('拖拽结束: ', el)
                            }
                        })
                    }
                }
                // 初始化连线
                for (var i = 0; i < this.data.lineList.length; i++) {
                    let line = this.data.lineList[i]
                    var connParam = {
                        source: line.from,
                        target: line.to,
                        label: line.label ? line.label : '',
                        connector: line.connector ? line.connector : '',
                        anchors: line.anchors ? line.anchors : undefined,
                        paintStyle: line.paintStyle ? line.paintStyle : undefined,
                    }
                    this.jsPlumb.connect(connParam, this.jsplumbConnectOptions)
                }
                this.$nextTick(function () {
                    this.loadEasyFlowFinish = true
                })
            },
            // 设置连线条件
            setLineLabel(from, to, label) {
                var conn = this.jsPlumb.getConnections({
                    source: from,
                    target: to
                })[0]
                if (!label || label === '') {
                    conn.removeClass('flowLabel')
                    conn.addClass('emptyFlowLabel')
                } else {
                    conn.addClass('flowLabel')
                }
                conn.setLabel({
                    label: label,
                })
                this.data.lineList.forEach(function (line) {
                    if (line.from == from && line.to == to) {
                        line.label = label
                    }
                })

            },
            // 删除激活的元素
            deleteElement() {
                if (this.activeElement.type === 'node') {
                    this.deleteNode(this.activeElement.nodeId)
                } else if (this.activeElement.type === 'line') {
                    this.$confirm('确定删除所点击的线吗?', '提示', {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning'
                    }).then(() => {
                        var conn = this.jsPlumb.getConnections({
                            source: this.activeElement.sourceId,
                            target: this.activeElement.targetId
                        })[0]
                        this.jsPlumb.deleteConnection(conn)
                    }).catch(() => {
                    })
                }
            },
            // 删除线
            deleteLine(from, to) {
                this.data.lineList = this.data.lineList.filter(function (line) {
                    if (line.from == from && line.to == to) {
                        return false
                    }
                    return true
                })
            },
            // 改变连线
            changeLine(oldFrom, oldTo) {
                this.deleteLine(oldFrom, oldTo)
            },
            // 改变节点的位置
            changeNodeSite(data) {
                for (var i = 0; i < this.data.nodeList.length; i++) {
                    let node = this.data.nodeList[i]
                    if (node.id === data.nodeId) {
                        node.left = data.left
                        node.top = data.top
                    }
                }
            },
            /**
             * 拖拽结束后添加新的节点
             * @param evt
             * @param nodeMenu 被添加的节点对象
             * @param mousePosition 鼠标拖拽结束的坐标
             */
            addNode(evt, nodeMenu, mousePosition) {
                console.log(nodeMenu)
                if(this.data.nodeList.some(item=>item.type=='start') && nodeMenu.type=='start') {
                    this.$message({
                        message:'一个流程只能存在一个开始节点'
                    })
                    return
                }
                if(this.data.nodeList.some(item=>item.type=='end') && nodeMenu.type=='end') {
                    this.$message({
                        message:'一个流程只能存在一个结束节点'
                    })
                    return
                }
                var screenX = evt.originalEvent.clientX, screenY = evt.originalEvent.clientY
                let efContainer = this.$refs.efContainer
                var containerRect = efContainer.getBoundingClientRect()
                var left = screenX, top = screenY
                // 计算是否拖入到容器中
                if (left < containerRect.x || left > containerRect.width + containerRect.x || top < containerRect.y || containerRect.y > containerRect.y + containerRect.height) {
                    this.$message.error("请把节点拖入到画布中")
                    return
                }
                left = left - containerRect.x + efContainer.scrollLeft
                top = top - containerRect.y + efContainer.scrollTop
                // 居中
                left -= 85
                top -= 16
                var nodeId = this.getUUID()
                // 动态生成名字
                var origName = nodeMenu.name
                var nodeName = origName
                var index = 1
                while (index < 10000) {
                    var repeat = false
                    for (var i = 0; i < this.data.nodeList.length; i++) {
                        let node = this.data.nodeList[i]
                        if (node.name === nodeName) {
                            nodeName = origName + index
                            repeat = true
                        }
                    }
                    if (repeat) {
                        index++
                        continue
                    }
                    break
                }
                var node = {
                    id: nodeId,
                    name: nodeName,
                    type: nodeMenu.type,
                    left: left + 'px',
                    top: top + 'px',
                    ico: nodeMenu.ico,
                    status:'1',
                    handleType:'user',
                    handleStrategy:'1',
                    remark:'',
                    handleRange:[]
                }
                /**
                 * 这里可以进行业务判断、是否能够添加该节点
                 */
                this.data.nodeList.push(node)
                this.$nextTick(function () {
                    this.jsPlumb.makeSource(nodeId, this.jsplumbSourceOptions)
                    this.jsPlumb.makeTarget(nodeId, this.jsplumbTargetOptions)
                    this.jsPlumb.draggable(nodeId, {
                        containment: 'parent',
                        stop: function (el) {
                            // 拖拽节点结束后的对调
                            console.log('拖拽结束: ', el)
                        }
                    })
                })
            },
            /**
             * 删除节点
             * @param nodeId 被删除节点的ID
             */
            deleteNode(nodeId) {
                this.$confirm('确定要删除节点' + nodeId + '?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning',
                    closeOnClickModal: false
                }).then(() => {
                    /**
                     * 这里需要进行业务判断，是否可以删除
                     */
                    this.data.nodeList = this.data.nodeList.filter(function (node) {
                        if (node.id === nodeId) {
                            // 伪删除，将节点隐藏，否则会导致位置错位
                            // node.show = false
                            return false
                        }
                        return true
                    })
                    this.$nextTick(function () {
                        this.jsPlumb.removeAllEndpoints(nodeId);
                    })
                }).catch(() => {
                })
                return true
            },
            clickNode(nodeId) {
                this.activeElement.type = 'node'
                this.activeElement.nodeId = nodeId
            },
            dblclickNode(node) {
                if(node.type === 'start' || node.type === 'end') {
                    return
                }
                this.editDialog = true
                this.activeElement.type = 'node'
                this.activeElement.nodeId = node.id
                this.$refs.nodeForm.nodeInit(this.data, node.id)
            },
            // 是否具有该线
            hasLine(from, to) {
                for (var i = 0; i < this.data.lineList.length; i++) {
                    var line = this.data.lineList[i]
                    if (line.from === from && line.to === to) {
                        return true
                    }
                }
                return false
            },
            // 是否连接多个节点
            hasMoreLine(from ,to) {
                const fromFlag = this.data.lineList.some(item=>item.from === from)
                const toFlag = this.data.lineList.some(item=>item.to === to)
                if(fromFlag || toFlag) {
                    return true
                }
                return false
            },
            // 是否含有相反的线
            hashOppositeLine(from, to) {
                return this.hasLine(to, from)
            },
            nodeRightMenu(nodeId, evt) {
                this.menu.show = true
                this.menu.curNodeId = nodeId
                this.menu.left = evt.x + 'px'
                this.menu.top = evt.y + 'px'
            },
            repaintEverything() {
                this.jsPlumb.repaint()
            },
            // 加载流程图
            dataReload(data) {
                this.easyFlowVisible = false
                this.data.nodeList = []
                this.data.lineList = []
                this.$nextTick(() => {
                    data = lodash.cloneDeep(data)
                    this.easyFlowVisible = true
                    this.data = data
                    this.$nextTick(() => {
                        this.jsPlumb = jsPlumb.getInstance()
                        this.$nextTick(() => {
                            this.jsPlumbInit()
                        })
                    })
                })
            },
            zoomAdd() {
                if (this.zoom >= 1) {
                    return
                }
                this.zoom = this.zoom + 0.1
                this.$refs.efContainer.style.transform = `scale(${this.zoom})`
                this.jsPlumb.setZoom(this.zoom)
            },
            zoomSub() {
                if (this.zoom <= 0) {
                    return
                }
                this.zoom = this.zoom - 0.1
                this.$refs.efContainer.style.transform = `scale(${this.zoom})`
                this.jsPlumb.setZoom(this.zoom)
            },
        }
    }
</script>
<style lang="scss" scoped>
.panel{
    height: 100%;
    .button-groups {
        float: right;
        span{
            margin-right: 10px;
        }
    }
    .main {
        display: flex;
        height: calc(100% - 47px);
        .main-left {
            width: 130px;
            flex-shrink: 0;
            border-right: 1px solid #dce3e8;
        }
        .container {
            overflow: auto;
        }
        .main-right {
            width: 250px;
            border-left: 1px solid #dce3e8;
            background-color: #FBFBFB
        }
    }
}
</style>
