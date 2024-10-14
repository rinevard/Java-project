<template>
  <div class="search-bar">
    <input
      v-model="searchQuery"
      @input="debounceSearch"
      placeholder="搜索序列..."
    />
    <select v-model="selectedCategory" @change="debounceSearch">
      <option value="">全部类别</option>
      <option v-for="category in categories" :key="category" :value="category">
        {{ category }}
      </option>
    </select>
  </div>
</template>

<script>
export default {
  name: "SearchBar",
  data() {
    return {
      searchQuery: "",
      selectedCategory: "", // 新增的搜索类别
      debounceTimeout: null,
      categories: [
        "ID",
        "Index",
        "Proteins",
        "Accessions",
        "Sequences",
        "Annotations",
        "Interpros",
        "Orgs",
      ],
    };
  },
  methods: {
    debounceSearch() {
      clearTimeout(this.debounceTimeout);
      this.debounceTimeout = setTimeout(() => {
        this.$emit("search", {
          query: this.searchQuery,
          category: this.selectedCategory,
        });
      }, 300);
    },
  },
};
</script>

<style scoped>
.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

input {
  flex: 1;
  padding: 8px;
  font-size: 16px;
}

select {
  padding: 8px;
  font-size: 16px;
}
</style>
