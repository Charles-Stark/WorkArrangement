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
      <div class="navigation" :class="{ active: showMenu }">
        <div class="navigation-items">
        </div>
      </div>
    </header>
    <section class="home">
      <video class="video-slide" :class="{ active: slideIndex === 0 }" src="../assets/home.mp4" autoplay muted loop></video>
      <video class="video-slide" :class="{ active: slideIndex === 1 }" src="../assets/home2.mp4" autoplay muted
        loop></video>
      <video class="video-slide" :class="{ active: slideIndex === 2 }" src="../assets/home3.mp4" autoplay muted
        loop></video>

      <div class="content" :class="{ active: slideIndex === 0 }">
        <h1>更精准<br><span>More accurate</span></h1>
        <p> 基于历史业务值，预测未来业务和劳动力需求<br>
          自动排班，将人员数据纳入体系，缩小期望与实际落差<br>
          结合一岗多能的实际，调整劳动力结构与供给<br>
        </p>
        <a href="#4" style="color: #670e68f7;"> 了解更多</a>
      </div>

      <div class="content" :class="{ active: slideIndex === 1 }">
        <h1>更便捷<br><span>More convenient</span></h1>
        <p> 员工请假，设置开放班次，及时响应<br>
          灵活排班，实现智能规划整体，微调局部排班<br>
          排班结果清晰易懂，及时获取排班信息<br>
        </p>
        <a href="#4" style="color: #670e68f7;"> 了解更多</a>
      </div>

      <div class="content" :class="{ active: slideIndex === 2 }">
        <h1>更合规<br><span>More compliant</span></h1>
        <p> 符合国家劳动法规，排班结果符合员工每日最低及最长时长要求<br>
          高效排班，帮助企业进行成本管控，实现员工企业利益双赢</p>
        <a href="#4" style="color: #670e68f7;"> 了解更多</a>
      </div>

      <div class="slider-navigation">
        <div class="nav-btn" :class="{ active: slideIndex === 0 }" @click="changeSlideIndex(0)"></div>
        <div class="nav-btn" :class="{ active: slideIndex === 1 }" @click="changeSlideIndex(1)"></div>
        <div class="nav-btn" :class="{ active: slideIndex === 2 }" @click="changeSlideIndex(2)"></div>
      </div>
    </section>
  </div>
</template>
  
<script>
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

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

header {
  z-index: 999;
  display: flex;
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  justify-content: space-between;
  padding: 15px 200px;
  transition: 0.5s ease;
  align-items: center;
}

.brand {
  color: #fff;
  font-size: 1.5rem;
  font-weight: 700;
  text-transform: uppercase;
  text-decoration: none;
}

header .navigation {
  position: relative;
}

header .navigation .navigation-items a {
  position: relative;
  color: #fff;
  font-size: 1rem;
  font-weight: 500;
  text-decoration: none;
  margin-left: 30px;
  transition: 0.3s ease;
}

header .navigation .navigation-items a::before {
  content: "";
  position: absolute;
  bottom: 0;
  left: 0;
  background-color: #fff;
  width: 0;
  height: 3px;
  transition: 0.3s ease;
}

header .navigation .navigation-items a:hover:before {
  width: 100%;
}

section {
  padding: 100px 200px;
}

.home {
  position: relative;
  width: 100%;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  flex-direction: column;
  background-color: #541581;
}

.home video {
  z-index: 000;
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.home:before {
  z-index: 777;
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  background: rgba(45, 1, 50, 0.242);
  width: 100%;
  height: 100%;
}

.home .content {
  z-index: 888;
  color: #fff;
  width: 70%;
  margin-top: 50px;
  display: none;
}

.home .content.active {
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

.home .content h1 span {
  font-size: 1.2em;
  font-weight: 600;
}

.home .content p {
  margin-bottom: 65px;
}

.home .content a {
  background: #fff;
  padding: 15px 35px;
  color: #1680ac;
  font-size: 1.1em;
  font-weight: 500;
  text-decoration: none;
  border-radius: 2px;
}

.home .media-icons a {
  color: #fff;
  font-size: 1.6em;
  transition: 0.3s ease;
}

.home .media-icons a:hover {
  transform: scale(1.2);
}


.slider-navigation {
  z-index: 888;
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  transform: translateY(80px);
  margin-bottom: 12px;
  gap: 20px
}

.slider-navigation .nav-btn {
  width: 12px;
  height: 12px;
  background: #fff;
  border-radius: 50%;
  cursor: pointer;
  box-shadow: 0 0 2px rgba(255, 255, 255, 0.5);
  transition: 0.3 ease;
}

.slider-navigation .nav-btn.active {
  background: #650f78;
}

.slider-navigation .nav-btn:hover {
  transform: scale(1.2)
}


.video-slide {
  position: absolute;
  width: 100%;
  clip-path: circle(2.9% at 0 50%);
}

.video-slide.active {
  clip-path: circle(150.0% at 0 50%);
  transition: 0.5s ease;
}



@media (max-width:1040px) {
  header {
    padding: 12px 20px;
  }

  section {
    padding: 100px 20px;
  }

  header .navigation {
    display: none;
  }

  header .navigation.active {
    position: fixed;
    width: 100%;
    height: 100vh;
    top: 0;
    left: 0;
    display: flex;
    justify-content: center;
    align-items: center;
    background: rgba(1, 1, 1, 0.5);
  }

  header .navigation .navigation-items a::before {
    background-color: rgb(0, 0, 0);
    height: 5px;
  }


  header .navigation.active .navigation-items {
    background: #fff;
    width: 600px;
    max-width: 600px;
    margin: 20px;
    padding: 40px;
    display: flex;
    flex-direction: column;
    align-items: center;
    border-radius: 5px;
    box-shadow: 0, 5px 25px rgb(1, 1, 1)
  }

  header .navigation .navigation-items a {
    color: #000;
    font-size: 1.2em;
    margin: 20px;
  }


}
</style>