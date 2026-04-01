import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import StudentHome from '../views/StudentHome.vue'
import AdminPanel from '../views/AdminPanel.vue'

const routes = [
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/student', component: StudentHome, meta: { requiresAuth: true, role: 1 } },
  { path: '/admin', component: AdminPanel, meta: { requiresAuth: true, role: 2 } },
  { path: '/', redirect: '/login' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userRole = localStorage.getItem('userRole')
  if (to.meta.requiresAuth) {
    if (!token) {
      next('/login')
    } else if (to.meta.role && userRole != to.meta.role) {
      next('/login')
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router