package com.example.backend.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VerifyCodeUtil {

    @Autowired
    private RedisUtil redisUtil;

    public String generateCode() {
        return null;
    }

}
