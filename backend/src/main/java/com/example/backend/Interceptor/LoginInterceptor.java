package com.example.backend.Interceptor;

import com.example.backend.Utils.JwtUtil;
import com.example.backend.VO.ResultVO;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            String token = request.getHeader("authorization");
            if (token != null && JwtUtil.verify(token)) {
                return true;
            }
        } catch (Exception ignored) {

        }
        response.setCharacterEncoding("utf-8");
        response.getWriter().append(new JSONObject(new ResultVO<>(-1, "请先注册或登陆", null)).toString());
        return false;
    }
}
