package com.example.backend.Controller;

import com.example.backend.POJO.User;
import com.example.backend.Service.MailService;
import com.example.backend.Service.UserService;
import com.example.backend.Utils.VerifyCodeUtil;
import com.example.backend.VO.ResultVO;
import com.example.backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    @Autowired
    private VerifyCodeUtil verifyCodeUtil;

    @PostMapping("/register")
    public ResultVO<Map<String, Object>> register(@RequestParam Map<String, Object> map) {
        if (map.containsKey("email") && map.containsKey("password") && map.containsKey("username") && map.containsKey("verify")) {
            return userService.register(map.get("email").toString(), map.get("password").toString(), map.get("username").toString(), map.get("verify").toString());
        } else {
            return new ResultVO<>(-1, "未接收到参数", null);
        }
    }

    @PostMapping("/email/sendCode")
    public ResultVO<String> sendCode(@RequestParam Map<String, Object> map) {
        if (map.containsKey("email")) {
            String email = map.get("email").toString();
            String code = verifyCodeUtil.getCode(email);
            if (code != null) {
                mailService.sendVerifyCode(email, code);
                return new ResultVO<>(0, "获取验证码成功", null);
            }
            return new ResultVO<>(-1, "获取验证码失败", null);
        } else {
            return new ResultVO<>(-1, "未接收到参数", null);
        }
    }

    @PostMapping("/login/password")
    public ResultVO<Map<String, Object>> loginByPassword(@RequestParam Map<String, Object> map) {
        if (map.containsKey("email") && map.containsKey("password")) {
            return userService.loginByPassword(map.get("email").toString(), map.get("password").toString());
        } else {
            return new ResultVO<>(-1, "未接收到参数", null);
        }
    }

    @PostMapping("/login/code")
    public ResultVO<Map<String, Object>> loginByCode(@RequestParam Map<String, Object> map) {
        if (map.containsKey("email") && map.containsKey("verify")) {
            return userService.loginByPassword(map.get("email").toString(), map.get("verify").toString());
        } else {
            return new ResultVO<>(-1, "未接收到参数", null);
        }
    }

    @GetMapping("/info/get/{id}")
    public ResultVO<Map<String, Object>> getUserInfo(@PathVariable long id) {
        try {
            Map<String, Object> searchingMap = new HashMap<>();
            searchingMap.put("id", id);
            User result = userMapper.selectByMap(searchingMap).get(0);
            searchingMap.put("email", result.getEmail());
            searchingMap.put("username", result.getUsername());
            return new ResultVO<>(0, "用户信息获取成功", searchingMap);
        } catch (Exception e) {
            return new ResultVO<>(-1, "用户信息获取失败", null);
        }
    }

    @GetMapping("/photo/get/{id}")
    public void getUserPhoto(@PathVariable long id, HttpServletResponse response) {
        Map<String, Object> searchingMap = new HashMap<>();
        searchingMap.put("id", id);

        try {
            User result = userMapper.selectByMap(searchingMap).get(0);
            InputStream inputStream = new ByteArrayInputStream(result.getPhoto());
            response.setContentType(result.getPhotoType());

            ServletOutputStream outputStream = response.getOutputStream();
            int length = 0;
            byte[] buffer = new byte[1024];
            while ((length = inputStream.read(buffer, 0, 1024)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
    }

    @PostMapping("/photo/upload/{id}")
    public ResultVO<Object> uploadUserPhoto(@PathVariable long id, @RequestParam("photo") MultipartFile multipartFile) {
        try {
            User user = new User(id, null, null, null, multipartFile.getBytes(), multipartFile.getContentType(), null);
            userMapper.updateById(user);
            return new ResultVO<>(0, "修改头像成功", null);
        } catch (Exception e) {
            return new ResultVO<>(-1, "修改头像失败", null);
        }
    }

    @PostMapping("/info/update/{id}")
    public ResultVO<Map<String, Object>> updateUserInfo(@PathVariable long id, @RequestParam Map<String, Object> map) {
        return userService.updateUserInfo(id, map);
    }

    @PostMapping("/password/reset")
    public ResultVO<Map<String, Object>> resetPassword(@RequestParam Map<String, Object> map) {
        if (map.containsKey("email") && map.containsKey("password") && map.containsKey("verify")) {
            return userService.resetPassword(map.get("email").toString(), map.get("password").toString(), map.get("verify").toString());
        } else {
            return new ResultVO<>(-1, "未接收到参数", null);
        }
    }

    @PostMapping("/logout")
    public ResultVO<Object> logout(@RequestParam("id") long id) {
        return null;
    }

}
