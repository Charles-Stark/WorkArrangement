<!--
 * @Author: eggegg14 1063403542@qq.com
 * @Date: 2023-05-13 20:27:07
 * @LastEditors: eggegg14 1063403542@qq.com
 * @LastEditTime: 2023-05-14 18:22:00
 * @FilePath: \WorkArrangement\frontend\src\components\ProductAdvantage.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
<!-- eslint-disable vue/multi-word-component-names -->
<template>
    <div>
      <section class="home">
        <video class="video-slide" :class="{active: slideIndex === 0}" src="../assets/home4.mp4" autoplay muted loop></video>
          <div class="content" :class="{active: slideIndex === 0}">
          <h1>
            <span class="iconfont icon-dengpao"></span>闭环反馈智能系统<br>
            <span class="iconfont icon-dengpao"></span>开发班次自动认领<br>
            <span class="iconfont icon-dengpao"></span>开发班次智能推荐<br>
            <span class="iconfont icon-dengpao"></span>可选均衡排班时长<br>
            <span class="iconfont icon-dengpao"></span>满足审批请假需求<br>
            <span class="iconfont icon-dengpao"></span>多重身份权限分离<br>
            <span class="iconfont icon-dengpao"></span>各类信息实时通知<br>
            <span class="iconfont icon-dengpao"></span>查看打印历史排班<br>
          </h1>
        </div>
      </section>   
    </div>
  </template>
  
  <script>
  import '../assets/icon/font_1r0h4voabzbh/iconfont.css'
//   import '../../public/all.min.css'
  export default {
    data() {
      return {
        showMenu: false,
        slideIndex: 0
      }
    },
    methods: {
      toggleMenu() {
        this.showMenu = !this.showMenu;
      },
      changeSlideIndex(index) {
        this.slideIndex = index;
      }
    }
  }
  </script>
  
  <style scoped>
  /* 样式省略 */
  .content{
    text-align: left;
  }

  .content h1{
    font-size: 2vw;
  }

  *{
  margin: 0px;
  padding: 0px;
  box-sizing: border-box;
}

header{
  z-index: 999;
  display: flex;
  position: absolute;
  top: 0 ;
  left: 0;
  width:100%;
  justify-content: space-between;
  padding: 15px 200px;
  transition: 0.5s ease ;
  align-items: center;
}

.brand{
  color: #fff;
  font-size: 1.5rem;
  font-weight: 700;
  text-transform: uppercase;
  text-decoration: none;
}

header .navigation{
  position: relative;
}

header .navigation .navigation-items a{
  position: relative;
  color: #fff;
  font-size: 1rem;
  font-weight: 500;
  text-decoration: none;
  margin-left: 30px;
  transition: 0.3s ease ;
}

header .navigation .navigation-items a::before{
  content: "";
  position: absolute;
  bottom: 0px;
  left: 0px;
  background-color: #fff;
  width: 0%;
  height: 3px;
  transition: 0.3s ease;
}

header .navigation .navigation-items a:hover:before{
  width: 100%;
}

section{
  padding: 100px 200px;
}



.home{
  position: relative;
  width: 100%;
  min-height: 100vh;
  height: 100vh;
  display: flex;
  justify-content: center;
  flex-direction: column;
  background-color: #541581;
}

.home video{
  z-index: 000;
  position: absolute;
  top:0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.home:before{
  z-index: 777;
  content: '';
  position:absolute;
  top: 0;
  left: 0;
  background: rgba(45, 1, 50, 0.242);
  width: 100%;
  height: 100%;
}

.home .content{
   z-index: 888;
   color: #fff;
   width: 70%;
   margin-top: 50px;
   display: none;
}

.home .content.active{
  display: block;
}

.home .content h1 {
  font-size: 4rem;
  font-weight: 900;
  text-transform: uppercase;
  letter-spacing: 5px;
  line-height: 75px;
  margin-bottom: 40px;
}
.home .content h1 span{
  font-size: 1.2em;
  font-weight: 600;
}

.home .content p{
  margin-bottom: 65px;
}

.home .content a{
  background: #fff;
  padding: 15px 35px;
  color: #1680ac;
  font-size: 1.1em;
  font-weight: 500;
  text-decoration: none;
  border-radius: 2px;
}

@media  (max-width:1040px) {
  header{
    padding:  12px 20px;
  }

  section{
    padding: 100px 20px;
  }
  .home .media-icons {
    right: 15px;

  }
}

.home .content h1[data-v-3fe14276]{
  font-size:2vw ;
}

video{
  height: 100%;
  object-fit: cover;
}
  </style>