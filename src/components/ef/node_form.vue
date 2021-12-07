<template>
    <el-dialog
    title="编辑节点"
    :visible="editDialog"
    :close-on-click-modal="false"
    custom-class="global-dialog-default"
    @close="closeDialog"
    width="800px"
  >
        <div class="ef-node-form">
            <div class="ef-node-form-body">
                <el-form :model="node" :inline="true" ref="dataForm" label-width="120px" v-show="type === 'node'" size="small">
                    <el-form-item label="类型">
                        <el-input v-model="node.type" :disabled="true"></el-input>
                    </el-form-item>
                    <el-form-item label="名称">
                        <el-input v-model="node.name"></el-input>
                    </el-form-item>
                    <el-form-item label="是否启用">
                        <el-select v-model="node.status">
                            <el-option label="是" value="1"></el-option>
                            <el-option label="否" value="0"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="处理者类型">
                        <el-select v-model="node.handleType">
                            <el-option label="人员" value="user"></el-option>
                            <el-option label="职务" value="post"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="处理范围" class="large">
                        <div class="handleRange" @click="chooseRange">
                            <el-tag
                                v-for="tag in node.handleRange"
                                :key="tag.handleUserId"
                                closable
                                :disable-transitions="false"
                                @close="handleClose(tag)">
                                {{tag.handleUserName}}
                            </el-tag>
                        </div>
                        
                    </el-form-item>
                    <el-form-item label="处理策略">
                        <el-select v-model="node.handleStrategy">
                            <el-option value="1" label="一人同意即可"></el-option>
                            <el-option value="2" label="所有人必须同意"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="备注" class="large">
                        <el-input type="textarea" v-model="node.remark" placeholder="请输入备注"></el-input>
                    </el-form-item>

                    <!-- <el-form-item label="状态">
                        <el-select v-model="node.state" placeholder="请选择">
                            <el-option
                                    v-for="item in stateList"
                                    :key="item.state"
                                    :label="item.label"
                                    :value="item.state">
                            </el-option>
                        </el-select>
                    </el-form-item> -->
                </el-form>

                <el-form :model="line" :inline="true" ref="dataForm" label-width="80px" v-show="type === 'line'">
                    <el-form-item label="条件">
                        <el-input v-model="line.label"></el-input>
                    </el-form-item>
                </el-form>
            </div>
        </div>
        <span slot="footer" class="dialog-footer">
            <el-button size="mini" @click="closeDialog">取消</el-button>
            <el-button type="primary" size="mini" @click="save">确定</el-button>
        </span>
    </el-dialog>

</template>

<script>
    import { cloneDeep } from 'lodash'
    import $alert from './alert'

    export default {
        props:{
            editDialog:Boolean
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
            save() {
                if(this.type == 'line') {
                    this.$emit('setLineLabel', this.line.from, this.line.to, this.line.label)
                }else {
                    this.data.nodeList.filter((node) => {
                        if (node.id === this.node.id) {
                            node.name = this.node.name
                            node.left = this.node.left
                            node.top = this.node.top
                            node.ico = this.node.ico
                            node.status = this.node.status
                            node.handleType = this.node.handleType
                            node.handleStrategy = this.node.handleStrategy
                            node.handleRange = this.node.handleRange
                            node.remark = this.node.remark
                            this.$emit('repaintEverything')
                        }
                    })
                }
                this.closeDialog()
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
      width: 45%;
      .el-select {
        width: 100%;
      }
      .el-form-item__content {
        width: calc(100% - 120px);
      }
      &.large {
        width: 100%;
        .el-select {
          width: 223px;
        }
        .el-form-item__content {
          width: calc(100% - 186px);
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
