package com.example.rocketmq.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: huangcx
 * @Date: 2023/2/23 - 02 - 23 - 7:10
 * @Description: com.example.rocketmq.config
 * @Version: 1.0
 */
@Component
@Slf4j
public class ProducerConfig {

    @Bean
    public DefaultMQPushConsumer orderConsumer() throws Exception {
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
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    // 这个点要注意：意思是先等一会，一会儿再处理这批消息，而不是放到重试队列
                    return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                }
                return ConsumeOrderlyStatus.SUCCESS;
            }
        });
        consumer.start();
        return consumer;
    }

    @Bean
    public DefaultMQPushConsumer scheduledConsumer() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("ScheduledConsumer");
        consumer.setNamesrvAddr("192.168.2.131:9876");
        consumer.subscribe("ScheduledTopic", "*");
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            for (MessageExt msg : msgs) {
                System.out.println("Receive message[msgId=" + msg.getMsgId() + "]"
                    + (msg.getStoreTimestamp()-msg.getBornTimestamp()) + "ms later");
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
        return consumer;
    }

    @Bean
    public DefaultMQPushConsumer transactionConsumer() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("TransactionConsumer");
        consumer.setNamesrvAddr("192.168.2.131:9876");
        consumer.subscribe("TransactionTopic", "*");
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            for (MessageExt msg : msgs) {
                System.out.println("Receive message[msgId=" + msg.getMsgId() + "]"
                        + (msg.getStoreTimestamp()-msg.getBornTimestamp()) + "ms later + \nmessage:" + msg);
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
        return consumer;
    }


}
