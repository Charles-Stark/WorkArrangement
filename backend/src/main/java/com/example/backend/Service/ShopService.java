package com.example.backend.Service;

import com.example.backend.VO.ResultVO;

public interface ShopService {

    public ResultVO<Object> addShop(String name, String address, double size, long manager);

}
