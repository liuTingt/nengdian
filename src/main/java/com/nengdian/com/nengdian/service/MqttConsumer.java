package com.nengdian.com.nengdian.service;

import com.alibaba.fastjson.JSONObject;
import com.nengdian.com.nengdian.bo.MessageData;
import com.nengdian.com.nengdian.bo.RecordBO;
import com.nengdian.com.nengdian.common.BizException;
import com.nengdian.com.nengdian.common.LiquidStatusEnum;
import com.nengdian.com.nengdian.dao.DeviceRecordRepository;
import com.nengdian.com.nengdian.dao.DeviceRepository;
import com.nengdian.com.nengdian.dao.UserDeviceRepository;
import com.nengdian.com.nengdian.dao.NotifyRecordRepository;
import com.nengdian.com.nengdian.entity.Device;
import com.nengdian.com.nengdian.entity.DeviceRecord;
import com.nengdian.com.nengdian.entity.UserDevice;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    @Resource
    private UserDeviceRepository userDeviceRepository;
    @Resource
    private WechatService wechatService;


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
            DeviceRecord record = buildRecord(recordBO, devId);
            recordRepository.save(record);

            if (recordBO.getWS() == LiquidStatusEnum.Low.getCode() || recordBO.getWS() == LiquidStatusEnum.Height.getCode()) {
                Device device = deviceRepository.findByDevId(devId);
                if (Objects.isNull(device)) {
                    logger.error("设备采集数据报警，未找到设备详情，devId:{}", devId);
                    return;
                }
                List<UserDevice> userDevices = userDeviceRepository.findUserDeviceByDevId(devId);
                for (UserDevice userDevice : userDevices) {
                    String openid = userDevice.getOpenid();
                    NotifyRecord notifyRecord = notifyRecordRepository.findLastByDevId(openid, devId);
                    if (isNotify(notifyRecord)) {
                        LocalDateTime notifyTime = LocalDateTime.now();
                        boolean sendResult = wechatService.sendMessage(openid, buildData(device, notifyTime, recordBO.getWS()));
                        if (sendResult) {
                            notifyRecordRepository.save(buildNotifyRecord(notifyRecord, openid, devId, notifyTime));
                        } else {
                            logger.error("发送用户告警消息失败,openid:{}, devId:{}", openid, userDevice.getDevId());
                        }
                    }
                }
            }
        } catch (BizException e) {
            logger.error("处理MQTT消息业务异常,record:{}", JSONObject.toJSON(message), e);
        } catch (Exception e) {
            logger.error("处理MQTT消息异常,record:{}", JSONObject.toJSON(message), e);
        }
    }

    private DeviceRecord buildRecord(RecordBO recordBO, String devId) {
        DeviceRecord record = new DeviceRecord();
        record.setDevId(devId);
        record.setLiquidHeight((int) (recordBO.getX() * 100));
        record.setLiquidPercent((int) (recordBO.getWater() * 100));
        record.setLiquidStatus(recordBO.getWS());
        record.setCreateTime(LocalDateTime.now());
        return record;
    }

    private NotifyRecord buildNotifyRecord(NotifyRecord notifyRecord, String openid, String devId, LocalDateTime notifyTime) {
        NotifyRecord record = new NotifyRecord();
        record.setOpenid(openid);
        record.setDevId(devId);
        record.setNotifyTime(notifyTime);
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

    private Map<String, MessageData> buildData(Device device, LocalDateTime time, int status) {
        Map<String, MessageData> map = new HashMap<>();
        map.put("thing2", new MessageData(device.getDevName()));
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        map.put("time4", new MessageData(time.format(dateFormatter)));
        String desc = LiquidStatusEnum.getStatusDesc(status);
        map.put("thing5", new MessageData(desc));
        return map;
    }

    public static void main(String[] args) {
        System.out.println(JSONObject.toJSONString(new MessageData("desc")));
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
