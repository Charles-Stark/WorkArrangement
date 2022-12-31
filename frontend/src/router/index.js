//路由配置
import Vue from 'vue'
import VueRouter from 'vue-router'
Vue.use(VueRouter)

//引入组件
import absenceArrange from '../components/admins/view/absenceArrange'
import settingPage from '../components/admins/view/settingPage'

var RouterVer = function () {
  //判断身份为管理员或用户 加载不同路由
  var jurisdiction = 'admin'
  if (jurisdiction==='admin') {
    return [
      {
        path: '/absences',
        component: absenceArrange,
        meta: {
          title: '用户设置',

          //控制左侧导航栏选中
          selectedItem: 2
        }
      },
      {
        path: '/settings',
        component: settingPage,
        meta: {
          title: '用户设置',

          //控制左侧导航栏选中
          selectedItem: 4
        }
      },
    ]
  } else {
    return [
      
    ]
  }
}

//导出 router
const router = new VueRouter({
  mode: 'hash',
  base: '',
  routes: RouterVer()
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

