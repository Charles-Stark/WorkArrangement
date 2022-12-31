<template>
  <v-data-iterator :items="items" :items-per-page.sync="itemsPerPage" :page.sync="page" :search="search"
    :sort-by="sortBy.toLowerCase()" :sort-desc="sortDesc" hide-default-footer>
    <template v-slot:header>
      <v-toolbar class="mb-1" elevation="2" rounded>
        <v-text-field v-model="search" clearable flat solo-inverted hide-details prepend-inner-icon="mdi-magnify"
          label="Search"></v-text-field>
        <template v-if="$vuetify.breakpoint.mdAndUp">
          <v-spacer></v-spacer>
          <v-select v-model="sortBy" flat solo-inverted hide-details :items="keys" prepend-inner-icon="mdi-magnify"
            label="Sort by"></v-select>
          <v-spacer></v-spacer>
          <v-btn-toggle v-model="sortDesc" mandatory>
            <v-btn large depressed color="secondary" :value="false">
              <v-icon>mdi-arrow-up</v-icon>
            </v-btn>
            <v-btn large depressed color="secondary" :value="true">
              <v-icon>mdi-arrow-down</v-icon>
            </v-btn>
          </v-btn-toggle>
        </template>
      </v-toolbar>
    </template>

    <template v-slot:default="props">
      <div class="ma-5">
        <v-row class="mt-5 hidden-sm-and-down">
          <v-col cols="2" class="ml-4">请假时间</v-col>
          <v-col cols="2">工号</v-col>
          <v-col cols="2">姓名</v-col>
          <v-col cols="4" class="hidden-sm-and-down">请假原因</v-col>
          <v-col>审批状态</v-col>
        </v-row>
        <v-row v-for="item in props.items" :key="item.id">
          <v-col cols="3" md="2">
            <v-list-item>
              <v-list-item-content :class="{ 'secondary': sortBy === item.time }">
                {{ item.time }}
              </v-list-item-content>
            </v-list-item>
          </v-col>
          <v-col cols="3" md="2">
            <v-list-item>
              <v-list-item-content :class="{ 'secondary': sortBy === item.id }">
                {{ item.id }}
              </v-list-item-content>
            </v-list-item>
          </v-col>
          <v-col cols="3" md="2">
            <v-list-item>
              <v-list-item-content :class="{ 'secondary': sortBy === item.name }">
                {{ item.name }}
              </v-list-item-content>
            </v-list-item>
          </v-col>
          <v-col cols="2" md="4" class="hidden-sm-and-down">
            <v-list-item>
              <v-list-item-content class="align-end" :class="{ 'secondary': sortBy === item.reason }">
                {{ item.reason }}
              </v-list-item-content>
            </v-list-item>
          </v-col>
          <v-spacer></v-spacer>
          <v-col cols="3" md="2" right class="align-end">
            <v-btn outlined color="secondary" :disabled="item.approved != null">
              <v-icon v-if="item.approved === true">mdi-check</v-icon>
              <v-icon v-else-if="item.approved === false">mdi-close</v-icon>
              <span v-if="item.approved === null">查看详情</span>
              <span v-else-if="item.approved === true">已批准</span>
              <span v-else>已拒绝</span>

            </v-btn>
          </v-col>
        </v-row>
      </div>
    </template>

    <template v-slot:footer>
      <v-pagination v-model="page" :length="numberOfPages"></v-pagination>
    </template>
  </v-data-iterator>

</template>

<script>
export default {
  data() {
    return {
      search: '',
      filter: {},
      sortDesc: true,
      page: 1,
      itemsPerPage: 10,
      sortBy: 'time',
      keys: [
        'time',
        'id',
        'name',
        'reason',
      ],
      items: [
        {
          time: '2022/12/31',
          id: 'A0001',
          name: 'AA',
          reason: 'safadsgasgfadsfadgadfadsfasfjfadsjfhuiashfuiasuiavocnojquiobiasfas',
          approved: true
        },
        {
          time: '2020/9/20',
          id: 'A0002',
          name: 'BBB',
          reason: 2,
          approved: false
        },
        {
          time: '2021/4/5',
          id: 'A0003C',
          name: 'CC',
          reason: 3,
          approved: null
        },
        {
          time: '2002/4/4',
          id: 'A0004',
          name: 'DD',
          reason: 4,
          approved: false
        },
        {
          time: '2022/4/3',
          id: 'A0005',
          name: 'EE',
          reason: 5,
          approved: null
        },

      ],
    }
  },
  computed: {
    numberOfPages() {
      return Math.ceil(this.items.length / this.itemsPerPage)
    },
  },
  methods: {
    nextPage() {
      if (this.page + 1 <= this.numberOfPages) this.page += 1
    },
    formerPage() {
      if (this.page - 1 >= 1) this.page -= 1
    },
    updateItemsPerPage(number) {
      this.itemsPerPage = number
    },
  },
}
</script>