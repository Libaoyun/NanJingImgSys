<template>
    <div class="errorPage">
        <div class="container">
            <img src="@/assets/images/errorPage.png" alt="">
            <h2>404</h2>
            <div>
                <el-button size="small" @click="backOverview">返回首页</el-button>
                <el-button size="small" @click="reLogin">重新登录</el-button>
            </div>
        </div>
    </div>
</template>

<script>
import { Component, Vue } from 'vue-property-decorator'

@Component({
    name: 'errorPage'
})
export default class extends Vue {
    backOverview() {
        this.$router.push({ path: '/' })
    }
    reLogin() {
        this.$router.push({ path: '/login' })
        this.$store.commit('SET_USERINFO', null)
    }
}
</script>

<style lang="scss" scoped>
.errorPage{
    height: 100vh;
    .container{
        height: 100%;
        display: flex;
        align-items: center;
        padding-top: 200px;
        flex-flow: column nowrap;
        img{
            width:250px;
        }
        h2{
            margin-top: 20px;
        }
    }
}
</style>
