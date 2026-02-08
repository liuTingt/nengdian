package com.nengdian.com.nengdian.service;

import com.nengdian.com.nengdian.common.DeviceTypeEnum;
import com.nengdian.com.nengdian.common.LiquidStatusEnum;
import com.nengdian.com.nengdian.dao.DeviceRecordRepository;
import com.nengdian.com.nengdian.entity.DeviceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Component
@EnableScheduling
public class ScheduledService {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledService.class);

    @Resource
    private DeviceRecordRepository deviceRecordRepository;
    @Resource
    private AlterNotifyService alterNotifyService;


    @Scheduled(fixedRate = 1 * 60 * 1000)
    public void deviceOffline() {
        try {
            StopWatch stopWatch = new StopWatch("crone");
            stopWatch.start("task");

            List<DeviceRecord> recordList = deviceRecordRepository.findByType(DeviceTypeEnum.ELECTRIC.getType());
            for (DeviceRecord record: recordList) {
                if (!record.isOffline()) {
                    continue;
                }
                // 更新设备状态，用于提醒
                if (!LiquidStatusEnum.Offline.getCode().equals(record.getLiquidStatus())) {
                    deviceRecordRepository.offline(record.getDevId());
                }
                alterNotifyService.alertNotify(record, buildRecordMsg());
            }

            stopWatch.stop();
            logger.info("定时任务耗时：{}", stopWatch.getTotalTimeMillis());
        } catch (Exception e) {
            logger.error("定时任务异常", e);
        }
    }

    private DeviceRecord buildRecordMsg() {
        DeviceRecord targetRecord = new DeviceRecord();
        targetRecord.setLiquidStatus(LiquidStatusEnum.Offline.getCode());
        targetRecord.setCreateTime(LocalDateTime.now());
        targetRecord.setLiquidHeight(0);
        return targetRecord;
    }
}
