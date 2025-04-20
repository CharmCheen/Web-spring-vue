<template>
    <div class="theme-switcher">
      <button @click="toggleTheme">
        Switch to {{ themeState.currentTheme === 'light' ? 'Dark' : 'Light' }} Theme
      </button>
    </div>
  </template>
  
  <script>
  export default {
    name: 'ThemeSwitcher',
    inject: ['switchTheme', 'themeState'],
    methods: {
      toggleTheme() {
        const newTheme = this.themeState.currentTheme === 'light' ? 'dark' : 'light'
        this.switchTheme(newTheme)
      }
    }
  }
  </script>
  
  <style scoped>
  .theme-switcher {
    position: fixed;
    top: 16px;
    right: 16px;
    z-index: 1000;
  }
  button {
    padding: 8px 12px;
    font-size: 14px;
    border: none;
    border-radius: 8px;
    background-color: var(--primary-color);
    color: var(--bg-color);
    cursor: pointer;
    box-shadow: 0 2px 5px rgba(0,0,0,0.1);
    transition: background-color 0.3s ease;
  }
  button:hover {
    opacity: 0.9;
  }
  </style>
  