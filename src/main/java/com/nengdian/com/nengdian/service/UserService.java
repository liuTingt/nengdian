package com.nengdian.com.nengdian.service;
import java.util.Date;
import java.util.Objects;

import com.nengdian.com.nengdian.bo.WechatUserInfoRes;
import com.nengdian.com.nengdian.common.BizException;
import com.nengdian.com.nengdian.common.ResultCodeEnum;
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
        user.setUnionid(unionid);
        user.setLanguage("zh-CN");
        user.setRemindSwitch(false);
        user.setCreateTime(new Date());
        user.setModifyTime(new Date());
        return userRepository.save(user);
    }

    public User updateServiceUser(WechatUserInfoRes wechatUser) {
        try {
            User user = userRepository.findByUnionid(wechatUser.getUnionid());
            if (Objects.isNull(user)) {
                throw new BizException(ResultCodeEnum.NOT_FIND_USER);
            }
            user.setServiceOpenid(wechatUser.getOpenid());
            return userRepository.save(user);
        } catch (BizException e) {
            logger.error("更新微信用户openid失败:{}", e.getMsg());
            throw e;
        } catch (Exception e) {
            logger.error("更新微信用户openid异常", e);
        }
        throw new BizException(ResultCodeEnum.UPDATE_USER_ERROR);
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
