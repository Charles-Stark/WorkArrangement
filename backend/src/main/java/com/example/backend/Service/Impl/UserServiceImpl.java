package com.example.backend.Service.Impl;

import com.example.backend.POJO.User;
import com.example.backend.Service.MailService;
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

    @Autowired
    private MailService mailService;

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
    public ResultVO<Map<String, Object>> register(String email, String password, String username, String code) {
        User result = searchUserByEmail(email);

        if (result != null) {
            return new ResultVO<>(-1, "用户已存在", null);
        }
        if (!verifyCodeUtil.verifyCode(email, code)) {
            return new ResultVO<>(-1, "验证码错误", null);
        }

        try {
            result = new User(null, email, HashUtil.getSHA256(password), username, null, null, true);
            userMapper.insert(result);
        } catch (Exception e) {
            return new ResultVO<>(-1, "用户注册失败", null);
        }

        return loginByPassword(email, password);

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
        resultMap.put("isManager", result.getIsManager());

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
        resultMap.put("isManager", result.getIsManager());

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
        User result = searchUserByEmail(email);

        if (result == null) {
            return new ResultVO<>(-1, "用户不存在", null);
        }
        if (!verifyCodeUtil.verifyCode(email, code)) {
            return new ResultVO<>(-1, "验证失败", null);
        }

        try {
            userMapper.updateById(new User(result.getId(), null, HashUtil.getSHA256(password), null, null, null, null));
        } catch (Exception e) {
            return new ResultVO<>(-1, "修改密码失败", null);
        }

        return new ResultVO<>(0, "修改密码成功", null);
    }

    @Override
    public ResultVO<Object> sendCodeWhenLogin(String email) {
        try {
            User result = searchUserByEmail(email);
            if (result == null) {
                return new ResultVO<>(-1, "用户不存在", null);
            }

            String code = verifyCodeUtil.getCode(email);
            if (code != null) {
                mailService.sendVerifyCode(email, code);
                return new ResultVO<>(0, "获取验证码成功", null);
            }
        } catch (Exception ignored) {

        }
        return new ResultVO<>(-1, "获取验证码失败", null);
    }

    @Override
    public ResultVO<Object> sendCodeWhenRegister(String email) {
        try {
            String code = verifyCodeUtil.getCode(email);
            if (code != null) {
                mailService.sendVerifyCode(email, code);
                return new ResultVO<>(0, "获取验证码成功", null);
            }
        } catch (Exception ignored) {

        }
        return new ResultVO<>(-1, "获取验证码失败", null);
    }

}
