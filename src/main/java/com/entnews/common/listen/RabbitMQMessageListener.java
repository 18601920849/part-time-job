package com.entnews.common.listen;

import com.entnews.common.config.RabbitMQConfiguration;
import com.google.gson.Gson;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class RabbitMQMessageListener {

    /*@RabbitListener(queues = RabbitMQConfiguration.QUEUE_NAME)
    public void receiveMessage(@Payload Object message, Message amqpMessage) {
        Gson gson = new Gson();
        // 将消息体转换为字符串
        String messageBody = new String(amqpMessage.getBody());
        System.out.println(messageBody);
        // 解析消息体为List<Map<String, String>>类型
        List<Map<String, String>> wordList = gson.fromJson(messageBody, List.class);

        // 处理消息体生成word文件的逻辑
    }*/
}
