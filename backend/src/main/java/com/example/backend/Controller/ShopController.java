package com.example.backend.Controller;

import com.example.backend.Service.ShopService;
import com.example.backend.VO.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shop")
public class ShopController {

    @Autowired
    private ShopService shopService;

    @PostMapping("/add")
    public ResultVO<Object> addShop(@RequestParam("name") String name, @RequestParam("address") String address,
                                    @RequestParam("size") Double size, @RequestParam("manager") Long manager) {
        return shopService.addShop(name, address, size, manager);
    }

    @PostMapping("/delete")
    public ResultVO<Object> deleteShop(@RequestParam("id") Long id) {
        return shopService.deleteShop(id);
    }

    @GetMapping("/get/{id}")
    public ResultVO<Object> getShop(@PathVariable Long id) {
        return shopService.getShop(id);
    }

    @GetMapping("/getAll/{managerId}")
    public ResultVO<Object> getAllShop(@PathVariable Long managerId) {
        return shopService.getAllShop(managerId);
    }

    @PostMapping("/update")
    public ResultVO<Object> updateShop(@RequestParam("id") Long id,
                                       @RequestParam(value = "name", required = false) String name,
                                       @RequestParam(value = "address", required = false) String address,
                                       @RequestParam(value = "size", required = false) Double size,
                                       @RequestParam(value = "manager", required = false) Long manager) {
        return shopService.updateShop(id, name, address, size, manager);
    }

    @GetMapping("/shopData/{id}")
    public ResultVO<Object> getShopData(@PathVariable long id) {
        return shopService.getShopData(id);
    }

}
