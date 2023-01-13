<template>
  <v-data-iterator :items="items" :search="search" hide-default-footer no-results-text="没有搜索结果" no-data-text="没有数据">
    <template v-slot:header>
      <v-toolbar class="mb-1" rounded :color="$vuetify.theme.dark === false ? 'white' : '#121212'">
        <v-btn large color="secondary" class="mr-5" outlined>
          <v-icon>mdi-plus</v-icon>
          新增分店
        </v-btn>
        <template>
          <v-text-field v-model="search" clearable flat solo-inverted hide-details prepend-inner-icon="mdi-magnify"
            label="搜索"></v-text-field>
        </template>
      </v-toolbar>
    </template>

    <template v-slot:default="props">
      <v-row class="px-5 py-5">
        <v-col v-for="item in props.items" :key="item.address" cols="12" md="6" lg="4">

          <v-card elevation="10" max-width="400" class="mx-auto">

            <v-carousel :continuous="false" show-arrows-on-hover hide-delimiters delimiter-icon="mdi-minus"
              height="230">

              <v-carousel-item>
                <v-card class="mx-auto " color="secondary" max-width="600">
                  <v-card-text>
                    <v-sheet color="rgba(0, 0, 0, .12)">
                      <span class="text-h6 ml-1 mt-1 white--text">客流量预测概览</span>
                      <v-sparkline :value="item.value1" color="rgba(255, 255, 255, .7)" height="120" padding="24"
                        stroke-linecap="round" smooth>
                        <template v-slot:label="item">
                          {{ item.value }}
                        </template>
                      </v-sparkline>
                    </v-sheet>
                  </v-card-text>
                </v-card>
              </v-carousel-item>

              <v-carousel-item>
                <v-card class="mx-auto " color="secondary" max-width="600">
                  <v-card-text>
                    <v-sheet color="rgba(0, 0, 0, .12)">
                      <span class="text-h6 ml-1 mt-1 white--text">劳动力预测概览 <span
                          class="text-subtitle-1 grey--text text--lighten-2">(/人)</span></span>
                      <v-sparkline :value="item.value2" color="rgba(255, 255, 255, .7)" height="120" padding="24"
                        stroke-linecap="round" smooth>
                        <template v-slot:label="item">
                          {{ item.value }}
                        </template>
                      </v-sparkline>
                    </v-sheet>
                  </v-card-text>
                </v-card>
              </v-carousel-item>


            </v-carousel>

            <v-list two-line>
              <v-list-item>
                <v-list-item-content>
                  <v-list-item-title class="text-h5">{{ item.name }}</v-list-item-title>
                  <v-list-item-subtitle class="mt-3">{{ item.address }}</v-list-item-subtitle>
                </v-list-item-content>
                <v-list-item-action>
                  <v-btn large color="secondary" outlined><v-icon class="mr-2">mdi-pencil-outline</v-icon> 编辑 </v-btn>
                </v-list-item-action>
              </v-list-item>
            </v-list>

          </v-card>

        </v-col>
      </v-row>


    </template>

  </v-data-iterator>

</template>

<script>
export default {
  data() {
    return {
      search: '',
      items: [
        {
            id: '', name: '分店1', address: '浙江省杭州市钱塘区xxx路xxx号', size: 100,
            value1: [111, 446, 675, 754, 590, 610, 437], value2: [444, 446, 531, 510, 558, 425, 754],
        },
        {
            id: '', name: '分店2', address: '浙江省金华市婺城区xxx路xxx号', size: 100,
            value1: [423, 735, 632, 326, 590, 782, 567], value2: [355, 446, 675, 477, 333, 572, 275],
        },
        {
            id: '', name: '分店3', address: '浙江省金华市金东区xxx路xxx号', size: 100,
            value1: [572, 446, 752, 394, 590, 610, 325], value2: [448, 103, 675, 510, 114, 388, 787],
        },
      ],

    }
  },
  computed: {

  },
  methods: {

  },
}
</script>
