<template>
  <div class="Login">
    <v-row justify="center">

      <v-menu bottom min-width="200" rounded class="ma-6" offset-y v-if="logined" dark>
        <template v-slot:activator="{ on }">
          <v-btn icon v-on="on" class="ma-8">
            <v-avatar size="44">
              <v-img :src="user.avatar"></v-img>
            </v-avatar>
          </v-btn>
        </template>
        <v-card>
          <v-list-item-content class="justify-center">
            <div class="mx-auto text-center">
              <v-avatar size="80">
                <v-img :src="user.avatar"></v-img>
              </v-avatar>
              <h3 class="mt-2">{{ user.userName }}</h3>
              <p class="text-caption mt-1">
                {{ user.email }}
              </p>
              <v-divider class="my-3"></v-divider>
              <v-btn depressed rounded text to="/controlpanel/settings">
                账户设置
              </v-btn>
              <v-divider class="my-3"></v-divider>
              <v-btn depressed rounded text color="warning" @click="logout()">
                退出登录
              </v-btn>
            </div>
          </v-list-item-content>
        </v-card>
      </v-menu>

      <v-dialog v-model="dialog" persistent max-width="450" :fullscreen="$vuetify.breakpoint.xsOnly" v-else>
        <template v-slot:activator="{ on, attrs }">
          <v-btn color="white" dark v-bind="attrs" v-on="on" text>
            登录
          </v-btn>
        </template>

        <v-card dark>
          <v-tabs background-color="purple darken-3" center-active dark>
            <v-tab @click="show = 1">登录</v-tab>
            <v-tab @click="show = 2">注册</v-tab>
            <v-spacer></v-spacer>
            <v-btn icon large @click="dialog = false" class="my-auto mr-1"><v-icon>mdi-close</v-icon></v-btn>
          </v-tabs>
          <v-card-text class="mt-3">
            <v-container>
              <keep-alive>
                <loginBox v-if="show === 1" @msg="getMsg"></loginBox>
                <registerBox v-if="show === 2" @msg="getMsg"></registerBox>
              </keep-alive>
            </v-container>
          </v-card-text>
        </v-card>

        <v-snackbar v-model="snackBar" style="z-index: 10010;">
          {{ snackBarText }}
          <template v-slot:action="{ attrs }">
            <v-btn color="error" icon v-bind="attrs" @click="snackBar = false">
              <v-icon>mdi-close</v-icon>
            </v-btn>
          </template>
        </v-snackbar>
        
      </v-dialog>



    </v-row>
  </div>
</template>
  
<script>
import loginBox from './loginBox.vue';
import registerBox from './registerBox.vue';
import { getUserInfo, getUserAvatar, logout } from '@/request/user'
export default {
  components: { loginBox, registerBox },
  data: () => ({
    dialog: false,
    show: 1,
    snackBar: false,
    snackBarText: '',
    user: {},
    logined: false,
  }),
  computed: {

  },
  methods: {
    getMsg(data) {
      this.snackBarText = data
      this.snackBar = true
    },
    logout() {
      logout().then(res => {
        if (res.data.code === 0) {
          this.$store.commit('deleteLoginInfo')
          this.getMsg('退出登录成功，正在重定向...')
          this.$router.push('/')
          this.$router.go(0)
        }
        else {
          this.getMsg('退出登录失败')
        }
      }).catch(
        this.getMsg('网络错误')
      )
    }
  },

  mounted() {
    if (this.$store.state.userId !== null && this.$store.state.token !== null) {
      getUserInfo().then(async res => {
        const user = {};
        if (res.data.code === 0) {
          user.userName = res.data.data.username
          user.email = res.data.data.email

          const avatar = await getUserAvatar(this.$store.state.userId);
          if (avatar.status === 200) {
            user.avatar = URL.createObjectURL(avatar.data)
          }
          else {
            user.avatar = require('../../assets/defaultAvatar.png')
          }
          this.logined = true

        }

        this.user = user

      }).catch(() => {
        this.getMsg('网络错误')
      })



    }

  }


}
</script>
  
<style scoped></style>
  