import { saveAs } from 'file-saver'

/**
 * 判断数组中是否包含某个字符串
 * @param array
 * @param key
 * @param str
 */
export const containsString = (array, key, str) => {
  return array.some(item => item[key]?.includes(str));
}

/**
 * 格式化文件大小
 * @param size
 */
export const formatSize = (size?: number) => {
  if (!size) return '未知'
  if (size < 1024) return size + ' B'
  if (size < 1024 * 1024) return (size / 1024).toFixed(2) + ' KB'
  return (size / (1024 * 1024)).toFixed(1) + ' MB'
}

/**
 * 下载图片
 * @param url 图片下载地址
 * @param fileName 要保存为的文件名
 */
export function downloadImage(url?: string, fileName?: string) {
  if (!url) {
    return
  }
  saveAs(url, fileName)
}

// region 拖拽图片
/**
 * 处理拖拽图片
 *
 * 需要用在 <div> 元素上，并设置 draggable="true"</div>
 * @param event
 */
export function handleDragStart(event) {
  event.preventDefault() // 阻止默认拖拽行为
}

/**
 * 将 16 进制颜色数值转化为标准的颜色值 => #RRGGBB
 * @param input
 */
export function toHexColor(input) {
  if (!input) {
    return '#fff'
  }
  // 去掉 0x 前缀
  const colorValue = input.startsWith('0x') ? input.slice(2) : input

  // 将剩余部分解析为十六进制数，再转成 6 位十六进制字符串
  const hexColor = parseInt(colorValue, 16).toString(16).padStart(6, '0')

  // 返回标准 #RRGGBB 格式
  return `#${hexColor}`
}
