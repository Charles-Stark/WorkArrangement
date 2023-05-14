<!-- eslint-disable vue/multi-word-component-names -->
<template>
    <div>
      <TopNav></TopNav>
      <div class="flex1" id="1">
      <section>
        <img src="../assets/stars.png" :style="{ left: stars }">
        <!-- <img src="../assets/moon.png" :style="{ top: moon }"> -->
        <img src="../assets/mountains_behind.png" :style="{ top: mountainsBehind }">
        <!-- <h2 :style="{ marginTop: textMarginTop, marginRight: textMarginRight }">Moon Light</h2> -->
        <img src="../assets/mountains_front.png" :style="{ top: mountainsFront }">
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
                            <!-- 特效按钮 -->
                            <div class="btn1">
                              <a href="#2" class="btn1-1" :style="{ marginTop: buttonMarginTop }">了解更多</a>
                            </div>

      </section>
      </div>

      <div class="sec" id="2">
          <div class="section1">
            <FlipImage></FlipImage>
            <h1>基于业务预测和劳动力管理模型，结合高效排班算法</h1>
            <h2>为您提供专业准确便捷的智能排班解决方案</h2>
          </div>
      </div>
    
      <div class="section2" id="3">
        <ProductAdvantage></ProductAdvantage>
      </div>

      <div class="section2" id="4">
        <SystemHighlight></SystemHighlight>
      </div>

      <div class="section1" id="5">
            <h1>基于闭环反馈思想的智能排班系统</h1>
            <h2>为您提供更精准、更便捷、更合规的解决方案</h2>
            <div class="RotateImg">
              <RotateImage></RotateImage>
            </div>
      </div>

    </div>
  </template>
  
  <script>
  import TopNav from '@/components/introduce/TopNav.vue'
  import FlipImage from '@/components/FlipImage.vue'
  import RotateImage from '@/components/RotateImage.vue'
  import ProductAdvantage from '@/components/ProductAdvantage.vue'
  import SystemHighlight from '@/components/SystemHighlight.vue'
  export default {
    components: {
        TopNav,FlipImage,RotateImage,ProductAdvantage,SystemHighlight
    },
    data() {
      return {
        active: 'home',
        stars: '0px',
        moon: '0px',
        mountainsBehind: '0px',
        mountainsFront: '0px',
        textMarginTop: '0px',
        textMarginRight: '0px',
        buttonMarginTop: '0px',
        headerTop: '0px'
      }
    },
    mounted() {
      window.addEventListener('scroll', this.handleScroll)
    },
    methods: {
      setActive(section) {
        this.active = section
      },
      handleScroll() {
        const value = window.scrollY
  
        this.stars = `${value * 0.25}px`
        this.moon = `${value * 1.05}px`
        this.mountainsBehind = `${value * 0.5}px`
        this.mountainsFront = `${value * 0}px`
        this.textMarginRight = `${value * 4}px`
        this.textMarginTop = `${value * 1.5}px`
        this.buttonMarginTop = `${value * 1.5}px`
        this.headerTop = `${value * 0.5}px`
      }
    }
  }
  </script>
  
  <style scoped>
  header {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    background: #333;
    padding: 20px 100px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  header a {
    color: #fff;
    font-size: 18px;
    text-decoration: none;
    margin-left: 50px;
  }
  header a.active,
  header a:hover {
    color: #f4b41a;
  }
  sec {
    padding: 100px;
    margin-top: 200px;
    text-align: justify;
    font-size: 24px;
    justify-content: center;
  }
  section {
    width: 100%;
    height: 100vh;
    background-image: linear-gradient(to bottom, #15202b, #345c7d);
    position: relative;
    overflow: hidden;
  }
  section img {
    pointer-events: none;
    position: absolute;
  }
  #stars {
    width: 1000px;
    top: 0;
    left: 0;
    z-index: -1;
  }
  #moon {
    width: 300px;
    top: 0;
    right: 100;
    z-index: -1;
  }
  #mountains_behind {
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    z-index: -1;
  }
  #mountains_front {
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    z-index: 1;
  }
  #text {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    z-index: 1;
    color: #f4b41a;
    font-size: 72px;
    text-shadow: 4px 4px 10px rgba(0, 0, 0, 0.5);
  }


  @import url('https://fonts.googleapis.com/css?family=Poppins:300,400,500,600,700,800,900&display=swap'); 

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Poppins', sans-serif;
  /* 当用户手动导航或者 CSSOM scrolling API 触发滚动操作时 */
  scroll-behavior: smooth; 
}

body {
  min-height: 100vh;
  overflow-x: hidden;
  background: linear-gradient(#2b1055,#7597de);
}

section {
  position: relative;
  width: 100%;
  height: 100vh;
  padding: 100px;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}

section::before {
  content: '';
  position: absolute;
  bottom: 0;
  width: 100%;
  height: 100px;
  background: linear-gradient(to top, #1c0522, transparent);
  z-index: 1000;
}

section img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  /* object-fit CSS 属性指定可替换元素（例如：<img> 或 <video>）的内容应该如何适应到其使用高度和宽度确定的框。 */
  object-fit: cover;
  pointer-events: none;
}

section img#moon {
  /* mix-blend-mode CSS 属性描述了元素的内容应该与元素的直系父元素的内容和元素的背景如何混合。 */ 
  mix-blend-mode: screen;
}

section img#mountains_front {
  z-index: 10;
}

#text {
  position: absolute;
  right: -350px;
  color: #fff;
  white-space: nowrap;
  font-size: 7.5vw;
  z-index: 9;
}

.btn1{
/* padding-top: 80vh; */
position: absolute;
top:65vh;
    /* left:20vw; */
}
.btn1-1 {
  display: flex;
  text-decoration: none;
  align-self: flex-end;
  justify-content: center;
  /* align-content: center; */
  padding: 0px 30px;
  border-radius: 40px;
  height: 10vh;
  line-height: 10vh;
  width: 30vw;
  font-size: 2.5vw;
  z-index: 999;

  /* transform: translateY(200px); */
}

.sec {
  position: relative;
  padding: 100px;
  /* background: #1c0522; */
  background: linear-gradient(#1c0522,#3e1948);
}
.sec h1 {
  font-size: 3vw;
  margin-top: 10vh;
  margin-bottom: 10px;
  color: #fff;
  text-align:center;
}
.sec h2 {
  font-size: 2vw;
  margin-bottom: 10px;
  color: #fff;
  text-align:center;
}

.sec p {
  font-size: 1vw;
  color: #fff;
}

/* 智能排班文字跳动 */
.right-section1{
    position: absolute;
    top:25vh;
    left:20vw;
}
.right-font{
    /* left: 5vh; */
    top: 10px; /*字符上下浮动*/
            position: relative;
            animation: loading 0.8s ease 0s infinite alternate; /*引用动画*/
            /* animation: 动画名称 动画时间 动画曲线 何时开始 播放次数 是否反方向; 
            alternate:表示动画先正常运行再反方向运行，并持续交替
            infinite:表示无限循环*/
            font-size: 10vw;
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

/* 首页特效按钮1 */

a {
      text-decoration: none;
      position: absolute;
      left: 50%;
      top: 50%;
      /* 居中 */
      transform: translate(-50%, -50%);
      font-size: 24px;
      background: linear-gradient(90deg, #03a9f4, #f441a5, #ffeb3b, #03a9f4);
      background-size: 400%;
      width: 400px;
      height: 100px;
      color: #fff;
      line-height: 100px;
      text-align: center;
      text-transform: uppercase;
      border-radius: 50px;
      z-index: 1;
    }
 
    a::before {
      content: "";
      position: absolute;
      left: -5px;
      right: -5px;
      top: -5px;
      bottom: -5px;
      background: linear-gradient(90deg, #03a9f4, #f441a5, #ffeb3b, #03a9f4);
      background-size: 400%;
      border-radius: 50px;
      filter: blur(20px);
      z-index: -1;
    }
 
    a:hover::before {
      animation: sun 8s infinite;
    }
 
    a:hover {
      animation: sun 8s infinite;
    }
 
    @keyframes sun {
      100% {
        background-position: -400% 0;
      }
    }


.section1{
  position: relative;
  width: 100%;
  height: 100vh;
  padding: 100px;
  /* top:20vh; */
  /* display: flex; */
  justify-content: center;
  align-items: center;
  overflow: hidden;
  background: linear-gradient(#1c0522,#3e1948);
}

.section2{
  position: relative;
  width: 100%;
  height: 100vh;
  /* padding-top: 8vh; */
  /* top:20vh; */
  /* display: flex; */
  justify-content: center;
  align-items: center;
  overflow: hidden;
  background: linear-gradient(#1c0522,#3e1948);
}

.section1 h1 {
  font-size: 3vw;
  margin-top: 10vh;
  margin-bottom: 10px;
  color: #fff;
  text-align:center;
}
.section1 h2 {
  font-size: 2vw;
  margin-bottom: 10px;
  color: #fff;
  text-align:center;
}

.section1 p {
  font-size: 1em;
  color: #fff;
}

.RotateImg{
  /* width: 100vw;
  margin-left: 400px; */
  display:flex;
  justify-content: center; 
  margin-top:-10vh;
  /* align-items:center; */
}

</style>