import Vue from 'vue'
import Vuetify from 'vuetify'
import 'vuetify/dist/vuetify.min.css'
import '@mdi/font/css/materialdesignicons.css'

Vue.use(Vuetify)


export default new Vuetify({
  icons: {
    iconfont: 'mdi'
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
        primary: '#3399CC',
        secondary: '#99C0FF',
        accent: '#009999',
        error: '#FF5252',
        success: '#4CAF50',
        warning: '#FB8C00',
        strong: '#212121'
      },
      dark: {
        primary: '#66CCFF',
        secondary: '#99CCFF',
        accent: '#009999',
        error: '#FF5252',
        success: '#4CAF50',
        warning: '#FB8C00',
        strong: '#F5F5F5'
      },
    }
  }
})