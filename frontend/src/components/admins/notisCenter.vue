<template>
  <v-data-iterator :items="filteredItems" :page.sync="page" :search="search" hide-default-footer no-results-text="没有搜索结果"
    no-data-text="没有数据">
    <template v-slot:header>
      <v-toolbar class="mb-1" rounded :color="$vuetify.theme.dark === false ? 'white' : '#121212'" flat>
        <v-btn v-if="onlyUnread === false" class="mx-3" large depressed @click="checkUnread()">显示未读</v-btn>
        <v-btn v-else class="mx-3" large color="secondary" depressed @click="checkUnread()">全部显示</v-btn>
        <v-text-field v-model="search" clearable flat solo-inverted hide-details prepend-inner-icon="mdi-magnify"
          label="搜索"></v-text-field>
      </v-toolbar>


    </template>

    <template v-slot:default="props">
      <template v-for="(notice, index) in props.items">
        <v-divider :key="index + ' ' + notice.type" class="mx-5"></v-divider>

        <v-list-item :key="index" @click="jumpToNoti()">
          <v-list-item-avatar v-if="notice.type === 1">
            <v-img :src="notice.avatar"></v-img>
          </v-list-item-avatar>
          <v-list-item-content>
            <v-list-item-subtitle class="mt-1 mx-2 text-subtitle-1"
              :class="notice.read === false ? 'strong--text' : 'grey--text'" v-if="notice.type === 1">{{
                notice.name
              }}申请了请假，点击查看详情</v-list-item-subtitle>
            <v-list-item-subtitle class="mt-1 mx-2 text-subtitle-1"
              :class="notice.read === false ? 'strong--text' : 'grey--text'"
              v-else>有一个开放班次长时间无人认领，点击进行手动排班</v-list-item-subtitle>
            <v-list-item-subtitle class="text-caption mt-3">{{ notice.date }} {{
              notice.time
            }}</v-list-item-subtitle>
          </v-list-item-content>

        </v-list-item>
      </template>
    </template>
  </v-data-iterator>
</template>

<script>
export default {
  data() {
    return {
      search: '',
      sortDesc: false,
      page: 1,
      itemsPerPage: 10,
      onlyUnread: false,

      notices: [
        { type: 1, avatar: 'https://cdn.vuetifyjs.com/images/lists/1.jpg', name: 'aa', time: '10:24', date: '2022/10/24', read: true },
        { type: 2, time: '10:24', date: '2022/10/24', read: false },
        { type: 1, avatar: 'https://cdn.vuetifyjs.com/images/lists/2.jpg', name: 'bb', time: '10:24', date: '2022/10/24', read: false },
        { type: 2, time: '10:24', date: '2022/10/24', read: true },
        { type: 2, time: '10:24', date: '2022/10/24', read: false },
        { type: 2, time: '10:24', date: '2022/10/24', read: true },
        { type: 2, time: '10:24', date: '2022/10/24', read: false },
        { type: 2, time: '10:24', date: '2022/10/24', read: false },
        { type: 2, time: '10:24', date: '2022/10/24', read: true },
      ],

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
        return this.notices.filter(item => { return item.read === false })
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
    }

  },
}
</script>
