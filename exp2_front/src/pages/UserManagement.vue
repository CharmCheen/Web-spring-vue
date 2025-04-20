<template>
  <div class="page-container">
    <Sidebar class="sidebar" />
    <div class="content-wrapper">
      <AppHeader />
      <div class="user-management">
        <h2>用户管理</h2>
        <div class="table-actions">
          <el-button type="primary" @click="handleAddUser">添加用户</el-button>
          <el-input
            placeholder="搜索用户"
            v-model="searchQuery"
            class="search-input"
          >
            <template #append>
              <el-button icon="el-icon-search"></el-button>
            </template>
          </el-input>
        </div>
        
        <UserTable />
      </div>
    </div>
  </div>
</template>

<script>
import Sidebar from '../components/SidebarMenu.vue'
import AppHeader from '../components/AppHeader.vue'
import UserTable from '../components/UserTable.vue'

export default {
  name: 'UserManagement',
  components: {
    Sidebar,
    AppHeader,
    UserTable
  },
  data() {
    return {
      searchQuery: ''
    }
  },
  methods: {
    handleAddUser() {
      this.$message.success('点击了添加用户按钮')
    }
  }
}
</script>

<style scoped>
.user-management {
  padding: 20px;
}

.user-management h2 {
  margin-bottom: 20px;
  color: var(--text-color);
}

.table-actions {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.search-input {
  width: 300px;
}

@media (max-width: 768px) {
  .table-actions {
    flex-direction: column;
    gap: 10px;
  }
  
  .search-input {
    width: 100%;
  }
}
</style>
