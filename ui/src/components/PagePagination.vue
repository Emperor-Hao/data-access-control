<template>
  <div class="pagination-bar">
    <el-pagination
      :current-page="currentPage"
      :page-size="pageSize"
      :total="total"
      layout="prev, pager, next, total"
      background
      @current-change="$emit('current-change', $event)"
      @size-change="$emit('size-change', $event)"
    />
  </div>
</template>

<script setup>
defineProps({
  currentPage: Number,
  pageSize: Number,
  total: Number
})

defineEmits(['current-change', 'size-change'])
</script>