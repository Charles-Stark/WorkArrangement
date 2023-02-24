<template>
  <v-data-iterator :items="filteredItems" :page.sync="page" :search="search" :sort-by="keys[sortBy]"
    :sort-desc="sortDesc" hide-default-footer no-results-text="没有搜索结果" no-data-text="没有数据">
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
            <v-btn large depressed color="secondary" :value="false"  :disabled="!sortBy">
              <v-icon>mdi-arrow-up</v-icon>
            </v-btn>
            <v-btn large depressed color="secondary" :value="true"  :disabled="!sortBy">
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
              {{ item.time }}
            </v-list-item-content>
          </v-col>
          <v-col cols="3" md="2">
            <v-list-item-content>
              {{ item.id }}
            </v-list-item-content>
          </v-col>
          <v-col cols="3" md="2">
            <v-list-item-content>
              {{ item.name }}
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

                <v-btn outlined color="secondary" :disabled="item.approved != null" v-bind="attrs" v-on="on"
                  :value="item.approved" class="mt-1">
                  <v-icon  v-text="item.approved ? 'mdi - check' :'mdi-close'"></v-icon>
                  <span v-if="item.approved === null">查看详情</span>
                  <span v-else-if="item.approved === true">已批准</span>
                  <span v-else>已拒绝</span>
                </v-btn>

              </template>
              <v-card>
                <v-card-title class="text-h5">
                  请假条
                </v-card-title>
                <v-card-text class="text-h6 mt-4">
                  工号: {{ item.id }}
                </v-card-text>
                <v-card-text class="text-h6">
                  姓名: {{ item.name }}
                </v-card-text>
                <v-card-text class="text-h6">
                  请假时间: {{ item.time }}
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
                  <v-btn color="error" text @click="approve()" large>
                    拒绝
                  </v-btn>
                  <v-btn color="success" text @click="reject()" large>
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

</template>

<script>
export default {
  data() {
    return {
      search: '',
      sortDesc: false,
      page: 1,
      itemsPerPage: 10,
      sortBy: null,
      keys: {
        '工号': 'id',
        '姓名': 'name'
      },
      onlyUnread: false,

      headers: [
        { text: '请假时间', value: 'time', },
        { text: '工号', value: 'id' },
        { text: '姓名', value: 'name' },
        { text: '请假原因', value: 'reason' },
        { text: '审批状态', value: 'approved' },
      ],

      items: [
        {
            time: '2022/12/31',
            id: 'A0001',
            name: 'BBB',
            reason: 'safadsgasgfadsfadgadfadsfasfjfadsjfhuiashfuiasuiavocnojquiobiasfas',
            approved: true,
            attachment: ''
        },
        {
            time: '2020/9/20',
            id: 'A0002',
            name: 'AA',
            reason: 2,
            approved: false,
            attachment: ''
        },
        {
            time: '2021/4/5',
            id: 'A0003',
            name: 'CC',
            reason: 'safadsgasgfadsfadgadfadsfasfjfadsjfhuiashfjfhuiashfuiasuiavocsfadgadfadsfasfjfadsjfhuiashfuiasuiavocnojquiobiasfas',
            approved: null,
            attachment: ''
        },
        {
            time: '2002/4/4',
            id: 'A0004',
            name: 'EE',
            reason: 4,
            approved: false,
            attachment: ''
        },
        {
            time: '2022/4/3',
            id: 'A0005',
            name: 'DD',
            reason: 5,
            approved: null,
            attachment: ''
        },
        
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
        return this.items.filter(item => { return item.approved === null })
      }
      return this.items
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
