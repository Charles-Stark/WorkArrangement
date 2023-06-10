//vuex的store创建
import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

//actions用于响应组件中的动作
const actions = {

}

//mutations用于操作数据(state)
const mutations = {
  dark(state, value) {
    state.dark = value
    localStorage.dark = value ? 'true' : ''
  },
  auto_dark(state, value) {
    state.autoDark = value
    localStorage.autoDark = value ? 'true' : ''
  },
  theme(state, value) {
    state.currentTheme = value
    localStorage.theme = value
  },
  setLoginInfo(state, value) {
    localStorage.setItem("token", value.token)
    localStorage.setItem("userId", value.userId)
    state.token = value.token
    state.userId = value.userId
  },
  deleteLoginInfo(state){
    localStorage.clear();
    state.token = null
    state.userId = null
  },
  saveAuthority(state,value){
    state.isManager=value.isManager,
    state.isShopManager=value.isShopManager
  }

}



//state用于存储数据
const state = {
  dark: localStorage.dark === 'true',
  autoDark: localStorage.autoDark === 'true',
  currentTheme: localStorage.theme || 'blue',
  token: localStorage.getItem('token'),
  userId: localStorage.getItem('userId'),
  isManager:false,
  isShopManager:false,
}


//创建store
const store = new Vuex.Store({
  actions: actions,
  mutations: mutations,
  state: state,
})





export default store

