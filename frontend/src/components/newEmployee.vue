<template>
    <v-card>
        <v-card-title>
            <span class="text-h5">新增员工</span>
        </v-card-title>
        <v-card-text>
            <v-container>
                <v-form ref="addEmployeeForm" lazy-validation>
                    <v-row>
                        <v-col cols="12"><span class="text-subtitle-1 mt-3">员工信息</span></v-col>

                        <v-col cols="12">
                            <v-text-field label="邮箱*" v-model="newEmployee.email" :rules="rules.emailRules"
                                prepend-icon="mdi-email" required></v-text-field>
                        </v-col>

                        <v-col cols="12" sm="6">
                            <v-text-field label="姓名*" counter="10" v-model="newEmployee.username" :rules="rules.nameRules"
                                prepend-icon="mdi-account" required></v-text-field>
                        </v-col>

                        <v-col cols="12" sm="6">
                            <v-text-field label="薪资*" type="number" v-model="newEmployee.salary"
                                :rules="rules.noneEmptyRules"
                                @blur="newEmployee.salary = newEmployee.salary <= 0 ? '' : newEmployee.salary"
                                prepend-icon="mdi-cash" required></v-text-field>
                        </v-col>

                        <v-col :cols="$store.state.isManager ? 6 : 12">
                            <v-select :items="['门店经理', '副经理', '小组长', '收银', '导购', '库房']" label="职位*"
                                prepend-icon="mdi-office-building-marker-outline" v-model="newEmployee.position"
                                :rules="rules.noneEmptyRules" required></v-select>
                        </v-col>

                        <v-col cols="12" sm="6">
                            <v-select :items="branches" item-text="name" item-value="id" label="所属分店*" no-data-text="没有数据"
                                v-if="$store.state.isManager" v-model="newEmployee.shop" :rules="rules.noneEmptyRules"
                                prepend-icon="mdi-store" required></v-select>
                        </v-col>

                        <v-col cols="12"><span class="text-subtitle-1">员工偏好</span></v-col>

                        <v-col cols="12" sm="6">
                            <v-select :items="week" item-text="key" item-value="value" small-chips clearable label="工作日偏好"
                                v-model="newEmployee.workingDay" no-data-text="没有数据" multiple required></v-select>
                        </v-col>

                        <v-col cols="12" sm="6">
                            <v-menu ref="menu" v-model="menu" :close-on-content-click="false" :nudge-right="40"
                                :return-value.sync="newEmployee.workingHours" transition="scale-transition" offset-y
                                :max-width="$vuetify.breakpoint.xsOnly ? '290px' : '580px'"
                                :min-width="$vuetify.breakpoint.xsOnly ? '290px' : '580px'">
                                <template v-slot:activator="{ on, attrs }">
                                    <v-text-field v-model="newEmployee.workingHours" clearable label="工作时间偏好" readonly
                                        v-bind="attrs" v-on="on"></v-text-field>
                                </template>

                                <v-card>
                                    <v-time-picker v-model="startTime" :max="endTime" format="24hr"
                                        :allowed-minutes="v => !(v % 30)" scrollable></v-time-picker>
                                    <v-time-picker v-model="endTime" :min="startTime" format="24hr"
                                        :allowed-minutes="v => !(v % 30)" scrollable></v-time-picker>
                                </v-card>
                            </v-menu>
                        </v-col>

                        <v-col cols="12" sm="6">
                            <v-text-field label="班次时长" v-model="newEmployee.durationOfShift" type="number"
                                required></v-text-field>
                        </v-col>

                        <v-col cols="12" sm="6">
                            <v-text-field label="周工作时间上限" v-model="newEmployee.durationOfWeek" type="number"
                                required></v-text-field>
                        </v-col>


                    </v-row>
                </v-form>
            </v-container>
            <small>*为必填项</small>
        </v-card-text>
        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="primary" text @click="$emit('close'); $refs.addEmployeeForm.reset()" large>
                关闭
            </v-btn>
            <v-btn color="primary" @click="addEmployee()" large>
                提交
            </v-btn>
        </v-card-actions>
    </v-card>
</template>

<script>
import { createEmployee } from '../request/staff'

export default {
    props: ['shop', 'branches'],
    data() {
        return {

            newEmployee: {
                email: '',
                username: '',
                position: '',
                shop: this.shop,
                salary: null,
                durationOfShift: null,
                durationOfWeek: null,
                workingDay: [],
                workingHours: []
            },

            startTime: null,
            endTime: null,
            menu: false,

            rules: {
                emailRules: [
                    v => !!v || '邮箱不能为空',
                    v => /.+@.+\..+/.test(v) || '邮箱格式错误',
                ],
                nameRules: [
                    v => !!v || '姓名不能为空',
                    v => (v && v.length <= 10) || '姓名长度不能大于10',
                ],
                noneEmptyRules: [
                    v => !!v || '不能为空',
                ]

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

        }
    },

    methods: {
        addEmployee() {
            if (this.$refs.addEmployeeForm.validate()) {
                createEmployee({
                    email: this.newEmployee.email,
                    username: this.newEmployee.username,
                    position: this.newEmployee.position,
                    shop: this.newEmployee.shop,
                    salary: this.newEmployee.salary,
                    durationOfShift: this.newEmployee.durationOfShift,
                    durationOfWeek: this.newEmployee.durationOfWeek,
                    workingDay: this.newEmployee.workingDay !== null ? this.newEmployee.workingDay.toString() : '',
                    workingHours: this.newEmployee.workingHours !== null ? this.newEmployee.workingHours.toString() : ''
                }).then(res => {
                    if (res.data.code === 0) {
                        this.$emit('msg', '添加成功')
                        this.$router.go(0)
                    }
                    if (res.data.code === -1) {
                        this.$emit('msg', '邮箱重复')
                    }
                }).catch(() => {
                    this.$emit('msg', '网络错误')
                })
            }
        },
    },

 

    watch: {
        menu: {
            immediate: true,
            handler(newV) {
                if (this.newEmployee.workingHours === null) this.newEmployee.workingHours = []
                if (newV === false) {
                    if (this.startTime !== null && this.endTime !== null && this.startTime !== this.endTime)
                        this.newEmployee.workingHours.push(this.startTime + '-' + this.endTime)
                    this.startTime = this.endTime = null
                }
            }
        }
    }

}
</script>
