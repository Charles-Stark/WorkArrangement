<template>
  <div v-if="((branches.length !== 0 && $store.state.isManager) || !$store.state.isManager) && ready1">
    <v-toolbar :color="$vuetify.theme.dark === false ? 'white' : '#121212'" flat
      v-if="$store.state.isManager || $store.state.isShopManager">
      <v-select v-model="branch" :items="branches" item-text="name" item-value="id" solo interval-minutes="60"
        no-data-text="没有数据" dense flat hide-details style="max-width:140px;min-width:120px"
        @change="changeBranch(); categories = {}" v-if="$store.state.isManager"></v-select>
      <span v-if="!$store.state.isManager" class="text-h6 ml-3">{{ shopName }}</span>
      <v-spacer></v-spacer>

      <v-text-field v-model="search1" clearable dense flat solo hide-details prepend-inner-icon="mdi-magnify"
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
    <v-card v-if="ready1" class="py-3 px-5" flat :color="$vuetify.theme.dark === false ? 'white' : '#121212'">
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
              <v-toolbar-title>{{ selectedEvent.name }}</v-toolbar-title>
              <v-toolbar-title class="ml-2">{{ selectedEvent.position }}</v-toolbar-title>

              <v-toolbar-title class="ml-1" v-if="selectedEvent.single === true">{{ selectedEvent.showStart + ' - '
                + selectedEvent.showEnd }}</v-toolbar-title>

              <v-spacer></v-spacer>

              <v-btn icon v-if="selectedEvent.single === true || type !== 'week'"
                @click="selectedEmployee = 0; dialog = true"><v-icon>mdi-open-in-app</v-icon></v-btn>
            </v-toolbar>

            

            <div v-if="ready2">

              <v-text-field v-model="search2" clearable dense flat solo hide-details
              prepend-inner-icon="mdi-magnify" v-if="selectedEvent.single === true || type !== 'week'" class="ma-2"
              label="姓名/工号"></v-text-field>

              <div v-if="type !== 'week' || selectedEvent.single === true">
                <v-list-item-title class=" ml-3 grey--text text-subtitle" v-if="search2 === null">推荐员工</v-list-item-title>
                <v-list-item @click="selectedEmployee = s.id; dialog = true" v-for="s of filteredStaff.slice(0, 3)"
                  :key="s.id">
                  <v-list-item-avatar>
                    <v-img :src="s.avatar"></v-img>
                  </v-list-item-avatar>
                  <v-list-item-content>
                    <v-list-item-title>{{ s.username }}</v-list-item-title>
                    <v-list-item-subtitle>{{ s.position }}</v-list-item-subtitle>
                  </v-list-item-content>
                </v-list-item>

                <v-list-item-title class="ml-3 grey--text text-subtitle" v-if="search2 === null">其他员工</v-list-item-title>
              </div>

              <!-- 周视图 -->
              <v-virtual-scroll v-if="type === 'week'" bench="10"
                :items="selectedEvent.single === true ? filteredStaff.slice(3) : selectedEvent.detail" :item-height="63"
                height="300" width="400">
                <template v-slot:default="{ item, index }">

                  <v-list-item
                    @click="selectedEvent.single === true ? (selectedEmployee = item.id, dialog = true) : openSelected(item, index)">
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

              <!-- 月、日视图 -->
              <v-virtual-scroll v-if="type !== 'week'" :items="filteredStaff.slice(3)" :item-height="63" height="300"
                width="400" bench="10">
                <template v-slot:default="{ item }">
                  <v-list-item @click="selectedEmployee = item.id; dialog = true">
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
            </div>
            <v-container v-else style="height: 630px;">
              <v-row class="fill-height" align-content="center" justify="center">
                <v-col class="text-h5 text-center" cols="12">
                  正在加载推荐员工
                </v-col>
                <v-col cols="6">
                  <v-progress-linear color="primary accent-4" indeterminate rounded height="6"></v-progress-linear>
                </v-col>
              </v-row>
            </v-container>
          </v-card>

          <v-dialog v-model="dialog" width="350">
            <v-card>
              <v-card-title class="">
                确认将该班次替换为{{ selectedEmployee === 0 ? '开放班次' : '该员工' }}吗
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
  </div>
  <v-container v-else-if="!ready1" style="height: 80vh;">
    <v-row class="fill-height" align-content="center" justify="center">
      <v-col class="text-h5 text-center" cols="12">
        正在加载排班表
      </v-col>
      <v-col cols="6">
        <v-progress-linear color="primary accent-4" indeterminate rounded height="6"></v-progress-linear>
      </v-col>
    </v-row>
  </v-container>
  <v-container v-else style="height: 80vh;">
    <v-row class="fill-height" align-content="center" justify="center">
      <v-col class="text-h5 text-center" cols="12">
        没有店铺信息
      </v-col>
      <v-col cols="12" class="text-center">
        <v-btn outlined color="primary" to="/controlpanel/branches">点击跳转到分店页<v-icon>mdi-arrow-right</v-icon></v-btn>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import { getAllShop, getShopInfo } from '@/request/shop'
import { getEmployeeByShop, getEmployee } from '@/request/staff'
import { getUserAvatar, getUserInfo } from '@/request/user'
import { getLatestArr, getArrByEmployee, getRecommendedStaff, alterSchedule } from '@/request/rule'
import { formatDate } from '@/plugins/utility'
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
      ready1: false,
      ready2: true,
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

      user: {},

      branch: '',
      branches: [],
      size: null,
      staff: [],
      categories: {},
      filteredEmployee: [],
      startTimes: {},
      shopName: "",
      dialog: false,
      schedule: null,
      begin: '',

      recommended: [],


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
      const rawEvents = this.rawEvents.filter(r => {
        return r.detail.some(e => {
          if (e !== undefined)
            return e.position.indexOf(this.search1 || '') !== -1 || e.username.indexOf(this.search1 || '') !== -1 || e.uid.indexOf(this.search1 || '') !== -1
        })
      });
      rawEvents.forEach(r => {
        r.detail = r.detail.filter(e => {
          if (e !== undefined)
            return e.position.indexOf(this.search1 || '') !== -1 || e.username.indexOf(this.search1 || '') !== -1 || e.uid.indexOf(this.search1 || '') !== -1
        })
        r.name = r.detail.length + '个员工'
      })
      const events = this.events.filter(e => {
        return e.position.indexOf(this.search1 || '') !== -1 || e.name.indexOf(this.search1 || '') !== -1 || e.uid.indexOf(this.search1 || '') !== -1
      });
      return this.type === 'week' && (this.$store.state.isManager || this.$store.state.isShopManager) ? rawEvents : events
    },
    filteredStaff() {
      return this.staff.filter(p => {
        return (p.uid.indexOf(this.search2 || '') !== -1 || p.username.indexOf(this.search2 || '') !== -1 || p.position.indexOf(this.search2 || '') !== -1) && p.id !== 0 && !(p.id in this.recommended)
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
      for (let i = 0; i < this.something_data.weeks.length; i++) {//周
        for (let j = 0; j < this.something_data.weeks[0].data.length; j++) {//天
          for (let x = 0; x < this.something_data.weeks[0].data[0].length; x++) {//时
            /*console.log("i="+i)
            console.log(this.something_data.weeks.length)
            console.log("j="+j)
            console.log( this.something_data.weeks[0].data.length)
            console.log("x="+x)
            console.log(this.something_data.weeks[0].data[0].length)
            console.log(this.something_data.weeks[i].data[j][x].beginTime)
            console.log(this.something_data.weeks[i].data[j][x].employees)*/
            let weeksData = {
              "id": this.something_data.id,
              "shop": this.something_data.shop,
              "manager": this.something_data.manager,
              "createAt": formatDate(this.something_data.createAt),
              "isActive": this.something_data.isActive,
              "useRule": this.something_data.useRule,
              "startAt": formatDate(this.something_data.startAt),
              "endAt": formatDate(this.something_data.endAt),
              "WeekId": i + 1,
              "WeekStartAt": formatDate(this.something_data.weeks[i].startAt),
              "WeekEndAt": formatDate(this.something_data.weeks[i].endAt),
              "DayId": j + 1,
              "beginTime": formatDate(this.something_data.weeks[i].data[j][x].beginTime),
              "employeesId": this.something_data.weeks[i].data[j][x].employees
            }
            this.json_data.push(weeksData)

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
      for (const b of this.branches) {
        if (b.id === this.branch) {
          this.size = b.size
        }
      }
      const staff = (await getEmployeeByShop(this.branch)).data.data;
      staff.forEach(s => {
        s.avatar = require('../assets/defaultAvatar.png')
        s.color = this.colors[this.rnd(0, this.colors.length - 1)]
      })

      this.staff = staff


      for (const s of this.staff) {
        const avatar = await getUserAvatar(s.id);
        if (avatar.status === 200) {
          s.avatar = URL.createObjectURL(avatar.data)
        }
        else {
          s.avatar = require('../assets/defaultAvatar.png')
        }
      }
      this.staff.push({
        avatar: null,
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
      let events;
      let weeks;
      try {
        this.events = []
        this.rawEvents = []
        if (this.$store.state.isManager || this.$store.state.isShopManager) {
          events = (await getLatestArr(this.branch)).data;
          console.log(events)
          this.schedule = events.data.id
          this.begin = events.data.startAt

          this.something_data = events.data
          weeks = events.data.weeks;
          const rawEvents = [];
          for (let week of weeks) {
            for (let day of week.data) {
              if (day.some(item => item !== null)) {
                const employees = [];
                const categories = [];
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

                            let time = new Date(start)

                            if (start === e.end[e.end.length - 1]) {

                              if (time.getMinutes() === 30 && time.getHours() === 23) {
                                e.end[e.end.length - 1] += 1740000
                              }
                              else {
                                e.end[e.end.length - 1] += 1800000
                              }
                            }
                            else {
                              e.start.push(start)
                              if (time.getMinutes() === 30 && time.getHours() === 23) {
                                e.end.push(start + 1740000)
                              }
                              else {
                                e.end.push(start + 1800000)
                              }
                            }
                          }
                        })
                        if (!flag) {
                          const e = this.staff.find(item => item.id === employee)
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
                    const d = new Date(event.beginTime).getDay();
                    const t = new Date(event.beginTime).getHours();
                    let color;
                    if (d >= 1 && d <= 5) {
                      if (t < 9 || t >= 21) color = 'green'
                      else color = 'blue'
                    }
                    else {
                      if (t < 10 || t >= 22) color = 'green'
                      else color = 'blue'
                    }
                    const detail = [];

                    event.employees.forEach(employee => {
                      detail.push(this.staff.find(item => item.id === employee))
                    })

                    let beginTime = new Date(event.beginTime)
                    let endTime
                    if (beginTime.getMinutes() === 30 && beginTime.getHours() === 23) {
                      endTime = event.beginTime + 1740000
                    }
                    else {
                      endTime = event.beginTime + 1800000
                    }
                    rawEvents.push({
                      start: new Date(event.beginTime),
                      end: endTime,
                      name: event.employees.length + '个员工',
                      detail,
                      color,
                      timed: true,
                    })
                  }

                }

                let date;
                for (let e of employees) {
                  for (let i = 0; i < e.start.length; i++) {
                    categories.push(e.category)
                    date = formatDate(e.start[i])
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
                  for (let j = i + 1; j < categories.length; j++) {
                    if (categories[i] === categories[j]) {         //第一个等同于第二个，splice方法删除第二个
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
          weeks = events.weeks
          for (let week of weeks) {
            for (let day of week.data) {
              if (day.some(item => item !== null)) {
                const shifts = [];
                let flag = false
                for (let event of day) {
                  if (event !== null) {

                    const start = event.beginTime;

                    shifts.forEach(e => {
                      let time = new Date(start)

                      if (start === e.end[e.end.length - 1]) {

                        if (time.getMinutes() === 30 && time.getHours() === 23) {
                          e.end[e.end.length - 1] += 1740000
                        }
                        else {
                          e.end[e.end.length - 1] += 1800000
                        }
                      }
                      else {
                        e.start.push(start)
                        if (time.getMinutes() === 30 && time.getHours() === 23) {
                          e.end.push(start + 1740000)
                        }
                        else {
                          e.end.push(start + 1800000)
                        }
                      }

                    })

                    if (!flag) {
                      flag = true
                      shifts.push({
                        start: [start],
                        end: [start + 1800000],
                      })
                    }

                  }

                }
                for (let e of shifts) {
                  for (let i = 0; i < e.start.length; i++) {
                    this.events.push({
                      id: this.staff[0].id,
                      uid: this.staff[0].uid,
                      position: this.staff[0].position,
                      start: new Date(e.start[i]),
                      end: new Date(e.end[i]),
                      name: this.user.username,
                      color: `primary darken-1`,
                      avatar: this.user.avatar,
                      timed: true
                    })
                  }
                }
              }
            }

          }

        }
      }
      catch (e) {
        console.log(e)
        this.$emit('msg', '排班信息为空，请先生成一个排班')
      }
      this.ready1 = true

    },
    async changeBranch() {
      this.ready1 = false
      await this.getStaff()
      await this.getArr()
    },
    async openSelected(item) {
      let day = this.startTimes[formatDate(this.selectedEvent.start)]
      let selectedEmployee = day.find(i => i.id === item.id)
      let start, end
      for (let i = 0; i < selectedEmployee.start.length; i++) {
        if (this.selectedEvent.start >= selectedEmployee.start[i] && this.selectedEvent.end <= selectedEmployee.end[i]) {
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
      await this.getRecommendedStaff()

    },
    async getRecommendedStaff() {
      this.ready2=false
      let recommended = (await getRecommendedStaff({
        id: this.schedule,
        begin: this.begin,
        now: this.selectedEvent.start.getTime()
      })).data.data
      this.recommended = recommended

      this.staff.sort(this.compare(recommended))
      this.ready2=true

    },
    compare(recommended) {
      return function (a, b) {
        return recommended.indexOf(a.id) - recommended.indexOf(b.id)
      }
    },
    async changeSchedule() {
      let response = (await alterSchedule({
        schedule: this.schedule,
        previousEmployee: this.selectedEvent.id,
        currentEmployee: this.selectedEmployee,
        beginTime: this.selectedEvent.start.getTime()
      })).data
      if (response.code === 0) {
        this.$emit('msg', '替换成功')
        this.$router.go(0)
      }
      if (response.code === -1) {
        this.$emit('msg', '替换失败')
      }
      this.selectedOpen = false
    },
    getMsg(data) {
      this.$emit('msg', data)
    },
  },

  watch: {
    async selectedOpen(newV) {
      if (newV && this.type !== 'week') {
        await this.getRecommendedStaff()
      }
    }
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
          this.branches = []
          this.ready1 = true
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
        user.avatar = URL.createObjectURL(avatar.data)
      }
      else if (avatar.status === 204) {
        user.avatar = require('../assets/defaultAvatar.png')
      }
      this.user = user

      let employee = (await getEmployee()).data.data
      let shop = (await getShopInfo(employee.shop)).data.data
      this.staff.push(employee)
      this.branch = shop.id
      this.shopName = shop.name
      await this.getArr()
    }

  }

}
</script>

<style></style>
