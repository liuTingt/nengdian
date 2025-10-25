package com.nengdian.com.nengdian.dao;

import com.nengdian.com.nengdian.entity.NotifyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface NotifyRecordRepository extends JpaRepository<NotifyRecord, Long> {

//    @Query("select n from NotifyRecord n where n.openid=:openid and n.devId=:devId order by n.notifyTime desc")
    @Query(value = "select * from notify_record where openid=:openid and dev_id=:devId limit 1", nativeQuery = true)
    NotifyRecord findLastByDevId(@Param("openid") String openid, @Param("devId") String devId);

    NotifyRecord findNotifyRecordByDevIdAndOpenid(String devId, String openid);

    @Query(value = "update notify_record set notify_time=:notifyTime where  openid=:openid and dev_id=:devId", nativeQuery = true)
    NotifyRecord update(@Param("openid") String openid, @Param("devId") String devId, @Param("notifyTime") LocalDateTime notifyTime);
}
