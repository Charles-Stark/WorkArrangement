<template>
  <div>
    <div v-if="!show3">
      <v-container v-if="!show2">

        <v-img
          :src="require('../../assets/logo-md-dark.png')"
          :width="$vuetify.breakpoint.xsOnly ? 300 : 250" class="mx-auto">
        </v-img>
        <v-row>
          <v-row class="mt-8">
            <v-col cols="4">
              <span class="text-h6 ml-3">欢迎回来</span>
            </v-col>
            <v-spacer></v-spacer>
            <v-col cols="4">
              <v-btn text class="text-subtitle-1" @click="loginMethod = loginMethod === 2 ? 1 : 2">
                <span class="mb-1" v-text="loginMethod === 1 ? '密码登录' : '验证码登录'"></span> </v-btn>
            </v-col>
          </v-row>

          <!-- 邮箱登录 -->
          <template v-if="loginMethod === 1">
            <v-col cols="12" class="mt-4">
              <v-text-field label="邮箱" outlined prepend-inner-icon="mdi-email" placeholder="hello@email.com"
                v-model="email" @keyup.enter="getOTP()"></v-text-field>

              <v-btn color="primary" @click="getOTP()" class="mx-auto mt-5" block height="55" :loading="loading">
                <span class="text-subtitle-1">继续</span>
              </v-btn>

            </v-col>
          </template>

          <!-- 密码登录 -->
          <template v-if="loginMethod === 2">
            <v-col cols="12">
              <v-text-field label="邮箱" outlined prepend-inner-icon="mdi-email" placeholder="hello@email.com"
                v-model="email"></v-text-field>
            </v-col>
            <v-col cols="12">
              <v-text-field v-model="password" :append-icon="show1 ? 'mdi-eye' : 'mdi-eye-off'"
                prepend-inner-icon="mdi-key" outlined :type="show1 ? 'text' : 'password'" name="input-10-1" label="密码"
                @keyup.enter="submit()" @click:append="show1 = !show1">
              </v-text-field>
            </v-col>
            <v-col cols="4">
              <v-btn color="primary" block height="55" outlined @click="show3 = true">
                <span class="text-subtitle-1">忘记密码</span>
              </v-btn>
            </v-col>
            <v-col cols="8">
              <v-btn color="primary" @click="submit()" class="mx-auto" block height="55">
                <span class="text-subtitle-1">登录</span>
              </v-btn>
            </v-col>

          </template>
        </v-row>
      </v-container>

      <template v-else>
        <v-btn text @click="show2 = false">返回</v-btn>
        <v-otp-input v-model="otp" @finish="submit()" class="my-10"></v-otp-input>
        <v-btn block color="primary" height="55" class="mt-4" :disabled="counter !== 0"
          v-text="counter !== 0 ? `在${counter}秒后重试` : '重新获取验证码'" @click="getOTP()"></v-btn>
      </template>
    </div>

    <!-- 忘记密码 -->
    <div v-else>
      <v-row>
        <v-btn text @click="show3 = false">返回</v-btn>
      </v-row>
      <v-row justify="center">
        <v-col cols="8">
          <v-text-field v-model="email" label="邮箱" required outlined :rules="rules.emailRules"></v-text-field>
        </v-col>

        <v-col cols="4">
          <v-btn color="primary" @click="getOTP()" class="mx-auto" block height="55" outlined
            v-text="counter === 0 ? '获取验证码' : `${counter}秒后重试`" :disabled="counter !== 0"></v-btn>
        </v-col>

        <v-col cols="12">
          <v-text-field v-model="otp" label="验证码" required outlined></v-text-field>
        </v-col>

        <v-col cols="12">
          <v-text-field v-model="resetPsw" label="密码" required outlined type="password"
            :rules="rules.pswRules"></v-text-field>
        </v-col>

        <v-col cols="12">
          <v-text-field v-model="repsw" label="重复密码" required outlined type="password"
            :rules="rules.repswRules"></v-text-field>
        </v-col>

        <v-col cols="12">
          <v-btn color="primary" class="mx-auto" block height="55" :loading="loading" @click="pswReset()">
            <span class="text-subtitle-1">修改密码</span>
          </v-btn>
        </v-col>
      </v-row>
    </div>


  </div>
</template>

<script>
import { getOTP, OTPLogin, pswLogin, pswReset } from '../../request/user'
export default {

  data: () => ({
    loading: false,
    pswLogin: true,
    show1: false,
    show2: false,
    show3: false,
    loginMethod: 1,
    email: '',
    password: '',
    resetPsw: '',
    repsw: '',
    otp: '',
    counter: 0,
  }),
  computed: {
    rules() {
      return {
        emailRules: [
          v => !!v || '邮箱不能为空',
          v => /.+@.+/.test(v) || '邮箱格式错误',
        ],
        pswRules: [
          v => !!v || '密码不能为空',
          v => v.length >= 8 || '密码长度需大于8',
        ],
        repswRules: [
          v => this.resetPsw === v || '两次输入密码不匹配',
        ],
      }
    },
  },
  methods: {
    submit() {
      this.loading = true
      if (this.loginMethod === 1) {
        OTPLogin(this.email, this.otp).then(res => {
          if (res.data.code === 0) {
            this.$emit('msg', '登录成功')

            //将登录信息保存在vuex和localstorage中
            this.$store.commit('setLoginInfo', { token: res.data.data.token, userId: res.data.data.id })
            this.$router.go(0)
          }
          else if (res.data.code === -1) {
            this.$emit('msg', '验证码错误')
          }
        }).catch(() => {
          this.$emit('msg', '网络错误')
        })
      }
      else {
        var validated = true

        if (this.email === '') {
          validated = false
          this.$emit('msg', '邮箱不能为空')
        }
        else if (!/.+@.+/.test(this.email)) {
          validated = false
          this.$emit('msg', '邮箱格式错误')
        }
        else if (this.password === '') {
          validated = false
          this.$emit('msg', '密码不能为空')
        }

        if (validated) {
          pswLogin(this.email, this.password).then(res => {
            if (res.data.code === 0) {
              this.$emit('msg', '登录成功')

              //将登录信息保存在vuex和localstorage中
              this.$store.commit('setLoginInfo', { token: res.data.data.token, userId: res.data.data.id })
              this.$router.go(0)
            }
            else if (res.data.code === -1) {
              if (res.data.message === '用户不存在') {
                this.$emit('msg', '用户未注册')
              }
              else if (res.data.message === '密码错误') {
                this.$emit('msg', '密码错误')
              }
            }
          }).catch(() => {
            this.$emit('msg', '网络错误')
          })
        }
      }
      this.loading = false
    },
    getOTP() {
      if (this.email !== '') {
        if (/.+@.+/.test(this.email)) {

          if (this.counter === 0) {
            this.loading = true

            getOTP(this.email).then(res => {
              if (res.data.code === 0) {
                this.$emit('msg', '验证码已发送')
                this.loading = false
                this.show2 = true;
                if (this.counter === 0) {
                  this.counter = 60;
                  var count = setInterval(() => {
                    this.counter--
                    if (this.counter <= 0) {
                      clearInterval(count)
                      this.counter = 0
                    }
                  }, 1000);
                }

              }
              else if (res.data.code === -1) {
                this.$emit('msg', '用户未注册')
                this.loading = false
              }
            }).catch(() => {
              this.$emit('msg', '网络错误')
                this.loading = false
            })
          }
          else {
            this.show2 = true
          }


        }
        else {
          this.$emit('msg', '邮箱格式错误')
        }
      }
      else {
        this.$emit('msg', '邮箱不能为空')
      }


    },
    pswReset() {
      if (this.validate()) {
        this.loading = true
        pswReset(this.email, this.resetPsw, this.otp).then(res => {
          if (res.data.code === 0) {
            this.$emit('msg', '密码修改成功')
            this.$router.go(0)
          }
          else if (res.data.code === -1) {

            if (res.data.message === '用户不存在') {
              this.$emit('msg', '用户未注册')
            }
            else if (res.data.message === '验证失败') {
              this.$emit('msg', '验证码错误')
            }

          }
        }).catch(() => {
          this.$emit('msg', '网络错误')
        })
        this.loading = false
      }
    },
    validate() {
      for (let v of this.rules.emailRules) {
        if (v(this.email) !== true) {
          this.$emit('msg', v(this.email))
          return false
        }
      }

      if (!this.otp) {
        this.$emit('msg', '请输入验证码')
        return false
      }

      for (let v of this.rules.pswRules) {
        if (v(this.resetPsw) !== true) {
          this.$emit('msg', v(this.resetPsw))
          return false
        }
      }

      for (let v of this.rules.repswRules) {
        if (v(this.repsw) !== true) {
          this.$emit('msg', v(this.repsw))
          return false
        }
      }

      return true
    }
  },


}
</script>
