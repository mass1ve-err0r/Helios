<script setup>
import {
  onBeforeUnmount,
  ref,
  watch
} from 'vue'

import { apiFetch } from '@/clients/api'
import LogDetailsDialog from '@/components/logs/LogDetailsDialog.vue'
import LogFilters from '@/components/logs/LogFilters.vue'


const headers = [
  {
    title: 'Timestamp',
    key: 'loggedAt'
  },
  {
    title: 'Level',
    key: 'level'
  },
  {
    title: 'Logger',
    key: 'loggerName'
  },
  {
    title: 'Message',
    key: 'message',
    sortable: false
  },
  {
    title: 'Details',
    key: 'actions',
    sortable: false,
    align: 'end'
  }
]


const logs = ref([])
const totalElements = ref(0)
const loading = ref(false)

const filters = ref(createDefaultFilters())

const tableOptions = ref({
  page: 1,
  itemsPerPage: 25,
  sortBy: [
    {
      key: 'loggedAt',
      order: 'desc'
    }
  ]
})

const selectedLog = ref(null)
const detailsDialogOpen = ref(false)

let filterTimer = null


watch(
    filters,
    () => {
      clearTimeout(filterTimer)

      filterTimer = setTimeout(() => {
        tableOptions.value = {
          ...tableOptions.value,
          page: 1
        }

        loadLogs(tableOptions.value)
      }, 300)
    },
    {
      deep: true
    }
)


onBeforeUnmount(() => {
  clearTimeout(filterTimer)
})


function createDefaultFilters() {
  const now = new Date()

  const start = new Date(
      now.getFullYear(),
      now.getMonth(),
      now.getDate(),
      0,
      0,
      0,
      0
  )

  const end = new Date(
      now.getFullYear(),
      now.getMonth(),
      now.getDate(),
      23,
      59,
      59,
      999
  )

  return {
    from: toDateTimeLocalValue(start),
    to: toDateTimeLocalValue(end),
    level: null
  }
}


function toDateTimeLocalValue(date) {
  const offset = date.getTimezoneOffset() * 60_000

  return new Date(date.getTime() - offset)
      .toISOString()
      .slice(0, 16)
}


function toUtcInstant(value) {
  if (!value) {
    return null
  }

  return new Date(value).toISOString()
}


async function loadLogs(options = tableOptions.value) {
  tableOptions.value = options
  loading.value = true

  try {
    const parameters = new URLSearchParams({
      page: String(options.page - 1),
      size: String(options.itemsPerPage)
    })

    const from = toUtcInstant(filters.value.from)
    const to = toUtcInstant(filters.value.to)

    if (from) {
      parameters.set('from', from)
    }

    if (to) {
      parameters.set('to', to)
    }

    if (filters.value.level) {
      parameters.set('level', filters.value.level)
    }

    const sorting = options.sortBy?.[0]

    if (sorting) {
      parameters.set(
          'sort',
          `${sorting.key},${sorting.order}`
      )
    }

    const response = await apiFetch(
        `/api/logs?${parameters.toString()}`
    )

    logs.value = response.content ?? []

    totalElements.value =
        response.page?.totalElements
        ?? response.totalElements
        ?? 0
  } finally {
    loading.value = false
  }
}


function resetFilters() {
  filters.value = createDefaultFilters()
}


function openDetails(log) {
  selectedLog.value = log
  detailsDialogOpen.value = true
}


function levelColor(level) {
  switch (level) {
    case 'ERROR':
      return 'error'

    case 'WARN':
      return 'warning'

    case 'DEBUG':
      return 'muted'

    case 'TRACE':
      return 'flatMediumGrey'

    default:
      return 'info'
  }
}


function levelIcon(level) {
  switch (level) {
    case 'ERROR':
      return 'mdi-alert-circle-outline'

    case 'WARN':
      return 'mdi-alert-outline'

    case 'DEBUG':
      return 'mdi-bug-outline'

    case 'TRACE':
      return 'mdi-code-tags'

    default:
      return 'mdi-information-outline'
  }
}


function formatDate(value) {
  if (!value) {
    return ''
  }

  return new Intl.DateTimeFormat(undefined, {
    dateStyle: 'medium',
    timeStyle: 'medium'
  }).format(new Date(value))
}
</script>


<template>
  <v-container fluid class="logs-view pa-7">
    <div class="mb-6">
      <h1 class="text-h4 font-weight-bold mb-1">
        Logs
      </h1>

      <div class="text-medium-emphasis">
        Review application activity and failures.
      </div>
    </div>


    <LogFilters
        v-model:from="filters.from"
        v-model:to="filters.to"
        v-model:level="filters.level"
        class="mb-4"
        @reset="resetFilters"
    />


    <v-card border class="logs-table-card">
      <v-data-table-server
          class="logs-table"
          :headers="headers"
          :items="logs"
          :items-length="totalElements"
          :loading="loading"
          :items-per-page="tableOptions.itemsPerPage"
          item-value="id"
          height="100%"
          fixed-header
          fixed-footer
          @update:options="loadLogs"
          @click:row="(_, row) => openDetails(row.item)"
      >
        <template #item.loggedAt="{ item }">
          <span class="text-no-wrap">
            {{ formatDate(item.loggedAt) }}
          </span>
        </template>


        <template #item.level="{ item }">
          <v-chip
              :color="levelColor(item.level)"
              :prepend-icon="levelIcon(item.level)"
              size="small"
              variant="tonal"
          >
            {{ item.level }}
          </v-chip>
        </template>


        <template #item.loggerName="{ item }">
          <span
              class="logger-name"
              :title="item.loggerName"
          >
            {{ item.loggerName }}
          </span>
        </template>


        <template #item.message="{ item }">
          <div class="log-message">
            {{ item.message }}
          </div>
        </template>


        <template #item.actions="{ item }">
          <v-btn
              icon="mdi-open-in-new"
              color="primary"
              variant="text"
              size="small"
              aria-label="Open log details"
              title="Open details"
              @click.stop="openDetails(item)"
          />
        </template>
      </v-data-table-server>
    </v-card>


    <LogDetailsDialog
        v-model="detailsDialogOpen"
        :log="selectedLog"
    />
  </v-container>
</template>


<style scoped>
.logs-view {
  display: flex;
  height: calc(100vh - 64px);
  min-height: 0;
  flex-direction: column;
}

.logs-table-card {
  min-height: 320px;
  flex: 1 1 auto;
  overflow: hidden;
}

.logs-table {
  height: 100%;
}

.logs-table :deep(.v-table__wrapper + .v-divider) {
  display: none;
}

.logs-table :deep(tbody tr) {
  cursor: pointer;
}

.logs-table :deep(tbody tr:hover) {
  background: rgb(var(--v-theme-surface-bright));
}

.logger-name {
  display: block;
  max-width: 320px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.log-message {
  display: -webkit-box;
  max-width: 900px;
  overflow: hidden;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow-wrap: anywhere;
}
</style>
