<template>
    <div>
        <v-card class="part2">
            <div class="title2">
                <img src="../assets/titile1-1.png" alt="门店客户信息">
            </div>
            <!-- 折线图 -->
            <e-charts class="chart1" :option="option1" />
        </v-card>

        <div class="part3">
            <v-card class="part4">
                <div class="title2">
                    <img src="../assets/titile1-2.png" alt="门店销售详情">
                </div>
                <e-charts class="chart2" :option="option2" />
            </v-card>

            <v-card class="part5">
                <div class="title2">
                    <img src="../assets/titile1-3.png" alt="门店部门业绩">
                </div>
                <e-charts class="chart3" :option="option3" />
            </v-card>
        </div>
        <!-- <v-card class="part1">
            <div class="title1">
                <img src="../assets/标题1.png" alt="门店风采展示">
            </div>
            轮播图
            <div class="rim1">
                <div class="card-loop">
                    <transition-group>
                        <div v-for="(item, idx) in loopModules" :key="idx" class="loop-item" :style="item.style"
                            @click="handleLoop(idx)">
                            <img :src="baseData[idx].cover" :alt="baseData[idx].cover" />
                            <p class="title">{{ baseData[idx].title }}</p>
                            <transition name="fade" enter-active-class="animate__animated animate__fadeInUp"
                                leave-active-class="animate__animated animate__fadeOutDown">
                                <div v-show="idx === loopCenterIdx">
                                    <p class="tool"></p>
                                </div>
                            </transition>
                        </div>
                    </transition-group>
                </div>
            </div>
        </v-card> -->

    </div>
</template>
  
  
  
<script>
// 引入图片资源
import loop1 from '../assets/shoe1.jpg';
import loop2 from '../assets/shoe2.jpg';
import loop3 from '../assets/shoe3.jpg';
import loop4 from '../assets/shoe4.jpg';
import loop5 from '../assets/shoe5.jpg';


export default {
    data() {
        return {
            // 轮播数据
            baseData: [
                {
                    // title: '这里是标题',
                    cover: loop1,
                },
                {
                    // title: '这里是标题',
                    cover: loop2,
                },
                {
                    // title: '这里是标题',
                    cover: loop3,
                },
                {
                    // title: '这里是标题',
                    cover: loop4,
                },
                {
                    // title: '这里是标题',
                    cover: loop5,
                },
            ],

            // 当前展示项
            loopCenterIdx: 2,

            // 轮播样式模板，用来循环赋值给 容器
            loopModules: [
                {
                    loopIdx: 0,
                    style: {
                        'z-index': 7,
                        background: '#fff',
                        transform: `scale(${1})`,
                        left: 'calc(100% / 5 * 0)',
                        height: '20px',
                        opacity: 0.8,
                    },
                },
                {
                    loopIdx: 1,
                    style: {
                        'z-index': 8,
                        transform: `scale(${1.4})`,
                        background: '#fff',
                        left: 'calc(100% / 5 * .9)',
                        height: '50px',
                        opacity: 0.9,
                    },
                },
                {
                    loopIdx: 2,
                    style: {
                        transform: `scale(${1.7})`,
                        'z-index': 9,
                        background: '#fff',
                        left: 'calc(100% / 5 * 2)',
                        opacity: 1,
                        'box-shadow': '0 0 5px #ccc',
                        height: '100px',
                    },
                },
                {
                    loopIdx: 3,
                    style: {
                        transform: `scale(${1.4})`,
                        'z-index': 8,
                        background: '#fff',
                        left: 'calc(100% / 5 * 3.1)',
                        opacity: 0.9,
                        height: '50px',
                    },
                },
                {
                    loopIdx: 4,
                    style: {
                        'z-index': 7,
                        transform: `scale(${1})`,
                        background: '#fff',
                        left: 'calc(100% / 5 * 4)',
                        height: '50px',
                        opacity: 0.8,
                    },
                },
            ],

            //柱状图1
            option1: {
                legend: {},
                tooltip: {},
                dataset: {
                    dimensions: ['用户类型', '存量客户', '新增客户'],
                    source: [
                        { 用户类型: '1月', 存量客户: 43, 新增客户: 485 },
                        { 用户类型: '2月', 存量客户: 83, 新增客户: 373 },
                        { 用户类型: '3月', 存量客户: 86, 新增客户: 365 },
                        { 用户类型: '4月', 存量客户: 72, 新增客户: 523 },
                        { 用户类型: '5月', 存量客户: 74, 新增客户: 563 },
                        { 用户类型: '6月', 存量客户: 84, 新增客户: 453 },
                        { 用户类型: '7月', 存量客户: 96, 新增客户: 583 },
                        { 用户类型: '8月', 存量客户: 102, 新增客户: 693 },
                        { 用户类型: '9月', 存量客户: 59, 新增客户: 423 },
                        { 用户类型: '10月', 存量客户: 66, 新增客户: 313 },
                        { 用户类型: '11月', 存量客户: 71, 新增客户: 593 },
                        { 用户类型: '12月', 存量客户: 79, 新增客户: 583 },
                    ]
                },
                xAxis: { type: 'category' },
                yAxis: {},
                // Declare several bar series, each will be mapped
                // to a column of dataset.source by default.
                series: [{ type: 'bar' }, { type: 'bar' }]
            },

            //折线图2
            option2: {
                // title: {
                //   text: '各类鞋销售量'
                // },
                //       title:{
                //   text: '不同季节鞋类销售量',
                //   x:'center' ,
                //   y:290,
                // },
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'cross',
                        label: {
                            backgroundColor: '#6a7985'
                        }
                    }
                },
                legend: {
                    data: ['马丁靴', '高跟鞋', '帆布鞋', '运动鞋']
                },
                toolbox: {
                    feature: {
                        saveAsImage: {}
                    }
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                xAxis: [
                    {
                        type: 'category',
                        boundaryGap: false,
                        data: ['春售量', '夏售量', '秋售量', '冬售量',]
                    }
                ],
                yAxis: [
                    {
                        type: 'value'
                    }
                ],
                series: [
                    {
                        name: '马丁靴',
                        type: 'line',
                        stack: 'Total',
                        areaStyle: {},
                        emphasis: {
                            focus: 'series'
                        },
                        data: [320, 132, 101, 464]
                    },
                    // {
                    //   name: '凉鞋',
                    //   type: 'line',
                    //   stack: 'Total',
                    //   areaStyle: {},
                    //   emphasis: {
                    //     focus: 'series'
                    //   },
                    //   data: [220, 682, 91, 34]
                    // },
                    {
                        name: '高跟鞋',
                        type: 'line',
                        stack: 'Total',
                        areaStyle: {},
                        emphasis: {
                            focus: 'series'
                        },
                        data: [50, 382, 41, 64]
                    },
                    {
                        name: '帆布鞋',
                        type: 'line',
                        stack: 'Total',
                        areaStyle: {},
                        emphasis: {
                            focus: 'series'
                        },
                        data: [560, 332, 701, 334]
                    },
                    {
                        name: '运动鞋',
                        type: 'line',
                        stack: 'Total',
                        label: {
                            show: true,
                            position: 'top'
                        },
                        areaStyle: {},
                        emphasis: {
                            focus: 'series'
                        },
                        data: [820, 932, 901, 934, 1290, 1330, 1320]
                    }
                ]
            },

            //饼图3
            option3: {
                // title: {
                //     text: '各部门贡献占比',
                //     x: 'center',
                //     y: 'bottom',
                // },
                tooltip: {
                    trigger: 'item'
                },
                legend: {
                    top: '5%',
                    left: 'center'
                },
                series: [
                    {
                        name: '贡献情况',
                        type: 'pie',
                        radius: ['40%', '70%'],
                        avoidLabelOverlap: false,
                        itemStyle: {
                            borderRadius: 10,
                            borderColor: '#fff',
                            borderWidth: 2
                        },
                        label: {
                            show: false,
                            position: 'center'
                        },
                        emphasis: {
                            label: {
                                show: true,
                                fontSize: 40,
                                fontWeight: 'bold'
                            }
                        },
                        labelLine: {
                            show: false
                        },
                        data: [
                            { value: 1048, name: '导购部' },
                            { value: 735, name: '收银部' },
                            { value: 580, name: '库房部' },
                            { value: 484, name: '营销部' },
                            { value: 300, name: '调研部' }
                        ]
                    }
                ]
            },
        };
    },
    created() {
        // todo Ajax ...
    },

    mounted() {
        // 初始化轮播
        this.initLoop();
        console.log('centerIdx = ', this.loopCenterIdx);

        //基于数据初始化echarts实例
        //折线图


    },

    methods: {
        // 初始化轮播
        initLoop() {
            this.loopModules = this.loopModules.map((item, idx) => {
                item.desc = this.baseData[idx];
                return item;
            });
            console.log(this.loopModules);
        },

        // 下一张
        handleNext() {
            this.loopCenterIdx = this.loopCenterIdx + 1 > 4 ? 0 : this.loopCenterIdx + 1;
            this.loopModules = this.loopModules.map((item, idx) => {
                item.desc = this.baseData[idx];
                return item;
            });
            this.loopModules.unshift(this.loopModules.pop());
        },

        // 上一张
        handleLast() {
            this.loopCenterIdx = this.loopCenterIdx - 1 < 0 ? 4 : this.loopCenterIdx - 1;

            this.loopModules = this.loopModules.map((item, idx) => {
                item.desc = this.baseData[idx];
                return item;
            });
            this.loopModules.push(this.loopModules.shift());
        },

        // 点击容器
        handleLoop(checkId) {
            console.log('checkId = ', checkId, ', loopCenterIdx = ', this.loopCenterIdx);
            // 当前项
            if (checkId === this.loopCenterIdx) {
                return;
            }

            if (checkId > this.loopCenterIdx) {
                if (this.loopCenterIdx === 0 && checkId === 4) {
                    return this.handleLast(checkId);
                } else {
                    return this.handleNext(checkId);
                }
            }

            if (checkId < this.loopCenterIdx) {
                if (checkId === 0 && this.loopCenterIdx === 4) {
                    return this.handleNext(checkId);
                } else {
                    return this.handleLast(checkId);
                }
            }
        },
    },
};
</script>
  
<style lang="scss" scoped>

.part1 {
    width: 100%;
    margin: 1vw auto;
    padding: 2vw;
    height: 40vh;
    background-color: rgba(201, 229, 234,0.1);
}

.title1 {
    width: 20vw;

    img {
        width: 100%;
    }
}

.part2 {
    width: 80vw;
    margin: 2vh auto;
    // padding: 3vw;
    height: 38vh;
    // background-color: rgb(248, 240, 230);
    background-color: rgba(201, 229, 234,0.1);
}

.title2 {
    //   height: 10%;
    //   img{
    //   height: 100%;
    // }
    width: 25vw;
    display: flex;

    img {
        width: 100%;
        justify-content: center;
    }
}

.part3 {
    // display:inline-block;
    width: 80vw;
    margin: 20px auto;
    height: 44vh;
    display: flex;
    justify-content: center;
    // background-color: rgb(248, 240, 230);
    // background-color: rgb(248, 240, 230);

    .part4 {
        // background-color: bisque;
        // background-color: rgb(248, 240, 230);
        background-color: rgba(201, 229, 234,0.1);
        width: 49.5%;
        height: 100%;
        margin: auto;
        // display:inline-block;
    }

    .part5 {
        // display: inline-block;
        // background-color: rgb(248, 240, 230);
        // background-color: aquamarine;
        background-color: rgba(201, 229, 234,0.1);
        width: 49.5%;
        height: 100%;
        margin: auto;
        // display:inline-block;
        margin-left:1%;
    }
}

.rim1 {
    // line-height: 100px;
    // text-align: center;
    margin: 10px auto;
    width: 100%;
    height: 240px;
}

.card-loop {
    width: 100%;
    height: 10px;

    margin-top: 10px;
    position: relative;

    .loop-item {
        width: calc(100% / 5);
        float: center;
        box-sizing: border-box;

        position: absolute;

        transition: all ease-out 0.5s;
        transform-origin: 50% 50%;

        img {
            width: 100%;
        }

        div {
            // background: #fff;
            padding-bottom: 5px;
        }

        p.title {
            padding: 3px;
            color: #333;
            font-weight: bolder;
            transform: scale(0.9);
            text-align: center;
        }

        p.content {
            font-size: 6px;
            // color: #999;
            overflow: hidden;
            display: -webkit-box; //将元素设为盒子伸缩模型显示
            -webkit-box-orient: vertical; //伸缩方向设为垂直方向
            -webkit-line-clamp: 2; //超出2行隐藏，并显示省略号
            transform: scale(0.9);
        }

        p.tool {
            text-align: center;
            transform: scale(0.7);
        }
    }
}

//柱状图
.chart1 {
    // margin-top: -50px;
    height: 30vh;
    width: 100%;
}


//面积图
.chart2 {
    // margin-top: 30px;
    height: 80%;
    width: 100%;
}

//饼图
.chart3{
    height: 80%;
    width: 100%;
}

v-card{
    display: flex;
    justify-content: center;
}
</style>