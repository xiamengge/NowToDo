package com.example.test1;
import java.util.*;
import javax.swing.*;

import java.awt.print.Printable;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.security.KeyStore.Entry;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import db.DbConnector;
import db.UserState;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

class HandleClientTask implements Runnable{
	private static Statement statement;
	private Socket client;
	private Scanner scanner;
	private PrintWriter out;
    private static  Iterator<java.util.Map.Entry<String, Socket>> iterator;
    private static  Map<String,Socket >myMap;
	
	static{
		myMap=Collections.synchronizedMap(new ConcurrentHashMap<String,Socket>());
		
	
	}
	
	public HandleClientTask(Socket client) {
		super();
		this.client = client;
		
		try {
			scanner = new Scanner(client.getInputStream());
			out = new PrintWriter(client.getOutputStream(), true);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		    iterator = myMap.entrySet().iterator();
		    
		  }

	//下线处理
	public void left(JsonObject jobject){
		String stream=jobject.get("msg").getAsString();
		if(stream.compareTo("exit")==0){
			String name=jobject.get("name").getAsString();
			 PrintWriter out;
			  while (iterator.hasNext())
		      {
		        java.util.Map.Entry<String, Socket> entry = iterator.next();
		        if(entry.getKey().compareTo(name)==0){
		        	
		        	Socket socket=entry.getValue();
		            System.out.println("client normal close!");
		            myMap.remove(name, socket);
		            JsonObject json1 = new JsonObject();
		            json1.addProperty("ack", "ok");
		            json1.addProperty("event",entry.getKey()+ "is left!!!");
		            json1.addProperty("msgtype", 24);
				try {
					out= new PrintWriter(socket.getOutputStream(),true);
					out.println(json1.toString());
					try {
						 BroadcastLeft(jobject);
						client.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						System.out.println("client normal close!");
					}
					 
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		       }
				
			}
			 
			}
				
	}
			 
	
	//登录
	public void login(JsonObject jobject){
		//访问数据库，鉴定name  password
		
		String name = jobject.get("name").getAsString();
		String pwd = jobject.get("passwd").getAsString();
		 String message=null;
		boolean bloginstate = false;
		
		
		DbConnector db = DbConnector.getInstance();
		ResultSet rset = db.select("select * from  user");
		ResultSet rpt = db.select("select * from  message");
		try {
			while(rset.next()){
				String _name = rset.getString("name");
				if(_name.compareTo(name) == 0){
					if(rset.getString("passwd").compareTo(pwd) == 0){
						//登录成功
						bloginstate = true;
						Broadcast(jobject);
						myMap.put(name, client);
						System.out.println(myMap.values());
						  while(rpt.next()){
					    	  String toname=rpt.getString("recv");
					    	  if(toname.compareTo(name)==0){
					    		   message=rpt.getString("msg");
					    		   
					    	  }
					    	 }
						 }
					} 
				
				}
		   
			   rpt.close();
		      rset.close();
		      
		     
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
		//给客户端回复

			JsonObject json1 = new JsonObject();
		   json1.addProperty("msgtype", 20);
			if(bloginstate){
				  // 20表示服务器的ack响应消息
				json1.addProperty("ack", "ok");
				if(message!=null)
				json1.addProperty("event","you have a message to recv!!"+"the message is"+message);
				//System.out.println("hello");
			}else{
				json1.addProperty("ack", "fail");
				json1.addProperty("reason", "name or pwd is wrong!!!");
			}
		
				
			
			out.println(json1.toString());	
			
			
			
		}
	
	
	
	//注册信息
	public void register(JsonObject jobject){ 
		String rename = jobject.get("name").getAsString();
		String repwd = jobject.get("passwd").getAsString();
        boolean registerstate = false;
		DbConnector db = DbConnector.getInstance();
		ResultSet rset = db.select("select * from  user");
	       	try {
				while(rset.next()){
					String _name = rset.getString("name");
					if(_name.compareTo(rename) == 0){
					 break;
					}
						
						int n=db.insert(rename, repwd);
					
					if(n>0){
					
						registerstate = true;
				        }
					     else break;
					}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	       	JsonObject json = new JsonObject();
			json.addProperty("msgtype", 21);
			if(registerstate ){
				  // 20表示服务器的ack响应消息
				json.addProperty("ack", "ok");
			}else{
				json.addProperty("ack", "fail");
				json.addProperty("reason", "name or pwd is null!!!");
			}
			out.println(json.toString());
		
		

	}
	//上线通知
	public void Broadcast(JsonObject jobject){ 
		
		String name=jobject.get("name").getAsString();
		jobject.addProperty("msgtype", 22);
		jobject.addProperty("name", name);
		jobject.addProperty("event", name+" is on line!");
		Iterator<Socket>it=myMap.values().iterator();
		try {
		while(it.hasNext()){
			
			Socket key=it.next();
			
			
				PrintWriter out=new PrintWriter(key.getOutputStream(),true);
				
				out.println(jobject.toString());
			}
		
		    out = new PrintWriter(client.getOutputStream(),true);
	        out.println(jobject.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	
		
		
	}
	//处理离线
  public void BroadcastLeft(JsonObject jobject){ 
		
		String name=jobject.get("name").getAsString();
		jobject.addProperty("msgtype", 23);
		jobject.addProperty("name", name);
		jobject.addProperty("event", name+" 已经离线了!");
		Iterator<Socket>it=myMap.values().iterator();
		
		while(it.hasNext()){
			
			Socket key=it.next();
			
			try {
				PrintWriter out=new PrintWriter(key.getOutputStream(),true);
				
				out.println(jobject.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
	
		try {
			PrintWriter out;
			out = new PrintWriter(client.getOutputStream(),true);
			out.println(jobject.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("client left unnormally!");
		}
		
		
		
		
	}
	//上线发送
	
	public void sendmessage(JsonObject jobject){
		
		    String from = jobject.get("from").getAsString();
			String to = jobject.get("to").getAsString();
			String msg = jobject.get("msg").getAsString();
			jobject.addProperty("msgtype", 25);
			jobject.addProperty("event", msg);
			PrintWriter out;
			boolean flag = true;
			
          while (iterator.hasNext()){
    		      
        	  
        	        java.util.Map.Entry<String, Socket> entry = iterator.next();
    		        if(entry.getKey().compareTo(to)==0){
    		        	flag = false;
    		        	Socket socket = entry.getValue();                                                                                                     
    					jobject.addProperty("ack", "ok");
    		           try {
						out  = new PrintWriter(socket.getOutputStream(),true);
						 out.println(jobject.toString());
						   System.out.print("hello");
						   break;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					  
					  }
    		       continue;
				} 
    					
    		
    			 if(flag){
    				 try {
						out = new PrintWriter(client.getOutputStream(),true);
						 jobject.addProperty("ack", "fail");
	    				 jobject.addProperty("reason", to+"is not on line now!"+"he will receve the message when he will on line!");
	    				 out.println(jobject.toString());
	    				
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    				 DbConnector db = DbConnector.getInstance();
    				 int n=db.insertMessage(from, to,msg);
 					
 					if(n>0){
 					         System.out.println("留言成功！");
 				           }
    				
    			 }
    			
			
    		 
			
    			
			}
			
       
		 
    
	
    		        	
    		        	
    		        
    		     
	
	
	
	
	@Override
	
	public void run()  {
		try{
			while(!Thread.currentThread().isInterrupted()){
		// TODO Auto-generated method stub
		String recvMsg = scanner.nextLine();
		JsonParser parser = new JsonParser();
		JsonElement json = parser.parse(recvMsg);
		JsonObject object = json.getAsJsonObject();
		System.out.println(recvMsg);
		int msgtype = object.get("msgtype").getAsInt();
		
		switch(msgtype){
		case 1:
			//处理登录消息
			login(object);
			
			break;
		case 2:
			register(object);
			break;
		 case 3:
			left(object);
			break;
		case 4:
			
			sendmessage(object);
			//System.out.println(4);
			break;
		
		}
	}
	}catch(Exception e){
		//e.printStackTrace();
		System.out.println("close exception!");
		
     }
	}
}

	


public class Server {
	
	private static ExecutorService threadpool;
	
	
	
	static{
		  threadpool = Executors.newCachedThreadPool();
	}
	
	public static void main(String[] args){
		int count=0;
		try {
			ServerSocket server = new ServerSocket(6000);
			
			System.out.println("server supply service on 6000...");
			
			while(!Thread.currentThread().isInterrupted()){
				Socket client = server.accept();
				threadpool.submit(new HandleClientTask(client));
				 count++; //统计客户端的数量 
			        System.out.println("客户端的数量: " + count); 
			        InetAddress address = client.getInetAddress(); 
			        System.out.println("当前客户端的端口号："+client.getRemoteSocketAddress() ); 

			}
			

			System.out.println("server out service...");
			server.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
