<template>
  <v-data-iterator :items="items" :search="search" hide-default-footer no-results-text="没有搜索结果" no-data-text="没有数据"
    disable-pagination>
    <template v-slot:header>
      <v-toolbar class="mb-1" rounded :color="$vuetify.theme.dark === false ? 'white' : '#121212'" flat>

        <v-dialog v-model="dialog1" persistent max-width="550px"
          :fullscreen="$vuetify.breakpoint.xsOnly ? true : false">
          <template v-slot:activator="{ on, attrs }">
            <v-btn large color="secondary" class="mr-5" outlined v-bind="attrs" v-on="on">
              <v-icon>mdi-plus</v-icon>
              新增分店
            </v-btn>
          </template>
          <v-card>
            <v-card-title>
              <span class="text-h5">新增分店</span>
            </v-card-title>
            <v-card-text>
              <v-container>
                <v-form ref="addShopForm" lazy-validation>
                  <v-row>

                    <v-col cols="12">
                      <v-text-field label="分店地址*" v-model="newShop.address" :rules="rules.noneEmptyRules"
                        prepend-icon="mdi-map-marker" required></v-text-field>
                    </v-col>

                    <v-col cols="12" sm="6">
                      <v-text-field label="分店名称*" v-model="newShop.name" :rules="rules.nameRules" counter="10"
                        prepend-icon="mdi-store" required></v-text-field>
                    </v-col>

                    <v-col cols="12" sm="6">
                      <v-text-field label="分店面积*" type="number" v-model="newShop.size" :rules="rules.noneEmptyRules"
                        prepend-icon="mdi-domain" required></v-text-field>
                    </v-col>

                  </v-row>
                </v-form>
              </v-container>
              <small>*为必填项</small>
            </v-card-text>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn color="primary" text @click="dialog1 = false; $refs.addShopForm.reset()">
                关闭
              </v-btn>
              <v-btn color="primary" @click="addShop()">
                提交
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>

        <template>
          <v-text-field v-model="search" clearable flat solo-inverted hide-details prepend-inner-icon="mdi-magnify"
            label="搜索"></v-text-field>
        </template>
      </v-toolbar>

    </template>

    <template v-slot:default="props">

      <v-row class="px-5 py-5" v-if="ready">
        <v-col v-for="(item, index) in props.items" :key="item.id" cols="12" md="6" lg="4">
          <v-card elevation="10" max-width="400" class="mx-auto">

            <v-carousel :continuous="false" show-arrows-on-hover hide-delimiters delimiter-icon="mdi-minus"
              height="230">

              <v-carousel-item>
                <v-card class="mx-auto " color="secondary" max-width="600">
                  <v-card-text>
                    <v-sheet color="rgba(0, 0, 0, .12)">
                      <span class="text-h6 ml-1 mt-1 white--text">客流量预测概览</span>
                      <v-sparkline :value="item.value1" color="rgba(255, 255, 255, .7)" height="120" padding="24"
                        stroke-linecap="round" smooth>
                        <template v-slot:label="item">
                          {{ item.value }}
                        </template>
                      </v-sparkline>
                    </v-sheet>
                  </v-card-text>
                </v-card>
              </v-carousel-item>

              <v-carousel-item>
                <v-card class="mx-auto " color="secondary" max-width="600">
                  <v-card-text>
                    <v-sheet color="rgba(0, 0, 0, .12)">
                      <span class="text-h6 ml-1 mt-1 white--text">劳动力预测概览 <span
                          class="text-subtitle-1 grey--text text--lighten-2">(/人)</span></span>
                      <v-sparkline :value="item.value2" color="rgba(255, 255, 255, .7)" height="120" padding="24"
                        stroke-linecap="round" smooth>
                        <template v-slot:label="item">
                          {{ item.value }}
                        </template>
                      </v-sparkline>
                    </v-sheet>
                  </v-card-text>
                </v-card>
              </v-carousel-item>


            </v-carousel>

            <v-list two-line>
              <v-list-item>
                <v-list-item-content>
                  <v-list-item-title class="text-h5">{{ item.name }}</v-list-item-title>
                  <v-list-item-subtitle class="mt-3">{{ item.address }}</v-list-item-subtitle>
                </v-list-item-content>
                <v-list-item-action>
                  <v-dialog v-model="item.dialog" persistent max-width="550px"
                    :fullscreen="$vuetify.breakpoint.xsOnly ? true : false">
                    <template v-slot:activator="{ on, attrs }">
                      <v-btn large color="secondary" class="mr-5" outlined v-bind="attrs" v-on="on"
                        @click="editedShop.name = item.name; editedShop.address = item.address; editedShop.size = item.size">
                        编辑信息
                      </v-btn>
                    </template>
                    <v-card>
                      <v-card-title>
                        <span class="text-h5">编辑信息</span>
                      </v-card-title>
                      <v-card-text>
                        <v-container>
                          <v-form :ref="`editShopForm${index}`" lazy-validation>
                            <v-row>

                              <v-col cols="12">
                                <v-text-field label="分店地址*" v-model="editedShop.address" :rules="rules.noneEmptyRules"
                                  prepend-icon="mdi-map-marker" required></v-text-field>
                              </v-col>

                              <v-col cols="12" sm="6">
                                <v-text-field label="分店名称*" v-model="editedShop.name" :rules="rules.nameRules"
                                  counter="10" prepend-icon="mdi-store" required></v-text-field>
                              </v-col>

                              <v-col cols="12" sm="6">
                                <v-text-field label="分店面积*" type="number" v-model="editedShop.size"
                                  :rules="rules.noneEmptyRules" prepend-icon="mdi-domain" required></v-text-field>
                              </v-col>

                            </v-row>
                          </v-form>
                        </v-container>
                      </v-card-text>
                      <v-card-actions>
                        <v-btn color="error" text large @click="deleteShop(index)">
                          删除
                        </v-btn>
                        <v-spacer></v-spacer>
                        <v-btn color="primary" text large
                          @click="item.dialog = false; $refs[`editShopForm${index}`][0].reset()">
                          关闭
                        </v-btn>
                        <v-btn color="primary" @click="editShop(index, item)" large>
                          提交
                        </v-btn>
                      </v-card-actions>
                    </v-card>
                  </v-dialog>

                </v-list-item-action>
              </v-list-item>
            </v-list>

          </v-card>
        </v-col>
      </v-row>

        <v-row class="px-5 py-5" v-else>
          <v-col cols="12" md="6" lg="4" v-for="index of 6" :key="index">
            <v-skeleton-loader class="mx-auto" type="image,card-heading, actions" max-width="400"
              ></v-skeleton-loader>
          </v-col>
        </v-row>

    </template>

  </v-data-iterator>

</template>

<script>
import { createShop, getAllShop, editShopInfo, deleteShop } from '../../request/shop'
export default {
  data() {
    return {
      search: '',
      dialog1: false,
      ready:false,
      items: [{}],
      newShop: {
        name: '',
        address: '',
        size: null,
      },

      editedShop: {
        name: '',
        address: '',
        size: null,
      },


      rules: {
        nameRules: [
          v => !!v || '名称不能为空',
          v => (v && v.length <= 10) || '姓名长度不能大于10',
        ],
        noneEmptyRules: [
          v => !!v || '不能为空',
        ]

      },

    }
  },
  computed: {

  },
  methods: {
    addShop() {
      if (this.$refs.addShopForm.validate()) {
        createShop({
          name: this.newShop.name,
          address: this.newShop.address,
          size: this.newShop.size,
        }).then(res => {
          if (res.data.code === 0) {
            this.$emit('msg', '添加门店成功')
            this.items.push({
              id: res.data.data.id,
              name: res.data.data.name,
              address: res.data.data.address,
              size: res.data.data.size,
              value1: [0, 0, 0, 0, 0, 0, 0],
              value2: [0, 0, 0, 0, 0, 0, 0],
            })
          }

        }).catch(() => {
          this.$emit('msg', '网络错误')
        })
      }
    },
    editShop(index, item) {
      if (this.$refs[`editShopForm${index}`][0].validate()) {
        editShopInfo({
          id: item.id,
          name: this.editedShop.name,
          address: this.editedShop.address,
          size: this.editedShop.size
        }).then(res => {
          if (res.data.code === 0) {
            item.name = this.editedShop.name
            item.address = this.editedShop.address
            item.size = this.editedShop.size
            this.$emit('msg', '修改成功')
          }
        }).catch(() => {
          this.$emit('msg', '网络错误')
        })
      }
    },
    deleteShop(index) {
      deleteShop(this.items[index].id).then(res => {
        if (res.data.code === 0) {
          this.items.splice(index, 1)
          this.$emit('msg', '删除成功')
        }
      }).catch(() => {
        this.$emit('msg', '网络错误')
      })
    },
  },

  mounted() {
    getAllShop().then(res => {
      if (res.data.code === 0) {
        this.items = res.data.data
        for (var i = 0; i < this.items.length; i++) {
          this.items[i].value1 = [111, 446, 675, 754, 590, 610, 437],
            this.items[i].value2 = [444, 446, 531, 510, 558, 425, 754]
        }
        this.ready=true
      }
    }).catch(() => {
      this.$emit('msg', '网络错误')
    })
  }
}
</script>
