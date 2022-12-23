package com.example.backend.Service;

import com.example.backend.VO.ResultVO;

import java.util.Map;

public interface UserService {

    public ResultVO<Map<String, Object>> register(String email, String password, String username, String verify);

    public ResultVO<Map<String, Object>> loginByPassword(String email, String password);

    public ResultVO<Map<String, Object>> loginByCode(String email, String code);

}
