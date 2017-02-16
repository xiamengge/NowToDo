package com.example.test1;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;

import db.DbConnector;
import db.UserState;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

class ReaderTask implements Runnable{
	private Socket client;
	private Scanner scanner;
	private PrintWriter out;

	public ReaderTask(Socket client) {
		super();
		this.client = client;
		try {
			scanner = new Scanner(client.getInputStream());
			out = new PrintWriter(client.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	


	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		while(!Thread.currentThread().isInterrupted()){
			
			
			String recvMsg = scanner.nextLine();
			JsonParser parser = new JsonParser();
			JsonElement json = parser.parse(recvMsg);
			JsonObject object = json.getAsJsonObject();
			System.out.println(recvMsg);
			int msgtype = object.get("msgtype").getAsInt();
			switch(msgtype){
			case 20: //接收服务器ack响应消息的
				//处理登录消息
				String ack1 = object.get("ack").getAsString();
				
				if(ack1.compareTo("fail") == 0){
					System.out.println("login fail!");
					
					System.out.println(object.get("reason").getAsString());
				}else
					System.out.println("login success!");
				//   System.out.println(object.get("event").getAsString());  
				   break;
			case 21://处理注册消息
			String ack2 = object.get("ack").getAsString();
				if(ack2.compareTo("fail") == 0){
					System.out.println("register fail!");
					
					System.out.println(object.get("reason").getAsString());
				}else{
					System.out.println("regesiter success!");
				
				}
				break;
			case 22://处理上线广播消息
				System.out.println(object.get("event").getAsString());
				break;
			case 23://处理离线广播消息
				System.out.println(object.get("event").getAsString());;	
				
				break;
			case 24://处理离线//
		            String ack3 = object.get("ack").getAsString();
					if(ack3.compareTo("ok") == 0){
						System.out.println("left success!");
						
						System.out.println(object.get("event").getAsString());
					}else{
						System.out.println("left fail!");
					
					}  
					break;
			case 25://处理发送消息
				 String ack4 = object.get("ack").getAsString();
					if(ack4.compareTo("ok") == 0){
						System.out.println("recv success!");
						System.out.println(object.get("msg").getAsString());
					}else if(ack4.compareTo("fail") == 0){
						System.out.println("send fail!");
						System.out.println(object.get("reason").getAsString());
						
					}  
					break;	
				
			}
		}
		
	}
}

public class Client {
	
	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		
		try {
			Scanner scanner = new Scanner(System.in);
			ExecutorService threadpool = Executors.newSingleThreadExecutor();
			Socket client = new Socket("127.0.0.1", 6000);
			threadpool.submit(new ReaderTask(client));
			
 while(!Thread.currentThread().isInterrupted()){
				
				// 显示菜单，用户进行选择
				System.out.println("=========选择1登录===========");
				System.out.println("=========选择2注册===========");
				System.out.println("=========选择3退出===========");
				System.out.println("=========选择4发送===========");
				System.out.println("请输入你所选的数：");
				Scanner scan=new Scanner(System.in);
				int choose=scan.nextInt();
				switch(choose){
				case 1:
					System.out.println("input name");
     				String name = scanner.nextLine();
					System.out.println("input pwd");
					String pwd = scanner.nextLine();
					JsonObject json1 = new JsonObject();
			     	json1.addProperty("msgtype", 1);
				    json1.addProperty("name", name);
				    json1.addProperty("passwd", pwd);
				    PrintWriter out1 = new PrintWriter(client.getOutputStream(), true);
					out1.println(json1.toString());
					//out1.close();
					//client.close();
				    break;
				case 2:
					System.out.println("请输入注册信息");
					System.out.println("input name");
					String rename = scanner.nextLine();
					System.out.println("input pwd");
					String repwd = scanner.nextLine();
					JsonObject json2 = new JsonObject();
					json2.addProperty("msgtype", 2);
					json2.addProperty("name",rename);
					json2.addProperty("passwd", repwd);
					PrintWriter out2 = new PrintWriter(client.getOutputStream(), true);
					 out2.println(json2.toString());
					//out2.close();
					//client.close();
					break;
				case 3:
					System.out.println("请离线人的名称：");
					System.out.println("input name");
					String lname = scanner.nextLine();
					JsonObject json3 = new JsonObject();
					json3.addProperty("msgtype", 3);
					json3.addProperty("name",lname);
					System.out.println("input msg:");
					String msg1 = scanner.nextLine();
					json3.addProperty("msg",msg1);
					PrintWriter out3 = new PrintWriter(client.getOutputStream(), true);
					out3.println(json3.toString());
					
					break;
				case 4:
					System.out.println("请输入发送消息的信息");
					System.out.println("message from:");
					String name3 = scanner.nextLine();
					System.out.println("message to:");
					String to = scanner.nextLine();
					System.out.println("msg is:");
					String msg = scanner.nextLine();
					JsonObject json4 = new JsonObject();
					json4.addProperty("msgtype",4);
					json4.addProperty("from",name3);
					json4.addProperty("to",to);
					json4.addProperty("msg",msg);
					PrintWriter out4 = new PrintWriter(client.getOutputStream(), true);
					out4.println(json4.toString());
				    break;
					
				}
				
			}
			
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

