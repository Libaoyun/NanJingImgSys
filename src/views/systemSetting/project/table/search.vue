<template>
    <el-drawer
        title=""
        :visible="searchDialog"
        direction="rtl"
        custom-class="search-drawer"
        size="250px"
        @close="$emit('update:searchDialog',false)"
        ref="drawer">
        <span slot="title"><i class="iconfont iconsearch1"></i> 搜索</span>
        <div>
            <el-form :model="searchParams" :inline="true" size="mini" label-width="70px" label-position="right">
                <el-form-item label="项目名称" prop="orgName">
                    <el-input v-model.trim="searchParams.orgName" placeholder="请输入项目名称"></el-input>
                </el-form-item>
                <el-form-item label="备注" prop="remark">
                    <el-input v-model.trim="searchParams.remark" placeholder="请输入备注"></el-input>
                </el-form-item>
            </el-form>
            <div style="text-align:right;margin-top:10px">
                <el-button size="small" @click="resetForm">重置</el-button>
                <el-button size="small" type="primary" @click="searchSubmit">搜索</el-button>
            </div>
        </div>
    </el-drawer>
</template>

<script>
import { Component, Vue, Prop } from 'vue-property-decorator'

@Component({
    name: 'search'
})
export default class extends Vue {
    @Prop() searchDialog
    @Prop() searchParams

    resetForm(){
        this.$emit('update:searchParams',{});
    }
    searchSubmit() {
        this.$emit('searchSubmit');
        this.$emit('update:searchDialog',false)
    }
    
}
</script>

<style lang="scss" scoped>
</style>
