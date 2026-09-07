<script setup>
defineProps({
  modelValue: {
    type: Boolean,
    required: true
  },

  log: {
    type: Object,
    default: null
  }
})

const emit = defineEmits([
  'update:modelValue'
])

function close() {
  emit('update:modelValue', false)
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
  <v-dialog
      :model-value="modelValue"
      max-width="900"
      @update:model-value="emit('update:modelValue', $event)"
  >
    <v-card border>
      <v-card-title class="d-flex align-start justify-space-between px-5 py-4">
        <div>
          <div class="text-h6 font-weight-bold">
            Log details
          </div>

          <div
              v-if="log"
              class="text-body-2 text-medium-emphasis mt-1"
          >
            Log #{{ log.id }}
          </div>
        </div>

        <v-btn
            icon="mdi-close"
            variant="text"
            size="small"
            aria-label="Close"
            @click="close"
        />
      </v-card-title>

      <v-card-text v-if="log" class="log-details pa-5">
        <div class="log-details-grid">
          <div>
            <div class="detail-label">
              Level
            </div>

            <div>
              {{ log.level }}
            </div>
          </div>

          <div>
            <div class="detail-label">
              Timestamp
            </div>

            <div>
              {{ formatDate(log.loggedAt) }}
            </div>
          </div>

          <div>
            <div class="detail-label">
              Logger
            </div>

            <div class="detail-value">
              {{ log.loggerName }}
            </div>
          </div>

          <div>
            <div class="detail-label">
              Thread
            </div>

            <div class="detail-value">
              {{ log.threadName || '—' }}
            </div>
          </div>
        </div>

        <div class="mt-5">
          <div class="detail-label mb-2">
            Message
          </div>

          <div class="message-block">
            {{ log.message }}
          </div>
        </div>

        <div
            v-if="log.exception"
            class="mt-5"
        >
          <div class="detail-label mb-2">
            Exception
          </div>

          <pre class="stacktrace"><code>{{ log.exception }}</code></pre>
        </div>
      </v-card-text>

      <v-card-actions class="px-5 pb-5">
        <v-spacer />

        <v-btn
            color="foreground"
            variant="text"
            @click="close"
        >
          Close
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<style scoped>
.log-details {
  max-height: min(700px, calc(100vh - 180px));
  overflow-y: auto;
}

.log-details-grid {
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

.message-block,
.stacktrace {
  padding: 16px;
  border: 1px solid rgb(var(--v-theme-border));
  border-radius: 8px;
  background: rgb(var(--v-theme-surface-bright));
  color: rgb(var(--v-theme-foreground));
  overflow-wrap: anywhere;
  user-select: text;
  white-space: pre-wrap;
  word-break: break-word;
}

.stacktrace {
  max-height: 420px;
  margin: 0;
  overflow-y: auto;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  font-size: 12px;
  line-height: 1.6;
}

.stacktrace code {
  color: inherit;
  font: inherit;
  white-space: inherit;
}

@media (max-width: 600px) {
  .log-details-grid {
    grid-template-columns: 1fr;
  }
}
</style>
