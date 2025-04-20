<template>
    <div class="page-layout">
      <SidebarMenu />
      <div class="page-content">
        <!-- Header Section -->
        <div class="header">
          <h2 class="title">文件管理</h2>
          <div class="header-actions">
            <el-button type="primary" size="medium" icon="el-icon-upload" @click="dialogVisible = true">
              上传文件
            </el-button>
            <el-input
              placeholder="搜索文件..."
              prefix-icon="el-icon-search"
              v-model="searchQuery"
              clearable
              style="width: 240px; margin-left: 12px"
            ></el-input>
          </div>
        </div>
        <!-- Table Section -->
        <div class="card table-wrapper">
          <el-table
            :data="filteredData"
            border
            style="width: 100%"
            class="file-table"
            :header-cell-style="{background: 'var(--el-color-primary-light-9)', color: 'var(--el-color-primary)'}"
            stripe
          >
            <el-table-column prop="id" label="文件ID" width="100" align="center"></el-table-column>
            <el-table-column prop="name" label="文件名" width="200"></el-table-column>
            <el-table-column prop="size" label="大小" width="120"></el-table-column>
            <el-table-column prop="type" label="类型" width="120"></el-table-column>
            <el-table-column prop="date" label="上传日期" width="180"></el-table-column>
            <el-table-column fixed="right" label="操作" width="180">
              <template #default="scope">
                <el-button @click="handleDownload(scope.row)" type="primary" size="small" icon="el-icon-download" plain class="action-btn">下载</el-button>
                <el-button @click="handleDelete(scope.row)" type="danger" size="small" icon="el-icon-delete" plain class="action-btn">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <!-- 上传对话框 -->
        <el-dialog title="上传文件/目录" v-model="dialogVisible" width="420px">
          <div class="upload-dialog-content">
            <el-upload
              class="upload-drag"
              drag
              action="#"
              :show-file-list="false"
              :before-upload="handleBeforeUpload"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
              multiple
            >
              <i class="el-icon-upload" style="font-size: 48px; color: #409EFF;"></i>
              <div class="el-upload__text">将文件拖到此处，或点击下方按钮选择</div>
            </el-upload>
            <el-upload
              class="upload-dir-btn"
              action="#"
              :show-file-list="false"
              :before-upload="handleBeforeUpload"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
              webkitdirectory
              directory
              style="margin-top: 16px"
            >
              <el-button type="success" size="medium" icon="el-icon-folder-opened">
                选择目录上传
              </el-button>
            </el-upload>
          </div>
          <template #footer>
            <el-button @click="dialogVisible = false">关闭</el-button>
          </template>
        </el-dialog>
      </div>
    </div>
  </template>
  
  <script>
  import SidebarMenu from '../components/SidebarMenu.vue'
  export default {
    name: 'FileManagement',
    components: { SidebarMenu },
    data() {
      return {
        dialogVisible: false,
        searchQuery: '',
        fileData: [
          { id: 1, name: '文件1.pdf', size: '1.2MB', type: 'PDF', date: '2025-04-10' },
          { id: 2, name: '文件2.docx', size: '500KB', type: 'Word', date: '2025-04-11' },
          { id: 3, name: '文件3.jpg', size: '2.1MB', type: '图片', date: '2025-04-12' },
          { id: 4, name: '文件4.xlsx', size: '800KB', type: 'Excel', date: '2025-04-13' }
        ]
      };
    },
    computed: {
      filteredData() {
        if (!this.searchQuery) return this.fileData;
        return this.fileData.filter(item =>
          item.name.includes(this.searchQuery) ||
          item.type.includes(this.searchQuery)
        );
      }
    },
    methods: {
      handleDownload(file) {
        this.$message.success(`开始下载文件：${file.name}`);
      },
      handleDelete(file) {
        this.$confirm(`确定删除文件：${file.name} 吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.fileData = this.fileData.filter(item => item.id !== file.id);
          this.$message.success('文件已删除');
        }).catch(() => {
          this.$message.info('取消删除');
        });
      },
      handleBeforeUpload(file) {
        // 这里只做演示，实际应上传到后端
        const ext = file.name.split('.').pop();
        const typeMap = { pdf: 'PDF', docx: 'Word', xlsx: 'Excel', jpg: '图片', png: '图片' };
        this.fileData.push({
          id: this.fileData.length + 1,
          name: file.name,
          size: (file.size / 1024 / 1024).toFixed(2) + 'MB',
          type: typeMap[ext] || ext,
          date: new Date().toISOString().slice(0, 10)
        });
        this.$message.success(`上传成功：${file.name}`);
        // 阻止自动上传
        return false;
      },
      handleUploadSuccess() {
        // 仅演示用，实际开发可移除
      },
      handleUploadError() {
        this.$message.error('上传失败');
      }
    }
  };
  </script>
  
  <style scoped>
  .page-layout {
    display: flex;
    min-height: 100vh;
    background: var(--bg-color, #f6f8fa);
  }
  .page-content {
    flex: 1;
    padding: 24px;
    min-width: 0;
  }
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    flex-wrap: wrap;
    gap: 16px;
  }
  .title {
    font-size: 20px;
    font-weight: 600;
    color: var(--el-text-color-primary, #222);
    margin: 0;
  }
  .header-actions {
    display: flex;
    align-items: center;
  }
  .upload-btn,
  .upload-dir-btn {
    display: inline-block;
  }
  .upload-dialog-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 12px 0 0 0;
  }
  .upload-drag {
    width: 320px;
    border: 2px dashed #409EFF;
    border-radius: 8px;
    background: #f8fbff;
    margin-bottom: 16px;
    padding: 32px 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    transition: border-color 0.3s;
  }
  .upload-drag:hover {
    border-color: #66b1ff;
  }
  .card {
    background-color: var(--el-bg-color-overlay, #fff);
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.08);
    padding: 16px;
    margin-bottom: 20px;
  }
  .table-wrapper {
    overflow: hidden;
  }
  .file-table {
    border-radius: 8px;
    overflow: hidden;
  }
  .file-table ::v-deep .el-table__header-wrapper {
    border-radius: 8px 8px 0 0;
  }
  .file-table ::v-deep .el-table__body-wrapper {
    border-radius: 0 0 8px 8px;
  }
  .action-btn {
    margin-right: 8px;
  }
  @media (max-width: 768px) {
    .header {
      flex-direction: column;
      align-items: flex-start;
    }
    .header-actions {
      width: 100%;
      margin-top: 12px;
    }
    .header-actions .el-input {
      width: 100% !important;
      margin-left: 0 !important;
      margin-top: 8px;
    }
    .upload-dialog-content, .upload-drag {
      width: 100% !important;
      min-width: 0 !important;
    }
  }
  </style>