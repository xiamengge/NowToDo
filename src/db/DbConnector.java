package db;
import javax.swing.*;

import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


import com.mysql.jdbc.PreparedStatement;

//单例模式
public class DbConnector {
	private Connection connection;
	private PreparedStatement preparestatement;
	
	private ResultSet resultset;
	
	private DbConnector(){
		
		
		try {
			//加载jdbc的驱动
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/log";
			//创建和mysql server的一个连接
			connection = DriverManager.getConnection(url, "root", "234567");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static class InnerDbConnector{
		public static final DbConnector db = new DbConnector();
	}
	
	public static DbConnector getInstance(){
		return InnerDbConnector.db;
	}
	
	public ResultSet select(String sql){
		try {
			Statement statement = connection.createStatement();
			return statement.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public int insert(String name,String passwd){
		
		int num=0;
		try{
		String sql="insert into user values('"+name+"','"+passwd+"')";
	    Statement	statement= connection.createStatement();
	     num=  statement.executeUpdate(sql) ;
		
	     } catch (SQLException e) {
		e.printStackTrace();
	}
		return num;
	}	
	
    public int insertMessage(String send,String recv,String msg){
		
		int num=0;
		try{
		String sql="insert into message values('"+send+"','"+recv+"','"+msg+"')";
	    Statement	statement= connection.createStatement();
	     num=  statement.executeUpdate(sql) ;
		
	     } catch (SQLException e) {
		e.printStackTrace();
	}
		return num;
	}		


	public static void main(String[] args) {
		// TODO Auto-generated method stub
              new DbConnector();
	}

}
