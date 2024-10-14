<template>
  <div id="app">
    <h1>蛋白质序列数据管理系统</h1>
    <file-upload @file-uploaded="refreshData" />
    <data-table :sequences="sequences" @export-data="exportData" />
    <search-bar @search="performSearch" />
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
    };
  },
  methods: {
    async refreshData() {
      try {
        const response = await axios.get("/api/sequences");
        this.sequences = response.data;
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
      } catch (error) {
        console.error("Error searching data:", error);
      }
    },
    async exportData() {
      try {
        const response = await axios.get("/api/sequences/export", {
          responseType: "blob",
        });
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement("a");
        link.href = url;
        link.setAttribute("download", "sequences.txt");
        document.body.appendChild(link);
        link.click();
      } catch (error) {
        console.error("Error exporting data:", error);
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
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}
</style>
