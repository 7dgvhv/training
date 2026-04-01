import request from './request'

export const getOrders = (status) => request.get('/admin/orders', { params: { status } })
export const getOrderDetail = (idOrNo) => request.get(`/admin/order/${idOrNo}`)
export const updateOrderStatus = (orderId, status) => 
  request.put(`/admin/order/${orderId}/status`, { status })
export const deleteOrder = (orderId) => request.delete(`/admin/order/${orderId}`)
export const changePassword = (oldPwd, newPwd) => 
  request.put('/admin/changePassword', null, { params: { oldPwd, newPwd } })
export const getAdminInfo = () => request.get('/admin/info')