<template>
  <div class="account-selector" @click.stop>
    <button
        class="account-selector-trigger"
        :aria-expanded="accountMenuOpen"
        @click="accountMenuOpen = !accountMenuOpen"
    >
      <span
          class="mini-avatar"
          :style="{ background: account.color }"
      >
        {{ initials(account.name) }}
      </span>
      <span class="account-selector-label">
        <b>{{ account.name }}</b>
        <small>{{ account.account }} · {{ account.scope }}</small>
      </span>
      <span class="selector-chevron" :class="{ open: accountMenuOpen }">⌄</span>
    </button>

    <div v-if="accountMenuOpen" class="account-menu">
      <button
          v-for="item in accounts"
          :key="item.id"
          class="account-menu-item"
          :class="{ selected: currentId === item.id }"
          @click="selectAccount(item.id)"
      >
        <span
            class="mini-avatar"
            :style="{ background: item.color }"
        >
          {{ initials(item.name) }}
        </span>
        <span class="account-selector-label">
          <b>{{ item.name }}</b>
          <small>{{ item.account }} · {{ item.scope }}</small>
        </span>
        <span v-if="currentId === item.id" class="account-check">✓</span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'

defineProps({
  account: {
    type: Object,
    required: true
  },
  accounts: {
    type: Array,
    default: () => []
  },
  currentId: Number,
  initials: {
    type: Function,
    default: (name) => (name || '?').slice(-1)
  }
})

const emit = defineEmits(['select-account'])
const accountMenuOpen = ref(false)

function selectAccount(id) {
  emit('select-account', id)
  accountMenuOpen.value = false
}

function closeAccountMenu() {
  accountMenuOpen.value = false
}

onMounted(() => document.addEventListener('click', closeAccountMenu))
onBeforeUnmount(() => document.removeEventListener('click', closeAccountMenu))
</script>