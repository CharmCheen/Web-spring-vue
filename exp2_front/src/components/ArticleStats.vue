<template>
  <div class="stats-container">
    <!-- 顶部标题区域 -->
    <div class="stats-header">
      <h1>文章数据统计</h1>
      <p class="subtitle">全平台发文数据</p>
    </div>

    <!-- 统计卡片网格 -->
    <div class="stats-grid">
      <!-- 总文章数统计卡片 -->
      <div class="stats-card total-articles">
        <div class="card-content">
          <h3>总文章数</h3>
          <div class="number">{{ totalArticles }}</div>
          <div class="trend">
            <span class="trend-icon">↑</span>
            <span>较上月增长 {{ articleGrowth }}%</span>
          </div>
        </div>
      </div>

      <!-- 活跃作者统计卡片 -->
      <div class="stats-card active-authors">
        <div class="card-content">
          <h3>发文统计</h3>
          <div class="chart-container">
            <canvas ref="authorChart"></canvas>
          </div>
        </div>
      </div>

      <!-- 月度趋势图 -->
      <div class="stats-card trends">
        <div class="card-content">
          <h3>发文趋势</h3>
          <div class="chart-container">
            <canvas ref="trendChart"></canvas>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加加载状态样式 -->
    <div v-if="loading" class="loading-overlay">
      <div class="loading-spinner"></div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import Chart from 'chart.js/auto'
import axios from 'axios'
import { ElMessage } from 'element-plus'

export default {
  name: 'ArticleStats',
  setup() {
    const totalArticles = ref(0)
    const articleGrowth = ref(0)
    const topAuthors = ref([])
    const authorChart = ref(null)
    const trendChart = ref(null)
    const loading = ref(true)

    // 获取统计数据
    const fetchStats = async () => {
      try {
        const response = await axios.get('/api/articles/stats')
        const data = response.data
        
        totalArticles.value = data.totalArticles || 0
        articleGrowth.value = data.growthRate || 0
        topAuthors.value = data.activeAuthors || []

        // 初始化作者排名图表
        const authorCtx = authorChart.value.getContext('2d')
        new Chart(authorCtx, {
          type: 'bar',
          data: {
            labels: topAuthors.value.map(author => author.name),
            datasets: [{
              label: '发文数量',
              data: topAuthors.value.map(author => author.articles),
              backgroundColor: '#007AFF',
              borderRadius: 5
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
              legend: {
                display: false
              }
            },
            scales: {
              y: {
                beginAtZero: true,
                ticks: {
                  stepSize: 1,
                  callback: function(value) {
                    return Math.floor(value);
                  }
                }
              }
            }
          }
        })
      } catch (error) {
        ElMessage.error('获取统计数据失败')
        console.error(error)
      }
    }

    // 初始化趋势图表
    const initTrendChart = async () => {
      try {
        const response = await axios.get('/api/articles/stats/trend')
        const data = response.data
        
        const trendCtx = trendChart.value.getContext('2d')
        new Chart(trendCtx, {
          type: 'line',
          data: {
            labels: data.labels || [],
            datasets: [{
              label: '文章数量',
              data: data.data || [],
              borderColor: '#007AFF',
              tension: 0.4,
              fill: true,
              backgroundColor: 'rgba(0, 122, 255, 0.1)'
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
              legend: {
                display: false
              },
              tooltip: {
                callbacks: {
                  label: function(context) {
                    return `文章数: ${context.raw || 0}`;
                  }
                }
              }
            },
            scales: {
              y: {
                beginAtZero: true,
                ticks: {
                  stepSize: 1,
                  callback: function(value) {
                    return Math.floor(value);
                  }
                }
              }
            }
          }
        })
      } catch (error) {
        ElMessage.error('获取趋势数据失败')
        console.error(error)
      }
    }

    onMounted(async () => {
      try {
        loading.value = true
        await fetchStats()
        await initTrendChart()
      } catch (error) {
        console.error(error)
      } finally {
        loading.value = false
      }
    })

    return {
      totalArticles,
      articleGrowth,
      topAuthors,
      authorChart,
      trendChart,
      loading
    }
  }
}
</script>

<style scoped>
.stats-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.stats-header {
  text-align: center;
  margin-bottom: 60px;
}

.stats-header h1 {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 16px;
  background: linear-gradient(135deg, #007AFF, #5856D6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.subtitle {
  font-size: 20px;
  color: #666;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 30px;
  margin-bottom: 60px;
}

.stats-card {
  background: #fff;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s ease;
}

.stats-card:hover {
  transform: translateY(-5px);
}

.card-content h3 {
  font-size: 20px;
  color: #333;
  margin-bottom: 20px;
}

.number {
  font-size: 48px;
  font-weight: 700;
  color: #007AFF;
  margin-bottom: 10px;
}

.trend {
  display: flex;
  align-items: center;
  color: #34C759;
  font-size: 16px;
}

.trend-icon {
  margin-right: 8px;
}

.chart-container {
  height: 300px;
  position: relative;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }

  .stats-header h1 {
    font-size: 36px;
  }

  .subtitle {
    font-size: 18px;
  }
}

/* 添加加载状态样式 */
.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 100;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid var(--el-color-primary-light-8);
  border-radius: 50%;
  border-top-color: var(--el-color-primary);
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style> 