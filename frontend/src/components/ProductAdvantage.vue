<!--
 * @Author: eggegg14 1063403542@qq.com
 * @Date: 2023-05-13 20:27:07
 * @LastEditors: eggegg14 1063403542@qq.com
 * @LastEditTime: 2023-05-14 14:17:24
 * @FilePath: \WorkArrangement\frontend\src\components\ProductAdvantage.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
<!-- eslint-disable vue/multi-word-component-names -->
<template>
    <div>
      <header>
        <a href="#" class="brand"></a>
        <div class="menu-btn" @click="toggleMenu"></div>
        <div class="navigation" :class="{active: showMenu}">
          <div class="navigation-items">
          </div>
        </div>
      </header>
      <section class="home">
        <video class="video-slide" :class="{active: slideIndex === 0}" src="../assets/home.mp4" autoplay muted loop></video>
        <video class="video-slide" :class="{active: slideIndex === 1}" src="../assets/home2.mp4" autoplay muted loop></video>
        <video class="video-slide" :class="{active: slideIndex === 2}" src="../assets/home3.mp4" autoplay muted loop></video>
  
        <div class="content" :class="{active: slideIndex === 0}">
          <h1>更精准<br><span>More accurate</span></h1>
          <p> 基于历史业务值，预测未来业务和劳动力需求<br>
              自动排班，将人员数据纳入体系，缩小期望与实际落差<br>
              结合一岗多能的实际，调整劳动力结构与供给<br>
          </p>
          <a href="#4" style="color: #670e68f7;">  了解更多</a>
        </div>
  
        <div class="content" :class="{active: slideIndex === 1}">
          <h1>更便捷<br><span>More convenient</span></h1>
          <p> 员工请假，设置开放班次，及时响应<br>
              灵活排班，实现智能规划整体，微调局部排班<br>
              排班结果清晰易懂，及时获取排班信息<br>
        </p>
          <a href="#4" style="color: #670e68f7;">  了解更多</a>
        </div>
  
        <div class="content" :class="{active: slideIndex === 2}">
          <h1>更合规<br><span>More compliant</span></h1>
          <p> 符合国家劳动法规，排班结果符合员工每日最低及最长时长要求<br>
              高效排班，帮助企业进行成本管控，实现员工企业利益双赢</p>
          <a href="#4" style="color: #670e68f7;">  了解更多</a>
        </div>
  
        <div class="slider-navigation">
          <div class="nav-btn" :class="{active: slideIndex === 0}" @click="changeSlideIndex(0)"></div>
          <div class="nav-btn" :class="{active: slideIndex === 1}" @click="changeSlideIndex(1)"></div>    
          <div class="nav-btn" :class="{active: slideIndex === 2}" @click="changeSlideIndex(2)"></div>   
        </div>
      </section>   
    </div>
  </template>
  
  <script>
//   import '../../public/style.css'
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
  
  <style>
  /* 样式省略 */
  
  </style>