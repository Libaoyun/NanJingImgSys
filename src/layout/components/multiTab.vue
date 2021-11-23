<template>
    <div>
        <div class="multi-tabs" :style="{width:isCollapse?'calc(100% - 50px)':'calc(100% - 210px)'}">
            <el-tabs
                v-model="activeTab"
                type="card"
                :closable="multiTabList.length>1"
                @tab-click="tabClick"
                v-if="multiTabList.length"
                @tab-remove="tabDelete">
                <el-tab-pane
                    :key="item.route"
                    v-for="(item,index) in multiTabList"
                    :label="item.title"
                    :name="item.route">
                    <slot slot="label">
                        <el-dropdown trigger="none" ref="dropdwon" @contextmenu.native="handleRightClick($event,index)" @command="handleCommand">
                            <span>{{item.title}}</span>
                            <el-dropdown-menu slot="dropdown">
                                <el-dropdown-item :command="{name:'closeCurrent',item:item}">关闭当前</el-dropdown-item>
                                <!-- <el-dropdown-item :command="{name:'closeBefore',item:item}">关闭左侧</el-dropdown-item>
                                <el-dropdown-item :command="{name:'closeAfter',item:item}">关闭右侧</el-dropdown-item> -->
                                <el-dropdown-item :command="{name:'closeAllOutSelf',item:item}">关闭其他</el-dropdown-item>
                                <el-dropdown-item :command="{name:'closeAll',item:item}">关闭所有</el-dropdown-item>
                            </el-dropdown-menu>
                        </el-dropdown>
                    </slot>
                </el-tab-pane>
            </el-tabs>
        </div>
        <div class="multi_placeholder"></div>
    </div>
</template>

<script>
import { Component, Vue, Watch } from 'vue-property-decorator'
@Component({
    name: 'multiTab'
})
export default class extends Vue {
    get isCollapse() {
      return !this.$store.getters.sidebar.opened
    }
    get multiTabList () {
      return this.$store.state.multiTab.multiTabList;
    }
    get activeTab() {
       return this.$store.state.multiTab.activeTab; 
    }
    set activeTab(val) {
        this.$store.commit('SET_ACTIVE_TAB', val);
    }
    @Watch('$route')
    onChangeRoute(to){
      if(to.path !== '/welcome'){
        let flag = false;
        for (let i in this.multiTabList ) {
            if (this.multiTabList[i].route === to.path) {
            flag = true;
            this.$store.commit('SET_ACTIVE_TAB', to.path);
            break
            }
        }
        if (!flag) {
            this.$store.commit('ADD_TAB', {route: to.path, title: to.meta.title, name:to.name});
            this.$store.commit('SET_ACTIVE_TAB', to.path);
        }
      }
    }
    tabClick() {
        this.$router.push({path: this.activeTab});
    }
    tabDelete(targetRoute) {
        this.$store.commit('DELETE_TAB', targetRoute);
        if (this.activeTab === targetRoute) {
            // 设置当前激活的路由
            if (this.multiTabList && this.multiTabList.length >= 1) {
                this.$store.commit('SET_ACTIVE_TAB', this.multiTabList[this.multiTabList.length-1].route);
                this.$router.push({path: this.activeTab});
            }
        }
    }

    handleRightClick(e,index){
        e.preventDefault()
        this.$refs.dropdwon[index].handleClick();
    }

    handleCommand(command){
        this[command.name](command.item);
    }
    closeCurrent(item){
        this.$store.commit('DELETE_TAB', item.route);
        if (this.multiTabList && this.multiTabList.length >= 1) {
            this.$store.commit('SET_ACTIVE_TAB', this.multiTabList[this.multiTabList.length-1].route);
            this.$router.push({path: this.activeTab});
        }else{
            this.$router.push({path: '/weclome'});
        }
    }

    closeBefore(item){
        this.$store.commit('DELETE_BEFORE_TAB', item.route);
    }

    closeAfter(item){
        this.$store.commit('DELETE_AFTER_TAB', item.route);
    }

    closeAll(item){
        this.$store.commit('CLEAR_TAB_LIST');
        this.$router.push({path: '/weclome'});
    }

    closeAllOutSelf(item){
        this.$store.commit('CLEAR_TAB_LIST_OUTSELF',item);
        this.$store.commit('SET_ACTIVE_TAB', item.route);
        this.$router.push({path: this.activeTab});
    }
}
</script>

<style lang="scss" scoped>
@import '@/assets/scss/element-variables.scss';
.multi_placeholder{
    width: 100%;
    height: 40px;
}
.multi-tabs{
    user-select: none;
    padding: 5px 10px 0;
    height: 40px;
    background-color: #f8f8f8;
    width: calc(100% - 210px);
    position: fixed;
    z-index: 5;
    box-shadow: 0 5px 10px #ccc;
     &::v-deep{
        .el-tabs{
            position: relative;
            z-index: 1;
        }
        .el-tabs__header{
            margin-bottom: 0;
        }
        .el-tabs--card > .el-tabs__header{
            border: none;
            .el-tabs__nav{
                // border: none;
                height: 35px;
                .el-tabs__item{
                    height: 35px;
                    line-height: 35px;
                    font-size: 13px;
                }
            }
            .el-tabs__item{
                background-color: #fafafa;
                padding-left: 15px !important;
                padding-right: 15px !important;
                &.is-active{
                    background-color: #E0EEFF;
                    .el-dropdown-selfdefine{
                        color: $--color-primary;
                    }
                }
                .el-icon-close{
                    width: 14px;
                }
            }
        }
     }
}
    // .el-tabs--border-card > .el-tabs__content{
    //     padding: 0;
    // }
    // .el-tabs--border-card > .el-tabs__header{
    //     background-color: #fff;
    // }
    // .el-tabs--border-card > .el-tabs__header .el-tabs__item{
    //     background-color: #fafafa;
    // }
    // .el-tabs--border-card > .el-tabs__header .el-tabs__item.is-active{
    //     background-color: #fff;
    // }
    // .el-tabs--border-card{
    //     box-shadow: none;
    // }
    
// }
</style>