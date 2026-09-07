<script setup>
import { ref, watch } from 'vue'

import { apiFetch } from '@/clients/api'


const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  },

  device: {
    type: Object,
    default: null
  }
})


const emit = defineEmits([
  'update:modelValue'
])


const sendingTest = ref(false)

const testNotification = ref(
    createTestNotification()
)


watch(
    () => props.modelValue,
    open => {
      if (open) {
        testNotification.value =
            createTestNotification()
      }
    }
)


function createTestNotification() {
  return {
    title: 'Test notification',
    subtitle: '',
    message: 'This is a test notification.'
  }
}


function close() {
  if (sendingTest.value) {
    return
  }

  emit('update:modelValue', false)
}


async function sendTestNotification() {
  if (!props.device) {
    return
  }

  sendingTest.value = true

  try {
    await apiFetch(
        `/api/devices/${encodeURIComponent(props.device.name)}/test-notification`,
        {
          method: 'POST',

          body: JSON.stringify({
            title: testNotification.value.title,
            subtitle: testNotification.value.subtitle,
            message: testNotification.value.message
          }),

          notification: async response => {
            if (response.ok) {
              return {
                type: 'success',
                title: 'Test notification sent',
                subtitle: props.device.name,
                message:
                    'The test notification was submitted successfully.'
              }
            }

            return {
              type: 'error',
              title: 'Test notification failed',
              subtitle: props.device.name,
              message:
                  'The server could not send the test notification.'
            }
          }
        }
    )
  } finally {
    sendingTest.value = false
  }
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


function activityColor(lastSeen) {
  if (!lastSeen) {
    return 'muted'
  }

  const difference =
      Date.now() - new Date(lastSeen).getTime()

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
  <v-dialog
      :model-value="modelValue"
      max-width="760"
      :persistent="sendingTest"
      @update:model-value="emit('update:modelValue', $event)"
  >
    <v-card v-if="device" border>
      <v-card-title class="d-flex align-start justify-space-between px-5 pt-5">
        <div>
          <div class="text-h6 font-weight-bold">
            {{ device.name || 'Unnamed device' }}
          </div>

          <div class="text-body-2 text-medium-emphasis mt-1">
            Device details
          </div>
        </div>


        <v-btn
            icon="mdi-close"
            color="foreground"
            variant="text"
            :disabled="sendingTest"
            @click="close"
        />
      </v-card-title>


      <v-card-text class="pa-5">
        <div class="details-grid">
          <div>
            <div class="detail-label">
              Device ID
            </div>

            <div class="detail-value device-id-full">
              {{ device.id }}
            </div>
          </div>


          <div>
            <div class="detail-label">
              Activity
            </div>

            <v-chip
                :color="activityColor(device.lastSeen)"
                size="small"
                variant="tonal"
            >
              {{ activityLabel(device.lastSeen) }}
            </v-chip>
          </div>


          <div>
            <div class="detail-label">
              Registered
            </div>

            <div>
              {{ formatDate(device.createdAt) }}
            </div>
          </div>


          <div>
            <div class="detail-label">
              Last seen
            </div>

            <div>
              {{ formatDate(device.lastSeen) }}
            </div>
          </div>
        </div>


        <v-divider class="my-6" />


        <div class="mb-4">
          <div class="text-h6 font-weight-bold">
            Send test notification
          </div>

          <div class="text-body-2 text-medium-emphasis mt-1">
            Send a push notification directly to this device.
          </div>
        </div>


        <v-form @submit.prevent="sendTestNotification">
          <v-text-field
              v-model="testNotification.title"
              label="Title"
              prepend-inner-icon="mdi-format-title"
              :disabled="sendingTest"
              class="mb-3"
              hide-details
          />


          <v-text-field
              v-model="testNotification.subtitle"
              label="Subtitle"
              prepend-inner-icon="mdi-subtitles-outline"
              :disabled="sendingTest"
              class="mb-3"
              clearable
              hide-details
          />


          <v-textarea
              v-model="testNotification.message"
              label="Message"
              prepend-inner-icon="mdi-message-text-outline"
              :disabled="sendingTest"
              rows="4"
              auto-grow
              hide-details
          />


          <div class="d-flex justify-end mt-5">
            <v-btn
                type="submit"
                prepend-icon="mdi-send-outline"
                :loading="sendingTest"
                :disabled="
                  !testNotification.title.trim()
                  || !testNotification.message.trim()
                "
            >
              Send test notification
            </v-btn>
          </div>
        </v-form>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>


<style scoped>
.details-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 24px;
}

.detail-label {
  margin-bottom: 4px;
  color: rgb(var(--v-theme-muted));
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
}

.detail-value {
  overflow-wrap: anywhere;
}

.device-id-full {
  font-family:
      ui-monospace,
      SFMono-Regular,
      Menlo,
      Monaco,
      Consolas,
      monospace;
  font-size: 12px;
}

@media (max-width: 600px) {
  .details-grid {
    grid-template-columns: 1fr;
  }
}
</style>
