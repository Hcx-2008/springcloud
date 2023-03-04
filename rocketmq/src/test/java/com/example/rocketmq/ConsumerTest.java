package com.example.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @Auther: huangcx
 * @Date: 2023/2/22 - 02 - 22 - 3:33
 * @Description: com.example.rocketmq
 * @Version: 1.0
 */
@SpringBootTest
public class ConsumerTest {
    @Test
    void consumerCluster() throws Exception {
        // 实例化消息生产者，指定组名
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("group_consumer");
        // 指定Namesrv地址信息
        consumer.setNamesrvAddr("192.168.2.131:9876");
        // 订阅Topic
        consumer.subscribe("TopicTest", "*");
        consumer.subscribe("PartOrder", "*");
        // 负载均衡消费模式（可以不设置，默认就是负载均衡模式）
        consumer.setMessageModel(MessageModel.CLUSTERING);
        // 注册回调函数，处理消息
        consumer.registerMessageListener((List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) -> {
            for (MessageExt msg : list) {
                String topic = msg.getTopic();
                String msgBody = new String(msg.getBody(), StandardCharsets.UTF_8);
                String tags = msg.getTags();
                System.out.println("收到的消息: " + "topic:" + topic + ",tags:" + tags + ",msgBody:" +msgBody);
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
        Thread.sleep(10000);
        consumer.shutdown();
    }

    @Test
    void consumerBroadcast() throws Exception {
        // 实例化消息生产者，指定组名
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("group_consumer");
        // 指定NameServ地址信息
        consumer.setNamesrvAddr("192.168.2.131:9876");
        // 订阅Topic
        consumer.subscribe("TopicTest", "*");
        consumer.subscribe("TopicTest2", "*");
        // 广播模式消费
        consumer.setMessageModel(MessageModel.BROADCASTING);
        // 注册回调函数，处理消息
        consumer.registerMessageListener((List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) -> {
            for (MessageExt msg : list) {
                String topic = msg.getTopic();
                String msgBody = new String(msg.getBody(), StandardCharsets.UTF_8);
                String tags = msg.getTags();
                System.out.println("收到的消息: " + "topic:" + topic + ",tags:" + tags + ",msgBody:" +msgBody);
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
        Thread.sleep(10000);
        System.out.println("consumer Start");
        consumer.shutdown();
    }


}
