<template>
  <v-app id="inspire">

    <!-- 顶栏 -->
    <v-app-bar app elevation="0" :color="$vuetify.theme.dark === false ? 'white' : '#121212'">
      <v-app-bar-nav-icon @click="miniManual = !miniManual" class="hidden-sm-and-down"></v-app-bar-nav-icon>
      <v-toolbar-title>{{ $router.currentRoute.meta.title }}</v-toolbar-title>
      <v-spacer></v-spacer>

      <v-menu offset-y allow-overflow max-height="600" max-width="400">
        <template v-slot:activator="{ on, attrs }">
          <v-badge :value="noti" :content="noti" overlap bordered dot>
            <v-btn icon v-bind="attrs" v-on="on">
              <v-icon>mdi-bell-outline</v-icon>
            </v-btn>
          </v-badge>

        </template>

        <v-list three-line>
          <v-subheader>
            当前共有{{ noti }}条未读通知
          </v-subheader>
          <template v-for="(notice, index) in notices">
            <v-divider :key="index + ' ' + notice.type"></v-divider>

            <v-list-item :key="index" @click="jumpToNoti()">
              <v-list-item-avatar size="50" v-if="notice.type === 1">
                <v-img :src="notice.avatar"></v-img>
              </v-list-item-avatar>
              <v-list-item-content>
                <v-list-item-subtitle class="mt-1 mx-2 text-subtitle-1"
                  :class="notice.read === false ? 'strong--text' : 'grey--text'" v-if="notice.type === 1">{{
                    notice.name
                  }}申请了请假，点击查看详情</v-list-item-subtitle>
                <v-list-item-subtitle class="mt-1 mx-2 text-subtitle-1"
                  :class="notice.read === false ? 'strong--text' : 'grey--text'"
                  v-else>有一个开放班次长时间无人认领，点击进行手动排班</v-list-item-subtitle>
                <v-list-item-subtitle class="text-caption mt-3">{{ notice.date }} {{
                  notice.time
                }}</v-list-item-subtitle>
              </v-list-item-content>

            </v-list-item>


          </template>
        </v-list>
      </v-menu>

      <v-menu bottom min-width="170" rounded class="ma-6" offset-y>
        <template v-slot:activator="{ on }">
          <v-btn icon v-on="on" class="ma-8">
            <v-avatar color="blue" size="44">
              <span class="white--text text-h6">{{ user.initials }}</span>
            </v-avatar>
          </v-btn>
        </template>
        <v-card>
          <v-list-item-content class="justify-center">
            <div class="mx-auto text-center">
              <v-avatar color="blue">
                <span class="white--text text-h5">{{ user.initials }}</span>
              </v-avatar>
              <h3>{{ user.fullName }}</h3>
              <p class="text-caption mt-1">
                {{ user.email }}
              </p>
              <v-divider class="my-3"></v-divider>
              <v-btn depressed rounded text>
                账户设置
              </v-btn>
              <v-divider class="my-3"></v-divider>
              <v-btn depressed rounded text color="warning">
                退出登录
              </v-btn>
            </div>
          </v-list-item-content>
        </v-card>
      </v-menu>

    </v-app-bar>

    <!-- 抽屉 -->
    <v-navigation-drawer v-model="drawer" permanent :mini-variant="mini" app mini-variant-width="60" width="220"
      :color="$vuetify.theme.dark === false ? 'white' : '#151515'">

      <v-list-item class="px-2 mt-2">
        <v-list-item v-if="mini == false">
          <v-img v-if="$vuetify.theme.dark === false" :src="require('../../assets/logo-md.png')" width="10"></v-img>
          <v-img v-else :src="require('../../assets/logo-md-dark.png')" width="10"></v-img>
        </v-list-item>

        <v-avatar v-if="mini == true">
          <v-img :src="require('../../assets/logo-sm.png')"></v-img>
        </v-avatar>
      </v-list-item>

      <v-list shaped class="mt-5 text-left">

        <v-list-item-group v-model="selectedItem" color="primary" mandatory>

          <v-list-item link to="/controlpanel/dashboard">
            <v-list-item-icon>
              <v-icon>mdi-home</v-icon>
            </v-list-item-icon>
            <v-list-item-title>概览</v-list-item-title>
          </v-list-item>

          <v-list-item link to="arrange">
            <v-list-item-icon>
              <v-icon>mdi-sitemap</v-icon>
            </v-list-item-icon>
            <v-list-item-title>智能排班</v-list-item-title>
          </v-list-item>


          <v-list-item to="/controlpanel/absences" link>
            <v-list-item-icon>
              <v-icon>mdi-shore</v-icon>
            </v-list-item-icon>
            <v-list-item-title>请假管理</v-list-item-title>
          </v-list-item>

          <v-list-group prepend-icon="mdi-inbox" mandatory :value="expand">
            <template v-slot:activator>
              <v-list-item-title>信息管理</v-list-item-title>
            </template>

            <v-list-item to="/controlpanel/branches" link>
              <v-list-item-icon>
                <v-icon>mdi-store</v-icon>
              </v-list-item-icon>
              <v-list-item-title>分店信息</v-list-item-title>
            </v-list-item>

            <v-list-item to="/controlpanel/staff" link>
              <v-list-item-icon>
                <v-icon>mdi-account</v-icon>
              </v-list-item-icon>
              <v-list-item-title>员工信息</v-list-item-title>
            </v-list-item>
          </v-list-group>

          <v-list-item to="/controlpanel/settings" link>
            <v-list-item-icon>
              <v-icon>mdi-cogs</v-icon>
            </v-list-item-icon>
            <v-list-item-title>用户设置</v-list-item-title>
          </v-list-item>

        </v-list-item-group>

      </v-list>

      <v-footer absolute v-if="mini === false" :color="$vuetify.theme.dark === false ? '' : '#1b1b1b'">
        <v-subheader class="grey--text font-italic">©2023</v-subheader>
      </v-footer>
    </v-navigation-drawer>

    <!-- 内容显示区 -->
    <v-main>
      <router-view></router-view>
    </v-main>
    
  </v-app>
</template>

<script>

export default {
  data: () => ({
    drawer: false,
    miniManual: false,
    user: {
      initials: 'JD',
      fullName: 'John Doe',
      email: 'john.doe@doe.com',
    },

    notices: [
      { type: 1, avatar: 'https://cdn.vuetifyjs.com/images/lists/1.jpg', name: 'aa', time: '10:24', date: '2022/10/24', read: true },
      { type: 2, time: '10:24', date: '2022/10/24', read: false },
      { type: 1, avatar: 'https://cdn.vuetifyjs.com/images/lists/2.jpg', name: 'bb', time: '10:24', date: '2022/10/24', read: false },
      { type: 2, time: '10:24', date: '2022/10/24', read: true },
      { type: 2, time: '10:24', date: '2022/10/24', read: false },
      { type: 2, time: '10:24', date: '2022/10/24', read: true },
      { type: 2, time: '10:24', date: '2022/10/24', read: false },
      { type: 2, time: '10:24', date: '2022/10/24', read: false },
      { type: 2, time: '10:24', date: '2022/10/24', read: true },
    ],



  }),
  computed: {
    mini() {
      return this.$vuetify.breakpoint.smAndDown ? true : this.miniManual
    },
    selectedItem: {
      set() { },
      get() {
        return this.$router.currentRoute.meta.selectedItem
      }
    },
    expand() {
      let path = this.$router.currentRoute.meta.selectedItem
      return path > 4 ? true : false
    },
    noti() {
      var num = 0
      for (var notice in this.notices) {
        if (this.notices[notice].read === false) {
          num++
        }
      }
      return num
    }
  },
  methods: {
    darkMode() {
      window.localStorage.dark = window.localStorage.dark === 'true' ? '' : 'true'
      this.$vuetify.theme.dark = window.localStorage.dark
    },
    jumpToNoti() {
      alert("~~~")
    },

  },


}
</script>

