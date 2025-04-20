<template>
    <div>
      <el-table :data="currentPageData" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="username" label="用户名" width="120"></el-table-column>
        <el-table-column prop="email" label="电子邮箱"></el-table-column>
        <el-table-column prop="registerDate" label="注册日期" width="180"></el-table-column>
        <el-table-column fixed="right" label="操作" width="150">
          <template #default="scope">
            <el-button @click="handleEdit(scope.row)" type="text" size="small">编辑</el-button>
            <el-button @click="handleDelete(scope.row)" type="text" size="small">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[5, 10, 20, 50]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="userData.length">
        </el-pagination>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: 'UserTable',
    data() {
      return {
        currentPage: 1,
        pageSize: 10,
        userData: Array.from({ length: 100 }, (_, i) => ({
          id: i + 1,
          username: `用户${i + 1}`,
          email: `user${i + 1}@example.com`,
          registerDate: new Date(Date.now() - Math.floor(Math.random() * 10000000000)).toLocaleDateString()
        }))
      }
    },
    computed: {
      currentPageData() {
        const start = (this.currentPage - 1) * this.pageSize
        const end = start + this.pageSize
        return this.userData.slice(start, end)
      }
    },
    methods: {
      handleEdit(row) {
        this.$message.success(`编辑用户: ${row.username}`)
      },
      handleDelete(row) {
        this.$message.success(`删除用户: ${row.username}`)
      },
      handleSizeChange(val) {
        this.pageSize = val
        this.currentPage = 1
      },
      handleCurrentChange(val) {
        this.currentPage = val
      }
    }
  }
  </script>
  
  <style scoped>
  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }
  </style>