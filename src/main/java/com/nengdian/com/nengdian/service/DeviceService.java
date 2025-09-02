package com.nengdian.com.nengdian.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.nengdian.com.nengdian.ao.QueryDeviceAO;
import com.nengdian.com.nengdian.ao.QueryDevicePageAO;
import com.nengdian.com.nengdian.ao.SettingAO;
import com.nengdian.com.nengdian.common.BizException;
import com.nengdian.com.nengdian.common.LiquidStatusEnum;
import com.nengdian.com.nengdian.common.ResultCodeEnum;
import com.nengdian.com.nengdian.controller.UserController;
import com.nengdian.com.nengdian.dao.DeviceRecordRepository;
import com.nengdian.com.nengdian.dao.DeviceRepository;
import com.nengdian.com.nengdian.dao.UserRepository;
import com.nengdian.com.nengdian.entity.Device;
import com.nengdian.com.nengdian.entity.DeviceRecord;
import com.nengdian.com.nengdian.entity.User;
import com.nengdian.com.nengdian.vo.*;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DeviceService {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Resource
    private DeviceRepository deviceRepository;
    @Resource
    private DeviceRecordRepository deviceRecordRepository;
    @Resource
    private UserRepository userRepository;
    @Resource
    private MqttPublish mqttPublish;


    public DevCountVO getDeviceCount(String openid) {
        List<Device> devices = deviceRepository.findByOpenidAndDeleted(openid, false);
        if (CollectionUtils.isEmpty(devices)) {
            new BizException(ResultCodeEnum.NOT_FIND_DEVICE);
        }

        List<String> deviceIds = devices.stream()
                .map(Device::getDevId)
                .collect(Collectors.toList());

        List<DeviceRecord> deviceRecords = findLatestByDeviceIds(deviceIds);
        if (CollectionUtils.isEmpty(deviceRecords)) {
            logger.warn("getDeviceCount 设备采集记录为空, openid:{}", openid);
            return new DevCountVO(0, 0);
        }
        int alarmCount = 0;
        int normalCount = 0;
        Map<String, Integer> deviceRecordMap = deviceRecords.stream().collect(Collectors.toMap(DeviceRecord::getDevId, DeviceRecord::getLiquidStatus));
        for (Device device : devices) {
            Integer status = deviceRecordMap.get(device.getDevId());
            if (Objects.nonNull(status) && LiquidStatusEnum.Normal.getCode().equals(status)) {
                normalCount++;
            }
            if (Objects.nonNull(status) &&
                    (LiquidStatusEnum.Low.getCode().equals(status) || LiquidStatusEnum.Height.getCode().equals(status))) {
                alarmCount++;
            }
        }
        return new DevCountVO(normalCount, alarmCount);
    }

    @Transactional
    public Device create(Device device) {
        try {
            if (Strings.isBlank(device.getDevName())) {
                long count = deviceRepository.countByOpenidAndDeleted(device.getOpenid(), false);
                device.setDevName("水箱"+ ++count);
            }
            String lowDevId = device.getDevId().replace(":","").toLowerCase();
            device.setDevId(lowDevId);

            Device currentDevice = deviceRepository.findByDevId(device.getDevId());
            if (Objects.nonNull(currentDevice)) {
                throw new BizException(ResultCodeEnum.DEVICE_HAS_EXIST);
            }

            logger.info("save data:{}", JSONObject.toJSON(device));
            Device result = deviceRepository.save(device);
            if (Objects.nonNull(result)) {
                return result;
            }
            throw new BizException(ResultCodeEnum.SAVE_DEVICE_ERROR);
        } catch (Exception e) {
            logger.error("create device error, device:{}", JSONObject.toJSON(device), e);
            throw e;
        }
    }

    @Transactional
    public boolean update(Device device) {
        try {
            int row = deviceRepository.updateDeviceName(device.getOpenid(), device.getDevId(), device.getDevName());
            return row > 0;
        } catch (Exception e) {
            logger.error("update deviceName error", e);
        }
        return false;
    }

    public boolean delete(Device device) {
        int row = deviceRepository.deleted(device.getOpenid(), device.getDevId());
        return row > 0;
    }

    public void setting(SettingAO request) {
        Device currentDevice = deviceRepository.findByOpenidAndDevIdAndDeleted(request.getOpenid(), request.getDevId(),false);
        if (Objects.isNull(currentDevice)) {
            throw new BizException(ResultCodeEnum.NOT_FIND_DEVICE);
        }
        currentDevice.setInstallHeight(request.getInstallHeight());
        currentDevice.setDistance(request.getDistance());
        currentDevice.setUpperLimit(request.getUpperLimit());
        currentDevice.setLowerLimit(request.getLowerLimit());
        currentDevice.setLowEnergySwitch(request.isLowEnergySwitch());
        currentDevice.setDrainageModel(request.isDrainageModel());
        Device result = deviceRepository.save(currentDevice);

        User currentUser = userRepository.findByOpenid(request.getOpenid());
        if (Objects.isNull(currentUser)) {
            throw new BizException(ResultCodeEnum.NOT_FIND_USER);
        }
        currentUser.setLanguage(request.getLanguage());
        currentUser.setRemindSwitch(request.isRemindSwitch());
        userRepository.save(currentUser);

        mqttPublish.publish(request);
    }

    public List<DeviceDetailVO> queryList(QueryDeviceAO request) {
        List<Device> devices = deviceRepository.findAll(deviceSpecification(request));
        if (CollectionUtils.isEmpty(devices)) {
            throw new BizException(ResultCodeEnum.NOT_FIND_DEVICE);
        }

        List<String> deviceIds = devices.stream()
                .map(Device::getDevId)
                .collect(Collectors.toList());
        List<DeviceRecord> deviceRecords = findLatestByDeviceIds(deviceIds);

        if (CollectionUtils.isEmpty(deviceRecords)) {
            throw new BizException(ResultCodeEnum.NOT_FIND_DEVICE_RECORD);
        }

        if (!CollectionUtils.isEmpty(request.getStatusList())) {
            deviceRecords = deviceRecords.stream()
                    .filter(record -> request.getStatusList().contains(record.getLiquidStatus()))
                    .collect(Collectors.toList());
        }

        List<DeviceDetailVO> result = Lists.newArrayList();
        Map<String, DeviceRecord> deviceRecordMap = deviceRecords.stream()
                .collect(Collectors.toMap(DeviceRecord::getDevId, Function.identity(), (o,n) -> n));
        for (Device device : devices) {
            DeviceRecord record = deviceRecordMap.get(device.getDevId());
            if (Objects.nonNull(record)) {
                DeviceDetailVO vo = new DeviceDetailVO(device);
                vo.setLiquidPercent(record.getLiquidPercent());
                result.add(vo);
            }
        }
        return result;
    }

    public DeviceSettingVO getDetail(Device request) {
        User user = userRepository.findByOpenid(request.getOpenid());
        if (Objects.isNull(user)) {
            throw new BizException(ResultCodeEnum.NOT_FIND_USER);
        }
        Device device = deviceRepository.findByOpenidAndDevIdAndDeleted(request.getOpenid(), request.getDevId(), false);
        if (Objects.isNull(device)) {
            throw new BizException(ResultCodeEnum.NOT_FIND_DEVICE);
        }
        return buildDeviceDetail(device, user);
    }

    public DeviceLiquidLevelVO getLiquidLevel(Device request) {
        Device device = deviceRepository.findByOpenidAndDevIdAndDeleted(request.getOpenid(), request.getDevId(), false);
        if (Objects.isNull(device)) {
            throw new BizException(ResultCodeEnum.NOT_FIND_DEVICE);
        }
        List<DeviceRecord> records = findLatestByDeviceIds(Lists.newArrayList(request.getDevId()));
        if (CollectionUtils.isEmpty(records)) {
            throw new BizException(ResultCodeEnum.NOT_FIND_DEVICE_RECORD);
        }
        DeviceLiquidLevelVO result = new DeviceLiquidLevelVO();
        result.setDevId(device.getDevId());
        result.setDevName(device.getDevName());
        result.setLiquidHeight(records.get(0).getLiquidHeight());
        result.setLiquidPercent(records.get(0).getLiquidPercent());
        result.setLiquidStatus(records.get(0).getLiquidStatus());
        return result;
    }

    public List<DeviceAvgLiquidLevelVO> getDeviceRecordList(Device request) {
        Device device = deviceRepository.findByOpenidAndDevIdAndDeleted(request.getOpenid(), request.getDevId(), false);
        if (Objects.isNull(device)) {
            throw new BizException(ResultCodeEnum.NOT_FIND_DEVICE);
        }
        LocalDateTime threeDaysAgo = LocalDateTime.now()
                .minusDays(3)
                .withHour(0)
                .withMinute(0)
                .withSecond(0);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM.dd");
        List<DeviceAvgLiquidLevelVO> result = Lists.newArrayList();
        LocalDateTime startTime = threeDaysAgo;
        for (int i = 0; i < 3; i++) {
            LocalDateTime endTime = startTime.plusDays(1);
            Tuple record = deviceRecordRepository.findAvgDeviceRecord(device.getDevId(), startTime.format(dateFormatter), endTime.format(dateFormatter));
            if (Objects.isNull(record)) {
                throw new BizException(ResultCodeEnum.FIND_RECORD_ERROR);
            }

            DeviceAvgLiquidLevelVO recordVO = new DeviceAvgLiquidLevelVO();
            recordVO.setDevId(device.getDevId());
            if (Objects.nonNull(record.get("liquidHeight"))) {
                recordVO.setAvgLiquid(record.get("liquidHeight").toString());
            }
            recordVO.setCreateTime(startTime.format(formatter));
            result.add(recordVO);

            startTime = endTime;
        }
        return result;
    }

    public Page<Device> pageList(QueryDevicePageAO request) {
        Specification<Device> specification = new Specification<Device>() {
            @Override
            public Predicate toPredicate(Root<Device> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = Lists.newArrayList();
                if (Strings.isNotBlank(request.getOpenid())) {
                    predicates.add(cb.equal(root.get("openid"), request.getOpenid()));
                }
                if (Strings.isNotBlank(request.getDevId())) {
                    predicates.add(cb.like(root.get("devId"), "%" + request.getDevId() + "%"));
                }
                if (Strings.isNotBlank(request.getDevName())) {
                    predicates.add(cb.like(root.get("devName"), "%" + request.getDevName() + "%"));
                }
                predicates.add(cb.equal(root.get("deleted"), false));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        Page<Device> page = deviceRepository.findAll(specification, PageRequest.of(request.getPageNum(), request.getPageSize()));
        return page;
    }

    public List<DeviceRecord> findLatestByDeviceIds(List<String> deviceIds) {
        List<DeviceRecord> list = Lists.newArrayList();
        for (String deviceId: deviceIds) {
            DeviceRecord record = deviceRecordRepository.findLatestByDeviceId(deviceId);
            if (Objects.nonNull(record)) {
                list.add(record);
            }
        }
        return list;
    }

    private DeviceSettingVO buildDeviceDetail(Device device, User user) {
        DeviceSettingVO deviceDetail = new DeviceSettingVO();
        deviceDetail.setDevId(device.getDevId());
        deviceDetail.setOpenid(device.getOpenid());
        deviceDetail.setDevName(device.getDevName());
        deviceDetail.setType(device.getType());
        deviceDetail.setInstallHeight(device.getInstallHeight());
        deviceDetail.setDistance(device.getDistance());
        deviceDetail.setUpperLimit(device.getUpperLimit());
        deviceDetail.setLowerLimit(device.getLowerLimit());
        deviceDetail.setLowEnergySwitch(device.isLowEnergySwitch());
        deviceDetail.setDrainageModel(device.isDrainageModel());
        deviceDetail.setLanguage(user.getLanguage());
        deviceDetail.setRemindSwitch(user.isRemindSwitch());
        return deviceDetail;
    }

    private Specification<Device> deviceSpecification(QueryDeviceAO request) {
        return new Specification<Device>() {
            @Override
            public Predicate toPredicate(Root<Device> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = Lists.newArrayList();
                if (Strings.isNotBlank(request.getOpenid())) {
                    predicates.add(cb.like(root.get("openid"), request.getOpenid()));
                }
                if (Strings.isNotBlank(request.getDevId())) {
                    predicates.add(cb.like(root.get("devId"), "%" + request.getDevId() + "%"));
                }
                if (Strings.isNotBlank(request.getDevName())) {
                    predicates.add(cb.like(root.get("devName"), "%" + request.getDevName() + "%"));
                }
                if (!Objects.isNull(request.getType())) {
                    predicates.add(cb.equal(root.get("type"), request.getType()));
                }
                predicates.add(cb.equal(root.get("deleted"), false));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };

    }
}
