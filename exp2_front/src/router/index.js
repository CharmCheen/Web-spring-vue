import { createRouter, createWebHistory } from 'vue-router';
import Login from '../pages/Login.vue';
import Register from '../pages/Register.vue';
import ForgotPassword from '../pages/ForgotPassword.vue';
import Home from '../pages/Home.vue';
import UserManagement from '../pages/UserManagement.vue';
import FeatureUserManagement from '../pages/FeatureUserManagement.vue';
import FileManagement from '../pages/FileManagement.vue';

const routes = [
  {
    path: '/',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: ForgotPassword
  },
  {
    path: '/home',
    name: 'Home',
    component: Home
  },
  {
    path: '/users',
    name: 'UserManagement',
    component: UserManagement
  },
  {
    path: '/feature/users',
    name: 'FeatureUserManagement',
    component: FeatureUserManagement
  },
  {
    path: '/feature/files',
    name: 'FileManagement',
    component: FileManagement
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;