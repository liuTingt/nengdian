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

    private static final String alarmTmp = "当前液位高度%s米，%s";

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
            DeviceRecord currentRecord = recordRepository.findDeviceRecordByDevId(devId);
            Integer currentStatus = Objects.nonNull(currentRecord) ? currentRecord.getLiquidStatus() : null;

            DeviceRecord messageRecord = buildRecord(currentRecord, recordBO, devId);
            recordRepository.save(messageRecord);

            if (recordBO.getWS() == LiquidStatusEnum.Low.getCode() || recordBO.getWS() == LiquidStatusEnum.Height.getCode()) {
                boolean isNotifyOfStatus = isNotifyOfStatus(currentStatus, messageRecord.getLiquidStatus());

                Device device = deviceRepository.findByDevId(devId);
                if (Objects.isNull(device)) {
                    logger.error("设备采集数据报警，未找到设备详情，devId:{}", devId);
                    return;
                }

                LocalDateTime notifyTime = LocalDateTime.now();
                List<UserDevice> userDevices = userDeviceRepository.findUserDeviceByDevId(devId);
                for (UserDevice userDevice : userDevices) {
                    String openid = userDevice.getOpenid();
                    boolean isNotify = isNotifyOfStatus;

                    if (!isNotifyOfStatus) {
                        NotifyRecord notifyRecord = notifyRecordRepository.findNotifyRecordByDevIdAndOpenid(devId, openid);
                        isNotify = isNotify(notifyRecord);
                    }

                    if (isNotify) {
                        boolean sendResult = wechatService.sendMessage(openid, buildData(device, notifyTime, messageRecord));
                        if (sendResult) {
                            notifyRecordRepository.update(openid, devId, notifyTime);
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

    private DeviceRecord buildRecord(DeviceRecord currentRecord, RecordBO recordBO, String devId) {
        DeviceRecord record = new DeviceRecord();
        record.setDevId(devId);
        record.setLiquidHeight((int) (recordBO.getX() * 100));
        record.setLiquidPercent((int) (recordBO.getWater() * 100));
        record.setLiquidStatus(recordBO.getWS());
        record.setCreateTime(LocalDateTime.now());
        if (Objects.nonNull(currentRecord)) {
            record.setId(currentRecord.getId());
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

    /**
     * 采集设备状态（正常液位除外）和上次不一致就报警，如果一致且采集到的是低液位或者高液位，2小时内不报警，超过2小时需要报警
     * @param currentStatus 数据库当前状态
     * @param messageStatus 采集到的状态
     * @return true:告警，false：是否告警需要再查看上次通知是否在2小时内
     */
    private boolean isNotifyOfStatus(Integer currentStatus, Integer messageStatus) {
        if (Objects.isNull(currentStatus) || !currentStatus.equals(messageStatus)) {
            return true;
        }
        return false;
    }

    private Map<String, MessageData> buildData(Device device, LocalDateTime time, DeviceRecord messageRecord) {
        Map<String, MessageData> map = new HashMap<>();
        map.put("thing2", new MessageData(device.getDevName()));
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        map.put("time4", new MessageData(time.format(dateFormatter)));

        String status = LiquidStatusEnum.getStatusDesc(messageRecord.getLiquidStatus());
        double height = (double) messageRecord.getLiquidHeight() / 100;

        map.put("thing5", new MessageData(String.format(alarmTmp, height, status)));
        return map;
    }

    public static void main(String[] args) {
        String status = LiquidStatusEnum.getStatusDesc(1);
        double height = (double) 1200 / 100;
        System.out.println(String.format(alarmTmp, height, status));

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
