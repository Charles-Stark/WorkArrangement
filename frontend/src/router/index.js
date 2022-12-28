//路由配置
import Vue from 'vue'
import VueRouter from 'vue-router'
Vue.use(VueRouter)

//引入组件
import settingPage from '../components/admins/view/settingPage'

//导出 router
const router = new VueRouter({
  routes: [
    {
      path: '/settings',
      component: settingPage,
      meta: {
        title: '设置'
      }
    },
  ]
})

//全局前置路由守卫
router.beforeEach((to, from, next) => {
  console.log(to, from, next)
  if (to.meta.title) {
    document.title = to.meta.title
  }
  next()
})

//全局后置路由守卫
router.afterEach((to, from) => {
  console.log(to, from)
  document.title = to.meta.title || '汇博云通'
})

export default router

