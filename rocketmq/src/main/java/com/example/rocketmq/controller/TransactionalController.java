package com.example.rocketmq.controller;

import com.example.rocketmq.service.TractionListenerImpl;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: huangcx
 * @Date: 2023/2/28 - 02 - 28 - 17:03
 * @Description: com.example.rocketmq.controller
 * @Version: 1.0
 */
@RestController
public class TransactionalController {
    @GetMapping("/producer/transaction")
    public void testTransaction() throws Exception {
        TransactionListener transactionListener = new TractionListenerImpl() ;// 创建事务监听
        TransactionMQProducer producer = new TransactionMQProducer("TransactionProducer");
        producer.setNamesrvAddr("192.168.2.131:9876");
        ExecutorService executorService = new ThreadPoolExecutor(2, 5, 100, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(2000), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) { // 创建事务监听
                Thread t = new Thread(r);
                t.setName("Thread-msg-check-check");
                return t;
            }
        });
        producer.setExecutorService(executorService);// 设置生产者回查线程池
        producer.setTransactionListener(transactionListener); // 生产者设置事务回查监听器
        producer.start();
        System.out.println("开启事务@Transaction");
        Message msg = new Message("TransactionTopic", null, ("A向B转100").getBytes(StandardCharsets.UTF_8));
        SendResult sendResult = null;
        try {
            sendResult = producer.sendMessageInTransaction(msg, null);
        } catch (MQClientException e) {
            e.printStackTrace(); // 回滚rollback
        }
        System.out.println(sendResult.getSendStatus());
        // 设置producer销毁时长,防止销毁过快不能模拟场景
        Thread.sleep(100000);
        producer.shutdown();
    }
}
