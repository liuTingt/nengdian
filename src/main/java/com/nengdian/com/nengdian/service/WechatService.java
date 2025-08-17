package com.nengdian.com.nengdian.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.nengdian.com.nengdian.bo.AccessTokenRes;
import com.nengdian.com.nengdian.bo.SendMessage;
import com.nengdian.com.nengdian.bo.WechatLoginRep;
import com.nengdian.com.nengdian.common.BizException;
import com.nengdian.com.nengdian.common.HttpUtil;
import com.nengdian.com.nengdian.common.ResultCodeEnum;
import com.nengdian.com.nengdian.common.WechatConstant;
import com.nengdian.com.nengdian.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

@Service
public class WechatService {
    private static final Logger logger = LoggerFactory.getLogger(WechatService.class);

    private static final String login_url = "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";
    private static final String access_token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&force_refresh=false&appid=%s&secret=%s";
    private static final String message_url = "https://api.weixin.qq.com/cgi-bin/message/subscribe/bizsend?access_token=%s";
    private static final String appid = "wx0b1b04d5b707e870";
    private static final String app_secret = "8b7f4357479552d7944fdb7de07fcbb3";
    @Resource
    private HttpUtil httpUtil;
    @Resource
    private UserService userService;


    public User login(String code) {
        String utl = String.format(login_url, appid, app_secret, code);
        String repStr = httpUtil.doGet(utl, String.class);
        logger.info("login response:{}", JSONObject.toJSON(repStr));
        WechatLoginRep response = JSONObject.parseObject(repStr, WechatLoginRep.class);
//        WechatLoginRep response = httpUtil.doGet(utl, WechatLoginRep.class);
        if (!response.isSuccess()) {
            throw new BizException(response.getErrcode() ,response.getErrmsg());
        }
        User user = userService.getUser(response.getOpenid());
        if (Objects.isNull(user)) {
            user = userService.save(response.getOpenid(), response.getUnionid());
        }
        return user;
    }

    public String getAccessToken() {
        try {
            String url = String.format(access_token_url, appid, app_secret);
            AccessTokenRes response = httpUtil.doGet(url, AccessTokenRes.class);
            logger.info("获取accessToken，response:{}", JSONObject.toJSONString(response));
            return response.getAccess_token();
        } catch (Exception e) {
            logger.info("获取accessToken失败", e);
            throw new BizException(ResultCodeEnum.ACCESS_TOKEN_ERROR);
        }
    }

    public String sendMessage() {
        try {
            Map<String, Object> map = Maps.newHashMap();
            map.put("grant_type", WechatConstant.GRANT_TYPE_VALUE);
            map.put("appid", appid);
            map.put("secret", app_secret);
            map.put("force_refresh", false);

            SendMessage sendMessage = new SendMessage();

            String token = this.getAccessToken();
            String url = String.format(message_url, token);

            AccessTokenRes response = httpUtil.doPostByJson(url, map, AccessTokenRes.class);
            logger.info("发送订阅消息，response:{}", response);
            return response.getAccess_token();
        } catch (Exception e) {
            logger.info("获取accessToken失败", e);
            throw new BizException(ResultCodeEnum.ACCESS_TOKEN_ERROR);
        }
    }

}
