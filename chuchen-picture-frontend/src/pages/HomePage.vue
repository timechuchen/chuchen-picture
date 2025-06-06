<template>
  <div id="homePage">
    <!-- 搜索框 -->
    <div class="search-bar">
      <a-input-search
        placeholder="从海量图片中搜索"
        v-model:value="searchParams.searchText"
        enter-button="搜索"
        size="large"
        @search="doSearch"
      />
    </div>
    <!-- 分类 + 标签 -->
    <a-tabs v-model:activeKey="selectedCategory" @change="doSearch">
      <a-tab-pane key="all" tab="全部" />
      <a-tab-pane v-for="category in categoryList" :key="category" :tab="category" />
    </a-tabs>
    <div class="tag-bar">
      <span style="margin-right: 8px">标签：</span>
      <a-space :size="[0, 8]" wrap>
        <a-checkable-tag
          v-for="(tag, index) in tagList"
          :key="tag"
          v-model:checked="selectedTagList[index]"
          @change="doSearch"
        >
          {{ tag }}
        </a-checkable-tag>
      </a-space>
    </div>
    <PictureHome
      v-if="dataList.length > 0"
      :picture-list="dataList"
      :do-click-picture="doClickPicture"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { listPictureVoByPageUsingPost } from '@/api/pictureController.ts'
import { Empty, message } from 'ant-design-vue'
import { tagCategoryOptionsStore } from '@/stores/classification.ts'
import { useRoute, useRouter } from 'vue-router'
import PictureHome from '@/components/PictureHome.vue'

// 定义数据
const loading = ref<boolean>(true)
const dataList = ref<API.Picture[]>([])
const total = ref<number>(0)

const route = useRoute()

// 索搜条件
const searchParams = reactive<API.PictureQueryRequest>({
  current: 1,
  pageSize: 20,
  sortField: 'createTime',
  sortOrder: 'descend',
})

// 定义分页
const pagination = computed(() => {
  return {
    current: searchParams.current,
    pageSize: searchParams.pageSize,
    total: total.value,
    onChange: (page: number, pageSize: number) => {
      searchParams.current = page
      searchParams.pageSize = pageSize
      fetchData()
    },
  }
})

const doSearch = () => {
  router.push({ query: { category: selectedCategory.value } });
  // 重置搜索条件
  searchParams.current = 1
  fetchData()
}

const tagCategoryOptions = tagCategoryOptionsStore()
const categoryList = ref<string[]>(tagCategoryOptions.categoryList)
const selectedCategory = ref<string>('all')
const tagList = ref<string[]>(tagCategoryOptions.tagList)
const selectedTagList = ref<string[]>([])

const fetchData = async () => {
  loading.value = true
  // 转换搜索参数
  const params = {
    ...searchParams,
    tags: [],
  }
  if (selectedCategory.value !== 'all') {
    params.category = selectedCategory.value
  }
  selectedTagList.value.forEach((useTag, index) => {
    if (useTag) {
      params.tags.push(tagList.value[index])
    }
  })
  const res = await listPictureVoByPageUsingPost(params)
  if (res.data.data) {
    dataList.value = res.data.data.records ?? []
    total.value = res.data.data.total ?? 0
  } else {
    message.error('获取数据失败，' + res.data.message)
  }
  loading.value = false
}

// 跳转到图片详情页面
const router = useRouter()
const doClickPicture = (picture: API.PictureVO) => {
  router.push({
    path: `/picture/${picture.id}`,
  })
}

// 页面加载的时候获取一次数据
onMounted(() => {
  const category = route.query?.category
  if (category) {
    selectedCategory.value = category
  }
  // const tags = route.query?.tags
  // if (tags) {
  //   const tagList = tags.split(',')
  //   tagList.forEach((tag, index) => {
  //     selectedTagList.value[index] = tag
  //   })
  // }
  fetchData()
})
</script>

<style scoped>
#homePage {
  margin-bottom: 16px;
}

#homePage .search-bar {
  max-width: 480px;
  margin: 0 auto 16px;
}

#homePage .tag-bar {
  margin-bottom: 20px;
}
</style>
