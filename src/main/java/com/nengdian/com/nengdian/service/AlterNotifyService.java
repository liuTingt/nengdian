package com.nengdian.com.nengdian.service;

import com.alibaba.fastjson.JSONObject;
import com.nengdian.com.nengdian.bo.MessageData;
import com.nengdian.com.nengdian.common.BizException;
import com.nengdian.com.nengdian.common.LiquidStatusEnum;
import com.nengdian.com.nengdian.dao.DeviceRepository;
import com.nengdian.com.nengdian.dao.NotifyRecordRepository;
import com.nengdian.com.nengdian.dao.UserDeviceRepository;
import com.nengdian.com.nengdian.entity.Device;
import com.nengdian.com.nengdian.entity.DeviceRecord;
import com.nengdian.com.nengdian.entity.NotifyRecord;
import com.nengdian.com.nengdian.entity.UserDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AlterNotifyService {
    private static final Logger logger = LoggerFactory.getLogger(AlterNotifyService.class);

    private static final String alarmTmp = "当前液位高度%s米，%s";
    @Resource
    private NotifyRecordRepository notifyRecordRepository;
    @Resource
    private UserDeviceRepository userDeviceRepository;
    @Resource
    private DeviceRepository deviceRepository;
    @Resource
    private WechatService wechatService;


    public void alertNotify(DeviceRecord currentRecord, DeviceRecord targetRecord) {
        try {
            String devId = currentRecord.getDevId();
            Integer currentStatus = currentRecord.getLiquidStatus();
            Integer targetStatus = targetRecord.getLiquidStatus();

            // 告警时间超过2小时，报警
            List<NotifyRecord> notifyRecords = notifyRecordRepository.findNotifyRecordByDevId(devId);
            Map<String, NotifyRecord> notifyMap = notifyRecords.stream()
                    .collect(Collectors.toMap(item -> item.getDevId()+item.getOpenid(), Function.identity(), (o, n)->o));

            List<UserDevice> userDevices = userDeviceRepository.findUserDeviceByDevId(devId);
            for (UserDevice userDevice: userDevices) {
                NotifyRecord notifyRecord = notifyMap.get(userDevice.getDevId() + userDevice.getOpenid());

                if (isNotify(notifyRecord, currentStatus, targetStatus)) {
                    Device device = deviceRepository.findByDevId(devId);

                    Map<String, MessageData> msg = buildWechatMsg(device, targetRecord);
                    boolean sendResult = wechatService.sendMessage(userDevice.getOpenid(), msg);

                    if (sendResult && Objects.isNull(notifyRecord)) {
                        notifyRecordRepository.save(buildNotifyRecord(userDevice.getOpenid(), userDevice.getDevId(), targetRecord.getCreateTime()));
                    }
                    if (sendResult && Objects.nonNull(notifyRecord)) {
                        notifyRecordRepository.update(userDevice.getOpenid(), userDevice.getDevId(), targetRecord.getCreateTime());
                    }
                }
            }
        } catch (BizException e) {
            logger.error("处理MQTT消息业务异常,record:{},targetRecord:{},error:{}",
                    JSONObject.toJSONString(currentRecord), JSONObject.toJSONString(targetRecord), e.getMsg());
        } catch (Exception e) {
            logger.error("告警通知异常,record:{},targetRecord:{}",
                    JSONObject.toJSONString(currentRecord), JSONObject.toJSONString(targetRecord), e);
        }
    }

    /**
     *
     * @param notifyRecord 通知记录
     * @param currentStatus 设备当前数据库中的状态
     * @param targetStatus 采集到的状态 这里一定是高液位、低液位、离线
     * @return true:通知   false：不通知
     */
    private boolean isNotify(NotifyRecord notifyRecord, Integer currentStatus, Integer targetStatus) {
        if (LiquidStatusEnum.Normal.getCode().equals(targetStatus)) {
            return false;
        }
        if (Objects.isNull(notifyRecord)) {
            return true;
        }
        if (!currentStatus.equals(targetStatus)) {
            return true;
        }
        LocalDateTime currentTime = LocalDateTime.now();
        return currentTime.minusHours(2).isAfter(notifyRecord.getNotifyTime());
    }

    private Map<String, MessageData> buildWechatMsg(Device device, DeviceRecord targetRecord) {
        Map<String, MessageData> map = new HashMap<>();
        map.put("thing2", new MessageData(device.getDevName()));
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        map.put("time4", new MessageData(targetRecord.getCreateTime().format(dateFormatter)));

        String status = LiquidStatusEnum.getStatusDesc(targetRecord.getLiquidStatus());
        double height = (double) targetRecord.getLiquidHeight() / 100;

        map.put("thing5", new MessageData(String.format(alarmTmp, height, status)));
        if (LiquidStatusEnum.Offline.getCode().equals(targetRecord.getLiquidStatus())) {
            map.put("thing5", new MessageData("设备已离线"));
        }
        return map;
    }

    private NotifyRecord buildNotifyRecord(String openid, String devId, LocalDateTime notifyTime) {
        NotifyRecord record = new NotifyRecord();
        record.setOpenid(openid);
        record.setDevId(devId);
        record.setNotifyTime(notifyTime);
        return record;
    }
}
