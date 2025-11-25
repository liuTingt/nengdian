package com.nengdian.com.nengdian.dao;

import com.nengdian.com.nengdian.entity.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Modifying
    @Transactional
    void deleteUserDeviceByDevId(String devId);

    @Query(value = "select ud from UserDevice ud where ud.openid like %:openid%")
    List<UserDevice> findByOpenid(String openid);

    @Query(value = "select * from user_device  where dev_id IN :devIds", nativeQuery = true)
    List<UserDevice> findByDevIds(@Param("devIds") List<String> devIds);

}
