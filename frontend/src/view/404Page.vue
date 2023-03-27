<template>
  <div class="text-center my-auto mx-auto">
      <p class="text-h1">404</p>
      <p class="text-h5">您请求的资源不存在,正在返回首页 {{ count }}...</p>
  </div>
</template>

<script>
export default {

  data: () => ({
    count:3
  }),
  created() {
    var counter=setInterval(() => {
      this.count--
      if(this.count<=0){
        clearInterval(counter)
        this.$router.push('/')
      }
    }, 1000);
  }


}
</script>
