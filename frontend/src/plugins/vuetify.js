import Vue from 'vue'
import Vuetify from 'vuetify'
import 'vuetify/dist/vuetify.min.css'
import '@mdi/font/css/materialdesignicons.css'

Vue.use(Vuetify)


export default new Vuetify({
  icons:{
    iconfont:'mdi'
  },
  theme: {
    dark: false,
    default: 'light',
    disable: false,
    options: {
      cspNonce: undefined,
      customProperties: undefined,
      minifyTheme: undefined,
      themeCache: undefined,
    },
    themes: {
      light: {
        primary: '#1976D2',
        secondary: '#4FC3F7',
        accent: '#3949AB',
        error: '#FF5252',
        success: '#4CAF50',
        warning: '#FB8C00',
      },
      dark: {
        primary: '#2196F3',
        secondary: '#B3E5FC',
        accent: '#00B0FF',
        error: '#FF5252',
        success: '#4CAF50',
        warning: '#FB8C00',
      },
    }
  }
})