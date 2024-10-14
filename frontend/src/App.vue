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
      currentSearchCategory: "", // 新增的当前搜索类别
    };
  },
  methods: {
    async refreshData() {
      this.currentSearchQuery = "";
      this.currentSearchCategory = "";
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
    async performSearch({ query, category }) {
      this.startLoading();
      this.currentSearchQuery = query;
      this.currentSearchCategory = category;
      try {
        let response;
        if (category) {
          response = await axios.get(
            `/api/sequences/search?query=${encodeURIComponent(
              query
            )}&category=${encodeURIComponent(category)}`
          );
        } else {
          // 如果没有选择类别，默认搜索所有支持的类别
          response = await axios.get(
            `/api/sequences/search?query=${encodeURIComponent(query)}`
          );
        }
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
        let exportRequest = {
          type, // "current" 或 "all"
          ids: type === "current" ? ids : null,
          format, // "tsv" 或 "txt"
          query: this.currentSearchQuery,
          category: this.currentSearchCategory,
        };

        const response = await axios.post(
          "/api/sequences/export-data",
          exportRequest,
          {
            responseType: "blob",
          }
        );

        let filename = "";
        if (type === "current") {
          filename =
            format === "txt"
              ? "sequences_current.txt"
              : "sequences_current.tsv";
        } else if (type === "all") {
          filename =
            format === "txt" ? "sequences_all.txt" : "sequences_all.tsv";
        }

        this.downloadFile(response.data, filename);
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
