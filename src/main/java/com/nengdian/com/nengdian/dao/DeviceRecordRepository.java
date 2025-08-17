package com.nengdian.com.nengdian.dao;

import com.nengdian.com.nengdian.entity.DeviceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.Tuple;
import java.util.Date;
import java.util.List;

public interface DeviceRecordRepository extends JpaRepository<DeviceRecord, Long> {

    @Query(value = "select dr from DeviceRecord dr where dr.devId in :deviceIds " +
            "and dr.createTime = (select Max(dr2.createTime) from DeviceRecord dr2 where dr2.devId = dr.devId)")
    List<DeviceRecord> findLatestByDeviceIds(@Param("deviceIds") List<String> deviceIds);

    @Query("select r from DeviceRecord r where r.devId in :devIds and r.createTime between :startTime and :endTime")
    List<DeviceRecord> findDeviceRecordByDevIdIsAndCreateTime(@Param("devIds") List<String> devIds, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    @Query(value = "select dev_id as devId, avg(liquid_height) as liquidHeight from device_record where dev_id = :devId and create_time >=:startTime and create_time < :endTime", nativeQuery = true)
    Tuple findAvgDeviceRecord(@Param("devId") String devId, @Param("startTime") String startTime, @Param("endTime") String endTime);

}
