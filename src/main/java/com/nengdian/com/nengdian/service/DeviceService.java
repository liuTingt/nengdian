package com.nengdian.com.nengdian.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.nengdian.com.nengdian.ao.QueryDeviceAO;
import com.nengdian.com.nengdian.ao.QueryDevicePageAO;
import com.nengdian.com.nengdian.ao.SettingAO;
import com.nengdian.com.nengdian.common.BizException;
import com.nengdian.com.nengdian.common.LiquidStatusEnum;
import com.nengdian.com.nengdian.common.ResultCodeEnum;
import com.nengdian.com.nengdian.controller.UserController;
import com.nengdian.com.nengdian.dao.DeviceRecordRepository;
import com.nengdian.com.nengdian.dao.DeviceRepository;
import com.nengdian.com.nengdian.dao.UserDeviceRepository;
import com.nengdian.com.nengdian.dao.UserRepository;
import com.nengdian.com.nengdian.entity.Device;
import com.nengdian.com.nengdian.entity.DeviceRecord;
import com.nengdian.com.nengdian.entity.UserDevice;
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
import java.util.*;
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
    private UserDeviceRepository userDeviceRepository;
    @Resource
    private MqttPublish mqttPublish;


    public DevCountVO getDeviceCount(String openid) {
        User user = userRepository.findByOpenid(openid);
        if (Objects.isNull(user)) {
            throw new BizException(ResultCodeEnum.NOT_FIND_USER);
        }
        List<UserDevice> userDevices = userDeviceRepository.findUserDeviceByOpenid(openid);

        Set<String> deviceIds = userDevices.stream()
                .map(UserDevice::getDevId)
                .collect(Collectors.toSet());

        List<DeviceRecord> deviceRecords = findLatestByDeviceIds(deviceIds);
        if (CollectionUtils.isEmpty(deviceRecords)) {
            logger.warn("getDeviceCount 设备采集记录为空, openid:{}", openid);
            return new DevCountVO(0, 0, user.getLanguage());
        }
        int alarmCount = 0;
        int normalCount = 0;
        Map<String, DeviceRecord> deviceRecordMap = deviceRecords.stream()
                .collect(Collectors.toMap(DeviceRecord::getDevId, Function.identity(), (o,n) -> n));
        for (UserDevice userDevice : userDevices) {
            DeviceRecord record = deviceRecordMap.get(userDevice.getDevId());
            if (Objects.isNull(record)) {
                continue;
            }
            normalCount++;
            if (Objects.isNull(record.getLiquidStatus()) ||
                    !LiquidStatusEnum.Normal.getCode().equals(record.getLiquidStatus())) {
                alarmCount++;
            }
        }
        return new DevCountVO(normalCount, alarmCount, user.getLanguage());
    }

    @Transactional
    public Device create(Device device) {
        Device result = null;
        try {
            String lowDevId = device.getDevId().replace(":","").toLowerCase();
            device.setDevId(lowDevId);
            device.setCreateTime(new Date());

            DeviceRecord deviceRecord = deviceRecordRepository.findDeviceRecordByDevId(device.getDevId());
            if (Objects.isNull(deviceRecord)) {
                throw new BizException(ResultCodeEnum.NOT_FIND_DEVICE_RECORD);
            }

            Device currentDevice = deviceRepository.findByDevId(device.getDevId());
            if (Objects.isNull(currentDevice)) {
                if (Strings.isBlank(device.getDevName())) {
                    long count = userDeviceRepository.countByOpenid(device.getOpenid());
                    device.setDevName("水箱"+ ++count);
                }
                logger.info("save data:{}", JSONObject.toJSON(device));
                result = deviceRepository.save(device);
            } else {
                UserDevice currentUserDevice = userDeviceRepository.findUserDeviceByDevIdAndOpenid(device.getDevId(), device.getOpenid());
                if (Objects.nonNull(currentUserDevice)) {
                    throw new BizException(ResultCodeEnum.DEVICE_HAS_EXIST);
                }
                if (Strings.isBlank(device.getDevName())) {
                    device.setDevName(currentDevice.getDevName());
                }
                result = currentDevice;
            }

            UserDevice userDevice = new UserDevice();
            userDevice.setOpenid(device.getOpenid());
            userDevice.setDevId(device.getDevId());
            userDevice.setCreateTime(LocalDateTime.now());
            userDeviceRepository.save(userDevice);

            return result;
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            logger.error("create device error, device:{}", JSONObject.toJSON(device), e);
        }
        throw new BizException(ResultCodeEnum.SAVE_DEVICE_ERROR);
    }

    @Transactional
    public boolean update(Device device) {
        try {
            UserDevice userDevice = userDeviceRepository.findUserDeviceByDevIdAndOpenid(device.getDevId(), device.getOpenid());
            if (Objects.isNull(userDevice)) {
                logger.error("用户没有设备修改权限");
                return false;
            }
            int row = deviceRepository.updateDeviceName(device.getDevId(), device.getDevName());
            return row > 0;
        } catch (Exception e) {
            logger.error("update deviceName error", e);
        }
        return false;
    }

    public boolean delete(Device device) {
        try {
            userDeviceRepository.deleteUserDeviceByDevIdAndOpenid(device.getDevId(), device.getOpenid());
            return true;
        } catch (Exception e) {
            logger.error("delete device error", e);
        }
        return false;
    }

    public void setting(SettingAO request) {
        UserDevice userDevice = userDeviceRepository.findUserDeviceByDevIdAndOpenid(request.getDevId(), request.getOpenid());
        if (Objects.isNull(userDevice)) {
            throw new BizException(ResultCodeEnum.NOT_FIND_USER_DEVICE);
        }
        Device currentDevice = deviceRepository.findByDevIdAndDeleted(request.getDevId(),false);
        if (Objects.isNull(currentDevice)) {
            throw new BizException(ResultCodeEnum.NOT_FIND_DEVICE);
        }
        currentDevice.setInstallHeight(request.getInstallHeight());
        currentDevice.setDistance(request.getDistance());
        currentDevice.setUpperLimit(request.getUpperLimit());
        currentDevice.setLowerLimit(request.getLowerLimit());
        currentDevice.setLowEnergySwitch(request.isLowEnergySwitch());
        currentDevice.setDrainageModel(request.isDrainageModel());
        currentDevice.setModifyTime(new Date());
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
        List<UserDevice> userDevices = userDeviceRepository.findUserDeviceByOpenid(request.getOpenid());
        if (CollectionUtils.isEmpty(userDevices)) {
            logger.warn("未找到用户设备");
            return Lists.newArrayList();
        }

        List<String> devIds = userDevices.stream().map(UserDevice::getDevId).collect(Collectors.toList());

        List<Device> devices = deviceRepository.findAll(deviceSpecification(request, devIds));
        if (CollectionUtils.isEmpty(devices)) {
            throw new BizException(ResultCodeEnum.NOT_FIND_DEVICE);
        }

        Set<String> deviceIds = devices.stream()
                .map(Device::getDevId)
                .collect(Collectors.toSet());
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
                vo.setStatus(record.getStatus());
                result.add(vo);
            }
        }
        return result;
    }

    public DeviceSettingVO getDetail(Device request) {
        UserDevice record = userDeviceRepository.findUserDeviceByDevIdAndOpenid(request.getDevId(), request.getOpenid());
        if (Objects.isNull(record)) {
            throw new BizException(ResultCodeEnum.NOT_FIND_USER_DEVICE);
        }
        User user = userRepository.findByOpenid(request.getOpenid());
        if (Objects.isNull(user)) {
            throw new BizException(ResultCodeEnum.NOT_FIND_USER);
        }
        Device device = deviceRepository.findByDevIdAndDeleted(request.getDevId(), false);
        if (Objects.isNull(device)) {
            throw new BizException(ResultCodeEnum.NOT_FIND_DEVICE);
        }
        return buildDeviceDetail(device, user);
    }

    public DeviceLiquidLevelVO getLiquidLevel(Device request) {
        UserDevice userDevice = userDeviceRepository.findUserDeviceByDevIdAndOpenid(request.getDevId(), request.getOpenid());
        if (Objects.isNull(userDevice)) {
            throw new BizException(ResultCodeEnum.NOT_FIND_USER_DEVICE);
        }
        Device device = deviceRepository.findByDevIdAndDeleted(request.getDevId(), false);
        if (Objects.isNull(device)) {
            throw new BizException(ResultCodeEnum.NOT_FIND_DEVICE);
        }

        List<DeviceRecord> records = findLatestByDeviceIds(Sets.newHashSet(request.getDevId()));
        if (CollectionUtils.isEmpty(records)) {
            throw new BizException(ResultCodeEnum.NOT_FIND_DEVICE_RECORD);
        }
        DeviceLiquidLevelVO result = new DeviceLiquidLevelVO();
        result.setDevId(device.getDevId());
        result.setDevName(device.getDevName());
        result.setLiquidHeight(records.get(0).getLiquidHeight());
        result.setLiquidPercent(records.get(0).getLiquidPercent());
        result.setLiquidStatus(records.get(0).getLiquidStatus());
        result.setStatus(records.get(0).getStatus());
        return result;
    }

    public List<DeviceAvgLiquidLevelVO> getDeviceRecordList(Device request) {
        UserDevice userDevice = userDeviceRepository.findUserDeviceByDevIdAndOpenid(request.getDevId(), request.getOpenid());
        if (Objects.isNull(userDevice)) {
            throw new BizException(ResultCodeEnum.NOT_FIND_USER_DEVICE);
        }
        Device device = deviceRepository.findByDevIdAndDeleted(request.getDevId(), false);
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

    public Page<Device> pageList(QueryDevicePageAO request, List<String> devIds) {
        Specification<Device> specification = new Specification<Device>() {
            @Override
            public Predicate toPredicate(Root<Device> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = Lists.newArrayList();
                if (!CollectionUtils.isEmpty(devIds)) {
                    predicates.add(cb.in(root.get("devId")).value(devIds));
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

    public List<DeviceRecord> findLatestByDeviceIds(Set<String> deviceIds) {
        return deviceRecordRepository.findByDeviceIds(deviceIds);
//        List<DeviceRecord> list = Lists.newArrayList();
//        for (String deviceId: deviceIds) {
//            DeviceRecord record = deviceRecordRepository.findLatestByDeviceId(deviceId);
//            if (Objects.nonNull(record)) {
//                list.add(record);
//            }
//        }
//        return list;
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

    private Specification<Device> deviceSpecification(QueryDeviceAO request, List<String> devIds) {
        return new Specification<Device>() {
            @Override
            public Predicate toPredicate(Root<Device> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = Lists.newArrayList();

                predicates.add(cb.in(root.get("devId")).value(devIds));

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
