<template>
  <div id="app">
    <mainLayout/>
  </div>
</template>

<script>
import mainLayout from './components/admins/mainLayout.vue'

export default {
  name: 'App',
  components: {
    mainLayout
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
