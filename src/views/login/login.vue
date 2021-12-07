<template>
  <div class="wrapper">
    <div class="login-frame">
      <div class="login-title">
        <!-- <div class="login-logo"><img src="@/assets/images/logo.png" alt=""></div> -->
        <h2>研发费用辅助管理系统</h2>
      </div>
      <div class="login-form">
        <el-form :rules="loginRules" ref="loginAccountForm" :model="loginInfo">
          <el-form-item prop="userCode" style="margin-top: 10px">
            <el-input
                ref="userCode"
                placeholder="用户名"
                prefix-icon="iconfont icon-yonghu"
                v-model="loginInfo.userCode"
                auto-complete="off"
                @keyup.enter.native="handleFormLogin"
            >
            </el-input>
          </el-form-item>
          <el-form-item prop="password" style="margin-bottom: 40px">
            <el-input
                ref="password"
                placeholder="密码"
                prefix-icon="iconfont icon-lock"
                v-model="loginInfo.password"
                type="password"
                auto-complete="new-password"
                @keyup.enter.native="handleFormLogin"
            >
            </el-input>
          </el-form-item>
        </el-form>
        <div class="loginBtn">
          <el-button
              type="primary"
              style="width:100%;"
              @click.native.prevent="handleFormLogin()"
              :loading="loadingBtn == 1"
          >登 录
          </el-button
          >
        </div>
      </div>
      <div class="login-mode">
        <el-tooltip
            class="item"
            effect="dark"
            content="请联系管理员重置密码！"
            placement="top-start"
        >
          <span>忘记密码?</span>
        </el-tooltip>
      </div>
    </div>
  </div>
</template>

<script>
import { Component, Vue } from 'vue-property-decorator'

@Component({
  name: 'login',
})
export default class extends Vue {
  loginInfo = {
    userCode: '',
    password: '',
  };
  loadingBtn = 0;
  loginRules = {
    userCode: [{required: true, message: '请输入用户名', trigger: 'change'}],
    password: [{required: true, message: '请输入密码', trigger: 'change'}],
  };

  mounted() {
  }

  handleFormLogin() {
    this.$refs['loginAccountForm'].validate((valid) => {
      if (valid) {
        this.loadingBtn = 1;
        this.$store
            .dispatch('Login', this.loginInfo)
            .then((res) => {
              this.loadingBtn = 0;
              if (res.data.firstLogin) {
                this.$router.push({path: '/updatePassword'});
              } else {
                this.$router.push({path: '/'});
              }
            })
            .catch(() => {
              this.loadingBtn = 0;
            });
      } else {
        return false;
      }
    });
  }
}
</script>

<style lang="scss" scoped>
@import '@/assets/scss/element-variables.scss';

.wrapper {
  position: relative;
  height: 100vh;
  background: url('~@/assets/images/login_bg.jpg') no-repeat 50%;
  background-size: cover;
  color: #000;

  .login-frame {
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

      .login-logo {
        padding-top: 20px;
        margin-right: 8px;

        img {
          width: 40px;
          height: 40px;
        }
      }

      h2 {
        font-size: 27px;
      }
    }

    .login-form {
      position: relative;
      height: 220px;
      padding: 0 10px;

      .loginBtn {
        position: absolute;
        width: calc(100% - 20px);
        bottom: 30px;
      }
    }

    .login-mode {
      text-align: center;
      color: #6c757d;
      font-weight: 500;
      font-size: 14px;
      cursor: pointer;

      span {
        cursor: pointer;
      }

      span:hover {
        color: $--color-primary;
      }
    }
  }
}
</style>
