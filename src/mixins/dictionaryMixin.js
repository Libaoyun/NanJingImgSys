import Vue from 'vue';
import  Component  from 'vue-class-component';

@Component  // 一定要用Component修饰
export default class dictionaryMixin extends Vue {
    // 项目状态
    projectStatus = [
        { label: '启用', value: 1 },
        { label: '禁用', value: 0 }
    ]
}
