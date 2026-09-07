import { createRouter, createWebHistory } from 'vue-router'

import PushNotificationsView from '@/views/PushNotificationsView.vue'
import LogsView from '@/views/LogsView.vue'
import DevicesView from "@/views/DevicesView.vue";
import SettingsView from '@/views/SettingsView.vue'


const router = createRouter({
  history: createWebHistory('/ui/'),

  routes: [
    {
      path: '/',
      redirect: '/pushnotifications'
    },
    {
      path: '/pushnotifications',
      name: 'pushnotifications',
      component: PushNotificationsView
    },
    {
      path: '/devices',
      name: 'devices',
      component: DevicesView
    },
    {
      path: '/logs',
      name: 'logs',
      component: LogsView
    },
    {
        path: '/settings',
        name: 'settings',
        component: SettingsView
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/pushnotifications'
    }
  ]
})

export default router
