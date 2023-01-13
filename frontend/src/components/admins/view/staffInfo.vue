<template>
  <div>

    <v-data-iterator :items="filteredItems" :search="search" :page.sync="page" hide-default-footer
      no-results-text="没有搜索结果" no-data-text="没有数据">
      <template v-slot:header>
        <v-toolbar class="mb-1" rounded :color="$vuetify.theme.dark === false ? 'white' : '#121212'">

          <v-btn large color="secondary" class="mr-5" outlined>
            <v-icon>mdi-plus</v-icon>
            新增员工
          </v-btn>

          <template>
            <v-text-field v-model="search" clearable flat solo-inverted hide-details prepend-inner-icon="mdi-magnify"
              label="搜索"></v-text-field>
          </template>
        </v-toolbar>
      </template>

      <template v-slot:default="props">

        <v-tabs background-color="transparent" v-model="branch">
          <v-tab v-for="item in items" :key="item.id">{{ item.name }}</v-tab>
        </v-tabs>

        <div class="mx-10">
          <v-row class="mt-3">
            <v-col cols="3">工号</v-col>
            <v-col cols="3">姓名</v-col>
            <v-col cols="3">职位</v-col>
            <v-col cols="3">编辑</v-col>
            <v-col cols="12"><v-divider></v-divider></v-col>
          </v-row>


          <v-row v-for="item in props.items" :key="item.id">
            <v-col cols="3">
              <v-list-item-content>
                {{ item.id }}
              </v-list-item-content>
            </v-col>
            <v-col cols="3">
              <v-list-item-content>
                {{ item.name }}
              </v-list-item-content>
            </v-col>
            <v-col cols="3">
              <v-list-item-content>
                {{ item.position }}
              </v-list-item-content>
            </v-col>
            <v-spacer></v-spacer>
            <v-col cols="3">

              <v-dialog max-width="470" :fullscreen="fullscreen" v-model="item.dialog">
                <template v-slot:activator="{ on, attrs }">
                  <v-btn outlined color="warning lighten-1" v-bind="attrs" v-on="on" class="mt-1"
                    :value="item.approved">
                    <v-icon>mdi-pencil</v-icon>
                    修改
                  </v-btn>

                </template>

                <v-card>
                  <v-card-title class="text-h4">
                    修改员工信息
                  </v-card-title>

                  <v-card-actions class="mt-4">
                    <v-spacer></v-spacer>
                    <v-btn color="grey" text @click="close(item)" large>
                      返回
                    </v-btn>
                    <v-btn color="success" text @click="submit()" large>
                      确定
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
  </div>


</template>

<script>
export default {
  data() {
    return {
      search: '',
      page: 1,
      branch: 0,
      items: [
        {
            id: '49794',
            name: '分店1',
            staff: [
              {
                  id: 'A1241',
                  name: 'AA',
                  position: '门店经理',
                  preference: {
                    workingDay: [],
                    workingHour: [],
                    duration: 2,
                  
                }
              }, {
                  id: 'A1947',
                  name: 'BB',
                  position: '副经理',
                  preference: {
                    workingDay: [],
                    workingHour: [],
                    duration: 3,
                  }
                
              }, {
                  id: 'A4941',
                  name: 'CC',
                  position: '小组长',
                  preference: {
                    workingDay: [],
                    workingHour: [],
                    duration: 1,
                  }
                
              }, {
                  id: 'A0146',
                  name: 'DD',
                  position: '店员',
                  preference: {
                    workingDay: [],
                    workingHour: [],
                    duration: 2,
                  }
                
              }, {
                  id: 'A3466',
                  name: 'EE',
                  position: '店员',
                  preference: {
                    workingDay: [],
                    workingHour: [],
                    duration: 4,
                  }
                
              }
            ]
          
        },
        {
            id: '97946',
            name: '分店2',
            staff: [
              {
                  id: 'B0349',
                  name: 'FF',
                  position: '门店经理',
                  preference: {
                    workingDay: [],
                    workingHour: [],
                    duration: 1,
                  }
                
              }, {
                  id: 'B0166',
                  name: 'GG',
                  position: '副经理',
                  preference: {
                    workingDay: [],
                    workingHour: [],
                    duration: 2,
                  }
                
              }, {
                  id: 'B4710',
                  name: 'HH',
                  position: '小组长',
                  preference: {
                    workingDay: [],
                    workingHour: [],
                    duration: 1,
                  }
                
              }, {
                  id: 'B3444',
                  name: 'II',
                  position: '店员',
                  preference: {
                    workingDay: [],
                    workingHour: [],
                    duration: 2,
                  }
                
              }, {
                  id: 'B5975',
                  name: 'JJ',
                  position: '店员',
                  preference: {
                    workingDay: [],
                    workingHour: [],
                    duration: 4,
                  }
                
              }
            ]
          
        },
        {
            id: '01346',
            name: '分店3',
            staff: [
              {
                  id: 'C9974',
                  name: 'KK',
                  position: '门店经理',
                  preference: {
                    workingDay: [],
                    workingHour: [],
                    duration: 3,
                  }
                
              }, {
                  id: 'C0133',
                  name: 'LL',
                  position: '副经理',
                  preference: {
                    workingDay: [],
                    workingHour: [],
                    duration: 1,
                  }
                
              }, {
                  id: 'C4410',
                  name: 'MM',
                  position: '小组长',
                  preference: {
                    workingDay: [],
                    workingHour: [],
                    duration: 2,
                  }
                
              }, {
                  id: 'C0012',
                  name: 'NN',
                  position: '店员',
                  preference: {
                    workingDay: [],
                    workingHour: [],
                    duration: 1,
                  }
                
              }, {
                  id: 'C0164',
                  name: 'OO',
                  position: '店员',
                  preference: {
                    workingDay: [],
                    workingHour: [],
                    duration: 3,
                  }
                
              }
            ]
          
        }
      ],
    }
  },
  computed: {
    numberOfPages() {
      return Math.ceil(this.items.length / 10)
    },
    filteredItems() {
      return this.items[this.branch].staff;
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
    close(item) {
      item.dialog = false
    },
  },
}
</script>
