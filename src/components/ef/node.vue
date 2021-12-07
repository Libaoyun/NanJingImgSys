<template>
    <div
            ref="node"
            :style="nodeContainerStyle"
            @click="clickNode"
            @dblclick="dblclickNode"
            @mouseup="changeNodeSite"
            :class="nodeContainerClass"
    >
        <!-- 最左侧的那条竖线 -->
        <div class="ef-node-left"></div>
        <!-- 节点类型的图标 -->
        <div class="ef-node-left-ico flow-node-drag">
            <i :class="nodeIcoClass"></i>
        </div>
        <!-- 节点名称 -->
        <div class="ef-node-text" :show-overflow-tooltip="true">
            {{node.name}}
        </div>
        <!-- 节点状态图标 -->
        <div class="ef-node-right-ico" v-if="readonly">
            <i class="el-icon-circle-check el-node-state-success" v-show="node.handleStatus == '1'"></i>
            <i class="el-icon-video-play el-node-state-running" v-show="node.handleStatus == '2'"></i>
            <i class="el-icon-remove-outline el-node-state-undo" v-show="node.handleStatus == '3' || !node.handleStatus"></i>
        </div>
        <!-- 节点可拖拽连线 -->
        <div class="ef-node-right-canLine" :class="{'flow-node-drag':!node.viewOnly}" v-else>
            <i class="iconfont icon-map-connect" :class="{'flow-node-drag':!node.viewOnly}"></i>
        </div>
    </div>
</template>

<script>
    export default {
        props: {
            node: Object,
            activeElement: Object,
            readonly: Boolean
        },
        data() {
            return {}
        },
        computed: {
            nodeContainerClass() {
                return {
                    'ef-node-container': true,
                    'ef-node-active': this.activeElement.type == 'node' ? this.activeElement.nodeId === this.node.id : false
                }
            },
            // 节点容器样式
            nodeContainerStyle() {
                return {
                    top: this.node.top,
                    left: this.node.left
                }
            },
            nodeIcoClass() {
                var nodeIcoClass = {}
                nodeIcoClass[this.node.ico] = true
                // 添加该class可以推拽连线出来，viewOnly 可以控制节点是否运行编辑
                // nodeIcoClass['flow-node-drag'] = this.node.viewOnly ? false : true
                return nodeIcoClass
            }
        },
        methods: {
            // 点击节点
            clickNode() {
                this.$emit('clickNode', this.node.id)
            },
            dblclickNode() {
                this.$emit('dblclickNode', this.node)
            },
            // 鼠标移动后抬起
            changeNodeSite() {
                // 避免抖动
                if (this.node.left == this.$refs.node.style.left && this.node.top == this.$refs.node.style.top) {
                    return;
                }
                this.$emit('changeNodeSite', {
                    nodeId: this.node.id,
                    left: this.$refs.node.style.left,
                    top: this.$refs.node.style.top,
                })
            }
        }
    }
</script>
