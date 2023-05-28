//路由配置
import Vue from 'vue'
import VueRouter from 'vue-router'


Vue.use(VueRouter)

//无权限限制的路由
var commonRoutes = [
  {
    path: '/',
    component: () => import('../view/mainView'),
    meta: {
      title: '慧博云通智能排班',
    }
  },
  {
    path: '/404',
    component: () => import('../view/404Page'),
    meta: {
      title: '404',
    }
  },
]

//管理员身份的路由
export var adminRoutes = {
  path: '/controlpanel',
  redirect: '/controlpanel/dashBoard',
  component: () => import('../view/naviBar'),
  children: [
    {
      path: 'dashboard',
      component: () => import('../view/dashBoard'),
      meta: {
        title: '概览',
        //控制左侧导航栏选中
        selectedItem: 0,
      }
    }, {
      path: 'arrange',
      component: () => import('../view/workArrange'),
      meta: {
        title: '智能排班',
        selectedItem: 1,
      }
    }, {
      path: 'history',
      component: () => import('../view/arrangementHistory'),
      meta:{
        title:'历史排班'
      }
    },{
      path: 'absences',
      component: () => import('../view/absenceNotis'),
      meta: {
        title: '请假管理',
        selectedItem: 2,
      }
    }, {
      path: 'notifications',
      component: () => import('../view/notisCenter'),
      meta: {
        title: '通知中心',
        selectedItem: 3,
      }
    }, {
      path: 'branches',
      component: () => import('../view/branchInfo'),
      meta: {
        title: '分店信息',
        selectedItem: 4,
      }
    }, {
      path: 'staff',
      component: () => import('../view/staffInfo'),
      meta: {
        title: '员工信息',
        selectedItem: 5,
      }
    }, {
      path: 'reviews',
      component: () => import('../view/customerReview'),
      meta: {
        title: '用户评价',
        selectedItem: 6,
      }
    },{
      path: 'settings',
      component: () => import('../view/settingPage'),
      meta: {
        title: '用户设置',
        selectedItem: 7,
      }
    },
  ]
}


//普通员工身份的路由
export var employeeRoutes = {
  path: '/controlpanel',
  redirect: '/controlpanel/dashBoard',
  component: () => import('../view/naviBar'),
  children: [
    {
      path: 'dashboard',
      component: () => import('../view/dashBoard'),
      meta: {
        title: '概览',
        //控制左侧导航栏选中
        selectedItem: 0,
      }
    }, {
      path: 'arrange',
      component: () => import('../view/workArrange'),
      meta: {
        title: '智能排班',
        selectedItem: 1,
      }
    }, {
      path: 'notifications',
      component: () => import('../view/notisCenter'),
      meta: {
        title: '通知中心',
        selectedItem: 3,
      }
    }, {
      path: 'settings',
      component: () => import('../view/settingPage'),
      meta: {
        title: '用户设置',
        selectedItem: 7,
      }
    },
  ]
}


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
router.afterEach((to) => {
  if (!localStorage.token && to.matched.length === 0) {
    router.push('/404')
  }
})

export default router

