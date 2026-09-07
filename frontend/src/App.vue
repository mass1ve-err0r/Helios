<script setup>
import { computed, onMounted, ref } from 'vue'

import { apiFetch } from '@/clients/api'
import { useNotifications } from '@/composables/useNotifications'

const drawer = ref(false)

const {
  notifications,
  currentNotification,
  dismissNotification
} = useNotifications()

const notificationVisible = computed({
  get() {
    return currentNotification.value !== null
  },

  set() {
    // Intentionally ignored.
    // Notifications may only be dismissed by the explicit close button.
  }
})

const notificationColor = computed(() => {
  switch (currentNotification.value?.type) {
    case 'success':
      return 'success'

    case 'warning':
      return 'warning'

    case 'error':
      return 'error'

    default:
      return 'info'
  }
})

const notificationIcon = computed(() => {
  switch (currentNotification.value?.type) {
    case 'success':
      return 'mdi-check-circle-outline'

    case 'warning':
      return 'mdi-alert-outline'

    case 'error':
      return 'mdi-alert-circle-outline'

    default:
      return 'mdi-information-outline'
  }
})

const items = [
  {
    title: 'Push Notifications',
    route: '/notifications',
    icon: 'mdi-square-rounded-badge'
  },
  {
    title: 'Devices',
    route: '/devices',
    icon: 'mdi-devices'
  },
  {
    title: 'Logs',
    route: '/logs',
    icon: 'mdi-text-box-search-outline'
  },
  {
    title: 'Settings',
    icon: 'mdi-cog-outline',
    route: '/settings'
  }
]

const buildInfo = ref({
  version: 'Unknown',
  hash: 'Unknown'
})

async function loadBuildInfo() {
  try {
    buildInfo.value = await apiFetch('/api/system/build-info')
  } catch {
    buildInfo.value = {
      version: '0',
      hash: 'Unavailable'
    }
  }
}

const environment = ref('UNKNOWN')
const normalizedEnvironment = computed(() => {
  return environment.value?.toUpperCase() ?? 'UNKNOWN'
})
const environmentClass = computed(() => {
  switch (normalizedEnvironment.value) {
    case 'PRODUCTION':
      return 'environment-badge--production'

    case 'DEVELOPMENT':
      return 'environment-badge--test'

    default:
      return 'environment-badge--unknown'
  }
})
async function loadEnvironment() {
  try {
    const response = await apiFetch('/api/system/environment')

    environment.value = typeof response === 'string'
        ? response
        : response.environment
  } catch {
    environment.value = 'UNKNOWN'
  }
}


// MAIL ASSISTANCE BECAUSE PEOPLE CANT RTFM
const email = 'support@your.domain'
const subject = 'Support Request - Helios'
const body = `Dear support,

I need help with...`

const mailtoLink = computed(() => {
  const params = new URLSearchParams({
    subject,
    body,
    cc: 'support@your.domain'
  })

  return `mailto:${email}?${params.toString()}`
})

function formatNotificationTimestamp(value) {
  return new Intl.DateTimeFormat(undefined, {
    dateStyle: 'medium',
    timeStyle: 'medium'
  }).format(new Date(value))
}

onMounted(() => {
  loadBuildInfo()
  loadEnvironment()
})
</script>

<template>
  <v-app>
    <v-app-bar color="surface" elevation="0" border="b">
      <v-app-bar-nav-icon color="primary" @click.stop="drawer = !drawer"/>

      <v-toolbar-title>
        <div class="d-flex align-center ga-3 font-weight-medium">
          <span>Helios</span>

          <div class="environment-badge" :class="environmentClass">
            <span class="environment-dot">
              <span class="environment-dot-pulse" />
            </span>
            {{ normalizedEnvironment }}
          </div>
        </div>
      </v-toolbar-title>

      <v-spacer />

      <v-btn
          class="me-6"
          color="flatDarkGray"
          variant="plain"
          prepend-icon="mdi-email-outline"
          :href="mailtoLink"
      >
        v{{ buildInfo.version }} • {{ buildInfo.hash }}
      </v-btn>
    </v-app-bar>

    <v-navigation-drawer v-model="drawer" color="surface" temporary>
      <div class="drawer-content">
        <v-list
            nav
            density="comfortable"
            class="pa-3"
        >
          <v-list-item
              v-for="item in items"
              :key="item.route"
              :to="item.route"
              :title="item.title"
              :prepend-icon="item.icon"
              color="primary"
              rounded="lg"
              @click="$vuetify.display.smAndDown && (drawer = false)"
          />
        </v-list>

        <div class="drawer-spacer" />
      </div>
    </v-navigation-drawer>

    <v-main class="main-content">
      <router-view />
    </v-main>

    <v-snackbar
        v-model="notificationVisible"
        location="bottom right"
        :timeout="-1"
        class="notification-snackbar"
    >
      <v-card v-if="currentNotification" color="surface-bright" class="notification-card">
        <div class="notification-header">
          <v-icon
              :color="notificationColor"
              :icon="notificationIcon"
              size="22"
          />

          <div class="notification-heading">
            <div class="notification-title">
              {{ currentNotification.title }}
            </div>

            <div
                v-if="currentNotification.subtitle"
                class="notification-subtitle"
            >
              {{ currentNotification.subtitle }}
            </div>

            <div class="notification-timestamp">
              {{ formatNotificationTimestamp(currentNotification.postedAt) }}
            </div>
          </div>

          <v-btn
              icon="mdi-close"
              color="primary"
              variant="text"
              size="small"
              aria-label="Dismiss notification"
              @click="dismissNotification"
          />
        </div>

        <div class="notification-scroll-area">
          <p v-if="currentNotification.message" class="notification-message">
            {{ currentNotification.message }}
          </p>

          <pre v-if="currentNotification.code" class="notification-code">
            <code>{{ currentNotification.code }}</code>
          </pre>

          <div v-if="notifications.length > 1" class="notification-queue">
            {{ notifications.length - 1 }} more notification(s) queued
          </div>
        </div>
      </v-card>
    </v-snackbar>
  </v-app>
</template>


<style scoped>
.app-logo {
  display: block;
  width: auto;
  height: 28px;
}

.drawer-content {
  display: flex;
  height: 100%;
  flex-direction: column;
}

.drawer-spacer {
  flex: 1;
}

.main-content {
  min-height: 100vh;
  background: rgb(var(--v-theme-background));
}


.notification-snackbar :deep(.v-snackbar__wrapper) {
  max-width: none;
  background: transparent;
  box-shadow: none;
}

.notification-snackbar :deep(.v-snackbar__content) {
  padding: 0;
}

.notification-card {
  width: min(620px, calc(100vw - 32px));
  min-height: 260px;
  max-height: min(720px, calc(100vh - 64px));
  overflow: hidden;
  border: 1px solid rgb(var(--v-theme-border));
  color: rgb(var(--v-theme-foreground));
  box-shadow: 0 12px 36px rgb(0 0 0 / 35%);
}

.notification-header {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 18px 20px;
  border-bottom: 1px solid rgb(var(--v-theme-border));
  background: rgb(var(--v-theme-surface));
}

.notification-heading {
  min-width: 0;
  flex: 1;
}

.notification-title {
  color: rgb(var(--v-theme-foreground));
  font-size: 16px;
  font-weight: 700;
  line-height: 1.4;
}

.notification-subtitle {
  margin-top: 4px;
  color: rgb(var(--v-theme-flatLightGrey));
  font-size: 14px;
  line-height: 1.5;
}

.notification-scroll-area {
  max-height: min(620px, calc(100vh - 150px));
  padding: 18px 20px 20px;
  overflow-y: auto;
  overscroll-behavior: contain;
  scrollbar-gutter: stable;
}

.notification-message {
  margin: 0;
  color: rgb(var(--v-theme-foreground));
  font-size: 14px;
  line-height: 1.6;
  overflow-wrap: anywhere;
  white-space: pre-wrap;
}

.notification-code {
  max-height: 360px;
  padding: 16px;
  margin: 16px 0 0;
  overflow-x: hidden;
  overflow-y: auto;
  border: 1px solid rgb(var(--v-theme-border));
  border-radius: 8px;
  background: rgb(var(--v-theme-background));
  color: rgb(var(--v-theme-flatLightGrey));
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  font-size: 12px;
  line-height: 1.6;
  overflow-wrap: anywhere;
  tab-size: 4;
  user-select: text;
  white-space: pre-wrap;
  word-break: break-word;
}

.notification-code code {
  display: block;
  color: inherit;
  font: inherit;
  white-space: inherit;
}

.notification-timestamp {
  margin-top: 6px;
  color: rgb(var(--v-theme-muted));
  font-size: 12px;
  line-height: 1.4;
}

.notification-queue {
  margin-top: 16px;
  color: rgb(var(--v-theme-muted));
  font-size: 12px;
}

.environment-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 5px 10px;
  border: 1px solid;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.06em;
  line-height: 1;
}

.environment-badge--test {
  border-color: rgb(var(--v-theme-warning) / 35%);
  border-width: 0px;
  background: rgb(var(--v-theme-warning) / 12%);
  color: rgb(var(--v-theme-warning));
}

.environment-badge--production {
  border-color: rgb(var(--v-theme-success) / 35%);
  background: rgb(var(--v-theme-success) / 12%);
  color: rgb(var(--v-theme-success));
}

.environment-badge--unknown {
  border-color: rgb(var(--v-theme-muted) / 35%);
  background: rgb(var(--v-theme-muted) / 12%);
  color: rgb(var(--v-theme-muted));
}

.environment-dot {
  position: relative;
  display: inline-flex;
  width: 7px;
  height: 7px;
  flex: 0 0 7px;
  border-radius: 50%;
  background: currentColor;
}

.environment-dot-pulse {
  position: absolute;
  inset: 0;
  border-radius: inherit;
  background: currentColor;
  animation: environment-pulse 2s ease-out infinite;
}

@keyframes environment-pulse {
  0% {
    opacity: 0.7;
    transform: scale(1);
  }

  70%,
  100% {
    opacity: 0;
    transform: scale(2.4);
  }
}

@media (prefers-reduced-motion: reduce) {
  .environment-dot-pulse {
    animation: none;
  }
}
</style>
