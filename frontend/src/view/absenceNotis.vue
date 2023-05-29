<template>
  <v-data-iterator v-if="absenceList.length !== 0 && ready" :items="filteredItems" :page.sync="page" :search="search"
    :sort-by="keys[sortBy]" :sort-desc="sortDesc" hide-default-footer no-results-text="没有搜索结果" no-data-text="没有数据">
    <template v-slot:header>
      <v-toolbar class="mb-1" rounded :color="$vuetify.theme.dark === false ? 'white' : '#121212'" flat>
        <v-select v-model="branch" :items="branches" item-text="name" item-value="id" solo interval-minutes="60"
          no-data-text="没有数据" flat hide-details style="max-width:140px;min-width:120px" @change="changeBranch()"
          v-if="$store.state.isManager" class="mr-2"></v-select>
        <span v-if="!$store.state.isManager" class="text-h6 ml-3">{{ shopName }}</span>
        <v-text-field v-model="search" clearable flat solo hide-details prepend-inner-icon="mdi-magnify" class="mr-2"
          label="搜索"></v-text-field>
        <template v-if="$vuetify.breakpoint.mdAndUp">
          <v-spacer></v-spacer>
          <v-select v-model="sortBy" clearable flat solo hide-details :items="Object.keys(keys)"
            prepend-inner-icon="mdi-magnify" label="排序"></v-select>
          <v-spacer></v-spacer>
        </template>
        <v-btn v-if="onlyUnread === false" class="mx-3" large depressed @click="checkUnread()">显示未读</v-btn>
        <v-btn v-else class="mx-3" large color="secondary" depressed @click="checkUnread()">全部显示</v-btn>
        <template v-if="$vuetify.breakpoint.mdAndUp">
          <v-btn-toggle v-model="sortDesc" mandatory>
            <v-btn large depressed color="secondary" :value="false" :disabled="!sortBy">
              <v-icon>mdi-arrow-up</v-icon>
            </v-btn>
            <v-btn large depressed color="secondary" :value="true" :disabled="!sortBy">
              <v-icon>mdi-arrow-down</v-icon>
            </v-btn>
          </v-btn-toggle>
        </template>
      </v-toolbar>
    </template>

    <template v-slot:default="props">
      <div class="mx-10">
        <v-row class="mt-5">
          <v-col cols="3" md="2">请假时间</v-col>
          <v-col cols="3" md="2">工号</v-col>
          <v-col cols="3" md="2">姓名</v-col>
          <v-col class="hidden-sm-and-down">请假原因</v-col>
          <v-spacer></v-spacer>
          <v-col cols="3" md="2">审批状态</v-col>
          <v-col cols="12"><v-divider></v-divider></v-col>
        </v-row>

        <v-row v-for="item in props.items" :key="item.id">
          <v-col cols="3" md="2">
            <v-list-item-content>
              {{ item.absenceDate }}
            </v-list-item-content>
          </v-col>
          <v-col cols="3" md="2">
            <v-list-item-content>
              {{ item.employeeVO.uid }}
            </v-list-item-content>
          </v-col>
          <v-col cols="3" md="2">
            <v-list-item-content>
              {{ item.employeeVO.username }}
            </v-list-item-content>
          </v-col>
          <v-col cols="4" class="hidden-sm-and-down">
            <v-list-item-content>
              {{ item.reason }}
            </v-list-item-content>
          </v-col>
          <v-spacer></v-spacer>
          <v-col cols="3" md="2">

            <v-dialog max-width="470" :fullscreen="fullscreen" v-model="item.dialog">
              <template v-slot:activator="{ on, attrs }">

                <v-btn outlined color="secondary" :disabled="item.isApproved != null" v-bind="attrs" v-on="on"
                  :value="item.isApproved" class="mt-1">
                  <v-icon
                    v-text="item.isApproved === true ? 'mdi-check' : item.isApproved === false ? 'mdi-close' : ''"></v-icon>
                  <span v-if="item.isApproved === null">查看详情</span>
                  <span v-else-if="item.isApproved === true">已批准</span>
                  <span v-else>已拒绝</span>
                </v-btn>

              </template>
              <v-card>
                <v-card-title class="text-h5">
                  请假条
                </v-card-title>
                <v-card-text class="text-h6 mt-4">
                  工号: {{ item.employeeVO.uid }}
                </v-card-text>
                <v-card-text class="text-h6">
                  姓名: {{ item.employeeVO.username }}
                </v-card-text>
                <v-card-text class="text-h6">
                  请假时间: {{ item.absenceDate }}
                </v-card-text>
                <v-card-text class="text-h6">
                  请假原因: <br>
                  <p class="text-body-1 mx-3 mt-3">{{ item.reason }}</p>

                </v-card-text>
                <v-card-text class="text-h6">
                  相关附件:
                </v-card-text>
                <v-img class="mx-15" :src="item.attachment"></v-img>
                <v-card-actions class="mt-4">
                  <v-spacer></v-spacer>
                  <v-btn color="grey" text @click="close(item)" large>
                    返回
                  </v-btn>
                  <v-btn color="error" text @click="approve(item, false)" large>
                    拒绝
                  </v-btn>
                  <v-btn color="success" text @click="approve(item, true)" large>
                    批准
                  </v-btn>
                </v-card-actions>
              </v-card>
            </v-dialog>
          </v-col>
          <v-col cols="12"><v-divider></v-divider></v-col>

        </v-row>
      </div>
    </template>

    <template v-slot:footer>
      <v-pagination class="mt-4" v-model="page" :length="numberOfPages" color="secondary"></v-pagination>
    </template>
  </v-data-iterator>

  <v-container v-else-if="!ready">
    <v-row>
      <v-col cols="12" v-for="index of 6" :key="index">
        <v-skeleton-loader class="mx-auto" type="table-heading,list-item-two-line,divider"></v-skeleton-loader>
      </v-col>
    </v-row>
  </v-container>
  <v-container v-else style="height: 80vh;">
    <v-row class="fill-height" align-content="center" justify="center">
      <v-col class="text-h5 text-center" cols="12">
        没有请假信息
      </v-col>
      <v-col class="text-h5 text-center" cols="12">
        <v-select v-model="branch" :items="branches" item-text="name" item-value="id" solo interval-minutes="60"
          no-data-text="没有数据" flat hide-details style="max-width:140px;min-width:120px" @change="changeBranch()"
          v-if="$store.state.isManager" class="mx-auto"></v-select>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import { getAbsenceByShop, approveAbsence, getAttachment } from '@/request/absence'
import { getAllShop, getShopInfo } from '@/request/shop'
import { formatDate } from '@/plugins/utility'
import { getEmployee } from '@/request/staff'


export default {
  data() {
    return {
      branches: [],
      branch: '',
      shopName: '',
      search: '',
      sortDesc: false,
      page: 1,
      ready: false,
      itemsPerPage: 10,
      sortBy: null,
      keys: {
        '工号': 'id',
        '姓名': 'name'
      },
      onlyUnread: false,

      absenceList: [],
    }
  },
  computed: {
    numberOfPages() {
      return Math.ceil(this.filteredItems.length / 10)
    },
    fullscreen() {
      return this.$vuetify.breakpoint.xsOnly
    },
    filteredItems() {
      if (this.onlyUnread === true) {
        return this.absenceList.filter(item => { return item.isApproved === null })
      }
      return this.absenceList
    }

  },
  methods: {
    async approve(item, handle) {
      let response = (await approveAbsence({
        id: item.id,
        approve: handle
      })).data
      if (response.code === 0) {
        this.$emit('msg', '处理成功')
        item.isApproved = handle
        item.dialog = false
      }
    },
    async changeBranch() {
      let response = (await getAbsenceByShop(this.branch)).data.data
      if (response) {
        console.log(response)
        for (let a of response) {
          a.absenceDate = formatDate(a.absenceDate)
          a.attachment = ''
        }
        this.absenceList = response
      }
      this.ready = true

      for (let a of response) {
        let attachment = (await getAttachment(a.id)).data
        a.attachment = URL.createObjectURL(attachment)
      }
    },
    nextPage() {
      if (this.page + 1 <= this.numberOfPages) this.page += 1
    },
    formerPage() {
      if (this.page - 1 >= 1) this.page -= 1
    },
    close(item) {
      item.dialog = false
    },
    checkUnread() {
      this.onlyUnread = !this.onlyUnread
    }
  },

  async mounted() {
    if (this.$store.state.isManager) {
      let branches = (await getAllShop()).data.data
      if (branches.length !== 0) {
        this.branch = branches[0].id
      } else {
        branches = []
      }
      this.branches = branches
    }
    else if (this.$store.state.isShopManager) {
      let employee = (await getEmployee()).data.data
      let shop = (await getShopInfo(employee.shop)).data.data
      this.branch = shop.id
      this.shopName = shop.name
    }
    await this.changeBranch()
  }
}
</script>
