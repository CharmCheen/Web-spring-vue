<template>
  <div class="page-layout">
    <SidebarMenu />
    <div class="page-content">
      <!-- Header Section -->
      <div class="header">
        <h2 class="title">用户管理</h2>
        <div class="header-actions">
          <el-button type="primary" size="medium" icon="el-icon-plus" @click="dialogVisible = true">新增用户</el-button>
          <el-input
            placeholder="搜索用户..."
            prefix-icon="el-icon-search"
            v-model="searchQuery"
            clearable
            style="width: 240px; margin-left: 12px"
          ></el-input>
        </div>
      </div>
      <!-- Table Section -->
      <div class="card table-wrapper">
        <el-table :data="pagedUsers" border style="width: 100%">
          <el-table-column prop="serial" label="序号" width="80" align="center"></el-table-column>
          <el-table-column prop="name" label="姓名" width="120"></el-table-column>
          <el-table-column prop="date" label="日期" width="140"></el-table-column>
          <el-table-column label="省份" width="120">
            <template #default="scope">
              {{ getAreaLabel(scope.row.province) }}
            </template>
          </el-table-column>
          <el-table-column label="城市" width="120">
            <template #default="scope">
              {{ getAreaLabel(scope.row.city) }}
            </template>
          </el-table-column>
          <el-table-column label="区县" width="120">
            <template #default="scope">
              {{ getAreaLabel(scope.row.district) }}
            </template>
          </el-table-column>
          <el-table-column prop="address" label="详细地址"></el-table-column>
          <el-table-column prop="postcode" label="邮编" width="100"></el-table-column>
          <el-table-column fixed="right" label="操作" width="150">
            <template #default="scope">
              <el-button @click="handleEdit" type="text" size="small">编辑</el-button>
              <el-button @click="handleDelete(scope.row)" type="text" size="small">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <!-- Pagination Section -->
      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[5, 10, 20]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="filteredUsers.length"
        ></el-pagination>
      </div>
      <!-- 新增用户对话框 -->
      <el-dialog title="新增用户" v-model="dialogVisible" width="400px">
        <el-form :model="newUser" label-width="70px">
          <el-form-item label="姓名">
            <el-input v-model="newUser.name"></el-input>
          </el-form-item>
          <el-form-item label="日期">
            <el-date-picker v-model="newUser.date" type="date" placeholder="选择日期" style="width: 100%"></el-date-picker>
          </el-form-item>
          <el-form-item label="省市区">
            <el-cascader
              :options="provinceOptions"
              v-model="newUser.provinceCity"
              placeholder="请选择省市区"
              clearable
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="详细地址">
            <el-input v-model="newUser.address"></el-input>
          </el-form-item>
          <el-form-item label="邮编">
            <el-input v-model="newUser.postcode"></el-input>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="addUser">确定</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import SidebarMenu from '../components/SidebarMenu.vue'
// 引入 element-china-area-data
import { regionData, codeToText } from 'element-china-area-data'

export default {
  name: 'FeatureUserManagement',
  components: { SidebarMenu },
  data() {
    return {
      searchQuery: '',
      dialogVisible: false,
      currentPage: 1,
      pageSize: 5,
      provinceOptions: regionData,
      userList: [
        { serial: 1, name: '张三', date: '2025-04-01', province: '11', city: '1101', district: '110101', address: '朝阳区1号', postcode: '100001' },
        { serial: 2, name: '李四', date: '2025-04-02', province: '31', city: '3101', district: '310101', address: '浦东新区2号', postcode: '200002' },
        { serial: 3, name: '王五', date: '2025-04-03', province: '44', city: '4401', district: '440103', address: '天河区3号', postcode: '510003' },
        { serial: 4, name: '赵六', date: '2025-04-04', province: '33', city: '3301', district: '330106', address: '西湖区4号', postcode: '310004' },
        { serial: 5, name: '钱七', date: '2025-04-05', province: '32', city: '3201', district: '320102', address: '鼓楼区5号', postcode: '210005' },
        { serial: 6, name: '孙八', date: '2025-04-06', province: '51', city: '5101', district: '510104', address: '锦江区6号', postcode: '610006' },
        { serial: 7, name: '周九', date: '2025-04-07', province: '42', city: '4201', district: '420106', address: '武昌区7号', postcode: '430007' },
        { serial: 8, name: '吴十', date: '2025-04-08', province: '43', city: '4301', district: '430104', address: '岳麓区8号', postcode: '410008' },
        { serial: 9, name: '郑十一', date: '2025-04-09', province: '35', city: '3501', district: '350102', address: '鼓楼区9号', postcode: '350009' },
        { serial: 10, name: '王十二', date: '2025-04-10', province: '37', city: '3701', district: '370102', address: '历下区10号', postcode: '250010' }
      ],
      newUser: {
        name: '',
        date: '',
        provinceCity: [],
        address: '',
        postcode: ''
      }
    }
  },
  computed: {
    filteredUsers() {
      // 搜索过滤
      return this.userList.filter(user =>
        user.name.includes(this.searchQuery) ||
        this.getAreaLabel(user.province).includes(this.searchQuery) ||
        this.getAreaLabel(user.city).includes(this.searchQuery) ||
        this.getAreaLabel(user.district).includes(this.searchQuery) ||
        user.address.includes(this.searchQuery) ||
        user.postcode.includes(this.searchQuery)
      );
    },
    pagedUsers() {
      // 分页
      const start = (this.currentPage - 1) * this.pageSize;
      return this.filteredUsers.slice(start, start + this.pageSize);
    }
  },
  methods: {
    getAreaLabel(code) {
      return codeToText[code] || code;
    },
    handleEdit() {
      this.$message.info('编辑功能可自行实现');
    },
    handleDelete(row) {
      this.$confirm(`确定删除用户: ${row.name} 吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.userList = this.userList.filter(item => item.serial !== row.serial);
        this.$message.success('删除成功');
      }).catch(() => {});
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.currentPage = 1;
    },
    handleCurrentChange(val) {
      this.currentPage = val;
    },
    addUser() {
      if (
        !this.newUser.name ||
        !this.newUser.date ||
        !this.newUser.provinceCity.length ||
        !this.newUser.address ||
        !this.newUser.postcode
      ) {
        this.$message.warning('请填写完整信息');
        return;
      }
      const maxSerial = this.userList.length ? Math.max(...this.userList.map(u => u.serial)) : 0;
      this.userList.push({
        serial: maxSerial + 1,
        name: this.newUser.name,
        date: typeof this.newUser.date === 'string' ? this.newUser.date : this.newUser.date.toLocaleDateString(),
        province: this.newUser.provinceCity[0],
        city: this.newUser.provinceCity[1],
        district: this.newUser.provinceCity[2],
        address: this.newUser.address,
        postcode: this.newUser.postcode
      });
      this.dialogVisible = false;
      this.$message.success('新增用户成功');
      // 重置表单
      this.newUser = { name: '', date: '', provinceCity: [], address: '', postcode: '' };
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
.pagination-container {
  margin-top: 20px;
  text-align: center;
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
}
</style>