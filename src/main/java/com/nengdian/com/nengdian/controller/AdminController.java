package com.nengdian.com.nengdian.controller;

import com.alibaba.fastjson.JSONObject;
import com.nengdian.com.nengdian.ao.QueryDevicePageAO;
import com.nengdian.com.nengdian.common.BizException;
import com.nengdian.com.nengdian.common.ResultResponse;
import com.nengdian.com.nengdian.entity.Device;
import com.nengdian.com.nengdian.service.DeviceService;
import com.nengdian.com.nengdian.service.WechatService;
import com.nengdian.com.nengdian.vo.DevicePageVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Resource
    private DeviceService deviceService;


    @PostMapping("/page/list")
    @ResponseBody
    public ResultResponse<DevicePageVO> pageList(@Valid @RequestBody QueryDevicePageAO request) {
        try {
            Page<Device> result = deviceService.pageList(request);
            return ResultResponse.success(new DevicePageVO(result.getTotalElements(), result.getContent()));
        } catch (BizException e) {
            logger.error("pageList biz error, request:{}", JSONObject.toJSONString(request), e);
            return ResultResponse.failed(e);
        } catch (Exception e) {
            logger.error("pageList error, request:{}", JSONObject.toJSONString(request), e);
            return ResultResponse.failed();
        }
    }


    @Resource
    private WechatService wechatService;

    @PostMapping("/test")
    @ResponseBody
    public ResultResponse<String> test() {
        try {
            String accessToken = wechatService.getAccessToken();
            return ResultResponse.success(accessToken);
        } catch (BizException e) {
            logger.error("test biz error, ");
            return ResultResponse.failed(e);
        } catch (Exception e) {
            logger.error("test error, ");
            return ResultResponse.failed();
        }
    }
}
