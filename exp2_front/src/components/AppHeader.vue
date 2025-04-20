<template>
  <div class="topbar">
    <div class="topbar-left">
      <span class="logo-text">🌈 欢迎使用</span>
    </div>
    <div class="topbar-right">
      <slot></slot>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AppHeader'  // ✅ 改为多词命名
}
</script>

<style scoped>
.topbar {
  background-color: var(--topbar-bg);
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  height: 56px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
  transition: background-color 0.3s ease;
}

.logo-text {
  font-size: 18px;
  font-weight: bold;
}
</style>
