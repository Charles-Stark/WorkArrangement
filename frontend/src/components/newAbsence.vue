<template>
    <v-card>
        <v-card-title>
            <span class="text-h5">新建请假</span>
        </v-card-title>
        <v-card-text>
            <v-container>
                <v-form ref="absenceForm" lazy-validation>
                    <v-row>
                        <v-col cols="12">
                            <v-menu ref="menu" v-model="menu" :close-on-content-click="false" transition="scale-transition"
                                offset-y min-width="auto">
                                <template v-slot:activator="{ on, attrs }">
                                    <v-text-field v-model="date" label="选择请假日期*" prepend-icon="mdi-calendar" readonly
                                        required v-bind="attrs" v-on="on" clearable :rules="noneEmptyRules"></v-text-field>
                                </template>
                                <v-date-picker v-model="date" locale="zh-cn"
                                    :min="(new Date(Date.now() - (new Date()).getTimezoneOffset() * 60000)).toISOString().substr(0, 10)"
                                    @change="save"></v-date-picker>
                            </v-menu>
                        </v-col>
                        <v-col cols="12">
                            <v-text-field v-model="reason" label="请假原因*" required :rules="noneEmptyRules"
                                prepend-icon="mdi-text-long"></v-text-field>
                        </v-col>
                        <v-col cols="12">
                            <v-file-input v-model="attachment" chips accept="image/*" label="附件"
                                truncate-length="15"></v-file-input>
                        </v-col>
                    </v-row>
                </v-form>
            </v-container>
            <small>*为必填项</small>
        </v-card-text>
        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="primary" text @click="$emit('close')" large>
                关闭
            </v-btn>
            <v-btn color="primary" @click="submit()" large>
                提交
            </v-btn>
        </v-card-actions>
    </v-card>
</template>

<script>
export default {

    data() {
        return {
            menu: false,
            date: null,
            reason: '',
            attachment: null,


            noneEmptyRules: [
                v => !!v || '不能为空',
            ],

        }
    },
    watch: {
        menu(val) {
            val && setTimeout(() => (this.activePicker = 'YEAR'))
        },
    },
    methods: {
        submit(){
            if(this.$refs.absenceForm.validate()){
                this.$emit('msg','申请成功')
                this.$emit('close')
            }
        },
        save(date) {
            this.$refs.menu.save(date)
        },
    },
}
</script>
