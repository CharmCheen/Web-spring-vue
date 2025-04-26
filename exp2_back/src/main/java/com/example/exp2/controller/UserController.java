package com.example.exp2.controller;

import com.example.exp2.entity.User;
import com.example.exp2.service.UserService;
import com.example.exp2.util.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;  // 注入 UserService

    @Autowired
    private ImageUtil imageUtil;

    // 获取所有用户
    @GetMapping("/list")
    public ResponseEntity<List<User>> list() {
        List<User> users = userService.findAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // 根据 ID 获取用户
    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        User user = userService.findUserById(id);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/avatar/{account}")
    public ResponseEntity<?> getAvatar(@PathVariable String account) {
        // 尝试通过用户名或邮箱查找用户
        User user = userService.findByUsername(account);
        if (user == null) {
            user = userService.findByEmail(account);
        }

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, String> response = new HashMap<>();
        response.put("avatar", user.getAvatar());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String account = loginRequest.get("account");
        String password = loginRequest.get("password");

        try {
            // 验证必填字段
            if (account == null || account.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("账号和密码不能为空");
            }

            // 尝试通过用户名或邮箱查找用户
            User user = userService.findByUsername(account);
            if (user == null) {
                user = userService.findByEmail(account);
            }

            // 用户不存在
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户不存在");
            }

            // 日志：数据库密码内容
            System.out.println("[登录] 用户名/邮箱: " + account + "，数据库密码: " + user.getPassword());

            // 验证密码（数据库密码必须是BCrypt加密格式）
            boolean match = false;
            try {
                match = org.springframework.security.crypto.bcrypt.BCrypt.checkpw(password, user.getPassword());
            } catch (Exception e) {
                // 兼容数据库中有明文密码的极端情况
                System.out.println("[登录] BCrypt校验异常，尝试明文对比");
                match = password.equals(user.getPassword());
            }
            if (!match) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("密码错误");
            }

            // 创建返回的用户信息（排除敏感信息）
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("username", user.getUsername());
            userInfo.put("email", user.getEmail());
            userInfo.put("avatar", user.getAvatar());
            userInfo.put("birthdate", user.getBirthdate());
            userInfo.put("address", user.getAddress());
            userInfo.put("postcode", user.getPostcode());
            // 兼容前端token存储
            userInfo.put("token", user.getUsername());

            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            // 记录异常信息
            e.printStackTrace();
            // 返回清晰的错误消息，避免"No message available"
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("登录处理过程中发生错误：" + (e.getMessage() != null ? e.getMessage() : "未知错误"));
        }
    }

    // 用户注册
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        // 验证必填字段
        if (user.getUsername() == null || user.getUsername().trim().isEmpty() ||
            user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("用户名和密码不能为空");
        }

        // 验证用户名是否已存在
        if (userService.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body("用户名已存在");
        }

        // 如果提供了邮箱，验证邮箱是否已存在
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty() && 
            userService.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("邮箱已被注册");
        }

        try {
            // 处理头像
            if (user.getAvatar() != null && !user.getAvatar().trim().isEmpty() && 
                !user.getAvatar().equals("default.jpg")) {
                try {
                    String fileName = imageUtil.saveBase64Image(user.getAvatar());
                    user.setAvatar(fileName);
                } catch (Exception e) {
                    return ResponseEntity.badRequest().body("头像处理失败：" + e.getMessage());
                }
            } else {
                user.setAvatar("default.jpg");
            }

            // 密码加密
            user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            
            // 保存用户
            userService.save(user);
            return ResponseEntity.ok("注册成功");
        } catch (Exception e) {
            // 如果保存失败且已上传了头像，删除头像文件
            if (user.getAvatar() != null && !user.getAvatar().equals("default.jpg")) {
                imageUtil.deleteImage(user.getAvatar());
            }
            return ResponseEntity.internalServerError().body("注册失败：" + e.getMessage());
        }
    }

    // 更新用户信息
    @PutMapping("/update")
    public ResponseEntity<String> update(@RequestBody User user) {
        if (userService.findUserById(user.getId()) == null) {
            return new ResponseEntity<>("用户不存在", HttpStatus.NOT_FOUND);
        }
        userService.updateUser(user);
        return new ResponseEntity<>("更新成功", HttpStatus.OK);
    }

    // 删除用户
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        if (userService.findUserById(id) == null) {
            return new ResponseEntity<>("用户不存在", HttpStatus.NOT_FOUND);
        }
        userService.deleteUser(id);
        return new ResponseEntity<>("删除成功", HttpStatus.OK);
    }
    
    /**
     * 搜索用户（模糊搜索）
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestParam String query) {
        try {
            if (query == null || query.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("搜索关键词不能为空");
            }
            
            List<User> users = userService.searchUsers(query);
            
            // 移除敏感信息
            users.forEach(user -> {
                user.setPassword(null);  // 不返回密码
            });
            
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("搜索用户失败: " + e.getMessage());
        }
    }

    /**
     * 批量获取用户信息
     */
    @GetMapping("/batch")
    public ResponseEntity<?> getUsersByUsernames(@RequestParam List<String> usernames) {
        try {
            List<User> users = userService.findUsersByUsernames(usernames);
            // 移除敏感信息
            users.forEach(user -> user.setPassword(null));
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("获取用户信息失败: " + e.getMessage());
        }
    }
}
