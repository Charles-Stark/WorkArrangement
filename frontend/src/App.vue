<template>
  <v-app id="inspire">
    <!-- <testAvatar></testAvatar> -->
    <router-view></router-view>
  </v-app>
</template>

<script>
import themes from './store/themes'
import testAvatar from './components/testAvatar.vue';

export default {
  name: 'App',
  components: { testAvatar },
  created() {
    for (var theme in themes) {
      if (this.$store.state.currentTheme === theme) {
        switch (theme) {
          case 'blue':
            this.$vuetify.theme.themes = themes.blue;
            break;
          case 'green':
            this.$vuetify.theme.themes = themes.green;
            break;
          case 'purple':
            this.$vuetify.theme.themes = themes.purple;
            break;
          case 'orange':
            this.$vuetify.theme.themes = themes.orange;
            break;
          case 'pink':
            this.$vuetify.theme.themes = themes.pink;
            break;
        }
        break;
      }
    }
    if (this.$store.state.autoDark === true) {
      this.$vuetify.theme.dark = window.matchMedia("(prefers-color-scheme: dark)").matches
      window.localStorage.dark = window.matchMedia("(prefers-color-scheme: dark)").matches ? 'true' : ''
    }
    else {
      this.$vuetify.theme.dark = this.$store.state.dark
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
