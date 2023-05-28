<!-- eslint-disable vue/multi-word-component-names -->
<template>
    <div class="RotateImage">
        <section>
            <div><img src="../assets/5-1.png" alt=""></div>
            <div><img src="../assets/5-2.png" alt=""></div>
            <div><img src="../assets/5-3.png" alt=""></div>
            <div><img src="../assets/5-4.png" alt=""></div>
            <div><img src="../assets/5-5.png" alt=""></div>
            <div><img src="../assets/5-6.png" alt=""></div>
        </section>
    </div>
</template>


<script>
export default {
    data() {
        return {

        }
    },
}
</script>

<style scoped>
* {
    /* 初始化 取消页面的内外边距 */
    margin: 0;
    padding: 0;
}

body {
    /* 弹性布局 水平、垂直居中 */
    display: flex;
    justify-content: center;
    align-items: center;
    /* 100%窗口高度 */
    height: 100vh;
    background-color: #000;
    /* 视距 让元素看起来更有3D效果 */
    perspective: 900px;
}

.RotateImage {
    /* position: relative; */
    margin: 20vh auto;
}

section {
    /* position: absolute;
	left: 20vw; */
    width: 30vw;
    height: 30vh;
    cursor: pointer;
    /* 让其子元素位于3D空间中 */
    transform-style: preserve-3d;
    /* 执行动画：动画名称 时长 线性的 无限次播放 */
    animation: rotate 20s linear infinite;
}

section:hover {
    /* 鼠标移上动画暂停 */
    animation-play-state: paused;
}

section div {
    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    text-align: center;
    /* border: #000; */
    /* 设置元素的倒影效果，below是倒影效果在元素下方，15px是元素和倒影的距离，后面的属性是设置倒影渐变 */
    -webkit-box-reflect: below 15px -webkit-linear-gradient(transparent 50%, rgba(255, 255, 255, 0.3));
}

section div img {
    width: 100%;
    height: 100%;
}

section div:nth-child(1) {
    transform: rotateY(0deg) translateZ(30vw);
}

section div:nth-child(2) {
    transform: rotateY(60deg) translateZ(30vw);
}

section div:nth-child(3) {
    transform: rotateY(120deg) translateZ(30vw);
}

section div:nth-child(4) {
    transform: rotateY(180deg) translateZ(30vw);
}

section div:nth-child(5) {
    transform: rotateY(240deg) translateZ(30vw);
}

section div:nth-child(6) {
    transform: rotateY(300deg) translateZ(30vw);
}

/* section div:nth-child(7){
    transform: rotateY(270deg) translateZ(300px);
}
section div:nth-child(8){
    transform: rotateY(315deg) translateZ(300px);
} */

/* 定义旋转动画 */
@keyframes rotate {
    0% {
        transform: rotateY(0deg);
    }

    100% {
        transform: rotateY(360deg);
    }
}</style>