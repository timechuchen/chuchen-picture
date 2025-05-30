<template>
  <div id="app">
    <BasicLayout />
  </div>
</template>

<script setup lang="ts">
import BasicLayout from '@/layouts/BasicLayout.vue'
import { healthUsingGet } from '@/api/mainController.ts'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'

// 检测后端连接是否正常
healthUsingGet().then((res) => {
  console.log(res)
})

// 获取登陆信息
const loginUserStore = useLoginUserStore()
loginUserStore.fetchLoginUser()
</script>

<style scoped></style>
