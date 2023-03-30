import Vue from 'vue'
import App from './App.vue'
import vuetify from '@/plugins/vuetify'
import store from './store'
import router from './router'

import JsonExcel from 'vue-json-excel'
Vue.component("downloadExcel",JsonExcel)

// import VueFullPage from 'vue-fullpage.js';
// Vue.use(VueFullPage);

Vue.config.productionTip = false

new Vue({
  render: h => h(App),
  vuetify,
  store,
  router,
}).$mount('#app')

