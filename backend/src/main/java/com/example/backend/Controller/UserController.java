package com.example.backend.Controller;

import com.example.backend.POJO.User;
import com.example.backend.Utils.JwtUtil;
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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;


    @PostMapping("/register")
    public ResultVO<Map> register(@RequestParam Map<String, Object> map) {
        return null;
    }

    @PostMapping("/email/sendCode")
    public ResultVO<String> sendCode(@RequestParam Map<String, Object> map) {
        return null;
    }

    @PostMapping("/login/password")
    public ResultVO<Map<String, Object>> loginByPassword(@RequestParam Map<String, Object> map) {
        Map<String, Object> searchingMap = new HashMap<>();
        searchingMap.put("email", map.get("email"));
        List<User> result = userMapper.selectByMap(searchingMap);
        if (result.size() == 0) {
            return new ResultVO<>(-1, "用户不存在", null);
        }
        if (!result.get(0).getPassword().equals(map.get("password"))) {
            return new ResultVO<>(-1, "密码错误", null);
        }

        String token = JwtUtil.sign(String.valueOf(map.get("email")));
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("token", token);
        resultMap.put("id", result.get(0).getId());

        if (token != null) {
            return new ResultVO<>(0, "用户登陆成功", resultMap);
        } else {
            return new ResultVO<>(-1, "Token生成失败", null);
        }
    }

    @PostMapping("/login/code")
    public ResultVO<Map<String, Object>> loginByCode(@RequestParam Map<String, Object> map) {
        return null;
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
        User result = userMapper.selectByMap(searchingMap).get(0);

        try {
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
        try {
            User user = new User();
            user.setId(id);
            if (map.containsKey("email")) {
                user.setEmail(map.get("email").toString());
            }
            if (map.containsKey("username")) {
                user.setUsername(map.get("username").toString());
            }
            if (map.containsKey("password")) {
                // Send email code to verify
                // if ok then proceed
            }
            // give a new token
            return null;
        } catch (Exception e) {
            return new ResultVO<>(-1, "修改信息失败", null);
        }
    }

    @PostMapping("/password/reset")
    public ResultVO<Object> resetPassword(@RequestParam Map<String, Object> map) {
        return null;
    }

    @PostMapping("/logout")
    public ResultVO<Object> logout(@RequestParam("id") long id) {
        return null;
    }

}
