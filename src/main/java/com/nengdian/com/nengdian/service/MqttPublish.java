package com.nengdian.com.nengdian.service;

import com.alibaba.fastjson.JSONObject;
import com.nengdian.com.nengdian.bo.InstructionBO;
import com.nengdian.com.nengdian.ao.SettingAO;
import com.nengdian.com.nengdian.common.DeviceTypeEnum;
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
        instruction.setSX((double) request.getUpperLimit());
        instruction.setXX((double) request.getLowerLimit());
        instruction.setSM(request.isLowEnergySwitch());
        instruction.setI((double) request.getInstallHeight()/100);
        instruction.setF((double) request.getDistance()/100);
        instruction.setPump(0);
        if (request.getType().equals(DeviceTypeEnum.ELECTRIC.getType()) && request.isDrainageModel()) {
            instruction.setPump(1);
        }
        if (request.getType().equals(DeviceTypeEnum.SOLAR_ENERGY.getType())) {
            instruction.setU(request.getCheckPeriod());
        }
//        instruction.setSize(0);
//        instruction.setL(0.0D);
//        instruction.setW(0.0D);
        return instruction;
    }
}
