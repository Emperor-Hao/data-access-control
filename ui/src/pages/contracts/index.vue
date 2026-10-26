<template>
  <section class="page-body">
    <div class="page-intro">
      <div>
        <span class="eyebrow">FILTERED RESOURCE</span>
        <h2>合同数据</h2>
        <p>以下结果已经由后端 DataScopeAspect 按当前用户权限过滤。</p>
      </div>
      <span class="result-count">{{ visibleCount }} 条结果</span>
    </div>

    <el-form class="filter-bar" inline @submit.prevent="loadData(true)">
      <el-form-item>
        <el-input
          :model-value="contractName"
          placeholder="搜索合同名称…"
          clearable
          @update:model-value="$emit('update:contractName', $event)"
          @keyup.enter="loadData(true)"
        />
      </el-form-item>
      <el-form-item>
          <el-button type="primary" :loading="loading" @click="loadData(true)">查询</el-button>
      </el-form-item>
    </el-form>

    <div class="table-panel">
      <el-table v-loading="loading" :data="contracts" stripe>
        <el-table-column prop="contractNo" label="合同编号" min-width="150" />
        <el-table-column label="合同名称" min-width="200">
          <template #default="{ row }"><b>{{ row.contractName }}</b></template>
        </el-table-column>
        <el-table-column label="归属部门" min-width="150">
          <template #default="{ row }">{{ row.deptName || `部门 ${row.deptId}` }}</template>
        </el-table-column>
        <el-table-column label="创建人" min-width="140">
          <template #default="{ row }">{{ row.createByName || row.createBy }}</template>
        </el-table-column>
        <el-table-column label="合同金额" min-width="150">
          <template #default="{ row }"><span class="money">{{ formatMoney(row.amount) }}</span></template>
        </el-table-column>
        <template #empty><el-empty description="当前权限范围内没有合同数据" /></template>
      </el-table>
    </div>
    <PagePagination
      :current-page="contractPage"
      :page-size="contractPageSize"
      :total="contractTotal"
      @current-change="changeContractPage"
      @size-change="changeContractPageSize"
    />
  </section>
</template>

<script setup>
import PagePagination from '../../components/PagePagination.vue'

defineProps({
  contracts: Array,
  contractName: String,
  visibleCount: Number,
  contractPage: Number,
  contractPageSize: Number,
  contractTotal: Number,
  loading: Boolean,
  formatMoney: Function,
  loadData: Function,
  changeContractPage: Function,
  changeContractPageSize: Function
})

defineEmits(['update:contractName'])
</script>