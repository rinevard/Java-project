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
        <tr v-for="sequence in paginatedSequences" :key="sequence.id">
          <td>{{ sequence.id }}</td>
          <td>{{ sequence.name }}</td>
          <td>{{ sequence.sequence }}</td>
        </tr>
      </tbody>
    </table>
    <div class="pageNum">
      <button @click="changePage(1)" :disabled="currentPage === 1">首页</button>
      <button
        @click="changePage(currentPage - 1)"
        :disabled="currentPage === 1"
      >
        上一页
      </button>
      <template v-for="(page, index) in displayedPages">
        <span
          v-if="page === '...'"
          :key="'ellipsis' + index"
          class="ellipsis"
          >{{ page }}</span
        >
        <button
          v-else
          :key="'page' + page"
          @click="changePage(page)"
          :class="{ active: currentPage === page }"
        >
          {{ page }}
        </button>
      </template>
      <button
        @click="changePage(currentPage + 1)"
        :disabled="currentPage === totalPages"
      >
        下一页
      </button>
      <button
        @click="changePage(totalPages)"
        :disabled="currentPage === totalPages"
      >
        末页
      </button>
    </div>
    <button @click="exportData">导出数据</button>
  </div>
</template>

<script>
export default {
  name: "DataTable",
  props: {
    sequences: Array,
  },
  data() {
    return {
      currentPage: 1,
      itemsPerPage: 12,
    };
  },
  computed: {
    totalPages() {
      return Math.ceil(this.sequences.length / this.itemsPerPage);
    },
    paginatedSequences() {
      const start = (this.currentPage - 1) * this.itemsPerPage;
      const end = start + this.itemsPerPage;
      return this.sequences.slice(start, end);
    },
    displayedPages() {
      const totalDisplayed = 7;
      const pages = [];
      if (this.totalPages <= totalDisplayed) {
        for (let i = 1; i <= this.totalPages; i++) {
          pages.push(i);
        }
      } else {
        pages.push(1);
        if (this.currentPage > 3) {
          pages.push("...");
        }
        for (
          let i = Math.max(2, this.currentPage - 1);
          i <= Math.min(this.totalPages - 1, this.currentPage + 1);
          i++
        ) {
          pages.push(i);
        }
        if (this.currentPage < this.totalPages - 2) {
          pages.push("...");
        }
        pages.push(this.totalPages);
      }
      return pages;
    },
  },
  methods: {
    changePage(page) {
      this.currentPage = page;
    },
    exportData() {
      const ids = this.sequences.map((seq) => seq.id);
      this.$emit("export-data", ids);
    },
    resetPage() {
      this.currentPage = 1;
    },
  },
  watch: {
    sequences() {
      this.resetPage();
    },
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
.pageNum {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
}
.pageNum button {
  margin: 0 5px;
  padding: 5px 10px;
  background-color: #f2f2f2;
  border: 1px solid #ddd;
  cursor: pointer;
}
.pageNum button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
.pageNum button.active {
  background-color: #4caf50;
  color: white;
}
.ellipsis {
  margin: 0 5px;
}
</style>
