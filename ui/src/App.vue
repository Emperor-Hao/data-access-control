<template>
  <div class="app-shell">
<!--    菜单目录-->
    <Sidebar
        :active-tab="activeTab"
        :system-menu-open="systemMenuOpen"
        :has-menu="dataScope.hasMenu"
        @navigate="navigate"
        @update:system-menu-open="systemMenuOpen = $event"
    />

<!--    主内容-->
    <main class="main-content">
      <div class="page-header-sticky">
<!--        模拟登录-->
        <section class="account-strip">
          <div>
            <span class="strip-icon">⇄</span>
            <div>
              <b>当前模拟登录视角</b>
              <small>切换用户后，所有列表将重新按后端数据权限过滤</small>
            </div>
          </div>
<!--          模拟角色登录，下拉-->
          <AccountSelector
              :account="dataScope.account"
              :accounts="dataScope.accounts"
              :current-id="dataScope.currentId"
              :initials="dataScope.initials"
              @select-account="selectAccount"
          />
        </section>

<!--        用户角色展示-->
<!--        <PageTopbar-->
<!--            :page-title="pageTitle"-->
<!--            :account="dataScope.account"-->
<!--            :initials="dataScope.initials"-->
<!--        />-->
      </div>

      <el-alert
          v-if="dataScope.error"
          :title="dataScope.error"
          type="error"
          show-icon
          class="global-error"
          closable
          @close="dataScope.error = ''"
      >
        <template #default>
          <el-button link type="danger" @click="dataScope.loadData">重试</el-button>
        </template>
      </el-alert>

      <component
          :is="pageComponents[activeTab]"
          :account="dataScope.account"
          :current-id="dataScope.currentId"
          :me="dataScope.me"
          :users="dataScope.users"
          :contracts="dataScope.contracts"
          :total-amount="dataScope.totalAmount"
          :format-money="dataScope.formatMoney"
          :scope-text="dataScope.scopeText"
          :initials="dataScope.initials"
          :flat-departments="dataScope.flatDepartments"
          :visible-count="visibleCount"
          :user-page="dataScope.userPage"
          :user-page-size="dataScope.userPageSize"
          :user-total="dataScope.userTotal"
          :contract-page="dataScope.contractPage"
          :contract-page-size="dataScope.contractPageSize"
          :contract-total="dataScope.contractTotal"
          :change-user-page="dataScope.changeUserPage"
          :change-user-page-size="dataScope.changeUserPageSize"
          :change-contract-page="dataScope.changeContractPage"
          :change-contract-page-size="dataScope.changeContractPageSize"
          :loading="dataScope.loading"
          v-model:user-name="dataScope.userName"
          v-model:contract-name="dataScope.contractName"
          :load-data="dataScope.loadData"
          @navigate="navigate"
      />

      <el-alert v-if="dataScope.loading" title="正在同步后端权限数据…" type="info" show-icon class="global-loading" />
    </main>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import OverviewPage from './pages/overview/index.vue'
import UsersPage from './pages/users/index.vue'
import ContractsPage from './pages/contracts/index.vue'
import DepartmentsPage from './pages/departments/index.vue'
import MenusPage from './pages/menus/index.vue'
import AccountSelector from './components/AccountSelector.vue'
import PageTopbar from './components/PageTopbar.vue'
import Sidebar from './components/Sidebar.vue'
import { useDataScope } from './composables/useDataScope'

const pageStorageKey = 'data-scope-active-tab'
const availableTabs = ['overview', 'users', 'contracts', 'departments', 'menus']

function getStoredTab() {
  const storedTab = window.localStorage.getItem(pageStorageKey)
  return availableTabs.includes(storedTab) ? storedTab : 'overview'
}

const activeTab = ref(getStoredTab())

// 将 composable 返回的 ref/computed 统一解包，避免嵌套对象传递 Ref
// 导致模板和动态页面组件拿到的不是实际数据。
const dataScope = reactive(useDataScope())

const pageTitles = {
  overview: '权限概览',
  users: '用户数据',
  contracts: '合同数据',
  departments: '组织架构',
  menus: '菜单权限'
}
const pageTitle = computed(() => pageTitles[activeTab.value])

const visibleCount = computed(() =>
    activeTab.value === 'users' ? dataScope.userTotal : dataScope.contractTotal
)

const pageComponents = {
  overview: OverviewPage,
  users: UsersPage,
  contracts: ContractsPage,
  departments: DepartmentsPage,
  menus: MenusPage
}

const systemMenuOpen = ref(false)

function navigate(tab) {
  activeTab.value = tab
  window.localStorage.setItem(pageStorageKey, tab)
  if (tab === 'menus') systemMenuOpen.value = true
}

function selectAccount(id) {
  dataScope.switchAccount(id)
}
</script>