package com.dorm.controller;

import com.dorm.dto.UpdateStatusRequest;
import com.dorm.entity.RepairOrder;
import com.dorm.entity.User;
import com.dorm.service.RepairOrderService;
import com.dorm.service.UserService;
import com.dorm.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private RepairOrderService repairOrderService;
    @Autowired
    private UserService userService;

    // 查询所有报修单（可筛选状态）
    @GetMapping("/orders")
    public Result<List<RepairOrder>> listOrders(@RequestParam(required = false) String status) {
        List<RepairOrder> list = repairOrderService.getAllOrders(status);
        return Result.success(list);
    }

    // 查看单个报修单详情（支持单号或ID）
    @GetMapping("/order/{idOrNo}")
    public Result<RepairOrder> getOrderDetail(@PathVariable String idOrNo) {
        // 第一步：先按单号查询
        RepairOrder order = repairOrderService.getOrderByOrderNo(idOrNo);
        if (order != null) {
            return Result.success(order);
        }
        // 第二步：单号查不到，再尝试按ID查询
        try {
            Long id = Long.parseLong(idOrNo);
            order = repairOrderService.getOrderById(id);
        } catch (NumberFormatException e) {
            // 转ID失败，直接返回
        }
        return order != null ? Result.success(order) : Result.error(404, "报修单不存在");
    }


    //更新报修单状态（支持按ID或单号更新）
    @PutMapping("/order/{orderIdOrNo}/status")
    public Result<?> updateStatus(
            @PathVariable String orderIdOrNo,
            @RequestBody UpdateStatusRequest request
    ) {
        // 打印接收到的原始参数
        System.out.println("接收到的单号/ID是：" + orderIdOrNo);

        RepairOrder order;

        // 1. 先尝试按单号查询
        order = repairOrderService.getOrderByOrderNo(orderIdOrNo);

        // 2. 单号查不到，再尝试按ID查询
        if (order == null) {
            try {
                Long orderId = Long.parseLong(orderIdOrNo);
                order = repairOrderService.getOrderById(orderId);
            } catch (NumberFormatException e) {
                // 转ID失败，直接返回
                return Result.error(404, "报修单不存在");
            }
        }

        // 3. 再次校验是否为空
        if (order == null) {
            return Result.error(404, "报修单不存在");
        }

        // 4. 执行状态更新
        boolean success = repairOrderService.updateOrderStatus(order.getId(), request.getStatus());

        // 5. 根据结果返回
        if (success) {
            return Result.success(null);
        } else {
            return Result.error(400, "更新失败");
        }
    }


    // 删除报修单（支持按ID或单号删除）
    @DeleteMapping("/order/{orderIdOrNo}")
    public Result<?> deleteOrder(@PathVariable String orderIdOrNo) {
        RepairOrder order;

        // 1. 优先按单号查询
        order = repairOrderService.getOrderByOrderNo(orderIdOrNo);

        // 2. 单号查不到，再尝试按ID查询
        if (order == null) {
            try {
                Long orderId = Long.parseLong(orderIdOrNo);
                order = repairOrderService.getOrderById(orderId);
            } catch (NumberFormatException e) {
                // 转ID失败，直接返回
                return Result.error(404, "报修单不存在");
            }
        }

        // 3. 最终校验订单是否存在
        if (order == null) {
            return Result.error(404, "报修单不存在");
        }

        // 4. 执行删除
        boolean success = repairOrderService.deleteOrder(order.getId());

        // 5. 返回结果
        if (success) {
            return Result.success(null);
        } else {
            return Result.error(400, "删除失败");
        }
    }

    // 管理员修改密码
    @PutMapping("/changePassword")
    public Result<?> changePassword(HttpServletRequest request,
                                    @RequestParam String oldPwd,
                                    @RequestParam String newPwd) {
        Long userId = (Long) request.getAttribute("userId");
        userService.changePassword(userId, oldPwd, newPwd);
        return Result.success(null);
    }

    // 管理员基本信息
    @GetMapping("/info")
    public Result<User> getInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getUserById(userId);
        user.setPassword(null);
        return Result.success(user);
    }
}