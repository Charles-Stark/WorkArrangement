//vuex的store创建
import Vue from 'vue'
import Vuex from 'vuex'
import theme from  './theme'

Vue.use(Vuex)

//actions用于响应组件中的动作
const actions = {

}

//mutations用于操作数据(state)
const mutations = {
  dark(state, value) {
    state.dark = value
    window.localStorage.dark = value ? 'true' : ''
  },
  auto_dark(state, value) {
    state.autoDark = value
    window.localStorage.autoDark = value ? 'true' : ''
  }

}



//state用于存储数据
const state = {
  dark: window.localStorage.dark==='true',
  autoDark: window.localStorage.autoDark==='true',
  theme
}


//创建store
const store = new Vuex.Store({
  actions: actions,
  mutations: mutations,
  state: state,
})





export default store

