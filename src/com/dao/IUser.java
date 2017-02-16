package com.dao;

import com.bean.User;

public interface IUser {
    int deleteByPrimaryKey(String name);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(String name);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
    User getUser(String name);
}