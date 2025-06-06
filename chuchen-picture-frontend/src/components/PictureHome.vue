<template>
  <div>
    <Waterfall :list="pictureList" :width="300" :breakpoints="breakpoints" :lazyload="true">
      <template #default="{ item, url, index }">
        <a-card hoverable @click="doClickPicture(item)" @dragstart="handleDragStart">
          <!-- @dragstart="handleDragStart" 禁止拖拽 -->
          <!--          <div @dragstart="handleDragStart" @click="doClickPicture(item)">-->
          <!--            <LazyImg :url="item.picUrl" />-->
          <!--          </div>-->
          <template #cover>
            <!--            <img :alt="item.name" :src="item.url" @mouseenter="isMaskVisible = true" @mouseleave="isMaskVisible = false"/>-->
            <div
              class="image-container"
              @mouseenter="isMaskVisible[index] = true"
              @mouseleave="isMaskVisible[index] = false"
            >
              <img :alt="item.name" :src="item.url" />
              <div class="mask-layer bottom-contain" :class="{ visible: isMaskVisible[index] }">
                <span style="font-size: small; font-weight: 400; color: #adffff">{{ item.picWidth}} x {{ item.picHeight}}</span>
              </div>
            </div>
          </template>
          <a-card-meta :title="item.name">
            <template #description>
              <a-flex>
                <a-tag color="green">
                  {{ item.category ?? '默认' }}
                </a-tag>
                <a-tag v-for="tag in item.tags" :key="tag">
                  {{ tag }}
                </a-tag>
              </a-flex>
            </template>
          </a-card-meta>
        </a-card>
      </template>
    </Waterfall>
  </div>
</template>

<script setup lang="ts">
import { LazyImg, Waterfall } from 'vue-waterfall-plugin-next'
import 'vue-waterfall-plugin-next/dist/style.css'

import { defineProps, ref } from 'vue'
import { handleDragStart } from '@/utils'

interface Props {
  pictureList: Array
  doClickPicture: Function
}

const props = defineProps<Props>()

const isMaskVisible = ref<{ [key: number]: boolean }>({})

/**
 * 瀑布流布局
 */
const breakpoints = ref({
  3000: {
    //当屏幕宽度小于等于3000
    rowPerView: 7, // 一行8图
  },
  1800: {
    rowPerView: 6,
  },
  1500: {
    rowPerView: 5,
  },
  1200: {
    rowPerView: 4,
  },
  1000: {
    rowPerView: 3,
  },
  800: {
    rowPerView: 2,
  },
  700: {
    rowPerView: 2,
  },
  500: {
    rowPerView: 1,
  },
  300: {
    rowPerView: 1,
  },
})
</script>

<style scoped>
.image-container {
  position: relative;
  overflow: hidden;
}

.image-container img {
  display: block;
  width: 100%;
  height: auto;
}

.mask-layer {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 20%; /* Adjust height of the gradient strip */
  background: linear-gradient(to top, rgba(128, 128, 128, 0.7), rgba(128, 128, 128, 0)); /* Gradient from gray to transparent */
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: 1; /* Ensure mask is above the image */
}

.mask-layer.visible {
  opacity: 1;
}

:deep(.ant-card) {
  transition: all 0.3s ease;
}

.bottom-contain {
  display: grid;          /* 启用 Grid 布局 */
  align-items: end;       /* 垂直对齐到底部 */
  line-height: 1; /* 移除行高 */
}

.bottom-contain span {
  margin: 0; /* 移除外边距 */
  padding: 0; /* 移除内边距 */
}
</style>
