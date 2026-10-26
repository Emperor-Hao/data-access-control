<template>
  <section class="page-body">
    <div class="page-intro">
      <div>
        <span class="eyebrow">FILTERED RESOURCE</span>
        <h2>组织架构</h2>
        <p>完整部门树由后端一次查询并在内存中组装。</p>
      </div>
    </div>

    <div class="department-panel">
      <el-table :data="flatDepartments" row-key="deptId" stripe>
        <el-table-column label="部门名称" min-width="220">
          <template #default="{ row }">
            <span :style="{ paddingLeft: `${row.depth * 32}px` }">
              <strong>{{ row.deptName }}</strong>
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="deptId" label="部门 ID" width="140" />
        <el-table-column prop="ancestors" label="祖级路径" min-width="220" />
        <el-table-column label="状态" width="100">
          <template #default><el-tag type="success">正常</el-tag></template>
        </el-table-column>
        <template #empty><el-empty description="暂无部门数据" /></template>
      </el-table>
    </div>
  </section>
</template>

<script setup>
defineProps({
  flatDepartments: Array
})
</script>