package com.dorm.service;

import com.dorm.entity.RepairOrder;

import java.util.List;

public interface RepairOrderService {
    /**
     * 创建报修单
     */
    boolean createOrder(Long userId, String deviceType, String description);

    /**
     * 查询学生的所有报修单
     */
    List<RepairOrder> getOrdersByUser(Long userId);

    /**
     * 查询报修单详情
     */
    RepairOrder getOrderById(Long orderId);

    /**
     * 学生取消报修单（仅允许取消“待处理”状态的）
     */
    boolean cancelOrder(Long orderId, Long userId);

    /**
     * 管理员查询所有报修单，可筛选状态
     */
    List<RepairOrder> getAllOrders(String status);

    /**
     * 管理员修改报修单状态
     */
    boolean updateOrderStatus(Long orderId, String newStatus);

    /**
     * 管理员删除报修单
     */
    boolean deleteOrder(Long orderId);
}