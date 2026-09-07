<script setup>
const props = defineProps({
  notifications: {
    type: Array,
    required: true
  },

  totalElements: {
    type: Number,
    required: true
  },

  loading: {
    type: Boolean,
    default: false
  },

  itemsPerPage: {
    type: Number,
    required: true
  },

  states: {
    type: Array,
    required: true
  }
})


const emit = defineEmits([
  'update:options',
  'open'
])


const headers = [
  {
    title: 'Created',
    key: 'createdAt'
  },
  {
    title: 'State',
    key: 'state'
  },
  {
    title: 'Receiver',
    key: 'receiver'
  },
  {
    title: 'Topic',
    key: 'topic'
  },
  {
    title: 'Title',
    key: 'title',
    sortable: false
  }
]


function stateDefinition(state) {
  return props.states.find(
      definition => definition.value === state
  )
}


function stateColor(state) {
  return stateDefinition(state)?.color ?? 'muted'
}


function stateIcon(state) {
  return stateDefinition(state)?.icon
      ?? 'mdi-help-circle-outline'
}


function stateLabel(state) {
  return stateDefinition(state)?.title
      ?? state
      ?? '—'
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
</script>


<template>
  <v-card border class="notifications-table-card">
    <v-data-table-server
        class="notifications-table"
        :headers="headers"
        :items="notifications"
        :items-length="totalElements"
        :loading="loading"
        :items-per-page="itemsPerPage"
        item-value="id"
        height="100%"
        fixed-header
        fixed-footer
        @update:options="emit('update:options', $event)"
        @click:row="(_, row) => emit('open', row.item)"
    >
      <template #item.createdAt="{ item }">
        <span class="text-no-wrap">
          {{ formatDate(item.createdAt) }}
        </span>
      </template>


      <template #item.state="{ item }">
        <v-chip
            v-if="item.state"
            :color="stateColor(item.state)"
            :prepend-icon="stateIcon(item.state)"
            size="small"
            variant="tonal"
        >
          {{ stateLabel(item.state) }}
        </v-chip>

        <span v-else>
          —
        </span>
      </template>


      <template #item.receiver="{ item }">
        <span
            class="receiver-value"
            :title="item.receiver"
        >
          {{ item.receiver || '—' }}
        </span>
      </template>


      <template #item.topic="{ item }">
        <v-chip
            v-if="item.topic"
            size="small"
            color="flatPurple"
            variant="tonal"
        >
          {{ item.topic }}
        </v-chip>

        <span v-else>
          —
        </span>
      </template>


      <template #item.title="{ item }">
        <span
            class="notification-title"
            :title="item.title"
        >
          {{ item.title || '—' }}
        </span>
      </template>


      <template #no-data>
        <div class="empty-state">
          <v-icon
              icon="mdi-bell-off-outline"
              size="44"
              color="muted"
              class="mb-3"
          />

          <div class="text-body-1 font-weight-medium">
            No notifications found
          </div>

          <div class="text-body-2 text-medium-emphasis mt-1">
            No persisted notifications match the current filters.
          </div>
        </div>
      </template>
    </v-data-table-server>
  </v-card>
</template>


<style scoped>
.notifications-table-card {
  min-height: 320px;
  flex: 1 1 auto;
  overflow: hidden;
}

.notifications-table {
  height: 100%;
}

.notifications-table :deep(.v-table__wrapper + .v-divider) {
  display: none;
}

.notifications-table :deep(tbody tr:last-child td) {
  border-bottom: 0 !important;
}

.notifications-table :deep(tbody tr) {
  cursor: pointer;
}

.notifications-table :deep(tbody tr:hover) {
  background: rgb(var(--v-theme-surface-bright));
}

.receiver-value {
  display: block;
  max-width: 260px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.notification-title {
  display: block;
  max-width: 520px;
  overflow: hidden;
  font-weight: 500;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.empty-state {
  padding: 48px 24px;
  text-align: center;
}
</style>
