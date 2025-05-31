import { ref, type Ref } from 'vue'
import { defineStore } from 'pinia'
import { getLoginUserUsingGet } from '@/api/userController.ts'

/**
 * 存储登陆信息的状态（store）
 */
export const useLoginUserStore = defineStore('loginUser', () => {
  // 登陆用户的初始变量
  const loginUser: Ref<any, any> = ref<API.LoginUserVO>({
    userName: '未登录',
  })

  /**
   * 设置登陆用户
   * @param newLoginUser
   */
  function setLoginUser(newLoginUser: any) {
    loginUser.value = newLoginUser
  }

  /**
   * 远程获取登陆用户信息
   */
  async function fetchLoginUser() {
    // 调用后端进行获取登陆用户信息
    const res = await getLoginUserUsingGet()
    if (res.data.code === 0 && res.data.data) {
      loginUser.value = res.data.data
    }
  }

  return {
    loginUser,
    setLoginUser,
    fetchLoginUser,
  }
})
