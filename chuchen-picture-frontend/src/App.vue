<template>
  <div id="app">
    <BasicLayout />
  </div>
</template>

<script setup lang="ts">
import BasicLayout from '@/layouts/BasicLayout.vue'
import { healthUsingGet } from '@/api/mainController.ts'
import { tagCategoryOptionsStore } from '@/stores/classification.ts'

// 检测后端连接是否正常
healthUsingGet().then((res) => {
  // console.log(res)
}).catch(e => {
  console.error(e)
})

// 获取到标签
const tagCategoryOptions = tagCategoryOptionsStore()
tagCategoryOptions.getTagCategoryOptions()
</script>

<style scoped></style>
