import { computed, ref } from 'vue'


const notifications = ref([])

const currentNotification = computed(() => {
    return notifications.value[0] ?? null
})

function addNotification({
                             type = 'info',
                             title,
                             subtitle = '',
                             message = '',
                             code = null
                         }) {
    notifications.value.push({
        id: crypto.randomUUID(),
        type,
        title,
        subtitle,
        message,
        code: formatCode(code),
        postedAt: new Date().toISOString()
    })
}

function dismissNotification() {
    notifications.value.shift()
}

function formatCode(code) {
    if (code === undefined || code === null || code === '') {
        return null
    }

    if (typeof code === 'string') {
        return code
    }

    return JSON.stringify(code, null, 2)
}

export function useNotifications() {
    return {
        notifications,
        currentNotification,
        addNotification,
        dismissNotification
    }
}
