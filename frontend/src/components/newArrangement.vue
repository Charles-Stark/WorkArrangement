<template>
  <div>
    <v-card :color="$vuetify.theme.dark === false ? 'white' : '#303030'" flat>
      <v-card-title>
        新建智能排班
      </v-card-title>
      <v-stepper v-model="e1" flat>
        <v-stepper-header>
          <v-stepper-step :complete="e1 > 1" step="1" class="h6">
            基本信息
          </v-stepper-step>
          <v-divider></v-divider>
          <v-stepper-step :complete="e1 > 1" step="2" class="h6">
            准备工作
          </v-stepper-step>
          <v-divider></v-divider>
          <v-stepper-step :complete="e1 > 2" step="3" class="h6">
            排班安排
          </v-stepper-step>
          <v-divider></v-divider>
          <v-stepper-step :complete="e1 > 3" step="4" class="h6">
            收尾工作
          </v-stepper-step>
        </v-stepper-header>
        <v-divider></v-divider>

        <v-stepper-items>

          <v-stepper-content step="1">
            <v-card flat>
              <v-card-subtitle>基本信息</v-card-subtitle>
              <v-card-text>
                <v-form ref="form1">
                  <v-row>
                    <v-col cols="6">
                      <v-menu ref="menu" v-model="menu" :close-on-content-click="false"
                        :return-value.sync="arrangeConfig.basic.start" transition="scale-transition" offset-y
                        min-width="auto">
                        <template v-slot:activator="{ on, attrs }">
                          <v-text-field v-model="arrangeConfig.basic.start" label="排班开始时间" prepend-icon="mdi-calendar"
                            readonly v-bind="attrs" v-on="on"></v-text-field>
                        </template>
                        <v-date-picker v-model="arrangeConfig.basic.start" no-title scrollable locale="zh-cn">
                          <v-spacer></v-spacer>
                          <v-btn text color="primary" @click="menu = false">
                            取消
                          </v-btn>
                          <v-btn text color="primary" @click="$refs.menu.save(arrangeConfig.basic.start)">
                            确定
                          </v-btn>
                        </v-date-picker>
                      </v-menu>
                    </v-col>
                    <v-col cols="6">
                      <v-text-field type="number" label="排班时长" v-model="arrangeConfig.basic.lasting" suffix="天"
                        @blur="arrangeConfig.basic.lasting <= 0 ? arrangeConfig.basic.lasting = 30 : arrangeConfig.basic.lasting = arrangeConfig.basic.lasting"></v-text-field>
                    </v-col>
                  </v-row>
                </v-form>
              </v-card-text>

              <v-card-subtitle>个性化</v-card-subtitle>
              <v-card-text>
                <v-form ref="form1">
                  <v-row>
                    <v-col cols="12">
                      <v-switch v-model="arrangeConfig.basic.balanced" label="均衡排班(使各员工被排班工作的工作总时长相对平均)"></v-switch>
                    </v-col>
                    <v-col cols="6">
                      <v-text-field type="number" label="最小月工作时长" v-model="arrangeConfig.basic.leastTime" suffix="h"
                        @blur="arrangeConfig.basic.leastTime <= 0 ? arrangeConfig.basic.leastTime = 120 : arrangeConfig.basic.leastTime = arrangeConfig.basic.leastTime"></v-text-field>
                    </v-col>
                    <v-col cols="6">
                      <v-text-field type="number" label="最大连续工作天数" v-model="arrangeConfig.basic.continuousTime" suffix="天"
                        @blur="arrangeConfig.basic.continuousTime <= 0 ? arrangeConfig.basic.continuousTime = 5 : arrangeConfig.basic.continuousTime = arrangeConfig.basic.continuousTime"></v-text-field>
                    </v-col>
                  </v-row>
                </v-form>
              </v-card-text>
              <v-card-actions>

                <v-btn color="primary" @click="$refs.form1.validate() ? e1++ : 0">
                  下一步
                </v-btn>

                <v-btn text class="ml-1" @click="$emit('close')">
                  取消
                </v-btn>

              </v-card-actions>
            </v-card>
          </v-stepper-content>

          <v-stepper-content step="2">
            <v-card>
              <v-card-text>
                <v-form ref="form2">
                  <v-row>
                    <v-col cols="5">
                      <v-subheader>营业前准备时间(0~2h):</v-subheader>
                    </v-col>
                    <v-col cols="2">
                      <v-text-field required v-model="arrangeConfig.pre.time" type="number" dense suffix="h" class="mt-3"
                        @blur="arrangeConfig.pre.time < 0 || arrangeConfig.pre.time > 2 ? arrangeConfig.pre.time = 1 : arrangeConfig.pre.time = arrangeConfig.pre.time"
                        :rules="noneEmptyRule"></v-text-field>
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col cols="3">
                      <v-subheader>人数调整:</v-subheader>
                    </v-col>
                    <v-col cols="4">
                      <v-subheader>{{ size }}m²(面积) ÷</v-subheader>
                    </v-col>
                    <v-col cols="3">
                      <v-text-field required v-model="arrangeConfig.pre.workLoad" dense label="人均工作量" type="number"
                        :rules="noneEmptyRule"
                        @blur="arrangeConfig.pre.workLoad <= 0 ? arrangeConfig.pre.workLoad = 100 : arrangeConfig.pre.workLoad = arrangeConfig.pre.workLoad"
                        class="mt-3" suffix="m²"></v-text-field>
                    </v-col>
                    <v-col cols="2">
                      <v-subheader>={{ Math.ceil(size / arrangeConfig.pre.workLoad) }}人</v-subheader>
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col class="ml-3">
                      <v-select :items="['门店经理', '副经理', '小组长', '收银', '导购', '库房']" label="限制职位(非必要)"
                        v-model="arrangeConfig.pre.position" clearable></v-select>
                    </v-col>
                  </v-row>
                </v-form>
              </v-card-text>
              <v-card-actions>

                <v-btn color="primary" @click="$refs.form2.validate() ? e1++ : 0">
                  下一步
                </v-btn>
                <v-btn color="primary" outlined @click="e1--">
                  上一步
                </v-btn>
                <v-btn text class="ml-1" @click="$emit('close')">
                  取消
                </v-btn>

              </v-card-actions>
            </v-card>
          </v-stepper-content>

          <v-stepper-content step="3">
            <v-card>
              <v-card-title>
                值班安排
              </v-card-title>
              <v-subheader class="ml-4">当客流量为0时需要安排值班</v-subheader>
              <v-card-text>
                <v-row>
                  <v-col cols="4">
                    <v-subheader>值班人数:</v-subheader>
                  </v-col>
                  <v-col cols="3">
                    <v-text-field required v-model="arrangeConfig.in.attendance" type="number" dense suffix="人"
                      class="mt-3"
                      @blur="arrangeConfig.in.attendance <= 0 ? arrangeConfig.in.attendance = 1 : arrangeConfig.in.attendance = arrangeConfig.in.attendance"
                      :rules="noneEmptyRule"></v-text-field>
                  </v-col>
                </v-row>
              </v-card-text>
              <v-card-title>
                班次安排
              </v-card-title>
              <v-card-text>
                <v-form ref="form3">
                  <v-row>
                    <v-col cols="3">
                      <v-subheader>店员需求数:</v-subheader>
                    </v-col>
                    <v-col cols="3">
                      <v-subheader>预测客流 ÷</v-subheader>
                    </v-col>
                    <v-col cols="3">
                      <v-text-field required v-model="arrangeConfig.in.num" dense label="单个员工服务人数" type="number"
                        :rules="noneEmptyRule" class="mt-3"
                        @blur="arrangeConfig.in.num <= 0 ? arrangeConfig.in.num = 3.8 : arrangeConfig.in.num = arrangeConfig.in.num"
                        suffix="人"></v-text-field>
                    </v-col>
                    <v-col cols="3">
                      <v-subheader>=需求员工数</v-subheader>
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col class="ml-3">
                      <v-select :items="['门店经理', '副经理', '小组长', '收银', '导购', '库房']" label="限制职位(非必要)"
                        v-model="arrangeConfig.in.position" clearable></v-select>
                    </v-col>
                  </v-row>
                </v-form>
              </v-card-text>
              <v-card-actions>

                <v-btn color="primary" @click="$refs.form3.validate() ? e1++ : 0">
                  下一步
                </v-btn>
                <v-btn color="primary" outlined @click="e1--">
                  上一步
                </v-btn>
                <v-btn text class="ml-1" @click="$emit('close')">
                  取消
                </v-btn>

              </v-card-actions>
            </v-card>
          </v-stepper-content>

          <v-stepper-content step="4">
            <v-card>
              <v-card-text>
                <v-form ref="form4">
                  <v-row>
                    <v-col cols="4">
                      <v-subheader>收尾工作时长(0~2h):</v-subheader>
                    </v-col>
                    <v-col cols="3">
                      <v-text-field required v-model="arrangeConfig.post.time" type="number" dense suffix="h" class="mt-3"
                        @blur="arrangeConfig.post.time < 0 || arrangeConfig.post.time > 2 ? arrangeConfig.post.time = 2 : arrangeConfig.post.time = arrangeConfig.post.time"
                        :rules="noneEmptyRule"></v-text-field>
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col cols="12">
                      <v-subheader>所需员工数:</v-subheader>
                    </v-col>
                    <v-col cols="4">
                      <v-subheader>{{ size }}m²(面积) ÷</v-subheader>
                    </v-col>
                    <v-col cols="2">
                      <v-text-field required v-model="arrangeConfig.post.workLoad" dense label="人均工作量" type="number"
                        :rules="noneEmptyRule"
                        @blur="arrangeConfig.post.workLoad <= 0 ? arrangeConfig.post.workLoad = 80 : arrangeConfig.post.workLoad = arrangeConfig.post.workLoad"
                        class="mt-3" suffix="m²"></v-text-field>
                    </v-col>
                    <v-col cols="1" class="mt-3">
                      +
                    </v-col>
                    <v-col cols="2">
                      <v-text-field required v-model="arrangeConfig.post.offset" dense label="偏移量" type="number"
                        :rules="noneEmptyRule"
                        @blur="arrangeConfig.post.offset <= 0 ? arrangeConfig.post.offset = 1 : arrangeConfig.post.offset = arrangeConfig.post.offset"
                        class="mt-3"></v-text-field>
                    </v-col>
                    <v-col cols="3">
                      <v-subheader>={{ Math.ceil(size / arrangeConfig.post.workLoad + parseInt(arrangeConfig.post.offset))
                      }}人</v-subheader>
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col class="ml-3">
                      <v-select :items="['门店经理', '副经理', '小组长', '收银', '导购', '库房']" label="限制职位(非必要)"
                        v-model="arrangeConfig.post.position" clearable></v-select>
                    </v-col>
                  </v-row>
                </v-form>
              </v-card-text>
              <v-card-actions>

                <v-btn color="primary" @click="$refs.form4.validate() ? submit() : 0">
                  提交
                </v-btn>
                <v-btn color="primary" outlined @click="e1--">
                  上一步
                </v-btn>
                <v-btn text class="ml-1" @click="$emit('close')">
                  取消
                </v-btn>

              </v-card-actions>
            </v-card>
          </v-stepper-content>
        </v-stepper-items>

      </v-stepper>
    </v-card>

  </div>
</template>

<script>
import { createArr } from '../request/rule'
export default {
  data() {
    return {
      e1: 1,

      menu: false,

      arrangeConfig: {
        basic: {
          start: (new Date(Date.now() - (new Date()).getTimezoneOffset() * 60000)).toISOString().substr(0, 10),
          lasting: 30,
          balanced: false,
          leastTime: 120,
          continuousTime: 5
        },
        pre: {
          time: 1,
          workLoad: 100,
          position: null
        },
        in: {
          num: 3.8,
          position: null,
          attendance: 1
        },
        post: {
          time: 2,
          workLoad: 80,
          offset: 1,
          position: null,
        }
      },

      noneEmptyRule: [
        v => v !== '' || '必填项不能为空',
      ],

    }
  },
  props: ['size', 'branch'],
  computed: {

  },
  methods: {
    submit() {
      createArr({
        shop: this.branch,
        manager: this.$store.state.userId,
        prepareTime: this.arrangeConfig.pre.time,
        prepareWorkloadPerPerson: this.arrangeConfig.pre.workLoad,
        preparePosition: this.arrangeConfig.pre.position,
        maxServiceNumber: this.arrangeConfig.in.num,
        servicePosition: this.arrangeConfig.in.position,
        numberOnDuty: this.arrangeConfig.in.attendance,
        closingTime: this.arrangeConfig.post.time,
        closingWorkloadPerPersonU: this.arrangeConfig.post.workLoad,
        closingWorkloadPerPersonV: this.arrangeConfig.post.offset,
        closingPosition: this.arrangeConfig.post.position,
        startDate: this.arrangeConfig.basic.start + ' 00:00:00',
        lastingDays: this.arrangeConfig.basic.lasting,
      }).then((res) => {
        if (res.data.code === 0) {
          this.$emit('msg', '排班成功')
          this.$router.go(0)
        }
        else {
          this.$emit('msg', '排班失败')
        }
      }).catch(() => {
        this.$emit('msg', '网络错误')
      })
    }

  }

}
</script>
