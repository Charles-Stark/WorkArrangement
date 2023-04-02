<template>
    <v-card flat>
        <v-card-title>
            <span class="text-h5">修改偏好</span>
        </v-card-title>
        <v-card-text>
            <v-container>
                <v-row>

                    <v-col cols="12">
                        <v-select :items="week" item-text="key" item-value="value" small-chips clearable label="工作日偏好"
                            v-model="favor.workingDay" no-data-text="没有数据" multiple required></v-select>
                    </v-col>

                    <v-col cols="12">
                        <v-menu ref="menu" v-model="menu" :close-on-content-click="false" :nudge-right="40"
                            :return-value.sync="favor.workingHours" transition="scale-transition" offset-y
                            :max-width="$vuetify.breakpoint.xsOnly ? '290px' : '580px'"
                            :min-width="$vuetify.breakpoint.xsOnly ? '290px' : '580px'">
                            <template v-slot:activator="{ on, attrs }">
                                <v-text-field v-model="favor.workingHours" clearable label="工作时间偏好" readonly v-bind="attrs"
                                    v-on="on"></v-text-field>
                            </template>

                            <v-card>
                                <v-time-picker v-model="startTime" :max="endTime" format="24hr"
                                    :allowed-minutes="v => !(v % 30)" scrollable></v-time-picker>
                                <v-time-picker v-model="endTime" :min="startTime" format="24hr"
                                    :allowed-minutes="v => !(v % 30)" scrollable></v-time-picker>
                            </v-card>
                        </v-menu>
                    </v-col>

                    <v-col cols="12">
                        <v-text-field label="班次时长" v-model="favor.durationOfShift" type="number"
                            @blur="favor.durationOfShift <= 0 ? favor.durationOfShift = '' : favor.durationOfShift = favor.durationOfShift"
                            required></v-text-field>
                    </v-col>

                    <v-col cols="12">
                        <v-text-field label="周工作时间上限" v-model="favor.durationOfWeek" type="number"
                            @blur="favor.durationOfWeek <= 0 ? favor.durationOfWeek = '' : favor.durationOfWeek = favor.durationOfWeek"
                            required></v-text-field>
                    </v-col>

                </v-row>
            </v-container>
        </v-card-text>
        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="primary" text @click="$emit('close')" large>
                关闭
            </v-btn>
            <v-btn color="primary" @click="editFavor()" large>
                提交
            </v-btn>
        </v-card-actions>
    </v-card>
</template>

<script>
import { editFavor, getEmployee } from '../request/staff'

export default {
    props: ['id'],
    data: () => ({
        menu: false,

        user: {
            id: '',
            avatar: '',
            userName: '',
            email: '',
        },


        favor: {
            durationOfShift: null,
            durationOfWeek: null,
            workingDay: [],
            workingHours: []
        },

        week: [
            { value: "1", key: '星期一' },
            { value: "2", key: '星期二' },
            { value: "3", key: '星期三' },
            { value: "4", key: '星期四' },
            { value: "5", key: '星期五' },
            { value: "6", key: '星期六' },
            { value: "7", key: '星期天' },

        ],

        startTime: null,
        endTime: null,

    }),


    methods: {
        editFavor() {
            editFavor({
                id: this.id || this.$store.state.userId,
                durationOfShift: this.favor.durationOfShift,
                durationOfWeek: this.favor.durationOfWeek,
                workingDay: this.favor.workingDay !== null ? this.favor.workingDay.toString() : '',
                workingHours: this.favor.workingHours !== null ? this.favor.workingHours.toString() : ''
            }).then(res => {
                if (res.data.code === 0) {
                    this.$emit('msg', '更新偏好成功')
                    this.$router.go(0)
                }
                else {
                    this.$emit('msg', '更新偏好失败')
                }
            }).catch((err) => {
                console.log(err)
                this.$emit('msg', '网络错误')
            })
        },

    },

    watch: {
        menu: {
            immediate: true,
            handler(newV) {
                if (this.favor.workingHours === null) this.favor.workingHours = []
                if (newV === false) {
                    if (this.startTime !== null && this.endTime !== null && this.startTime !== this.endTime)
                        this.favor.workingHours.push(this.startTime + '-' + this.endTime)
                    this.startTime = this.endTime = null
                }
            }
        }
    },

    async mounted() {
        try {
            var employee = (await getEmployee(this.id)).data.data
            this.favor.durationOfShift = employee.durationOfShift
            this.favor.durationOfWeek = employee.durationOfWeek
            this.favor.workingDay = (employee.workingDay !== '' & employee.workingDay !== null) ? employee.workingDay.split(',') : []
            this.favor.workingHours = (employee.workingHours !== '' & employee.workingHours !== null) ? employee.workingHours.split(',') : []
        } catch (err) {
            this.$emit('msg', '网络错误')
        }
    }



}
</script>