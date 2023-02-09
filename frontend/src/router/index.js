//路由配置
import Vue from 'vue'
import VueRouter from 'vue-router'


Vue.use(VueRouter)

//无权限限制的路由
var commonRoutes = [
  {
    path: '/',
    component: () => import('../components/testAvatar'),
    meta: {
      title: '慧博云通智能排班',
    }
  },
  {
    path: '/404',
    component: () => import('../components/404Page'),
    meta: {
      title: '404',
    }
  },
]

//总管理员身份的路由
export var adminRoutes = {
  path: '/admin',
  redirect: '/admin/dashBoard',
  component: () => import('../components/admins/appBars'),
  children: [
    {
      path: 'dashboard',
      component: () => import('../components/admins/dashBoard'),
      meta: {
        title: '概览',
        //控制左侧导航栏选中
        selectedItem: 0,
      }
    }, {
      path: 'arrange',
      component: () => import('../components/admins/workArrange'),
      meta: {
        title: '智能排班',
        selectedItem: 1,
      }
    }, {
      path: 'absences',
      component: () => import('../components/admins/absenceArrange'),
      meta: {
        title: '请假管理',
        selectedItem: 2,
      }
    }, {
      path: 'branches',
      component: () => import('../components/admins/branchInfo'),
      meta: {
        title: '分店信息',
        selectedItem: 5,
      }
    }, {
      path: 'staff',
      component: () => import('../components/admins/staffInfo'),
      meta: {
        title: '员工信息',
        selectedItem: 6,
      }
    }, {
      path: 'settings',
      component: () => import('../components/admins/settingPage'),
      meta: {
        title: '用户设置',
        selectedItem: 4,
      }
    },
  ]
}

//门店管理员身份的路由
export var managerRoutes = {}

//普通员工身份的路由
export var employeeRoutes = {}


//导出 router
const router = new VueRouter({
  mode: 'hash',
  base: '',
  routes: commonRoutes
})

//全局前置路由守卫
router.beforeEach((to, from, next) => {
  if (to.meta.title) {
    document.title = to.meta.title || '慧博云通'
  }
  next()
})

//全局后置路由守卫
router.afterEach((to, from) => {
  if (!localStorage.token && to.matched.length === 0) {
    router.push('/404')
  }
})

export default router

