<template>
  <v-container fluid class="mt-9">
    <v-row justify="center">
      <v-expansion-panels popout>
        <v-expansion-panel hide-actions>
          <v-expansion-panel-header>
            <v-row align="center" class="spacer" no-gutters>
              <v-col cols="4" sm="3" md="2">
                <v-avatar size="80" @click="changeAvatar()">
                  <img v-if="personal.avatar" alt="Avatar"
                    src="https://avatars0.githubusercontent.com/u/9064066?v=4&s=460">
                </v-avatar>
              </v-col>

              <v-col sm="5" md="3">
                <strong>{{ personal.name }}</strong>
              </v-col>

              <v-col class="text-no-wrap hidden-xs-only" cols="5" sm="2">
                <strong>账户设置</strong>
              </v-col>

              <v-col class="grey--text text-truncate hidden-sm-and-down">
                手机号，邮箱，密码，工号
              </v-col>
            </v-row>
          </v-expansion-panel-header>

          <v-expansion-panel-content>
            <div v-for="info in infos" :key="info.type">
              <v-divider></v-divider>
              <v-list flat rounded>
                <v-list-item-group>
                  <v-list-item @click="editInfo()">

                    <v-list-item-icon>
                      <v-icon color="accent">{{ info.icon }}</v-icon>
                    </v-list-item-icon>
                    <v-list-item-title>{{ info.type }}</v-list-item-title>
                    <v-list-item-subtitle>{{ info.data }}</v-list-item-subtitle>
                  </v-list-item>
                </v-list-item-group>
              </v-list>
            </div>
          </v-expansion-panel-content>

        </v-expansion-panel>


        <v-expansion-panel hide-actions>
          <v-expansion-panel-header>
            <v-row align="center" class="spacer" no-gutters>
              <v-col cols="4" sm="2" md="1">
                <v-icon color="green" size="35">
                  mdi-draw
                </v-icon>
              </v-col>

              <v-col class="text-no-wrap" cols="5" sm="3">
                <strong>显示设置</strong>
              </v-col>

              <v-col class="grey--text text-truncate hidden-xs-only">
                深色模式，跟随系统颜色设置，主题
              </v-col>
            </v-row>
          </v-expansion-panel-header>

          <v-expansion-panel-content>
            <v-divider></v-divider>
            <v-list flat rounded>
              <v-list-item-group>
                <v-list-item @click="darkMode()" :disabled="autoDark">
                  <template>
                    <v-list-item-icon>
                      <v-icon color="accent" size="30">mdi-theme-light-dark</v-icon>
                    </v-list-item-icon>
                    <v-list-item-title>深色模式</v-list-item-title>
                    <v-switch v-model="dark" :disabled="autoDark" @click="darkMode()"></v-switch>
                  </template>
                </v-list-item>
              </v-list-item-group>
            </v-list>

            <v-divider></v-divider>
            <v-list flat rounded>
              <v-list-item-group>
                <v-list-item @click="autoDarkMode()">
                  <template>
                    <v-list-item-icon>
                      <v-icon color="accent" size="30">mdi-desktop-classic</v-icon>
                    </v-list-item-icon>
                    <v-list-item-title>跟随系统颜色模式</v-list-item-title>
                    <v-switch v-model="autoDark" @click="autoDarkMode()"></v-switch>
                  </template>

                </v-list-item>
              </v-list-item-group>
            </v-list>

            <v-divider></v-divider>
            <v-list flat rounded>
              <v-list-item-group>
                <v-list-item>
                  <v-list-item-icon>
                    <v-icon color="accent" size="30">mdi-palette</v-icon>
                  </v-list-item-icon>
                  <v-list-item-title>主题颜色</v-list-item-title>
                  <v-btn-toggle v-model="toggle" mandatory>
                    <v-btn class="mx-2" fab :color="color.type" v-for="color in colors" :key="color.type"
                      @click="changeTheme(color)"></v-btn>
                  </v-btn-toggle>
                </v-list-item>
              </v-list-item-group>
            </v-list>

          </v-expansion-panel-content>
        </v-expansion-panel>

      </v-expansion-panels>
    </v-row>
  </v-container>



</template>

<script>
import themes from '../../../store/themes'
export default {
  data: () => ({
    dark: null,
    autoDark: null,
    toggle: [],
    personal: {
      avatar: 'https://avatars0.githubusercontent.com/u/9064066?v=4&s=460',
      name: 'John Leider',
      id: 2100303308,
      phone: 15858926005,
      email: 'abcdefg@xxx.com',
      psw: '1234567'
    },
    items: [
      'Dog Photos',
      'Cat Photos',
      'Potatoes',
      'Carrots',
    ],
    model: ['Carrots'],

  }),
  computed: {
    infos() {
      return [
        { type: "工号", data: this.personal.id, editable: false, icon: 'mdi-card-account-details' },
        { type: "手机号", data: this.personal.phone, editable: true, icon: 'mdi-phone' },
        { type: "邮箱", data: this.personal.email, editable: true, icon: 'mdi-gmail' },
        { type: "密码", data: '点击修改', editable: true, icon: 'mdi-key' }
      ]
    },
    colors() {
      return [{ type: 'blue', theme: themes.blue },
      { type: 'green', theme: themes.green },
      { type: 'purple', theme: themes.purple },
      { type: 'orange', theme: themes.orange },
      { type: 'pink', theme: themes.pink }]
    }

  },

  methods: {
    editInfo() {
      alert('编辑信息')
    },
    changeAvatar() {
      alert('更换头像')
    },
    changeTheme(color) {
      this.$vuetify.theme.themes = color.theme
      this.$store.commit('theme', color.type)
    },
    darkMode() {
      this.dark = !this.dark
      this.$store.commit('dark', this.dark)
      this.$vuetify.theme.dark = this.dark
    },
    autoDarkMode() {
      this.autoDark = !this.autoDark
      this.$store.commit('auto_dark', this.autoDark)
    },

  },

  watch: {
    autoDark() {
      if (this.$store.state.autoDark === true) {
        this.dark = window.matchMedia("(prefers-color-scheme: dark)").matches ? 'true' : ''
        this.$vuetify.theme.dark = window.matchMedia("(prefers-color-scheme: dark)").matches
        this.$store.commit('dark', window.matchMedia("(prefers-color-scheme: dark)").matches ? 'true' : '')
      }
    }
  },

  created() {
    this.dark = this.$store.state.dark
    this.autoDark = this.$store.state.autoDark
  }


}
</script>