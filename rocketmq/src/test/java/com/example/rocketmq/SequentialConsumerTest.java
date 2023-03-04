package com.example.rocketmq;

import com.example.rocketmq.pojo.Order;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: huangcx
 * @Date: 2023/2/22 - 02 - 22 - 20:41
 * @Description: com.example.rocketmq
 * @Version: 1.0
 */
@SpringBootTest
public class SequentialConsumerTest {
    @Test
    void testProducer() throws Exception {
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

    @Test
    void testConsumer() throws Exception{
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("OrderConsumer");
        consumer.setNamesrvAddr("192.168.2.131:9876");
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        consumer.subscribe("PartOrder", "*");
        consumer.registerMessageListener(new MessageListenerOrderly() {
             @Override
             public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
                 context.setAutoCommit(true);
                 for (MessageExt msg : msgs) {
                     // 可以看到每个queue有唯一的consume线程来消费，订单对每个queue分区有序
                     System.out.println("consumeThread=" + Thread.currentThread().getName()
                             + ", queueId=" + msg.getQueueId() + ", content:" + new String(msg.getBody()));
                 }
                 try {
                     // 模拟业务逻辑处理
                     TimeUnit.MILLISECONDS.sleep(1000);
                 } catch (InterruptedException e) {
                     e.printStackTrace();
                     // 这个点要注意：意思是先等一会，一会儿再处理这批消息，而不是放到重试队列
                     return ConsumeOrderlyStatus.SUCCESS;
                 }
                 return ConsumeOrderlyStatus.SUCCESS;
             }
         });
        consumer.start();
        Thread.sleep(50000);
        System.out.println("consume finish");
    }
}
