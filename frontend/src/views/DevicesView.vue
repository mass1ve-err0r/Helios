<script setup>
import {
  computed,
  ref
} from 'vue'

import { apiFetch } from '@/clients/api'

import DeviceDetailsDialog from '@/components/devices/DeviceDetailsDialog.vue'
import DeviceTable from '@/components/devices/DeviceTable.vue'


const devices = ref([])
const loading = ref(false)

const search = ref('')

const selectedDevice = ref(null)
const detailsDialogOpen = ref(false)


const filteredDevices = computed(() => {
  const query = search.value.trim().toLowerCase()

  if (!query) {
    return devices.value
  }

  return devices.value.filter(device => {
    return device.name?.toLowerCase().includes(query)
        || device.id?.toLowerCase().includes(query)
  })
})


const latestActivity = computed(() => {
  const timestamps = devices.value
      .map(device => device.lastSeen)
      .filter(Boolean)
      .map(value => new Date(value).getTime())

  if (timestamps.length === 0) {
    return null
  }

  return new Date(Math.max(...timestamps))
})


async function loadDevices() {
  loading.value = true

  try {
    devices.value =
        await apiFetch('/api/devices')
        ?? []
  } finally {
    loading.value = false
  }
}


function openDevice(device) {
  selectedDevice.value = device
  detailsDialogOpen.value = true
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


loadDevices()
</script>


<template>
  <v-container fluid class="devices-view pa-7">
    <div class="mb-6">
      <h1 class="text-h4 font-weight-bold mb-1">
        Devices
      </h1>

      <div class="text-medium-emphasis">
        Monitor registered devices and send test notifications.
      </div>
    </div>


    <v-row class="mb-4 flex-grow-0">
      <v-col>
        <v-card border class="summary-card">
          <v-card-text>
            <div class="d-flex align-center justify-space-between">
              <div>
                <div class="text-medium-emphasis">
                  Registered devices
                </div>

                <div class="text-h4 font-weight-bold text-primary mt-2">
                  {{ devices.length }}
                </div>
              </div>

              <v-icon
                  icon="mdi-cellphone-link"
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
                  {{ formatDate(latestActivity) }}
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


    <v-card border class="filters-card mb-4">
      <v-card-text>
        <v-text-field
            v-model="search"
            label="Search devices"
            placeholder="Device name or ID"
            prepend-inner-icon="mdi-magnify"
            clearable
            hide-details
        />
      </v-card-text>
    </v-card>


    <DeviceTable
        :devices="filteredDevices"
        :loading="loading"
        @open="openDevice"
    />


    <DeviceDetailsDialog
        v-model="detailsDialogOpen"
        :device="selectedDevice"
    />
  </v-container>
</template>


<style scoped>
.devices-view {
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

.filters-card {
  flex: 0 0 auto;
}

@media (max-width: 600px) {
  .devices-view {
    height: auto;
    min-height: calc(100vh - 64px);
  }
}
</style>
