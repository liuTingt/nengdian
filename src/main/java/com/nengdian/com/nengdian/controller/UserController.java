package com.nengdian.com.nengdian.controller;

import com.nengdian.com.nengdian.common.BizException;
import com.nengdian.com.nengdian.common.ResultResponse;
import com.nengdian.com.nengdian.entity.User;
import com.nengdian.com.nengdian.service.UserService;
import com.nengdian.com.nengdian.service.WechatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

@RestController
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Resource
    private UserService userService;
    @Resource
    private WechatService wechatService;


    @GetMapping("/detail")
    @ResponseBody
    public ResultResponse<User> query(@Param("openid") String openid) {
         User user = userService.getUser(openid);
         if (Objects.isNull(user)) {
             return ResultResponse.failed();
         }
         return ResultResponse.success(user);
    }

    @GetMapping("/login")
    @ResponseBody
    public ResultResponse<User> login(@Param("code") String code) {
        try {
            User user = wechatService.login(code);
            if (Objects.nonNull(user)) {
                return ResultResponse.success(user);
            }
        } catch (BizException e) {
            logger.error("login biz error", e);
            return ResultResponse.failed(e.getMsg());
        } catch (Exception e) {
            logger.error("login error", e);
        }
        return ResultResponse.failed();
    }

    @GetMapping("/follow/callback")
    @ResponseBody
    public void follow(String param) {
        logger.info("关注回调"+param);
    }
}
