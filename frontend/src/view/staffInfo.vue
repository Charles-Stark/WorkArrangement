<template>
  <div>
    <v-data-iterator v-if="branches.length !== 0 && ready1" :items="staff" :search="search" :page.sync="page"
      hide-default-footer no-results-text="没有搜索结果" :sort-by="keys[sortBy]" :sort-desc="sortDesc" no-data-text="没有员工">
      <template v-slot:header>
        <v-toolbar class="mb-1" rounded :color="$vuetify.theme.dark === false ? 'white' : '#121212'" flat>

          <v-dialog v-model="dialog" persistent max-width="600" :fullscreen="$vuetify.breakpoint.xsOnly">
            <template v-slot:activator="{ on, attrs }">
              <v-btn large color="secondary" class="mr-5" outlined v-bind="attrs" v-on="on">
                <v-icon>mdi-plus</v-icon>
                新增员工
              </v-btn>
            </template>
            <newEmployee v-if="dialog" :shop="branches[branch].id" :branches="branches" @close="close" @msg="getMsg" />
          </v-dialog>

          <template>
            <v-text-field v-if="$vuetify.breakpoint.mdAndUp" v-model="search" clearable flat solo-inverted hide-details
              prepend-inner-icon="mdi-magnify" label="搜索"></v-text-field>

            <v-spacer></v-spacer>

            <template>
              <v-select v-model="sortBy" clearable flat solo-inverted hide-details :items="Object.keys(keys)"
                prepend-inner-icon="mdi-magnify" label="排序"></v-select>
            </template>

            <v-spacer></v-spacer>

            <template>
              <v-btn-toggle v-model="sortDesc" mandatory>
                <v-btn large depressed color="secondary" :value="false" :disabled="!sortBy">
                  <v-icon>mdi-arrow-up</v-icon>
                </v-btn>
                <v-btn large depressed color="secondary" :value="true" :disabled="!sortBy">
                  <v-icon>mdi-arrow-down</v-icon>
                </v-btn>
              </v-btn-toggle>
            </template>

          </template>

        </v-toolbar>

        <v-tabs background-color="transparent" v-model="branch" v-if="$store.state.isManager">
          <v-tab v-for="branch in branches" :key="branch.id" @change="getStaff(branch.id)">{{ branch.name }}</v-tab>
        </v-tabs>

      </template>

      <template v-slot:default="props">

        <div class="mx-10" v-if="ready2">
          <v-row class="mt-3">
            <v-col cols="3">工号</v-col>
            <v-col cols="3">姓名</v-col>
            <v-col cols="3">职位</v-col>
            <v-col cols="3">编辑</v-col>
            <v-col cols="12"><v-divider></v-divider></v-col>
          </v-row>

          <v-row v-for="(item, index) in props.items" :key="item.uid">
            <v-col cols="3">
              <v-list-item-content>
                {{ item.uid }}
              </v-list-item-content>
            </v-col>
            <v-col cols="3">
              <v-list-item-content>
                {{ item.username }}
              </v-list-item-content>
            </v-col>
            <v-col cols="3">
              <v-list-item-content>
                {{ item.position }}
              </v-list-item-content>
            </v-col>
            <v-spacer></v-spacer>
            <v-col cols="3">

              <v-dialog max-width="600" :fullscreen="fullscreen" v-model="item.dialog1" persistent>
                <template v-slot:activator="{ on, attrs }">
                  <v-btn outlined color="warning lighten-1" v-bind="attrs" v-on="on" class="mt-1 mx-1"
                    :icon="$vuetify.breakpoint.mdAndDown" :value="item.approved" @click="editedEmployee.email = item.email; editedEmployee.username = item.username; editedEmployee.salary = item.salary;
                    editedEmployee.position = item.position; editedEmployee.shop = item.shop; editedEmployee.durationOfShift = item.durationOfShift; editedEmployee.durationOfWeek = item.durationOfWeek;
                    editedEmployee.workingDay = item.workingDay; editedEmployee.workingHours = item.editedEmployee">
                    <v-icon>mdi-pencil</v-icon>
                    <span v-if="!$vuetify.breakpoint.mdAndDown">编辑</span>
                  </v-btn>
                </template>

                <v-card>

                  <v-tabs v-model="tabs">
                    <v-tab>
                      基本信息
                    </v-tab>
                    <v-tab>
                      员工偏好
                    </v-tab>
                  </v-tabs>

                  <v-tabs-items v-model="tabs">
                    <v-tab-item>
                      <v-card>
                        <v-card-title>
                          <span class="text-h5">编辑信息</span>
                        </v-card-title>
                        <v-card-text>
                          <v-container>
                            <v-form :ref="`editEmployeeForm${index}`" lazy-validation>
                              <v-row>


                                <v-col cols="12">
                                  <v-text-field label="薪资*" type="number" v-model="editedEmployee.salary"
                                    :rules="rules.noneEmptyRules" prepend-icon="mdi-cash" required
                                    @blur="editedEmployee.salary = editedEmployee.salary <= 0 ? '' : editedEmployee.salary"></v-text-field>
                                </v-col>

                                <v-col :cols="$store.state.isManager ? 6 : 12">
                                  <v-select :items="['门店经理', '副经理', '小组长', '收银', '导购', '库房']" label="职位*"
                                    prepend-icon="mdi-office-building-marker-outline" v-model="editedEmployee.position"
                                    :rules="rules.noneEmptyRules" required></v-select>
                                </v-col>

                                <v-col cols="12" sm="6" v-if="$store.state.isManager">
                                  <v-select :items="branches" item-text="name" item-value="id" label="所属分店*"
                                    v-model="editedEmployee.shop" :rules="rules.noneEmptyRules" prepend-icon="mdi-store"
                                    required></v-select>
                                </v-col>



                              </v-row>
                            </v-form>
                          </v-container>
                        </v-card-text>
                        <v-card-actions>
                          <v-spacer></v-spacer>
                          <v-btn color="primary" text
                            @click="item.dialog1 = false; $refs[`editEmployeeForm${index}`][0].reset()" large>
                            关闭
                          </v-btn>
                          <v-btn color="primary" @click="editEmployee(index, item)" large>
                            提交
                          </v-btn>
                        </v-card-actions>
                      </v-card>
                    </v-tab-item>
                    <v-tab-item>
                      <editFavor @close="item.dialog1 = false" :id="item.id" @msg="getMsg">
                      </editFavor>
                    </v-tab-item>
                  </v-tabs-items>
                </v-card>
              </v-dialog>

              <v-dialog max-width="470" v-model="item.dialog2">
                <template v-slot:activator="{ on, attrs }">
                  <v-btn outlined color="error lighten-1" v-bind="attrs" v-on="on" class="mt-1 mx-1"
                    :value="item.approved" :icon="$vuetify.breakpoint.mdAndDown">
                    <v-icon>mdi-delete</v-icon>
                    <span v-if="!$vuetify.breakpoint.mdAndDown">删除</span>
                  </v-btn>
                </template>

                <v-card>
                  <v-card-title class="text-h6">
                    确定删除？
                  </v-card-title>
                  <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="grey" text @click="item.dialog2 = false" large>
                      返回
                    </v-btn>
                    <v-btn color="error" text @click="deleteEmployee(index, item.id)" large>
                      确定
                    </v-btn>
                  </v-card-actions>
                </v-card>
              </v-dialog>

            </v-col>

            <v-col cols="12"><v-divider></v-divider></v-col>

          </v-row>

        </div>

        <v-container v-else>
          <v-row>
            <v-col cols="12" v-for="index of 6" :key="index">
              <v-skeleton-loader class="mx-auto" type="table-heading,list-item-two-line,divider"></v-skeleton-loader>
            </v-col>
          </v-row>
        </v-container>

      </template>

      <template v-slot:footer v-if="ready2 && staff.length !== 0">
        <v-pagination class="mt-4" v-model="page" :length="numberOfPages" color="secondary"></v-pagination>
      </template>

    </v-data-iterator>
    <v-container v-else-if="!ready1">
      <v-row>
        <v-col cols="12" v-for="index of 6" :key="index">
          <v-skeleton-loader class="mx-auto" type="table-heading,list-item-two-line,divider"></v-skeleton-loader>
        </v-col>
      </v-row>
    </v-container>
    <v-container v-else style="height: 80vh;">
      <v-row class="fill-height" align-content="center" justify="center">
        <v-col class="text-h5 text-center" cols="12">
          没有店铺信息
        </v-col>
        <v-col cols="12" class="text-center">
          <v-btn outlined color="primary" to="/controlpanel/branches">点击跳转到分店页<v-icon>mdi-arrow-right</v-icon></v-btn>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script>
import {getAllShop, getShopInfo} from '@/request/shop'
import {deleteEmployee, editEmployeeInfo, getEmployee, getEmployeeByShop} from '@/request/staff'
import editFavor from '@/components/editFavor.vue'
import newEmployee from '@/components/newEmployee.vue'

export default {
  components: {
    editFavor,
    newEmployee
  },
  data() {
    return {
      search: '',
      page: 1,
      branch: 0,
      dialog: false,
      ready1: false,
      ready2: false,
      sortBy: null,
      keys: {
        '工号': 'id',
        '姓名': 'username',
      },
      sortDesc: true,
      tabs: 0,
      staff: [{}],
      branches: [],

      rules: {
        emailRules: [
          v => !!v || '邮箱不能为空',
          v => /.+@.+\..+/.test(v) || '邮箱格式错误',
        ],
        nameRules: [
          v => !!v || '姓名不能为空',
          v => (v && v.length <= 10) || '姓名长度不能大于10',
        ],
        noneEmptyRules: [
          v => !!v || '不能为空',
        ]

      },

      editedEmployee: {
        position: '',
        shop: '',
        salary: null,
      },


    }
  },
  computed: {
    numberOfPages() {
      return Math.ceil(this.staff.length / 10)
    },
    fullscreen() {
      return this.$vuetify.breakpoint.xsOnly
    },
  },
  methods: {
    nextPage() {
      if (this.page + 1 <= this.numberOfPages) this.page += 1
    },
    formerPage() {
      if (this.page - 1 >= 1) this.page -= 1
    },
    editEmployee(index, item) {
      if (this.$refs[`editEmployeeForm${index}`][0].validate()) {
        editEmployeeInfo({
          id: item.id,
          salary: this.editedEmployee.salary,
          shop: this.editedEmployee.shop,
          position: this.editedEmployee.position,
        }).then(res => {
          if (res.data.code === 0) {
            this.$emit('msg', '修改信息成功')
            this.$router.go(0)
          }
        }).catch(() => {
          this.$emit('msg', '网络错误')
        })

      }
    },
    deleteEmployee(id) {
      deleteEmployee(id).then(res => {
        if (res.data.code === 0) {
          this.$emit('msg', '删除成功')
          this.$router.go(0)
        }
      }).catch(() => {
        this.$emit('msg', '网络错误')
      })
    },

    getStaff(shopId) {
      this.ready2 = false
      getEmployeeByShop(shopId).then(res => {
        this.staff = res.data.data
        this.ready1 = true
        this.ready2 = true

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
        this.branches = (await getAllShop()).data.data
        if (this.branches.length !== 0) {
          this.getStaff(this.branches[this.branch].id)
        }
        else {
          this.staff = []
          this.ready1 = true

        }
      }
      if (this.$store.state.isShopManager) {
        const employee = (await getEmployee()).data.data;
        this.getStaff(employee.shop)
        const shop = (await getShopInfo(employee.shop)).data.data;
        this.branches.push(shop)
        this.ready1 = true
      }
    } catch (err) {
      this.$emit('msg', '网络错误')
    }

  },

}
</script>
