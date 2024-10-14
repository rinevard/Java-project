<template>
  <div class="file-upload">
    <input
      type="file"
      @change="handleFileUpload"
      multiple
      accept=".tsv,.fa,.fasta,.txt"
    />
    <button @click="uploadFiles">上传文件</button>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "FileUpload",
  data() {
    return {
      files: [],
    };
  },
  methods: {
    handleFileUpload(event) {
      this.files = event.target.files;
    },
    async uploadFiles() {
      if (this.files.length === 0) {
        alert("请选择文件");
        return;
      }

      const formData = new FormData();
      for (let file of this.files) {
        formData.append("files", file);
      }

      try {
        await axios.post("/api/sequences/upload", formData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        });
        alert("文件上传成功");
        this.$emit("file-uploaded");
      } catch (error) {
        console.error("Error uploading files:", error);
        alert("文件上传失败");
      }
    },
  },
};
</script>

<style scoped>
.file-upload {
  margin-bottom: 20px;
}
</style>
