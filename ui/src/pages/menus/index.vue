<template>
  <section class="page-body">
    <!-- 页面头部 -->
    <div class="page-intro">
      <div>
        <span class="eyebrow">ACCESS CONTROL</span>
        <h2>菜单权限</h2>
        <p>按角色配置系统菜单，保存后立即影响当前用户可见导航。</p>
      </div>
    </div>

    <!-- 错误/提示信息 -->
    <el-alert v-if="error" :title="error" type="error" show-icon closable @close="error = ''" />

    <!-- 加载状态 -->
    <el-skeleton v-if="loading" :rows="6" animated />

    <!-- 主内容区 -->
    <div v-else class="menu-layout">
      <!-- 角色列表卡片 -->
      <el-card class="menu-card role-list-card" shadow="never">
        <div class="card-title">
          <b>角色</b>
        </div>
        <div class="role-list">
          <el-button
              v-for="role in roles"
              :key="role.roleId"
              class="role-item"
              :type="selectedRole?.roleId === role.roleId ? 'primary' : 'default'"
              plain
              @click="selectRole(role)"
          >
            <span class="role-item-content">
              <span class="role-name">{{ role.roleName }}</span>
              <span class="role-key">{{ role.roleKey }}</span>
            </span>
          </el-button>
        </div>
      </el-card>

      <!-- 菜单授权卡片 -->
      <el-card class="menu-card" shadow="never">
        <div class="card-title">
          <b>{{ selectedRole?.roleName }} · 菜单授权</b>
          <el-button type="primary" :disabled="!selectedRole" @click="save">保存授权</el-button>
        </div>
        <MenuNode
            v-for="item in menus"
            :key="item.menuId"
            :item="item"
            :checked="checked"
            @toggle="toggle"
        />
      </el-card>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { api } from '../../api'
import MenuNode from './MenuNode.vue'

const props = defineProps({
  currentId: Number
})

const menus = ref([])
const roles = ref([])
const selectedRole = ref(null)
const checked = ref(new Set())
const loading = ref(true)
const error = ref('')

async function load() {
  try {
    const data = await api.menuManage(props.currentId)
    menus.value = data.menus || []
    roles.value = data.roles || []
    selectRole(roles.value[0])
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
}

function selectRole(role) {
  selectedRole.value = role
  checked.value = new Set(role?.menuIds || [])
}

function findNode(nodes, id) {
  for (const node of nodes) {
    if (node.menuId === id) return node
    const found = findNode(node.children || [], id)
    if (found) return found
  }
  return null
}

function findParent(nodes, id, parent = null) {
  for (const node of nodes) {
    if (node.menuId === id) return parent
    const found = findParent(node.children || [], id, node)
    if (found) return found
  }
  return null
}

function addParents(next, node) {
  const parent = findParent(menus.value, node.menuId)
  if (parent) {
    next.add(parent.menuId)
    addParents(next, parent)
  }
}

function addChildren(next, node) {
  ;(node.children || []).forEach((child) => {
    next.add(child.menuId)
    addChildren(next, child)
  })
}

function removeChildren(next, node) {
  ;(node.children || []).forEach((child) => {
    next.delete(child.menuId)
    removeChildren(next, child)
  })
}

function toggle(id) {
  const node = findNode(menus.value, id)
  if (!node) return
  const next = new Set(checked.value)
  if (next.has(id)) {
    next.delete(id)
    removeChildren(next, node)
  } else {
    next.add(id)
    addChildren(next, node)
    addParents(next, node)
  }
  checked.value = next
}

async function save() {
  try {
    await api.roleMenus(props.currentId, selectedRole.value.roleId, [...checked.value])
    error.value = '已保存菜单授权'
  } catch (e) {
    error.value = e.message
  }
}

onMounted(load)
</script>

<style scoped>
/* 页面主体 */
.page-body {
  padding: 24px 32px;
  max-width: 1200px;
  margin: 0 auto;
  font-family: 'Inter', system-ui, -apple-system, sans-serif;
  color: #1e293b;
  background-color: #f8fafc;
  min-height: 100vh;
  box-sizing: border-box;
}

/* 页面简介区域 */
.page-intro {
  margin-bottom: 28px;
}

.eyebrow {
  display: inline-block;
  font-size: 0.75rem;
  font-weight: 600;
  letter-spacing: 0.05em;
  text-transform: uppercase;
  color: #6366f1;
  background: #eef2ff;
  padding: 4px 10px;
  border-radius: 20px;
  margin-bottom: 12px;
}

.page-intro h2 {
  font-size: 1.75rem;
  font-weight: 700;
  margin: 0 0 6px 0;
  color: #0f172a;
}

.page-intro p {
  font-size: 0.95rem;
  color: #64748b;
  margin: 0;
}

/* 错误提示横幅 */
.error-banner {
  background: #fef2f2;
  border-left: 4px solid #ef4444;
  color: #b91c1c;
  padding: 12px 16px;
  border-radius: 8px;
  margin-bottom: 20px;
  font-size: 0.9rem;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03);
}

/* 加载状态 */
.loading-state {
  text-align: center;
  padding: 60px 0;
  color: #94a3b8;
  font-size: 1rem;
}

/* 菜单布局：两列 */
.menu-layout {
  display: grid;
  grid-template-columns: 380px 1fr;
  gap: 12px;
  align-items: start;
}

/* 卡片通用样式 */
.menu-card {
  background: #ffffff;
  border-radius: 16px;
  box-shadow:
      0 1px 3px rgba(0, 0, 0, 0.05),
      0 1px 2px rgba(0, 0, 0, 0.03);
  padding: 20px 20px 16px 20px;
  border: 1px solid #e9eef4;
  transition: box-shadow 0.2s ease;
}

.menu-card:hover {
  box-shadow:
      0 4px 12px rgba(0, 0, 0, 0.06),
      0 2px 4px rgba(0, 0, 0, 0.02);
}

/* 卡片标题区域 */
.card-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f1f5f9;
}

.card-title b {
  font-size: 0.95rem;
  font-weight: 600;
  color: #334155;
  letter-spacing: 0.02em;
}

/* 角色列表按钮 */
.role-item {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  width: 100%;
  background: transparent;
  border: 1px solid transparent;
  border-radius: 10px;
  padding: 12px 14px;
  margin-bottom: 4px;
  cursor: pointer;
  transition: all 0.15s ease;
  font-size: 0.95rem;
  color: #1e293b;
  font-weight: 500;
  text-align: left;
  font-family: inherit;
  line-height: 1.4;
}

.role-item small {
  font-size: 0.7rem;
  font-weight: 400;
  color: #94a3b8;
  margin-top: 2px;
  letter-spacing: 0.02em;
}

.role-item:hover {
  background-color: #f8fafc;
  border-color: #e2e8f0;
}

.role-item.selected {
  background: #eef2ff;
  border-color: #c7d2fe;
  color: #4f46e5;
  font-weight: 600;
}

.role-item.selected small {
  color: #6366f1;
}

/* 主按钮 */
.primary-button {
  background: #4f46e5;
  color: #ffffff;
  border: none;
  border-radius: 8px;
  padding: 8px 18px;
  font-size: 0.85rem;
  font-weight: 500;
  cursor: pointer;
  transition:
      background 0.15s ease,
      box-shadow 0.15s ease;
  font-family: inherit;
  letter-spacing: 0.01em;
  box-shadow: 0 1px 2px rgba(79, 70, 229, 0.2);
}

.primary-button:hover {
  background: #4338ca;
  box-shadow: 0 4px 10px rgba(79, 70, 229, 0.25);
}

.primary-button:active {
  background: #3730a3;
  transform: translateY(1px);
  box-shadow: 0 1px 2px rgba(79, 70, 229, 0.2);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .page-body {
    padding: 16px;
  }

  .menu-layout {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .page-intro h2 {
    font-size: 1.4rem;
  }

  .card-title {
    flex-wrap: wrap;
    gap: 8px;
  }
}
</style>