package com.example.backend.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class VerifyCodeUtil {

    @Autowired
    private RedisUtil redisUtil;

    private static final long CODE_EXPIRE_TIME = 10;
    private static final TimeUnit CODE_EXPIRE_TIME_UNIT = TimeUnit.MINUTES;

    private String generateCode() {
        return String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
    }

    private boolean storeIntoRedis(String email, String code) {
        email = "code_" + email;

        redisUtil.delete(email);
        return redisUtil.setWithTimeout(email, code, CODE_EXPIRE_TIME, CODE_EXPIRE_TIME_UNIT);
    }

    public String getCode(String email) {
        try {
            String code = generateCode();
            if (storeIntoRedis(email, code)) {
                return code;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean verifyCode(String email, String code) {
        email = "code_" + email;

        if (code != null && Objects.equals(redisUtil.get(email), code)) {
            redisUtil.delete(email);
            return true;
        }
        return false;
    }

}
