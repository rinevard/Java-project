<template>
  <div class="file-upload">
    <input
      type="file"
      @change="handleFileUpload"
      multiple
      accept=".tsv,.fa,.fasta,.txt"
    />
    <button
      type="button"
      @click="uploadFiles"
      :disabled="files.length === 0 || uploading"
    >
      上传文件
    </button>

    <div v-if="uploading" class="progress-container">
      <div class="progress-bar" :style="{ width: uploadProgress + '%' }"></div>
      <span>{{ uploadProgress }}%</span>
    </div>

    <div v-if="uploadError" class="error-message">
      上传失败: {{ uploadError }}
    </div>

    <div v-if="uploadSuccess" class="success-message">文件上传成功</div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "FileUpload",
  data() {
    return {
      files: [],
      uploadProgress: 0,
      uploading: false,
      uploadError: null,
      uploadSuccess: false,
    };
  },
  methods: {
    handleFileUpload(event) {
      this.files = event.target.files;
      this.uploadError = null;
      this.uploadSuccess = false;
      this.uploadProgress = 0;
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

      this.uploading = true;
      this.uploadProgress = 0;
      this.uploadError = null;
      this.uploadSuccess = false;

      try {
        await axios.post("/api/sequences/upload", formData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
          onUploadProgress: (progressEvent) => {
            if (progressEvent.lengthComputable) {
              this.uploadProgress = Math.round(
                (progressEvent.loaded * 100) / progressEvent.total
              );
            }
          },
        });
        this.uploading = false;
        this.uploadSuccess = true;
        this.$emit("file-uploaded");
      } catch (error) {
        this.uploading = false;
        this.uploadError =
          error.response && error.response.data
            ? error.response.data
            : "未知错误";
        console.error("Error uploading files:", error);
      }
    },
  },
};
</script>

<style scoped>
.file-upload {
  margin-bottom: 20px;
}

.button {
  margin-top: 10px;
}

.progress-container {
  margin-top: 10px;
  width: 100%;
  background-color: #f3f3f3;
  border-radius: 4px;
  position: relative;
  height: 20px;
}

.progress-bar {
  height: 100%;
  background-color: #4caf50;
  border-radius: 4px 0 0 4px;
  transition: width 0.4s ease;
}

.progress-container span {
  position: absolute;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  font-size: 12px;
}

.error-message {
  color: red;
  margin-top: 10px;
}

.success-message {
  color: green;
  margin-top: 10px;
}
</style>
