import { createApp } from 'vue'
import { createPinia } from 'pinia'

import '@/asccess'
import router from './router'
import Antd from "ant-design-vue";
import "ant-design-vue/dist/reset.css";
import App from './App.vue'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(Antd);

app.mount('#app')
