package com.example.backend.Service.Impl;

import com.example.backend.POJO.User;
import com.example.backend.Service.UserService;
import com.example.backend.Utils.HashUtil;
import com.example.backend.Utils.JwtUtil;
import com.example.backend.Utils.VerifyCodeUtil;
import com.example.backend.VO.ResultVO;
import com.example.backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private VerifyCodeUtil verifyCodeUtil;

    private User searchUserByEmail(String email) {
        Map<String, Object> searchingMap = new HashMap<>();
        searchingMap.put("email", email);
        List<User> result = userMapper.selectByMap(searchingMap);

        if (result.size() == 0) {
            return null;
        }
        return result.get(0);
    }

    @Override
    public ResultVO<Map<String, Object>> register(String email, String password, String username, String verify) {
        return null;
    }

    @Override
    public ResultVO<Map<String, Object>> loginByPassword(String email, String password) {
        User result = searchUserByEmail(email);

        if (result == null) {
            return new ResultVO<>(-1, "用户不存在", null);
        }
        if (!HashUtil.isHashSame(password, result.getPassword())) {
            return new ResultVO<>(-1, "密码错误", null);
        }

        String token = JwtUtil.sign(email);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("token", token);
        resultMap.put("id", result.getId());

        if (token != null) {
            return new ResultVO<>(0, "用户登陆成功", resultMap);
        } else {
            return new ResultVO<>(-1, "Token生成失败", null);
        }
    }

    @Override
    public ResultVO<Map<String, Object>> loginByCode(String email, String code) {
        User result = searchUserByEmail(email);

        if (result == null) {
            return new ResultVO<>(-1, "用户不存在", null);
        }
        if (!verifyCodeUtil.verifyCode(email, code)) {
            return new ResultVO<>(-1, "验证码错误", null);
        }

        String token = JwtUtil.sign(email);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("id", result.getId());
        resultMap.put("token", token);

        if (token != null) {
            return new ResultVO<>(0, "用户登陆成功", resultMap);
        } else {
            return new ResultVO<>(-1, "Token生成失败", null);
        }
    }

    @Override
    public ResultVO<Map<String, Object>> updateUserInfo(long id, Map<String, Object> map) {
        return null;
    }

    @Override
    public ResultVO<Map<String, Object>> resetPassword(String email, String password, String code) {
        return null;
    }

}
