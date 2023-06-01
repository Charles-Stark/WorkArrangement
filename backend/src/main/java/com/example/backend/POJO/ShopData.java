package com.example.backend.POJO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("ShopData")
public class ShopData {

    private Long id;
    private long shopId;
    private long managerId;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Data data;

    @lombok.Data
    @AllArgsConstructor
    public static class Data {
        public List<Integer> existingCustomer;
        public List<Integer> newCustomer;

        public Map<String, Integer> performance;

        public Map<String, List<Integer>> sales;

        public Data() {
            Random random = new Random();
            existingCustomer = new ArrayList<>();
            newCustomer = new ArrayList<>();

            existingCustomer.add(random.nextInt(20, 100));
            newCustomer.add(random.nextInt(10, 60));

            for (int i = 1; i < 12; i++) {
                existingCustomer.add(existingCustomer.get(i - 1) + newCustomer.get(i - 1));
                newCustomer.add(random.nextInt(10, 60));
            }

            int a = random.nextInt(15, 25),
                    b = random.nextInt(15, 25),
                    c = random.nextInt(15, 25),
                    d = (int) ((100 - a - b - c) * random.nextDouble(0.4, 0.6));
            performance = new HashMap<>();
            performance.put("导购部", a);
            performance.put("收银部", b);
            performance.put("营销部", c);
            performance.put("库房部", d);
            performance.put("调研部", 100 - a - b - c - d);

            String[] shoeType = new String[]{"皮鞋", "运动鞋", "凉鞋", "拖鞋", "旅游鞋", "跑鞋", "钉鞋", "篮球鞋", "足球鞋"};
            sales = new HashMap<>();
            Set<String> types = new HashSet<>();
            while (types.size() < 5) {
                types.add(shoeType[random.nextInt(0, 9)]);
            }
            for (String type : types) {
                List<Integer> list = new ArrayList<>();
                list.add(random.nextInt(20, 140));
                list.add(random.nextInt(20, 140));
                list.add(random.nextInt(20, 140));
                list.add(random.nextInt(20, 140));
                sales.put(type, list);
            }
        }
    }

}
