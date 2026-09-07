<script setup>
const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  },

  notification: {
    type: Object,
    default: null
  },

  states: {
    type: Array,
    required: true
  }
})


const emit = defineEmits([
  'update:modelValue'
])


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


function formattedPayload(payload) {
  if (!payload) {
    return ''
  }

  try {
    return JSON.stringify(
        JSON.parse(payload),
        null,
        2
    )
  } catch {
    return payload
  }
}
</script>


<template>
  <v-dialog
      :model-value="modelValue"
      max-width="900"
      @update:model-value="emit('update:modelValue', $event)"
  >
    <v-card v-if="notification" border>
      <v-card-title class="px-5 pt-5 pb-2">
        <div class="text-h6 font-weight-bold">
          Notification details
        </div>

        <div class="text-body-2 text-medium-emphasis mt-1 notification-id">
          {{ notification.id }}
        </div>
      </v-card-title>


      <v-card-text class="pa-5">
        <div class="details-grid">
          <div>
            <div class="detail-label">
              Created
            </div>

            <div>
              {{ formatDate(notification.createdAt) }}
            </div>
          </div>


          <div>
            <div class="detail-label">
              State
            </div>

            <v-chip
                v-if="notification.state"
                :color="stateColor(notification.state)"
                :prepend-icon="stateIcon(notification.state)"
                size="small"
                variant="tonal"
            >
              {{ stateLabel(notification.state) }}
            </v-chip>

            <span v-else>
              —
            </span>
          </div>


          <div>
            <div class="detail-label">
              Receiver
            </div>

            <div class="detail-value">
              {{ notification.receiver || '—' }}
            </div>
          </div>


          <div>
            <div class="detail-label">
              Topic
            </div>

            <div class="detail-value">
              {{ notification.topic || '—' }}
            </div>
          </div>
        </div>


        <div class="mt-6">
          <div class="detail-label mb-2">
            Title
          </div>

          <div class="detail-block">
            {{ notification.title || '—' }}
          </div>
        </div>


        <div v-if="notification.subtitle" class="mt-4">
          <div class="detail-label mb-2">
            Subtitle
          </div>

          <div class="detail-block">
            {{ notification.subtitle }}
          </div>
        </div>


        <div class="mt-4">
          <div class="detail-label mb-2">
            Body
          </div>

          <div class="detail-block">
            {{ notification.body || '—' }}
          </div>
        </div>


        <div v-if="notification.payload" class="mt-4">
          <div class="detail-label mb-2">
            Payload
          </div>

          <pre class="payload-block"><code>{{ formattedPayload(notification.payload) }}</code></pre>
        </div>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>


<style scoped>
.notification-id {
  overflow-wrap: anywhere;
}

.details-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 24px;
}

.detail-label {
  color: rgb(var(--v-theme-muted));
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
}

.detail-value {
  overflow-wrap: anywhere;
}

.detail-block,
.payload-block {
  padding: 16px;
  border: 1px solid rgb(var(--v-theme-border));
  border-radius: 8px;
  background: rgb(var(--v-theme-background));
  color: rgb(var(--v-theme-foreground));
  overflow-wrap: anywhere;
  user-select: text;
  white-space: pre-wrap;
  word-break: break-word;
}

.payload-block {
  max-height: 420px;
  margin: 0;
  overflow-y: auto;
  font-family:
      ui-monospace,
      SFMono-Regular,
      Menlo,
      Monaco,
      Consolas,
      monospace;
  font-size: 12px;
  line-height: 1.6;
}

.payload-block code {
  color: inherit;
  font: inherit;
  white-space: inherit;
}

@media (max-width: 600px) {
  .details-grid {
    grid-template-columns: 1fr;
  }
}
</style>
