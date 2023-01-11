//路由配置
import Vue from 'vue'
import VueRouter from 'vue-router'
Vue.use(VueRouter)

var RouterVer = function () {
  //判断身份为管理员或用户 加载不同路由
  var jurisdiction = 'admin'

  if (jurisdiction === 'admin') {
    //管理员身份的路由
    return [
      {
        path:'/',
        component:()=>import('../components/admins/appBars'),
        children:[
          {
            path: 'absences',
            component: () => import('../components/admins/view/absenceArrange'),
            meta: {
              title: '请假管理',
              //控制左侧导航栏选中
              selectedItem: 2
            }
          },
          {
            path: 'branches',
            component: () => import('../components/admins/view/branchInfo'),
            meta: {
              title: '分店信息',
              selectedItem: 5
            }
          },
          {
            path: 'staff',
            component: () => import('../components/admins/view/staffInfo'),
            meta: {
              title: '员工信息',
              selectedItem: 6
            }
          },
          {
            path: '/settings',
            component: () => import('../components/admins/view/settingPage'),
            meta: {
              title: '用户设置',
              selectedItem: 4
            }
          },
        ]
      }
    ]
  } else {
    //普通用户身份的路由
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

