<script setup>
defineProps({
  from: {
    type: String,
    required: true
  },

  to: {
    type: String,
    required: true
  },

  state: {
    type: String,
    default: null
  },

  receiver: {
    type: String,
    default: ''
  },

  states: {
    type: Array,
    required: true
  }
})


defineEmits([
  'update:from',
  'update:to',
  'update:state',
  'update:receiver'
])


function stateDefinition(states, state) {
  return states.find(
      definition => definition.value === state
  )
}
</script>


<template>
  <v-card border class="filters-card">
    <v-card-text>
      <v-row>
        <v-col cols="12" sm="6" lg="2">
          <v-text-field
              :model-value="from"
              type="datetime-local"
              label="From"
              prepend-inner-icon="mdi-calendar-start"
              class="date-field"
              clearable
              hide-details
              @update:model-value="$emit('update:from', $event ?? '')"
          />
        </v-col>


        <v-col cols="12" sm="6" lg="2">
          <v-text-field
              :model-value="to"
              type="datetime-local"
              label="To"
              prepend-inner-icon="mdi-calendar-end"
              class="date-field"
              clearable
              hide-details
              @update:model-value="$emit('update:to', $event ?? '')"
          />
        </v-col>


        <v-col cols="12" sm="6" lg="2">
          <v-select
              :model-value="state"
              :items="states"
              item-title="title"
              item-value="value"
              label="State"
              prepend-inner-icon="mdi-state-machine"
              clearable
              hide-details
              @update:model-value="$emit('update:state', $event)"
          >
            <template #item="{ props, item }">
              <v-list-item v-bind="props">
                <template #prepend>
                  <v-icon
                      :icon="
                        stateDefinition(states, item.value)?.icon
                        ?? 'mdi-help-circle-outline'
                      "
                      :color="
                        stateDefinition(states, item.value)?.color
                        ?? 'muted'
                      "
                  />
                </template>
              </v-list-item>
            </template>


            <template #selection="{ item }">
              <v-chip
                  :color="
                    stateDefinition(states, item.value)?.color
                    ?? 'muted'
                  "
                  :prepend-icon="
                    stateDefinition(states, item.value)?.icon
                    ?? 'mdi-help-circle-outline'
                  "
                  size="small"
                  variant="tonal"
              >
                {{
                  stateDefinition(states, item.value)?.title
                  ?? item.value
                }}
              </v-chip>
            </template>
          </v-select>
        </v-col>


        <v-col>
          <v-text-field
              :model-value="receiver"
              label="Receiver"
              prepend-inner-icon="mdi-account-outline"
              clearable
              hide-details
              @update:model-value="$emit('update:receiver', $event ?? '')"
          />
        </v-col>
      </v-row>
    </v-card-text>
  </v-card>
</template>


<style scoped>
.filters-card {
  flex: 0 0 auto;
}

.date-field :deep(input[type='datetime-local']::-webkit-calendar-picker-indicator) {
  visibility: hidden;
}
</style>
