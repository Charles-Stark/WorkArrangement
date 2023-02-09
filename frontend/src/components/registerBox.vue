<template>
  <div>
    <v-form ref="form" lazy-validation>
      <v-row justify="center">

        <v-col cols="12">
          <v-text-field v-model="name" :counter="10" label="姓名" :rules="rules.nameRules" required
            outlined></v-text-field>
        </v-col>

        <v-col cols="8">
          <v-text-field v-model="email" :rules="rules.emailRules" label="邮箱" required outlined></v-text-field>
        </v-col>

        <v-col cols="4">
          <v-btn color="primary" @click="getOTP()" class="mx-auto" block height="55" outlined
            v-text="counter === 0 ? '获取验证码' : `${counter}秒后重试`" :disabled="counter !== 0"></v-btn>
        </v-col>

        <v-col cols="12">
          <v-text-field v-model="otp" label="验证码" required outlined></v-text-field>
        </v-col>

        <v-col cols="12">
          <v-text-field v-model="password" label="密码" required outlined :rules="rules.pswRules" type="password"></v-text-field>
        </v-col>

        <v-col cols="12">
          <v-text-field v-model="repsw" label="重复密码" required outlined :rules="rules.repswRules" type="password"></v-text-field>
        </v-col>


        <v-col cols="12">
          <v-btn color="primary " @click="submit()" class="mx-auto" block height="55" :loading="loading">
            <span class="text-subtitle-1">注册</span>
          </v-btn>
        </v-col>

      </v-row>

    </v-form>


  </div>
</template>

<script>
import { getRegisterOTP,register } from '../request/user'

export default {

  data: () => ({
    counter: 0,
    loading:false,

    name: '',
    email: '',
    otp: '',
    password: '',
    repsw: '',
  }),
  computed: {
    rules() {
      return {
        nameRules: [
          v => !!v || '请输入姓名',
          v => v.length <= 10 || '姓名长度不能大于10'
        ],
        emailRules: [
          v => !!v || '邮箱不能为空',
          v => /.+@.+/.test(v) || '邮箱格式错误',
        ],
        pswRules: [
          v => !!v || '密码不能为空',
          v => v.length >= 8 || '密码长度不能小于8位',
        ],
        repswRules: [
          v => this.password === v || '两次输入密码不匹配',
        ],

      }
    },
  },
  methods: {
    submit() {
      if (this.validate()) {
        this.loading=true
        register(this.email,this.password,this.name,this.otp).then(res=>{
          if(res.data.code===0){
            this.$emit('msg', '注册成功')
            this.$store.commit('setLoginInfo', { token: res.data.data.token, userId: res.data.data.id })
            this.$router.go(0)
          }
          if(res.data.code===-1){
            this.$emit('msg', '验证码错误')
          }
        }).catch(() => {
          this.$emit('msg', '网络错误')
        })
        this.loading = false

      }
    },
    getOTP() {
      if (this.email !== '') {
        if (/.+@.+/.test(this.email)) {
          if (this.counter === 0) {
            getRegisterOTP(this.email).then(res => {
              if(res.data.code===0){
                this.counter = 60;
                var count = setInterval(() => {
                  this.counter--
                  if (this.counter <= 0) {
                    clearInterval(count)
                    this.counter = 0
                  }
                }, 1000);
                this.$emit('msg','验证码发送成功')
              }
              if (res.data.code === -1) {
                if(res.data.message==='用户已注册'){
                  this.$emit('msg', '用户已注册')
                }
              }
            }).catch(()=>{
              this.$emit('msg', '网络错误')
            })

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
    validate() {
      for (let v of this.rules.nameRules) {
        if (v(this.name) !== true) {
          this.$emit('msg', v(this.name))
          return false
        }
      }
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
        if (v(this.password) !== true) {
          this.$emit('msg', v(this.password))
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
