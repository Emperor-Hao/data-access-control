<template>
  <section class="page-body">
    <div class="page-intro">
      <div>
        <span class="eyebrow">FILTERED RESOURCE</span>
        <h2>用户数据</h2>
        <p>以下结果已经由后端 DataScopeAspect 按当前用户权限过滤。</p>
      </div>
      <span class="result-count">{{ visibleCount }} 条结果</span>
    </div>

    <el-form class="filter-bar" inline @submit.prevent="loadData(true)">
      <el-form-item>
        <el-input
          :model-value="userName"
          placeholder="搜索登录账号…"
          clearable
          @update:model-value="$emit('update:userName', $event)"
          @keyup.enter="loadData(true)"
        />
      </el-form-item>
      <el-form-item>
          <el-button type="primary" :loading="loading" @click="loadData(true)">查询</el-button>
      </el-form-item>
    </el-form>

    <div class="table-panel">
      <el-table v-loading="loading" :data="users" stripe>
        <el-table-column label="用户" min-width="180">
          <template #default="{ row }">
            <b>{{ row.nickName }}</b>
            <small class="cell-sub">ID {{ row.userId }}</small>
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="登录账号" min-width="140" />
        <el-table-column label="所属部门" min-width="140">
          <template #default="{ row }">{{ row.deptName || `部门 ${row.deptId}` }}</template>
        </el-table-column>
        <el-table-column label="角色" min-width="160">
          <template #default="{ row }">{{ row.roleNames || '未分配角色' }}</template>
        </el-table-column>
        <el-table-column label="数据权限" min-width="130">
          <template #default="{ row }">
            <el-tag type="primary">{{ scopeText(row.dataScope) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default><el-tag type="success">正常</el-tag></template>
        </el-table-column>
        <template #empty><el-empty description="当前权限范围内没有用户数据" /></template>
      </el-table>
    </div>
    <PagePagination
      :current-page="userPage"
      :page-size="userPageSize"
      :total="userTotal"
      @current-change="changeUserPage"
      @size-change="changeUserPageSize"
    />
  </section>
</template>

<script setup>
import PagePagination from '../../components/PagePagination.vue'

defineProps({
  users: Array,
  userName: String,
  visibleCount: Number,
  userPage: Number,
  userPageSize: Number,
  userTotal: Number,
  loading: Boolean,
  scopeText: Function,
  loadData: Function,
  changeUserPage: Function,
  changeUserPageSize: Function
})

defineEmits(['update:userName'])
</script>