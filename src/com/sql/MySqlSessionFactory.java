package com.sql;

import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class MySqlSessionFactory {

	private static SqlSessionFactory sqlSessionFactory;
	
	private MySqlSessionFactory(){
		
	}
	
	public synchronized static SqlSessionFactory getInstance(){
		
		if(sqlSessionFactory == null){
			//创建SqlSessionFactor
			String resource = "com/config/mybatisconfigUser.xml";
	        InputStream is;
			try {
				is = Resources.getResourceAsStream(resource);
				sqlSessionFactory = new SqlSessionFactoryBuilder().build(is);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return sqlSessionFactory;
	}
}
