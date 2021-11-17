<template>
  <div class="footer-global">
    <div class="copyright">
      中铁十一局集团城市轨道工程有限公司&nbsp;版权所有
      <a-icon type="copyright" />
      {{new Date().getFullYear()}}
      <span></span>
    </div>
  </div>
</template>

<script>
import { Component, Vue } from 'vue-property-decorator'
@Component({
    name: 'footerGlobal'
})
export default class extends Vue {
}
</script>

<style lang="scss" scoped>
.footer-global{
  padding: 2px 0;
  text-align: center;
  .links {
    margin-bottom: 8px;
    a {
      color: rgba(0, 0, 0, 0.45);
      &:hover {
        color: rgba(0, 0, 0, 0.65);
      }
      &:not(:last-child) {
        margin-right: 40px;
      }
    }
  }
  .copyright {
    color: rgba(0, 0, 0, 0.45);
    font-size: 14px;
  }
}
</style>
