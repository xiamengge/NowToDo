package com.example.test1;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import db.DbConnector;


class Work implements Runnable{
	
	private Selector selector;
	private ByteBuffer buffer;
	private List<SocketChannel> channellist;
	private static  Map<String,Socket >myMap;
    private static Iterator<java.util.Map.Entry<String, Socket>> iterator;
   
    static{
		myMap=Collections.synchronizedMap(new ConcurrentHashMap<String,Socket>());
     
	}
	public Work() throws IOException {
		super();
		// TODO Auto-generated constructor stub
		//����selector����
		selector = Selector.open();
		 iterator = myMap.entrySet().iterator();
		buffer = ByteBuffer.allocate(1024);
		channellist=Collections.synchronizedList( new ArrayList<SocketChannel>());
		
	}
	public List<SocketChannel> getList(){
		return channellist;
	 }
	public List<SocketChannel> add(List<SocketChannel> list){
		return this.channellist=list;
		
	}
	
    	public Selector getSelector(){
		return selector;
	  }
    	
   


    	
     //��¼
    	public void login(JsonObject jobject,SocketChannel cchannel){
    		//�������ݿ⣬����name  password
    		String name = jobject.get("name").getAsString();
    		String pwd = jobject.get("passwd").getAsString();
    		String msg=null;
    		boolean bloginstate = false;
    		
    		
    		DbConnector db = DbConnector.getInstance();
    		ResultSet rset = db.select("select * from  user");
    	//	ResultSet rpt = db.select("select * from  message");
    		try {
    			while(rset.next()){
    				String _name = rset.getString("name");
    				if(_name.compareTo(name) == 0){
    					if(rset.getString("passwd").compareTo(pwd) == 0){
    						//��¼�ɹ�
    						bloginstate = true;
    						Broadcast(jobject,cchannel);
    						
    						myMap.put(name,cchannel.socket() );
    						System.out.println(myMap.values());
    						}
    					} 
    				}
    			/* while(rpt.next()){
   		    	  String toname=rpt.getString("recv");
   		    	  if(toname.compareTo(name)==0){
   		    		   msg=rpt.getString("msg");
   		    		   
   		    	  }
    			 }*/
    		rset.close();
    	//	rpt.close();
    			
    		} catch (SQLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	
    		
    		//���ͻ��˻ظ�
               
    			JsonObject json1 = new JsonObject();
    			json1.addProperty("msgtype", 20);
    			if(bloginstate){
    				  // 20��ʾ��������ack��Ӧ��Ϣ
    				json1.addProperty("ack", "ok");
    				json1.addProperty("leftmsg", msg);
    			}
    		  
    		  else{
    				json1.addProperty("ack", "fail");
    				json1.addProperty("reason", "name or pwd is wrong!!!");
    				
    			}
    			
			try {
				
			    	cchannel.write(ByteBuffer.wrap(new String(json1.toString()).getBytes()));
					cchannel.write(ByteBuffer.wrap(new String("\n").getBytes()));
					buffer.flip();
					buffer.clear();
					
				} catch (IOException e) {
							// TODO Auto-generated catch block
						e.printStackTrace();
						
					}
				}
    	//ע����Ϣ
    	public void register(JsonObject jobject,SocketChannel cchannel){ 
    		String rename = jobject.get("name").getAsString();
    		String repwd = jobject.get("passwd").getAsString();
            boolean registerstate = false;
    		DbConnector db = DbConnector.getInstance();
    		ResultSet rset = db.select("select * from  user");
    	       	try {
    				while(rset.next()){
    					System.out.println("llll");
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
    	       	JsonObject json2 = new JsonObject();
    			json2.addProperty("msgtype", 21);
    			if(registerstate ){
    				  // 20��ʾ��������ack��Ӧ��Ϣ
    				json2.addProperty("ack", "ok");
    			}else{
    				json2.addProperty("ack", "fail");
    				json2.addProperty("reason", "name or pwd is null!!!");
    			}
    			try {
    				cchannel.write(ByteBuffer.wrap(new String(json2.toString()).getBytes()));
					cchannel.write(ByteBuffer.wrap(new String("\n").getBytes()));
					buffer.flip();
					buffer.clear();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
    		}
    	//����֪ͨ
    	public void Broadcast(JsonObject jobject,SocketChannel cchannel){ 
    		
    		String name=jobject.get("name").getAsString();
    		jobject.addProperty("msgtype", 22);
    		jobject.addProperty("name", name);
    		jobject.addProperty("event", name+" is on line!");
    		Iterator<Socket>it=myMap.values().iterator();
    		
    		while(it.hasNext()){
    			
    			SocketChannel key=it.next().getChannel();
    			
    			try {
					key.write(ByteBuffer.wrap(new String(jobject.toString()).getBytes()));
					key.write(ByteBuffer.wrap(new String("\n").getBytes()));
					buffer.flip();
					buffer.clear();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
    		}
    		try {
    			cchannel.write(ByteBuffer.wrap(new String(jobject.toString()).getBytes()));
				cchannel.write(ByteBuffer.wrap(new String("\n").getBytes()));
				buffer.flip();
				buffer.clear();
			//	System.out.println("hello");
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    			
    		}
    	}
    	//���ߴ���
    	public void left(JsonObject jobject,SocketChannel cchannel){
    		String stream=jobject.get("msg").getAsString();
    		if(stream.compareTo("exit")==0){
    			String name=jobject.get("name").getAsString();
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
    					socket.getChannel().write(ByteBuffer.wrap(new String(json1.toString()).getBytes()));
    					socket.getChannel().write(ByteBuffer.wrap(new String("\n").getBytes()));
    					buffer.flip();
    					buffer.clear();
    					try {
    						
    						cchannel.socket().close();
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
    		 BroadcastLeft(jobject,cchannel);		
    	}
    			     
    public void BroadcastLeft(JsonObject jobject,SocketChannel cchannel){ 
    		
    	String name=jobject.get("name").getAsString();
		jobject.addProperty("msgtype", 23);
		jobject.addProperty("name", name);
		jobject.addProperty("event", name+" is left!");
		Iterator<Socket>it=myMap.values().iterator();
		
		while(it.hasNext()){
			
			SocketChannel key=it.next().getChannel();
			
			try {
				key.write(ByteBuffer.wrap(new String(jobject.toString()).getBytes()));
				key.write(ByteBuffer.wrap(new String("\n").getBytes()));
				buffer.flip();
				buffer.clear();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		try {
			cchannel.write(ByteBuffer.wrap(new String(jobject.toString()).getBytes()));
			cchannel.write(ByteBuffer.wrap(new String("\n").getBytes()));
			buffer.flip();
			buffer.clear();
		//	System.out.println("hello");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
      }
		
  //���߷���
	
  	public void sendmessage(JsonObject jobject,SocketChannel cchannel){
  		

	    String from = jobject.get("from").getAsString();
		String to = jobject.get("to").getAsString();
		String msg = jobject.get("msg").getAsString();
		jobject.addProperty("msgtype", 25);
		jobject.addProperty("event", msg);
		boolean flag = true;
		
      while (iterator.hasNext()){
		      
    	  
    	        java.util.Map.Entry<String, Socket> entry = iterator.next();
		        if(entry.getKey().compareTo(to)==0){
		        	flag = false;
		        	Socket socket = entry.getValue();                                                                                                     
					jobject.addProperty("ack", "ok");
		           try {
		        	socket.getChannel().write(ByteBuffer.wrap(new String(jobject.toString()).getBytes()));
		   			socket.getChannel().write(ByteBuffer.wrap(new String("\n").getBytes()));
		   			buffer.flip();
		   			buffer.clear();
		   			break;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				  
				  }
		       
			} 
				 if(flag){
				 try {
					
					 jobject.addProperty("ack", "fail");
    				 jobject.addProperty("reason", to+"is not on line now!"+"he will receve the message when he will on line!");
    				 cchannel.write(ByteBuffer.wrap(new String(jobject.toString()).getBytes()));
    				 cchannel.write(ByteBuffer.wrap(new String("\n").getBytes()));
    				 buffer.flip();
    				 buffer.clear();
    				
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 DbConnector db = DbConnector.getInstance();
				 int n=db.insertMessage(from, to,msg);
					
					if(n>0){
					         System.out.println("���Գɹ���");
				           }
				
			 }
			
		
		 
  			}
    	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try{
			while(!Thread.currentThread().isInterrupted()){
				
				int num = selector.select();
				if(num <= 0){
					Iterator<SocketChannel> it=channellist.iterator();
					while(it.hasNext()){
						it.next().register(getSelector(), SelectionKey.OP_READ);
					}
					continue;
				}
				
				Iterator<SelectionKey> it = selector.selectedKeys().iterator();
				try{
				while(it.hasNext()){
					SelectionKey key = it.next();
					//��ɾ���¼����������key
					it.remove();
					
					if(key.isValid() && key.isReadable()){
						
						SocketChannel cchannel = (SocketChannel)key.channel();
						// socket  out cchannel.write()  in  cchannel.read()   
					    //�����û��������رպ��쳣�ر� ????
					
						int readcnt = cchannel.read(buffer);
						if(readcnt<=0)
						{
							System.out.println("client normal close!");
							cchannel.close();
							key.cancel();
							continue;
						}
						
						String recvMsg=new String(buffer.array()).trim();
						System.out.println(recvMsg);
						JsonParser parser = new JsonParser();
						JsonElement json = parser.parse(recvMsg);
						JsonObject object = json.getAsJsonObject();
						
						int msgtype = object.get("msgtype").getAsInt();
						
						switch(msgtype){
						case 1:
							//�����¼��Ϣ
							login(object,cchannel);
							
							break;
						case 2:
							register(object,cchannel);
							break;
						 case 3:
							left(object,cchannel);
							break;
						case 4:
							
							sendmessage(object,cchannel);
							//System.out.println(4);
							break;
							
						
						}
						   
					//	System.out.println("recv msg:" + new String(buffer.array()).trim());
			         
					}
				}}catch(Exception e){
					//	e.printStackTrace();
				   	 System.out.println("close unformally!!");
				   	 break;
				         }
				
			}
		}catch(IOException e){
		    e.printStackTrace();
		}
		
	}
		
}

public class NewServer {
	
	private Selector selector;
	private ServerSocketChannel sschannel;
	private Work workTask;
	
	public NewServer() throws IOException {
		super();
		// TODO Auto-generated constructor stub
		//����selector����
		selector = Selector.open();
		//����server��channel
		sschannel = ServerSocketChannel.open();
		sschannel.bind(new InetSocketAddress("127.0.0.1", 6000));
		sschannel.configureBlocking(false);
		//ע��server channel��accept�¼�
		sschannel.register(selector, SelectionKey.OP_ACCEPT);
		
		//���������д�¼������߳�
		workTask = new Work();
		new Thread(workTask).start();
	}
	
	public void startServer() throws IOException{
		System.out.println("server is start!");
		while(!Thread.currentThread().isInterrupted()){
			int num = selector.select();
			if(num <= 0){
				continue;
			}
			
			//key(channel()) -> channel(socket()) -> socket
			Iterator<SelectionKey> it = selector.selectedKeys().iterator();
			while(it.hasNext()){
				SelectionKey key = it.next();
				//��ɾ���¼����������key
				it.remove();
				
				if(key.isValid() && key.isAcceptable()){
					SocketChannel cchannel = sschannel.accept();
					cchannel.configureBlocking(false);
					
					//��ӵ��µ��߳�����   
					workTask.getList().add(cchannel);
					workTask.getSelector().wakeup();
					
					
				}
			}
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			new  NewServer().startServer();
		    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

