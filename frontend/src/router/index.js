//路由配置
import Vue from 'vue'
import VueRouter from 'vue-router'

import { getUserInfo } from '../request/api'

Vue.use(VueRouter)

//管理员身份的路由
var adminRoutes = [
  {
    path: '/',
    component: () => import('../components/testAvatar'),
    meta: {
      title: '慧博云通智能排班',

      //控制是否需要登陆验证
      requireAuth: false
    }
  },

  {
    path: '/controlpanel',
    redirect: '/controlpanel/dashBoard',
    component: () => import('../components/admins/appBars'),
    children: [
      {
        path: 'dashboard',
        component: () => import('../components/admins/view/dashBoard'),
        meta: {
          title: '概览',
          //控制左侧导航栏选中
          selectedItem: 0,
          requireAuth: true
        }
      }, {
        path: 'arrange',
        component: () => import('../components/admins/view/workArrange'),
        meta: {
          title: '智能排班',
          selectedItem: 1,
          requireAuth: true
        }
      }, {
        path: 'absences',
        component: () => import('../components/admins/view/absenceArrange'),
        meta: {
          title: '请假管理',
          selectedItem: 2,
          requireAuth: true
        }
      }, {
        path: 'branches',
        component: () => import('../components/admins/view/branchInfo'),
        meta: {
          title: '分店信息',
          selectedItem: 5,
          requireAuth: true
        }
      }, {
        path: 'staff',
        component: () => import('../components/admins/view/staffInfo'),
        meta: {
          title: '员工信息',
          selectedItem: 6,
          requireAuth: true
        }
      }, {
        path: 'settings',
        component: () => import('../components/admins/view/settingPage'),
        meta: {
          title: '用户设置',
          selectedItem: 4,
          requireAuth: true
        }
      },
    ]
  }
]

//经理身份的路由
var managerRoutes = []

//普通员工身份的路由
var employeeRoutes = []


//导出 router
const router = new VueRouter({
  mode: 'hash',
  base: '',
  routes: []
})

//全局前置路由守卫
router.beforeEach((to, from, next) => {
  if (to.meta.title) {
    document.title = to.meta.title || '慧博云通'
  }


  getUserInfo(localStorage.getItem('userId')).then(res => {
    if (res.data.data.isManager) {
      for (let route of adminRoutes) {
        router.addRoute(route)
      }
    }
    else if (res.data.data.isShopManager) {
      for (let route of managerRoutes) {
        router.addRoute(route)
      }
    }
    else {
      for (var route of employeeRoutes) {
        router.addRoute(route)
      }
    }
  }).catch((err) => {
    console.log(err)
  })

  if (to.meta.requireAuth) {
    if (!localStorage.token) {
      router.push('/')
    } else {
      next()
    }
  } else {
    next()
  }
})

//全局后置路由守卫
router.afterEach((to, from) => {
  console.log(to, from)
})

export default router

