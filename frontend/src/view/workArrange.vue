<template>
  <div>

    <v-sheet>
      <v-toolbar :color="$vuetify.theme.dark === false ? 'white' : '#121212'" flat>
        <v-select v-model="branch" :items="branches" item-text="name" item-value="id" solo-inverted interval-minutes="60"
          no-data-text="没有数据" dense flat hide-details style="max-width:140px;min-width:120px"
          @change="getStaff(); getArr()"></v-select>
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
          <newArrangement @close="newArr = false" :size="size" :branch="branch" />

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
    </v-sheet>



    <v-card class="py-3 px-5" flat :color="$vuetify.theme.dark === false ? 'white' : '#121212'">
      <v-row>
        <v-col cols="3" v-if="$vuetify.breakpoint.mdAndUp & ($store.state.isManager || $store.state.isShopManager)">
          <v-card flat :color="$vuetify.theme.dark === false ? 'white' : '#121212'">
            <v-card-title class="white--text accent">
              本店员工
              <v-spacer></v-spacer>

            </v-card-title>

            <v-virtual-scroll :items="filteredStaff" :item-height="63" height="700">
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
        </v-col>
        <v-col>
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

                <v-btn value="day">
                  日
                </v-btn>
              </v-btn-toggle>

            </v-toolbar>
          </v-sheet>
          <v-sheet>
            <v-calendar ref="calendar" v-model="focus" color="primary" :events="scheduleType" :event-color="getEventColor"
              first-interval="6" interval-count="18" locale="zh-cn" :type="type" @click:event="showEvent"
              @click:more="viewDay" @click:date="viewDay" event-overlap-mode="column" @change="updateRange">
            </v-calendar>

            <v-menu v-model="selectedOpen" :close-on-content-click="false" :activator="selectedElement" offset-x>
              <v-card min-width="350px" flat>
                <v-toolbar :color="selectedEvent.color" dark>
                  <v-btn icon>
                    <v-icon>mdi-pencil</v-icon>
                  </v-btn>
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
                    Cancel
                  </v-btn>
                </v-card-actions>
              </v-card>
            </v-menu>
          </v-sheet>
        </v-col>
      </v-row>
    </v-card>


  </div>
</template>

<script>
import { getAllShop } from '../request/shop'
import { getEmployeeByShop } from '../request/staff'
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
        day: '日视图',
      },
      selectedEvent: {},
      selectedElement: null,
      selectedOpen: false,

      events: [],
      rawEvents: [],

      colors: ['blue', 'indigo', 'deep-purple', 'cyan', 'green', 'orange', 'grey darken-1'],
      names: ['Meeting', 'Holiday', 'PTO', 'Travel', 'Event', 'Birthday', 'Conference', 'Party'],

      branch: ' ',
      branches: [],
      size: null,
      staff: [],

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
      this.type = 'day'
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
      const events = []

      const min = new Date(`${start.date}T00:00:00`)
      const max = new Date(`${end.date}T23:59:59`)
      const days = (max.getTime() - min.getTime()) / 86400000
      const eventCount = this.rnd(days, days + 20)

      for (let i = 0; i < eventCount; i++) {
        const allDay = this.rnd(0, 3) === 0
        const firstTimestamp = this.rnd(min.getTime(), max.getTime())
        const first = new Date(firstTimestamp - (firstTimestamp % 900000))
        const secondTimestamp = this.rnd(2, allDay ? 288 : 8) * 900000
        const second = new Date(first.getTime() + secondTimestamp)

        events.push({
          name: this.names[this.rnd(0, this.names.length - 1)],
          start: first,
          end: second,
          color: this.colors[this.rnd(0, this.colors.length - 1)],
          timed: !allDay,
        })
      }
      // this.events = events
    },
    rnd(a, b) {
      return Math.floor((b - a + 1) * Math.random()) + a
    },
    async getStaff() {
      for (var b of this.branches) {
        if (b.id === this.branch) {
          this.size = b.size
        }
      }
      var staff = (await getEmployeeByShop(this.branch)).data.data
      staff.forEach(s => {
        s.avatar = ''
        this.staff.push(s)
      })

      if (this.staff.length === 0) this.staff = []

    },
    async getArr() {
      var events = (await getAllArr(this.branch)).data
      console.log(events)
      this.rules = (await getRule(events.data[events.data.length - 1].useRule)).data.data
      var weeks = events.data[events.data.length - 1].weeks
      var rawEvents = []


      for (var week of weeks) {

        for (var day of week.data) {
          var employees = []
          if (day !== null) {
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
                    employees.push({
                      id: employee,
                      start: [start],
                      end: [start + 1800000],
                      name: this.staff.find(item => item.id === employee).username,
                    })
                  }
                }


                //将排班处理成按时间分类
                var d=new Date(event.beginTime).getDay()
                var time=new Date(event.beginTime).getHours()
                var color
                if(d>=1& d<=5){
                  if(time<9||time>=21)color='green'
                  else color='blue'
                }
                else{
                  if(time<10||time>=22)color='green'
                  else color='blue'
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
          }
          for (var e of employees) {
            for (var i = 0; i < e.start.length; i++) {
              this.events.push({
                id: e.id,
                start: new Date(e.start[i]),
                end: new Date(e.end[i]),
                name: e.name,
                color: this.colors[this.rnd(0, this.colors.length - 1)],
                timed: true
              })

            }
          }



        }

      }
      this.rawEvents = rawEvents
    }
  },
  mounted() {
    this.$refs.calendar.checkChange()

    getAllShop().then(async res => {
      this.branches = res.data.data
      if (this.branches.length !== 0) {
        this.branch = this.branches[0].id
        await this.getStaff()
        await this.getArr()

        this.staff.forEach( async s => {
        var avatar =  await getUserAvatar(s.id)
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
    }).catch(() => {
      this.$emit('msg', '网络错误')
    })

    

  }

}
</script>
