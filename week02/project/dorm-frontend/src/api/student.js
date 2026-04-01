import request from './request'

export const bindDorm = (building, room) => 
  request.post('/student/bindDorm', null, { params: { building, room } })

export const createOrder = (formData) => 
  request.post('/student/createOrder', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })

export const getMyOrders = () => request.get('/student/myOrders')
export const cancelOrder = (orderNo) => request.put(`/student/cancelOrder/${orderNo}`)
export const changePassword = (oldPwd, newPwd) => 
  request.put('/student/changePassword', null, { params: { oldPwd, newPwd } })
export const getUserInfo = () => request.get('/student/info')