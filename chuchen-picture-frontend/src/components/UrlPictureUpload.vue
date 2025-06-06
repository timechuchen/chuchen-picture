<template>
  <div class="url-picture-upload">
    <a-input-group compact style="margin-bottom: 16px">
      <a-input v-model:value="fileUrl" style="width: calc(100% - 120px)" placeholder="请输入图片 URL" />
      <a-button type="primary" :loading="loading" @click="handleUpload" style="width: 120px">提交</a-button>
    </a-input-group>
    <div class="image-wrapper">
      <img v-if="picture?.url" :src="picture?.url" alt="avatar" />
    </div>
  </div>
</template>
<script lang="ts" setup>
import { ref } from 'vue'
import { message } from 'ant-design-vue'
import { uploadPictureByUrlUsingPost } from '@/api/pictureController.ts'

interface Props {
  picture?: API.PictureVO
  onSuccess?: (newPicture: API.PictureVO) => void
}

const props = defineProps<Props>()
const loading = ref<boolean>(false)
const fileUrl = ref<string>('')

/**
 * 上传图片
 */
const handleUpload = async () => {
  loading.value = true
  try {
    const params:API.PictureUploadRequest = {fileUrl: fileUrl.value}
    if(props.picture) {
      params.id = props.picture.id
    }
    const res = await uploadPictureByUrlUsingPost(params)
    if (res.data.code === 0 && res.data.data) {
      message.info('图片上传成功')
      // 将上传成功的信息传递给父组件
      props.onSuccess?.(res.data.data)
    }else {
      message.error('图片上传失败：' + res.data.message)
    }
  }catch (e) {
    console.error('图片上传失败：' + e)
    message.error('图片上传失败：' + e.message)
  }finally {
    loading.value = false
  }
}
</script>
<style scoped>
.url-picture-upload {
  margin-bottom: 16px;
}

.url-picture-upload img {
  max-width: 100%;
  max-height: 480px;
}

.url-picture-upload .image-wrapper {
  text-align: center;
  margin-top: 16px;
}
</style>
