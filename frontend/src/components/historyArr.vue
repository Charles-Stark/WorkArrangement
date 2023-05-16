<template>
    <div>
        <v-card v-if="ready" style="height: 100vh;" class="py-3 px-5" flat
            :color="$vuetify.theme.dark === false ? 'white' : '#121212'">
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

                    <v-text-field v-model="search1" clearable dense flat solo-inverted hide-details
                        prepend-inner-icon="mdi-magnify" v-if="$vuetify.breakpoint.mdAndUp" class="mx-2"
                        label="姓名/工号/岗位"></v-text-field>

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

                    <download-excel :data="json_data" :fields="json_fields" name="排班导出结果.xls" class="ml-2">
                        <v-btn text outlined @click="GetExcel">导出/打印</v-btn>
                    </download-excel>

                    <v-btn @click="close()" text outlined class="ml-2">
                        返回上级
                        <v-icon>mdi-arrow-u-left-top</v-icon>
                    </v-btn>

                </v-toolbar>
            </v-sheet>
            <v-sheet>
                <v-calendar ref="calendar" v-model="focus" color="primary" :events="scheduleType"
                    :event-color="getEventColor" first-interval="6" interval-count="18" locale="zh-cn" :type="type"
                    @click:event="showEvent" @click:date="viewDay" event-overlap-mode="column" @change="updateRange"
                    :categories="filteredEmployee">
                </v-calendar>

                <v-menu v-model="selectedOpen" :close-on-content-click="false" :activator="selectedElement" offset-y>
                    <v-card min-width="350px" flat>
                        <v-toolbar :color="selectedEvent.color" dark>
                            <!-- <v-list-item-avatar v-if="type !== 'week'">
                <v-img :src="selectedEvent.avatar"></v-img>
                </v-list-item-avatar> -->
                            <v-toolbar-title>{{ selectedEvent.name }}</v-toolbar-title>
                            <v-toolbar-title class="ml-2">{{ selectedEvent.position }}</v-toolbar-title>

                            <v-toolbar-title class="ml-1" v-if="selectedEvent.single === true">{{ selectedEvent.showStart +
                                ' - '
                                + selectedEvent.showEnd }}</v-toolbar-title>

                            <v-spacer></v-spacer>

                            <v-btn icon
                                v-if="selectedEvent.single === true || type !== 'week'"><v-icon>mdi-open-in-app</v-icon></v-btn>
                        </v-toolbar>


                        <!-- 周视图 -->
                        <v-virtual-scroll :items="selectedEvent.detail" :item-height="63" height="300" width="400">
                            <template v-slot:default="{ item }">

                                <v-list-item>
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

                </v-menu>


            </v-sheet>

        </v-card>
        <v-card v-else>
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
import { getEmployeeByShop } from '../request/staff'
import { getUserAvatar } from '../request/user'
import { getRule, getArrById } from '../request/rule'

export default {
    props: ['shop', 'id'],
    data() {
        return {
            focus: '',
            type: 'month',
            ready: false,
            search1: null,
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

            staff: [],
            categories: {},
            filteredEmployee: [],
            startTimes: {},
            shopName: "",
            rules: [],

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
            if (this.type === 'week') {
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
            var staff = (await getEmployeeByShop(this.shop)).data.data
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
            var events = (await getArrById(this.id)).data
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
        close() {
            this.$emit('close')
        }
    },

    async mounted() {
        // this.$refs.calendar.checkChange()
        await this.getStaff()
        await this.getArr()
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
