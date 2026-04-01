<template>
  <div class="register-container">
    <h2>用户注册</h2>
    <form @submit.prevent="handleRegister">
      <div>
        <label>角色：</label>
        <select v-model="form.role">
          <option :value="1">学生</option>
          <option :value="2">维修人员</option>
        </select>
      </div>
      <div>
        <label>账号：</label>
        <input v-model="form.username" placeholder="学生：3125/3225开头，管理员：0025开头" />
        <span v-if="usernameError" class="error">{{ usernameError }}</span>
      </div>
      <div>
        <label>姓名：</label>
        <input v-model="form.name" placeholder="真实姓名" />
      </div>
      <div>
        <label>密码：</label>
        <input v-model="form.password" type="password" placeholder="密码" />
      </div>
      <div>
        <label>确认密码：</label>
        <input v-model="confirmPassword" type="password" placeholder="再次输入密码" />
        <span v-if="passwordMismatch" class="error">两次密码不一致</span>
      </div>
      <button type="submit">注册</button>
      <router-link to="/login">已有账号？去登录</router-link>
    </form>
  </div>
</template>

<script setup>
import { reactive, ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/auth'

const router = useRouter()
const form = reactive({
  username: '',
  password: '',
  name: '',
  role: 1
})
const confirmPassword = ref('')

// 账号格式校验
const usernameError = computed(() => {
  const username = form.username
  if (!username) return ''
  const studentPattern = /^(3125|3225)\d{6,}$/
  const adminPattern = /^0025\d{4,}$/
  if (form.role === 1 && !studentPattern.test(username)) {
    return '学号格式错误（应为3125或3225开头的10位以上数字）'
  }
  if (form.role === 2 && !adminPattern.test(username)) {
    return '工号格式错误（应为0025开头的8位以上数字）'
  }
  return ''
})

const passwordMismatch = computed(() => {
  return form.password && confirmPassword.value && form.password !== confirmPassword.value
})

const handleRegister = async () => {
  if (usernameError.value) {
    alert(usernameError.value)
    return
  }
  if (passwordMismatch.value) {
    alert('两次密码不一致')
    return
  }
  if (!form.name) {
    alert('请输入姓名')
    return
  }
  try {
    const res = await register(form)
    if (res.code === 200) {
      alert('注册成功！请登录')
      router.push('/login')
    } else {
      alert(res.message || '注册失败')
    }
  } catch (err) {
    alert('注册失败，请检查网络')
  }
}
</script>

<style scoped>
.register-container {
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
  width: 80px;
}
input, select {
  width: 200px;
  padding: 5px;
}
.error {
  color: red;
  font-size: 12px;
  margin-left: 10px;
}
button {
  margin-right: 10px;
  padding: 6px 12px;
}
</style>