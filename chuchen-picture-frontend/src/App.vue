<template>
  <div id="app">
    <BasicLayout />
  </div>
</template>

<script setup lang="ts">
import BasicLayout from '@/layouts/BasicLayout.vue'
import { healthUsingGet } from '@/api/mainController.ts'

// 检测后端连接是否正常
healthUsingGet().then((res) => {
  // console.log(res)
}).catch(e => {
  console.error(e)
})
</script>

<style scoped></style>
