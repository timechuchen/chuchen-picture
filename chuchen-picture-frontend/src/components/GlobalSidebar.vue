<template>
  <div id="globalSidebar">
    <a-layout-sider v-if="loginUserStore.loginUser.id" class="sidebar" width="200" breakpoint="lg">
      <a-menu
        mode="inline"
        v-model:selectedKeys="current"
        :items="menuItems"
        @click="doMenuClick"
      />
    </a-layout-sider>
  </div>
</template>

<script lang="ts" setup>
// 菜单列表
import { h, ref } from 'vue'
import { useRouter } from 'vue-router'
import { PictureOutlined, UserOutlined } from '@ant-design/icons-vue'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'

const menuItems = [
  {
    key: '/',
    label: '公共图库',
    icon: () => h(PictureOutlined),
  },
  {
    key: '/my_space',
    label: '我的空间',
    icon: () => h(UserOutlined),
  },
]
const loginUserStore = useLoginUserStore()

const router = useRouter()

// 当前选中菜单
const current = ref<string[]>([])
// 监听路由变化，更新当前选中菜单
router.afterEach((to, from, failure) => {
  if (to.path.includes('space')) {
    current.value = ['/my_space']
  } else {
    current.value = [to.path]
  }
})

// 路由跳转事件
const doMenuClick = ({ key }: { key: string }) => {
  router.push({
    path: key,
  })
}
</script>

<style scoped></style>
