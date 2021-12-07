import Vue from 'vue';
import  Component  from 'vue-class-component';

@Component  // 一定要用Component修饰
export default class tableMixin extends Vue {
    // 动态：作为参数传入后端
    listQuery = {
        page: 1,
        limit: 1
    }
    // 静态：表格页面展示配置
    tableConfig = {
        maxHeight: '500',//表格高度超过500，将固定表头表格内容滚动
        border: true,
        loadingIcon: 'el-icon-loading',
        pageSizeList: [1,15, 30, 50, 100],
        layout: '->,total, sizes, prev, pager, next, jumper'

    }
    tableHeight = 0
    mounted() {
        this.$nextTick(() => {
            this.tableHeight = window.innerHeight - 260;
        })
    }
    resetPageNum(){
        this.listQuery.page = 1;
    }
}
