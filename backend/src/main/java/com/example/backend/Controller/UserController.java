package com.example.backend.Controller;

import com.example.backend.POJO.User;
import com.example.backend.Utils.JwtUtil;
import com.example.backend.VO.ResultVO;
import com.example.backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/login/password")
    public ResultVO<Map<String, Object>> loginByPassword(@RequestParam Map<String, Object> map) {
        System.out.println(map);

        Map<String, Object> searchingMap = new HashMap<>();
        searchingMap.put("telephone", map.get("telephone"));
        List<User> result = userMapper.selectByMap(searchingMap);
        if (result.size() == 0) {
            return new ResultVO<>(-1, "用户不存在", null);
        }
        if (!result.get(0).getPassword().equals(map.get("password"))) {
            return new ResultVO<>(-1, "密码错误", null);
        }

        String token = JwtUtil.sign(String.valueOf(map.get("telephone")));
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("token", token);
        resultMap.put("id", result.get(0).getId());

        if (token != null) {
            return new ResultVO<>(0, "用户登陆成功", resultMap);
        } else {
            return new ResultVO<>(-1, "Token生成失败", null);
        }
    }

}
