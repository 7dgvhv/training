package com.dorm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dorm.constant.OrderStatusConst;
import com.dorm.constant.RoleConst;
import com.dorm.entity.RepairOrder;
import com.dorm.entity.User;
import com.dorm.mapper.RepairOrderMapper;
import com.dorm.mapper.UserMapper;
import com.dorm.service.RepairOrderService;
import com.dorm.utils.DbUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

public class RepairOrderServiceImpl implements RepairOrderService {

    // 生成唯一报修单号：时间戳+随机数
    private String generateOrderNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = new Random().nextInt(1000);
        return date + String.format("%03d", random);
    }

    @Override
    public boolean createOrder(Long userId, String deviceType, String description) {
        return DbUtils.execute(session -> {
            // 检查用户是否为有效学生
            UserMapper userMapper = session.getMapper(UserMapper.class);
            User user = userMapper.selectById(userId);
            if (user == null || user.getRole() != RoleConst.STUDENT) return false;

            RepairOrder order = new RepairOrder();
            order.setOrderNo(generateOrderNo());
            order.setUserId(userId);
            order.setDeviceType(deviceType);
            order.setDescription(description);
            order.setStatus(OrderStatusConst.PENDING);
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            mapper.insert(order);
            return true;
        });
    }

    @Override
    public List<RepairOrder> getOrdersByUser(Long userId) {
        return DbUtils.execute(session -> {
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            LambdaQueryWrapper<RepairOrder> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RepairOrder::getUserId, userId)
                    .orderByDesc(RepairOrder::getCreatedAt);
            return mapper.selectList(wrapper);
        });
    }

    @Override
    public RepairOrder getOrderById(Long orderId) {
        return DbUtils.execute(session -> {
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            return mapper.selectById(orderId);
        });
    }

    @Override
    public boolean cancelOrder(Long orderId, Long userId) {
        return DbUtils.execute(session -> {
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            RepairOrder order = mapper.selectById(orderId);
            if (order == null || !order.getUserId().equals(userId)) return false;
            if (!OrderStatusConst.PENDING.equals(order.getStatus())) return false;
            order.setStatus(OrderStatusConst.CANCELLED);
            mapper.updateById(order);
            return true;
        });
    }

    @Override
    public List<RepairOrder> getAllOrders(String status) {
        return DbUtils.execute(session -> {
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            LambdaQueryWrapper<RepairOrder> wrapper = new LambdaQueryWrapper<>();
            if (status != null && !status.isEmpty()) {
                wrapper.eq(RepairOrder::getStatus, status);
            }
            wrapper.orderByDesc(RepairOrder::getCreatedAt);
            return mapper.selectList(wrapper);
        });
    }

    @Override
    public boolean updateOrderStatus(Long orderId, String newStatus) {
        return DbUtils.execute(session -> {
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            RepairOrder order = mapper.selectById(orderId);
            if (order == null) return false;
            order.setStatus(newStatus);
            mapper.updateById(order);
            return true;
        });
    }

    @Override
    public boolean deleteOrder(Long orderId) {
        return DbUtils.execute(session -> {
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            return mapper.deleteById(orderId) > 0;
        });
    }
}