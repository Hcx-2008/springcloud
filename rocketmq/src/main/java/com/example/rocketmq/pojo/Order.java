package com.example.rocketmq.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: huangcx
 * @Date: 2023/2/22 - 02 - 22 - 20:42
 * @Description: com.example.rocketmq.pojo
 * @Version: 1.0
 */
@Data
@Accessors(chain = true)
public class Order {
    private String desc;
    private long orderId;

    public static List<Order> buildOrders() {
        List<Order> orders = new ArrayList<>();
        Order orderDemo = new Order();

        orderDemo.setOrderId(1).setDesc("创建");
        orders.add(orderDemo);

        orderDemo = new Order();
        orderDemo.setOrderId(2).setDesc("创建");
        orders.add(orderDemo);

        orderDemo = new Order();
        orderDemo.setOrderId(1).setDesc("付款");
        orders.add(orderDemo);

        orderDemo = new Order();
        orderDemo.setOrderId(3).setDesc("创建");
        orders.add(orderDemo);

        orderDemo = new Order();
        orderDemo.setOrderId(2).setDesc("付款");
        orders.add(orderDemo);

        orderDemo = new Order();
        orderDemo.setOrderId(3).setDesc("付款");
        orders.add(orderDemo);

        orderDemo = new Order();
        orderDemo.setOrderId(2).setDesc("推送");
        orders.add(orderDemo);

        orderDemo = new Order();
        orderDemo.setOrderId(3).setDesc("推送");
        orders.add(orderDemo);

        orderDemo = new Order();
        orderDemo.setOrderId(2).setDesc("完成");
        orders.add(orderDemo);

        orderDemo = new Order();
        orderDemo.setOrderId(1).setDesc("推送");
        orders.add(orderDemo);

        orderDemo = new Order();
        orderDemo.setOrderId(3).setDesc("完成");
        orders.add(orderDemo);

        orderDemo = new Order();
        orderDemo.setOrderId(1).setDesc("完成");
        orders.add(orderDemo);
        return orders;
    }

}
