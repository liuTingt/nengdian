package com.nengdian.com.nengdian.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.nengdian.com.nengdian.ao.QueryDevicePageAO;
import com.nengdian.com.nengdian.common.BizException;
import com.nengdian.com.nengdian.common.ResultResponse;
import com.nengdian.com.nengdian.dao.UserDeviceRepository;
import com.nengdian.com.nengdian.entity.Device;
import com.nengdian.com.nengdian.entity.UserDevice;
import com.nengdian.com.nengdian.service.DeviceService;
import com.nengdian.com.nengdian.vo.DevicePageVO;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Resource
    private DeviceService deviceService;
    @Resource
    private UserDeviceRepository userDeviceRepository;


    @PostMapping("/page/list")
    @ResponseBody
    public ResultResponse<DevicePageVO> pageList(@Valid @RequestBody QueryDevicePageAO request) {
        Page<Device> result = null;
        try {
            List<String> devIds = Lists.newArrayList();
            if (Strings.isNotBlank(request.getOpenid())) {
                List<UserDevice> userDevices = userDeviceRepository.findByOpenid(request.getOpenid());
                devIds = userDevices.stream().map(UserDevice::getDevId).collect(Collectors.toList());
                result = deviceService.pageList(request, devIds);
            } else {
                result = deviceService.pageList(request, devIds);
            }
            return ResultResponse.success(new DevicePageVO(result.getTotalElements(), result.getContent()));
        } catch (BizException e) {
            logger.error("pageList biz error, request:{}", JSONObject.toJSONString(request), e);
            return ResultResponse.failed(e);
        } catch (Exception e) {
            logger.error("pageList error, request:{}", JSONObject.toJSONString(request), e);
            return ResultResponse.failed();
        }
    }
}
