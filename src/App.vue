<template>
  <div id="app">
    <router-view />
  </div>
</template>

<style lang="scss">
@import '@/assets/scss/reset.scss';
@import '@/assets/scss/global.scss';
@import '@/assets/scss/panel.scss';
#app {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB',
    'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: rgba(0, 0, 0, 0.65);
}
</style>
