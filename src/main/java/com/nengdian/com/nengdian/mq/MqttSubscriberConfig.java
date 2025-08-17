package com.nengdian.com.nengdian.mq;

import com.alibaba.fastjson.JSONObject;
import com.nengdian.com.nengdian.service.MqttConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHandler;

import javax.annotation.Resource;

@Configuration
public class MqttSubscriberConfig {
    private static final Logger logger = LoggerFactory.getLogger(MqttSubscriberConfig.class);
    @Resource
    private MqttConsumer mqttConsumer;


    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return message -> {
//            logger.error("处理MQTT消息,record:{}", JSONObject.toJSON(message));
            mqttConsumer.consumer(message);
        };
    }


}
