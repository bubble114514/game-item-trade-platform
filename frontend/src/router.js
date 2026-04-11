import { createRouter, createWebHistory } from 'vue-router'

// Direct import instead of lazy loading to avoid dynamic import issues
import Home from './views/Home.vue'
import Login from './views/Login.vue'
import Register from './views/Register.vue'
import Orders from './views/Orders.vue'
import Category from './views/Category.vue'
import ItemDetails from './views/ItemDetails.vue'
import Listings from './views/Listings.vue'

const routes = [
  { path: '/', component: Home, meta: { requiresAuth: true } },
  { path: '/item/:id', name: 'ItemDetails', component: ItemDetails, meta: { requiresAuth: true } },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/orders', component: Orders, meta: { requiresAuth: true } },
  { path: '/listings', component: Listings, meta: { requiresAuth: true } },
  // Route for specific categories like /category/equipment
  { path: '/category/:type', component: Category, props: true },
  // Redirect for the base /category path to a default view or the main page
  { path: '/category', redirect: '/' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Enhanced navigation guard
router.beforeEach((to, from, next) => {
  const isLoggedIn = !!localStorage.getItem('token')

  if (to.meta.requiresAuth && !isLoggedIn) {
    // Redirect to login page if trying to access a protected route without being logged in
    next({ path: '/login' })
  } else if ((to.path === '/login' || to.path === '/register') && isLoggedIn) {
    // If user is logged in, redirect from login/register to home page
    next({ path: '/' })
  }
  else {
    next()
  }
})

export default router

