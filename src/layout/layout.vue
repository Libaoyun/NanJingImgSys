<template>
    <el-container>
        <el-header height="60px">
            <navbar></navbar>
        </el-header>
        <el-container style="padding-top:60px" :class="{hideSidebar:!sidebar.opened}">
            <el-aside width="210px">
                <sidebar class="sidebar-frame"></sidebar>
            </el-aside>
            <el-main>
                <app-main></app-main>
            </el-main>
        </el-container>
    </el-container>
</template>

<script>
import { Component, Vue } from 'vue-property-decorator'
import AppMain from './components/appMain.vue'
import Navbar from './components/navbar.vue'
import Sidebar from './components/sidebar.vue'

@Component({
    name: 'layout',
    components: {
        AppMain,
        Navbar,
        Sidebar
    }
})
export default class extends Vue {

    get sidebar() {
      return this.$store.getters.sidebar
    }
}
</script>

<style lang="scss" scoped>
@import '@/assets/scss/element-variables.scss';
.el-header{
    position: fixed;
    top: 0;
    width: 100%;
    line-height: 60px;
    padding: 0 30px;
    // #5ea2ea
    background-color: #B4D9FC;
    -webkit-box-shadow: 0 1px 4px rgba(0,21,41,.08);
    box-shadow: 0 1px 4px rgba(0,21,41,.08);
    z-index: 10;
}
.el-aside{
  position: fixed;
  width: 180px;
  top: 60px;
  bottom: 0;
  left: 0;
  z-index: 1001;
  padding-top: 15px;
  overflow-y: auto;
  background-color: #CDE4FB;
  box-shadow: 0 0 1px 0 #ccc;
  transition: all 0.18s linear;
  &::-webkit-scrollbar {
    display: none
  }
}
.el-main {
    height: calc(100vh - 60px);
    transition: all 0.18s linear;
    margin-left: 210px;
    padding: 0;
}
.hideSidebar{
    .el-aside{
        transform: translate(-160px, 0);
    }
    .sidebar-frame{
        height: 100%;
        transform: translate(160px, 0);
    }
   .el-main {
       margin-left: 50px;
   } 
}
</style>
