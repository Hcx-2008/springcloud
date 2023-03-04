package com.example.rocketmq.util;

import org.apache.rocketmq.common.message.Message;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @Auther: huangcx
 * @Date: 2023/2/23 - 02 - 23 - 15:48
 * @Description: com.example.rocketmq.util
 * @Version: 1.0
 */
public class ListSplitter implements Iterator<List<Message>> {
    private int sizeLimit = 1000*1000;
    private final List<Message> messages;
    private int currIndex;
    public ListSplitter(List<Message> messages) {this.messages = messages;};
    @Override
    public boolean hasNext() {
        return currIndex < messages.size();
    }

    @Override
    public List<Message> next() {
        int nextIndex = currIndex;
        int totalSize = 0;
        for (; nextIndex < messages.size(); nextIndex++) {
            Message message = messages.get(nextIndex);
            int tmpSize = message.getTopic().length() + message.getBody().length;
            Map<String , String> properties = message.getProperties();
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                tmpSize += entry.getKey().length() + entry.getValue().length();
            }
            tmpSize = tmpSize + 20; // 增加日志开销的20字节
            if (tmpSize > sizeLimit) {
                if (nextIndex - currIndex == 0) {// 单个消息超过了最大的限制1M，否则会阻塞线程
                    nextIndex++;// 假如下一个子列表没有元素，则添加这个子列表然后退出循环，否则退出循环
                }
                break;
            }
            if (tmpSize + totalSize > sizeLimit) {
                break;
            } else {
                totalSize += tmpSize;
            }
        }
        List<Message> subList = messages.subList(currIndex, nextIndex);
        currIndex = nextIndex;
        return subList;
    }
}
