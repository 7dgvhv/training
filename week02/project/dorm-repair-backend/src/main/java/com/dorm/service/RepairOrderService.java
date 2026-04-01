package com.dorm.service;

import com.dorm.entity.RepairOrder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface RepairOrderService {
    RepairOrder createOrder(Long userId, String deviceType, String description, MultipartFile image) throws IOException;
    List<RepairOrder> getOrdersByUser(Long userId);
    RepairOrder getOrderById(Long orderId);
    boolean cancelOrder(String orderNo, Long userId);
    List<RepairOrder> getAllOrders(String status);
    boolean updateOrderStatus(Long orderId, String newStatus);
    boolean deleteOrder(Long orderId);
    RepairOrder getOrderByOrderNo(String orderNo);
}