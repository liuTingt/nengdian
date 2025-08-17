package com.nengdian.com.nengdian.dao;

import com.nengdian.com.nengdian.entity.NotifyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotifyRecordRepository extends JpaRepository<NotifyRecord, Long> {

//    @Query("select n from NotifyRecord n where n.openid=:openid and n.devId=:devId order by n.notifyTime desc")
    @Query(value = "select * from notify_record where openid=:openid and dev_id=:devId order by notify_time desc limit 1", nativeQuery = true)
    NotifyRecord findLastByDevId(@Param("openid") String openid, @Param("devId") String devId);

}
