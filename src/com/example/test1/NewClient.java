package com.example.test1;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class NewClient {
	
	public static void main(String[] args){
		
		try {
			Socket client = new Socket("127.0.0.1", 6000);
			Scanner console = new Scanner(System.in);
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);

			while(true){
				System.out.println("input msg:");
				String msg = console.nextLine();
				if(msg.trim().compareTo("exit") == 0)
					break;
				out.println(msg);
			}
			
			client.close();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
}
