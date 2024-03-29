<template>
  <v-data-iterator v-if="notices.length !== 0 && ready" :items="filteredItems" :page.sync="page" :search="search"
    hide-default-footer no-results-text="没有搜索结果" no-data-text="没有数据">
    <template v-slot:header>
      <v-toolbar rounded :color="$vuetify.theme.dark === false ? 'white' : '#121212'" flat>
        <v-btn v-if="onlyUnread === false" class="mx-3" large depressed @click="checkUnread()">显示未读</v-btn>
        <v-btn v-else class="mx-3" large color="secondary" depressed @click="checkUnread()">全部显示</v-btn>

        <v-text-field v-model="search" clearable flat solo hide-details prepend-inner-icon="mdi-magnify"
          label="姓名/日期/时间"></v-text-field>


        <v-dialog v-model="dialog" width="330">
          <template v-slot:activator="{ on, attrs }">
            <v-btn class="mx-3" large color="secondary" outlined v-bind="attrs" v-on="on">一键已读</v-btn>

          </template>

          <v-card>
            <v-card-title class="text-h6">
              将所有通知设为已读？
            </v-card-title>

            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn color="primary" text @click="dialog = false">
                取消
              </v-btn>
              <v-btn color="error" text @click="setAllRead()">
                确定
              </v-btn>

            </v-card-actions>
          </v-card>
        </v-dialog>


      </v-toolbar>
      <v-toolbar :color="$vuetify.theme.dark === false ? 'white' : '#121212'" flat dense rounded>
        <v-chip class="ml-2" :class="filter === 1 ? 'white--text' : ''" :color="filter === 1 ? 'blue' : ''"
          @click="filter = filter === 1 ? 0 : 1">请假申请</v-chip>
        <v-chip class="ml-2" :class="filter === 2 ? 'white--text' : ''" :color="filter === 2 ? 'green' : ''"
          @click="filter = filter === 2 ? 0 : 2">排班信息</v-chip>
      </v-toolbar>


    </template>

    <template v-slot:default="props">
      <v-row>
        <v-col>
          <v-list :color="$vuetify.theme.dark === false ? 'white' : '#121212'" v-if="ready">
            <div v-for="notice of props.items" :key="notice.id">
              <v-divider></v-divider>
              <v-hover v-slot="{ hover }">
                <v-list-item @click="1">

                  <v-list-item-avatar>
                    <v-img size="70" :src="notice.avatar"></v-img>
                  </v-list-item-avatar>

                  <v-list-item-content @click="check(notice)">
                    <v-list-item-title :class="notice.isRead === false ? 'strong--text' : 'grey--text'">
                      {{ notice.fromUsername }}
                    </v-list-item-title>
                    <v-list-item-subtitle>
                      {{ messages[notice.type] }}
                    </v-list-item-subtitle>
                    <v-list-item-subtitle>
                      {{ notice.createAt }}
                    </v-list-item-subtitle>
                  </v-list-item-content>
                  <v-list-item-action>

                    <v-dialog v-model="notice.dialog" width="300">
                      <template v-slot:activator="{ on, attrs }">
                        <v-btn color="transparent" :class="{ 'show-btns': hover }" v-bind="attrs" v-on="on"
                          icon><v-icon>mdi-delete</v-icon></v-btn>
                      </template>

                      <v-card>
                        <v-card-title class="text-h6">
                          确定删除？
                        </v-card-title>

                        <v-card-actions>
                          <v-spacer></v-spacer>
                          <v-btn color="primary" text @click="notice.dialog = false">
                            取消
                          </v-btn>
                          <v-btn color="error" text @click="deleteNotice(notice.id); notice.dialog = false">
                            确定
                          </v-btn>

                        </v-card-actions>
                      </v-card>
                    </v-dialog>

                  </v-list-item-action>
                </v-list-item>
              </v-hover>

            </div>

          </v-list>
        </v-col>
        <v-col cols="4" v-if="selectedNotice">
          <v-card class="mr-2">
            <v-card-title class="text-h5">
              排班认领
            </v-card-title>
            <v-card-subtitle class="mt-2">当前班次安排存在劳动力空缺，点击可以认领排班：</v-card-subtitle>
            <v-card-text class="text-h6">
              班次时间：
              <p>{{ selectedNotice.fromTime }}</p>
              至 
              <p>{{ selectedNotice.toTime }}</p>
            </v-card-text>
            <v-card-actions>
              <v-row>
                <v-col cols="4">
                  <v-btn block text color="primary" @click="selectedNotice = null">返回</v-btn>
                </v-col>
                <v-col cols="8">
                  <v-btn block outlined color="primary" @click="getSchedule()">确认认领</v-btn>
                </v-col>
              </v-row>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>

    </template>

    <template v-slot:footer v-if="ready & notices.length !== 0">
      <v-pagination class="mt-4" v-model="page" :length="numberOfPages" color="secondary"
        :total-visible="7"></v-pagination>
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
        没有通知
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import { getNotis, setAllRead, deleteNoti, setRead } from '@/request/notis'
import { getUserAvatar } from '@/request/user'
import { formatDate, formatTime } from '@/plugins/utility'

export default {
  data() {
    return {
      dialog: false,
      ready: false,
      search: '',
      sortDesc: false,
      page: 1,
      itemsPerPage: 10,
      onlyUnread: false,

      notices: [{}],

      selectedNotice: null,

      filter: 0,

      types: {
        1: '排班',
        2: '请假',
      },

      messages: {
        1: '发布了一个新的排班表',
        2: '变更了排班',
        3: '有一个开放班次可以认领',
        4: '申请了请假',
        5: '请假审核状态更新'
      }

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
      let notices = this.notices;
      if (this.onlyUnread === true) {
        notices = notices.filter(item => { return item.isRead === false })
      }
      if (this.filter === 1) {
        notices = notices.filter(item => { return item.type === 4 || item.type === 5 })
      }
      if (this.filter === 2) {
        notices = notices.filter(item => { return item.type === 1 || item.type === 2 || item.type === 3 })
      }
      return notices
    }

  },
  methods: {
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
    },
    check(notice) {
      setRead(notice.id)
      if (notice.type >= 1 && notice.type < 3) {
        this.$router.push('/controlpanel/arrange')
      }
      else if (notice.type === 3) {
        let fromTime = notice.text.split(',')[1]
        let toTime = notice.text.split(',')[2]
        notice.fromTime = formatDate(fromTime) + ' ' + formatTime(fromTime)
        notice.toTime = formatDate(toTime) + ' ' + formatTime(toTime)

        notice.fromTime='2023-6-19 10:00'
        notice.toTime='2023-6-19 14:00'

        this.selectedNotice = notice

      }
      else if (notice.type === 4) {
        this.$router.push('/controlpanel/absences')
      }
    },
    getSchedule(){
      this.selectedNotice=null,
      this.$emit('msg', '操作成功')
    },
    setAllRead() {
      setAllRead().then(res => {
        if (res.data.code === 0) {
          for (const notice of this.notices) {
            notice.isRead = true
          }
          this.$emit('msg', '操作成功')
          this.dialog = false
        }
      }).catch(() => {
        this.$emit('msg', '网络错误')
      })
    },
    deleteNotice(id) {
      deleteNoti(id).then(res => {
        if (res.data.code === 0) {
          for (const index in this.notices) {
            if (this.notices[index].id === id) {
              this.notices.splice(index, 1)
            }
          }
          this.$emit('msg', '删除成功')
        }
      }).catch(() => {
        this.$emit('msg', '网络错误')
      })
    },
  },

  mounted() {
    getNotis().then(async res => {
      const notices = res.data.data;
      notices.forEach(notice => {
        notice.avatar = require('../assets/defaultAvatar.png')
        notice.createAt = formatDate(notice.createAt) + ' ' + formatTime(notice.createAt)
      })
      this.notices = notices
      if (notices.length === 0) this.notices = []
      this.ready = true
      for (const notice of this.notices) {
        const avatar = await getUserAvatar(notice.fromUser);
        if (avatar.status === 200) {
          notice.avatar = URL.createObjectURL(avatar.data)
        }
      }

    }).catch(() => {
      this.$emit('msg', '网络错误')
    })
  }
}
</script>

<style scoped>
.show-btns {
  color: rgb(216, 40, 40) !important;
}
</style>
