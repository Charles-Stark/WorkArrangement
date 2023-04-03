<template>
  <div>

    <v-toolbar :color="$vuetify.theme.dark === false ? 'white' : '#121212'" flat
      v-if="$store.state.isManager || $store.state.isShopManager">
      <v-select v-model="branch" :items="branches" item-text="name" item-value="id" solo-inverted interval-minutes="60"
        no-data-text="没有数据" dense flat hide-details style="max-width:140px;min-width:120px" @change="getStaff(); getArr()"
        v-if="$store.state.isManager"></v-select>
      <span v-if="$store.state.isShopManager" class="text-h6 ml-3">{{ branch }}</span>
      <v-spacer></v-spacer>

      <v-text-field v-model="search" clearable dense flat solo-inverted hide-details prepend-inner-icon="mdi-magnify"
        v-if="$vuetify.breakpoint.mdAndUp" class="mx-2" label="姓名/工号"></v-text-field>
      <v-spacer></v-spacer>

      <v-dialog offset-y width="650" persistent v-model="newArr">
        <template v-slot:activator="{ on, attrs }">
          <v-btn color="primary" dark v-bind="attrs" v-on="on" class="mr-2" outlined depressed>
            智能排班
          </v-btn>
        </template>
        <newArrangement @close="newArr = false" :size="size" :branch="branch" @msg="getMsg" />

      </v-dialog>

      <v-btn depressed class="mr-2">
        暂存
      </v-btn>
      <v-btn depressed class="mr-2">
        发布
      </v-btn>

      <v-menu offset-y>
        <template v-slot:activator="{ on, attrs }">
          <v-btn v-bind="attrs" v-on="on" depressed>
            <span class="ml-2">更多</span>
            <v-icon>mdi-menu-down</v-icon>
          </v-btn>
        </template>
        <v-list>
          <v-list-item>
            <download-excel name="排班导出结果.xls">
              <v-btn text>导出/打印</v-btn>
            </download-excel>

          </v-list-item>
          <v-list-item>
            <v-btn text>历史排班</v-btn>
          </v-list-item>
        </v-list>
      </v-menu>
    </v-toolbar>



    <v-card class="py-3 px-5" flat :color="$vuetify.theme.dark === false ? 'white' : '#121212'">
      <v-sheet>
        <v-toolbar flat color="transparent">

          <v-toolbar-title v-if="$refs.calendar">
            {{ $refs.calendar.title }}
          </v-toolbar-title>

          <v-btn fab text small color="grey darken-2" @click="prev" class="ml-1">
            <v-icon small>
              mdi-chevron-left
            </v-icon>
          </v-btn>
          <v-btn small fab text color="grey darken-2" @click="setToday">
            <b>·</b>
          </v-btn>
          <v-btn fab text small color="grey darken-2" @click="next">
            <v-icon small>
              mdi-chevron-right
            </v-icon>
          </v-btn>

          <v-spacer></v-spacer>

          <v-btn-toggle v-model="type" dense mandatory color="primary">
            <v-btn value="month">
              月
            </v-btn>

            <v-btn value="week">
              周
            </v-btn>

            <v-btn value="category">
              日
            </v-btn>
          </v-btn-toggle>

        </v-toolbar>
      </v-sheet>
      <v-sheet>
        <v-calendar ref="calendar" v-model="focus" color="primary" :events="scheduleType" :event-color="getEventColor"
          first-interval="6" interval-count="18" locale="zh-cn" :type="type" @click:event="showEvent"
          @click:more="viewDay" @click:date="viewDay" event-overlap-mode="column" @change="updateRange"
          :categories="filteredEmployee">
        </v-calendar>

        <v-menu v-model="selectedOpen" :close-on-content-click="false" :activator="selectedElement" offset-x>
          <v-card min-width="350px" flat>
            <v-toolbar :color="selectedEvent.color" dark>

              <v-menu>
                <template v-slot:activator="{ on, attrs }">

                  <v-btn icon v-bind="attrs" v-on="on">
                    <v-icon>mdi-pencil</v-icon>
                  </v-btn>
                </template>

                <v-card flat :color="$vuetify.theme.dark === false ? 'white' : '#121212'">
                  <v-card-title class="white--text accent">
                    本店员工
                    <v-spacer></v-spacer>

                  </v-card-title>

                  <v-virtual-scroll :items="filteredStaff" :item-height="63" height="00">
                    <template v-slot:default="{ item }">

                      <v-list-item>
                        <v-list-item-avatar>
                          <v-img :src="item.avatar"></v-img>
                        </v-list-item-avatar>

                        <v-list-item-content>
                          <v-list-item-title>{{ item.username }}</v-list-item-title>
                          <v-list-item-subtitle>{{ item.uid }}</v-list-item-subtitle>
                        </v-list-item-content>
                      </v-list-item>
                      <v-divider></v-divider>

                    </template>
                  </v-virtual-scroll>
                </v-card>
              </v-menu>

              <v-toolbar-title v-html="selectedEvent.name"></v-toolbar-title>
              <v-spacer></v-spacer>
              <v-btn icon>
                <v-icon>mdi-heart</v-icon>
              </v-btn>
              <v-btn icon>
                <v-icon>mdi-dots-vertical</v-icon>
              </v-btn>
            </v-toolbar>
            <v-card-text>
              <span v-html="selectedEvent.details"></span>
            </v-card-text>
            <v-card-actions>
              <v-btn text color="secondary" @click="selectedOpen = false">
                关闭
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-menu>
      </v-sheet>

    </v-card>


  </div>
</template>

<script>
import { getAllShop, getShopInfo } from '../request/shop'
import { getEmployeeByShop, getEmployee } from '../request/staff'
import { getUserAvatar } from '../request/user'
import { getAllArr, getRule } from '../request/rule'
import newArrangement from '../components/newArrangement.vue'

export default {
  components: {
    newArrangement
  },
  data() {
    return {
      focus: '',
      newArr: false,
      type: 'month',
      search: '',
      typeToLabel: {
        month: '月视图',
        week: '周视图',
        category: '日视图',
      },
      selectedEvent: {},
      selectedElement: null,
      selectedOpen: false,

      events: [],
      rawEvents: [],

      colors: ['blue', 'indigo', 'deep-purple', 'cyan', 'green', 'orange', 'grey darken-1'],
      names: ['Meeting', 'Holiday', 'PTO', 'Travel', 'Event', 'Birthday', 'Conference', 'Party'],

      branch: '',
      branches: [],
      size: null,
      staff: [],
      categories: {},
      filteredEmployee: [],

      rules: [],

      json_fields: {
        id: "",
        employees: "",
        beginTime: "",
        shop: "",
        manager: "",
        createAt: "",
        isActive: "",
        useRule: "",
        startAt: "",
        endAt: "",
        week: "",

      }

    }
  },
  computed: {
    scheduleType() {
      return this.type === 'week' ? this.rawEvents : this.events
    },
    filteredStaff() {
      return this.staff.filter(p => {
        return p.uid.indexOf(this.search) !== -1 || p.username.indexOf(this.search) !== -1
      })
    },


    json_data() {
      return {
        "排班表id": this.json_fields.id,
        "员工id": this.json_fields.employees,
        "开始工作时间": this.json_fields.beginTime,
        "店铺id": this.json_fields.shop,
        "管理员id": this.json_fields.manager,
        "创建时间": this.json_fields.createAt,
        "是否正在使用": this.json_fields.isActive,
        "使用的排班规则id": this.json_fields.useRule,
        "开始启用时间": this.json_fields.startAt,
        "结束使用时间": this.json_fields.endAt,
        "工作详细": this.json_fields.week,
      }
    }

  },
  methods: {
    viewDay({ date }) {
      this.focus = date
      this.type = 'category'
    },
    getEventColor(event) {
      return event.color
    },
    setToday() {
      this.focus = ''
    },
    prev() {
      this.$refs.calendar.prev()
    },
    next() {
      this.$refs.calendar.next()
    },
    showEvent({ nativeEvent, event }) {
      const open = () => {
        this.selectedEvent = event
        this.selectedElement = nativeEvent.target
        requestAnimationFrame(() => requestAnimationFrame(() => this.selectedOpen = true))
      }

      if (this.selectedOpen) {
        this.selectedOpen = false
        requestAnimationFrame(() => requestAnimationFrame(() => open()))
      } else {
        open()
      }

      nativeEvent.stopPropagation()
    },
    updateRange({ start, end }) {
      console.log(start, 123, end)

      this.filteredEmployee = this.categories[start.date]

    },
    rnd(a, b) {
      return Math.floor((b - a + 1) * Math.random()) + a
    },
    async getStaff() {
      this.staff = []
      for (var b of this.branches) {
        if (b.id === this.branch) {
          this.size = b.size
        }
      }
      var staff = (await getEmployeeByShop(this.branch)).data.data
      staff.forEach(s => {
        s.avatar = require('../assets/defaultAvatar.png')
      })
      this.staff = staff

      if (this.staff.length === 0) this.staff = []

    },
    async getArr() {
      this.events = [],
        this.rawEvents = []
      var events = (await getAllArr(this.branch)).data

      this.rules = (await getRule(events.data[events.data.length - 1].useRule)).data.data
      var weeks = events.data[events.data.length - 1].weeks
      var rawEvents = []


      for (var week of weeks) {

        for (var day of week.data) {
          if (day.some(item => item !== null)) {
            var employees = []
            var categories = []
            for (var event of day) {
              if (event !== null) {

                //将排班处理成按员工分类
                var start = event.beginTime
                for (var employee of event.employees) {
                  var flag = false
                  employees.forEach(e => {
                    if (e.id === employee) {
                      flag = true
                      if (start === e.end[e.end.length - 1]) {
                        e.end[e.end.length - 1] += 1800000
                      }
                      else {
                        e.start.push(start)
                        e.end.push(start + 1800000)
                      }
                    }
                  })
                  if (!flag) {
                    var name=this.staff.find(item => item.id === employee)
                    employees.push({
                      id: employee,
                      start: [start],
                      end: [start + 1800000],
                      category: name!==undefined?name.username:'开放班次',
                      name: name!==undefined?name.username:'开放班次',
                      color: name!==undefined?this.colors[this.rnd(0, this.colors.length - 1)]:'grey'
                    })
                  }
                }


                //将排班处理成按时间分类
                var d = new Date(event.beginTime).getDay()
                var time = new Date(event.beginTime).getHours()
                var color
                if (d >= 1 & d <= 5) {
                  if (time < 9 || time >= 21) color = 'green'
                  else color = 'blue'
                }
                else {
                  if (time < 10 || time >= 22) color = 'green'
                  else color = 'blue'
                }
                rawEvents.push({
                  start: new Date(event.beginTime),
                  end: new Date(event.beginTime + 1800000),
                  name: event.employees.length + '个员工',
                  detail: event.employees,
                  color,
                  timed: true
                })


              }

            }

            var date
            for (var e of employees) {
              for (var i = 0; i < e.start.length; i++) {
                categories.push(e.category)
                date = this.formatDate(e.start[i])
                this.events.push({
                  id: e.id,
                  start: new Date(e.start[i]),
                  end: new Date(e.end[i]),
                  name: e.name,
                  category: e.category,
                  color: e.color,
                  timed: true
                })
              }
            }

            for (let i = 0; i < categories.length; i++) {
              for (var j = i + 1; j < categories.length; j++) {
                if (categories[i] == categories[j]) {         //第一个等同于第二个，splice方法删除第二个
                  categories.splice(j, 1);
                  j--;
                }
              }
            }
            this.categories[date] = categories
          }
        }

      }
      this.rawEvents = rawEvents

    },
    formatDate(value) { // 时间戳转换日期格式方法
      if (value == null) {
        return ''
      } else {
        const date = new Date(value)
        const y = date.getFullYear()// 年
        let MM = date.getMonth() + 1 // 月
        MM = MM < 10 ? ('0' + MM) : MM
        let d = date.getDate() // 日
        d = d < 10 ? ('0' + d) : d
        return y + '-' + MM + '-' + d
      }
    },
    getMsg(data) {
      this.$emit('msg', data)
    }
  },
  async mounted() {
    this.$refs.calendar.checkChange()
    if (this.$store.state.isManager) {
      getAllShop().then(async res => {
        this.branches = res.data.data
        if (this.branches.length !== 0) {
          this.branch = this.branches[0].id
          await this.getStaff()
          await this.getArr()

          this.staff.forEach(async s => {
            var avatar = await getUserAvatar(s.id)
            if (avatar.status === 200) {
              s.avatar = URL.createObjectURL(avatar.data)
            }
            else {
              s.avatar = require('../assets/defaultAvatar.png')
            }
          })

        }
        else {
          this.$emit('msg', '没有店铺信息')
        }
      }).catch((err) => {
        console.log(err)
        this.$emit('msg', '网络错误')
      })
    }
    else if (this.$store.state.isShopManager) {
      let employee = (await getEmployee()).data.data
      let shopName = (await getShopInfo(employee.shop)).data.data.name
      this.branch = shopName



    }
    else {
      let employee = (await getEmployee()).data.data
      let shopName = (await getShopInfo(employee.shop)).data.data.name
      this.branch = shopName


    }




  }

}
</script>
