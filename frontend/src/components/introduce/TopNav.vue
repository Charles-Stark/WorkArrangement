<template>
    <div class="topnav">
        <header>
            <img :src="require('../../assets/logo-md-dark.png')" class="logoImg" />
            <div class="hidden-class">
                <ul>
                    <li><a href="#1">首页</a></li>
                    <li><a href="#2">核心业务</a></li>
                    <li><a href="#3">产品优势</a></li>
                    <li><a href="#4">系统亮点</a></li>
                    <li><a href="#5">项目品鉴</a></li>
                </ul>
            </div>
            <ul>
                <li v-if="$store.state.userId!==null && $store.state.token!==null"><router-link to="controlpanel">控制台</router-link> </li>
                <li><accountStatus></accountStatus></li>
            </ul>
       
        </header>
    </div>
</template>

<script>
import accountStatus from '@/components/introduce/accountStatus.vue' 
export default {
    components: { accountStatus },
    data() {
        return {
            value: "",
            style: { backgroundColor: "" },
            scrollTop: 0,
        }
    },
    methods: {
        handleScroll() {
            let scrollTop =
                window.pageYOffset ||
                document.documentElement.scrollTop ||
                document.body.scrollTop;

            if (scrollTop >= 100) {
                this.style.backgroundColor = `rgba(131, 59, 196,${scrollTop / (scrollTop + 50)
                    }) `;
            } else if (scrollTop == 0) {
                this.style.backgroundColor = "transparent";
            }
        },
    },
    mounted() {
        window.addEventListener("scroll", this.handleScroll);
    },
    beforeDestroy() {
        window.removeEventListener("scroll", this.handleScroll);
    },
    // mounted() {
    //     window.addEventListener("scroll", this.handleScroll);
    // },
    // //由于是在整个window中添加的事件，所以要在页面离开时摧毁掉，否则会报错
    // beforeDestroy() {
    //     window.removeEventListener("scroll", this.handleScroll);
    // },
    // methods: {
    //     handleScroll() {
    //         let scrollTop =
    //             window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop;
    //         let offsetTop = document.querySelector("#searchBar").offsetTop;
    //         //设置背景颜色的透明读
    //         if (scrollTop) {
    //             this.linkObj.bag = `rgba(2, 150, 247,${scrollTop / (scrollTop + 43.88)})`;
    //         } else if (scrollTop == 0) {
    //             this.linkObj.bag = "transparent";
    //         }
    //     }
    // }
    // // rem下获取顶部高度   document.getElementById("id").offsetHeight 

}

</script>

<style scoped>
/* 引入网络字体（Poppins） */
@import url("http://fonts.googleapis.com/css?family=Poppins:200,300,400,500,600,700,800,900&display=swap");

* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
    font-family: "Poppins";
    scroll-behavior:smooth;
}

body {
    /* 默认最小高度 2屏 */
    min-height: 200vh;
    background-color: #56094597;
}


header {
    /* 固定定位 */
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 5vw;
    /* 弹性布局 */
    display: flex;
    /* 将元素靠边对齐 */
    justify-content: space-between;
    align-items: center;
    padding: 15px 60px;
    z-index: 999;
    /* 动画过渡 */
    transition: 0.6s;
    font-size: 25px;
    background-color: #1c022d89;
}


header ul {
    display: flex;
    justify-content: center;
    align-items: center;
}

header ul li {
    list-style: none;
    flex-grow: 1;
}

header ul li a {
    margin: 0 1.5vw;
    text-decoration: none;
    color: #fff;
    font-weight: 500;
    letter-spacing: 2px;
    transition: 0.6s;
    font-size: 1.5vw;
}


/* 鼠标滚动后，改变导航栏样式 */

.logoImg {
    height: 3vw;
}

@media screen and (max-width: 768px) {
  .hidden-class {
    display: none;
  }
}
</style>