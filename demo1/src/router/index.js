import Vue from 'vue'
import VueRouter from 'vue-router'
import login from '../views/login.vue'
import main from '../views/main.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'login',
    component: login
  },
  {
    path:'/main',
    name:'main',
    component:main
  }
]

const router = new VueRouter({
  routes
})


const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push (location) {
return originalPush.call(this, location).catch(err => err)
}

export default router
