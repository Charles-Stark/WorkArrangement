package com.example.backend.Service;

import com.example.backend.VO.ResultVO;

import java.util.Map;

public interface UserService {

    ResultVO<Map<String, Object>> register(String email, String password, String username, String verify);

    ResultVO<Map<String, Object>> loginByPassword(String email, String password);

    ResultVO<Map<String, Object>> loginByCode(String email, String code);

    ResultVO<Map<String, Object>> updateUserInfo(long id, Map<String, Object> map);

    ResultVO<Map<String, Object>> resetPassword(String email, String password, String code);

    ResultVO<Object> sendCodeWhenLogin(String email);

    ResultVO<Object> sendCodeWhenRegister(String email);

}
