<template>
  <div class="admin-panel">
    <h2>管理员端</h2>
    <el-tabs v-model="activeTab">
      <!-- 报修单管理 -->
      <el-tab-pane label="报修单管理" name="orders">
        <div style="margin-bottom: 10px">
          <el-select v-model="filterStatus" placeholder="筛选状态" clearable @change="loadOrders">
            <el-option label="待处理" value="待处理" />
            <el-option label="处理中" value="处理中" />
            <el-option label="已完成" value="已完成" />
            <el-option label="已取消" value="已取消" />
          </el-select>
          <el-button @click="loadOrders" style="margin-left: 10px">查询</el-button>
        </div>
        <el-table :data="orders" stripe style="width: 100%">
          <el-table-column prop="orderNo" label="单号" />
          <el-table-column prop="deviceType" label="设备" />
          <el-table-column prop="status" label="状态" />
          <el-table-column prop="createdAt" label="创建时间" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" type="primary" @click="viewDetail(scope.row)">详情</el-button>
              <el-button size="small" type="danger" @click="handleDeleteOrder(scope.row.orderNo)">删除</el-button>
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
            <el-button type="primary" @click="handleChangePassword">修改</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <!-- 基本信息 -->
      <el-tab-pane label="基本信息" name="info">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="姓名">{{ adminInfo.name }}</el-descriptions-item>
          <el-descriptions-item label="工号">{{ adminInfo.username }}</el-descriptions-item>
        </el-descriptions>
        <el-button @click="loadAdminInfo">刷新</el-button>
      </el-tab-pane>
    </el-tabs>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="报修详情" width="500px" @close="resetDialog">
      <p><strong>单号：</strong>{{ currentOrder.orderNo }}</p>
      <p><strong>设备：</strong>{{ currentOrder.deviceType }}</p>
      <p><strong>描述：</strong>{{ currentOrder.description }}</p>
      <p><strong>状态：</strong>
        <el-select v-model="newStatus" size="small">
          <el-option label="待处理" value="待处理" />
          <el-option label="处理中" value="处理中" />
          <el-option label="已完成" value="已完成" />
          <el-option label="已取消" value="已取消" />
        </el-select>
        <el-button size="small" type="primary" @click="updateStatus">更新状态</el-button>
      </p>
      <p><strong>创建时间：</strong>{{ currentOrder.createdAt }}</p>
      <p><strong>最后更新：</strong>{{ currentOrder.updatedAt }}</p>
      <img v-if="currentOrder.imageUrl" :src="currentOrder.imageUrl" width="100%" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getOrders, 
  getOrderDetail, 
  updateOrderStatus, 
  deleteOrder, 
  changePassword, 
  getAdminInfo 
} from '@/api/admin'

const activeTab = ref('orders')
const filterStatus = ref('')
const orders = ref([])
const pwdForm = ref({ oldPwd: '', newPwd: '', confirmPwd: '' })
const adminInfo = ref({})
const detailVisible = ref(false)
const currentOrder = ref({})
const newStatus = ref('')

// 加载报修单列表
const loadOrders = async () => {
  try {
    const res = await getOrders(filterStatus.value || null)
    if (res.code === 200) {
      orders.value = res.data
    } else {
      ElMessage.error(res.message || '加载失败')
    }
  } catch (err) {
    console.error(err)
    ElMessage.error('加载报修单失败')
  }
}

// 加载管理员信息
const loadAdminInfo = async () => {
  try {
    const res = await getAdminInfo()
    if (res.code === 200) {
      adminInfo.value = res.data
    }
  } catch (err) {
    console.error(err)
    ElMessage.error('获取信息失败')
  }
}

// 查看详情
const viewDetail = async (order) => {
  if (!order || !order.orderNo) {
    ElMessage.error('无效的报修单')
    return
  }
  try {
    const res = await getOrderDetail(order.orderNo)
    if (res.code === 200 && res.data) {
      currentOrder.value = res.data
      newStatus.value = currentOrder.value.status
      detailVisible.value = true
    } else {
      ElMessage.error(res.message || '报修单不存在')
    }
  } catch (err) {
    console.error(err)
    ElMessage.error('获取详情失败')
  }
}

// 更新状态
const updateStatus = async () => {
  if (!currentOrder.value.orderNo) {
    ElMessage.error('订单号缺失')
    return
  }
  try {
    const res = await updateOrderStatus(currentOrder.value.orderNo, newStatus.value)
    if (res.code === 200) {
      ElMessage.success('状态更新成功')
      await loadOrders()
      detailVisible.value = false
    } else {
      ElMessage.error(res.message || '更新失败')
    }
  } catch (err) {
    console.error(err)
    ElMessage.error('更新失败')
  }
}

// 删除
const handleDeleteOrder = async (orderNo) => {
  ElMessageBox.confirm('确定删除该报修单吗？', '提示', { type: 'warning' }).then(async () => {
    try {
      const res = await deleteOrder(orderNo)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        await loadOrders()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    } catch (err) {
      console.error(err)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 修改密码
const handleChangePassword = async () => {
  if (pwdForm.value.newPwd !== pwdForm.value.confirmPwd) {
    ElMessage.error('两次新密码不一致')
    return
  }
  try {
    const res = await changePassword(pwdForm.value.oldPwd, pwdForm.value.newPwd)
    if (res.code === 200) {
      ElMessage.success('密码修改成功，请重新登录')
      localStorage.removeItem('token')
      localStorage.removeItem('userRole')
      window.location.href = '/login'
    } else {
      ElMessage.error(res.message || '修改失败')
    }
  } catch (err) {
    ElMessage.error('修改失败，请检查旧密码')
  }
}

const resetDialog = () => {
  currentOrder.value = {}
  newStatus.value = ''
}

onMounted(() => {
  loadOrders()
  loadAdminInfo()
})
</script>

<style scoped>
.admin-panel {
  max-width: 1000px;
  margin: 20px auto;
}
</style>