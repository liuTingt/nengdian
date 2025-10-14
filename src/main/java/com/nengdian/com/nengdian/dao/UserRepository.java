package com.nengdian.com.nengdian.dao;

import com.nengdian.com.nengdian.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {

    User findByOpenid(String openid);

    User findByUnionid(String unionid);

    @Query(value = "select u from User u where u.userName = ?1")
    List<User> find(@Param("name") String name);
}
