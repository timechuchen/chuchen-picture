<template>
  <div id="addPictureBatchPage">
    <h2 style="margin-bottom: 16px; text-align: center">批量创建图片</h2>
    <!-- 图片信息表单 -->
    <a-form name="pictureFrom" layout="vertical" :model="formData" @finish="handleSubmit">
      <a-form-item name="name" label="关键词">
        <a-input v-model:value="formData.searchWord" placeholder="请输入关键词" allowClear />
      </a-form-item>
      <a-form-item name="count" label="抓取数量">
        <a-input-number
          style="min-width: 180px"
          v-model:value="formData.count"
          :min="1"
          :max="30"
          allowClear
        ></a-input-number>
      </a-form-item>
      <a-form-item name="namePrefix" label="名称前缀">
        <a-input v-model:value="formData.namePrefix" placeholder="请输入名称前缀" allowClear />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%" :loading="loading">
          批量提交
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { uploadPictureByBatchUsingPost } from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const picture = ref<API.PictureVO>()
const formData = reactive<API.PictureUploadByBatchRequest>({
  count: 10,
})

// 提交
const loading = ref<boolean>(false)
const handleSubmit = async (values: any) => {
  loading.value = true
  try {
    const res = await uploadPictureByBatchUsingPost({
      ...formData,
    })

    if (res.data.code === 0 && res.data.data) {
      // 提交成功
      message.success(`创建成功，共 ${res.data.data} 条`)
      // 跳转到图片详情页
      await router.push({
        path: `/`,
      })
    } else {
      message.error('失败：' + res.data.message)
    }
  }catch (e) {
    message.error('失败：' + e)
  }finally {
    loading.value = false
  }
}
</script>

<style scoped>
#addPictureBatchPage {
  max-width: 720px;
  margin: 0 auto;
}
</style>
