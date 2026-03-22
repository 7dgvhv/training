package com.dorm.entity;

import java.time.LocalDateTime;

public class User {
    private Long id;
    private String username;       // 账号
    private String password;       // 加密后密码
    private Integer role;          // 1-学生 2-管理员
    private String name;           // 姓名
    private String dormBuilding;   // 宿舍楼栋
    private String dormRoom;       // 房间号
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 构造方法
    public User() {}

    // getter 和 setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Integer getRole() { return role; }
    public void setRole(Integer role) { this.role = role; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDormBuilding() { return dormBuilding; }
    public void setDormBuilding(String dormBuilding) { this.dormBuilding = dormBuilding; }

    public String getDormRoom() { return dormRoom; }
    public void setDormRoom(String dormRoom) { this.dormRoom = dormRoom; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role=" + role +
                ", name='" + name + '\'' +
                ", dormBuilding='" + dormBuilding + '\'' +
                ", dormRoom='" + dormRoom + '\'' +
                '}';
    }
}