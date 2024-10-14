<template>
  <div class="data-table">
    <table>
      <thead>
        <tr>
          <!-- 表头 -->
          <th>ID</th>
          <th>Index</th>
          <th>Proteins</th>
          <th>Accessions</th>
          <th>Sequences</th>
          <th>Annotations</th>
          <th>Interpros</th>
          <th>Orgs</th>
        </tr>
      </thead>
      <tbody>
        <!-- 遍历分页后的序列数据 -->
        <tr v-for="sequence in paginatedSequences" :key="sequence.id">
          <td>{{ sequence.id }}</td>
          <td>{{ sequence.indexNumber }}</td>
          <td>{{ sequence.proteins }}</td>
          <td>{{ sequence.accessions }}</td>

          <!-- Sequences -->
          <td>
            <span v-if="isTruncated('sequence', sequence)">
              {{ getDisplayText("sequence", sequence) }}
              <button @click="toggleExpand(sequence.id, 'sequence')">
                {{ isExpanded("sequence", sequence) ? "折叠" : "展开" }}
              </button>
            </span>
            <span v-else>
              {{ sequence.sequence }}
            </span>
          </td>

          <!-- Annotations -->
          <td>
            <span v-if="isTruncated('annotations', sequence)">
              {{ getDisplayText("annotations", sequence) }}
              <button @click="toggleExpand(sequence.id, 'annotations')">
                {{ isExpanded("annotations", sequence) ? "折叠" : "展开" }}
              </button>
            </span>
            <span v-else>
              {{ formatAnnotations(sequence.annotations) }}
            </span>
          </td>

          <!-- Interpros -->
          <td>
            <span v-if="isTruncated('interpros', sequence)">
              {{ getDisplayText("interpros", sequence) }}
              <button @click="toggleExpand(sequence.id, 'interpros')">
                {{ isExpanded("interpros", sequence) ? "折叠" : "展开" }}
              </button>
            </span>
            <span v-else>
              {{ formatInterpros(sequence.interpros) }}
            </span>
          </td>

          <td>{{ sequence.orgs }}</td>
        </tr>
      </tbody>
    </table>

    <!-- 分页 -->
    <div class="pageNum">
      <button @click="changePage(1)" :disabled="currentPage === 1">首页</button>
      <button
        @click="changePage(currentPage - 1)"
        :disabled="currentPage === 1"
      >
        上一页
      </button>
      <template v-for="(page, index) in displayedPages" :key="'page' + index">
        <span v-if="page === '...'" class="ellipsis">{{ page }}</span>
        <button
          v-else
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

    <!-- 导出按钮组 -->
    <div class="export-buttons">
      <div class="export-group">
        <span>导出本页：</span>
        <button @click="exportData('current', 'tsv')">TSV</button>
        <button @click="exportData('current', 'txt')">TXT</button>
      </div>
      <div class="export-group">
        <span>导出全部：</span>
        <button @click="exportData('all', 'tsv')">TSV</button>
        <button @click="exportData('all', 'txt')">TXT</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "DataTable",
  props: {
    sequences: {
      type: Array,
      required: true,
    },
  },
  data() {
    return {
      currentPage: 1,
      itemsPerPage: 12,
      expandedCells: {}, // 用于存储展开状态的对象
      truncatableFields: {
        sequence: 50,
        annotations: 50,
        interpros: 50,
      },
    };
  },
  computed: {
    totalPages() {
      return Math.ceil(this.sequences.length / this.itemsPerPage);
    },
    // 获取当前页的数据
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
      if (page >= 1 && page <= this.totalPages) {
        this.currentPage = page;
      }
    },
    exportData(type, format) {
      if (type === "current") {
        const ids = this.paginatedSequences.map((seq) => seq.id);
        this.$emit("export-data", { type, ids, format });
      } else if (type === "all") {
        this.$emit("export-data", { type, format });
      }
    },
    resetPage() {
      this.currentPage = 1;
    },
    formatAnnotations(annotations) {
      if (!annotations) return "";
      try {
        const parsed = JSON.parse(annotations.replace(/'/g, '"'));
        return parsed.join(", ");
      } catch (e) {
        return annotations;
      }
    },
    formatInterpros(interpros) {
      if (!interpros) return "";
      try {
        const parsed = JSON.parse(interpros.replace(/'/g, '"'));
        return parsed.join(", ");
      } catch (e) {
        return interpros;
      }
    },
    isTruncated(field, sequence) {
      const maxLength = this.truncatableFields[field];
      if (!maxLength) return false;
      let text;
      if (field === "sequence") {
        text = sequence.sequence;
      } else if (field === "annotations") {
        text = this.formatAnnotations(sequence.annotations);
      } else if (field === "interpros") {
        text = this.formatInterpros(sequence.interpros);
      } else {
        text = "";
      }
      return text.length > maxLength;
    },
    getDisplayText(field, sequence) {
      const maxLength = this.truncatableFields[field];
      let text;
      if (field === "sequence") {
        text = sequence.sequence;
      } else if (field === "annotations") {
        text = this.formatAnnotations(sequence.annotations);
      } else if (field === "interpros") {
        text = this.formatInterpros(sequence.interpros);
      } else {
        text = "";
      }

      if (this.isExpanded(field, sequence)) {
        return text;
      } else {
        return text.slice(0, maxLength) + "...";
      }
    },
    isExpanded(field, sequence) {
      return (
        this.expandedCells[sequence.id] &&
        this.expandedCells[sequence.id][field]
      );
    },
    toggleExpand(sequenceId, field) {
      if (!this.expandedCells[sequenceId]) {
        this.expandedCells[sequenceId] = {};
      }
      this.expandedCells[sequenceId][field] =
        !this.expandedCells[sequenceId][field];
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
/* 数据表格样式 */
.data-table {
  margin-top: 20px;
}

table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;
  word-wrap: break-word;
}

th,
td {
  border: 1px solid #ccc;
  padding: 8px;
  text-align: left;
}

td button {
  margin-left: 5px;
  padding: 2px 5px;
  font-size: 12px;
  cursor: pointer;
}

/* 分页控件样式 */
.pageNum {
  margin-top: 10px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.pageNum button {
  margin: 0 5px;
  padding: 5px 10px;
  cursor: pointer;
}

.pageNum .active {
  background-color: #007bff;
  color: white;
}

.ellipsis {
  margin: 0 5px;
}

/* 导出按钮样式 */
.export-buttons {
  margin-top: 20px;
  display: flex;
  gap: 20px;
}

.export-group {
  display: flex;
  align-items: center;
}

.export-group span {
  margin-right: 10px;
}

.export-buttons button {
  margin-right: 10px;
  padding: 5px 10px;
}
</style>
