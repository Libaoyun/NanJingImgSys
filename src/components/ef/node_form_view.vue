<template>
    <el-dialog
    title="查看节点"
    :visible="editDialog"
    :close-on-click-modal="false"
    custom-class="global-dialog-default"
    @close="closeDialog"
    width="800px"
  >
        <div class="ef-node-form">
            <div class="ef-node-form-body">
                <el-form :model="node" :inline="true" ref="dataForm" label-width="120px" v-show="type === 'node'" size="small">
                    <el-form-item label="名称：">
                        <span>{{node.name}}</span>
                        <span></span>
                    </el-form-item>
                    <el-form-item label="状态：">
                        <span>{{node.handleStatus==1?'已完成':(node.handleStatus==2?'运行中':'未启动')}}</span>
                    </el-form-item>
                    <el-form-item label="备注：" class="large">
                        <span>{{node.remark}}</span>
                    </el-form-item>
                </el-form>
                <el-table
                    :data="node.approveList"
                    :border="true"
                    class="global-table-default"
                    style="width: 100%">
                    <el-table-column prop="approveName" label="参与者" align="center"></el-table-column>
                    <el-table-column prop="approveStartTime" label="开始时间" align="center"></el-table-column>
                    <el-table-column prop="approveEndTime" label="结束时间" align="center"></el-table-column>
                </el-table>
                <el-form :model="line" :inline="true" ref="dataForm" label-width="80px" v-show="type === 'line'">
                    <el-form-item label="条件">
                        <el-input v-model="line.label"></el-input>
                    </el-form-item>
                </el-form>
            </div>
        </div>
        <span slot="footer" class="dialog-footer">
            <el-button size="mini" @click="closeDialog">取消</el-button>
            <el-button type="primary" size="mini" @click="save" v-if="!readonly">确定</el-button>
        </span>
    </el-dialog>

</template>

<script>
    import { cloneDeep } from 'lodash'
    import $alert from './alert'

    export default {
        props:{
            editDialog:Boolean,
            readonly:Boolean
        },
        data() {
            return {
                visible: true,
                // node 或 line
                type: 'node',
                node: {},
                line: {},
                data: {},
                stateList: [{
                    state: 'success',
                    label: '成功'
                }, {
                    state: 'warning',
                    label: '警告'
                }, {
                    state: 'error',
                    label: '错误'
                }, {
                    state: 'running',
                    label: '运行中'
                }]
            }
        },
        methods: {
            /**
             * 表单修改，这里可以根据传入的ID进行业务信息获取
             * @param data
             * @param id
             */
            nodeInit(data, id) {
                this.type = 'node'
                this.data = data
                data.nodeList.filter((node) => {
                    if (node.id === id) {
                        node.handleType = 'user'
                        this.node = cloneDeep(node)
                    }
                })
            },
            lineInit(line) {
                this.type = 'line'
                this.line = line
            },
            chooseRange() {
                if(this.readonly) return
                if(this.node.handleType == 'user') {
                    $alert.alertChooseUser().then(res=>{
                        res.forEach(item=>{
                            if(!this.node.handleRange.some(c=>c.handleUserId == item.userCode)) {
                                this.node.handleRange.push({
                                    handleType:"user",
                                    handleUserId:item.userCode,
                                    handleUserName:item.userName
                                })
                            }
                        })
                    })
                }else if(this.node.handleType == 'post') {
                    $alert.alertChoosePost().then(res=>{
                        res.forEach(item=>{
                            if(!this.node.handleRange.some(c=>c.handleUserId == item.orgId)) {
                                this.node.handleRange.push({
                                    handleType:"post",
                                    handleUserId:item.orgId,
                                    handleUserName:item.orgName
                                })
                            }
                        })
                    })
                }
            },
            handleClose(tag) {
                this.node.handleRange.splice(this.node.handleRange.findIndex(item=>item.handleUserId == tag.handleUserId), 1);
            },
            closeDialog () {
                this.$emit('update:editDialog', false)
            }
        }
    }
</script>

<style lang="scss" scoped>
::v-deep {
  .global-dialog-default {
    height: auto;
    .el-dialog__body {
      height: auto;
      overflow: auto;
    }
    .el-form-item {
      width: 45% !important;
      .el-select {
        width: 100% !important;
      }
      .el-form-item__content {
        width: calc(100% - 120px) !important;
      }
      &.large {
        width: 100% !important;
        .el-select {
          width: 223px !important;
        }
        .el-form-item__content {
          width: calc(100% - 186px) !important;
        }
      }
    }
    .el-divider--horizontal {
      margin: 0 0 18px 0;
    }
  }
}
.handleRange {
    width: 100%;
    height: 100px;
    border: 1px solid #DCDFE6;
    border-radius: 4px;
    padding: 10px;
    overflow: auto;
    cursor: pointer;
}
</style>
