-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS dorm_repair DEFAULT CHARACTER SET utf8mb4;

USE dorm_repair;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
                                      `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                                      `username` VARCHAR(20) NOT NULL UNIQUE COMMENT '账号（学号/工号）',
    `password` VARCHAR(100) NOT NULL COMMENT '加密密码',
    `role` TINYINT NOT NULL COMMENT '角色：1-学生，2-管理员',
    `name` VARCHAR(50) DEFAULT '' COMMENT '姓名',
    `dorm_building` VARCHAR(10) DEFAULT '' COMMENT '宿舍楼栋（学生）',
    `dorm_room` VARCHAR(10) DEFAULT '' COMMENT '房间号（学生）',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 报修单表
CREATE TABLE IF NOT EXISTS `repair_order` (
                                              `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                                              `order_no` VARCHAR(20) NOT NULL UNIQUE COMMENT '报修单号',
    `user_id` BIGINT NOT NULL COMMENT '学生ID',
    `device_type` VARCHAR(50) NOT NULL COMMENT '设备类型',
    `description` TEXT COMMENT '问题描述',
    `status` VARCHAR(20) NOT NULL DEFAULT '待处理' COMMENT '状态：待处理，处理中，已完成，已取消',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
