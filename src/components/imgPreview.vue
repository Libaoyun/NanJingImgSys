<template>
    <el-dialog
    :visible="isShowImageDialog"
    @close="$emit('update:isShowImageDialog',false)"
    @closed="clearImg"> 
        <!-- <el-carousel indicator-position="outside" height="300px">
        <el-carousel-item v-for="src in imgPreviewList" :key="src">
            <img :src="src" style="max-width: 100%;max-height: 100%;display: block; margin: 0 auto;"/>
        </el-carousel-item>
        </el-carousel> -->
        <img :src="imgPreviewSrc" style="max-width: 100%;max-height: 100%;display: block; margin: 0 auto;"/>
    </el-dialog>
</template>

<script>
import { Component, Vue, Prop } from 'vue-property-decorator'

@Component({
    name: 'imgPreview'
})
export default class extends Vue {
    @Prop() imgPreviewSrc;
    @Prop() isShowImageDialog;

    clearImg() {
    //   this.isShowImageDialog = false
    }
}
</script>

<style lang="scss" scoped>
</style>
