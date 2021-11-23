<template>
    <div class="wrapper">
        <div class="login-frame">
            <div class="login-title">
                <!-- <div class="login-logo"><img src="@/assets/images/logo.png" alt=""></div> -->
                <h2>修改密码</h2>
            </div>
            <div class="login-form">
                <el-form ref="formData" :model="formData" :rules="rules">
                    <el-form-item label="" prop="oldPassword">
                        <el-input v-model.trim="formData.oldPassword" placeholder="请输入旧密码" type="password" :show-password="true"></el-input>
                    </el-form-item>
                    <el-form-item label="" prop="password">
                        <el-input v-model.trim="formData.password" placeholder="请输入新密码" type="password" :show-password="true"></el-input>
                    </el-form-item>
                    <el-form-item label="" prop="confirmPassword">
                        <el-input v-model.trim="formData.confirmPassword" placeholder="请确认新密码" type="password" :show-password="true"></el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="onSubmit" style="width: 100%">提交</el-button>
                    </el-form-item>
                </el-form>
                <div class="login-mode">
                    <span @click="jumpToLogin">返回登录</span>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { Component, Vue } from 'vue-property-decorator'

@Component({
    name: 'updatePassword'
})
export default class extends Vue {
    validatePass = (rule, value, callback) => {
        if (value === '') {
            callback(new Error('请确认新密码'));
        } else if (value !== this.formData.password) {
            callback(new Error('两次输入密码不一致'));
        } else {
            callback();
        }
    };
    formData={}
    loadingBtn = 0
    rules= {
        oldPassword: [
          { required: true, message: '请输入旧密码', trigger: 'change' },
        ],
        password: [
          { required: true, message: '请输入新密码', trigger: 'change' },
          { pattern:/^\w{3,15}$/, message: '密码可由数字、字母、下划线组成，且密码位数为3-15位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认新密码', trigger: 'change' },
          { pattern:/^\w{3,15}$/, message: '密码可由数字、字母、下划线组成，且密码位数为3-15位', trigger: 'blur' },
          { validator: this.validatePass, trigger: 'blur' }
        ]
    }
    mounted() {
    }
    jumpToLogin() {
        this.$router.push({ path: '/login' })
    }
    onSubmit() {
        console.log(this.$API)
        this.$refs['formData'].validate((valid) => {
            if (valid) {
                this.$API.apiUpdatePassword(this.formData).then(()=>{
                    this.$message({
                        type: 'success',
                        message: '密码修改成功!'
                    });
                    this.$router.push({ path: '/login' })
                }).catch((err)=>{

                })
            } else {
            return false
            }
        })
    }
}
</script>

<style lang="scss" scoped>
@import '@/assets/scss/element-variables.scss';
.wrapper{
    position: relative;
    height: 100vh;
    background: url('~@/assets/images/login_bg.jpg') no-repeat 50%;
    background-size: cover;
    color: #000;
    .login-frame{
        position: absolute;
        top: 50%;
        right: 10%;
        width: 360px;
        height: 380px;
        margin-top: -230px;
        padding: 10px 20px 20px;
        background-color: #fff;
        border-radius: 5px;
        box-shadow: 0 0 5px 2px #ccc;
        z-index: 2;
        .login-title {
            display: flex;
            height: 80px;
            justify-content: center;
            .login-logo{
                padding-top: 20px;
                margin-right: 8px;
                img{
                    width: 40px;
                    height: 40px;
                }
            }
            h2{
                font-size: 27px;
            }
        }
        .login-form {
            position: relative;
            height: 220px;
            padding: 0 10px;
            .loginBtn{
                position: absolute;
                width: calc(100% - 20px);
                bottom: 30px;
            }
        }
        .login-mode{
            text-align: center;
            color:#6c757d;
            font-weight: 500;
            font-size: 14px;
            cursor: pointer;
            span{
              cursor: pointer;  
            }
            span:hover{
                color: $--color-primary;
            }
        }
    }
}
</style>
