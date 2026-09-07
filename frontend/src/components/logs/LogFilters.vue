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

  level: {
    type: String,
    default: null
  }
})


defineEmits([
  'update:from',
  'update:to',
  'update:level',
  'reset'
])


const levels = [
  'TRACE',
  'DEBUG',
  'INFO',
  'WARN',
  'ERROR'
]
</script>


<template>
  <v-card border class="log-filters">
    <v-card-text class="py-3">
      <div class="filter-bar">
        <v-text-field
            :model-value="from"
            type="datetime-local"
            label="From"
            prepend-inner-icon="mdi-calendar-start"
            class="log-date-field"
            hide-details
            @update:model-value="$emit('update:from', $event)"
        />


        <v-text-field
            :model-value="to"
            type="datetime-local"
            label="To"
            prepend-inner-icon="mdi-calendar-end"
            class="log-date-field"
            hide-details
            @update:model-value="$emit('update:to', $event)"
        />


        <v-select
            :model-value="level"
            :items="levels"
            label="Log level"
            prepend-inner-icon="mdi-filter-variant"
            clearable
            hide-details
            @update:model-value="$emit('update:level', $event)"
        />


        <v-btn
            icon="mdi-filter-off-outline"
            color="foreground"
            variant="text"
            aria-label="Reset filters"
            title="Reset filters"
            @click="$emit('reset')"
        />
      </div>
    </v-card-text>
  </v-card>
</template>


<style scoped>
.log-filters {
  flex: 0 0 auto;
}

.filter-bar {
  display: grid;
  grid-template-columns:
      minmax(220px, 1fr)
      minmax(220px, 1fr)
      minmax(180px, 0.65fr)
      auto;
  gap: 12px;
  align-items: center;
}

.log-date-field :deep(input[type='datetime-local']::-webkit-calendar-picker-indicator) {
  visibility: hidden;
}

@media (max-width: 960px) {
  .filter-bar {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 600px) {
  .filter-bar {
    grid-template-columns: 1fr;
  }
}
</style>