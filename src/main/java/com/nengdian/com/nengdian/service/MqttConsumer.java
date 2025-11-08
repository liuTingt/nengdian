package com.nengdian.com.nengdian.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.nengdian.com.nengdian.bo.RecordBO;
import com.nengdian.com.nengdian.common.BizException;
import com.nengdian.com.nengdian.common.LiquidStatusEnum;
import com.nengdian.com.nengdian.dao.DeviceRecordRepository;
import com.nengdian.com.nengdian.entity.DeviceRecord;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class MqttConsumer {
    private static final Logger logger = LoggerFactory.getLogger(MqttConsumer.class);

    private static final String stringPayload = "mqtt subscribe successful";

    private List<String> testDevices = Lists.newArrayList("f4650beb620a","f4650beb7006","f4650b5c5c86","2043a8f53b0a");

    @Resource
    private DeviceRecordRepository recordRepository;
    @Resource
    private AlterNotifyService alterNotifyService;


    public void consumer(Message message) {
        try {
            String topic = "";
            if (!Objects.isNull(message.getHeaders().get(MqttHeaders.TOPIC))) {
                topic = message.getHeaders().get(MqttHeaders.TOPIC).toString();
            }
            if (!Objects.isNull(message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC))) {
                topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC).toString();
            }
            String devId = topic.split("/")[0];
            if (!isDeal(devId, message)) {
                return;
            }

            RecordBO recordBO = JSONObject.parseObject(message.getPayload().toString(), RecordBO.class);
            long startTime = System.currentTimeMillis();

            logger.info("处理MQTT消息,devId:{},record:{}", devId, JSONObject.toJSON(recordBO));
            DeviceRecord currentRecord = recordRepository.findDeviceRecordByDevId(devId);

            DeviceRecord targetRecord = buildRecord(currentRecord, recordBO, devId);
            recordRepository.save(targetRecord);

            long time2 = System.currentTimeMillis();
            if (recordBO.getWS() == LiquidStatusEnum.Low.getCode() || recordBO.getWS() == LiquidStatusEnum.Height.getCode()) {
                alterNotifyService.alertNotify(currentRecord, targetRecord);
            }
            long endTime = System.currentTimeMillis();
            logger.info("处理MQTT消息耗时,查库耗时：{}，处理消息耗时：{}，总耗时:{}", (time2 - startTime), (endTime - time2), (endTime - startTime));
        } catch (BizException e) {
            logger.error("处理MQTT消息业务异常,record:{}", JSONObject.toJSON(message), e);
        } catch (Exception e) {
            logger.error("处理MQTT消息异常,record:{}", JSONObject.toJSON(message), e);
        }
    }

    private boolean isDeal(String devId, Message message) {
        String payload = message.getPayload().toString();
        if (Strings.isBlank(payload) || stringPayload.equals(payload)) {
            return false;
        }

        RecordBO recordBO = JSONObject.parseObject(payload, RecordBO.class);
        if (testDevices.contains(devId) && Strings.isNotBlank(recordBO.getWater())) {
            return true;
        }

        if (Strings.isBlank(recordBO.getNET()) || Objects.isNull(recordBO.getF()) ||
                Strings.isBlank(recordBO.getWater()) || Objects.isNull(recordBO.getI()) ||
                !"4G".equals(recordBO.getNET())) {
            return false;
        }
        return true;
    }

    private DeviceRecord buildRecord(DeviceRecord currentRecord, RecordBO recordBO, String devId) {
        DeviceRecord record = new DeviceRecord();
        record.setDevId(devId);
        record.setLiquidHeight((int) (recordBO.getX() * 100));

        double water = Double.parseDouble(recordBO.getWater());
        record.setLiquidPercent((int) (water * 100));
        record.setLiquidStatus(recordBO.getWS());
        record.setCreateTime(LocalDateTime.now());
        if (Objects.nonNull(currentRecord)) {
            record.setId(currentRecord.getId());
        }
        return record;
    }

}
