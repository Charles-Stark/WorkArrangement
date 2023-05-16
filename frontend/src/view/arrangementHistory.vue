<template>
    <v-container>
        <v-card v-for="s of schedules" :key="s.id" class="my-5 pa-3 schedules mx-auto" max-width="1000">
            <v-row>
                <v-col cols="5">
                    <v-card-subtitle class="grey--text caption">创建时间</v-card-subtitle>
                    <v-card-title>{{ s.createAt }}</v-card-title>
                </v-col>
                <v-col cols="4">
                    <v-card-subtitle class="grey--text caption">排班范围</v-card-subtitle>
                    <v-card-title>{{ s.startAt + ' 至 ' + s.endAt }}</v-card-title>
                </v-col>
                <v-spacer></v-spacer>
                <v-card-actions class="mr-5">


                    <v-dialog v-model="s.dialog" width="1400" fullscreen>
                        <template v-slot:activator="{ on, attrs }">
                            <v-btn v-bind="attrs" v-on="on" color="primary" outlined>查看</v-btn>
                        </template>

                        <historyArr :shop="s.shop" :id="s.id" @close="s.dialog=false"/>

                    </v-dialog>


                    <v-btn color="error" class="ml-1">删除</v-btn>
                </v-card-actions>
            </v-row>
        </v-card>
    </v-container>
</template>
<script>
import { getAllShop } from '../request/shop'
import { getAllArr } from '../request/rule'
import historyArr from '../components/historyArr.vue'


export default {
    components: {
        historyArr
    },
    data: () => ({
        branch: '',
        branches: '',
        staff: [],
        schedules: [],
    }),
    methods: {
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
    },
    mounted() {
        getAllShop().then(async res => {
            this.branches = res.data.data
            if (this.branches.length !== 0) {
                this.branch = this.branches[0].id
                var schedules = (await getAllArr(this.branch)).data.data
                console.log(schedules)

                schedules.forEach(s => {
                    let time = new Date(s.createAt)
                    s.createAt = this.formatDate(s.createAt) + ' ' + (time.getHours() < 10 ? '0' + time.getHours() : time.getHours()) + ':' + (time.getMinutes() < 10 ? '0' + time.getMinutes() : time.getMinutes())
                    s.startAt = this.formatDate(s.startAt)
                    s.endAt = this.formatDate(s.endAt)
                    s.dialog=false
                })
                this.schedules = schedules.reverse()


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

<style scoped>
.schedules {
    border-left: 4px solid rgb(38, 145, 245);
}
</style>