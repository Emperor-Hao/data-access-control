import { computed, onMounted, ref, watch } from 'vue'
import { api } from '../api'

export const accounts = [
  { id: 1, name: '超级管理员', account: 'admin', scope: '全部数据', color: '#5b6cff' },
  { id: 2, name: '张三', account: 'zhangsan', scope: '本部门', color: '#12a594' },
  { id: 3, name: '李四', account: 'lisi', scope: '本部门及以下', color: '#e58b2d' },
  { id: 4, name: '王五', account: 'wangwu', scope: '仅本人', color: '#d3547d' },
]

const scopeLabels = { 1: '全部数据', 3: '本部门', 4: '本部门及以下', 5: '仅本人' }

export function useDataScope() {
  const currentId = ref(1)
  const me = ref(null)
  const users = ref([])
  const contracts = ref([])
  const departments = ref([])
  const menus = ref([])
  const userName = ref('')
  const contractName = ref('')
  const userPage = ref(1)
  const contractPage = ref(1)
  const userPageSize = ref(10)
  const contractPageSize = ref(10)
  const userTotal = ref(0)
  const contractTotal = ref(0)
  const loading = ref(false)
  const error = ref('')

  const account = computed(
      () => accounts.find((item) => item.id === currentId.value) || accounts[0]
  )
  const totalAmount = computed(() =>
      contracts.value.reduce((total, item) => total + Number(item.amount || 0), 0)
  )

  function flattenTree(nodes, depth = 0, result = []) {
    nodes.forEach((node) => {
      result.push({ ...node, depth })
      if (node.children?.length) flattenTree(node.children, depth + 1, result)
    })
    return result
  }

  const flatDepartments = computed(() => flattenTree(departments.value))

  // 兼容后端升级过程中的两种返回格式：
  // 旧版本直接返回数组，新版本返回 { rows, total, ... }。
  function normalizePage(result) {
    if (Array.isArray(result)) {
      return { rows: result, total: result.length }
    }
    return {
      rows: result?.rows || [],
      total: result?.total || 0
    }
  }

  async function loadData(resetPage = false) {
    if (resetPage) {
      userPage.value = 1
      contractPage.value = 1
    }
    loading.value = true
    error.value = ''
    try {
      const [current, userRows, contractRows, deptRows, menuRows] = await Promise.all([
        api.me(currentId.value),
        api.users(currentId.value, { userName: userName.value, pageNum: userPage.value, pageSize: userPageSize.value }),
        api.contracts(currentId.value, { contractName: contractName.value, pageNum: contractPage.value, pageSize: contractPageSize.value }),
        api.departments(currentId.value),
        api.menuTree(currentId.value)
      ])
      me.value = current
      const userPageResult = normalizePage(userRows)
      const contractPageResult = normalizePage(contractRows)
      users.value = userPageResult.rows
      contracts.value = contractPageResult.rows
      userTotal.value = userPageResult.total
      contractTotal.value = contractPageResult.total
      departments.value = deptRows || []
      menus.value = menuRows || []
    } catch (err) {
      error.value = `${err.message}。请确认 Spring Boot 已运行在 localhost:8080。`
    } finally {
      loading.value = false
    }
  }

  function switchAccount(id) {
    currentId.value = id
  }

  function changeUserPage(page) {
    userPage.value = page
    loadData()
  }

  function changeUserPageSize(size) {
    userPageSize.value = size
    userPage.value = 1
    loadData()
  }

  function changeContractPage(page) {
    contractPage.value = page
    loadData()
  }

  function changeContractPageSize(size) {
    contractPageSize.value = size
    contractPage.value = 1
    loadData()
  }

  function formatMoney(value) {
    return `¥ ${Number(value || 0).toLocaleString('zh-CN', { minimumFractionDigits: 2 })}`
  }

  function scopeText(value) {
    return scopeLabels[value] || `权限级别 ${value}`
  }

  function initials(name) {
    return (name || '?').slice(-1)
  }

  function hasMenu(path) {
    return menus.value.some(
        (item) => item.path === path || item.children?.some((child) => child.path === path)
    )
  }

  watch(currentId, () => loadData(true))
  onMounted(loadData)

  return {
    accounts,
    currentId,
    me,
    users,
    contracts,
    departments,
    menus,
    userName,
    contractName,
    userPage,
    contractPage,
    userPageSize,
    contractPageSize,
    userTotal,
    contractTotal,
    loading,
    error,
    account,
    totalAmount,
    flatDepartments,
    loadData,
    switchAccount,
    changeUserPage,
    changeUserPageSize,
    changeContractPage,
    changeContractPageSize,
    formatMoney,
    scopeText,
    initials,
    hasMenu
  }
}