<template>
    <div>
        <TopNav></TopNav>
        <div>
            <full-page :options="options">
                <div class="section" >
                    <div class="section1" id="1">
                        <div>
                            <div class="left-img"><img src="../../../assets/logo-sm.png" alt="智能排班图标"></div>
                            <div class="right-section1">
                                <div class="right-font">
                                    <span>智</span>
                                    <span>能</span>
                                    <span>排</span>
                                    <span>班</span>
                                    <span>系</span>
                                    <span>统</span>
                                </div>
                            </div>
                        </div>
                        <div class="section1-2">
                        <span>基于业务预测和劳动力管理模型，结合高效排班算法<br/>
                                为您提供专业准确便捷的智能排班解决方案
                        </span>
                        </div>
                    </div>
                </div>
                <div class="section" id="2">
                    <div class="section2">
                        <div class="section2-1">
                        <div class="container">
                            <div class="panel" :class="{ active: isActive1 }" @click="ActiveClick1" style="background:red">
                                <img src="../../../assets/2-1.png">
                                <h3>门店业务预测</h3>
                        </div>
                        <!-- <div class="panel" :class="{ active: isActive2 }" @click="ActiveClick2" style="background-image:url('https://images.unsplash.com/photo-1572276596237-5db2c3e16c5d?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1350&q=80')"> -->
                            <div class="panel" :class="{ active: isActive2 }" @click="ActiveClick2" style="background:white">
                                <img src="../../../assets/2-2.png">    
                            <h3 style="color:black">劳动力管理预测</h3>
                        </div>
                        <!-- <div class="panel" :class="{ active: isActive3 }" @click="ActiveClick3" style="background-image:url('https://images.unsplash.com/photo-1507525428034-b723cf961d3e?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1353&q=80')"> -->
                            <div class="panel" :class="{ active: isActive3 }" @click="ActiveClick3" >
                                <img src="../../../assets/2-3.png">    
                                <h3>高效智能排班</h3>
                        </div>
                        </div>
                    </div>
                    </div>
                </div>
                <div class="section" id="3">
                    <div class="section3">
                        <div class="section3-left">
                            <img src="../../../assets/3-1.png">
                        </div>
                        <div class="section3-right">
                            <img src="../../../assets/ss3-1.png" alt="">
                            <!-- <div class="s3 s3-1">
                                <div class="s3-1-font">
                                    <span>
                                        ·基于历史业务值，预测未来业务和劳动力需求<br/>
                                        ·自动排班，将人员数据纳入体系，缩小期望与实际落差<br/>
                                        ·结合一岗多能的实际，调整劳动力结构与供给
                                    </span>
                                </div>
                            </div>
                            <div class="s3 s3-2">

                            </div>
                            <div class="s3 s3-3">

                            </div> -->
                        </div>
                    </div>
                </div>
                <div class="section" id="4">
                    <div class="section4">
                        <div class="s4-1img">
                            <img src="../../../assets/4-1.png">
                        </div>
                        <div class="s4-2img">
                            <img src="../../../assets/4-2.png">
                        </div>
                    </div>
                </div>
                <div class="section" id="5">
                    <div class="section5">
                        <div class="section5-1">
                            <v-carousel style="height:570px">
                                <v-carousel-item
                                    src="https://cdn.vuetifyjs.com/images/cards/docks.jpg"
                                    cover
                                ></v-carousel-item>
                                <v-carousel-item
                                    src="https://cdn.vuetifyjs.com/images/cards/hotel.jpg"
                                    cover
                                ></v-carousel-item>
                                <v-carousel-item
                                    src="https://cdn.vuetifyjs.com/images/cards/sunshine.jpg"
                                    cover
                                ></v-carousel-item>
                                <v-carousel-item
                                    src="https://cdn.vuetifyjs.com/images/cards/docks.jpg"
                                    cover
                                ></v-carousel-item>
                                <v-carousel-item
                                    src="https://cdn.vuetifyjs.com/images/cards/hotel.jpg"
                                    cover
                                ></v-carousel-item>
                            </v-carousel>
                        </div>
                        <div class="section5-2">
                            <img src="../../../assets/5-1.png" >
                        </div>
                        <div class="section5-3">
                            <div class="right-section5">
                                <div class="right-font">
                                    <span>系</span>
                                    <span>统</span>
                                    <span>内</span>
                                    <span>部</span>
                                    <span>展</span>
                                    <span>示</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </full-page>
        </div>
    </div>
</template>

<script>
import TopNav from '@/components/introduce/components/TopNav.vue'
export default {
    name: '',
    components: {
        TopNav
    },
    data() {
        return {
            isActive1: true,
            isActive2: false,
            isActive3: false,
            options: {
                // 为每个section设置背景色
                //sectionsColor: ["#f00","#0f0","#00f"],
                //用来控制slide幻灯片的箭头，设置为false，两侧的箭头会消失
                //controlArrows: false,
                //每一页幻灯片的内容是否垂直居中
                //verticalCentered: false,
                //字体是否随着窗口缩放而缩放
                //resize: true,
                //页面滚动速度
                //scrollingSpeed: 700,
                //定义锚链接，用户可以快速打开定位到某一页面；不需要加"#"，不要和页面中任意的id和name相同
                //anchors: ["page1","page2","page3"],
                //是否锁定锚链接
                //lockAnchors: true,
                //定义section页面的滚动方式，需要引入jquery.easings插件
                //easing:,
                //是否使用css3 transform来实现滚动效果
                //css3: false,
                //滚动到最顶部后是否连续滚动到底部
                //loopTop: true,
                //滚动到最底部后是否连续滚动到顶部
                //loopBottom: true,
                //横向slide幻灯片是否循环滚动
                //loopHorizontal: false,
                //是否循环滚动，不会出现跳动，效果很平滑
                //continuousVertical: true,
                //是否使用插件滚动方式，设为false后，会出现浏览器自带的滚动条，将不会按页滚动
                //autoScrolling: false,
                //是否包含滚动条，设为true，则浏览器自带的滚动条会出现，页面还是按页滚动，但是浏览器滚动条默认行为也有效
                //scrollBar: true,
                //设置每个section顶部的padding，当我们要设置一个固定在顶部的菜单、导航、元素等时使用
                //paddingTop: "100px",
                //设置每个section底部的padding，当我们要设置一个固定在底部的菜单、导航、元素等时使用
                //paddingBottom: "100px",
                //固定的元素，为jquery选择器；可用于顶部导航等
                //fixedElements: ".nav",
                //是否可以使用键盘方向键导航
                //keyboardScrolling: false,
                //在移动设置中页面敏感性，最大为100，越大越难滑动
                //touchSensitivity: 5,
                //设为false，则通过锚链接定位到某个页面不再有动画效果
                //animateAnchor: false,
                //是否记录历史，可以通过浏览器的前进后退来导航
                //recordHistory: true,
                //绑定菜单，设定相关属性和anchors的值对应后，菜单可以控制幻灯片滚动
                //menu: '.nav',
                //是否显示导航，设为true会显示小圆点作为导航
                //navigation: true,
                //导航小圆点的位置，可以设置为left或者right
                //navigationPosition: right,
                //鼠标移动到小圆点上时显示出的提示信息
                //navigationTooltips: ["第一页","第二页","第三页"],
                //是否显示当前页面小圆点导航的提示信息，不需要鼠标移上
                //showActiveTooltip: true,
                //是否显示横向幻灯片的导航
                //slidesNavigation: true,
                //横向幻灯片导航的位置，可以为top或者bottom
                //slidesNavPosition: bottom,
                //内容超过满屏时是否显示滚动条，需要jquery.slimscroll插件
                //scrollOverflow: true,
                //section选择器
                //sectionSelector: ".section",
                //slide选择器
                //slideSelector: ".slide"
                licenseKey: 'OPEN-SOURCE-GPLV3-LICENSE',
                afterLoad: this.afterLoad,
                scrollOverflow: true,
                scrollBar: false,
                menu: '#menu',
                // navigation: true,
                // anchors: ['page1', 'page2', 'page3'],
                sectionsColor: ['#41b883', '#ff5f45', '#0798ec', '#fec401', '#1bcee6']
            }
        }
    },
    methods: {
        ActiveClick1(){
            this.isActive1 = true;
            this.isActive2 = false;
            this.isActive3 = false;
        },
        ActiveClick2(){
            this.isActive1 = false;
            this.isActive2 = true;
            this.isActive3 = false;
        },
        ActiveClick3(){
            this.isActive1 = false;
            this.isActive2 = false;
            this.isActive3 = true;
        }
    }
}
</script>

<style scoped>
.section1 {
    background: url("../../../assets/首页UI_01.png");
    background: linear-gradient(to bottom right, #331987, #431b62);
    background-repeat: no-repeat;
    background-size: 100%;
    width: 100%;
    height: 100vh;
    position: fixed;
    margin: 0px;
    position: relative;
}

.section1 .left-img{
    position: absolute;
    top: 20%;
    left: 8%;
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    width: 300px;
    height: 300px;
}
.section1 .left-img img{
    width:auto;
    height:auto;
    max-width:100%;
    max-height:100%;
}
.right-section1{
    position: absolute;
    top:22%;
    left: 12%;
}
.right-font{
    left: 10%;
    top: 10px; /*字符上下浮动*/
            position: relative;
            animation: loading 0.8s ease 0s infinite alternate; /*引用动画*/
            /* animation: 动画名称 动画时间 动画曲线 何时开始 播放次数 是否反方向; 
            alternate:表示动画先正常运行再反方向运行，并持续交替
            infinite:表示无限循环*/
            font-size: 160px;
            color: white;
            text-shadow: 
                        0 1px 0  #CCC,
                        0 2px 0 #CCC,
                        0 3px 0 #CCC,
                        0 4px 0 #CCC,
                        0 5px 0 #CCC,
                        0 6px 0 #CCC,
                        0 7px 0 #CCC,
                        0 8px 0 #CCC,
                        0 9px 0 #CCC,
                        0 10px 10px rgba(0, 0, 0, 0.5);
    /*text-shadow: 水平阴影位置(必须) 垂直阴影位置(必须) 模糊距离(可选) 阴影颜色(可选);
       text-shadow: h-shadow v-shadow blur color; */
        }
        @keyframes loading {  /* 定义动画*/
            100% {
                top: -10px;  /*字符上下浮动*/
                text-shadow: 0 1px 0 #826a86,
                    0 2px 0 #826a86,
                    0 3px 0 #826a86,
                    0 4px 0 #826a86,
                    0 5px 0 #826a86,
                    0 6px 0 #826a86,
                    0 7px 0 #826a86,
                    0 8px 0 #826a86,
                    0 9px 0 #826a86,
                    0 50px 25px rgba(0, 0, 0, 0.3);     
/*上升的时候阴影要在文字下面一些，所以垂直阴影的位置坐标和模糊距离要大一些*/
            }
}

.section1-2{
    font-size:40px ;
    font-family: KaiTi;
    text-align: center;
    color:#fff;
    position: absolute;
    top:60%;
    left: 20%;

}
.section2 {
    /* background: url("../../../assets/首页UI_02.png"); */
    background: linear-gradient(to bottom right, #5f3ecd, #602f85);
    background-repeat: no-repeat;
    background-size: 100%;
    width: 100%;
    height: 100vh;
    position: fixed;
    margin: 0px;
    position: relative;
}

.section2-1{
    position: absolute;
    top: 12%;
    left: 8%;
}

.container{
    display: flex;
    width: 90vw;
}

.panel{
    /* 跟封面一样大小 */
    background-size: cover;
    /* 把封面图片居中 */
    background-position: center;
    background-repeat: no-repeat;
    height: 80vh;
    /* 边框圆角 */
    border-radius: 50px;
    color: #fff;
    /* 鼠标放上变小手 */
    cursor: pointer;
    flex: 0.5;
    margin: 10px;
    /* 相对定位 */
    position: relative;
    -webkit-transition: all 700ms ease-in;
    
}

.panel img{
    background-size: cover;
    /* 把封面图片居中 */
    background-position: center;
    background-repeat: no-repeat;
    height: 80vh;
    /* 边框圆角 */
    border-radius: 50px;
    width: 100%;
    height: 100%;
}

.panel h3{
    font-size: 24px;
    /* 绝对定位 */
    position: absolute;
    bottom: 20px;
    left: 20px;
    margin: 0;
    opacity: 0;
}

.panel.active{
    flex: 5;
}

.panel.active h3 {
    opacity: 1;
    transition: opacity 0.3s ease-in 0.4s;
}

@media (max-width:480px){
    .container{
        width: 100vw;
    }
    .panel:nth-of-type(2),
    .panel:nth-of-type(3){
        display: none;
    }
}


.section3 {
    /* background: url("../../../assets/首页UI_03.png"); */
    background: linear-gradient(to bottom right, #331987, #431b62);
    background-repeat: no-repeat;
    background-size: 100%;
    width: 100%;
    height: 100vh;
    position: fixed;
    margin: 0px;
    position: relative;
}

/* .section3-left{
    background: lightblue;
    margin:50px auto 0px;
} */
.section3-left img{
    position: absolute;
    height: 650px;
    bottom:50px;
    z-index: 5;
    animation:mymove 1s infinite;
}

@keyframes mymove{
        0%{left:0px;}
        50%{left:40px;}   
        100%{left:0px;}
        }

/* .section3-right{
    position: absolute;
    top: 200px;
    right: 80px;
} */
.section3-right img{
    width: 65%;
    position: absolute;
    right: 5%;
    top:20%;
    z-index: 2;
}
/* .s3{
    position: absolute;
    top: 180px;
    right: 80px;
    width: 800px;
    height: 150px;
    background: rgba(255, 255, 255, 0.2);
}


.s3-2{
    top: 380px;
}

.s3-3{
    top: 580px;
} */

.section4 {
    /* background: url("../../../assets/首页UI_04.png"); */
    background: linear-gradient(to bottom right, #5f3ecd, #602f85);
    background-repeat: no-repeat;
    background-size: 100%;
    width: 100%;
    height: 100vh;
    position: fixed;
    margin: 0px;
    position: relative;
}

.s4-1img img{
    position: absolute;
    width: 90%;
    top: 18%;
    left:5%;
}


.s4-2img img{
    position: absolute;
    margin-left: 58%;
    margin-top: 15%;
    height: 500px;
    z-index: 5;
    animation:scaleDraw 3s infinite;
}

@keyframes scaleDraw {
/*定义关键帧、scaleDrew是需要绑定到选择器的关键帧名称*/
    0% {
    transform: scale(1); /*开始为原始大小*/
  }
    25% {
    transform: scale(1.2); /*放大1.1倍*/
  }
    50% {
    transform: scale(1);
  }
    75% {
    transform: scale(1.2);
  }
}

.section5 {
    /* background: url("../../../assets/首页UI_05.png"); */
    background: linear-gradient(to bottom right, #331987, #431b62);
    background-repeat: no-repeat;
    background-size: 100%;
    width: 100%;
    height: 100vh;
    position: fixed;
    margin: 0px;
    position: relative;
}

.section5-1{
    position: absolute;
    width: 50%;
    height: 80%;
    left: 7%;
    top:20%
}

.section5-1 v-carousel{
    height: 600px;
}

.section5-2 {
    position: absolute;
    top: 18%;
    right: -3%;
    display: flex;
    justify-content: center;
    align-items: center;
    width: 55%;
    height: 60%;
}
.section5-2 img{
    width:auto;
    height:auto;
    max-width:100%;
    max-height:100%;
}

.section5-3{
    position: absolute;
    right: 10%;
    bottom: 10%;
}
.right-section5 .right-font{
            top: 10px; /*字符上下浮动*/
            position: relative;
            animation: loading 0.8s ease 0s infinite alternate; /*引用动画*/
            /* animation: 动画名称 动画时间 动画曲线 何时开始 播放次数 是否反方向; 
            alternate:表示动画先正常运行再反方向运行，并持续交替
            infinite:表示无限循环*/
            font-size: 80px;
            color: white;
            text-shadow: 
                        0 1px 0  #CCC,
                        0 2px 0 #CCC,
                        0 3px 0 #CCC,
                        0 4px 0 #CCC,
                        0 5px 0 #CCC,
                        0 6px 0 #CCC,
                        0 7px 0 #CCC,
                        0 8px 0 #CCC,
                        0 9px 0 #CCC,
                        0 10px 10px rgba(0, 0, 0, 0.5);
    /*text-shadow: 水平阴影位置(必须) 垂直阴影位置(必须) 模糊距离(可选) 阴影颜色(可选);
       text-shadow: h-shadow v-shadow blur color; */
        }
        @keyframes loading {  /* 定义动画*/
            100% {
                top: -10px;  /*字符上下浮动*/
                text-shadow: 0 1px 0 #826a86,
                    0 2px 0 #826a86,
                    0 3px 0 #826a86,
                    0 4px 0 #826a86,
                    0 5px 0 #826a86,
                    0 6px 0 #826a86,
                    0 7px 0 #826a86,
                    0 8px 0 #826a86,
                    0 9px 0 #826a86,
                    0 50px 25px rgba(0, 0, 0, 0.3);     
/*上升的时候阴影要在文字下面一些，所以垂直阴影的位置坐标和模糊距离要大一些*/
            }
    }
</style>

