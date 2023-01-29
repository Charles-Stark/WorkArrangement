<template>
  <v-row justify="center">
    <v-dialog v-model="dialog" persistent max-width="450" :fullscreen="$vuetify.breakpoint.xsOnly">
      <template v-slot:activator="{ on, attrs }">
        <v-btn color="primary" dark v-bind="attrs" v-on="on">
          确定
        </v-btn>
      </template>

      <v-card>
        <v-tabs background-color="primary darken-2" center-active dark>
          <v-tab @click="show = 1">登录</v-tab>
          <v-tab @click="show = 2">注册</v-tab>
          <v-spacer></v-spacer>
          <v-btn icon large @click="dialog = false" class="my-auto mr-1"><v-icon>mdi-close</v-icon></v-btn>
        </v-tabs>
        <v-card-text class="mt-3">
          <v-container>
            <keep-alive>
              <loginBox v-if="show === 1" @msg="getMsg"></loginBox>
              <registerBox v-if="show === 2" @msg="getMsg"></registerBox>
            </keep-alive>

          </v-container>
        </v-card-text>
      </v-card>
    </v-dialog>

    <v-snackbar v-model="snackBar">
      {{ snackBarText }}
      <template v-slot:action="{ attrs }">
        <v-btn color="error" icon v-bind="attrs" @click="snackBar = false">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </template>
    </v-snackbar>

  </v-row>
</template>

<script>
import loginBox from '@/components/loginBox.vue';
import registerBox from '@/components/registerBox.vue';
export default {
  components: { loginBox, registerBox },
  data: () => ({
    dialog: false,
    show: 1,
    snackBar: false,
    snackBarText: ''
  }),
  computed: {

  },
  methods: {
    getMsg(data) {
      this.snackBarText = data
      this.snackBar=true
    }
  },


}
</script>

