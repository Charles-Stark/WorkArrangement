<template>
  <v-container fluid class="mt-9">
    <v-expansion-panels multiple v-model="panel">

      <v-expansion-panel>
        <v-expansion-panel-header>
          <v-row align="center" class="spacer" no-gutters>
            <v-col cols="4" sm="3" md="2">


              <v-avatar size="80">
                <v-img :src="user.avatar"></v-img>
              </v-avatar>
            </v-col>

            <v-col sm="5" md="3">
              <strong>{{ user.userName }}</strong>
            </v-col>

            <v-col class="text-no-wrap hidden-xs-only" cols="5" sm="2">
              <strong>账户设置</strong>
            </v-col>

            <v-col class="grey--text text-truncate hidden-sm-and-down">
              修改头像，用户名，邮箱，密码
            </v-col>
          </v-row>
        </v-expansion-panel-header>

        <v-expansion-panel-content>
          <v-divider></v-divider>
          <v-list flat rounded>
            <v-list-item-group>

              <v-list-item @click="changeAvatar()">
                <v-list-item-title>修改头像</v-list-item-title>
                <input hidden type="file" id="avatarUploader" accept="image/png, image/jpg" @change="updateAvatar()">

              </v-list-item>

              <v-divider></v-divider>

              <!-- 修改用户名 -->
              <v-dialog v-model="dialog1" width="400" persistent height="500">
                <template v-slot:activator="{ on, attrs }">

                  <v-list-item v-bind="attrs" v-on="on" class="mt-2">
                    <v-list-item-icon>
                      <v-icon color="accent">mdi-account</v-icon>
                    </v-list-item-icon>
                    <v-list-item-title>用户名</v-list-item-title>
                    <v-list-item-subtitle>{{ user.userName }}</v-list-item-subtitle>
                  </v-list-item>

                </template>

                <v-card>
                  <v-card-title class="text-h5">
                    修改用户名
                  </v-card-title>
                  <v-card-text class="mt-5"><v-text-field v-model="newName" counter="10" label="输入新用户名" required outlined
                      @keyup.enter="updateName()"></v-text-field></v-card-text>
                  <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn class="mb-5" @click="updateName()" color="primary" width="120" height="40">
                      修改
                    </v-btn>
                    <v-btn class="mb-5" text @click="dialog1 = false" width="120" height="40">
                      取消
                    </v-btn>
                  </v-card-actions>
                </v-card>
              </v-dialog>

              <v-divider></v-divider>

              <!-- 修改邮箱 -->
              <v-dialog v-model="dialog2" width="500" persistent>
                <template v-slot:activator="{ on, attrs }">

                  <v-list-item v-bind="attrs" v-on="on" class="mt-2">
                    <v-list-item-icon>
                      <v-icon color="accent">mdi-gmail</v-icon>
                    </v-list-item-icon>
                    <v-list-item-title>邮箱</v-list-item-title>
                    <v-list-item-subtitle>{{ user.email }}</v-list-item-subtitle>
                  </v-list-item>

                </template>

                <v-card>

                  <v-stepper v-model="step1"
                    :style="this.$vuetify.theme.dark === true ? 'backgroundColor:#1E1E1E' : 'backgroundColor:#FFFFFF'">
                    <v-stepper-header>
                      <span class="text-h4 mt-4 ml-5">修改邮箱</span>
                    </v-stepper-header>

                    <v-stepper-items>
                      <v-stepper-content step="1">

                        <v-row class="mt-5">
                          <v-col cols="12">
                            <v-text-field label="邮箱" required outlined v-model="user.email" disabled></v-text-field>
                          </v-col>
                          <v-col cols="8">
                            <v-text-field label="验证码" required outlined v-model="otp1"
                              @keyup.enter="nextStep1()"></v-text-field>
                          </v-col>
                          <v-col cols="4">
                            <v-btn color="primary" @click="getOTP()" class="mx-auto" block height="55" outlined
                              v-text="counter === 0 ? '获取验证码' : `${counter}秒后重试`" :disabled="counter !== 0"></v-btn>
                          </v-col>

                        </v-row>

                        <v-row>
                          <v-spacer></v-spacer>
                          <v-col cols="3">
                            <v-btn color="primary" @click="nextStep1()" class="mr-3" block height="40">
                              下一步
                            </v-btn>
                          </v-col>
                          <v-col cols="3">
                            <v-btn text @click="dialog2 = false" block height="40">
                              取消
                            </v-btn>
                          </v-col>
                        </v-row>
                      </v-stepper-content>

                      <v-stepper-content step="2">

                        <v-row class="mt-5">
                          <v-col cols="12">
                            <v-text-field label="新邮箱" required outlined v-model="newEmail"></v-text-field>
                          </v-col>
                          <v-col cols="8">
                            <v-text-field label="验证码" required outlined v-model="otp2"></v-text-field>
                          </v-col>
                          <v-col cols="4">
                            <v-btn color="primary" @click="getEmailOTP()" class="mx-auto" block height="55" outlined
                              v-text="counter2 === 0 ? '获取验证码' : `${counter2}秒后重试`" :disabled="counter2 !== 0"></v-btn>
                          </v-col>
                        </v-row>

                        <v-row>
                          <v-spacer></v-spacer>
                          <v-col cols="3">
                            <v-btn color="primary" @click="emailReset()" class="mr-3" block height="40">
                              完成
                            </v-btn>
                          </v-col>
                          <v-col cols="3">
                            <v-btn text @click="dialog2 = false; step1 = 1" block height="40">
                              取消
                            </v-btn>
                          </v-col>
                        </v-row>

                      </v-stepper-content>

                    </v-stepper-items>
                  </v-stepper>

                </v-card>

              </v-dialog>

              <v-divider></v-divider>

              <!-- 修改密码 -->
              <v-dialog v-model="dialog3" width="500" persistent>
                <template v-slot:activator="{ on, attrs }">

                  <v-list-item v-bind="attrs" v-on="on" class="mt-2">
                    <v-list-item-icon>
                      <v-icon color="accent">mdi-key</v-icon>
                    </v-list-item-icon>
                    <v-list-item-title>密码</v-list-item-title>
                    <v-list-item-subtitle>点击修改</v-list-item-subtitle>
                  </v-list-item>

                </template>

                <v-card>

                  <v-stepper v-model="step2"
                    :style="this.$vuetify.theme.dark === true ? 'backgroundColor:#1E1E1E' : 'backgroundColor:#FFFFFF'">

                    <v-stepper-header>
                      <span class="text-h4 mt-4 ml-5">修改密码</span>
                    </v-stepper-header>

                    <v-stepper-items>
                      <v-stepper-content step="1">

                        <v-row class="mt-5">
                          <v-col cols="12">
                            <v-text-field label="邮箱" required outlined v-model="user.email" disabled></v-text-field>
                          </v-col>
                          <v-col cols="8">
                            <v-text-field label="验证码" required outlined v-model="otp3"
                              @keyup.enter="nextStep2()"></v-text-field>
                          </v-col>
                          <v-col cols="4">
                            <v-btn color="primary" @click="getOTP()" class="mx-auto" block height="55" outlined
                              v-text="counter === 0 ? '获取验证码' : `${counter}秒后重试`" :disabled="counter !== 0"></v-btn>
                          </v-col>

                        </v-row>

                        <v-row>
                          <v-spacer></v-spacer>
                          <v-col cols="3">
                            <v-btn color="primary" @click="nextStep2()" class="mr-3" block height="40">
                              下一步
                            </v-btn>
                          </v-col>
                          <v-col cols="3">
                            <v-btn text @click="dialog3 = false" block height="40">
                              取消
                            </v-btn>
                          </v-col>
                        </v-row>

                      </v-stepper-content>

                      <v-stepper-content step="2">

                        <v-row class="mt-5">
                          <v-col cols="12">
                            <v-text-field label="密码" required outlined v-model="resetPsw"></v-text-field>
                          </v-col>
                          <v-col cols="12">
                            <v-text-field label="重复密码" required outlined v-model="repsw"></v-text-field>
                          </v-col>
                        </v-row>

                        <v-row>
                          <v-col cols="3">
                            <v-btn text @click="step2 = 1" class="mr-3" block height="40">
                              返回上一步
                            </v-btn>
                          </v-col>
                          <v-spacer></v-spacer>
                          <v-col cols="3">
                            <v-btn color="primary" @click="pswReset()" class="mr-3" block height="40">
                              完成
                            </v-btn>
                          </v-col>
                          <v-col cols="3">
                            <v-btn text @click="dialog3 = false" block height="40">
                              取消
                            </v-btn>
                          </v-col>
                        </v-row>

                      </v-stepper-content>

                    </v-stepper-items>
                  </v-stepper>

                </v-card>
              </v-dialog>

              <v-divider v-if="!$store.state.isManager"></v-divider>

              <v-dialog v-model="dialog4" width="500" persistent v-if="!$store.state.isManager">
                <template v-slot:activator="{ on, attrs }">

                  <v-list-item v-bind="attrs" v-on="on" class="mt-2">
                    <v-list-item-icon>
                      <v-icon color="accent">mdi-star-circle</v-icon>
                    </v-list-item-icon>
                    <v-list-item-title>排班喜好</v-list-item-title>
                    <v-list-item-subtitle>点击修改</v-list-item-subtitle>
                  </v-list-item>

                </template>

                <editFavor @close="dialog4=false" ></editFavor>

              </v-dialog>

            </v-list-item-group>
          </v-list>
        </v-expansion-panel-content>

      </v-expansion-panel>

      <v-expansion-panel>
        <v-expansion-panel-header>
          <v-row align="center" class="spacer" no-gutters>
            <v-col cols="4" sm="2" md="1">
              <v-icon color="green" size="35">
                mdi-draw
              </v-icon>
            </v-col>

            <v-col class="text-no-wrap" cols="5" sm="3">
              <strong>显示设置</strong>
            </v-col>

            <v-col class="grey--text text-truncate hidden-xs-only">
              深色模式，跟随系统颜色设置，主题
            </v-col>
          </v-row>
        </v-expansion-panel-header>

        <v-expansion-panel-content>
          <v-divider></v-divider>
          <v-list flat rounded>
            <v-list-item-group>
              <v-list-item @click="darkMode()" :disabled="autoDark">
                <template>
                  <v-list-item-icon>
                    <v-icon color="blue" size="30">mdi-theme-light-dark</v-icon>
                  </v-list-item-icon>
                  <v-list-item-title>深色模式</v-list-item-title>
                  <v-switch v-model="dark" :disabled="autoDark" @click="darkMode()"></v-switch>
                </template>
              </v-list-item>
            </v-list-item-group>
          </v-list>

          <v-divider></v-divider>
          <v-list flat rounded>
            <v-list-item-group>
              <v-list-item @click="autoDarkMode()">
                <template>
                  <v-list-item-icon>
                    <v-icon color="green" size="30">mdi-desktop-classic</v-icon>
                  </v-list-item-icon>
                  <v-list-item-title>跟随系统颜色模式</v-list-item-title>
                  <v-switch v-model="autoDark" @click="autoDarkMode()"></v-switch>
                </template>

              </v-list-item>
            </v-list-item-group>
          </v-list>

          <v-divider></v-divider>
          <v-list flat rounded>
            <v-list-item-group>
              <v-list-item>
                <v-list-item-icon>
                  <v-icon color="secondary" size="30">mdi-palette</v-icon>
                </v-list-item-icon>
                <v-list-item-title>主题颜色</v-list-item-title>
                <v-btn-toggle v-model="toggle" mandatory>
                  <v-btn class="mx-2" fab :color="color.type" v-for="color in colors" :key="color.type"
                    @click="changeTheme(color)"></v-btn>
                </v-btn-toggle>
              </v-list-item>
            </v-list-item-group>
          </v-list>

        </v-expansion-panel-content>
      </v-expansion-panel>

      <v-expansion-panel>
        <v-btn block height="50" color="warning" text @click="logout()">
          退出登录
        </v-btn>
      </v-expansion-panel>

    </v-expansion-panels>
  </v-container>
</template>

<script>
import themes from '../plugins/themes'
import {
  getEmailOTP,
  getOTP,
  getUserAvatar,
  getUserInfo,
  logout,
  OTPLogin,
  pswReset,
  updateAvatar,
  updateEmail,
  updateName
} from '@/request/user'
import editFavor from '../components/editFavor.vue'

export default {
  components:{
    editFavor
  },
  data: () => ({
    step1: 1,
    step2: 1,
    dialog1: false,
    dialog2: false,
    dialog3: false,
    dialog4: false,
    dark: null,
    autoDark: null,
    toggle: [],
    panel: [0, 1],
    counter: 0,
    counter2: 0,
    loading: false,
    menu: false,

    user: {
      avatar: '',
      userName: '',
      email: '',
    },


    otp1: '',
    otp2: '',
    otp3: '',
    newEmail: '',
    resetPsw: '',
    repsw: '',
    newName: '',
    newAvatar: ''

  }),
  computed: {

    colors() {
      return [{ type: 'blue', theme: themes.blue },
      { type: 'green', theme: themes.green },
      { type: 'purple', theme: themes.purple },
      { type: 'orange', theme: themes.orange },
      { type: 'pink', theme: themes.pink }]
    }

  },

  methods: {
    updateAvatar() {
      let file = document.getElementById('avatarUploader').files[0];
      const formData = new FormData();
      formData.append('photo', file)
      updateAvatar(formData).then(res => {
        if (res.data.code === 0) {
          this.$emit('msg', '更新头像成功')
          this.$router.go(0)
        }
        else if (res.data.code === -1) {
          if (res.data.message === '图片过大') {
            this.$emit('msg', '上传图片过大')
          }
          else if (res.data.message === '类型不符') {
            this.$emit('msg', '请上传.jpg/.png格式的头像')
          }
        }

      }).catch(() => {
        this.$emit('msg', '网络错误')
      })
    },
    changeAvatar() {
      document.getElementById("avatarUploader").click()
    },
    getOTP() {
      this.loading = true
      if (this.counter === 0) {
        getOTP(this.user.email).then(res => {
          if (res.data.code === 0) {
            this.$emit('msg', '验证码已发送')
            if (this.counter === 0) {
              this.counter = 60;
              const count = setInterval(() => {
                this.counter--
                if (this.counter <= 0) {
                  clearInterval(count)
                  this.counter = 0
                }
              }, 1000);
            }

          }
        }).catch(() => {
          this.$emit('msg', '网络错误')
        })
      }
      this.loading = false

    },
    pswReset() {
      if (this.resetPsw.length >= 8) {
        if (this.resetPsw === this.repsw) {
          this.loading = true
          pswReset(this.user.email, this.resetPsw, this.otp3).then(res => {
            if (res.data.code === 0) {
              this.$emit('msg', '密码修改成功')
              this.$router.go(0)
            }
            else if (res.data.code === -1) {
              if (res.data.message === '验证失败') {
                this.$emit('msg', '验证码错误')
              }
            }
          }).catch(() => {
            this.$emit('msg', '网络错误')
          })
          this.loading = false
        }
        else {
          this.$emit('msg', '两次输入密码不匹配')
        }
      }
      else {
        this.$emit('msg', '密码长度不能小于8位')
      }
    },
    getEmailOTP() {
      this.loading = true
      if (this.newEmail !== '') {
        if (/.+@.+/.test(this.newEmail)) {
          if (this.counter2 === 0) {
            getEmailOTP(this.newEmail).then(res => {
              if (res.data.code === 0) {
                this.$emit('msg', '验证码已发送')
                if (this.counter2 === 0) {
                  this.counter2 = 60;
                  const count = setInterval(() => {
                    this.counter2--
                    if (this.counter2 <= 0) {
                      clearInterval(count)
                      this.counter2 = 0
                    }
                  }, 1000);
                }
              }
            }).catch(() => {
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

      this.loading = false
    },
    emailReset() {
      if (this.otp2 !== '') {
        updateEmail(this.newEmail, this.otp2).then(res => {
          console.log(res)
          if (res.data.code === 0) {
            this.$emit('msg', '邮箱修改成功')
            this.$store.commit('setLoginInfo', { token: res.data.data.token, userId: res.data.data.id })
            this.$router.go(0)
          }
          else if (res.data.code === -1) {
            if (res.data.message === '邮箱已占用') {
              this.$emit('msg', '邮箱已占用')
            }
            else if (res.data.message === '验证失败') {
              this.$emit('msg', '验证码错误')
            }
          }

        }).catch(() => {
          this.$emit('msg', '网络错误')
        })
      }
      else {
        this.$emit('msg', '请输入验证码')
      }
    },
    updateName() {
      if (this.newName !== '') {
        if (this.newName.length <= 10) {
          updateName(this.newName).then(() => {
            this.$emit('msg', '修改成功')
            this.$router.go(0)
          }).catch(() => {
            this.$emit('msg', '网络错误')
          })
        }
        else {
          this.$emit('msg', '姓名长度不能大于10')
        }
      }
      else {
        this.$emit('msg', '请输入姓名')
      }
    },
    changeTheme(color) {
      this.$vuetify.theme.themes = color.theme
      this.$store.commit('theme', color.type)
      this.$router.go(0)
    },
    darkMode() {
      this.dark = !this.dark
      this.$store.commit('dark', this.dark)
      this.$vuetify.theme.dark = this.dark
    },
    autoDarkMode() {
      this.autoDark = !this.autoDark
      this.$store.commit('auto_dark', this.autoDark)
    },
    nextStep1() {
      if (this.otp1 !== '') {
        OTPLogin(this.user.email, this.otp1).then(res => {
          if (res.data.code === 0) {
            this.step1 = 2
          }
          else if (res.data.code === -1) {
            this.$emit('msg', '验证码错误')
          }
        }).catch(() => {
          this.$emit('msg', '网络错误')
        })
      }
      else {
        this.$emit('msg', '请输入验证码')
      }

    },
    nextStep2() {
      if (this.otp3 !== '') {
        this.step2 = 2
      }
      else {
        this.$emit('msg', '请输入验证码')
      }
    },
    logout() {
      logout().then(res => {
        if (res.data.code === 0) {
          this.$store.commit('deleteLoginInfo')
          this.$emit('msg', '退出登录成功，正在重定向...')
          this.$router.push('/')
          this.$router.go(0)
        }
        else {
          this.$emit('msg', '退出登录失败')
        }
      }).catch(
        this.$emit('msg', '网络错误')
      )
    }


  },

  watch: {
    autoDark() {
      if (this.$store.state.autoDark === true) {
        this.dark = matchMedia("(prefers-color-scheme: dark)").matches ? 'true' : ''
        this.$vuetify.theme.dark = matchMedia("(prefers-color-scheme: dark)").matches
        this.$store.commit('dark', matchMedia("(prefers-color-scheme: dark)").matches ? 'true' : '')
      }
    },
  },

  async mounted() {
    try {
      const userinfo = (await getUserInfo()).data.data;
      this.user.userName = userinfo.username
      this.user.email = userinfo.email

      const avatar = (await getUserAvatar(this.$store.state.userId));
      if (avatar.status === 200) {
        this.user.avatar = URL.createObjectURL(avatar.data)
      }
      else if (avatar.status === 204) {
        this.user.avatar = require('../assets/defaultAvatar.png')
      }

      
    } catch (err) {
      this.$emit('msg', '网络错误')
    }
  },

  created() {
    this.dark = this.$store.state.dark
    this.autoDark = this.$store.state.autoDark
  }


}
</script>