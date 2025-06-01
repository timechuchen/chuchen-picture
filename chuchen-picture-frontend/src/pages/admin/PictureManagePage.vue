<template>
  <div id="pictureManagePage">
    <!-- 搜索栏 -->
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="关键词">
        <a-input
          v-model:value="searchParams.searchText"
          placeholder="从名称和简介搜索"
          allowClear
        />
      </a-form-item>
      <a-form-item label="类型">
        <a-auto-complete
          v-model:value="searchParams.category"
          placeholder="请输入分类"
          :options="categoryOptions"
          style="min-width: 180px"
          allow-clear
        />
      </a-form-item>
      <a-form-item label="标签">
        <a-select
          v-model:value="searchParams.tags"
          mode="tags"
          placeholder="请输入标签"
          style="min-width: 180px"
          :options="tagOptions"
          allow-clear
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">搜索</a-button>
      </a-form-item>
    </a-form>
    <!-- 表格 -->
    <a-table
      :columns="columns"
      :data-source="dataList"
      :loading="loading"
      :pagination="pagination"
      @change="doTableChange"
      scroll=""
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'url'">
          <a-image :src="record.url" :width="60"></a-image>
        </template>
        <template v-if="column.dataIndex === 'tags'">
          <a-space wrap>
            <a-tag v-for="tag in JSON.parse(record.tags || '[]')" :key="tag">
              {{ tag }}
            </a-tag>
          </a-space>
        </template>
        <template v-if="column.dataIndex === 'introduction'">
          {{ record.introduction ? record.introduction : '暂无简介' }}
        </template>
        <template v-if="column.dataIndex === 'picInfo'">
          <div>格式： {{ record.picFormat }}</div>
          <div>宽度： {{ record.picWidth }}</div>
          <div>高度： {{ record.picHeight }}</div>
          <div>宽高比： {{ record.picScale }}</div>
          <div>大小： {{ (record.picSize / 1024).toFixed(1) }} KB</div>
        </template>
        <template v-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm') }}
        </template>
        <template v-if="column.dataIndex === 'editTime'">
          {{ dayjs(record.editTime).format('YYYY-MM-DD HH:mm') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-button type="link" :href="`/add_picture?id=${record.id}`" target="_blank">编辑</a-button>
          <a-popconfirm
            title="确定删除？"
            @confirm="doDelete(record.id)"
            @cancel="
              () => {
                message.info('取消删除')
              }
            "
          >
            <a-button danger>删除</a-button>
          </a-popconfirm>
        </template>
      </template>
    </a-table>
  </div>
</template>
<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { deletePictureUsingPost, listPictureByPageUsingPost } from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import { tagCategoryOptionsStore } from '@/stores/classification.ts'

const loading = ref<boolean>(true)
// 定义表格列
const columns = [
  {
    title: 'id',
    dataIndex: 'id',
    width: 80,
  },
  {
    title: '图片',
    dataIndex: 'url',
  },
  {
    title: '名称',
    dataIndex: 'name',
  },
  {
    title: '简介',
    dataIndex: 'introduction',
    ellipsis: true,
  },
  {
    title: '类型',
    dataIndex: 'category',
  },
  {
    title: '标签',
    dataIndex: 'tags',
  },
  {
    title: '图片信息',
    dataIndex: 'picInfo',
  },
  {
    title: '用户 id',
    dataIndex: 'userId',
    width: 80,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '编辑时间',
    dataIndex: 'editTime',
  },
  {
    title: '操作',
    key: 'action',
  },
]
// 定义数据
const dataList = ref<API.Picture[]>([])
const total = ref<number>(0)

// 索搜条件
const searchParams = reactive<API.PictureQueryRequest>({
  current: 1,
  pageSize: 10,
  sortField: 'createTime',
  sortOrder: 'descend',
})
// 定义分页
const pagination = computed(() => {
  return {
    current: searchParams.current,
    pageSize: searchParams.pageSize,
    total: total.value,
    showSizeChanger: true,
    showTotal: (total: number) => `共 ${total} 条`,
  }
})

const fetchData = async () => {
  if (!loading.value) {
    loading.value = true
  }
  const res = await listPictureByPageUsingPost({ ...searchParams })
  if (res.data.code === 0 && res.data.data) {
    dataList.value = res.data.data.records ?? []
    total.value = res.data.data.total ?? 0
  } else {
    message.error('获取数据失败：' + res.data.message)
  }
  loading.value = false
}
// 分页改变的时候触发
const doTableChange = (page: any) => {
  searchParams.current = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}
// 搜索
const doSearch = () => {
  searchParams.current = 1
  fetchData()
}
// 删除
const doDelete = async (id: string) => {
  if (!id) {
    return
  }
  const res = await deletePictureUsingPost({ id })
  if (res.data.code === 0) {
    message.success('删除成功')
    // 刷新数据
    await fetchData()
  } else {
    message.error('删除失败：' + res.data.message)
  }
}

const tagCategoryOptions = tagCategoryOptionsStore()
const categoryOptions = ref<string[]>(tagCategoryOptions.categoryOptions)
const tagOptions = ref<string[]>(tagCategoryOptions.tagOptions)

// 页面加载的时候获取一次数据
onMounted(() => {
  fetchData()
})
</script>
