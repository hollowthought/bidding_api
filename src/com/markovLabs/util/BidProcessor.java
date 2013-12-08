package com.markovLabs.util;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.simple.JSONObject;

public class BidProcessor implements Runnable{
	private Integer id;
	private Integer operation;
	private JSONObject json;
	private JMSClient jmsClient;
	
	private static final int CREATE_OP = 0;
	private static final int DELETE_OP = 1;
	private static final int PROCESS_BID = 2;
	private static final String POOL_URL="http://localhost:8080/bid_pool_mngr/handler";
	
	public BidProcessor(Integer id,Integer operation, JSONObject json, JMSClient jmsClient){
		this.id=id;
		this.operation=operation;
		this.json=json;
		this.jmsClient=jmsClient;
	}
	
	public boolean isValidOperation(){
		if(CREATE_OP==operation || DELETE_OP==operation || PROCESS_BID==operation){
			return true;
		}
		return false;
	}

	@Override
	public void run() {
		switch (operation) {
			case CREATE_OP:createBid(id);break;
			case DELETE_OP:deleteBid(id);break;
			case PROCESS_BID:processBid(json);break;
		}
	}
	

	private void processBid(JSONObject json) {
		jmsClient.sendMessage(id,(Double) json.get("bid"));
	}

	private void deleteBid(Integer id) {
		sendRequestToPool(id,DELETE_OP);
	}

	private void createBid(Integer id) {
		sendRequestToPool(id,CREATE_OP);
	}
	
	//it can be improved by using connection pooling 
	private void sendRequestToPool(Integer dat,int code){
		try {
			URL req=new URL(POOL_URL);
			HttpURLConnection con=(HttpURLConnection) req.openConnection();
			OutputStream out=con.getOutputStream();
			out.write(code);
			out.write(id);
			out.flush();
			out.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
