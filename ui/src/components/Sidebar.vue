<template>
  <el-aside class="sidebar">
    <div class="brand">
      <div class="brand-mark">DS</div>
      <div>
        <strong>DataScope</strong>
        <small>数据权限控制台</small>
      </div>
    </div>

    <div class="side-label">工作台</div>

    <el-menu
        class="sidebar-menu"
        :default-active="activeTab"
        :default-openeds="systemMenuOpen ? ['system'] : []"
        background-color="#0b1f3a"
        text-color="#91a3bb"
        active-text-color="#ffffff"
        @select="navigate"
        @open="setSystemMenuOpen(true)"
        @close="setSystemMenuOpen(false)"
    >
      <el-menu-item v-if="hasMenu('overview')" index="overview">
        <span class="menu-icon">◈</span>
        <span>权限概览</span>
      </el-menu-item>

      <el-menu-item v-if="hasMenu('users')" index="users">
        <span class="menu-icon">♙</span>
        <span>用户数据</span>
      </el-menu-item>

      <el-menu-item v-if="hasMenu('contracts')" index="contracts">
        <span class="menu-icon">▤</span>
        <span>合同数据</span>
      </el-menu-item>

      <el-menu-item v-if="hasMenu('departments')" index="departments">
        <span class="menu-icon">⌘</span>
        <span>组织架构</span>
      </el-menu-item>

      <div v-if="hasMenu('system')" class="side-label permission-label">权限管理</div>

      <el-sub-menu v-if="hasMenu('system')" index="system">
        <template #title>
          <span class="menu-icon">⚙</span>
          <span>系统管理</span>
        </template>
        <el-menu-item v-if="hasMenu('menus')" index="menus">
          <span class="menu-icon">☷</span>
          <span>菜单权限</span>
        </el-menu-item>
      </el-sub-menu>
    </el-menu>

    <div class="sidebar-foot">
      <div class="security-dot"></div>
      <div>
        <b>权限引擎运行中</b>
        <small>AOP · MyBatis DataScope</small>
      </div>
    </div>
  </el-aside>
</template>

<script setup>
defineProps({
  activeTab: {
    type: String,
    default: 'overview'
  },
  systemMenuOpen: {
    type: Boolean,
    default: false
  },
  hasMenu: {
    type: Function,
    required: true
  }
})

const emit = defineEmits(['navigate', 'update:system-menu-open'])

function navigate(tab) {
  emit('navigate', tab)
}

function setSystemMenuOpen(value) {
  emit('update:system-menu-open', value)
}
</script>