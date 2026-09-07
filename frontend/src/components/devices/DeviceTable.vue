<script setup>
defineProps({
  devices: {
    type: Array,
    required: true
  },

  loading: {
    type: Boolean,
    default: false
  }
})


const emit = defineEmits([
  'open'
])


const headers = [
  {
    title: 'Device name',
    key: 'name'
  },
  {
    title: 'Last seen',
    key: 'lastSeen'
  },
  {
    title: 'Registered',
    key: 'createdAt'
  },
  {
    title: 'Registration ID',
    key: 'id',
    sortable: false
  }
]


function formatDate(value) {
  if (!value) {
    return '—'
  }

  return new Intl.DateTimeFormat(undefined, {
    dateStyle: 'medium',
    timeStyle: 'medium'
  }).format(new Date(value))
}


function activityColor(lastSeen) {
  if (!lastSeen) {
    return 'muted'
  }

  const difference =
      Date.now() - new Date(lastSeen).getTime()

  if (difference <= 60 * 60 * 1000) {
    return 'success'
  }

  if (difference <= 24 * 60 * 60 * 1000) {
    return 'success'
  }

  if (difference <= 7 * 24 * 60 * 60 * 1000) {
    return 'success'
  }

  return 'muted'
}


function activityLabel(lastSeen) {
  if (!lastSeen) {
    return 'Never'
  }

  const difference =
      Date.now() - new Date(lastSeen).getTime()

  if (difference <= 60 * 60 * 1000) {
    return 'Recently active'
  }

  if (difference <= 24 * 60 * 60 * 1000) {
    return 'Active today'
  }

  if (difference <= 7 * 24 * 60 * 60 * 1000) {
    return 'Active this week'
  }

  return 'Inactive'
}
</script>


<template>
  <v-card border class="devices-table-card">
    <v-data-table
        class="devices-table"
        :headers="headers"
        :items="devices"
        :loading="loading"
        item-value="id"
        :items-per-page="25"
        fixed-header
        height="100%"
        @click:row="(_, row) => emit('open', row.item)"
    >
      <template #item.name="{ item }">
        <div class="d-flex align-center ga-3 py-2">
          <div class="device-icon">
            <v-icon
                icon="mdi-cellphone"
                color="primary"
                size="20"
            />
          </div>

          <div>
            <div class="font-weight-medium">
              {{ item.name || 'Unnamed device' }}
            </div>

            <div class="text-caption text-medium-emphasis">
              {{ activityLabel(item.lastSeen) }}
            </div>
          </div>
        </div>
      </template>


      <template #item.lastSeen="{ item }">
        <div class="d-flex align-center ga-2">
          <v-icon
              icon="mdi-circle"
              :color="activityColor(item.lastSeen)"
              size="9"
          />

          <span class="text-no-wrap">
            {{ formatDate(item.lastSeen) }}
          </span>
        </div>
      </template>


      <template #item.createdAt="{ item }">
        <span class="text-no-wrap">
          {{ formatDate(item.createdAt) }}
        </span>
      </template>


      <template #item.id="{ item }">
        <code class="device-id" :title="item.id">
          {{ item.id }}
        </code>
      </template>


      <template #no-data>
        <div class="empty-state">
          <v-icon
              icon="mdi-cellphone-off"
              size="44"
              color="muted"
              class="mb-3"
          />

          <div class="text-body-1 font-weight-medium">
            No devices found
          </div>

          <div class="text-body-2 text-medium-emphasis mt-1">
            No registered devices match the current search.
          </div>
        </div>
      </template>
    </v-data-table>
  </v-card>
</template>


<style scoped>
.devices-table-card {
  min-height: 320px;
  flex: 1 1 auto;
  overflow: hidden;
}

.devices-table {
  height: 100%;
}

.devices-table :deep(.v-table__wrapper + .v-divider) {
  display: none;
}

.devices-table :deep(tbody tr) {
  cursor: pointer;
}

.devices-table :deep(tbody tr:hover) {
  background: rgb(var(--v-theme-surface-bright));
}

.device-icon {
  display: flex;
  width: 36px;
  height: 36px;
  flex: 0 0 36px;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  background: rgba(var(--v-theme-primary), 0.08);
}

.device-id {
  display: block;
  max-width: 260px;
  overflow: hidden;
  color: rgb(var(--v-theme-flatDarkGrey));
  font-size: 12px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.empty-state {
  padding: 48px 24px;
  text-align: center;
}
</style>
