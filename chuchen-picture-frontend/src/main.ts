import { createApp } from 'vue'
import { createPinia } from 'pinia'

import '@/asccess'
import router from './router'
import Antd from 'ant-design-vue'
import 'ant-design-vue/dist/reset.css'
import VueCropper from 'vue-cropper'
import 'vue-cropper/dist/index.css'
import App from '@/App.vue'

const app = createApp(App)

app.use(createPinia())
app.use(VueCropper)
app.use(router)
app.use(Antd)

app.mount('#app')
