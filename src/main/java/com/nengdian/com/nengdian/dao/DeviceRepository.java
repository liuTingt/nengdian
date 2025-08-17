package com.nengdian.com.nengdian.dao;

import com.nengdian.com.nengdian.entity.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface DeviceRepository extends JpaRepository<Device, String> {

    List<Device> findByOpenidAndDeleted(String openid, Boolean deleted);

    long countByOpenidAndDeleted(String openid, Boolean deleted);

    Device findByOpenidAndDevIdAndDeleted(String openid, String devId, boolean deleted);

    @Query("select d from Device d where d.openid = :openid and (:devId is null or d.devId like %:devId%) " +
            "and (:devName is null or d.devName like %:devName%) and (:type is null or d.type = :type)" +
            "and d.deleted = false ")
    List<Device> findByDynamicConditions(@Param("openid") String openid, @Param("devId") String devId,
                                         @Param("devName") String devName, @Param("type") Integer type);

    @Modifying
    @Query("update Device d set d.devName = :devName where d.devId = :devId and d.openid = :openid")
    int updateDeviceName(@Param("openid") String openid, @Param("devId") String devId, @Param("devName") String devName);

    @Modifying
    @Transactional
    @Query("update Device d set d.deleted = 1 where d.devId = :devId and d.openid = :openid")
    int deleted(@Param("openid") String openid, @Param("devId") String devId);

    Page<Device> findAll(Specification<Device> specification, Pageable of);

    List<Device> findAll(Specification<Device> specification);

    @Query("select d from Device d where d.devId=:devId and d.deleted = false")
    Device findByDevId(@Param("devId") String devId);

}
