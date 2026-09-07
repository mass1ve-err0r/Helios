# Helios
> Java 25 | Spring Boot 4.x | Spring 7.x | Vue 3.x


> [!NOTE]
> The companion app for your MDM, HeliosMobile, can be found [HERE](https://github.com/mass1ve-err0r/Helios-iOS)

Helios is a Spring Boot push notification service with an embedded management UI.

It provides one deployable application for device registration, push notification delivery, monitoring, and runtime administration.

The frontend is built with Vue 3 and Vuetify. It is packaged with the Spring application, so a normal deployment does not require a separate frontend service.

## Product Images

Monitoring View:
![Screenshot1](/GitImages/Screen1.png)

Device View / AdHoc Notification Dispatch:
![Screenshot2](/GitImages/Screen2.png)

## Features

- Register devices for push notification delivery
- Send push notifications to registered devices
- Send adhoc notifications from the management UI
- Store notification state and delivery data
- Review persisted notification history
- Review application logs
- Change Spring log levels at runtime for better debugging

## Technology

Backend:

- Java
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Liquibase

Frontend:

- Vue 3
- Vuetify
- Vite

The frontend is built into the Spring application for production use.

## Requirements

Install the following software before you build Helios:

- JDK 25
- PostgreSQL
- Node.js and npm

You only need Node.js when you build or develop the frontend.

## Configuration

Helios itself relies on APN and FCM, however rightnow only APN is implemented.

For APN to work you need to supply your own applications' AuthKey, instructions can be found
here: https://developer.apple.com/documentation/usernotifications/registering-your-app-with-apns


## Database

Helios uses Liquibase to manage its database schema.

Create an empty PostgreSQL database and configure the Spring datasource.

Helios applies the required Liquibase changesets during application startup.

Do not create the application tables manually.

## Build

Simply invoke the Gradle task to generate a production-ready artifact:

```bash
bootJar
```

## Run

Start the built Spring application:

```bash
java -jar <helios-jar>
```

Helios starts the backend and serves the embedded frontend from the same application.

Open the management UI in your browser and sign in with the configured Basic Authentication credentials.

The UI can be found under "/ui/pushnotifications".


## Management UI

The management UI provides the main operational views.

### Devices

The Devices view shows registered devices and their recent activity.

You can also send a test notification directly to that device.

### Notifications

The Notifications view shows persisted push notifications and their current state.

Select a notification to review its full details and payload.

### Logs

The Logs view shows persisted application logs.

You can filter logs by time and log level.


### Settings

The Settings view manages explicit Spring log-level overrides.

You can configure a package or class with one of the supported levels:

- TRACE
- DEBUG
- INFO
- WARN
- ERROR
- OFF

Changes take effect immediately.

Helios stores these overrides in DB and applies them again after the next application start.

Delete an override to restore Spring's inherited log configuration.

## API

Helios exposes REST endpoints under:

```text
/api
```

The API includes device registration, push notification operations, monitoring data, and management functions.

Use the management UI for normal administration.

Use the REST API when another service must integrate with Helios.

## Device Registration

A client must provide the configured registration safety token when it registers a device.

Helios rejects the registration request when the token is invalid.

After registration, Helios stores the device data required for notification delivery.


## Production Deployment

If you are going to production with this, please change ANY exemplary secrets used in this application, otherwise you are at risk.


## Credits

- My local pigeons
