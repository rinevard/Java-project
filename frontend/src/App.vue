<template>
  <div id="app">
    <h1>蛋白质序列数据管理系统</h1>
    <file-upload @file-uploaded="refreshData" />
    <search-bar @search="performSearch" />
    <data-table
      :sequences="sequences"
      @export-data="exportData"
      :key="searchKey"
      ref="dataTable"
    />
  </div>
</template>

<script>
import FileUpload from "./components/FileUpload.vue";
import DataTable from "./components/DataTable.vue";
import SearchBar from "./components/SearchBar.vue";
import axios from "axios";

export default {
  name: "App",
  components: {
    FileUpload,
    DataTable,
    SearchBar,
  },
  data() {
    return {
      sequences: [],
      searchKey: 0,
    };
  },
  methods: {
    async refreshData() {
      try {
        const response = await axios.get("/api/sequences");
        this.sequences = response.data;
        this.resetPageNum();
      } catch (error) {
        console.error("Error fetching data:", error);
      }
    },
    async performSearch(query) {
      try {
        const response = await axios.get(
          `/api/sequences/search?query=${query}`
        );
        this.sequences = response.data;
        this.resetPageNum();
      } catch (error) {
        console.error("Error searching data:", error);
      }
    },
    resetPageNum() {
      this.searchKey += 1;
      if (this.$refs.dataTable) {
        this.$refs.dataTable.resetPage();
      }
    },
    async exportData({ ids, format }) {
      try {
        let endpoint;
        let filename;
        if (format === "txt") {
          endpoint = "/api/sequences/export-txt";
          filename = "sequences.txt";
        } else {
          // 默认导出为 TSV
          endpoint = "/api/sequences/export";
          filename = "sequences.tsv";
        }
        const response = await axios.post(endpoint, ids, {
          responseType: "blob",
        });
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement("a");
        link.href = url;
        link.setAttribute("download", filename);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link); // 清理 DOM
      } catch (error) {
        console.error("Error exporting data:", error);
        alert("导出数据失败");
      }
    },
  },
  mounted() {
    this.refreshData();
  },
};
</script>

<style>
#app {
  font-family: Arial, sans-serif;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.search-bar {
  margin-bottom: 20px;
}
</style>
