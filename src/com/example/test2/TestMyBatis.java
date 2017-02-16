package com.example.test2;

import org.apache.ibatis.session.SqlSession;

import com.bean.Message;
import com.bean.User;
import com.dao.IMessage;
import com.dao.IUser;
import com.sql.MySqlSessionFactory;

public class TestMyBatis {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		SqlSession session = MySqlSessionFactory.getInstance().openSession(true);
		//mybatis锟皆讹拷锟斤拷锟斤拷锟斤拷实锟斤拷IUser锟接口碉拷锟斤拷锟斤拷锟�
//		IUser usermodel = session.getMapper(IUser.class);
//		User user = usermodel. selectByPrimaryKey("xia");
//		System.out.println(user);
//		
//		usermodel.insert(new User( "pangle", "123456"));
		//usermodel.delUser(1002);
		IMessage messagemodel = session.getMapper(IMessage.class);
	    messagemodel.insert(new Message( "xia", "hu","hello"));
		
		session.close();
		
	}
}
