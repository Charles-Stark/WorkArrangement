<!--
 * @Author: eggegg14 1063403542@qq.com
 * @Date: 2023-05-13 20:27:07
 * @LastEditors: eggegg14 1063403542@qq.com
 * @LastEditTime: 2023-05-14 14:24:45
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


  </style>