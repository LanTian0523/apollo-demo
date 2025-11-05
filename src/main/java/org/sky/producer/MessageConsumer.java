package org.sky.producer;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

/**
 * 消息队列消费者
 *
 * @author lantian
 */
@Service
@RocketMQMessageListener(topic = "AOI_ADDR_LIB_EVENT_TOPIC", consumerGroup = "PID-AOI-WORKBENCH")
public class MessageConsumer implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {
        System.out.println("接收消息：" + message);
    }
}