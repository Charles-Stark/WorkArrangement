<template>
    <div>
        <v-toolbar :color="$vuetify.theme.dark === false ? 'white' : '#121212'" flat
            v-if="$store.state.isManager || $store.state.isShopManager">
            <v-select v-model="branch" :items="branches" item-text="name" item-value="id" solo
                interval-minutes="60" no-data-text="没有数据" dense flat hide-details style="max-width:140px;min-width:120px"
                @change="changeBranch()" v-if="$store.state.isManager"></v-select>
            <span v-if="!$store.state.isManager" class="text-h6 ml-3">{{ shopName }}</span>
            <v-spacer></v-spacer>

            <v-dialog offset-y width="350" persistent v-model="dialog">
                <template v-slot:activator="{ on, attrs }">
                    <v-btn color="primary" dark v-bind="attrs" v-on="on" class="mr-2" outlined depressed>
                        上传用户评价
                    </v-btn>
                </template>
                <v-card>
                    <v-card-title>
                        上传文件
                    </v-card-title>
                    <v-card-text>
                        <v-form ref="uploadFileForm">
                            <v-file-input v-model="file" :rules="noneEmptyRules"
                                accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel"
                                show-size></v-file-input>
                        </v-form>
                    </v-card-text>
                    <v-card-actions>
                        <v-spacer></v-spacer>
                        <v-btn color="grey" outlined @click="dialog = false">
                            返回
                        </v-btn>
                        <v-btn color="primary" @click="upload()">
                            上传
                        </v-btn>
                    </v-card-actions>
                </v-card>
            </v-dialog>
        </v-toolbar>

        <v-row>
            <v-col cols="12">
                <e-charts style="height: 300px;width: 100%;" autoresize :option="option" />
            </v-col>
        </v-row>
    </div>
</template>

<script>
import { getAllShop, getShopInfo } from '@/request/shop'
import { getEmployee } from '@/request/staff'
import { uploadFile, getAnalysis } from '@/request/survey'
export default {

    data() {
        return {
            file: null,
            branches: [],

            analysis: {
                nps: 0,
                optimizedValue: 0,
                q1: 0,
                q2: 0,
                q3: 0,
                q4: 0,
                q5: 0,
                q6: 0,
                satisfaction: 0
            },

            branch: '',
            shopName: '',
            ready: false,
            dialog: false,


            noneEmptyRules: [
                v => !!v || '不能为空',
            ]
        }
    },

    computed: {
        option() {
            return {
                xAxis: {
                    data: ['服务质量', '门店环境', '整体分区布局', '卫生状况', '人员数量', '推荐指数']
                },
                yAxis: {},
                series: [
                    {
                        type: 'bar',
                        data: [this.analysis.q1, this.analysis.q2, this.analysis.q3, this.analysis.q4, this.analysis.q5, this.analysis.q6]
                    }
                ]
            }
        }

    },

    methods: {
        async changeBranch() {
            let response = (await getAnalysis(this.branch)).data.data
            if (response) {
                response.q6/=2
                this.analysis = response
            }
            else {
                this.analysis = {}
                this.$emit('msg', '没有数据，请先上传分析文件')
            }
        },
        async upload() {
            if (this.$refs.uploadFileForm.validate()) {
                const formData = new FormData();
                formData.append('shopId', this.branch)
                formData.append('excelFile', this.file)
                let response = (await uploadFile(formData)).data
                if (response.code === 0) {
                    this.$emit('msg', '上传成功')
                    this.analysis = response.data
                    this.analysis.q6 /= 2
                } else if (response.code === -1) {
                    this.$emit('msg', '上传失败')
                }
            }

        }
    },

    async mounted() {
        console.log(this)
        if (this.$store.state.isManager) {
            let branches = (await getAllShop()).data.data
            if (branches.length !== 0) {
                this.branch = branches[0].id
            } else {
                branches = []
            }
            this.branches = branches
        }
        else if (this.$store.state.isShopManager) {
            let employee = (await getEmployee()).data.data
            let shop = (await getShopInfo(employee.shop)).data.data
            this.branch = shop.id
            this.shopName = shop.name
        }

        await this.changeBranch()
    }



}
</script>
