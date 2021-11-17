<template>
    <el-menu mode="vertical" :collapse-transition=false :default-active="routePath" :collapse="isCollapse">
        <sidebar-item :routes='routes' style="padding-bottom:40px"></sidebar-item>
        <div class="fold" @click="toggleSideBar">
            <i :class="[isCollapse ? 'el-icon-arrow-right' : 'el-icon-arrow-left']"></i>
            <span>收起菜单</span>
        </div>
    </el-menu>
</template>

<script>
import { Component, Vue } from 'vue-property-decorator'
import SidebarItem from './sidebarItem.vue'

@Component({
    name: 'sidebar',
    components: {
        SidebarItem,
    }
})
export default class extends Vue {
    get isCollapse() {
      return !this.$store.getters.sidebar.opened
    }
    get routes() {
        return this.$store.getters.addRouters
    }
    get routePath(){
        var path = this.$route.path;
        var Indexs = [path.indexOf('New'),path.indexOf('Edit'),path.indexOf('Detail'),path.indexOf('Approval')];
        Indexs.forEach(item=>{
            if(item>-1){
                path = path.substring(0,item)+'List';
            }
        })
        return path;
    }
    toggleSideBar() {
      this.$store.dispatch('ToggleSideBar')
    }
    mounted () {
      // 刷新时以当前路由做为tab加入tabs
      if(this.$route.path !== '/welcome'){
        let flag = false;
        let multiTabList = this.$store.state.multiTab.multiTabList
        for (let i in multiTabList ) {
            if (multiTabList[i].route === this.$route.path) {
            flag = true;
            this.$store.commit('SET_ACTIVE_TAB', this.$route.path);
            break
            }
        }
        if (!flag) {
            this.$store.commit('ADD_TAB', {route: this.$route.path , title: this.$route.meta.title, name:this.$route.name });
            this.$store.commit('SET_ACTIVE_TAB', this.$route.path);
        }
      }
    }
}
</script>

<style lang="scss" scoped>
.el-menu{
  border: none !important;
  background-color: #CDE4FB !important;
}
.fold{
    position: fixed;
    bottom: 0;
    width: 210px;
    font-size: 14px;
    // font-weight: bold;
    cursor: pointer;
    background-color: #CDE4FB;
    z-index: 6;
    i{
        position: relative;
        display:inline-block;
        width: 50px;
        height: 40px;
        line-height: 40px;
        text-align: center;
        font-size: 20px;
        // font-weight: bold;
        top: 3px;
    }
}
</style>