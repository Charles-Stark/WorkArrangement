package com.example.backend.Interceptor;

import com.example.backend.VO.ResultVO;
import com.example.backend.mapper.UserMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserInterceptor implements HandlerInterceptor {

    @Autowired
    UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            String userId = request.getHeader("userId");
            if (userId != null && userMapper.selectById(userId).getIsManager()) {
                return true;
            }
        } catch (Exception ignored) {

        }
        response.setCharacterEncoding("utf-8");
        response.getWriter().append(new JSONObject(new ResultVO<>(-1, "无权访问", null)).toString());
        return false;
    }
}
