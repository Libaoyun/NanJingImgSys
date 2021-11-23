<template>
    <div class="wrapper">
        <div>
            <p style="font-size: 25px;font-weight:bold">{{welcomeWord}}，{{username}}</p>
            <p>{{currentTime}}</p>
        </div>
    </div>
</template>

<script>
import { Component, Vue } from 'vue-property-decorator'

@Component({
    name: 'welcome'
})
export default class extends Vue {
    currentTime = null
    dayMap = ['星期日','星期一','星期二','星期三','星期四','星期五','星期六'] 
    // 欢迎语
    welcomeWord = '欢迎'
    welcomeWordMap = ['上午好','下午好']
    get username() {
       return this.$store.getters.userInfo.userName
    }

    mounted() {
        var time = new Date();
        this.currentTime = time.getFullYear() + "年" + (time.getMonth() + 1) + "月" + time.getDate() + "日" + " " +this.dayMap[time.getDay()];
        if(time.getHours()>12){
            this.welcomeWord = this.welcomeWordMap[1]
        }else{
            this.welcomeWord = this.welcomeWordMap[0]
        }
    }
}
</script>

<style lang="scss" scoped>
.wrapper{
    position: relative;
    height: calc(100vh - 130px);
    background-color: #fff;
    padding: 15px;
    div{
        position: absolute;
        width: 100%;
        top: 50%;
        transform: translateY(-50%)
    }
    p{
        text-align: center;
        margin-bottom: 20px;
    }
}
</style>
