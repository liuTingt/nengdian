package com.nengdian.com.nengdian.dao;

import com.nengdian.com.nengdian.entity.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import javax.transaction.Transactional;
import java.util.List;

public interface UserDeviceRepository extends JpaRepository<UserDevice, String> {

    UserDevice findUserDeviceByDevIdAndOpenid(String devId, String openid);

    List<UserDevice> findUserDeviceByOpenid(String openid);

    List<UserDevice> findUserDeviceByDevId(String devId);

    long countByOpenid(String openid);

    @Modifying
    @Transactional
    void deleteUserDeviceByDevIdAndOpenid(String devId, String openid);
}
