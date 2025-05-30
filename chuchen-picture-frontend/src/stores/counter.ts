import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

// 一个状态就存储一类要共享的数据（存一类常量）
export const useCounterStore = defineStore('counter', () => {
  // 定义状态的初始值
  const count = ref(0)
  // 定义计算属性 == getter
  const doubleCount = computed(() => count.value * 2)

  // 怎么定义修改的状态
  function increment() {
    count.value++
  }

  return { count, doubleCount, increment }
})
