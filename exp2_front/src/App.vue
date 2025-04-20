<template>
  <div id="app" :class="themeClass">
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App',
  data() {
    return {
      currentTheme: 'blue' // or 'yellow'
    }
  },
  computed: {
    themeClass() {
      return `theme-${this.currentTheme}`
    }
  },
  provide() {
    return {
      switchTheme: this.switchTheme,
      themeState: this
    }
  },
  methods: {
    switchTheme(themeName) {
      this.currentTheme = themeName
    }
  }
}
</script>

<style>
.theme-blue {
  --primary-color: #007aff;
  --topbar-bg: #0055a4;
  --sidebar-bg: #d6e8fd;
  --bg-color: #f0f6fd;
  --text-color: #1c1c1e;
  --card-bg: #ffffff;
  --border-color: #c4d8ec;
  --glow-color: #007aff88;
}

.theme-yellow {
  --primary-color: #d89614;
  --topbar-bg: #b37a2f;
  --sidebar-bg: #e8d9b1;
  --bg-color: #fcf6e8;
  --text-color: #2f2f1e;
  --card-bg: #ffffff;
  --border-color: #e2d2aa;
  --glow-color: #d8961488;
}

#app {
  font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', sans-serif;
  background-color: var(--bg-color);
  color: var(--text-color);
  min-height: 100vh;
  transition: background-color 0.3s ease, color 0.3s ease;
}
</style>
