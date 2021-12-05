import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui'
import Storage from 'vue-ls'
import moment from 'moment' //导入模块
import '@/api/index'
import '@/assets/scss/variables.scss'
import '@/assets/fonts/iconfont/iconfont.css'
import * as filterList from './filters/index.js'
import './permission'
import { checkPermission } from './directives/checkPermission.js'
import {
  CardGlobal,
  ImgPreview,
  UploadApprovalGlobal,
  ApprovalGlobal,
  LoadingBtn,
  FooterGlobal,
} from '@/components/index'
import { menuCodeList } from '@/utils/menuCodeList.js'
import { judgeBtn } from '@/utils/judgeBtn.js'
import { exportFile } from '@/utils/export.js'
import {
  getDictionariesList,
  getDictionariesText,
} from '@/utils/getDictionariesList.js'

Vue.prototype.MENU_CODE_LIST = menuCodeList
Vue.prototype.JUDGE_BTN = judgeBtn
Vue.prototype.EXPORT_FILE = exportFile
Vue.prototype.GET_DICTIONARY_LIST = getDictionariesList
Vue.prototype.GET_DICTIONARY_TEXT = getDictionariesText

let storageOptions = {
  namespace: 'pro__', // key prefix
  name: 'ls', // name variable Vue.[ls] or this.[$ls],
  storage: 'local', // storage name session, local, memory
}
moment.locale('zh-cn') //设置语言 或 moment.lang('zh-cn');

Vue.prototype.$moment = moment //赋值使用
Vue.config.productionTip = false

Object.keys(filterList).forEach((key) => {
  Vue.filter(key, filterList[key])
})

Vue.use(ElementUI)
Vue.use(Storage, storageOptions)
Vue.use(CardGlobal)
Vue.use(ImgPreview)
Vue.use(UploadApprovalGlobal)
Vue.use(ApprovalGlobal)
Vue.use(LoadingBtn)
Vue.use(FooterGlobal)
Vue.directive('checkPermission', checkPermission)

new Vue({
  router,
  store,
  render: (h) => h(App),
}).$mount('#app')
