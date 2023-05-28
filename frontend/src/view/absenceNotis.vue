<template>
  <v-data-iterator v-if="absenceList.length !== 0 && ready" :items="filteredItems" :page.sync="page" :search="search" :sort-by="keys[sortBy]" :sort-desc="sortDesc"
    hide-default-footer no-results-text="没有搜索结果" no-data-text="没有数据">
    <template v-slot:header>
      <v-toolbar class="mb-1" rounded :color="$vuetify.theme.dark === false ? 'white' : '#121212'" flat>
        <v-text-field v-model="search" clearable flat solo-inverted hide-details prepend-inner-icon="mdi-magnify"
          label="搜索"></v-text-field>
        <template v-if="$vuetify.breakpoint.mdAndUp">
          <v-spacer></v-spacer>
          <v-select v-model="sortBy" clearable flat solo-inverted hide-details :items="Object.keys(keys)"
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
              {{ item.employeeId }}
            </v-list-item-content>
          </v-col>
          <v-col cols="3" md="2">
            <v-list-item-content>
              小明
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
                  工号: {{ item.employeeId }}
                </v-card-text>
                <v-card-text class="text-h6">
                  姓名: 小明
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
                <v-img class="mx-15" :src="item.employeeId"></v-img>
                <v-card-actions class="mt-4">
                  <v-spacer></v-spacer>
                  <v-btn color="grey" text @click="close(item)" large>
                    返回
                  </v-btn>
                  <v-btn color="error" text @click="reject(item)" large>
                    拒绝
                  </v-btn>
                  <v-btn color="success" text @click="approve(item)" large>
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
    </v-row>
  </v-container>
</template>

<script>
import { getAbsenceList } from '@/request/absence'
import { formatDate } from '@/plugins/utility'

export default {
  data() {
    return {
      search: '',
      sortDesc: false,
      page: 1,
      ready:false,
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

    approve(item) {
      item.isApproved = true
      item.dialog = false
      this.$emit('msg', '处理成功')

    },
    reject(item) {
      item.isApproved = false
      item.dialog = false
      this.$emit('msg', '处理成功')

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
    let respond= (await getAbsenceList()).data
    if(respond.code===0){
      let absenceList=respond.data
      absenceList.forEach(abs => {
        abs.absenceDate=formatDate(abs.absenceDate)
      });
      this.absenceList=absenceList
      console.log(respond.data)
    }else if(respond.code===-1){
      this.$emit('msg', '获取请假列表失败')
    }
    this.ready=true
  }
}
</script>
