package com.nengdian.com.nengdian.controller;

import com.alibaba.fastjson.JSONObject;
import com.nengdian.com.nengdian.bo.MessageData;
import com.nengdian.com.nengdian.bo.WechatUserInfoRes;
import com.nengdian.com.nengdian.common.BizException;
import com.nengdian.com.nengdian.common.LiquidStatusEnum;
import com.nengdian.com.nengdian.common.ResultResponse;
import com.nengdian.com.nengdian.service.UserService;
import com.nengdian.com.nengdian.service.WechatService;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/wechat")
public class WechatController {
    private static final Logger logger = LoggerFactory.getLogger(WechatController.class);

    @Resource
    private WechatService wechatService;
    @Resource
    private UserService userService;


    @GetMapping("/receive/event")
    @ResponseBody
    public String verify(@RequestParam("signature") String signature, @RequestParam("timestamp") Long timestamp,
                                       @RequestParam("nonce") Long nonce, @RequestParam("echostr") String echostr) {
        try {
            logger.info(signature);
            return echostr;
        } catch (Exception e) {
            logger.error("接收微信事件校验异常", e);
        }
        return "false";
    }

    @PostMapping("/receive/event")
    @ResponseBody
    public String receiverMsg(@RequestBody String message, HttpServletRequest request) {
        try {
            logger.info("接收消息:{}", JSONObject.toJSONString(message));
            Document document = DocumentHelper.parseText(message);
            Element root = document.getRootElement();
            String openid = root.elementText("FromUserName");

            // todo 只有关注时保存unionid
            String event = root.elementText("Event");
//            if (!"subscribe".equals(event)) {
//                return "success";
//            }
            WechatUserInfoRes wechatUser = wechatService.queryUser(openid);
            userService.updateServiceUser(wechatUser);
            return "success";
        } catch (BizException e) {
            logger.error("接收微信事件失败:{}", e.getMsg());
        } catch (Exception e) {
            logger.error("接收微信事件异常", e);
        }
        return "failed";
    }

    @PostMapping("/accessToken")
    @ResponseBody
    public ResultResponse<String> accessToken() {
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

    @PostMapping("/send")
    @ResponseBody
    public ResultResponse<String> send(@RequestParam("openid") String openid) {
        try {
            Map<String, MessageData> map = new HashMap<>();
            map.put("thing2", new MessageData("设备名称"));
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime time = LocalDateTime.now();
            map.put("time4", new MessageData(time.format(dateFormatter)));
            String desc = LiquidStatusEnum.getStatusDesc(2);
            map.put("thing5", new MessageData("液位过高"));

            String msgid = wechatService.sendMessage(openid, map);
            return ResultResponse.success(msgid);
        } catch (BizException e) {
            logger.error("send message biz error, ");
            return ResultResponse.failed(e);
        } catch (Exception e) {
            logger.error("send message  error, ");
            return ResultResponse.failed();
        }
    }
}
