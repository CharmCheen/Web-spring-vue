module.exports = {
  devServer: {
    proxy: {
      '/api': {
        target: 'http://localhost:8081', // Spring Boot 后端端口
        changeOrigin: true,
        // 删除 pathRewrite，保留 '/api' 前缀
      }
    }
  },
  outputDir: '../exp2_back/src/main/resources/static', // 可选：打包部署时直接放进后端
}
