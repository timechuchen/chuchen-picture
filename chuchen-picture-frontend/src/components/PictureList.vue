<template>
  <div class="picture-list">
    <Waterfall :list="pictureList" :width="300" :breakpoints="breakpoints">
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
              <!-- 使用这个办法先解决首次加载过慢出现图片叠加问题 -->
              <img
                :alt="item.name"
                :src="item.thumbnailUrl ?? item.url"
                :width="item.picWidth"
                :height="item.picHeight"
                loading="lazy"
              />
              <!--              <img :alt="item.name" :src="item.thumbnailUrl ?? item.url"/>-->
              <div class="mask-layer bottom-contain" :class="{ visible: isMaskVisible[index] }">
                <span style="font-size: small; font-weight: 400; color: #adffff"
                  >{{ item.picWidth }} x {{ item.picHeight }}</span
                >
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
          <template v-if="showOp" #actions>
            <a-tooltip placement="top">
              <template #title>
                <span>分享</span>
              </template>
              <share-alt-outlined @click="(e) => doShare(item, e)" />
            </a-tooltip>
            <a-tooltip placement="top">
              <template #title>
                <span>搜索类似图片</span>
              </template>
              <search-outlined @click="(e) => doSearch(item, e)" />
            </a-tooltip>
            <a-tooltip placement="top">
              <template #title>
                <span>编辑</span>
              </template>
              <edit-outlined @click="(e) => doEdit(item, e)" />
            </a-tooltip>
            <a-tooltip placement="top">
              <template #title>
                <span>删除</span>
              </template>
              <a-space
                @click="
                (e) => {
                  e.stopPropagation()
                }
              "
              >
                <a-popconfirm
                  title="确定删除？"
                  @confirm="doDelete(item)"
                  @cancel="
                  () => {
                    message.info('取消删除')
                  }
                "
                >
                  <delete-outlined />
                </a-popconfirm>
              </a-space>
            </a-tooltip>
          </template>
        </a-card>
      </template>
    </Waterfall>
    <ShareModal ref="shareModalRef" :link="shareLink" :title="'分享'"/>
  </div>
</template>

<script setup lang="ts">
import { Waterfall } from 'vue-waterfall-plugin-next'
import 'vue-waterfall-plugin-next/dist/style.css'

import { defineProps, ref } from 'vue'
import { handleDragStart } from '@/utils'
import { useRouter } from 'vue-router'
import {
  EditOutlined,
  DeleteOutlined,
  SearchOutlined,
  ShareAltOutlined,
} from '@ant-design/icons-vue'
import { deletePictureUsingPost } from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'
import ShareModal from '@/components/ShareModal.vue'

interface Props {
  pictureList: API.Picture[]
  showOp?: boolean
  onReload?: () => void
}

const isMaskVisible = ref<{ [key: number]: boolean }>({})

const props = withDefaults(defineProps<Props>(), {
  pictureList: () => [],
  showOp: false,
})
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

// 跳转到图片详情页面
const router = useRouter()
const doClickPicture = (picture: API.PictureVO) => {
  router.push({
    path: `/picture/${picture.id}`,
  })
}

const doEdit = (picture, e) => {
  // 阻止冒泡
  e.stopPropagation()
  router.push({
    path: '/add_picture',
    query: {
      id: picture.id,
      spaceId: picture.spaceId,
    },
  })
}

const doSearch = (picture, e) => {
  // 阻止冒泡
  e.stopPropagation()
  // 打开新的页面
  // window.open(`/search_picture?pictureId=${picture.id}`)

  message.warn('该功能暂时关闭')
}

// 分享弹窗引用
const shareModalRef = ref()
// 分享链接
const shareLink = ref<string>()
// 分享
const doShare = (picture: API.PictureVO, e: Event) => {
  e.stopPropagation()
  shareLink.value = `${window.location.protocol}//${window.location.host}/picture/${picture.id}`
  if (shareModalRef.value) {
    shareModalRef.value.openModal()
  }
}

const doDelete = async (picture) => {
  const id = picture.id
  if (!id) {
    return
  }
  const res = await deletePictureUsingPost({ id })
  if (res.data.code === 0) {
    message.success('删除成功')
    // 更新照片列表
    props.onReload?.()
  } else {
    message.error('删除失败')
  }
}
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
  background: linear-gradient(
    to top,
    rgba(128, 128, 128, 0.7),
    rgba(128, 128, 128, 0)
  ); /* Gradient from gray to transparent */
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
  display: grid; /* 启用 Grid 布局 */
  align-items: end; /* 垂直对齐到底部 */
  line-height: 1; /* 移除行高 */
}

.bottom-contain span {
  margin: 0; /* 移除外边距 */
  padding: 0; /* 移除内边距 */
}
</style>
