<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="big-container">
    <div class="col" v-for="(item, index) in items" :key="index">
      <div class="container">
        <div class="front" :style="{ backgroundImage: `url(${item.frontImgUrl})` }">
          <div class="inner">
            <p>{{ item.title }}</p>
          </div>
        </div>
        <div class="back">
          <div class="inner">
            <p>{{ item.content }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      items: [
        {
          title: '门店业务预测',
          frontImgUrl: require('@/assets/1-1.png'),
          content: '基于门店历史业务数据，利用人工智能算法，精准实现门店未来三十天及以上的业务预测，系统实现数据可视化',
        },
        {
          title: '劳动力管理预测',
          frontImgUrl: require('@/assets/1-2.png'),
          content: '基于门店业务预测数据，结合数据的波动情况，细化每天不同时段的门店劳动力需求量，实现岗位匹配，人力调用契合',
        },
        {
          title: '高效智能排班',
          frontImgUrl: require('@/assets/1-3.png'),
          content: '门店管理人员可一键生成排班表，并根据员工的请假反馈进行及时调整，从而得到更人性化、更高质量的员工排班表',
        },
      ],
    }
  },
}
</script>

<style scoped>
*{
  margin: 0;
  padding: 0;
}

.big-container{
	
	display: flex;
	/* height: 100vh; */
  margin-top: 2vh;
	align-items: center;
	justify-content: center;
}

.col{
  width: calc(25% - 2rem);
  margin: 1rem;
  cursor: pointer;
}

.container{
    transform-style: preserve-3d;
	perspective: 1000px;
}

.front,
.back{
  background-size: cover;
  box-shadow: 0 4px 8px 0 rgba(0,0,0,0.25);
  border-radius: 10px;
  background-position: center;
  transition: transform .7s cubic-bezier(0.4, 0.2, 0.2, 1);
  text-align: center;
  min-height: 280px;
  height: auto;
  border-radius: 10px;
  color: #fff;
  font-size: 1.5rem;
}
.container:hover .front,
.container:hover .back{
    transition: transform .7s cubic-bezier(0.4, 0.2, 0.2, 1);
}


.back{
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
}

.inner{

    transform: translateY(-50%) translateZ(60px);
    top: 50%;
    position: absolute;

    width: 100%;
    padding: 2rem;
    box-sizing: border-box;
    /* outline: 1px solid transparent; */
    z-index: 2;
}

.container .back{
    transform: rotateY(180deg);
    transform-style: preserve-3d;
}
.container .front{

    transform: rotateY(0deg);
    transform-style: preserve-3d;
}
.container:hover .back{
    transform: rotateY(0deg);
    transform-style: preserve-3d;
}
.container:hover .front{
	transform: rotateY(-180deg);
    transform-style: preserve-3d;
}



.front .inner p{
  font-size: 2rem;
  margin-bottom: 2rem;
  position: relative;
}
.front .inner p:after{
  content: '';
  width: 4rem;
  height: 2px;
  position: absolute;
  background: #C6D4DF;
  display: block;
  left: 0;
  right: 0;
  margin: 0 auto;
  bottom: -.75rem;
}
</style>