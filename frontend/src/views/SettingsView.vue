<script setup>
import { ref } from 'vue'

import { apiFetch } from '@/clients/api'

import LoggingConfigurationTable
  from '@/components/settings/LoggingConfigurationTable.vue'


const configurations = ref([])
const loading = ref(false)
const mutating = ref(false)


async function loadConfigurations() {
  loading.value = true

  try {
    configurations.value =
        await apiFetch('/api/settings/logging')
        ?? []
  } finally {
    loading.value = false
  }
}


async function createConfiguration(configuration) {
  mutating.value = true

  try {
    const created = await apiFetch(
        '/api/settings/logging',
        {
          method: 'POST',

          body: JSON.stringify({
            loggerName: configuration.loggerName,
            level: configuration.level
          }),

          notification: async response => {
            if (response.ok) {
              return {
                type: 'success',
                title: 'Logging configuration added',
                message: configuration.loggerName
              }
            }

            return {
              type: 'error',
              title: 'Could not add logging configuration'
            }
          }
        }
    )

    configurations.value = [
      ...configurations.value,
      created
    ]
  } finally {
    mutating.value = false
  }
}


async function updateConfiguration(configuration) {
  mutating.value = true

  try {
    const updated = await apiFetch(
        `/api/settings/logging/${encodeURIComponent(configuration.id)}`,
        {
          method: 'PUT',

          body: JSON.stringify({
            level: configuration.level
          }),

          notification: async response => {
            if (response.ok) {
              return {
                type: 'success',
                title: 'Log level updated',
                message: configuration.loggerName
              }
            }

            return {
              type: 'error',
              title: 'Could not update log level'
            }
          }
        }
    )

    configurations.value =
        configurations.value.map(current =>
            current.id === updated.id
                ? updated
                : current
        )
  } finally {
    mutating.value = false
  }
}


async function deleteConfiguration(configuration) {
  mutating.value = true

  try {
    await apiFetch(
        `/api/settings/logging/${encodeURIComponent(configuration.id)}`,
        {
          method: 'DELETE',

          notification: async response => {
            if (response.ok) {
              return {
                type: 'success',
                title: 'Logging override removed',
                message: configuration.loggerName
              }
            }

            return {
              type: 'error',
              title: 'Could not remove logging override'
            }
          }
        }
    )

    configurations.value =
        configurations.value.filter(
            current => current.id !== configuration.id
        )
  } finally {
    mutating.value = false
  }
}


loadConfigurations()
</script>


<template>
  <v-container fluid class="settings-view pa-7">
    <div class="mb-6">
      <h1 class="text-h4 font-weight-bold mb-1">
        Settings
      </h1>

      <div class="text-medium-emphasis">
        Configure application behavior.
      </div>
    </div>


    <div class="mb-4">
      <div class="text-h6 font-weight-bold mb-1">
        Logging configuration
      </div>

      <div class="text-body-2 text-medium-emphasis">
        Override log levels for individual classes or packages.
      </div>
    </div>


    <LoggingConfigurationTable
        :configurations="configurations"
        :loading="loading"
        :mutating="mutating"
        @create="createConfiguration"
        @update="updateConfiguration"
        @delete="deleteConfiguration"
    />
  </v-container>
</template>


<style scoped>
.settings-view {
  display: flex;
  min-height: calc(100vh - 64px);
  flex-direction: column;
}
</style>
