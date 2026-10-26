<template>
  <header class="topbar">
    <div>
      <span class="eyebrow">CONTROL CENTER / 2026</span>
      <h1>{{ pageTitle }}</h1>
    </div>

    <div class="top-user">
      <div
          class="avatar"
          :style="{ background: account.color }"
      >
        {{ initials(account.name) }}
      </div>
      <div>
        <b>{{ account.name }}</b>
        <small>{{ account.account }} · {{ account.scope }}</small>
      </div>
    </div>
  </header>
</template>

<script setup>
defineProps({
  pageTitle: {
    type: String,
    default: ''
  },
  account: {
    type: Object,
    required: true
  },
  initials: {
    type: Function,
    default: (name) => (name || '?').slice(-1)
  }
})
</script>