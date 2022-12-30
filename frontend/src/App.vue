<template>
  <v-app id="inspire">

    <mainLayout />
  </v-app>
</template>

<script>
import mainLayout from './components/admins/mainLayout.vue'

export default {
  name: 'App',
  components: {
    mainLayout
  },
  created(){
    if (this.$store.state.autoDark === true) {
      this.$vuetify.theme.dark = window.matchMedia("(prefers-color-scheme: dark)").matches
      window.localStorage.dark = window.matchMedia("(prefers-color-scheme: dark)").matches ? 'true' : ''
    }
    else{
      this.$vuetify.theme.dark=this.$store.state.dark
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
