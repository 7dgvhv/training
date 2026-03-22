package com.dorm.console;

import com.dorm.constant.OrderStatusConst;
import com.dorm.constant.RoleConst;
import com.dorm.entity.RepairOrder;
import com.dorm.entity.User;
import com.dorm.service.RepairOrderService;
import com.dorm.service.UserService;
import com.dorm.service.impl.RepairOrderServiceImpl;
import com.dorm.service.impl.UserServiceImpl;
import com.dorm.utils.InputValidator;

import java.util.List;
import java.util.Scanner;


import java.io.IOException;

public class ConsoleUI {
    private final Scanner scanner = new Scanner(System.in);
    private final UserService userService = new UserServiceImpl();
    private final RepairOrderService orderService = new RepairOrderServiceImpl();

    private User currentUser; // 当前登录用户

    public void start() {
        while (true) {
            if (currentUser == null) {
                showMainMenu();
            } else {
                if (currentUser.getRole() == RoleConst.STUDENT) {
                    showStudentMenu();
                } else {
                    showAdminMenu();
                }
            }
        }
    }

    // 主菜单（未登录）
    private void showMainMenu() {
        System.out.println("\n===========================");
        System.out.println("🏠 宿舍报修管理系统");
        System.out.println("===========================");
        System.out.println("1. 登录");
        System.out.println("2. 注册");
        System.out.println("3. 退出");
        System.out.print("请选择操作（输入 1-3）：");
        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
                login();
                break;
            case "2":
                register();
                break;
            case "3":
                System.out.println("感谢使用，再见！");
                System.exit(0);
                break;
            default:
                System.out.println("输入无效，请重新选择。");
        }
    }



    // 密码输入
    private String readPasswordWithStar(String prompt) throws IOException {
        System.out.print(prompt);
        StringBuilder password = new StringBuilder();
        // 关键：先清空缓冲区，解决Scanner残留的回车问题
        while (System.in.available() > 0) {
            System.in.read();
        }

        int ch;
        while (true) {
            ch = System.in.read();
            // 回车/换行：结束输入
            if (ch == '\n' || ch == '\r') {
                break;
            }
            // 退格键（ASCII 8）：删除字符
            if (ch == '\b') {
                if (password.length() > 0) {
                    password.deleteCharAt(password.length() - 1);
                    // 回退一格、空格覆盖、再回退，实现删除效果
                    System.out.print("\b \b");
                }
            } else {
                // 普通字符：追加到密码，输出*
                password.append((char) ch);
                System.out.print('*');
                // 关键：强制刷新输出，避免*延迟显示
                System.out.flush();
            }
        }

        // 输入完成后换行
        System.out.println();
        return password.toString();
    }

    // 登录方法
    private void login() {
        System.out.println("\n===== 用户登录 =====");

        // 读取账号
        System.out.print("请输入账号：");
        StringBuilder usernameSb = new StringBuilder();
        int ch;
        try {
            while ((ch = System.in.read()) != -1) {
                if (ch == '\n' || ch == '\r') {
                    break;
                }
                usernameSb.append((char) ch);
            }
            String username = usernameSb.toString().trim();

            // 读取密码
            String password = readPasswordWithStar("请输入密码：");

            User user = userService.login(username, password);
            if (user != null) {
                currentUser = user;
                System.out.println("登录成功！角色：" + (user.getRole() == RoleConst.STUDENT ? "学生" : "管理员"));
            } else {
                System.out.println("账号或密码错误！");
            }

        } catch (IOException e) {

            System.out.println("\n输入异常：" + e.getMessage());
        }
    }

    // 注册
    private void register() {
        System.out.println("\n===== 用户注册 =====");
        System.out.print("请选择角色（1-学生，2-维修人员）：");
        String roleStr = scanner.nextLine();
        int role;
        try {
            role = Integer.parseInt(roleStr);
            if (role != RoleConst.STUDENT && role != RoleConst.ADMIN) {
                System.out.println("角色选择无效！");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("输入无效！");
            return;
        }

        System.out.print("请输入" + (role == RoleConst.STUDENT ? "学号" : "工号") + "（学生前缀3125/3225，管理员前缀0025）：");
        String username = scanner.nextLine();
        if (!InputValidator.isValidUsername(username, role)) {
            System.out.println("账号格式不正确！");
            return;
        }
        if (userService.isUsernameExists(username)) {
            System.out.println("账号已存在！");
            return;
        }

        System.out.print("请输入姓名：");
        String name = scanner.nextLine();

        System.out.print("请输入密码：");
        String pwd1 = scanner.nextLine();
        System.out.print("请确认密码：");
        String pwd2 = scanner.nextLine();
        if (!pwd1.equals(pwd2)) {
            System.out.println("两次密码不一致！");
            return;
        }

        boolean success = userService.register(username, pwd1, role, name);
        if (success) {
            System.out.println("注册成功！请返回主界面登录。");
        } else {
            System.out.println("注册失败，请重试。");
        }
    }

    // 学生菜单
    private void showStudentMenu() {
        // 检查是否已绑定宿舍（首次登录提示）
        if (currentUser.getDormBuilding() == null || currentUser.getDormBuilding().isEmpty()) {
            System.out.println("\n【提示】您还未绑定宿舍，请先绑定。");
            bindDorm();
            // 绑定后重新获取用户信息
            currentUser = userService.getUserById(currentUser.getId());
        }

        System.out.println("\n===== 学生菜单 =====");
        System.out.println("1. 绑定/修改宿舍");
        System.out.println("2. 创建报修单");
        System.out.println("3. 查看我的报修记录");
        System.out.println("4. 取消报修单");
        System.out.println("5. 修改密码");
        System.out.println("6. 查看基本信息");
        System.out.println("7. 退出");
        System.out.print("请选择操作（输入 1-7）：");
        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
                bindDorm();
                break;
            case "2":
                createOrder();
                break;
            case "3":
                viewMyOrders();
                break;
            case "4":
                cancelOrder();
                break;
            case "5":
                changePassword();
                break;
            case "6":
                showUserInfo();
                break;
            case "7":
                currentUser = null;
                System.out.println("已退出登录。");
                break;
            default:
                System.out.println("输入无效，请重新选择。");
        }
    }

    // 管理员菜单
    private void showAdminMenu() {
        System.out.println("\n===== 管理员菜单 =====");
        System.out.println("1. 查看所有报修单");
        System.out.println("2. 查看报修单详情");
        System.out.println("3. 更新报修单状态");
        System.out.println("4. 删除报修单");
        System.out.println("5. 修改密码");
        System.out.println("6. 查看基本信息");
        System.out.println("7. 退出");
        System.out.print("请选择操作（输入 1-7）：");
        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
                listAllOrders();
                break;
            case "2":
                viewOrderDetail();
                break;
            case "3":
                updateOrderStatus();
                break;
            case "4":
                deleteOrder();
                break;
            case "5":
                changePassword();
                break;
            case "6":
                showUserInfo();
                break;
            case "7":
                currentUser = null;
                System.out.println("已退出登录。");
                break;
            default:
                System.out.println("输入无效，请重新选择。");
        }
    }

    // 绑定宿舍
    private void bindDorm() {
        System.out.println("\n----- 绑定宿舍 -----");
        System.out.print("请输入宿舍楼栋（如：A栋）：");
        String building = scanner.nextLine();
        System.out.print("请输入房间号（如：301）：");
        String room = scanner.nextLine();
        boolean success = userService.bindDorm(currentUser.getId(), building, room);
        if (success) {
            System.out.println("绑定成功！");
            currentUser = userService.getUserById(currentUser.getId()); // 刷新
        } else {
            System.out.println("绑定失败，请重试。");
        }
    }

    // 创建报修单
    private void createOrder() {
        System.out.println("\n----- 创建报修单 -----");
        System.out.print("请输入设备类型（如：水龙头、灯管等）：");
        String deviceType = scanner.nextLine();
        System.out.print("请输入问题描述：");
        String desc = scanner.nextLine();
        boolean success = orderService.createOrder(currentUser.getId(), deviceType, desc);
        if (success) {
            System.out.println("报修单创建成功！");
        } else {
            System.out.println("创建失败，请重试。");
        }
    }

    // 查看我的报修记录
    private void viewMyOrders() {
        List<RepairOrder> list = orderService.getOrdersByUser(currentUser.getId());
        if (list.isEmpty()) {
            System.out.println("暂无报修记录。");
            return;
        }
        System.out.println("\n----- 我的报修记录 -----");
        for (RepairOrder order : list) {
            System.out.printf("单号：%s | 设备：%s | 状态：%s | 时间：%s\n",
                    order.getOrderNo(), order.getDeviceType(), order.getStatus(),
                    order.getCreatedAt().toString().replace("T", " "));
        }
        System.out.println("------------------------");
        System.out.print("输入单号查看详情（直接回车返回）：");
        String orderNo = scanner.nextLine();
        if (!orderNo.isEmpty()) {
            // 根据单号查找
            RepairOrder detail = list.stream().filter(o -> o.getOrderNo().equals(orderNo)).findFirst().orElse(null);
            if (detail != null) {
                System.out.println("\n----- 报修详情 -----");
                System.out.println("单号：" + detail.getOrderNo());
                System.out.println("设备：" + detail.getDeviceType());
                System.out.println("描述：" + detail.getDescription());
                System.out.println("状态：" + detail.getStatus());
                System.out.println("创建时间：" + detail.getCreatedAt());
                System.out.println("最后更新：" + detail.getUpdatedAt());
            } else {
                System.out.println("未找到该单号。");
            }
        }
    }

    // 取消报修单（学生）
    private void cancelOrder() {
        System.out.println("\n----- 取消报修单 -----");
        System.out.print("请输入要取消的报修单号：");
        String orderNo = scanner.nextLine();
        // 先根据单号查找自己的报修单
        List<RepairOrder> list = orderService.getOrdersByUser(currentUser.getId());
        RepairOrder order = list.stream().filter(o -> o.getOrderNo().equals(orderNo)).findFirst().orElse(null);
        if (order == null) {
            System.out.println("未找到该报修单或不属于您。");
            return;
        }
        if (!OrderStatusConst.PENDING.equals(order.getStatus())) {
            System.out.println("只有待处理状态的报修单可以取消。");
            return;
        }
        boolean success = orderService.cancelOrder(order.getId(), currentUser.getId());
        if (success) {
            System.out.println("报修单已取消。");
        } else {
            System.out.println("取消失败，请重试。");
        }
    }


    // 修改密码
    private void changePassword() {
        // 校验用户是否已登录（避免未登录操作）
        if (currentUser == null) {
            System.out.println("请先登录！");
            return;
        }

        System.out.println("\n----- 修改密码 -----");

        // 1. 输入旧密码
        String oldPwd = null;
        try {
            oldPwd = readPasswordWithStar("请输入旧密码：");
        } catch (IOException e) {
            System.out.println("密码输入异常：" + e.getMessage());
            return;
        }

        // 2. 立即校验旧密码
        boolean isOldPwdCorrect = userService.checkOldPassword(currentUser.getId(), oldPwd);
        if (!isOldPwdCorrect) {
            System.out.println("旧密码错误！修改密码失败。");
            return; // 旧密码错，直接结束，不允许输新密码
        }
        System.out.println("旧密码验证通过！");

        // 3. 旧密码正确，才允许输入新密码
        String newPwd1 = null, newPwd2 = null;
        try {
            newPwd1 = readPasswordWithStar("请输入新密码：");
            newPwd2 = readPasswordWithStar("请确认新密码：");
        } catch (IOException e) {
            System.out.println("密码输入异常：" + e.getMessage());
            return;
        }

        // 4. 校验两次新密码是否一致
        if (!newPwd1.equals(newPwd2)) {
            System.out.println("两次新密码输入不一致！修改密码失败。");
            return;
        }

        // 5. 执行密码修改（最终确认）
        boolean modifySuccess = userService.changePassword(currentUser.getId(), oldPwd, newPwd1);
        if (modifySuccess) {
            System.out.println("密码修改成功！请重新登录。");
            currentUser = null; // 强制登出，需重新登录
        } else {
            System.out.println("密码修改失败！请稍后重试。");
        }
    }

    // 查看基本信息
    private void showUserInfo() {
        System.out.println("\n----- 基本信息 -----");
        System.out.println("账号：" + currentUser.getUsername());
        System.out.println("姓名：" + currentUser.getName());
        System.out.println("角色：" + (currentUser.getRole() == RoleConst.STUDENT ? "学生" : "管理员"));
        if (currentUser.getRole() == RoleConst.STUDENT) {
            System.out.println("宿舍：" + currentUser.getDormBuilding() + " " + currentUser.getDormRoom());
        }
    }

    // 管理员：列出所有报修单（可筛选状态）
    private void listAllOrders() {
        System.out.println("\n----- 所有报修单 -----");
        System.out.print("输入状态筛选（直接回车查看全部，可选：待处理/处理中/已完成/已取消）：");
        String status = scanner.nextLine();
        if (status.isEmpty()) status = null;
        List<RepairOrder> list = orderService.getAllOrders(status);
        if (list.isEmpty()) {
            System.out.println("暂无报修单。");
            return;
        }
        for (RepairOrder order : list) {
            System.out.printf("ID：%d | 单号：%s | 学生ID：%d | 设备：%s | 状态：%s | 时间：%s\n",
                    order.getId(), order.getOrderNo(), order.getUserId(), order.getDeviceType(),
                    order.getStatus(), order.getCreatedAt().toString().replace("T", " "));
        }
    }

    // 管理员：查看单个报修单详情
    private void viewOrderDetail() {
        System.out.print("请输入报修单号或ID：");
        String input = scanner.nextLine().trim();
        RepairOrder order = null;

        //  查询单号
        List<RepairOrder> all = orderService.getAllOrders(null);
        order = all.stream()
                .filter(o -> o.getOrderNo().trim().equals(input))
                .findFirst()
                .orElse(null);

        //  单号没查到，再尝试用ID查询
        if (order == null) {
            try {
                Long id = Long.parseLong(input);
                order = orderService.getOrderById(id);
            } catch (NumberFormatException e) {
                // 不是数字，直接返回null
            }
        }

        if (order == null) {
            System.out.println("未找到该报修单。");
            return;
        }
        System.out.println("\n----- 报修详情 -----");
        System.out.println("ID：" + order.getId());
        System.out.println("单号：" + order.getOrderNo());
        System.out.println("学生ID：" + order.getUserId());
        System.out.println("设备：" + order.getDeviceType());
        System.out.println("描述：" + order.getDescription());
        System.out.println("状态：" + order.getStatus());
        System.out.println("创建时间：" + order.getCreatedAt());
        System.out.println("最后更新：" + order.getUpdatedAt());
    }

    // 管理员：更新状态
    private void updateOrderStatus() {
        System.out.print("请输入要更新的报修单号或ID：");
        String input = scanner.nextLine();
        RepairOrder order = null;

        //  查询单号
        List<RepairOrder> all = orderService.getAllOrders(null);
        order = all.stream()
                .filter(o -> o.getOrderNo().trim().equals(input))
                .findFirst()
                .orElse(null);

        //  单号没查到，再尝试用ID查询
        if (order == null) {
            try {
                Long id = Long.parseLong(input);
                order = orderService.getOrderById(id);
            } catch (NumberFormatException e) {
                // 不是数字，直接返回null
            }
        }

        if (order == null) {
            System.out.println("未找到该报修单。");
            return;
        }
        System.out.println("当前状态：" + order.getStatus());
        System.out.print("请输入新状态（待处理/处理中/已完成/已取消）：");
        String newStatus = scanner.nextLine();
        if (!(OrderStatusConst.PENDING.equals(newStatus) ||
                OrderStatusConst.PROCESSING.equals(newStatus) ||
                OrderStatusConst.COMPLETED.equals(newStatus) ||
                OrderStatusConst.CANCELLED.equals(newStatus))) {
            System.out.println("状态无效！");
            return;
        }
        boolean success = orderService.updateOrderStatus(order.getId(), newStatus);
        if (success) {
            System.out.println("状态更新成功！");
        } else {
            System.out.println("更新失败！");
        }
    }

    // 管理员：删除报修单
    private void deleteOrder() {
        System.out.print("请输入要删除的报修单号或ID：");
        String input = scanner.nextLine();
        RepairOrder order = null;

        //  查询单号
        List<RepairOrder> all = orderService.getAllOrders(null);
        order = all.stream()
                .filter(o -> o.getOrderNo().trim().equals(input))
                .findFirst()
                .orElse(null);

        //  单号没查到，再尝试用ID查询
        if (order == null) {
            try {
                Long id = Long.parseLong(input);
                order = orderService.getOrderById(id);
            } catch (NumberFormatException e) {
                // 不是数字，直接返回null
            }
        }

        if (order == null) {
            System.out.println("未找到该报修单。");
            return;
        }
        System.out.print("确认删除该报修单？(y/n)：");
        String confirm = scanner.nextLine();
        if ("y".equalsIgnoreCase(confirm)) {
            boolean success = orderService.deleteOrder(order.getId());
            if (success) {
                System.out.println("删除成功！");
            } else {
                System.out.println("删除失败！");
            }
        }
    }
}