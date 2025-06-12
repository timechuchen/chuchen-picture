<template>
  <div id="addPicturePage">
    <h2 style="margin-bottom: 16px; text-align: center">
      {{ route.query?.id ? '修改图片' : '创建图片' }}
    </h2>
    <a-typography-paragraph v-if="spaceId" type="secondary">
      保存至空间：<a :href="`/space/${spaceId}`" target="_blank">{{ spaceId }}</a>
    </a-typography-paragraph>
    <!-- 选择上传方式 -->
    <a-tabs v-model:activeKey="uploadType">
      <a-tab-pane key="file" tab="文件上传">
        <PictureUpload :space-id="spaceId" :picture="picture" :onSuccess="onSuccess" />
      </a-tab-pane>
      <a-tab-pane key="url" tab="URL 上传" force-render>
        <UrlPictureUpload :space-id="spaceId" :picture="picture" :onSuccess="onSuccess" />
      </a-tab-pane>
    </a-tabs>
    <!-- 图片信息表单 -->
    <a-form
      v-if="picture"
      name="pictureFrom"
      layout="vertical"
      :model="pictureForm"
      @finish="handleSubmit"
    >
      <a-form-item name="name" label="名称">
        <a-input v-model:value="pictureForm.name" placeholder="请输入名称" allowClear />
      </a-form-item>
      <a-form-item name="introduction" label="简介">
        <a-textarea
          v-model:value="pictureForm.introduction"
          placeholder="请输入简介"
          :auto-size="{ minRows: 2, maxRows: 5 }"
          allowClear
        ></a-textarea>
      </a-form-item>
      <a-form-item name="category" label="分类">
        <a-auto-complete
          v-model:value="pictureForm.category"
          placeholder="请输入分类"
          :options="categoryOptions"
          allowClear
        />
      </a-form-item>
      <a-form-item name="tags" label="标签">
        <a-select
          mode="tags"
          v-model:value="pictureForm.tags"
          placeholder="请输入标签"
          :options="tagOptions"
          allowClear
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">
          {{ route.query?.id ? '修改' : '创建' }}
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import PictureUpload from '@/components/PictureUpload.vue'
import { computed, onMounted, reactive, ref } from 'vue'
import { editPictureUsingPost, getPictureVoByIdUsingGet } from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'
import { tagCategoryOptionsStore } from '@/stores/classification.ts'
import UrlPictureUpload from '@/components/UrlPictureUpload.vue'

const route = useRoute()
const router = useRouter()
const picture = ref<API.PictureVO>()
const uploadType = ref<'file' | 'url'>('file')
const pictureForm = reactive<API.PictureEditRequest>({})
// 空间 id
const spaceId = computed(() => {
  return route.query?.spaceId
})

/**
 * 图片上传成功
 * @param newPicture
 */
const onSuccess = (newPicture: API.PictureVO) => {
  picture.value = newPicture
  pictureForm.name = newPicture.name
}

// 提交
const handleSubmit = async (values: any) => {
  const pictureId = picture.value.id
  if (!pictureId) {
    return
  }
  let msg = route.query.id ? '编辑' : '创建'
  const res = await editPictureUsingPost({
    id: pictureId,
    spaceId: spaceId.value,
    ...values,
  })

  if (res.data.code === 0 && res.data.data) {
    // 提交成功
    message.success(msg + '成功')
    // 跳转到图片详情页
    await router.push({
      path: `/picture/${pictureId}`,
    })
  } else {
    message.error(msg + '失败：' + res.data.message)
  }
}

const tagCategoryOptions = tagCategoryOptionsStore()
const categoryOptions = ref<string[]>(tagCategoryOptions.categoryOptions)
const tagOptions = ref<string[]>(tagCategoryOptions.tagOptions)

// 获取老数据
const getOldPicture = async () => {
  // 获取到 ID
  const id = route.query?.id
  if (id) {
    const res = await getPictureVoByIdUsingGet({
      id,
    })
    if (res.data.code === 0 && res.data.data) {
      const data = res.data.data
      picture.value = data
      pictureForm.name = data.name
      pictureForm.introduction = data.introduction
      pictureForm.category = data.category
      pictureForm.tags = data.tags
    }
  }
}

onMounted(() => {
  getOldPicture()
})
</script>

<style scoped>
#addPicturePage {
  max-width: 720px;
  margin: 0 auto;
}
</style>
