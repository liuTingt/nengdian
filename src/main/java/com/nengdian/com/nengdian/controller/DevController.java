package com.nengdian.com.nengdian.controller;

import com.alibaba.fastjson.JSONObject;
import com.nengdian.com.nengdian.ao.QueryDeviceAO;
import com.nengdian.com.nengdian.ao.SettingAO;
import com.nengdian.com.nengdian.common.BizException;
import com.nengdian.com.nengdian.common.ResultCodeEnum;
import com.nengdian.com.nengdian.common.ResultResponse;
import com.nengdian.com.nengdian.entity.Device;
import com.nengdian.com.nengdian.service.DeviceService;
import com.nengdian.com.nengdian.vo.*;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/dev")
public class DevController {
    private static final Logger logger = LoggerFactory.getLogger(DevController.class);

    @Resource
    private DeviceService deviceService;


    @GetMapping("/count")
    @ResponseBody
    public ResultResponse<DevCountVO> count(@Valid @Param("openid") String openid) {
        if (Strings.isBlank(openid)) {
            return ResultResponse.failed(ResultCodeEnum.PARAM_ERROR.getCode(), "openid不能为空");
        }
        try {
            DevCountVO devCountVO = deviceService.getDeviceCount(openid);
            return ResultResponse.success(devCountVO);
        } catch (BizException e) {
            logger.error("count biz error, openid:{}", openid, e);
            return ResultResponse.failed(e);
        } catch (Exception e) {
            logger.error("count error, openid:{}", openid, e);
            return ResultResponse.failed();
        }
    }

    @PostMapping("/create")
    @ResponseBody
    public ResultResponse<Device> create(@Valid @RequestBody Device device) {
        try {
            logger.info("新增设备,devId:{},openid:{}", device.getDevId(), device.getOpenid());
            Device result = deviceService.create(device);
            return ResultResponse.success(result);
        } catch (BizException e) {
            logger.error("create biz error, request:{}", JSONObject.toJSONString(device), e);
            return ResultResponse.failed(e);
        } catch (Exception e) {
            logger.error("create error, request:{}", JSONObject.toJSONString(device), e);
            return ResultResponse.failed();
        }
    }

    @PostMapping("/update")
    @ResponseBody
    public ResultResponse<Void> update(@RequestBody Device device) {
        if (deviceService.update(device)) {
            return ResultResponse.success();
        }
        return ResultResponse.failed();
    }

    @PostMapping("/delete")
    @ResponseBody
    public ResultResponse<Void> delete(@RequestBody Device device) {
        try {
            if (deviceService.delete(device)) {
                return ResultResponse.success();
            }
            return ResultResponse.failed();
        } catch (Exception e) {
            logger.error("delete error,device:{}", JSONObject.toJSON(device));
        }
        return ResultResponse.failed();
    }

    @PostMapping("/setting")
    @ResponseBody
    public ResultResponse<Void> setting(@RequestBody SettingAO request) {
        try {
            deviceService.setting(request);
            return ResultResponse.success();
        } catch (BizException e) {
            logger.error("setting biz error, request:{}", JSONObject.toJSON(request), e);
            return ResultResponse.failed(e);
        } catch (Exception e) {
            logger.error("setting error, request:{}", JSONObject.toJSON(request), e);
            return ResultResponse.failed();
        }
    }

    @PostMapping("/list")
    @ResponseBody
    public ResultResponse<List<DeviceDetailVO>> list(@RequestBody QueryDeviceAO request) {
        try {
//            logger.info("查询设备列表,request:{}", JSONObject.toJSON(request));
            List<DeviceDetailVO> devices = deviceService.queryList(request);
            return ResultResponse.success(devices);
        } catch (BizException e) {
            logger.error("查询设备列表失败，request:{}", JSONObject.toJSON(request), e);
            return ResultResponse.failed(e);
        } catch (Exception e) {
            logger.error("查询设备列表异常，request:{}", JSONObject.toJSON(request), e);
            return ResultResponse.failed();
        }
    }

    @PostMapping("/detail")
    @ResponseBody
    public ResultResponse<DeviceSettingVO> detail(@RequestBody Device request) {
        try {
            return ResultResponse.success(deviceService.getDetail(request));
        } catch (BizException e) {
            logger.error("device detail biz error, request:{}", JSONObject.toJSON(request), e);
            return ResultResponse.failed(e);
        } catch (Exception e) {
            logger.error("device detail error, request:{}", JSONObject.toJSON(request), e);
            return ResultResponse.failed();
        }
    }

    @PostMapping("/liquid/level")
    @ResponseBody
    public ResultResponse<DeviceLiquidLevelVO> liquidLevel(@RequestBody Device request) {
        try {
            return ResultResponse.success(deviceService.getLiquidLevel(request));
        } catch (BizException e) {
            logger.error("getLiquidLevel biz error, request:{}", JSONObject.toJSON(request), e);
            return ResultResponse.failed(e);
        } catch (Exception e) {
            logger.error("getLiquidLevel error, request:{}", JSONObject.toJSON(request), e);
            return ResultResponse.failed(e.getMessage());
        }
    }

    @PostMapping("/record/list")
    @ResponseBody
    public ResultResponse<List<DeviceAvgLiquidLevelVO>> recordList(@RequestBody Device request) {
        try {
            List<DeviceAvgLiquidLevelVO> records = deviceService.getDeviceRecordList(request);
            return ResultResponse.success(records);
        }  catch (BizException e) {
            logger.error("recordList biz error, request:{}", JSONObject.toJSON(request), e);
            return ResultResponse.failed(e);
        } catch (Exception e) {
            logger.error("recordList error, request:{}", JSONObject.toJSON(request), e);
            return ResultResponse.failed(e.getMessage());
        }
    }

    @PostMapping("/holle")
    @ResponseBody
    public ResultResponse<Void> holle() {
        logger.info("holle");
        System.out.println("holle");
        return ResultResponse.success();
    }

}
