import { ref, type Ref } from 'vue'
import { defineStore } from 'pinia'

/**
 * 存储登陆信息的状态（store）
 */
export const useLoginUserStore = defineStore('loginUser', () => {
  // 登陆用户的初始变量
  const loginUser: Ref<any, any> = ref<any>({
    userName: '未登录',
  })

  /**
   * 设置登陆用户
   * @param newLoginUser
   */
  function setLoginUser(newLoginUser: any) {
    loginUser.value = newLoginUser
  }

  function fetchLoginUser() {
    // TODO 调用后端进行获取登陆用户信息
    // 模拟三秒后自动登陆
    setTimeout(() => {
      loginUser.value = {id: 1, userName: ''}
    }, 3000)
  }

  return {
    loginUser,
    setLoginUser,
    fetchLoginUser,
  }
})
