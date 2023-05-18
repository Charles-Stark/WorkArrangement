package com.example.backend.Controller;

import com.example.backend.POJO.Employee;
import com.example.backend.POJO.User;
import com.example.backend.Service.UserService;
import com.example.backend.Utils.MagicNumberUtil;
import com.example.backend.VO.ResultVO;
import com.example.backend.mapper.EmployeeMapper;
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

    private static final long MAX_PHOTO_SIZE = 2 * 1024 * 1024;  // 用户头像大小限制 2MB

    private static final Map<String, String> ALLOWED_PHOTO_TYPES = new HashMap<>();

    static {
        ALLOWED_PHOTO_TYPES.put("FFD8FF", "image/jpeg");
        ALLOWED_PHOTO_TYPES.put("89504E", "image/png");
    }

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResultVO<Map<String, Object>> register(@RequestBody Map<String, String> data) {
        return userService.register(data.get("email"), data.get("password"), data.get("username"), data.get("verify"));
    }

    @PostMapping("/register/sendCode")
    public ResultVO<Object> sendCodeWhenRegister(@RequestBody Map<String, String> data) {
        return userService.sendCodeWhenRegister(data.get("email"));
    }

    @PostMapping("/login/sendCode")
    public ResultVO<Object> sendCodeWhenLogin(@RequestBody Map<String, String> data) {
        return userService.sendCodeWhenLogin(data.get("email"));
    }

    @PostMapping("/email/reset/sendCode")
    public ResultVO<Object> sendCodeWhenChangingEmail(@RequestBody Map<String, String> data) {
        return userService.sendCodeWhenChangingEmail(data.get("email"));
    }

    @PostMapping("/login/password")
    public ResultVO<Map<String, Object>> loginByPassword(@RequestBody Map<String, String> data) {
        return userService.loginByPassword(data.get("email"), data.get("password"));
    }

    @PostMapping("/login/code")
    public ResultVO<Map<String, Object>> loginByCode(@RequestBody Map<String, String> data) {
        return userService.loginByCode(data.get("email"), data.get("verify"));
    }

    @GetMapping("/info/get/{id}")
    public ResultVO<Map<String, Object>> getUserInfo(@PathVariable long id) {
        try {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("id", id);

            User user = userMapper.selectById(id);
            resultMap.put("email", user.getEmail());
            resultMap.put("username", user.getUsername());
            resultMap.put("isManager", user.getIsManager());

            if (!user.getIsManager()) {
                Employee employee = employeeMapper.selectById(id);
                if (employee.getPosition().contains("门店经理")) {
                    resultMap.put("isShopManager", true);
                } else {
                    resultMap.put("isShopManager", false);
                }
            }
            return new ResultVO<>(0, "用户信息获取成功", resultMap);
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
    public ResultVO<Object> uploadUserPhoto(@PathVariable long id, @RequestParam("photo") MultipartFile photo) {
        try {
            if (photo.getSize() > MAX_PHOTO_SIZE) {
                return new ResultVO<>(-1, "图片过大", null);
            }
            String magicNumber = MagicNumberUtil.getMagicNumber(photo.getBytes(), 6);
            if (!ALLOWED_PHOTO_TYPES.containsKey(magicNumber)) {
                return new ResultVO<>(-1, "类型不符", null);
            }

            User user = new User(id, null, null, null, photo.getBytes(), ALLOWED_PHOTO_TYPES.get(magicNumber), null);
            userMapper.updateById(user);
            return new ResultVO<>(0, "修改头像成功", null);
        } catch (Exception e) {
            return new ResultVO<>(-1, "修改头像失败", null);
        }
    }

    @PostMapping("/info/username/update/{id}")
    public ResultVO<Map<String, Object>> updateUsername(@PathVariable long id, @RequestBody Map<String, String> data) {
        try {
            User user = new User(id, null, null, data.get("username"), null, null, null);
            userMapper.updateById(user);
            return new ResultVO<>(0, "修改用户名成功", null);
        } catch (Exception e) {
            return new ResultVO<>(-1, "修改用户名失败", null);
        }
    }

    @PostMapping("/password/reset")
    public ResultVO<Map<String, Object>> resetPassword(@RequestBody Map<String, String> data) {
        return userService.resetPassword(data.get("email"), data.get("password"), data.get("verify"));
    }

    @PostMapping("/email/reset")
    public ResultVO<Object> resetEmail(@RequestBody Map<String, String> data) {
        return userService.resetEmail(Long.valueOf(data.get("id")), data.get("email"), data.get("verify"));
    }

    @PostMapping("/logout")
    public ResultVO<Object> logout(@RequestBody Map<String, String> data) {
        return new ResultVO<>(0, "退出登陆成功", null);
    }

}
