package com.dao;

import com.bean.Message;
import com.bean.User;

public interface IMessage {
    int insert(Message record);

    int insertSelective(Message record);
    int  selectByrecv(String name);
    Message selectByrecv1(String name);
}