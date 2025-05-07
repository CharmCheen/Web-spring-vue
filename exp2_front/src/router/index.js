import { createRouter, createWebHistory } from 'vue-router';
import Login from '../pages/Login.vue';
import Register from '../pages/Register.vue';
import ForgotPassword from '../pages/ForgotPassword.vue';
import ChangePassword from '../pages/ChangePassword.vue';
import DeleteAccount from '../pages/DeleteAccount.vue';
import Home from '../pages/Home.vue';
import UserManagement from '../pages/UserManagement.vue';
import FeatureUserManagement from '../pages/FeatureUserManagement.vue';
import FileManagement from '../pages/FileManagement.vue';
import ArticleCreate from '../components/ArticleCreate.vue';
import ArticleManage from '../components/ArticleManage.vue';
import ArticleEdit from '../components/ArticleEdit.vue';
import ArticleFeed from '../components/ArticleFeed.vue';
import SubscriptionList from '../components/SubscriptionList.vue';
import FollowedArticles from '../pages/FollowedArticles.vue';

const routes = [
  {
    path: '/',
    redirect: '/login'  // 默认重定向到登录页
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false }  // 明确标记不需要登录
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { requiresAuth: false }
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: ForgotPassword,
    meta: { requiresAuth: false }
  },
  {
    path: '/home',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: true }
  },
  {
    path: '/feed',
    name: 'ArticleFeed',
    component: ArticleFeed,
    meta: { requiresAuth: true }
  },
  {
    path: '/articles/create',
    name: 'ArticleCreate',
    component: ArticleCreate,
    meta: { requiresAuth: true }
  },
  {
    path: '/articles/manage',
    name: 'ArticleManage',
    component: ArticleManage,
    meta: { requiresAuth: true }
  },
  {
    path: '/articles/edit/:id',
    name: 'ArticleEdit',
    component: ArticleEdit,
    meta: { requiresAuth: true }
  },
  {
    path: '/subscriptions',
    name: 'SubscriptionList',
    component: SubscriptionList,
    meta: { requiresAuth: true }
  },
  {
    path: '/users',
    name: 'UserManagement',
    component: UserManagement,
    meta: { requiresAuth: true }
  },
  {
    path: '/feature/users',
    name: 'FeatureUserManagement',
    component: FeatureUserManagement,
    meta: { requiresAuth: true }
  },
  {
    path: '/feature/files',
    name: 'FileManagement',
    component: FileManagement,
    meta: { requiresAuth: true }
  },
  {
    path: '/followed-articles',
    name: 'FollowedArticles',
    component: FollowedArticles,
    meta: { requiresAuth: false }
  },
  {
    path: '/article-stats',
    name: 'ArticleStats',
    component: () => import('@/components/ArticleStats.vue'),
    meta: {
      requiresAuth: true,
      title: '文章统计'
    }
  },
  {
    path: '/change-password',
    name: 'ChangePassword',
    component: ChangePassword,
    meta: { requiresAuth: true }
  },
  {
    path: '/delete-account',
    name: 'DeleteAccount',
    component: DeleteAccount,
    meta: { requiresAuth: true }
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// 全局导航守卫
router.beforeEach((to, from, next) => {
  const username = localStorage.getItem('username');
  
  // 如果用户未登录且尝试访问需要认证的页面
  if (!username && to.meta.requiresAuth) {
    next('/login');
  } 
  // 如果用户已登录且尝试访问登录页面
  else if (username && to.path === '/login') {
    next('/home');
  } 
  else {
    next();
  }
});

export default router;