package org.sky.producer;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageProducer {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public void send(String message) {
        rocketMQTemplate.convertAndSend("AOI_ADDR_LIB_EVENT_TOPIC", message);
        System.out.println("发送消息：" + message);
    }
}