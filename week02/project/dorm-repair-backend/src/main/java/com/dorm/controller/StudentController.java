package com.dorm.controller;

import com.dorm.dto.RepairOrderRequest;
import com.dorm.entity.RepairOrder;
import com.dorm.entity.User;
import com.dorm.service.RepairOrderService;
import com.dorm.service.UserService;
import com.dorm.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

import java.io.File;
import java.util.UUID;


@RestController
@RequestMapping("/api/student")
public class StudentController {

    @Autowired
    private UserService userService;

    @Autowired
    private RepairOrderService orderService;

    private Long getCurrentUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    @PostMapping("/bindDorm")
    public Result<?> bindDorm(HttpServletRequest request, @RequestParam String building, @RequestParam String room) {
        userService.bindDorm(getCurrentUserId(request), building, room);
        return Result.success(null);
    }


    @PostMapping("/createOrder")
    public Result<RepairOrder> createOrder(
            HttpServletRequest request,
            @RequestParam("device") String device,
            @RequestParam("description") String description,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) throws IOException {

        Long userId = getCurrentUserId(request);
        RepairOrder savedOrder = orderService.createOrder(userId, device, description, image);
        return Result.success(savedOrder);
    }

    @GetMapping("/myOrders")
    public Result<List<RepairOrder>> myOrders(HttpServletRequest request) {
        List<RepairOrder> list = orderService.getOrdersByUser(getCurrentUserId(request));
        return Result.success(list);
    }

    @PutMapping("/cancelOrder/{orderNo}")
    public Result<?> cancelOrder(HttpServletRequest request, @PathVariable String orderNo) {
        orderService.cancelOrder(orderNo, getCurrentUserId(request));
        return Result.success(null);
    }

    @PutMapping("/changePassword")
    public Result<?> changePassword(HttpServletRequest request,
                                    @RequestParam String oldPwd,
                                    @RequestParam String newPwd) {
        userService.changePassword(getCurrentUserId(request), oldPwd, newPwd);
        return Result.success(null);
    }

    @GetMapping("/info")
    public Result<User> info(HttpServletRequest request) {
        User user = userService.getUserById(getCurrentUserId(request));
        user.setPassword(null); // 隐藏密码
        return Result.success(user);
    }
}