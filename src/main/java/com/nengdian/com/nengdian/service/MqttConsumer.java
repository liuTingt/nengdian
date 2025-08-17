package com.nengdian.com.nengdian.service;

import com.alibaba.fastjson.JSONObject;
import com.nengdian.com.nengdian.bo.RecordBO;
import com.nengdian.com.nengdian.dao.DeviceRecordRepository;
import com.nengdian.com.nengdian.dao.DeviceRepository;
import com.nengdian.com.nengdian.dao.NotifyRecordRepository;
import com.nengdian.com.nengdian.entity.Device;
import com.nengdian.com.nengdian.entity.DeviceRecord;
import com.nengdian.com.nengdian.entity.NotifyRecord;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Service
public class MqttConsumer {
    private static final Logger logger = LoggerFactory.getLogger(MqttConsumer.class);
    @Resource
    private DeviceRecordRepository recordRepository;
    @Resource
    private NotifyRecordRepository notifyRecordRepository;
    @Resource
    private DeviceRepository deviceRepository;


    public void consumer(Message message) {
        try {
            String payload = message.getPayload().toString();
            if (Strings.isBlank(payload)) {
                return;
            }
            RecordBO recordBO = JSONObject.parseObject(payload, RecordBO.class);
            String topic = "";
            if (!Objects.isNull(message.getHeaders().get(MqttHeaders.TOPIC))) {
                topic = message.getHeaders().get(MqttHeaders.TOPIC).toString();
            }
            if (!Objects.isNull(message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC))) {
                topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC).toString();
            }
            String devId = topic.split("/")[0];
            recordRepository.save(buildRecord(recordBO, devId));

            if (recordBO.getStatus() == 1 || recordBO.getStatus() == 3) {
                Device device = deviceRepository.findByDevId(devId);
                if (Objects.isNull(device)) {
                    logger.error("设备采集数据报警，未找到设备详情，devId:{}", devId);
                    return;
                }
                String openid = device.getOpenid();
                NotifyRecord notifyRecord = notifyRecordRepository.findLastByDevId(openid, devId);
                if (isNotify(notifyRecord)) {
                    notifyRecordRepository.save(buildNotifyRecord(notifyRecord, openid, devId));
                    // todo 通知用户公众号

                }
            }
        } catch (Exception e) {
            logger.error("处理MQTT消息异常,record:{}", JSONObject.toJSON(message), e);
        }
    }

    private DeviceRecord buildRecord(RecordBO recordBO, String devId) {
        DeviceRecord record = new DeviceRecord();
        record.setDevId(devId);
        record.setLiquidHeight((int) (recordBO.getX() * 100));
        record.setLiquidPercent((int) (recordBO.getPt() * 100));
        // todo 处理测试采集数据
        record.setLiquidStatus(recordBO.getStatus());
        if (recordBO.getStatus() == 0) {
            record.setLiquidStatus(2);
        }
        record.setCreateTime(LocalDateTime.now());
        return record;
    }

    private NotifyRecord buildNotifyRecord(NotifyRecord notifyRecord, String openid, String devId) {
        NotifyRecord record = new NotifyRecord();
        notifyRecord.setOpenid(openid);
        notifyRecord.setDevId(devId);
        notifyRecord.setNotifyTime(LocalDateTime.now());
        if (Objects.nonNull(notifyRecord)) {
            notifyRecord.setId(notifyRecord.getId());
        }
        return record;
    }

    private boolean isNotify(NotifyRecord notifyRecord) {
        if (Objects.isNull(notifyRecord)) {
            return true;
        }
        LocalDateTime currentTime = LocalDateTime.now();
        return currentTime.minusHours(2).isAfter(notifyRecord.getNotifyTime());
    }

    public static void main(String[] args) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime currentTime = LocalDateTime.now();
        System.out.println("当前："+currentTime.format(dateFormatter));

        LocalDateTime befor = currentTime.minusHours(1).minusMinutes(10);
        System.out.println("记录时间"+befor.format(dateFormatter));

        LocalDateTime cur =currentTime.minusHours(2);
        System.out.println("计划时间"+cur.format(dateFormatter));

        System.out.println(cur.isAfter(befor));
    }
}
