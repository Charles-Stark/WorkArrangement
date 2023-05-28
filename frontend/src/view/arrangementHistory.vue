<template>
    <v-container>
        <v-row>
            <v-col cols="2">
                <v-select v-model="branch" :items="branches" item-text="name" item-value="id" solo-inverted
                    interval-minutes="60" no-data-text="没有数据" dense flat hide-details
                    style="max-width:140px;min-width:120px" @change="changeBranch()"
                    v-if="$store.state.isManager"></v-select>
            </v-col>
            <v-spacer></v-spacer>
            <v-col cols="2">
                <v-btn color="secondary" outlined to="/controlpanel/arrange">返回</v-btn>
            </v-col>
        </v-row>
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

                        <historyArr :shop="s.shop" :id="s.id" @close="s.dialog = false" />

                    </v-dialog>

                    <v-dialog v-model="s.deleteDialog" width="450">
                        <template v-slot:activator="{ on, attrs }">
                            <v-btn color="error" @click="s.deleteDialog = true" v-bind="attrs" v-on="on"
                                class="ml-1">删除</v-btn>
                        </template>

                        <v-card>
                            <v-card-title class="text-h5">
                                删除排班
                            </v-card-title>

                            <v-card-text>
                                确定要删除该条排班数据吗，此操作不可撤销
                            </v-card-text>

                            <v-card-actions>
                                <v-spacer></v-spacer>
                                <v-btn color="primary" text @click="s.deleteDialog = false">
                                    取消
                                </v-btn>
                                <v-btn color="error" text @click="deleteArr(s.id)">
                                    确认删除
                                </v-btn>

                            </v-card-actions>
                        </v-card>
                    </v-dialog>

                </v-card-actions>
            </v-row>
        </v-card>
    </v-container>
</template>
<script>
import { getAllShop, getShopInfo } from '@/request/shop'
import { getAllArr, deleteArr } from '@/request/rule'
import { getEmployee } from '@/request/staff'
import { formatDate } from '@/plugins/utility'


import historyArr from '../components/historyArr.vue'


export default {
    components: {
        historyArr
    },
    data: () => ({
        branch: '',
        branches: [],
        staff: [],
        schedules: [],
    }),
    methods: {

        async changeBranch() {
            let schedules = (await getAllArr(this.branch)).data.data
            schedules.forEach(s => {
                let time = new Date(s.createAt)
                s.createAt = formatDate(s.createAt) + ' ' + (time.getHours() < 10 ? '0' + time.getHours() : time.getHours()) + ':' + (time.getMinutes() < 10 ? '0' + time.getMinutes() : time.getMinutes())
                s.startAt = formatDate(s.startAt)
                s.endAt = formatDate(s.endAt)
                s.dialog = false
                s.deleteDialog = false
            })
            this.schedules = schedules.reverse()
        },
        deleteArr(id) {
            deleteArr(id).then(res => {
                if (res.data.code === 0) {
                    this.$emit('msg', '删除成功')
                    this.$router.go(0)
                }
                else if (res.data.code === 1) {
                    this.$emit('msg', '删除失败')
                }
            }).catch(() => {
                this.$emit('msg', '网络错误')
            })
        }
    },
    async mounted() {

        if (this.$store.state.isManager) {
            this.branches = (await getAllShop()).data.data
            this.branch = this.branches[0].id
        }
        else if (this.$store.state.isShopManager) {
            let employee = (await getEmployee()).data.data
            let shop = (await getShopInfo(employee.shop)).data.data
            this.branch = shop.id
        }

      await this.changeBranch()


    }


}
</script>

<style scoped>
.schedules {
    border-left: 4px solid var(--v-primary-base);
}
</style>