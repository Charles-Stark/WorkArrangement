<template>
  <div>

    <v-data-iterator :items="items" :search="search" :page.sync="page" hide-default-footer no-results-text="没有搜索结果"
      no-data-text="没有数据">
      <template v-slot:header>
        <v-toolbar class="mb-1" rounded :color="$vuetify.theme.dark === false ? 'white' : '#121212'">

          <v-dialog v-model="dialog1" persistent max-width="600"
            :fullscreen="$vuetify.breakpoint.xsOnly ? true : false">
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
                        <v-text-field label="薪资*" type="number" v-model="newEmployee.salary"
                          :rules="rules.noneEmptyRules" prepend-icon="mdi-cash" required></v-text-field>
                      </v-col>

                      <v-col cols="12" sm="6">
                        <v-select :items="['门店经理', '副经理', '小组长', '收银', '导购', '库房']" label="职位*"
                          prepend-icon="mdi-office-building-marker-outline" v-model="newEmployee.position"
                          :rules="rules.noneEmptyRules" required></v-select>
                      </v-col>

                      <v-col cols="12" sm="6">
                        <v-select :items="branches" item-text="name" item-value="id" label="所属分店*"
                          v-model="newEmployee.shop" :rules="rules.noneEmptyRules" prepend-icon="mdi-store"
                          required></v-select>
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
            <v-text-field v-model="search" clearable flat solo-inverted hide-details prepend-inner-icon="mdi-magnify"
              label="搜索"></v-text-field>
          </template>


        </v-toolbar>

        <v-tabs background-color="transparent" v-model="branch">
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
                    :icon="$vuetify.breakpoint.mdAndDown" :value="item.approved"
                    @click="editedEmployee.salary = item.salary; editedEmployee.position = item.position; editedEmployee.shop = item.shop">
                    <v-icon>mdi-pencil</v-icon>
                    <span v-if="!$vuetify.breakpoint.mdAndDown">编辑</span>
                  </v-btn>
                </template>

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

                          <v-col cols="12" sm="6">
                            <v-select :items="['门店经理', '副经理', '小组长', '收银', '导购', '库房']" label="职位*"
                              prepend-icon="mdi-office-building-marker-outline" v-model="editedEmployee.position"
                              :rules="rules.noneEmptyRules" required></v-select>
                          </v-col>

                          <v-col cols="12" sm="6">
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
                    <v-btn color="success" text @click="deleteEmployee(index, item.id)" large>
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

      <template v-slot:footer v-if="ready">
        <v-pagination class="mt-4" v-model="page" :length="numberOfPages" color="secondary"></v-pagination>
      </template>

    </v-data-iterator>
  </div>


</template>

<script>
import { getAllShop } from '../../request/shop'
import { getEmployee, createEmployee, editEmployeeInfo, deleteEmployee } from '../../request/staff'
export default {
  data() {
    return {
      search: '',
      page: 1,
      branch: 0,
      dialog1: false,
      ready: false,

      items: [{}],
      branches: [],
      week: [
        { value: 1, key: '星期一' },
        { value: 2, key: '星期二' },
        { value: 3, key: '星期三' },
        { value: 4, key: '星期四' },
        { value: 5, key: '星期五' },
        { value: 6, key: '星期六' },
        { value: 7, key: '星期天' },

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
      },

      editedEmployee: {
        position: '',
        shop: '',
        salary: null,
      }

    }
  },
  computed: {
    numberOfPages() {
      return Math.ceil(this.items.length / 10)
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
        }).then(res => {
          if (res.data.code === 0) {
            this.$emit('msg', '添加成功')
            this.$router.go(0)
          }
          if(res.data.code===-1){
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
            this.$emit('msg', '修改成功')
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
          this.items.splice(index, 1)
        }
      }).catch(() => {
        this.$emit('msg', '网络错误')
      })
    },
    getStaff(shopId) {
      getEmployee(shopId).then(res => {
        this.items = res.data.data
        this.ready = true
      }).catch(() => {
        this.$emit('msg', '网络错误')
      })
    }
  },


  mounted() {
    getAllShop().then(res => {
      if (res.data.code === 0) {
        this.branches = res.data.data
        this.getStaff(this.branches[this.branch].id)
      }
    }).catch(() => {
      this.$emit('msg', '网络错误')
    })
  }
}
</script>
