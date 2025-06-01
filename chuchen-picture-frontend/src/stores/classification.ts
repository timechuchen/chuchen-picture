import { ref, type Ref } from 'vue'
import { defineStore } from 'pinia'
import { listPictureTagCategoryUsingGet } from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'

/**
 * 存储后端分类相关的信息（store）
 */
export const tagCategoryOptionsStore = defineStore('tagCategoryOptions', () => {

  const categoryOptions = ref<string[]>([])
  const tagOptions = ref<string[]>([])
  const categoryList = ref<string[]>([])
  const tagList = ref<string[]>([])

  /**
   * 获取标签和分类选项
   */
  const getTagCategoryOptions = async () => {
    const res = await listPictureTagCategoryUsingGet()
    if (res.data.code === 0 && res.data.data) {

      // 转换成下拉选项组件接受的格式
      categoryList.value = res.data.data.categoryList ?? []
      tagList.value = res.data.data.tagList ?? []

      tagOptions.value = (res.data.data.tagList ?? []).map((data: string) => {
        return {
          value: data,
          label: data,
        }
      })
      categoryOptions.value = (res.data.data.categoryList ?? []).map((data: string) => {
        return {
          value: data,
          label: data,
        }
      })
    } else {
      // 获取失败
      message.error('获取标签或分类列表失败：' + res.data.message)
    }
  }

  return {
    categoryList,
    tagList,
    categoryOptions,
    tagOptions,
    getTagCategoryOptions
  }
})
