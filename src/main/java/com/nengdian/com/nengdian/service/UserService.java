package com.nengdian.com.nengdian.service;
import java.util.Date;

import com.nengdian.com.nengdian.dao.UserRepository;
import com.nengdian.com.nengdian.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Resource
    private UserRepository userRepository;


    public User save(String openid, String unionid) {
        User user = new User();
        user.setOpenid(openid);
        user.setUserName(unionid);
        user.setLanguage("zh-CN");
        user.setRemindSwitch(false);
        user.setCreateTime(new Date());
        user.setModifyTime(new Date());
        return userRepository.save(user);
    }

    public User getUser(String openid) {
        try {
            return userRepository.findByOpenid(openid);
        } catch (Exception e) {
            logger.error("getUser error, openid:{}", openid, e);
        }
        return null;
    }
}
