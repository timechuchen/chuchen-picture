<template>
  <div id="pictureDetailPage">
    <a-row v-if="isPicture" :gutter="[16, 16]">
      <!-- 图片展示区 -->
      <a-col :sm="24" :md="16" :xl="18">
        <a-card title="图片预览">
          <a-image style="max-height: 600px; object-fit: contain" :src="picture.url" />
        </a-card>
      </a-col>
      <!-- 图片信息区 -->
      <a-col :sm="24" :md="8" :xl="6">
        <a-card title="图片信息">
          <a-descriptions :column="1">
            <a-descriptions-item label="作者">
              <a-space>
                <a-avatar :size="24" :src="picture.user?.userAvatar" />
                <div>{{ picture.user?.userName }}</div>
              </a-space>
            </a-descriptions-item>
            <a-descriptions-item label="名称">
              {{ picture.name ?? '未命名' }}
            </a-descriptions-item>
            <a-descriptions-item label="简介">
              {{ picture.introduction ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="分类">
              {{ picture.category ?? '默认' }}
            </a-descriptions-item>
            <a-descriptions-item label="标签">
              <div v-if="!picture.tags">-</div>
              <a-tag v-else v-for="tag in picture.tags" :key="tag">
                {{ tag }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="格式">
              {{ picture.picFormat ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="宽度">
              {{ picture.picWidth ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="高度">
              {{ picture.picHeight ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="宽高比">
              {{ picture.picScale ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="大小">
              {{ formatSize(picture.picSize) }}
            </a-descriptions-item>
            <a-descriptions-item label="主色调">
              <a-space>
                {{ picture.picColor ?? '-' }}
                <div
                  v-if="picture.picColor"
                  :style="{
                    width: '16px',
                    height: '16px',
                    backgroundColor: toHexColor(picture.picColor),
                  }"
                />
              </a-space>
            </a-descriptions-item>
          </a-descriptions>
          <!-- 图片操作 -->
          <a-space wrap>
            <a-button type="primary" @click="doDownload">
              免费下载
              <template #icon>
                <DownOutlined />
              </template>
            </a-button>
            <div v-if="canEdit">
              <a-button
                danger
                @click="
                  () => {
                    open = true
                  }
                "
                >拒绝展示
              </a-button>
              <a-modal :mask="false" v-model:open="open" title="拒绝原因" @ok="handleReview()">
                <a-input v-model:value="inputReviewMessage" placeholder="请输入拒绝原因" />
              </a-modal>
            </div>
            <a-button type="primary"  @click="doShare" :icon="h(ShareAltOutlined)">
              分享
            </a-button>
            <a-button v-if="canEdit" type="default" @click="doEdit">
              编辑
              <template #icon>
                <EditOutlined />
              </template>
            </a-button>
            <a-popconfirm
              v-if="canDelete"
              title="确定删除？"
              @confirm="doDelete"
              @cancel="
                () => {
                  message.info('取消删除')
                }
              "
            >
              <a-button v-if="canEdit" danger>
                删除
                <template #icon>
                  <DeleteOutlined />
                </template>
              </a-button>
            </a-popconfirm>
          </a-space>
        </a-card>
      </a-col>
    </a-row>
    <a-empty v-else description="不存在该照片" />
    <ShareModel ref="shareModalRef" :link="shareLink" :title="'分享'"/>
  </div>
</template>

<script setup lang="ts">
import { computed, h, onMounted, ref } from 'vue'
import {
  deletePictureUsingPost,
  doPictureReviewUsingPost,
  getPictureVoByIdUsingGet,
} from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'
import { EditOutlined, DeleteOutlined, DownOutlined, ShareAltOutlined } from '@ant-design/icons-vue'
import { downloadImage, formatSize, toHexColor } from '@/utils'
import router from '@/router'
import { PIC_REVIEW_STATUS_ENUM } from '@/constants/picture.ts'
import ShareModel from '@/components/ShareModal.vue'
import { SPACE_PERMISSION_ENUM } from '@/constants/space.ts'

const props = defineProps<{
  id: string | number
}>()
const isPicture = ref<boolean>(false)

const picture = ref<API.PictureVO>({})

// 通用权限检查函数
function createPermissionChecker(permission: string) {
  return computed(() => {
    return (picture.value.permissionList ?? []).includes(permission)
  })
}

// 定义权限检查
const canEdit = createPermissionChecker(SPACE_PERMISSION_ENUM.PICTURE_EDIT)
const canDelete = createPermissionChecker(SPACE_PERMISSION_ENUM.PICTURE_DELETE)

// 获取图片详情
const fetchPictureDetail = async () => {
  try {
    const res = await getPictureVoByIdUsingGet({
      id: props.id,
    })
    if (res.data.code === 0 && res.data.data) {
      picture.value = res.data.data
      isPicture.value = true
    } else {
      message.error('获取图片详情失败，' + res.data.message)
    }
  } catch (e: any) {
    message.error('获取图片详情失败：' + e.message)
  }
}

// 编辑
const doEdit = () => {
  // router.push('/add_picture?id=' + picture.value.id)
  router.push({
    path: '/add_picture',
    query: {
      id: picture.value.id,
      spaceId: picture.value.spaceId,
    },
  })
}
// 删除
const doDelete = async () => {
  const id = picture.value.id
  if (!id) {
    return
  }
  const res = await deletePictureUsingPost({ id })
  if (res.data.code === 0) {
    message.success('删除成功')
    router.back()
  } else {
    message.error('删除失败')
  }
}

// 下载图片
// 处理下载
const doDownload = () => {
  downloadImage(picture.value.url)
}

const open = ref<boolean>(false)
const inputReviewMessage = ref<string>('审核拒绝')
const handleReview = async () => {
  const res = await doPictureReviewUsingPost({
    id: picture.value.id,
    reviewStatus: PIC_REVIEW_STATUS_ENUM.REJECT,
    reviewMessage: inputReviewMessage.value,
  })
  if (res.data.code === 0) {
    message.success('图片拒绝展示成功')
    // 跳转路由
    router
      .push({
        path: '/',
        isReplace: true,
      })
      .then()
  } else {
    message.error('图片拒绝展示失败：' + res.data.message)
  }
  open.value = false
}

// 分享弹窗引用
const shareModalRef = ref()
// 分享链接
const shareLink = ref<string>()
// 分享
const doShare = () => {
  shareLink.value = `${window.location.protocol}//${window.location.host}/picture/${picture.value.id}`
  if (shareModalRef.value) {
    shareModalRef.value.openModal()
  }
}

onMounted(() => {
  fetchPictureDetail()
})
</script>

<style scoped>
#pictureDetailPage {
}
</style>
