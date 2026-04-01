<template>
  <div class="login-container">
    <h2>登录</h2>
    <form @submit.prevent="handleLogin">
      <div>
        <label>账号：</label>
        <input v-model="username" placeholder="学号/工号" />
      </div>
      <div>
        <label>密码：</label>
        <input v-model="password" type="password" placeholder="密码" />
      </div>
      <button type="submit">登录</button>
      <router-link to="/register">去注册</router-link>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '@/api/auth'

const router = useRouter()
const username = ref('')
const password = ref('')

const handleLogin = async () => {
  try {
    const res = await login({ username: username.value, password: password.value })
    if (res.code === 200) {
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('userRole', res.data.user.role)
      if (res.data.user.role === 1) {
        router.push('/student')
      } else {
        router.push('/admin')
      }
    } else {
      alert(res.message)
    }
  } catch (err) {
    alert('登录失败，请检查网络')
  }
}
</script>

<style scoped>
.login-container {
  max-width: 400px;
  margin: 50px auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 8px;
}
div {
  margin-bottom: 15px;
}
label {
  display: inline-block;
  width: 60px;
}
button {
  margin-right: 10px;
  padding: 6px 12px;
}
</style>