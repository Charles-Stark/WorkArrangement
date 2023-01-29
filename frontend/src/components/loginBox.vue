<template>
  <div>
    <v-container v-if="!show2">
      <div>

      </div>
      <v-img
        :src="$vuetify.theme.dark === false ? require('../assets/logo-md.png') : require('../assets/logo-md-dark.png')"
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

        <template v-if="loginMethod === 1">
          <v-col cols="12" class="mt-4">
            <v-text-field label="邮箱" outlined prepend-inner-icon="mdi-email" placeholder="hello@email.com"
              v-model="email" @keyup.enter="getOTP()"></v-text-field>

            <v-btn color="primary" @click="getOTP()" class="mx-auto mt-5" block height="55">
              <span class="text-subtitle-1">继续</span>
            </v-btn>

          </v-col>
        </template>

        <template v-if="loginMethod === 2">
          <v-col cols="12" class="mt-7">
            <v-text-field label="邮箱" outlined prepend-inner-icon="mdi-email" placeholder="hello@email.com"
              v-model="email"></v-text-field>
          </v-col>
          <v-col cols="12">
            <v-text-field v-model="password" :append-icon="show1 ? 'mdi-eye' : 'mdi-eye-off'"
              prepend-inner-icon="mdi-key" outlined :type="show1 ? 'text' : 'password'" name="input-10-1" label="密码"
              @keyup.enter="submit()" @click:append="show1 = !show1">
            </v-text-field>
          </v-col>
          <v-col cols="12">
            <v-btn color="primary " @click="submit()" class="mx-auto" block height="55">
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
</template>

<script>
import {getOTP} from '../request/login/api'
export default {

  data: () => ({
    pswLogin: true,
    show1: false,
    show2: false,
    emailRules: [
      v => !!v || '请输入邮箱',
      v => /.+@.+/.test(v) || '请输入正确的格式',
    ],
    loginMethod: 1,
    email: '',
    password: '',
    otp: '',
    counter: 0,
  }),
  methods: {
    submit() {
      // if(this.loginMethod===1){

      // }
      // else{

      // }
    },
    getOTP() {
      if (this.email !== '') {
        if (/.+@.+/.test(this.email)) {
          

          this.show2 = true;
          if(this.counter===0){
            this.counter = 60;
          var count = setInterval(() => {
            this.counter--
            if (this.counter <= 0) {
              clearInterval(count)
              this.counter=0
            }
          }, 1000);

            getOTP(this.email).then(res => {
              console.log(res)
              if (res.data.code === -1) {
                console.log()
              }
            }).catch(err => console.log(err))
            
          }
          
        }
        else {
          this.$emit('msg', '邮箱格式错误')
        }
      }
      else {
        this.$emit('msg', '邮箱不能为空')
      }


    }
  },


}
</script>
