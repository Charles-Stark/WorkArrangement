<template>
    <v-card>
        <v-card-title>
            <span class="text-h5">新增分店</span>
        </v-card-title>
        <v-card-text>
            <v-container>
                <v-form ref="addShopForm" lazy-validation>
                    <v-row>

                        <v-col cols="12">
                            <v-text-field label="分店地址*" v-model="newShop.address" :rules="rules.noneEmptyRules"
                                prepend-icon="mdi-map-marker" required></v-text-field>
                        </v-col>

                        <v-col cols="12" sm="6">
                            <v-text-field label="分店名称*" v-model="newShop.name" :rules="rules.nameRules" counter="10"
                                prepend-icon="mdi-store" required></v-text-field>
                        </v-col>

                        <v-col cols="12" sm="6">
                            <v-text-field label="分店面积*" type="number" v-model="newShop.size" :rules="rules.noneEmptyRules"
                                prepend-icon="mdi-domain" required
                                @blur="newShop.size = newShop.size <= 0 ? '' : newShop.size"></v-text-field>
                        </v-col>

                    </v-row>
                </v-form>
            </v-container>
            <small>*为必填项</small>
        </v-card-text>
        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="primary" text @click="$emit('close'); $refs.addShopForm.reset()">
                关闭
            </v-btn>
            <v-btn color="primary" @click="addShop()">
                提交
            </v-btn>
        </v-card-actions>
    </v-card>
</template>

<script>
import { createShop } from '@/request/shop'
export default {
    props: ['shop', 'branches'],
    data() {
        return {
            newShop: {
                name: '',
                address: '',
                size: null,
            },

            rules: {
                nameRules: [
                    v => !!v || '名称不能为空',
                    v => (v && v.length <= 10) || '姓名长度不能大于10',
                ],
                noneEmptyRules: [
                    v => !!v || '不能为空',
                ]

            },

        }
    },

    methods: {
        addShop() {
            if (this.$refs.addShopForm.validate()) {
                createShop({
                    name: this.newShop.name,
                    address: this.newShop.address,
                    size: this.newShop.size,
                }).then(res => {
                    if (res.data.code === 0) {
                        this.$emit('msg', '添加门店成功')
                        this.$router.go(0)
                    }

                }).catch(() => {
                    this.$emit('msg', '网络错误')
                })
            }
        },
    },



}
</script>
