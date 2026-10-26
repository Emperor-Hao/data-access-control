<template>
  <section class="page-body">
    <div class="welcome">
      <div>
        <span class="eyebrow">LIVE DATA SCOPE</span>
        <h2>{{ account.name }}，欢迎回来</h2>
        <p>
          当前请求将携带 <code>X-User-Id: {{ currentId }}</code>，后端切面会自动注入数据范围。
        </p>
      </div>
      <div class="scope-badge">
        <span>当前数据权限</span>
        <strong>
          {{
            scopeText(
                me?.dataScope ||
                (currentId === 1 ? 1 : currentId === 2 ? 3 : currentId === 3 ? 4 : 5)
            )
          }}
        </strong>
      </div>
    </div>

    <div class="stat-grid">
      <div class="stat-card">
        <span>可见用户</span>
        <strong>{{ users.length }}</strong>
        <small>经过 DataScope 过滤</small>
        <b class="stat-icon blue">♙</b>
      </div>

      <div class="stat-card">
        <span>可见合同</span>
        <strong>{{ contracts.length }}</strong>
        <small>当前权限范围内</small>
        <b class="stat-icon orange">▤</b>
      </div>

      <div class="stat-card">
        <span>合同总额</span>
        <strong>{{ formatMoney(totalAmount) }}</strong>
        <small>可访问业务数据合计</small>
        <b class="stat-icon green">￥</b>
      </div>

      <div class="stat-card">
        <span>所属部门</span>
        <strong>{{ me?.deptId || '—' }}</strong>
        <small>主部门 ID</small>
        <b class="stat-icon purple">⌘</b>
      </div>
    </div>

    <div class="dashboard-grid">
      <div class="panel">
        <div class="panel-head">
          <div>
            <h3>数据权限链路</h3>
            <p>一次请求的完整过滤过程</p>
          </div>
          <span class="live">● LIVE</span>
        </div>

        <div class="flow">
          <div>
            <b>01</b>
            <span>请求进入</span>
            <small>X-User-Id: {{ currentId }}</small>
          </div>
          <i>→</i>

          <div>
            <b>02</b>
            <span>加载用户</span>
            <small>{{ account.name }} / {{ scopeText(me?.dataScope) }}</small>
          </div>
          <i>→</i>

          <div>
            <b>03</b>
            <span>AOP 切面</span>
            <small>注入 params.dataScope</small>
          </div>
          <i>→</i>

          <div>
            <b>04</b>
            <span>安全返回</span>
            <small>仅展示授权数据</small>
          </div>
        </div>
      </div>

      <div class="panel recent">
        <div class="panel-head">
          <div>
            <h3>当前可见用户</h3>
            <p>前 5 条数据</p>
          </div>
          <button class="text-button" @click="emit('navigate', 'users')">
            查看全部 →
          </button>
        </div>

        <div
            v-for="item in users.slice(0, 5)"
            :key="item.userId"
            class="user-row"
        >
          <span class="row-avatar">{{ initials(item.nickName) }}</span>
          <div>
            <b>{{ item.nickName }}</b>
            <small>{{ item.userName }} · {{ item.deptName || `部门 ${item.deptId}` }}</small>
          </div>
          <span class="row-scope">{{ scopeText(item.dataScope) }}</span>
        </div>
      </div>
    </div>

    <div class="contract-overview-panel">
      <div class="panel-head contract-panel-head">
        <div>
          <h3>合同数据</h3>
          <p>当前数据权限范围内的合同概览</p>
        </div>
        <button class="text-button" @click="emit('navigate', 'contracts')">
          查看全部 →
        </button>
      </div>

      <div class="contract-overview-grid">
        <div class="contract-chart-card">
          <div class="contract-card-head">
            <div class="contract-card-title">
              <span class="contract-card-icon contract-card-icon-bar">↗</span>
              <div>
                <h4>合同金额排行</h4>
                <p>按合同金额从高到低展示前 5 项</p>
              </div>
            </div>
            <span class="contract-card-badge">TOP 5</span>
          </div>
          <div class="contract-chart-wrap">
            <div class="contract-summary">
              <div>
                <span>合同总额</span>
                <strong>{{ formatMoney(totalAmount) }}</strong>
              </div>
              <div>
                <span>合同数量</span>
                <strong>{{ contracts.length }}</strong>
              </div>
            </div>
            <div ref="contractChartRef" class="contract-chart"></div>
          </div>
        </div>

        <div class="contract-chart-card">
          <div class="contract-card-head">
            <div class="contract-card-title">
              <span class="contract-card-icon contract-card-icon-line">⌁</span>
              <div>
                <h4>合同金额趋势</h4>
                <p>重点合同金额分布与变化趋势</p>
              </div>
            </div>
            <span class="contract-card-badge contract-card-badge-soft">金额</span>
          </div>
          <div class="contract-line-wrap">
            <div v-if="contracts.length" ref="contractLineRef" class="contract-line"></div>
            <el-empty v-else description="当前权限范围内没有合同数据" :image-size="72" />
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { createChart } from '../../utils/echarts'

const props = defineProps({
  account: Object,
  currentId: Number,
  me: Object,
  users: Array,
  contracts: Array,
  totalAmount: Number,
  formatMoney: Function,
  scopeText: Function,
  initials: Function
})

const emit = defineEmits(['navigate'])

const contractChartRef = ref(null)
let contractChart = null
const contractLineRef = ref(null)
let contractLineChart = null

function updateContractChart() {
  if (!contractChart) return

  const rows = [...props.contracts]
    .sort((left, right) => Number(right.amount || 0) - Number(left.amount || 0))
    .slice(0, 5)
    .reverse()

  contractChart.setOption({
    grid: { top: 8, right: 18, bottom: 8, left: 8, containLabel: true },
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      valueFormatter: (value) => props.formatMoney(value)
    },
    xAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: '#edf1f7' } },
      axisLabel: {
        color: '#9aa7b5',
        fontSize: 10,
        formatter: (value) => `${Math.round(Number(value) / 10000)}万`
      }
    },
    yAxis: {
      type: 'category',
      data: rows.map((item) => item.contractName),
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#526176', fontSize: 10, width: 100, overflow: 'truncate' }
    },
    series: [
      {
        type: 'bar',
        barWidth: 13,
        data: rows.map((item) => Number(item.amount || 0)),
        showBackground: true,
        backgroundStyle: { color: '#f5f7fb', borderRadius: 8 },
        itemStyle: { color: '#6876ff', borderRadius: [0, 8, 8, 0] }
      }
    ]
  }, true)
}

async function initContractChart() {
  await nextTick()
  if (!contractChartRef.value) return

  contractChart = createChart(contractChartRef.value, {})
  updateContractChart()
}

function updateContractLine() {
  if (!contractLineChart) return

  const rows = [...props.contracts]
    .sort((left, right) => Number(right.amount || 0) - Number(left.amount || 0))
    .slice(0, 5)

  contractLineChart.setOption({
    grid: { top: 18, right: 14, bottom: 42, left: 48, containLabel: true },
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'line' },
      valueFormatter: (value) => props.formatMoney(value)
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: rows.map((item) => item.contractName || item.contractNo || '未命名合同'),
      axisLine: { lineStyle: { color: '#dfe5ee' } },
      axisTick: { show: false },
      axisLabel: {
        color: '#7e8da2',
        fontSize: 9,
        interval: 0,
        rotate: rows.length > 3 ? 28 : 0,
        formatter: (value) => value.length > 6 ? `${value.slice(0, 6)}…` : value
      }
    },
    yAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: '#edf1f7' } },
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: {
        color: '#9aa7b5',
        fontSize: 9,
        formatter: (value) => `${Math.round(Number(value) / 10000)}万`
      }
    },
    series: [
      {
        name: '合同金额',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 7,
        data: rows.map((item) => Number(item.amount || 0)),
        lineStyle: { width: 3, color: '#6876ff' },
        itemStyle: { color: '#6876ff', borderColor: '#fff', borderWidth: 2 },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(104, 118, 255, 0.28)' },
              { offset: 1, color: 'rgba(104, 118, 255, 0.03)' }
            ]
          }
        }
      }
    ]
  }, true)
}

async function initContractLine() {
  await nextTick()
  if (!contractLineRef.value || contractLineChart) return

  contractLineChart = createChart(contractLineRef.value, {})
  updateContractLine()
}

watch(() => props.contracts, async () => {
  updateContractChart()

  if (!props.contracts.length) {
    contractLineChart?.dispose()
    contractLineChart = null
    return
  }

  await initContractLine()
  updateContractLine()
}, { deep: true })

onMounted(async () => {
  await initContractChart()
  await initContractLine()
})

onBeforeUnmount(() => {
  contractChart?.dispose()
  contractChart = null
  contractLineChart?.dispose()
  contractLineChart = null
})
</script>