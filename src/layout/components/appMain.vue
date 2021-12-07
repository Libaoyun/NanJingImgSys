<template>
  <section class="app-main">
    <multi-tab></multi-tab>
    <transition name="fade" mode="out-in">
      <div
          style="background-color:#e6e5e5;min-height:calc(100vh - 100px);padding:15px"
          :style="{ height: $route.name == 'dashboard' ? '100vh' : 'auto' }"
      >
        <keep-alive>
          <router-view v-if="keepAlive"></router-view>
        </keep-alive>
        <keep-alive :include="multiTabView">
          <router-view v-if="!keepAlive"></router-view>
        </keep-alive>
      </div>
    </transition>
  </section>
</template>

<script>
import { Component, Vue, Watch } from 'vue-property-decorator'
import multiTab from './multiTab.vue'
@Component({
  name: 'appMain',
  components: {
    multiTab,
  },
})
export default class extends Vue {
  get multiTabView() {
    return this.$store.state.multiTab.multiTabList?.map((item) => item.name);
  }

  // keepAlive = false
  get keepAlive() {
    console.log(this.$route);
    return this.$route.meta.keepAlive || false;
  }

  // @Watch('$route')
  // onChangeRoute(to){
  //     this.keepAlive = to.meta.keepAlive || false
  // }
}
</script>

<style lang="scss" scoped>
.app-main {
  background-color: #fff;
}
</style>
