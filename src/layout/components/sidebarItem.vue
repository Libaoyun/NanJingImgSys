<template>
  <div class="sidebar-wrapper" :class="{ hideSidebar: !sidebar.opened }">
    <template v-for="item in routes">
      <router-link
        v-if="
          !item.hidden &&
            item.noDropdown &&
            item.children.length > 0 &&
            !firstItem.hidden
        "
        v-for="firstItem in item.children"
        :to="item.path + firstItem.path"
        :key="firstItem.name"
      >
        <el-menu-item
          :index="item.path + firstItem.path"
          class="submenu-title-first"
        >
          <i v-if="firstItem.icon" :class="firstItem.icon" class="iconfont"></i
          ><span class="nav-title">{{ firstItem.meta.title }}</span>
        </el-menu-item>
      </router-link>
      <el-submenu
        :index="item.name"
        v-if="!item.noDropdown && !item.hidden"
        :key="item.name"
        class="submenu-title-dropdown"
        style="padding-left:0"
      >
        <template slot="title">
          <i v-if="item.icon" :class="item.icon" class="iconfont"></i>
          <span class="nav-title">{{ item.meta.title }}</span>
        </template>

        <template v-for="child in item.children">
          <template v-if="!child.hidden">
            <sidebar-item
              class="nest-menu"
              v-if="child.children && child.children.length > 0"
              :routes="[child]"
              :key="child.name"
            >
            </sidebar-item>
            <router-link
              v-else
              :to="item.path + '/' + child.path"
              :key="child.name"
            >
              <el-menu-item
                :index="item.path + '/' + child.path"
                class="submenu-title-secondOrThird"
              >
                <i v-if="child.icon" :class="child.icon" class="iconfont"></i
                ><span class="nav-title">{{ child.meta.title }}</span>
              </el-menu-item>
            </router-link>
          </template>
        </template>
      </el-submenu>
    </template>
  </div>
</template>
<script>
import { Component, Prop, Vue } from 'vue-property-decorator'

@Component({
  name: 'sidebarItem',
})
export default class extends Vue {
  @Prop() routes

  get sidebar() {
    return this.$store.getters.sidebar
  }
}
</script>
<style lang="scss">
@import '@/assets/scss/element-variables.scss';
.sidebar-wrapper {
  .submenu-title-first,
  .submenu-title-secondOrThird.el-menu-item,
  .submenu-title-dropdown .el-submenu__title {
    height: 40px;
    line-height: 40px;
    padding-left: 50px !important;
    cursor: pointer;
    color: #333;
    font-weight: 400;
    &:hover {
      background-color: transparent;
      color: $--color-primary;
      cursor: pointer;
    }
    i {
      font-size: 14px;
      font-weight: bold;
      color: unset;
    }
    .iconfont {
      position: absolute;
      left: 0;
      top: 0;
      width: 50px;
      height: 40px;
      text-align: center;
      font-size: 16px;
      z-index: 2;
    }
  }
  // 激活菜单
  .el-menu-item.is-active {
    background-color: #ecf4ff !important;
  }
  &.nest-menu .submenu-title-secondOrThird.el-menu-item {
    padding-left: 70px !important;
  }
  &.hideSidebar {
    .el-submenu__title .iconfont {
      background-color: #cde4fb;
    }
    .el-submenu.is-active .iconfont {
      background-color: #badff1;
      // color: $--color-primary;
    }
  }
}
.el-menu--popup-right-start .el-menu-item.is-active {
  background-color: #badff1 !important;
}
.el-menu--popup-right-start {
  margin-left: 0;
  min-width: 180px;
  padding: 0;
  .el-menu-item {
    height: 40px;
    line-height: 40px;
  }
  .submenu-title-dropdown.el-submenu .el-submenu__title,
  .submenu-title-dropdown.el-submenu
    .el-menu--popup-right-start
    .el-menu-item.submenu-title-secondOrThird {
    padding-left: 20px !important;
  }
}
.el-menu--inline {
  // max-height: 200px;
  background-color: transparent !important;
  overflow-y: auto;
  overflow-x: hidden;
  &::-webkit-scrollbar {
    // display: none
  }
}
</style>
