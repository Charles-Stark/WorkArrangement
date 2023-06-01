package com.example.backend.Service;

import com.example.backend.VO.ResultVO;

public interface ShopService {

    ResultVO<Object> addShop(String name, String address, double size, long manager);

    ResultVO<Object> deleteShop(long id);

    ResultVO<Object> getShop(long id);

    ResultVO<Object> getAllShop(long managerId);

    ResultVO<Object> updateShop(long id, String name, String address, Double size, Long manager);

    ResultVO<Object> getShopData(long shopId);

}
