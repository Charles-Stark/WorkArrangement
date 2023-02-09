<template>
  <v-app id="inspire">
    <router-view></router-view>
  </v-app>
</template>

<script>
import { adminRoutes, managerRoutes, employeeRoutes } from './router'
import { getUserInfo } from './request/user'


export default {
  name: 'App',
  created() {

    //根据身份生成路由
    if (localStorage.getItem('token')) {
      var blank = {
        path: '*',
        redirect: '/404'
      }
      getUserInfo().then(res => {
        if (res.data.data.isManager) {
          this.$router.addRoute(adminRoutes)
          this.$router.addRoute(blank)
        }
        else if (res.data.data.isShopManager) {
          this.$router.addRoute(managerRoutes)
          this.$router.addRoute(blank)
        }
        else {
          this.$router.addRoute(employeeRoutes)
          this.$router.addRoute(blank)
        }
      }).catch(() => {
        console.log('网络错误')
      })
    }

    if (this.$vuetify.theme.dark === true) {
      document.body.style.backgroundColor = '#121212'
    }
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
