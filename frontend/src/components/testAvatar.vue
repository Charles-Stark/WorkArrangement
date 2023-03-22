<template>
  <div>
    <TestScoll></TestScoll>
  </div>
</template>

<script>
import TestScoll from '@/components/introduce/components/TestScoll.vue'
// import TopNav from '@/components/introduce/components/TopNav.vue' 
// import Login from '@/components/introduce/components/Login.vue' 
// import loginBox from '@/components/loginBox.vue';
// import registerBox from '@/components/registerBox.vue';
export default {
  components: { TestScoll},
  data: () => ({
    dialog: false,
    show: 1,
    snackBar: false,
    snackBarText: ''
  }),
  computed: {

  },
  methods: {
    getMsg(data) {
      this.snackBarText = data
      this.snackBar=true
    }
  },


}
</script>

