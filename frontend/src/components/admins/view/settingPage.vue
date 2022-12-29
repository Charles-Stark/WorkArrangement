<template>
  <v-container fluid class="mt-9">
    <v-row justify="center">
      <v-expansion-panels popout>
        <v-expansion-panel hide-actions>
          <v-expansion-panel-header>
            <v-row align="center" class="spacer" no-gutters>
              <v-col cols="4" sm="2" md="1">
                <v-avatar size="36px">
                  <img v-if="personal.avatar" alt="Avatar"
                    src="https://avatars0.githubusercontent.com/u/9064066?v=4&s=460">
                </v-avatar>
              </v-col>

              <v-col class="hidden-xs-only" sm="5" md="3">
                <strong>{{ personal.name }}</strong>
              </v-col>

              <v-col class="text-no-wrap" cols="5" sm="3">
                <strong>账户设置</strong>
              </v-col>

              <v-col class="grey--text text-truncate hidden-sm-and-down">
                手机号，邮箱，密码，工号
              </v-col>
            </v-row>
          </v-expansion-panel-header>

          <v-expansion-panel-content v-for="info in infos" :key="info.type" >
            <v-divider></v-divider>
            <v-list flat >
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
          </v-expansion-panel-content>

        </v-expansion-panel>


        <v-expansion-panel hide-actions>
          <v-expansion-panel-header>
            <v-row align="center" class="spacer" no-gutters>
              <v-col cols="4" sm="2" md="1">
                <v-icon color="green" size="28">
                  mdi-draw
                </v-icon>
              </v-col>

              <v-col class="text-no-wrap" cols="5" sm="3">
                <strong>显示设置</strong>
              </v-col>

              <v-col class="grey--text text-truncate hidden-sm-and-down">
                深色模式，主题
              </v-col>
            </v-row>
          </v-expansion-panel-header>

          <v-expansion-panel-content>
            <v-divider></v-divider>
            asffasfas
          </v-expansion-panel-content>

        </v-expansion-panel>
      </v-expansion-panels>
    </v-row>
  </v-container>



</template>

<script>

export default {
  data: () => ({
    personal: {
      avatar: 'https://avatars0.githubusercontent.com/u/9064066?v=4&s=460',
      name: 'John Leider',
      id: 2100303308,
      phone: 15858926005,
      email: 'abcdefg@xxx.com',
      psw: '1234567'
    },


  }),
  computed: {
    infos() {
      return [
        { type: "工号", data: this.personal.id, editable: false, icon: 'mdi-card-account-details' },
        { type: "手机号", data: this.personal.phone, editable: true, icon: 'mdi-phone' },
        { type: "邮箱", data: this.personal.email, editable: true, icon: 'mdi-gmail' },
        { type: "密码", data: '点击修改', editable: true, icon: 'mdi-key' }
      ]
    }
  },

  methods:{
    editInfo(){
      
    }
  }



}
</script>