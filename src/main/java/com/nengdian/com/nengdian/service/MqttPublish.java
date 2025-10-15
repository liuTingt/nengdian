package com.nengdian.com.nengdian.service;

import com.alibaba.fastjson.JSONObject;
import com.nengdian.com.nengdian.bo.InstructionBO;
import com.nengdian.com.nengdian.ao.SettingAO;
import com.nengdian.com.nengdian.mq.MqttGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MqttPublish {
    private static final Logger logger = LoggerFactory.getLogger(MqttPublish.class);
    private static final String SUFFIX = "%s/service/get";
    @Resource
    private MqttGateway mqttGateway;


    public void publish(SettingAO request) {
        try {
            String topic = String.format(SUFFIX, request.getDevId());
            InstructionBO instruction = build(request);
            mqttGateway.sendToMqtt(topic,0, JSONObject.toJSONString(instruction));
            logger.info("发送指令成功:{}", JSONObject.toJSONString(instruction));
        } catch (Exception e) {
            logger.error("发送指令失败，request:{}", JSONObject.toJSON(request));
        }
    }

    private InstructionBO build(SettingAO request) {
        InstructionBO instruction = new InstructionBO();
        instruction.setSX(request.getUpperLimit());
        instruction.setXX(request.getLowerLimit());
        instruction.setSM(request.isLowEnergySwitch());
        instruction.setI(request.getInstallHeight());
        instruction.setF(request.getDistance());
        instruction.setPump(0);
        if (request.isDrainageModel()) {
            instruction.setPump(1);
        }
//        instruction.setSize(0);
//        instruction.setL(0.0D);
//        instruction.setW(0.0D);
        return instruction;
    }
}
