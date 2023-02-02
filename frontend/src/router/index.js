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

      //控制是否需要登陆验证
      requireAuth: false
    }
  },
  {
    path: '/404',
    component: () => import('../components/404Page'),
    meta: {
      title: '404',
      //控制是否需要登陆验证
      requireAuth: false
    }
  },
]

//管理员身份的路由
export var adminRoutes = {
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

//经理身份的路由
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
  if (to.matched.length === 0) {
    router.push('/404')
  }
  else if(from.meta.requireAuth && !localStorage.token) {
    router.push('/')
  }


})

export default router

