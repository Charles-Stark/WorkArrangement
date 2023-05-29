<template>
  <div>
    <v-data-iterator v-if="branches.length !== 0" :items="branches" :search="search" hide-default-footer
      no-results-text="没有搜索结果" no-data-text="没有数据" disable-pagination>
      <template v-slot:header>
        <v-toolbar class="mb-1" rounded :color="$vuetify.theme.dark === false ? 'white' : '#121212'" flat>

          <v-dialog v-model="dialog" persistent max-width="550px" :fullscreen="$vuetify.breakpoint.xsOnly"
            v-if="$store.state.isManager">
            <template v-slot:activator="{ on, attrs }">
              <v-btn large color="secondary" class="mr-5" outlined v-bind="attrs" v-on="on">
                <v-icon>mdi-plus</v-icon>
                新增分店
              </v-btn>
            </template>
            <newBranch @close="close" @msg="getMsg" />
          </v-dialog>

          <template>
            <v-text-field v-model="search" clearable flat solo hide-details prepend-inner-icon="mdi-magnify"
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
                        <v-sparkline :value="item.flow" color="rgba(255, 255, 255, .7)" height="120" padding="24"
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
                      :fullscreen="$vuetify.breakpoint.xsOnly">
                      <template v-slot:activator="{ on, attrs }">
                        <v-btn large color="secondary" class="mr-5" outlined v-bind="attrs" v-on="on"
                          @click="editedShop.name = item.name; editedShop.address = item.address; editedShop.size = item.size; editedShop.dialog = false">
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
                                    :rules="rules.noneEmptyRules" prepend-icon="mdi-domain" required
                                    @blur="editedShop.size = editedShop.size <= 0 ? '' : editedShop.size"></v-text-field>
                                </v-col>

                              </v-row>
                            </v-form>
                          </v-container>
                        </v-card-text>
                        <v-card-actions>


                          <v-dialog v-model="editedShop.dialog" width="500">
                            <template v-slot:activator="{ on, attrs }">
                              <v-btn color="error" text large @click="editedShop.dialog = true" v-bind="attrs" v-on="on">
                                删除
                              </v-btn>
                            </template>

                            <v-card>
                              <v-card-title class="text-h5">
                                删除门店
                              </v-card-title>

                              <v-card-text>
                                确定要删除{{ editedShop.name }}吗，此操作不可撤销
                              </v-card-text>

                              <v-card-actions>
                                <v-spacer></v-spacer>
                                <v-btn color="primary" text @click="editedShop.dialog = false">
                                  取消
                                </v-btn>
                                <v-btn color="error" text @click="deleteShop(index)">
                                  确认删除
                                </v-btn>
                              </v-card-actions>
                            </v-card>
                          </v-dialog>


                          <v-spacer></v-spacer>
                          <v-btn color="primary" text large
                            @click="item.dialog = false; $refs[`editShopForm${index}`][0].reset()">
                            关闭
                          </v-btn>
                          <v-btn color="primary" @click="item.dialog = false; editShop(index, item)" large>
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
            <v-skeleton-loader class="mx-auto" type="image,card-heading, actions" max-width="400"></v-skeleton-loader>
          </v-col>
        </v-row>

      </template>

    </v-data-iterator>
    <v-container v-else style="height: 80vh;">
      <v-row class="fill-height" align-content="center" justify="center">
        <v-col class="text-h5 text-center" cols="12">
          没有店铺信息
        </v-col>
        <v-col cols="12" class="text-center">

          <v-dialog v-model="dialog" persistent max-width="550px" :fullscreen="$vuetify.breakpoint.xsOnly"
            v-if="$store.state.isManager">
            <template v-slot:activator="{ on, attrs }">
              <v-btn outlined color="primary" @click="dialog = true" v-bind="attrs"
                v-on="on">点击添加<v-icon>mdi-plus</v-icon></v-btn>
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
                          prepend-icon="mdi-domain" required
                          @blur="newShop.size = newShop.size <= 0 ? '' : newShop.size"></v-text-field>
                      </v-col>

                    </v-row>
                  </v-form>
                </v-container>
                <small>*为必填项</small>
              </v-card-text>
              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="primary" text @click="dialog = false; $refs.addShopForm.reset()">
                  关闭
                </v-btn>
                <v-btn color="primary" @click="dialog = false; addShop()">
                  提交
                </v-btn>
              </v-card-actions>
            </v-card>
          </v-dialog>

        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script>
import { getAllShop, editShopInfo, deleteShop, getFlow, getShopInfo } from '@/request/shop'
import { getEmployee } from '@/request/staff'
import newBranch from '@/components/newBranch.vue'
export default {
  components: { newBranch },
  data() {
    return {
      search: '',
      dialog: false,
      ready: false,
      branches: [{}],

      editedShop: {
        name: '',
        address: '',
        size: null,
        dialog: false
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
  methods: {
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
      deleteShop(this.branches[index].id).then(res => {
        if (res.data.code === 0) {
          this.branches.splice(index, 1)
          this.$emit('msg', '删除成功')
        }
      }).catch(() => {
        this.$emit('msg', '网络错误')
      })
    },

    close() {
      this.dialog = false
    },

    getMsg(data) {
      this.$emit('msg', data)
    },
  },

  async mounted() {
    try {
      if (this.$store.state.isManager) {
        const shop1 = (await getAllShop()).data.data;
        if (shop1.length === 0) {
          this.branches = []
        }
        else {
          for (let i = 0; i < shop1.length; i++) {
            const flow1 = (await getFlow({
              shop: shop1[i].id,
              start: (new Date(Date.now() - (new Date()).getTimezoneOffset() * 60000)).toISOString().substr(0, 10) + " 00:00:00",
              lasting: 1
            })).data.data[0].flowUnits;
            shop1[i].flow = []
            shop1[i].flow = [flow1[0].flow, flow1[3].flow, flow1[6].flow, flow1[9].flow, flow1[12].flow, flow1[15].flow, flow1[18].flow, flow1[21].flow]
            this.branches = shop1
          }
        }
        this.ready = true
      }
      else if (this.$store.state.isShopManager) {
        const employee = (await getEmployee()).data.data.shop;
        const shop2 = (await getShopInfo(employee)).data.data;
        const flow2 = (await getFlow({
          shop: shop2.id,
          start: (new Date(Date.now() - (new Date()).getTimezoneOffset() * 60000)).toISOString().substr(0, 10) + " 00:00:00",
          lasting: 1
        })).data.data[0].flowUnits;
        shop2.flow = []
        shop2.flow = [flow2[0].flow, flow2[3].flow, flow2[6].flow, flow2[9].flow, flow2[12].flow, flow2[15].flow, flow2[18].flow, flow2[21].flow]
        this.branches = []
        this.branches.push(shop2)
        this.ready = true
      }
    } catch (err) {
      this.$emit('msg', '网络错误')
    }

  }
}
</script>
