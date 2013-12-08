package com.markovLabs.util;

import org.json.simple.JSONObject;

public class BidProcessor implements Runnable{
	private Integer id;
	private Integer operation;
	private JSONObject json;
	private JMSClient jmsClient;
	
	private static final int CREATE_OP = 0;
	private static final int DELETE_OP = 1;
	private static final int PROCESS_BID = 2;

	
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
		NetUtil.sendRequestToPool(id,DELETE_OP);
	}

	private void createBid(Integer id) {
		NetUtil.sendRequestToPool(id,CREATE_OP);
	}
}
