package com.example.rocketmq.controller;

import com.example.rocketmq.pojo.Order;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @Auther: huangcx
 * @Date: 2023/2/23 - 02 - 23 - 5:04
 * @Description: com.example.rocketmq.controller
 * @Version: 1.0
 */
@RestController
public class SeqProducerController {


    @GetMapping("/producer/order")
    public void testProducer() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("OrderProducer");
        producer.setNamesrvAddr("192.168.2.131:9876");
        producer.start();
        List<Order> orderList = Order.buildOrders();
        for (int i = 0; i < orderList.size(); i++) {
            String body = orderList.get(i).toString();
            Message msg = new Message("PartOrder", null, "Key" + i, body.getBytes(StandardCharsets.UTF_8));
            SendResult sendResult = producer.send(msg, (list, message, o) -> {
                Long id = (Long) o;
                long index = id % list.size(); // 根据订单id选择发送队列,这里的Object o就是下面的订单id
                return list.get((int) index);
            }, orderList.get(i).getOrderId());// 订单id
            System.out.println("SendResult status:" + sendResult.getSendStatus()
                    + ", queueId:" + sendResult.getMessageQueue().getQueueId()
                    + ", body:" + body);
        }
        producer.shutdown();
    }

    @GetMapping("/producer/scheduled")
    public void testScheduleProducer() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("ScheduledProducer");
        producer.setNamesrvAddr("192.168.2.131:9876");
        producer.start();
        for (int i = 0; i < 10; i++) {
            Message msg = new Message("ScheduledTopic", ("Hello scheduled message " + i).getBytes(StandardCharsets.UTF_8));
            msg.setDelayTimeLevel(4);
            producer.send(msg);
        }
        producer.shutdown();
    }
}
