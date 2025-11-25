package com.nengdian.com.nengdian.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.nengdian.com.nengdian.bo.*;
import com.nengdian.com.nengdian.common.BizException;
import com.nengdian.com.nengdian.common.CommonConstant;
import com.nengdian.com.nengdian.common.HttpUtil;
import com.nengdian.com.nengdian.common.ResultCodeEnum;
import com.nengdian.com.nengdian.entity.User;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;

@Service
public class WechatService {
    private static final Logger logger = LoggerFactory.getLogger(WechatService.class);

    private static final String login_url = "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";
    private static final String access_token_url = "https://api.weixin.qq.com/cgi-bin/stable_token";
//    private static final String access_token_url = "https://api.weixin.qq.com/cgi-bin/stable_token?grant_type=client_credential&force_refresh=true&appid=%s&secret=%s";
    private static final String message_url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s";
//    private static final String message_url = "https://api.weixin.qq.com/cgi-bin/message/subscribe/bizsend?access_token=%s";

    private static final String query_user_url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s";
    /**
     * 小程序配置
     */
    private static final String appid = "wxb568ad9a4bd0a37b";
    private static final String app_secret = "c130296390b7160e4b2ce16f25a5c485";

    /**
     * 服务号配置
     */
    private static final String service_appid = "wx565adf2eb2b4a36f";
    private static final String service_app_secret = "b4cd8c45d8fbcae139631fda5ca1b1d1";

    private static final String test_appid = "wx34af4a57692ba95d";
    private static final String test_app_secret = "9a7641ae9e98c225662307aeba6f8e34";

    /**
     * 设备告警通知-服务号消息模版id
     */
    private static final String message_template_id = "ZiroR5LsxCJHszRWx0spTCV8bpy-Gp-3ltDUC76yThs";

    @Resource
    private HttpUtil httpUtil;
    @Resource
    private UserService userService;

    public Cache<String, String> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(100, TimeUnit.MINUTES)
            .maximumSize(100)
            .removalListener(removalNotification -> {
                logger.info("cache remove reason:{}", removalNotification.getCause());
            })
            .build();


    public User login(String code) {
        String url = String.format(login_url, appid, app_secret, code);
        String repStr = httpUtil.doGet(url, String.class);
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
        logger.info("获取accessToken请求");
        String token = cache.getIfPresent(CommonConstant.CACHE_ACCESS_TOKEN_KEY);
        if (Strings.isBlank(token)) {
            token = queryAccessToken();
            if (Strings.isNotBlank(token)) {
                cache.put(CommonConstant.CACHE_ACCESS_TOKEN_KEY, token);
            }
        }
        return token;
    }

    public String queryAccessToken() {
        try {
            String url = String.format(access_token_url, service_appid, service_app_secret);
            Map<String, String> param = Maps.newHashMap();
            param.put("grant_type","client_credential");
            param.put("appid",service_appid);
            param.put("secret",service_app_secret);
            AccessTokenRes response = httpUtil.doPostByJson(url, JSONObject.toJSONString(param), AccessTokenRes.class);
            logger.info("获取accessToken，response:{}", JSONObject.toJSONString(response));
            if (response.isSuccess()) {
                return response.getAccess_token();
            }
        } catch (Exception e) {
            logger.error("获取accessToken异常", e);
        }
        throw new BizException(ResultCodeEnum.ACCESS_TOKEN_ERROR);
    }

    public boolean sendMessage(String openid, Map<String, MessageData> msg) {
        try {
            User user = userService.getUser(openid);
            if (Objects.isNull(user)) {
                throw new BizException(ResultCodeEnum.NOT_FIND_USER);
            }
            if (!user.isRemindSwitch()) {
                throw new BizException(ResultCodeEnum.NOT_OPEN_REMIND);
            }
            if (Strings.isBlank(user.getServiceOpenid())) {
                throw new BizException(ResultCodeEnum.NOT_FIND_USER_SERVICE_OPEN_ID);
            }
            SendMessage sendMessage = new SendMessage();
            sendMessage.setTemplate_id(message_template_id);
            sendMessage.setTouser(user.getServiceOpenid());
            sendMessage.setData(msg);
            String token = this.getAccessToken();
            String url = String.format(message_url, token);

            MessageRes response = httpUtil.doPostByJson(url, JSONObject.toJSONString(sendMessage), MessageRes.class);
            logger.info("发送订阅消息，openid:{}, serviceOpenid:{}, message:{}, response:{}",
                    openid, user.getServiceOpenid(), JSONObject.toJSONString(sendMessage), JSONObject.toJSONString(response));
            return response.isSuccess();
        } catch (BizException e) {
            logger.error("发送消息业务异常,openid:{},msg:{}", openid, e.getMsg());
        } catch (Exception e) {
            logger.error("发送消息异常", e);
        }
        return false;
//        throw new BizException(ResultCodeEnum.SEND_MESSAGE_ERROR);
    }

    public WechatUserInfoRes queryUser(String openid) {
        try {
            String token = this.getAccessToken();
            String url = String.format(query_user_url, token, openid);
            WechatUserInfoRes response = httpUtil.doGet(url, WechatUserInfoRes.class);
            logger.info("微信用户信息:{}", JSONObject.toJSONString(response));
            if (response.isSuccess()) {
                return response;
            }
        } catch (Exception e) {
            logger.error("查询微信用户信息异常", e);
        }
        throw new BizException(ResultCodeEnum.QUERY_WECHAT_USER_ERROR);
    }

}
