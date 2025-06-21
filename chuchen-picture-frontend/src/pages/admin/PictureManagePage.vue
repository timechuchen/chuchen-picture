<template>
  <div id="pictureManagePage">
    <a-flex justify="space-between">
      <h2>图片管理</h2>
      <a-space>
        <a-button type="primary" href="/add_picture" target="_blank">+ 创建图片</a-button>
        <a-button type="primary" href="/add_picture/batch" target="_blank" ghost
          >+ 批量创建图片</a-button
        >
      </a-space>
    </a-flex>
    <div style="margin-bottom: 16px" />
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
      <a-form-item label="审核状态" name="reviewStatus">
        <a-select
          v-model:value="searchParams.reviewStatus"
          :options="PIC_REVIEW_STATUS_OPTIONS"
          placeholder="请输入审核状态"
          style="min-width: 180px"
          allow-clear
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">搜索</a-button>
      </a-form-item>
    </a-form>
    <div style="margin-bottom: 16px" />
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
        <template v-if="column.dataIndex === 'reviewMessage'">
          <div>审核状态： {{ PIC_REVIEW_STATUS_MAP[record.reviewStatus] }}</div>
          <div>审核信息： {{ record.reviewMessage }}</div>
          <div v-if="record.reviewerId">审核人： {{ record.reviewerId }}</div>
          <div v-if="record.reviewDate">
            审核时间： {{ dayjs(record.reviewDate).format('YYYY-MM-DD HH:mm') }}
          </div>
        </template>
        <template v-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm') }}
        </template>
        <template v-if="column.dataIndex === 'editTime'">
          {{ dayjs(record.editTime).format('YYYY-MM-DD HH:mm') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space wrap>
            <a-button
              v-if="record.reviewStatus !== PIC_REVIEW_STATUS_ENUM.PASS"
              type="link"
              @click="handleReview(record, PIC_REVIEW_STATUS_ENUM.PASS)"
              >通过
            </a-button>
            <div v-if="record.reviewStatus !== PIC_REVIEW_STATUS_ENUM.REJECT">
              <a-button
                type="link"
                danger
                @click="
                  () => {
                    open = true
                  }
                "
                >拒绝</a-button
              >
              <a-modal
                :mask="false"
                v-model:open="open"
                title="拒绝原因"
                @ok="handleReview(record, PIC_REVIEW_STATUS_ENUM.REJECT)"
              >
                <a-input v-model:value="inputReviewMessage" placeholder="请输入拒绝原因" />
              </a-modal>
            </div>
            <a-button type="link" :href="`/add_picture?id=${record.id}`" target="_blank"
              >编辑
            </a-button>
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
          </a-space>
        </template>
      </template>
    </a-table>
  </div>
</template>
<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import {
  deletePictureUsingPost,
  doPictureReviewUsingPost,
  listPictureByPageUsingPost,
} from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import { tagCategoryOptionsStore } from '@/stores/classification.ts'
import {
  PIC_REVIEW_STATUS_ENUM,
  PIC_REVIEW_STATUS_MAP,
  PIC_REVIEW_STATUS_OPTIONS,
} from '@/constants/picture.ts'

const loading = ref<boolean>(true)
const open = ref<boolean>(false)
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
    title: '审核信息',
    dataIndex: 'reviewMessage',
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
  const res = await listPictureByPageUsingPost({ ...searchParams, nullSpaceId: true })
  if (res.data.code === 0 && res.data.data) {
    dataList.value = res.data.data.records ?? []
    total.value = Number(res.data.data.total) ?? 0
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

const inputReviewMessage = ref<string>('审核拒绝')
const handleReview = async (picture: API.Picture, reviewStatus: number) => {
  const reviewMessage =
    reviewStatus === PIC_REVIEW_STATUS_ENUM.PASS ? '审核通过' : inputReviewMessage.value
  const res = await doPictureReviewUsingPost({
    id: picture.id,
    reviewStatus,
    reviewMessage,
  })
  if (res.data.code === 0) {
    message.success('审核操作成功')
    // 重新获取数据
    await fetchData()
  } else {
    message.error('审核操作失败：' + res.data.message)
  }
  open.value = false
}

// 页面加载的时候获取一次数据
onMounted(() => {
  fetchData()
})
</script>
