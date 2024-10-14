<template>
  <div class="data-table">
    <table>
      <thead>
        <tr>
          <th>ID</th>
          <th>序列名</th>
          <th>序列</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="sequence in sequences" :key="sequence.id">
          <td>{{ sequence.id }}</td>
          <td>{{ sequence.name }}</td>
          <td>{{ sequence.sequence }}</td>
        </tr>
      </tbody>
    </table>
    <button @click="$emit('export-data')">导出数据</button>
  </div>
</template>

<script>
export default {
  name: "DataTable",
  props: {
    sequences: Array,
  },
};
</script>

<style scoped>
.data-table {
  margin-bottom: 20px;
}
table {
  width: 100%;
  border-collapse: collapse;
}
th,
td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}
th {
  background-color: #f2f2f2;
}
</style>
