package com.library.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Result;
import com.library.entity.User;
import com.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<User>> getUserList(@RequestParam(defaultValue = "1") Integer current,
                                         @RequestParam(defaultValue = "10") Integer size,
                                         @RequestParam(required = false) String keyword) {
        Page<User> page = new Page<>(current, size);
        Page<User> userPage = userService.getUserList(page, keyword);
        return Result.success(userPage);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.name")
    public Result<User> getUserDetail(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) {
            user.setPassword(null); // 不返回密码
        }
        return user != null ? Result.success(user) : Result.error("用户不存在");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.getById(#id).username == authentication.name")
    public Result<String> updateUser(@PathVariable Long id, @RequestBody User user) {
        User existingUser = userService.getById(id);
        if (existingUser == null) {
            return Result.error("用户不存在");
        }

        // 不允许通过此接口修改密码，密码修改有专门的接口
        user.setPassword(null);
        user.setId(id);
        return userService.updateById(user) ? Result.success("更新成功") : Result.error("更新失败");
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        return userService.updateUserStatus(id, status) ?
               Result.success("状态更新成功") : Result.error("状态更新失败");
    }

    @PutMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMIN') or @userService.getById(#id).username == authentication.name")
    public Result<String> updatePassword(@PathVariable Long id, @RequestParam String oldPassword, @RequestParam String newPassword) {
        return userService.updatePassword(id, oldPassword, newPassword) ?
               Result.success("密码修改成功") : Result.error("密码修改失败，请检查原密码是否正确");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> deleteUser(@PathVariable Long id) {
        return userService.removeById(id) ? Result.success("删除成功") : Result.error("删除失败");
    }
}