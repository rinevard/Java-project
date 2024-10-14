<template>
  <div class="search-bar">
    <input
      v-model="searchQuery"
      @input="debounceSearch"
      placeholder="搜索序列..."
    />
  </div>
</template>

<script>
export default {
  name: "SearchBar",
  data() {
    return {
      searchQuery: "",
      debounceTimeout: null,
    };
  },
  methods: {
    debounceSearch() {
      clearTimeout(this.debounceTimeout);
      this.debounceTimeout = setTimeout(() => {
        this.$emit("search", this.searchQuery);
      }, 300);
    },
  },
};
</script>

<style scoped>
.search-bar {
  margin-bottom: 20px;
}
input {
  width: 100%;
  padding: 8px;
  font-size: 16px;
}
</style>
