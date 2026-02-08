package com.nengdian.com.nengdian.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.nengdian.com.nengdian.bo.RecordBO;
import com.nengdian.com.nengdian.common.BizException;
import com.nengdian.com.nengdian.common.DeviceTypeEnum;
import com.nengdian.com.nengdian.common.LiquidStatusEnum;
import com.nengdian.com.nengdian.dao.DeviceRecordRepository;
import com.nengdian.com.nengdian.dao.DeviceRepository;
import com.nengdian.com.nengdian.entity.Device;
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
    @Resource
    private DeviceRepository deviceRepository;


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
            if (!isLegal(devId, message)) {
                return;
            }
            RecordBO recordBO = JSONObject.parseObject(message.getPayload().toString(), RecordBO.class);
            long startTime = System.currentTimeMillis();

            logger.info("处理MQTT消息,devId:{},record:{}", devId, JSONObject.toJSON(recordBO));

            updateDeviceSetting(devId, recordBO);

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
            logger.error("处理MQTT消息业务异常,record:{},error:{}", JSONObject.toJSON(message), e.getMsg());
        } catch (Exception e) {
            logger.error("处理MQTT消息异常,record:{}", JSONObject.toJSON(message), e);
        }
    }

    private void updateDeviceSetting(String devId, RecordBO recordBO) {
        try {
            Device device = deviceRepository.findByDevId(devId);

            int distance = (int) Math.round(recordBO.getF() * 100L);
            int installHeight = (int) Math.round(recordBO.getI() * 100L);

            if (!device.getUpperLimit().equals(recordBO.getSX()) ||
                    !device.getLowerLimit().equals(recordBO.getXX()) ||
                    !device.getDistance().equals(distance) ||
                    !device.getInstallHeight().equals(installHeight) ||
                    isSunUpdate(device, recordBO)) {
                device.setUpperLimit(recordBO.getSX());
                device.setLowerLimit(recordBO.getXX());
                device.setDistance(distance);
                device.setInstallHeight(installHeight);
                device.setCheckPeriod(recordBO.getU());

                deviceRepository.save(device);
            }
        } catch (Exception e) {
            logger.error("更新设备参数异常", e);
        }
    }
    private boolean isLegal(String devId, Message message) {
        String payload = message.getPayload().toString();
        if (Strings.isBlank(payload) || stringPayload.equals(payload)) {
            return false;
        }

        JSONObject jsonObject = JSONObject.parseObject(payload, JSONObject.class);
        // 测试设备保证有water字段
        if (testDevices.contains(devId) && Objects.nonNull(jsonObject.get("water"))) {
            return true;
        }
        if (Objects.isNull(jsonObject.get("NET")) || Objects.isNull(jsonObject.get("F")) ||
                Objects.isNull(jsonObject.get("water")) || Objects.isNull(jsonObject.get("I")) ||
                !"4G".equals(jsonObject.get("NET").toString())) {
            return false;
        }
        return true;
    }

    private DeviceRecord buildRecord(DeviceRecord currentRecord, RecordBO recordBO, String devId) {
        DeviceRecord record = new DeviceRecord();
        record.setType(DeviceTypeEnum.ELECTRIC.getType());
        record.setDevId(devId);
        record.setLiquidHeight((int) (recordBO.getX() * 100));

//        double water = Double.parseDouble(recordBO.getWater());
        record.setLiquidPercent((int) (recordBO.getWater() * 100));
        record.setLiquidStatus(recordBO.getWS());
        record.setCreateTime(LocalDateTime.now());
        if (Objects.nonNull(currentRecord)) {
            record.setId(currentRecord.getId());
        }
        // 太阳能款，设置电量
        if (Objects.nonNull(recordBO.getB())) {
            record.setType(DeviceTypeEnum.SOLAR_ENERGY.getType());
            record.setPowerLevel(recordBO.getB());
            record.setStart(recordBO.getStart());
        }
        return record;
    }

    private boolean isSunUpdate(Device device, RecordBO recordBO) {
        return device.getType().equals(DeviceTypeEnum.SOLAR_ENERGY.getType()) && !device.getCheckPeriod().equals(recordBO.getU());
    }
}
