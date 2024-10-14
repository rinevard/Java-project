<template>
  <div id="app">
    <h1>蛋白质序列数据管理系统</h1>

    <!-- 文件上传组件 -->
    <file-upload @file-uploaded="refreshData" />
    <!-- 搜索栏组件 -->
    <search-bar @search="performSearch" />
    <!-- 加载进度条 -->
    <div class="loading-container" :class="{ hidden: !showProgressBar }">
      <div class="loading-text">{{ loadingText }}</div>
      <div class="progress-container">
        <div
          class="progress-bar"
          :style="{ width: progressPercent + '%' }"
        ></div>
      </div>
    </div>
    <!-- 数据表格组件 -->
    <data-table
      :sequences="sequences"
      :loading="loading"
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
      sequences: [], // 存储蛋白质序列数据的数组
      searchKey: 0, // 每次执行搜索或刷新数据时, 修改其值, 并重新渲染DataTable
      loading: false,
      showProgressBar: false,
      progressPercent: 0,
      progressInterval: null, // 更新进度条的定时器
      loadingText: "",
      currentSearchQuery: "",
    };
  },
  methods: {
    async refreshData() {
      this.currentSearchQuery = "";
      this.startLoading();
      try {
        const response = await axios.get("/api/sequences");
        this.sequences = response.data;
        this.resetPageNum();
      } catch (error) {
        console.error("Error fetching data:", error);
      } finally {
        this.stopLoading();
      }
    },
    async performSearch(query) {
      this.startLoading();
      this.currentSearchQuery = query;
      try {
        const response = await axios.get(
          `/api/sequences/search?query=${encodeURIComponent(query)}`
        );
        this.sequences = response.data;
        this.resetPageNum();
      } catch (error) {
        console.error("Error searching data:", error);
      } finally {
        this.stopLoading();
      }
    },
    resetPageNum() {
      this.searchKey += 1;
      if (this.$refs.dataTable) {
        this.$refs.dataTable.resetPage();
      }
    },
    async exportData({ type, ids, format }) {
      try {
        let endpoint;
        let filename;

        if (type === "current") {
          // 导出当前页数据
          if (format === "txt") {
            endpoint = "/api/sequences/export-txt";
            filename = "sequences_current.txt";
          } else {
            endpoint = "/api/sequences/export";
            filename = "sequences_current.tsv";
          }
          const response = await axios.post(endpoint, ids, {
            responseType: "blob",
          });
          this.downloadFile(response.data, filename);
        } else if (type === "all") {
          if (format === "txt") {
            endpoint = "/api/sequences/export-all-txt";
            filename = "sequences_all.txt";
          } else {
            endpoint = "/api/sequences/export-all";
            filename = "sequences_all.tsv";
          }
          const response = await axios.post(
            endpoint,
            { query: this.currentSearchQuery },
            {
              responseType: "blob",
            }
          );
          this.downloadFile(response.data, filename);
        }
      } catch (error) {
        console.error("Error exporting data:", error);
        alert("导出数据失败");
      }
    },
    downloadFile(data, filename) {
      const url = window.URL.createObjectURL(new Blob([data]));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", filename);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },
    startLoading() {
      this.loading = true;
      this.showProgressBar = true;
      this.progressPercent = 0;
      this.loadingText = "数据库加载中";
      if (this.progressInterval) clearInterval(this.progressInterval);
      this.progressInterval = setInterval(() => {
        this.progressPercent += Math.random() * 5;
        if (this.progressPercent > 99) {
          this.progressPercent = 99;
        }
      }, 200);
    },
    stopLoading() {
      this.loading = false;
      if (this.progressInterval) clearInterval(this.progressInterval);
      this.progressPercent = 100;
      this.loadingText = "加载完毕";
      setTimeout(() => {
        this.showProgressBar = false;
        this.progressPercent = 0;
        this.loadingText = "";
      }, 1000);
    },
  },
  // 组件挂载后刷新数据
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

/* 加载容器样式 */
.loading-container {
  margin-bottom: 20px;
  opacity: 1;
  transition: opacity 0.5s ease, height 0.5s ease;
}

.loading-container.hidden {
  opacity: 0;
  height: 0;
  overflow: hidden;
}

.progress-container {
  width: 100%;
  height: 4px;
  background-color: #f3f3f3;
  overflow: hidden;
  position: relative;
  border-radius: 2px;
}

.progress-bar {
  height: 100%;
  background-color: #4caf50;
  width: 0%;
  transition: width 0.2s ease;
}

.loading-text {
  text-align: center;
  margin-top: 10px;
  font-size: 14px;
  color: #666;
}
</style>
