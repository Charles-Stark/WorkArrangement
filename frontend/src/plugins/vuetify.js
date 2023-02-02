import Vue from 'vue'
import Vuetify from 'vuetify'
import 'vuetify/dist/vuetify.min.css'
import '@mdi/font/css/materialdesignicons.css'

import themes from './themes'
import store from '../store'


Vue.use(Vuetify)


export default new Vuetify({
  icons: {
    iconfont: 'mdi'
  },
  theme: {
    dark: store.state.autoDark ? matchMedia("(prefers-color-scheme: dark)").matches : store.state.dark,
    default: 'light',
    disable: false,
    options: {
      cspNonce: undefined,
      customProperties: undefined,
      minifyTheme: undefined,
      themeCache: undefined,
    },
    themes: themes[store.state.currentTheme]
  }
})