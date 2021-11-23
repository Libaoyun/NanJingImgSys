<template>
    <div class="navbar">
        <el-select v-model="selectedOrganization" @change="changeOrganization" size="small" :popper-append-to-body="false">
            <el-option v-for="item in organizationList" :label="item.organizationName" :value="item.organizationId" :key="item.organizationId"></el-option>
        </el-select>
        <el-dropdown>
            <span class="el-dropdown-link">
                <i class="iconfont icon-yonghu" style="font-size:18px"></i>
                {{username}}<i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
                <el-dropdown-item class="iconfont icontuichu" @click.native="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
        </el-dropdown>
    </div>
</template>

<script>
import { Component, Vue } from 'vue-property-decorator'
import {resetRouter} from '@/router/index.js'

@Component({
    name: 'navbar'
})
export default class extends Vue {
    get username() {
       return this.$store.getters.userInfo ? this.$store.getters.userInfo.userName : ''
    }
    get organizationList() {
        return this.$store.getters.userInfo ? this.$store.getters.userInfo.organizationList : []
    }
    selectedOrganization = ''
    mounted() {
        if(this.organizationList.length > 0) {
            this.selectedOrganization = this.organizationList[0].organizationId
            this.$store.commit('SET_CURRENT_ORGANIZATION', this.organizationList[0]);
        }
    }
    changeOrganization(e) {
        var currentArr = this.organizationList.filter(v=>v.organizationId === e)
        this.$store.commit('SET_CURRENT_ORGANIZATION',currentArr[0]);
        resetRouter(this.$store.getters.userInfo).then(res=>{
            this.$router.push({ path: '/' })
            // 清除tab标签
            if(this.$route.path == '/overview'){
                this.$store.commit('CLEAR_TAB_LIST_OUTSELF',{route: this.$route.path , title: this.$route.meta.title })
            }else{
                this.$store.commit('CLEAR_TAB_LIST')
            }
        })
        
        
    }
    logout() {
        this.$store.dispatch('Logout').then(() => {
            this.$router.push({ path: '/login' })
            this.$store.commit('SET_USERINFO', null)
        })
    }
}
</script>

<style lang="scss" scoped>
.navbar{
    height: 60px;
    line-height: 60px;
    text-align: right;
    padding: 0 15px;
}
.el-select {
    margin-right: 20px;
    /deep/{
        input {
            border: none;
            background-color: #B4D9FC;
            text-align: right;
        }
        .el-scrollbar {
            text-align: left;
        }
        .el-popper .popper__arrow::after {
            margin-left: 100px !important;
        }
    }
}
.el-dropdown-link {
    font-size: 14px;
    font-weight: bold;
    cursor: pointer;
}
.el-icon-arrow-down {
    font-size: 13px;
}
</style>