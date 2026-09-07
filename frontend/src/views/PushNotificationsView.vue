<script setup>
import {
  computed,
  onBeforeUnmount,
  ref,
  watch
} from 'vue'

import { apiFetch } from '@/clients/api'

import NotificationDetailsDialog
  from '@/components/pushnotifications/PushNotificationDetailsDialog.vue'

import NotificationFilters
  from '@/components/pushnotifications/PushNotificationFilters.vue'

import NotificationTable
  from '@/components/pushnotifications/PushNotificationTable.vue'


const notificationStates = [
  {
    title: 'Created',
    value: 'CREATED',
    color: 'info',
    icon: 'mdi-clock-outline'
  },
  {
    title: 'Dispatched',
    value: 'DISPATCHED',
    color: 'success',
    icon: 'mdi-check-circle-outline'
  },
  {
    title: 'Dispatch failed',
    value: 'DISPATCH_FAILED',
    color: 'error',
    icon: 'mdi-alert-circle-outline'
  }
]


const notifications = ref([])
const totalElements = ref(0)
const loading = ref(false)

const filters = ref({
  from: '',
  to: '',
  state: null,
  receiver: '',
  topic: ''
})

const tableOptions = ref({
  page: 1,
  itemsPerPage: 25,
  sortBy: [
    {
      key: 'createdAt',
      order: 'desc'
    }
  ]
})

const selectedNotification = ref(null)
const detailsDialogOpen = ref(false)

let filterTimer = null


const latestNotification = computed(() => {
  const timestamps = notifications.value
      .map(notification => notification.createdAt)
      .filter(Boolean)
      .map(value => new Date(value).getTime())

  if (timestamps.length === 0) {
    return null
  }

  return new Date(Math.max(...timestamps))
})


watch(
    filters,
    () => {
      clearTimeout(filterTimer)

      filterTimer = setTimeout(() => {
        tableOptions.value = {
          ...tableOptions.value,
          page: 1
        }

        loadNotifications(tableOptions.value)
      }, 300)
    },
    {
      deep: true
    }
)


onBeforeUnmount(() => {
  clearTimeout(filterTimer)
})


async function loadNotifications(
    options = tableOptions.value) {

  tableOptions.value = options
  loading.value = true

  try {
    const parameters = new URLSearchParams({
      page: String(options.page - 1),
      size: String(options.itemsPerPage)
    })

    if (filters.value.from) {
      parameters.set(
          'from',
          toUtcInstant(filters.value.from)
      )
    }

    if (filters.value.to) {
      parameters.set(
          'to',
          toUtcInstant(filters.value.to)
      )
    }

    if (filters.value.state) {
      parameters.set(
          'state',
          filters.value.state
      )
    }

    const receiver = filters.value.receiver.trim()

    if (receiver) {
      parameters.set(
          'receiver',
          receiver
      )
    }

    const topic = filters.value.topic.trim()

    if (topic) {
      parameters.set(
          'topic',
          topic
      )
    }

    const sorting = options.sortBy?.[0]

    if (sorting) {
      parameters.set(
          'sort',
          `${sorting.key},${sorting.order}`
      )
    }

    const response = await apiFetch(
        `/api/pushnotifications?${parameters.toString()}`
    )

    notifications.value = response.content ?? []

    totalElements.value =
        response.page?.totalElements
        ?? response.totalElements
        ?? 0
  } finally {
    loading.value = false
  }
}


function toUtcInstant(value) {
  return new Date(value).toISOString()
}


function formatDate(value) {
  if (!value) {
    return '—'
  }

  return new Intl.DateTimeFormat(undefined, {
    dateStyle: 'medium',
    timeStyle: 'medium'
  }).format(new Date(value))
}


function openDetails(notification) {
  selectedNotification.value = notification
  detailsDialogOpen.value = true
}
</script>


<template>
  <v-container fluid class="notifications-view pa-7">
    <div class="mb-6">
      <h1 class="text-h4 font-weight-bold mb-1">
        Push Notifications
      </h1>

      <div class="text-medium-emphasis">
        Monitor push notifications and delivery status.
      </div>
    </div>


    <v-row class="mb-4 flex-grow-0">
      <v-col>
        <v-card border class="summary-card">
          <v-card-text>
            <div class="d-flex align-center justify-space-between">
              <div>
                <div class="text-medium-emphasis">
                  Total notifications
                </div>

                <div class="text-h4 font-weight-bold text-primary mt-2">
                  {{ totalElements }}
                </div>
              </div>

              <v-icon
                  icon="mdi-bell-outline"
                  color="primary"
                  size="32"
              />
            </div>
          </v-card-text>
        </v-card>
      </v-col>


      <v-col>
        <v-card border class="summary-card">
          <v-card-text>
            <div class="d-flex align-center justify-space-between">
              <div class="min-width-0">
                <div class="text-medium-emphasis">
                  Latest activity
                </div>

                <div class="text-body-1 font-weight-bold mt-2 latest-value">
                  {{ formatDate(latestNotification) }}
                </div>
              </div>

              <v-icon
                  icon="mdi-clock-outline"
                  color="flatAqua"
                  size="32"
              />
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>


    <NotificationFilters
        v-model:from="filters.from"
        v-model:to="filters.to"
        v-model:state="filters.state"
        v-model:receiver="filters.receiver"
        :states="notificationStates"
        class="mb-4"
    />


    <NotificationTable
        :notifications="notifications"
        :total-elements="totalElements"
        :loading="loading"
        :items-per-page="tableOptions.itemsPerPage"
        :states="notificationStates"
        @update:options="loadNotifications"
        @open="openDetails"
    />


    <NotificationDetailsDialog
        v-model="detailsDialogOpen"
        :notification="selectedNotification"
        :states="notificationStates"
    />
  </v-container>
</template>


<style scoped>
.notifications-view {
  display: flex;
  height: calc(100vh - 64px);
  min-height: 0;
  flex-direction: column;
}

.summary-card {
  height: 100%;
}

.min-width-0 {
  min-width: 0;
}

.latest-value {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

@media (max-width: 600px) {
  .notifications-view {
    height: auto;
    min-height: calc(100vh - 64px);
  }
}
</style>
