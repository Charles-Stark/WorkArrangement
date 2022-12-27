//vuex的store创建
import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

//actions用于响应组件中的动作
const actions = {

}

//mutations用于操作数据(state)
const mutations = {

}

//state用于存储数据
const state = {

}


//创建store
const store =new Vuex.Store({
  actions:actions,
  mutations:mutations,
  state:state,
})

export default store