<template>
    <el-button 
        :size="size" 
        :icon="icon"
        @click="loadingStart" 
        :element-loading-text="elementLoadingText"
        :element-loading-background="elementLoadingBackground" 
        v-loading.fullscreen.lock="isLoading"
        :type="type"
    >
        <slot></slot>
    </el-button>
</template>

<script>
import { Component, Vue, Prop } from 'vue-property-decorator'

@Component({
    name: 'loadingBtn'
})
export default class extends Vue {
    // loading背景颜色
    @Prop({default:'rgba(255, 255, 255,.5)',type:String}) elementLoadingBackground
    // loading图案
    @Prop({default:'el-loading-spinner',type:String}) elementLoadingSpinner
    // loading文本
    @Prop({default:'   ',type:String}) elementLoadingText
    // 按钮图标
    @Prop({default:'',type:String}) icon
    // loading标识
    @Prop({default:0,type:Number}) loading
    // button大小
    @Prop({default:'small',type:String}) size
    // button颜色
    @Prop({default:'',type:String}) type


    // 标识当前按钮被点击
    isClick = false;

    // 是否开始loading
    get isLoading(){
        return this.loading && this.isClick;
    }

    loadingStart(){
        this.isClick = true;
        this.$emit('click');
    }
}
</script>

<style lang="scss" scoped>
</style>
