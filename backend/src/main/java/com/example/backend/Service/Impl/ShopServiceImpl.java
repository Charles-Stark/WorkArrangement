package com.example.backend.Service.Impl;

import com.example.backend.POJO.Shop;
import com.example.backend.Service.ShopService;
import com.example.backend.VO.ResultVO;
import com.example.backend.mapper.ShopMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    private ShopMapper shopMapper;

    @Override
    public ResultVO<Object> addShop(String name, String address, double size, long manager) {

        try {
            Shop shop = new Shop(null, name, address, size, manager);
            shopMapper.insert(shop);
        } catch (Exception e) {
            return new ResultVO<>(-1, "添加门店失败", null);
        }
        return new ResultVO<>(0, "添加门店成功", null);

    }
}
