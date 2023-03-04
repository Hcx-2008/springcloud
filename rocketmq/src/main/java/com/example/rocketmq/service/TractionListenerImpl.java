package com.example.rocketmq.service;

import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Auther: huangcx
 * @Date: 2023/2/28 - 02 - 28 - 17:18
 * @Description: com.example.rocketmq.service
 * @Version: 1.0
 */
@Service
public class TractionListenerImpl implements TransactionListener {
    @Override
    public LocalTransactionState executeLocalTransaction(Message message, Object o) {
        System.out.println("执行业务代码.....update");
        // return LocalTransactionState.COMMIT_MESSAGE
        // return LocalTransactionState.ROLLBACK_MESSAGE;
        return LocalTransactionState.UNKNOW;
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
        System.out.println("checkLocalTransacation:" + new Date());
        return LocalTransactionState.COMMIT_MESSAGE;
    }
}
