<template>
  <div class="menu-node">
    <label>
      <el-checkbox
          :model-value="checked.has(item.menuId)"
          @change="$emit('toggle', item.menuId)"
      >
      <span>{{ item.icon || '•' }}</span>
      <b>{{ item.menuName }}</b>
      <small>{{ item.path }}</small>
      </el-checkbox>
    </label>

    <div class="menu-children">
      <MenuNode
          v-for="child in item.children"
          :key="child.menuId"
          :item="child"
          :checked="checked"
          @toggle="$emit('toggle', $event)"
      />
    </div>
  </div>
</template>

<script setup>
defineProps({
  item: Object,
  checked: Object
})

defineEmits(['toggle'])
</script>