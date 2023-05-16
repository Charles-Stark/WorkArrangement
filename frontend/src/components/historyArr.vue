<template>
    <div>
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
                <v-calendar ref="calendar" v-model="focus" color="primary" :events="scheduleType"
                    :event-color="getEventColor" first-interval="6" interval-count="18" locale="zh-cn" :type="type"
                    @click:event="showEvent" @click:date="viewDay" event-overlap-mode="column" @change="updateRange"
                    :categories="filteredEmployee">
                </v-calendar>


            </v-sheet>

        </v-card>
        <v-card  v-else>
            <v-container style="height: 100vh;">
            <v-row class="fill-height" align-content="center" justify="center">
                <v-col class="text-h5 text-center" cols="12">
                    正在加载排班表
                </v-col>
                <v-col cols="6">
                    <v-progress-linear color="primary" indeterminate rounded height="6"></v-progress-linear>
                </v-col>
            </v-row>
        </v-container>
        </v-card>
        


    </div>
</template>
  
<script>
import { getAllShop, getShopInfo } from '../request/shop'
import { getEmployeeByShop, getEmployee } from '../request/staff'
import { getUserAvatar, getUserInfo } from '../request/user'
import { getLatestArr, getRule, getArrByEmployee } from '../request/rule'

export default {
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
                "排班序号": "id",
                "店铺序号": "shop",
                "管理员序号": "manager",
                "创建时间": "createAt",
                "是否生效": "isActive",
                "使用规则序号": "useRule",
                "表生效时间": "startAt",
                "表结束时间": "endAt",
                "work_emp": "work_emp"
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
                    work_emp: "",
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
            if (this.something_data != null) {
                console.log(this.something_data)
                console.log(typeof this.something_data)
                console.log(this.something_data.length)
                console.log(this.something_data.id)
                this.json_data[0].id = this.something_data.id
                this.json_data[0].shop = this.something_data.shop
                this.json_data[0].manager = this.something_data.position
                this.json_data[0].createAt = this.something_data.createAt
                this.json_data[0].isActive = this.something_data.isActive
                this.json_data[0].useRule = this.something_data.useRule
                this.json_data[0].startAt = this.something_data.startAt
                this.json_data[0].endAt = this.something_data.endAt
                for (var i = 0; i < this.something_data.week.length; i++) {    //week
                    for (var j = 0; j < this.something_data.week.data.length; j++) {   //day
                        for (var x = 0; x < this.something_data.week.data.employees.length; x++) {   //day_employees

                        }
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
