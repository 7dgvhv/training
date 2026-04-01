package com.dorm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dorm.entity.RepairOrder;
import com.dorm.entity.User;
import com.dorm.exception.BusinessException;
import com.dorm.mapper.RepairOrderMapper;
import com.dorm.mapper.UserMapper;
import com.dorm.service.RepairOrderService;
import com.dorm.utils.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import java.io.File;
import java.util.UUID;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Value;



@Service
public class RepairOrderServiceImpl extends ServiceImpl<RepairOrderMapper, RepairOrder> implements RepairOrderService {

    @Autowired
    private RepairOrderMapper repairOrderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private FileUploadUtil fileUploadUtil;

    private String generateOrderNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = new Random().nextInt(1000);
        return date + String.format("%03d", random);
    }

    // 1. 注入配置文件的路径
    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public RepairOrder createOrder(Long userId, String deviceType, String description, MultipartFile image) throws IOException {
        // 1. 构建报修单实体
        RepairOrder order = new RepairOrder();
        order.setUserId(userId);
        order.setDeviceType(deviceType);
        order.setDescription(description);
        order.setStatus("待处理");
        order.setImageUrl(null); // 初始化图片路径

        // 2. 处理图片上传
        if (image != null && !image.isEmpty()) {
            // 上传目录
            File uploadDirFile = new File(uploadPath);
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs(); // 自动创建目录
            }

            // 生成唯一文件名，避免重名覆盖
            String fileName = UUID.randomUUID() + ".jpg";
            File destFile = new File(uploadDirFile, fileName);
            image.transferTo(destFile); // 保存图片到本地

            // 生成可访问的图片URL，存入实体
            String imageUrl = "/uploads/" + fileName;
            order.setImageUrl(imageUrl);
        }

        // 3. 生成唯一单号
        String orderNo = "2026" + System.currentTimeMillis();
        order.setOrderNo(orderNo);

        // 4. 保存到数据库
        this.save(order);

        // 5. 返回保存后的报修单
        return order;
    }

    @Override
    public List<RepairOrder> getOrdersByUser(Long userId) {
        LambdaQueryWrapper<RepairOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RepairOrder::getUserId, userId)
                .orderByDesc(RepairOrder::getCreatedAt);
        return repairOrderMapper.selectList(wrapper);
    }

    @Override
    public RepairOrder getOrderById(Long orderId) {
        return repairOrderMapper.selectById(orderId);
    }

    @Override
    public boolean cancelOrder(String orderNo, Long userId) {
        RepairOrder order = this.getOrderByOrderNo(orderNo);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(400, "订单不存在或不属于您");
        }
        if (!"待处理".equals(order.getStatus())) {
            throw new BusinessException(400, "只有待处理状态的报修单可以取消");
        }
        order.setStatus("已取消");
        repairOrderMapper.updateById(order);
        return true;
    }

    @Override
    public List<RepairOrder> getAllOrders(String status) {
        LambdaQueryWrapper<RepairOrder> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(RepairOrder::getStatus, status);
        }
        wrapper.orderByDesc(RepairOrder::getCreatedAt);
        return repairOrderMapper.selectList(wrapper);
    }

    @Override
    public boolean updateOrderStatus(Long orderId, String newStatus) {
        RepairOrder order = repairOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(400, "订单不存在");
        }
        order.setStatus(newStatus);
        repairOrderMapper.updateById(order);
        return true;
    }

    @Override
    public boolean deleteOrder(Long orderId) {
        return repairOrderMapper.deleteById(orderId) > 0;
    }

    @Override
    public RepairOrder getOrderByOrderNo(String orderNo) {
        LambdaQueryWrapper<RepairOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RepairOrder::getOrderNo, orderNo);
        return repairOrderMapper.selectOne(wrapper);
    }
}