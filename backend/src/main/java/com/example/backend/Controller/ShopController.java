package com.example.backend.Controller;

import com.example.backend.VO.ResultVO;
import com.example.backend.mapper.ShopMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shop")
public class ShopController {

    @Autowired
    private ShopMapper shopMapper;

    @PostMapping("/add")
    public ResultVO<Object> addShop() {
        return null;
    }

}
