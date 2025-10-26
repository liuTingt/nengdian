package com.nengdian.com.nengdian.mq.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
public class MqttConfig {

    @Value("${mqtt.host}")
    private String host;

    @Value("${mqtt.username}")
    private String username;

    @Value("${mqtt.password}")
    private String password;

    @Value("${mqtt.outClientId}")
    private String outClientId;

    @Value("${mqtt.inClientId}")
    private String inClientId;


    // 配置MQTT客户端工厂
    @Bean
    public MqttConnectOptions getMqttConnectOptions() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());
        mqttConnectOptions.setServerURIs(new String[]{host});
        mqttConnectOptions.setKeepAliveInterval(60);

        mqttConnectOptions.setAutomaticReconnect(true);      // 启用自动重连
        mqttConnectOptions.setMaxReconnectDelay(30000);     // 最大重连延迟30秒
        mqttConnectOptions.setConnectionTimeout(30);        // 连接超时30秒
        mqttConnectOptions.setCleanSession(false);          // 保持会话状态
        return mqttConnectOptions;
    }

    // 配置MQTT客户端
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(getMqttConnectOptions());
        return factory;
    }

    /**
     * 上报指令通道
     */
    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }
    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler =
                new MqttPahoMessageHandler(outClientId, mqttClientFactory());
        messageHandler.setAsync(true);
//        messageHandler.setDefaultTopic("defaultTopic");
        messageHandler.setConverter(new DefaultPahoMessageConverter());
        return messageHandler;
    }



    @Bean
    public MessageChannel mqttInputChannel() {

        return new DirectChannel();
    }

    @Bean
    public MessageProducer inbound(MqttPahoClientFactory mqttClientFactory) {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(
                        inClientId,
                        mqttClientFactory,
                        "1c69203e57fa/service/post","f4650b5bbaa6/service/post");
//                        "03aa2c178bf0/service/post","1c69203e57fa/service/post","1c69203fbafa/service/post",
//                        "430818019ddd/service/post","f4650b5bbaa6/service/post","f4650b5d4f66/service/post");
//                        "+/service/post"); // 订阅主题通配符 +来表示单一层级。#：多层级
        adapter.setCompletionTimeout(5000);

        DefaultPahoMessageConverter converter = new DefaultPahoMessageConverter();
        converter.setPayloadAsBytes(false);
        adapter.setConverter(converter);
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

}
