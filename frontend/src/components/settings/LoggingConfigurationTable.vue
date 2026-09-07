<!-- src/components/settings/LoggingConfigurationTable.vue -->

<script setup>
import {
  computed,
  ref
} from 'vue'


const props = defineProps({
  configurations: {
    type: Array,
    required: true
  },

  loading: {
    type: Boolean,
    default: false
  },

  mutating: {
    type: Boolean,
    default: false
  }
})


const emit = defineEmits([
  'create',
  'update',
  'delete'
])


const headers = [
  {
    title: 'Logger / package',
    key: 'loggerName'
  },
  {
    title: 'Log level',
    key: 'level',
    sortable: false,
    width: 220
  },
  {
    title: '',
    key: 'actions',
    sortable: false,
    align: 'end',
    width: 64
  }
]


const levels = [
  'TRACE',
  'DEBUG',
  'INFO',
  'WARN',
  'ERROR',
  'OFF'
]


const newLoggerName = ref('')
const newLevel = ref('INFO')


const sortedConfigurations = computed(() => {
  return [...props.configurations]
      .sort((a, b) =>
          a.loggerName.localeCompare(b.loggerName)
      )
})


const canCreate = computed(() => {
  return newLoggerName.value.trim().length > 0
      && newLevel.value
      && !props.mutating
})


function createConfiguration() {
  if (!canCreate.value) {
    return
  }

  emit('create', {
    loggerName: newLoggerName.value.trim(),
    level: newLevel.value
  })

  newLoggerName.value = ''
  newLevel.value = 'INFO'
}


function updateLevel(configuration, level) {
  if (!level || level === configuration.level) {
    return
  }

  emit('update', {
    ...configuration,
    level
  })
}


function levelColor(level) {
  switch (level) {
    case 'ERROR':
      return 'error'

    case 'WARN':
      return 'warning'

    case 'INFO':
      return 'info'

    case 'DEBUG':
      return 'flatDarkGrey'

    case 'TRACE':
      return 'flatMediumGrey'

    case 'OFF':
      return 'muted'

    default:
      return 'muted'
  }
}


function levelIcon(level) {
  switch (level) {
    case 'ERROR':
      return 'mdi-alert-circle-outline'

    case 'WARN':
      return 'mdi-alert-outline'

    case 'INFO':
      return 'mdi-information-outline'

    case 'DEBUG':
      return 'mdi-bug-outline'

    case 'TRACE':
      return 'mdi-code-tags'

    case 'OFF':
      return 'mdi-volume-off'

    default:
      return 'mdi-circle-outline'
  }
}
</script>


<template>
  <div class="logging-configuration">
    <v-card border class="add-bar mb-4">
      <v-card-text class="py-3">
        <div class="add-bar-grid">
          <v-text-field
              v-model="newLoggerName"
              label="Logger or package"
              placeholder="software.baig.helios.core"
              prepend-inner-icon="mdi-code-braces"
              :disabled="mutating"
              hide-details
              @keydown.enter.prevent="createConfiguration"
          />


          <v-select
              v-model="newLevel"
              :items="levels"
              label="Log level"
              :disabled="mutating"
              hide-details
          >
            <template #prepend-inner>
              <v-icon
                  :icon="levelIcon(newLevel)"
                  :color="levelColor(newLevel)"
                  size="18"
              />
            </template>
          </v-select>


          <v-btn
              icon="mdi-plus"
              color="foreground"
              variant="text"
              :disabled="!canCreate"
              :loading="mutating"
              aria-label="Add logging configuration"
              title="Add logging configuration"
              @click="createConfiguration"
          />
        </div>
      </v-card-text>
    </v-card>


    <v-card border class="configuration-table-card">
      <v-data-table
          class="configuration-table"
          :headers="headers"
          :items="sortedConfigurations"
          :loading="loading"
          item-value="id"
          hide-default-footer
      >
        <template #item.loggerName="{ item }">
          <div class="logger-cell">
            <v-icon
                icon="mdi-code-braces"
                color="muted"
                size="18"
            />

            <code class="logger-name" :title="item.loggerName">
              {{ item.loggerName }}
            </code>
          </div>
        </template>


        <template #item.level="{ item }">
          <v-select
              :model-value="item.level"
              :items="levels"
              :disabled="mutating"
              density="compact"
              variant="outlined"
              hide-details
              class="level-select"
              @update:model-value="
                updateLevel(item, $event)
              "
          >
            <template #prepend-inner>
              <v-icon
                  :icon="levelIcon(item.level)"
                  :color="levelColor(item.level)"
                  size="18"
              />
            </template>
          </v-select>
        </template>


        <template #item.actions="{ item }">
          <v-btn
              icon="mdi-delete-outline"
              color="foreground"
              variant="text"
              size="small"
              :disabled="mutating"
              aria-label="Remove logging override"
              title="Remove override"
              @click="emit('delete', item)"
          />
        </template>


        <template #no-data>
          <div class="empty-state">
            <v-icon
                icon="mdi-text-box-remove-outline"
                size="44"
                color="muted"
                class="mb-3"
            />

            <div class="text-body-1 font-weight-medium">
              No logging overrides
            </div>

            <div class="text-body-2 text-medium-emphasis mt-1">
              Spring uses its inherited logging configuration.
            </div>
          </div>
        </template>
      </v-data-table>
    </v-card>
  </div>
</template>


<style scoped>
.logging-configuration {
  display: flex;
  min-height: 0;
  flex: 1 1 auto;
  flex-direction: column;
}

.add-bar {
  flex: 0 0 auto;
}

.add-bar-grid {
  display: grid;
  grid-template-columns:
      minmax(360px, 1fr)
      minmax(180px, 240px)
      48px;
  gap: 12px;
  align-items: center;
}

.configuration-table-card {
  min-height: 320px;
  flex: 1 1 auto;
  overflow: hidden;
}

.configuration-table {
  height: 100%;
}

.configuration-table :deep(.v-table__wrapper + .v-divider) {
  display: none;
}

.configuration-table :deep(tbody tr:last-child td) {
  border-bottom: 0 !important;
}

.configuration-table :deep(tbody td) {
  height: 72px;
  vertical-align: middle;
}

.logger-cell {
  display: flex;
  min-width: 0;
  align-items: center;
  gap: 10px;
}

.logger-name {
  display: block;
  max-width: 760px;
  overflow: hidden;
  color: rgb(var(--v-theme-foreground));
  font-family:
      ui-monospace,
      SFMono-Regular,
      Menlo,
      Monaco,
      Consolas,
      monospace;
  font-size: 13px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.level-select {
  width: 200px;
}

.level-select :deep(.v-field) {
  min-height: 44px;
}

.level-select :deep(.v-field__input) {
  min-height: 44px;
  padding-top: 0;
  padding-bottom: 0;
}

.empty-state {
  padding: 56px 24px;
  text-align: center;
}

@media (max-width: 800px) {
  .add-bar-grid {
    grid-template-columns: 1fr;
  }

  .level-select {
    width: 100%;
  }
}
</style>
