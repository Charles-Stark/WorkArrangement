<template>
  <div>
    <v-toolbar :color="$vuetify.theme.dark === false ? 'white' : '#121212'" flat
      v-if="$store.state.isManager || $store.state.isShopManager">
      <v-select v-model="branch" :items="branches" item-text="name" item-value="id" solo-inverted interval-minutes="60"
        no-data-text="没有数据" dense flat hide-details style="max-width:140px;min-width:120px"
        @change="changeBranch(); categories = {}" v-if="$store.state.isManager"></v-select>
      <span v-if="!$store.state.isManager" class="text-h6 ml-3">{{ shopName }}</span>
      <v-spacer></v-spacer>

      <v-text-field v-model="search1" clearable dense flat solo-inverted hide-details prepend-inner-icon="mdi-magnify"
        @click:clear="search1 = ''" v-if="$vuetify.breakpoint.mdAndUp" class="mx-2" label="姓名/工号/岗位"></v-text-field>
      <v-spacer></v-spacer>

      <v-dialog offset-y width="650" persistent v-model="newArr">
        <template v-slot:activator="{ on, attrs }">
          <v-btn color="primary" dark v-bind="attrs" v-on="on" class="mr-2" outlined depressed>
            智能排班
          </v-btn>
        </template>
        <newArrangement @close="newArr = false" :size="size" :branch="branch" @msg="getMsg" />
      </v-dialog>

      <v-btn text to="history">历史排班</v-btn>

      <download-excel :data="json_data" :fields="json_fields" name="排班导出结果.xls">
        <v-btn text @click="GetExcel">导出/打印</v-btn>
      </download-excel>

    </v-toolbar>
    <v-card v-if="ready" class="py-3 px-5" flat :color="$vuetify.theme.dark === false ? 'white' : '#121212'">
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

          <v-dialog offset-y width="550" persistent v-model="newAbsc"
            v-if="!this.$store.state.isManager & !this.$store.state.isShopManager">
            <template v-slot:activator="{ on, attrs }">
              <v-btn color="primary" dark v-bind="attrs" v-on="on" class="mr-2" outlined depressed>
                新建请假
              </v-btn>
            </template>
            <newAbsence @close="newAbsc = false" @msg="getMsg" />

          </v-dialog>

          <v-btn-toggle v-model="type" dense mandatory color="primary">
            <v-btn value="month">
              月
            </v-btn>

            <v-btn value="week">
              周
            </v-btn>

            <v-btn value="category" v-if="this.$store.state.isManager || this.$store.state.isShopManager">
              日
            </v-btn>

            <v-btn value="day" v-else>
              日
            </v-btn>
          </v-btn-toggle>

        </v-toolbar>
      </v-sheet>
      <v-sheet>
        <v-calendar ref="calendar" v-model="focus" color="primary" :events="scheduleType" :event-color="getEventColor"
          first-interval="6" interval-count="18" locale="zh-cn" :type="type" @click:event="showEvent"
          @click:date="viewDay" event-overlap-mode="column" @change="updateRange" :categories="filteredEmployee">
        </v-calendar>

        <v-menu v-model="selectedOpen" :close-on-content-click="false" :activator="selectedElement" offset-x>
          <v-card min-width="350px" flat>
            <v-toolbar :color="selectedEvent.color" dark>
              <!-- <v-list-item-avatar v-if="type !== 'week'">
                <v-img :src="selectedEvent.avatar"></v-img>
              </v-list-item-avatar> -->
              <v-toolbar-title>{{ selectedEvent.name }}</v-toolbar-title>
              <v-toolbar-title class="ml-2">{{ selectedEvent.position }}</v-toolbar-title>

              <v-toolbar-title class="ml-1" v-if="selectedEvent.single === true">{{ selectedEvent.showStart + ' - '
                + selectedEvent.showEnd }}</v-toolbar-title>

              <v-spacer></v-spacer>

              <v-btn icon v-if="selectedEvent.single === true || type !== 'week'"><v-icon>mdi-open-in-app</v-icon></v-btn>
            </v-toolbar>

            <v-text-field v-model="search2" clearable dense flat solo-inverted hide-details
              prepend-inner-icon="mdi-magnify" v-if="selectedEvent.single === true || type !== 'week'" class="ma-2"
              label="姓名/工号"></v-text-field>

            <!-- 周视图 -->
            <v-virtual-scroll v-if="type === 'week'"
              :items="selectedEvent.single === true ? filteredStaff : selectedEvent.detail" :item-height="63" height="300"
              width="400">
              <template v-slot:default="{ item, index }">

                <v-list-item @click="selectedEvent.single === true ? dialog = true : openSelected(item, index)">
                  <v-list-item-avatar>
                    <v-img :src="item.avatar"></v-img>
                  </v-list-item-avatar>

                  <v-list-item-content>
                    <v-list-item-title>{{ item.username }}</v-list-item-title>
                    <v-list-item-subtitle>{{ item.position }}</v-list-item-subtitle>

                  </v-list-item-content>
                </v-list-item>
                <v-divider></v-divider>

              </template>
            </v-virtual-scroll>

            <div v-if="type !== 'week'">
              <v-list-item-title class=" ml-3 grey--text text-subtitle">推荐员工</v-list-item-title>
              <v-list-item @click="dialog = true; selectedEmployee = s" v-for="s of staff.slice(6, 9)" :key="s.id">
                <v-list-item-avatar>
                  <v-img :src="s.avatar"></v-img>
                </v-list-item-avatar>
                <v-list-item-content>
                  <v-list-item-title>{{ s.username }}</v-list-item-title>
                  <v-list-item-subtitle>{{ s.position }}</v-list-item-subtitle>
                </v-list-item-content>
              </v-list-item>

              <v-list-item-title class="ml-3 grey--text text-subtitle">其他员工</v-list-item-title>
            </div>

            <!-- 月、日视图 -->
            <v-virtual-scroll v-if="type !== 'week'" :items="filteredStaff" :item-height="63" height="300" width="400">
              <template v-slot:default="{ item }">
                <v-list-item @click="dialog = true; selectedEmployee = item">
                  <v-list-item-avatar>
                    <v-img :src="item.avatar"></v-img>
                  </v-list-item-avatar>

                  <v-list-item-content>
                    <v-list-item-title>{{ item.username }}</v-list-item-title>
                    <v-list-item-subtitle>{{ item.position }}</v-list-item-subtitle>
                  </v-list-item-content>
                </v-list-item>
                <v-divider></v-divider>

              </template>
            </v-virtual-scroll>

            <v-card-actions>
              <v-btn text :color="selectedEvent.color" @click="selectedOpen = false">
                关闭
              </v-btn>
            </v-card-actions>
          </v-card>

          <v-dialog v-model="dialog" width="350">
            <v-card>
              <v-card-title class="">
                确认将该班次替换为该员工吗
              </v-card-title>

              <v-divider></v-divider>

              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="warning" text @click="dialog = false; changeSchedule()">
                  确认
                </v-btn>
                <v-btn color="primary" text @click="dialog = false">
                  取消
                </v-btn>
              </v-card-actions>
            </v-card>
          </v-dialog>
        </v-menu>



      </v-sheet>

    </v-card>
    <v-container v-else style="height: 800px;">
      <v-row class="fill-height" align-content="center" justify="center">
        <v-col class="text-h5 text-center" cols="12">
          正在加载排班表
        </v-col>
        <v-col cols="6">
          <v-progress-linear color="deep-purple accent-4" indeterminate rounded height="6"></v-progress-linear>
        </v-col>
      </v-row>
    </v-container>


  </div>
</template>

<script>
import { getAllShop, getShopInfo } from '../request/shop'
import { getEmployeeByShop, getEmployee } from '../request/staff'
import { getUserAvatar, getUserInfo } from '../request/user'
import { getLatestArr, getRule, getArrByEmployee } from '../request/rule'
import newArrangement from '../components/newArrangement.vue'
import newAbsence from '../components/newAbsence.vue'

export default {
  components: {
    newArrangement, newAbsence
  },
  data() {
    return {
      selectedEmployee: '',
      focus: '',
      newArr: false,
      newAbsc: false,
      type: 'month',
      ready: false,
      search1: null,
      search2: null,
      typeToLabel: {
        month: '月视图',
        week: '周视图',
        category: '日视图',
        day: '日视图'
      },
      selectedEvent: {},
      selectedElement: null,
      selectedOpen: false,

      events: [],
      rawEvents: [],

      colors: ['blue', 'indigo', 'deep-purple', 'cyan', 'green', 'orange', 'pink lighten-1', 'purple lighten-2', 'teal lighten-2', 'green darken-1'],
      names: ['Meeting', 'Holiday', 'PTO', 'Travel', 'Event', 'Birthday', 'Conference', 'Party'],

      user: {},

      branch: '',
      branches: [],
      size: null,
      staff: [],
      categories: {},
      filteredEmployee: [],
      startTimes: {},
      shopName: "",
      rules: [],
      dialog: false,

      /*以下三个data是打印使用*/
      json_fields: {
        "月排班序号": "id",
        "店铺序号": "shop",
        "管理员序号": "manager",
        "月排班表创建时间": "createAt",
        "月排班表是否生效": "isActive",
        "该月使用规则序号": "useRule",
        "月排班表生效时间": "startAt",
        "月排班表结束时间": "endAt",
        "周排班表序号": "WeekId",
        "周排班表开始时间": "WeekStartAt",
        "周排班表结束时间": "WeekEndAt",
        "天排班表序号": "DayId",
        "开始时间": "beginTime",
        "工作员工序号": "employeesId"
      },
      json_data: [
        {
          id: "",
          shop: "",
          manager: "",
          createAt: "",
          isActive: "",
          useRule: "",
          startAt: "",
          endAt: "",
          WeekId: "",
          WeekStartAt: "",
          WeekEndAt: "",
          DayId: "",
          beginTime: "",
          employeesId: "",
        }
      ],
      something_data: [],


    }
  },
  computed: {
    scheduleType() {
      var rawEvents = this.rawEvents.filter(r => {
        return r.detail.some(e => {
          if (e !== undefined)
            return e.position.indexOf(this.search1 || '') !== -1 || e.username.indexOf(this.search1 || '') !== -1 || e.uid.indexOf(this.search1 || '') !== -1
        })
      })
      rawEvents.forEach(r => {
        r.detail = r.detail.filter(e => {
          if (e !== undefined)
            return e.position.indexOf(this.search1 || '') !== -1 || e.username.indexOf(this.search1 || '') !== -1 || e.uid.indexOf(this.search1 || '') !== -1
        })
        r.name = r.detail.length + '个员工'
      })
      var events = this.events.filter(e => { return e.position.indexOf(this.search1 || '') !== -1 || e.name.indexOf(this.search1 || '') !== -1 || e.uid.indexOf(this.search1 || '') !== -1 })
      return this.type === 'week' & (this.$store.state.isManager || this.$store.state.isShopManager) ? rawEvents : events
    },
    filteredStaff() {
      return this.staff.filter(p => {
        return p.uid.indexOf(this.search2 || '') !== -1 || p.username.indexOf(this.search2 || '') !== -1 || p.position.indexOf(this.search2 || '') !== -1
      })
    },




  },
  methods: {
    GetExcel() {
      /*
      * 一天：32              this.something_data.weeks[0].data[0].length
      * 一周：7*32            this.something_data.weeks[0].data.length * this.something_data.weeks[0].data[0].length
      * 一个月：7*32*5         this.something_data.weeks.length * this.something_data.weeks[0].data.length * this.something_data.weeks[0].data[0].length
      * */

      for (var i = 0; i < this.something_data.weeks.length; i++) {//周
        for (var j = 0; j < this.something_data.weeks[0].data.length; j++) {//天
          for (var x = 0; x < this.something_data.weeks[0].data[0].length; x++) {//时
            /*console.log("i="+i)
            console.log(this.something_data.weeks.length)
            console.log("j="+j)
            console.log( this.something_data.weeks[0].data.length)
            console.log("x="+x)
            console.log(this.something_data.weeks[0].data[0].length)
            console.log(this.something_data.weeks[i].data[j][x].beginTime)
            console.log(this.something_data.weeks[i].data[j][x].employees)*/
            let weeksdatas = {
              "id": this.something_data.id,
              "shop": this.something_data.shop,
              "manager": this.something_data.manager,
              "createAt": this.formatDate(this.something_data.createAt),
              "isActive": this.something_data.isActive,
              "useRule": this.something_data.useRule,
              "startAt": this.formatDate(this.something_data.startAt),
              "endAt": this.formatDate(this.something_data.endAt),
              "WeekId": i + 1,
              "WeekStartAt": this.formatDate(this.something_data.weeks[i].startAt),
              "WeekEndAt": this.formatDate(this.something_data.weeks[i].endAt),
              "DayId": j + 1,
              "beginTime": this.formatDate(this.something_data.weeks[i].data[j][x].beginTime),
              "employeesId": this.something_data.weeks[i].data[j][x].employees
            }
            this.json_data.push(weeksdatas)

          }
        }
      }


    },
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
      if (this.$store.state.isManager || this.$store.state.isShopManager) {
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
      }
    },
    updateRange({ start }) {
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
        s.color = this.colors[this.rnd(0, this.colors.length - 1)]
      })

      this.staff = staff

      this.staff.forEach(async s => {
        var avatar = await getUserAvatar(s.id)
        if (avatar.status === 200) {
          s.avatar = URL.createObjectURL(avatar.data)
        }
        else {
          s.avatar = require('../assets/defaultAvatar.png')
        }
      })


      this.staff.push({
        avatar: '123',
        durationOfShift: '',
        durationOfWeek: '',
        email: '',
        id: 0,
        position: '',
        salary: '',
        shop: '',
        time: '',
        uid: '',
        username: '开放班次',
        workingDay: '',
        workingHours: '',
        color: 'grey'

      })
      if (staff.length === 0) this.staff = []


    },
    async getArr() {
      this.events = []
      this.rawEvents = []
      this.rules = []
      if (this.$store.state.isManager || this.$store.state.isShopManager) {
        var events = (await getLatestArr(this.branch)).data
        this.something_data = events.data
        console.log(this.something_data)
        this.rules = (await getRule(events.data.useRule)).data.data
        var weeks = events.data.weeks
        var rawEvents = []
        for (let week of weeks) {
          for (let day of week.data) {
            if (day.some(item => item !== null)) {
              var employees = []
              var categories = []
              for (let event of day) {
                if (event !== null) {
                  //将排班处理成按员工分类
                  let start = event.beginTime
                  for (let employee of event.employees) {
                    if (employee !== null) {
                      let flag = false
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
                        var e = this.staff.find(item => item.id === employee)
                        employees.push({
                          id: employee,
                          start: [start],
                          end: [start + 1800000],
                          category: e.username,
                          name: e.username,
                          color: e.color,
                          avatar: e.avatar,
                          position: e.position,
                          uid: e.uid,
                        })
                      }
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
                  var detail = []
                  event.employees.forEach(employee => {
                    detail.push(this.staff.find(item => item.id === employee))
                  })
                  rawEvents.push({
                    start: new Date(event.beginTime),
                    end: new Date(event.beginTime + 1800000),
                    name: event.employees.length + '个员工',
                    detail,
                    color,
                    timed: true,
                  })


                }

              }

              var date
              for (let e of employees) {
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
                    avatar: e.avatar,
                    timed: true,
                    position: e.position,
                    uid: e.uid

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
              this.startTimes[date] = employees
            }
          }

        }
        this.rawEvents = rawEvents
      }
      else {
        events = (await getArrByEmployee()).data.data
        this.rules = (await getRule(events.useRule)).data.data
        weeks = events.weeks
        for (let week of weeks) {
          for (let day of week.data) {
            if (day.some(item => item !== null)) {
              var shifts = []
              let flag = false
              for (let event of day) {
                if (event !== null) {
                  var start = event.beginTime


                  shifts.forEach(e => {
                    if (start === e.end[e.end.length - 1]) {
                      e.end[e.end.length - 1] += 1800000
                    }
                    else {
                      e.start.push(start)
                      e.end.push(start + 1800000)
                    }

                  })

                  if (!flag) {
                    flag = true
                    shifts.push({
                      start: [start],
                      end: [start + 1800000],
                      name: this.user.username,
                      color: `primary darken-1`,
                      avatar: this.user.avatar,
                    })
                  }

                }

              }
              for (let e of shifts) {
                for (let i = 0; i < e.start.length; i++) {
                  this.events.push({
                    start: new Date(e.start[i]),
                    end: new Date(e.end[i]),
                    name: e.name,
                    category: e.category,
                    color: e.color,
                    avatar: e.avatar,
                    timed: true
                  })
                }
              }
            }
          }

        }

      }
      this.ready = true
    },
    async changeBranch() {
      this.ready = false
      await this.getStaff()
      await this.getArr()
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
    openSelected(item) {
      let day = this.startTimes[this.formatDate(this.selectedEvent.start)]
      let selectedEmployee = day.find(i => i.id === item.id)
      let start, end
      for (let i = 0; i < selectedEmployee.start.length; i++) {
        if (this.selectedEvent.start >= selectedEmployee.start[i] & this.selectedEvent.end <= selectedEmployee.end[i]) {
          start = new Date(selectedEmployee.start[i])
          end = new Date(selectedEmployee.end[i])
          break
        }
      }
      this.selectedEvent = {
        id: item.id,
        start,
        end,
        showStart: start.getMinutes() === 0 ? start.getHours() + '时' : start.getHours() + ':' + start.getMinutes(),
        showEnd: end.getMinutes() === 0 ? end.getHours() + '时' : end.getHours() + ':' + end.getMinutes(),
        name: item.username,
        color: this.selectedEvent.color,
        avatar: item.avatar,
        timed: true,
        single: true,
      }
    },
    changeSchedule() {
      console.log(this.selectedEmployee)
      setTimeout(() => {
        this.$emit('msg', '替换成功')
      }, 500);
      this.selectedOpen = false


    },
    getMsg(data) {
      this.$emit('msg', data)
    },
  },

  async mounted() {
    // this.$refs.calendar.checkChange()
    if (this.$store.state.isManager) {
      getAllShop().then(async res => {
        this.branches = res.data.data
        if (this.branches.length !== 0) {
          this.branch = this.branches[0].id
          await this.getStaff()
          await this.getArr()
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
      let shop = (await getShopInfo(employee.shop)).data.data
      this.branch = shop.id
      this.shopName = shop.name
      await this.getStaff()
      await this.getArr()
    }
    else {
      let user = (await getUserInfo()).data.data
      let avatar = (await getUserAvatar())
      if (avatar.status === 200) {
        let url = URL.createObjectURL(avatar.data)
        user.avatar = url
      }
      else if (avatar.status === 204) {
        user.avatar = require('../assets/defaultAvatar.png')
      }
      this.user = user

      let employee = (await getEmployee()).data.data
      let shop = (await getShopInfo(employee.shop)).data.data
      this.branch = shop.id
      this.shopName = shop.name
      this.getArr()
    }

  }

}
</script>

<style>
.v-calendar-category .v-calendar-daily__day {
  min-width: 30px;
}

.v-calendar-category .v-calendar-category__columns .v-calendar-category__column-header {
  min-width: 30px;
}
</style>
