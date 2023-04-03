
<template>
    <Vcode :show="showCheckCode" @success="success" @close="close" />
</template>
   
<script>
import Vcode from "vue-puzzle-vcode";
export default {
    props:['showCheckCode'],
    data() {
        return {
            isShow: false // 验证码模态框是否出现
        };
    },
    components: {
        Vcode
    },
    methods: {
        // 用户通过了验证
        success() {
            this.isShow = false; // 通过验证后，需要手动隐藏模态框
            this.$emit('success')
        },
        // 用户点击遮罩层，应该关闭模态框
        close() {
            this.isShow = false;
        }
    }
};
</script>