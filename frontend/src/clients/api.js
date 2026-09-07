import { useNotifications } from '@/composables/useNotifications'
const { addNotification } = useNotifications()


export class ApiError extends Error {
    constructor(status, body) {
        super(`HTTP request failed with status ${status}`)

        this.status = status
        this.body = body
    }
}

export async function apiFetch(path, options = {}) {
    const {
        notification,
        ...requestOptions
    } = options

    const headers = new Headers(requestOptions.headers ?? {})

    if (requestOptions.body !== undefined && !(requestOptions.body instanceof FormData) && !headers.has('Content-Type')) {
        headers.set('Content-Type', 'application/json')
    }

    const response = await fetch(path, {
        ...requestOptions,
        headers
    })

    await createNotification(notification, response)

    if (!response.ok) {
        throw new ApiError(response.status, await readResponseBody(response))
    }

    if (response.status === 204) {
        return null
    }

    return readResponseBody(response)
}

async function createNotification(notification, response) {
    if (typeof notification !== 'function') {
        return
    }

    const notificationConfiguration = await notification(response.clone())

    if (notificationConfiguration) {
        addNotification(notificationConfiguration)
    }
}

async function readResponseBody(response) {
    const contentType = response.headers.get('content-type') ?? ''

    if (contentType.includes('application/json')) {
        return response.json()
    }

    return response.text()
}
