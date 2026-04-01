<template>
  <div class="student-home">
    <h2>学生端</h2>
    <el-tabs v-model="activeTab">
      <!-- 绑定宿舍 -->
      <el-tab-pane label="绑定宿舍" name="bind">
        <el-form :model="dormForm" label-width="80px">
          <el-form-item label="楼栋">
            <el-input v-model="dormForm.building" placeholder="如 A栋" />
          </el-form-item>
          <el-form-item label="房间号">
            <el-input v-model="dormForm.room" placeholder="如 301" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="bindDorm">绑定</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <!-- 创建报修单 -->
      <el-tab-pane label="创建报修单" name="create">
        <el-form :model="orderForm" label-width="80px">
          <el-form-item label="设备类型">
            <el-input v-model="orderForm.deviceType" placeholder="如水龙头、灯管" />
          </el-form-item>
          <el-form-item label="问题描述">
            <el-input type="textarea" v-model="orderForm.description" rows="3" />
          </el-form-item>
          <el-form-item label="图片">
            <input type="file" @change="handleFileChange" accept="image/*" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="createOrder">提交报修</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <!-- 我的报修单 -->
      <el-tab-pane label="我的报修单" name="list">
        <el-table :data="orders" stripe style="width: 100%">
          <el-table-column prop="orderNo" label="单号" />
          <el-table-column prop="deviceType" label="设备" />
          <el-table-column prop="status" label="状态" />
          <el-table-column prop="createdAt" label="创建时间" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button v-if="scope.row.status === '待处理'" size="small" type="danger" @click="cancelOrder(scope.row.orderNo)">取消</el-button>
              <el-button size="small" type="info" @click="viewDetail(scope.row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 修改密码 -->
      <el-tab-pane label="修改密码" name="password">
        <el-form :model="pwdForm" label-width="80px">
          <el-form-item label="旧密码">
            <el-input v-model="pwdForm.oldPwd" type="password" />
          </el-form-item>
          <el-form-item label="新密码">
            <el-input v-model="pwdForm.newPwd" type="password" />
          </el-form-item>
          <el-form-item label="确认新密码">
            <el-input v-model="pwdForm.confirmPwd" type="password" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="changePassword">修改</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <!-- 基本信息 -->
      <el-tab-pane label="基本信息" name="info">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="姓名">{{ userInfo.name }}</el-descriptions-item>
          <el-descriptions-item label="账号">{{ userInfo.username }}</el-descriptions-item>
          <el-descriptions-item label="宿舍">{{ userInfo.dormBuilding }} {{ userInfo.dormRoom }}</el-descriptions-item>
        </el-descriptions>
        <el-button @click="loadUserInfo">刷新</el-button>
      </el-tab-pane>
    </el-tabs>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="报修详情" width="500px">
      <p><strong>单号：</strong>{{ currentOrder.orderNo }}</p>
      <p><strong>设备：</strong>{{ currentOrder.deviceType }}</p>
      <p><strong>描述：</strong>{{ currentOrder.description }}</p>
      <p><strong>状态：</strong>{{ currentOrder.status }}</p>
      <p><strong>创建时间：</strong>{{ currentOrder.createdAt }}</p>
      <img v-if="currentOrder.imageUrl" :src="currentOrder.imageUrl" width="100%" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
// 导入 API 并起别名，避免与本地函数名冲突
import { 
  bindDorm as apiBindDorm,
  createOrder as apiCreateOrder,
  getMyOrders,
  cancelOrder as apiCancelOrder,
  changePassword as apiChangePassword,
  getUserInfo
} from '@/api/student'

const activeTab = ref('bind')
const dormForm = ref({ building: '', room: '' })
const orderForm = ref({ deviceType: '', description: '' })
const imageFile = ref(null)
const orders = ref([])
const pwdForm = ref({ oldPwd: '', newPwd: '', confirmPwd: '' })
const userInfo = ref({})
const detailVisible = ref(false)
const currentOrder = ref({})

// 加载报修单
const loadOrders = async () => {
  const res = await getMyOrders()
  orders.value = res.data
}

// 加载用户信息
const loadUserInfo = async () => {
  const res = await getUserInfo()
  userInfo.value = res.data
}

// 绑定宿舍
const bindDorm = async () => {
  await apiBindDorm(dormForm.value.building, dormForm.value.room)
  ElMessage.success('绑定成功')
  await loadUserInfo()
}

// 创建报修单
const handleFileChange = (e) => {
  imageFile.value = e.target.files[0]
}
const createOrder = async () => {
  const formData = new FormData()
  const orderJson = { deviceType: orderForm.value.deviceType, description: orderForm.value.description }
  //formData.append('order', new Blob([JSON.stringify(orderJson)], { type: 'application/json' }))
  formData.append('device', orderForm.value.deviceType)
  formData.append('description', orderForm.value.description)
  if (imageFile.value) formData.append('image', imageFile.value)
  await apiCreateOrder(formData)
  ElMessage.success('报修单已创建')
  orderForm.value = { deviceType: '', description: '' }
  imageFile.value = null
  await loadOrders()
}


// 取消报修单
const cancelOrder = async (orderNo) => {
  ElMessageBox.confirm('确定取消该报修单吗？', '提示', { type: 'warning' })
    .then(async () => {
      try {
        // 1. 调用接口，传业务单号 orderNo
        await apiCancelOrder(orderNo)
        // 2. 只有接口成功，才弹成功提示
        ElMessage.success('已取消')
        // 3. 刷新列表
        await loadOrders()
      } catch (err) {
        // 3. 捕获后端异常，弹错误提示
        ElMessage.error(err.response?.data?.msg || '取消失败，请重试')
      }
    })
    .catch(() => {
      // 用户点击取消，不做任何操作
    })
}

// 查看详情
const viewDetail = (order) => {
  currentOrder.value = order
  detailVisible.value = true
}

// 修改密码
const changePassword = async () => {
  if (pwdForm.value.newPwd !== pwdForm.value.confirmPwd) {
    ElMessage.error('两次新密码不一致')
    return
  }
  await apiChangePassword(pwdForm.value.oldPwd, pwdForm.value.newPwd)
  ElMessage.success('密码修改成功，请重新登录')
  localStorage.removeItem('token')
  localStorage.removeItem('userRole')
  window.location.href = '/login'
}

onMounted(() => {
  loadOrders()
  loadUserInfo()
})
</script>

<style scoped>
.student-home {
  max-width: 1000px;
  margin: 20px auto;
}
</style>