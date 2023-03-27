<template>
  <div>

    <v-data-iterator :items="staff" :search="search" :page.sync="page" hide-default-footer no-results-text="没有搜索结果"
      :sort-by="keys[sortBy]" :sort-desc="sortDesc" no-data-text="没有数据">
      <template v-slot:header>
        <v-toolbar class="mb-1" rounded :color="$vuetify.theme.dark === false ? 'white' : '#121212'" flat>

          <v-dialog v-model="dialog1" persistent max-width="600" :fullscreen="$vuetify.breakpoint.xsOnly ? true : false">
            <template v-slot:activator="{ on, attrs }">
              <v-btn large color="secondary" class="mr-5" outlined v-bind="attrs" v-on="on"
                @click="newEmployee.shop = branches[branch].id">
                <v-icon>mdi-plus</v-icon>
                新增员工
              </v-btn>
            </template>
            <v-card>
              <v-card-title>
                <span class="text-h5">新增员工</span>
              </v-card-title>
              <v-card-text>
                <v-container>
                  <v-form ref="addEmployeeForm" lazy-validation>
                    <v-row>
                      <v-col cols="12"><span class="text-subtitle-1 mt-3">员工信息</span></v-col>

                      <v-col cols="12">
                        <v-text-field label="邮箱*" v-model="newEmployee.email" :rules="rules.emailRules"
                          prepend-icon="mdi-email" required></v-text-field>
                      </v-col>

                      <v-col cols="12" sm="6">
                        <v-text-field label="姓名*" counter="10" v-model="newEmployee.username" :rules="rules.nameRules"
                          prepend-icon="mdi-account" required></v-text-field>
                      </v-col>

                      <v-col cols="12" sm="6">
                        <v-text-field label="薪资*" type="number" v-model="newEmployee.salary" :rules="rules.noneEmptyRules"
                          prepend-icon="mdi-cash" required></v-text-field>
                      </v-col>

                      <v-col :cols="$store.state.isManager ? 6 : 12">
                        <v-select :items="['门店经理', '副经理', '小组长', '收银', '导购', '库房']" label="职位*"
                          prepend-icon="mdi-office-building-marker-outline" v-model="newEmployee.position"
                          :rules="rules.noneEmptyRules" required></v-select>
                      </v-col>

                      <v-col cols="12" sm="6">
                        <v-select :items="branches" item-text="name" item-value="id" label="所属分店*" no-data-text="没有数据"
                          v-if="$store.state.isManager" v-model="newEmployee.shop" :rules="rules.noneEmptyRules"
                          prepend-icon="mdi-store" required></v-select>
                      </v-col>

                      <v-col cols="12"><span class="text-subtitle-1">员工偏好</span></v-col>

                      <v-col cols="12" sm="6">
                        <v-select :items="week" item-text="key" item-value="value" small-chips clearable label="工作日偏好"
                          v-model="newEmployee.workingDay" no-data-text="没有数据" multiple required></v-select>
                      </v-col>

                      <v-col cols="12" sm="6">
                        <v-menu ref="menu" v-model="menu" :close-on-content-click="false" :nudge-right="40"
                          :return-value.sync="newEmployee.workingHours" transition="scale-transition" offset-y
                          :max-width="$vuetify.breakpoint.xsOnly ? '290px' : '580px'"
                          :min-width="$vuetify.breakpoint.xsOnly ? '290px' : '580px'">
                          <template v-slot:activator="{ on, attrs }">
                            <v-text-field v-model="newEmployee.workingHours" clearable label="工作时间偏好" readonly
                              v-bind="attrs" v-on="on"></v-text-field>
                          </template>

                          <v-card>
                            <v-time-picker v-model="startTime" :max="endTime" format="24hr"
                              :allowed-minutes="v => !(v % 30)" scrollable></v-time-picker>
                            <v-time-picker v-model="endTime" :min="startTime" format="24hr"
                              :allowed-minutes="v => !(v % 30)" scrollable></v-time-picker>
                          </v-card>
                        </v-menu>
                      </v-col>

                      <v-col cols="12" sm="6">
                        <v-text-field label="班次时长" v-model="newEmployee.durationOfShift" type="number"
                          required></v-text-field>
                      </v-col>

                      <v-col cols="12" sm="6">
                        <v-text-field label="周工作时间上限" v-model="newEmployee.durationOfWeek" type="number"
                          required></v-text-field>
                      </v-col>


                    </v-row>
                  </v-form>
                </v-container>
                <small>*为必填项</small>
              </v-card-text>
              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="primary" text @click="dialog1 = false; $refs.addEmployeeForm.reset()" large>
                  关闭
                </v-btn>
                <v-btn color="primary" @click="addEmployee()" large>
                  提交
                </v-btn>
              </v-card-actions>
            </v-card>
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
          <v-tab v-for="branch in branches" :key="branch.id" @click="getStaff(branch.id)">{{ branch.name }}</v-tab>
        </v-tabs>

      </template>

      <template v-slot:default="props">



        <div class="mx-10" v-if="ready">
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
                                    :rules="rules.noneEmptyRules" prepend-icon="mdi-cash" required></v-text-field>
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
                      <editFavor @close="item.dialog1 = false" :id="item.id"
                        @msg="getMsg">
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

        <v-row v-else>
          <v-col cols="12" v-for="index of 6" :key="index">
            <v-skeleton-loader class="mx-auto" type="table-heading,list-item-two-line,divider"></v-skeleton-loader>
          </v-col>
        </v-row>

      </template>

      <template v-slot:footer v-if="ready & staff.length !== 0">
        <v-pagination class="mt-4" v-model="page" :length="numberOfPages" color="secondary"></v-pagination>
      </template>

    </v-data-iterator>
  </div>
</template>

<script>
import { getAllShop, getShopInfo } from '../request/shop'
import { createEmployee, editEmployeeInfo, deleteEmployee, getEmployeeByShop, getEmployee } from '../request/staff'
import editFavor from '../components/editFavor.vue'
export default {
  components: {
    editFavor
  },
  data() {
    return {
      search: '',
      page: 1,
      branch: 0,
      dialog1: false,
      ready: false,
      sortBy: null,
      keys: {
        '工号': 'id',
        '姓名': 'username',
      },
      sortDesc: true,
      tabs: 0,
      staff: [{}],
      branches: [],
      week: [
        { value: "1", key: '星期一' },
        { value: "2", key: '星期二' },
        { value: "3", key: '星期三' },
        { value: "4", key: '星期四' },
        { value: "5", key: '星期五' },
        { value: "6", key: '星期六' },
        { value: "7", key: '星期天' },

      ],

      startTime: null,
      endTime: null,
      menu: false,

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

      newEmployee: {
        email: '',
        username: '',
        position: '',
        shop: '',
        salary: null,
        durationOfShift: null,
        durationOfWeek: null,
        workingDay: [],
        workingHours: []
      },

      editedEmployee: {
        position: '',
        shop: '',
        salary: null,
        durationOfShift: null,
        durationOfWeek: null,
        workingDay: [],
        workingHours: []
      },


    }
  },
  computed: {
    numberOfPages() {
      return Math.ceil(this.staff.length / 10)
    },
    fullscreen() {
      return this.$vuetify.breakpoint.xsOnly ? true : false
    },
  },
  methods: {
    nextPage() {
      if (this.page + 1 <= this.numberOfPages) this.page += 1
    },
    formerPage() {
      if (this.page - 1 >= 1) this.page -= 1
    },
    addEmployee() {
      if (this.$refs.addEmployeeForm.validate()) {
        createEmployee({
          email: this.newEmployee.email,
          username: this.newEmployee.username,
          position: this.newEmployee.position,
          shop: this.newEmployee.shop,
          salary: this.newEmployee.salary,
          durationOfShift: this.newEmployee.durationOfShift,
          durationOfWeek: this.newEmployee.durationOfWeek,
          workingDay: this.newEmployee.workingDay !== null ? this.newEmployee.workingDay.toString() : '',
          workingHours: this.newEmployee.workingHours !== null ? this.newEmployee.workingHours.toString() : ''
        }).then(res => {
          if (res.data.code === 0) {
            this.$emit('msg', '添加成功')
            this.$router.go(0)
          }
          if (res.data.code === -1) {
            this.$emit('msg', '邮箱重复')
          }
        }).catch(() => {
          this.$emit('msg', '网络错误')
        })
      }
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
    deleteEmployee(index, id) {
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
      getEmployeeByShop(shopId).then(res => {
        this.staff = res.data.data
      }).catch(() => {
        this.$emit('msg', '网络错误')
      })
    },

    getMsg(data) {
      this.$emit('msg', data)
    }
  },


  async mounted() {
    try {
      if (this.$store.state.isManager) {
        var shops = (await getAllShop()).data.data
        this.branches = shops
        if (this.branches.length !== 0) {
          this.getStaff(this.branches[this.branch].id)
        }
        else {
          this.ready = true,
            this.staff = []
          this.$emit('msg', '没有店铺信息')
        }
        this.ready = true
      }
      if (this.$store.state.isShopManager) {
        var employee = (await getEmployee()).data.data
        this.getStaff(employee.shop)
        var shop = (await getShopInfo(employee.shop)).data.data
        this.branches.push(shop)
        this.ready = true
      }
    } catch (err) {
      this.$emit('msg', '网络错误')
    }

  },

  watch: {
    menu: {
      immediate: true,
      handler(newV) {
        if (this.newEmployee.workingHours === null) this.newEmployee.workingHours = []
        if (newV === false) {
          if (this.startTime !== null && this.endTime !== null && this.startTime !== this.endTime)
            this.newEmployee.workingHours.push(this.startTime + '-' + this.endTime)
          this.startTime = this.endTime = null
        }
      }
    }
  }

}
</script>
