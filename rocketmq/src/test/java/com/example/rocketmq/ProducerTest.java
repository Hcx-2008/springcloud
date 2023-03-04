package com.example.rocketmq;

import com.example.rocketmq.util.ListSplitter;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: huangcx
 * @Date: 2023/2/22 - 02 - 22 - 3:33
 * @Description: com.example.rocketmq
 * @Version: 1.0
 */
@SpringBootTest
public class ProducerTest {
    @Test
    void testSynchronizationMsg() throws Exception {
        // 实例化消息生产者producer
        DefaultMQProducer producer = new DefaultMQProducer("group_test");
        // 设置NameServer的地址
        producer.setNamesrvAddr("192.168.2.131:9876");
        // 启动producer实例
        producer.start();
        // 同步方式发送10条消息
        for (int i = 0; i < 10; i++) {
            // 创建消息并指定Topic，Tag和消息体
            Message msg = new Message("TopicTest", "TagA",
                    ("Hello RocketMQ" + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
            // 同步发送方式
            SendResult sendResult = producer.send(msg);
            // 通过sendResult返回消息是否成功送达
            System.out.println(sendResult);
        }
        // 如果不再发送消息，关闭producer
        producer.shutdown();
    }

    @Test
    void testAsynchronousMsg() throws Exception{
        // 实例化消息生产者producer
        DefaultMQProducer producer = new DefaultMQProducer("group_test");
        // 设置NameServer的地址
        producer.setNamesrvAddr("192.168.2.131:9876");
        // 启动producer实例
        producer.start();
        // 同步方式发送10条消息
        for (int i = 0; i < 10; i++) {
            final int index = i;
            // 创建消息并指定Topic，Tag和消息体
            Message msg = new Message("TopicTest2", "TagA", "OrderId888",
                    ("Hello RocketMQ" + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
            // 异步方式发送,SendCallback接收异步返回结果的调用
            producer.send(msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    System.out.println("success:" + sendResult);
                }

                @Override
                public void onException(Throwable throwable) {
                    System.out.println("fail:" + index + " " + throwable.getMessage());
                }
            });
        }
        Thread.sleep(10000);
        // 如果不再发送消息，关闭producer
        producer.shutdown();
    }

    @Test
    void unidirectionalMsg() throws Exception {
        // 实例化消息生产者producer
        DefaultMQProducer producer = new DefaultMQProducer("group_test");
        // 设置NameServer的地址
        producer.setNamesrvAddr("192.168.2.131:9876");
        // 启动producer实例
        producer.start();
        // 同步方式发送10条消息
        for (int i = 0; i < 10; i++) {
            // 创建消息并指定Topic，Tag和消息体
            Message msg = new Message("TopicTest",
                    "TagA",
                    ("Hello RocketMQ" + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
            // 发送单向消息，无任何返回结果
            producer.sendOneway(msg);
        }
        // 如果不再发送消息，关闭producer
        producer.shutdown();
    }

    @Test
    void testBatchMsg() throws Exception{
        // 实例化消息生产者producer
        DefaultMQProducer producer = new DefaultMQProducer("BatchProducer");
        // 设置NameServer的地址
        producer.setNamesrvAddr("192.168.2.131:9876");
        // 启动producer实例
        producer.start();
        String topic = "BatchTest";
        List<Message> messages = new ArrayList<>(100 * 1000);
        for (int i = 0; i < 100 * 1000; i++) {
            messages.add(new Message(topic, "tag", "OrderId" + i, ("Hello world " + i).getBytes(StandardCharsets.UTF_8)));
        }
        ListSplitter splitter = new ListSplitter(messages);
        while (splitter.hasNext()) {
            List<Message> list = splitter.next();
            producer.send(list);
        }
        producer.shutdown();
    }

    void producerDetail() throws Exception {
        // producerGroup:生产者所属组(针对 事务消息 高可用)
        DefaultMQProducer producer = new DefaultMQProducer("produce_details");
        // 默认主题在每一个Broker队列数量(对于新创建主题有效)
        producer.setDefaultTopicQueueNums(8);
        // 发送消息默认超时时间，默认3s
        producer.setSendMsgTimeout(1000*3);
        // 消息体超过该值则启用压缩，默认4K
        producer.setCompressMsgBodyOverHowmuch(1024*4);
        // 同步方式发送消息重试次数,默认为2，总共执行3次
        producer.setRetryTimesWhenSendFailed(2);
        // 异步方式发送消息重试次数，默认为2, 总共执行3次
        producer.setRetryTimesWhenSendAsyncFailed(2);
        // 消息重试时选择另外一个Broker时(消息没有存储成功是否发送到另外一个Broker)，默认为false
        producer.setRetryAnotherBrokerWhenNotStoreOK(false);
        // 允许发送的最大消息长度，默认4M
        producer.setMaxMessageSize(1024*1024*4);
        // 设置NameServer地址
        producer.setNamesrvAddr("192.168.1.131:9876");
        // 启动producer实例
        producer.start();
        // 查找该主题下所有消息队列
        List<MessageQueue> messageQueues = producer.fetchPublishMessageQueues("TopicTest");
        for (MessageQueue messageQueue : messageQueues) {
            System.out.println(messageQueue.getQueueId());
        }
        // 创建消息，指定topic和tag
        Message msg = new Message("TopicTest", "TagA", "OrderId888", "Hello world".getBytes(StandardCharsets.UTF_8));
        // 单向发送
        // 1.1 发送单向消息
        producer.sendOneway(msg);
        // 1.2 指定队列单向发送消息(使用select方法)
        producer.sendOneway(msg, new MessageQueueSelector() {
            @Override
            public MessageQueue select(List<MessageQueue> list, Message message, Object o) {
                return list.get(0);
            }
        }, null);
        // 1.3 指定队列单向发送消息(根据之前查找出来的主题)
        producer.sendOneway(msg, messageQueues.get(0));


        // 同步发送
        // 2.1 同步发送消息
        SendResult sendResult0 = producer.send(msg);
        // 2.2 同步超时发送消息（属性设置：sendMsgTimeOut 发送消息默认超时时间，默认3s）
        sendResult0 = producer.send(msg, 3000);
        // 2.3 指定队列同步发送消息(使用select方法)
        sendResult0 = producer.send(msg, new MessageQueueSelector() {
            @Override
            public MessageQueue select(List<MessageQueue> list, Message message, Object o) {
                return list.get(0);
            }
        }, null);
        // 2.4 指定队列返送同步消息（根据之前查找出来的主题队列信息）
        sendResult0 = producer.send(msg, messageQueues.get(0));


        // 异步消息发送,成功走onSuccess,失败走onException
        producer.send(msg, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println(sendResult.getMsgId() + ":OK");
            }

            @Override
            public void onException(Throwable throwable) {
                System.out.println("Fail:" + throwable.getMessage());
            }
        });

        producer.send(msg, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println(sendResult.getMsgId() + ":OK");
            }

            @Override
            public void onException(Throwable throwable) {
                System.out.println("Fail:" + throwable.getMessage());
            }
        }, 3000);

        // 选择指定队列异步发送消息(根据之前查找出来的主题队列信息)
        producer.send(msg, messageQueues.get(0), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println(sendResult.getMsgId() + ":OK");
            }

            @Override
            public void onException(Throwable throwable) {
                System.out.println("Fail:" + throwable.getMessage());
            }
        });

        // 选择指定队列异步发送消息
        producer.send(msg, new MessageQueueSelector() {
            @Override
            public MessageQueue select(List<MessageQueue> list, Message message, Object o) {
                return list.get(0);
            }
        }, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println(sendResult.getMsgId() + ":OK");
            }

            @Override
            public void onException(Throwable throwable) {
                System.out.println("Fail:" + throwable.getMessage());
            }
        });


    }

    void consumerDetails() throws MQClientException {
        // 消费者组
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("test");
        // 指定nameServer地址信息
        consumer.setNamesrvAddr("192.168.2.131:9876");
        // 消息消费模式(默认集群消费)
        consumer.setMessageModel(MessageModel.CLUSTERING);
        // 指定消费开始偏移量(上次消费偏移量，最大偏移量，最小偏移量，启动时间戳)开始消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);

        // 消费者最小线程数量（默认20）
        consumer.setConsumeThreadMin(20);
        // 消费者最大线程数量（默认20）
        consumer.setConsumeThreadMax(20);
        // 推模式下任务间隔时间(推模式也是基于不断地轮询拉取的封装)
        consumer.setPullInterval(0);
        // 推模式下任务拉取的条数，默认32条
        consumer.setPullBatchSize(32);
        // 消息重试次数，-1代表16次（超过次数称为死信消息）
        consumer.setMaxReconsumeTimes(-1);
        // 消息消费超时时间(消息可能阻塞正在使用的线程的最大时间：以分钟为单位)
        consumer.setConsumeTimeout(15);

        // 获取消费者对主题分配了哪些消息
        Set<MessageQueue> messageQueueSet = consumer.fetchSubscribeMessageQueues("TopicTest");
        Iterator iterator = messageQueueSet.iterator();
        while(iterator.hasNext()) {
            MessageQueue messageQueue = (MessageQueue) iterator.next();
            System.out.println(messageQueue.getQueueId());
        }

        // 方法订阅
        // 基于主题订阅消息，消息过滤使用表达式
        consumer.subscribe("TopicTest", "*" );
        // 基于主题订阅消息，消息过滤使用表达式
        consumer.subscribe("TopicTest", MessageSelector.bySql("a between 0 and 3"));
        // 基于主题订阅消息，消息过滤使用表达式
        consumer.subscribe("TopicTest", MessageSelector.byTag("tagA|tagB"));
        // 取消消息订阅
        consumer.unsubscribe("TopicTest");

        //  注册并发事件监听器(多线程并发，消费无序，比较快)
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                for (MessageExt msg : list) {
                    String topic = msg.getTopic();
                    String msgBody = new String(msg.getBody(), StandardCharsets.UTF_8);
                    String tags = msg.getTags();
                    System.out.println("收到消息: " + "topic:" + topic + ", tags:" + tags + ", msg: " + msgBody);
                }
                // 失败会进重试队列
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        // 注册顺序消息事件监听器
        consumer.registerMessageListener(new MessageListenerOrderly() {
            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
                context.setAutoCommit(true);
                for (MessageExt msg : msgs) {
                    System.out.println("ConsumeThread=" + Thread.currentThread().getName() + ", queueId=" + msg.getQueueId() + "content:" + new String(msg.getBody()));
                }
                try {
                    // 模拟业务逻辑处理
                    TimeUnit.MILLISECONDS.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    // 意思是西安等一会儿，一会儿在处理这批消息，而不是放到重试队列，放到重试队列会乱序
                    return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                }
                return ConsumeOrderlyStatus.SUCCESS;
            }
        });
        // 启动消费者
        consumer.start();
    }
}
