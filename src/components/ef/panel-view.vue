<template>
    <div v-if="easyFlowVisible" class="panel">
        <div class="main">
            <div id="efContainer" ref="efContainer" class="container">
                <template v-for="node in data.nodeList">
                    <flow-node
                            :id="node.id"
                            :key="node.id"
                            :activeElement="activeElement"
                            :node="node"
                            :readonly="true"
                            @dblclickNode="dblclickNode"
                    >
                    </flow-node>
                </template>
                <!-- 给画布一个默认的宽度和高度 -->
                <div style="position:absolute;top: 2000px;left: 2000px;" v-html="'&nbsp;'"></div>
            </div>
            <!-- 右侧表单 -->
            <flow-node-form ref="nodeForm" :editDialog.sync="editDialog" :readonly="true"></flow-node-form>
        </div>
    </div>

</template>

<script>
    // import draggable from 'vuedraggable'
    // import { jsPlumb } from 'jsplumb'
    // 使用修改后的jsplumb
    import './jsplumb'
    import { easyFlowMixin } from '@/components/ef/mixins'
    import flowNode from '@/components/ef/node'
    import FlowNodeForm from './node_form_view'
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
        // 一些基础配置移动该文件中
        mixins: [easyFlowMixin],
        components: {
            flowNode, FlowNodeForm,
        },
        props:{
            serialNumber:String
        },
        directives: {

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
                        this.$API.apiGetSerialFlow({serialNumber:this.serialNumber}).then(res=>{
                            this.interfaceData = res.data
                            let getData = inData || (res.data.id ? JSON.parse(res.data.flowContent) : JSON.parse(JSON.stringify(this.originData)))
                            getData.nodeList = getData.nodeList || []
                            getData.lineList = getData.lineList || []
                            console.log(getData)
                            this.dataReload(getData)
                            this.dataHasChange = false
                            resolve()
                        }).catch(()=>{
                            this.interfaceData = {}
                            let getData = inData || JSON.parse(JSON.stringify(this.originData))
                            getData.nodeList = getData.nodeList || []
                            getData.lineList = getData.lineList || []
                            this.dataReload(getData)
                            this.dataHasChange = false
                            reject()
                        })
                    })
                })
                
            },
            jsPlumbInit() {
                this.jsPlumb.ready(() => {
                    // 导入默认配置
                    this.jsPlumb.importDefaults(this.jsplumbSetting)
                    // 会使整个jsPlumb立即重绘。
                    this.jsPlumb.setSuspendDrawing(false, true);
                    // 初始化节点
                    this.loadEasyFlow()
                    this.jsPlumb.setContainer(this.$refs.efContainer)
                })
            },
            // 加载流程图
            loadEasyFlow() {
                // 初始化节点
                for (var i = 0; i < this.data.nodeList.length; i++) {
                    let node = this.data.nodeList[i]
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
            dblclickNode(node) {
                if(node.type === 'start' || node.type === 'end') {
                    return
                }
                this.editDialog = true
                this.activeElement.type = 'node'
                this.activeElement.nodeId = node.id
                this.$refs.nodeForm.nodeInit(this.data, node.id)
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
