<template>
  <v-data-iterator :items="filteredItems" :page.sync="page" :search="search" hide-default-footer no-results-text="没有搜索结果"
    no-data-text="没有数据">
    <template v-slot:header>
      <v-toolbar rounded :color="$vuetify.theme.dark === false ? 'white' : '#121212'" flat>
        <v-btn v-if="onlyUnread === false" class="mx-3" large depressed @click="checkUnread()">显示未读</v-btn>
        <v-btn v-else class="mx-3" large color="secondary" depressed @click="checkUnread()">全部显示</v-btn>

        <v-text-field v-model="search" clearable flat solo-inverted hide-details prepend-inner-icon="mdi-magnify"
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
        <v-chip class="ml-2">请假申请</v-chip>
        <v-chip class="ml-2">排班信息</v-chip>
      </v-toolbar>


    </template>

    <template v-slot:default="props">


      <v-list :color="$vuetify.theme.dark === false ? 'white' : '#121212'" v-if="ready">
        <div v-for="notice of props.items" :key="notice.id">
          <v-divider></v-divider>
          <v-hover v-slot="{ hover }">
            <v-list-item @click="1">

              <v-list-item-avatar>
                <v-img size="70" :src="notice.avatar"></v-img>
              </v-list-item-avatar>

              <v-list-item-content @click="check()">
                <v-list-item-title :class="notice.isRead === false ? 'strong--text' : 'grey--text'">
                  {{ messages[notice.type] }}
                </v-list-item-title>
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
                      <v-btn color="error" text @click="deleteNoti(notice.id);notice.dialog = false">
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

      <v-row v-else>
        <v-col cols="12" v-for="index of 6" :key="index">
          <v-skeleton-loader class="mx-auto" type="table-heading,list-item-two-line,divider"></v-skeleton-loader>
        </v-col>
      </v-row>


    </template>

    <template v-slot:footer v-if="ready & notices.length !== 0">
      <v-pagination class="mt-4" v-model="page" :length="numberOfPages" color="secondary"></v-pagination>
    </template>

  </v-data-iterator>
</template>

<script>
import { getNotis, setAllRead, deleteNoti } from '../../request/notis'
import { getUserAvatar } from '../../request/user'
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

      types: {
        1: '排班',
        2: '请假',
      },

      messages: {
        1: '你有一个新的排班，点击查看',
        2: '有一个排班表发生了变更',
        3: '有一个开放班次长时间无人认领，点击进行手动排班'
      }

    }
  },
  computed: {
    numberOfPages() {
      return Math.ceil(this.filteredItems.length / 10)
    },
    fullscreen() {
      return this.$vuetify.breakpoint.xsOnly ? true : false
    },
    filteredItems() {
      if (this.onlyUnread === true) {
        return this.notices.filter(item => { return item.isRead === false })
      }
      return this.notices
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
    check() {
      console.log(123)
    },
    setAllRead() {
      setAllRead().then(res => {
        if (res.data.code === 0) {
          for (var notice of this.notices) {
            notice.isRead = true
          }
        this.$emit('msg', '操作成功')
        }
      }).catch(() => {
        this.$emit('msg', '网络错误')
      })
    },
    deleteNoti(id) {
      deleteNoti(id).then(res => {
        if (res.data.code === 0) {
          for(var index in this.notices){
            if(this.notices[index].id===id){
              this.notices.splice(index,1) 
            }
          }
          this.$emit('msg', '删除成功')
        }
      }).catch(() => {
        this.$emit('msg', '网络错误')
      })
    }

  },

  mounted() {
    getNotis().then(async res => {
      var notices = res.data.data
      for (var notice of notices) {
        notice.avatar = await getUserAvatar(notice.fromUser).data || require('../../assets/defaultAvatar.png')
      }
      this.notices = notices
      if (this.notices.length === 0) this.notices = []
      this.ready = true

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
