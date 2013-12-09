package com.markovLabs.util;

import org.json.simple.JSONObject;

import com.markovLabs.servlets.PoolOperationsHandler;

public class BidProcessor implements Runnable{
	private Integer bid_id;
	private Integer operation;
	private JSONObject json;
	private JMSClient jmsClient;
	
	private static final int PROCESS_BID = 2;
	private Integer user_id;

	
	public BidProcessor(Integer id,Integer operation, Integer user_id, JSONObject json, JMSClient jmsClient){
		this.bid_id=id;
		this.operation=operation;
		this.json=json;
		this.jmsClient=jmsClient;
		this.user_id=user_id;
	}
	
	public boolean isValidOperation(){
		if(PoolOperationsHandler.CREATE_OP==operation || PoolOperationsHandler.DELETE_OP==operation || PROCESS_BID==operation){
			return true;
		}
		return false;
	}

	@Override
	public void run() {
		switch (operation) {
			case PoolOperationsHandler.CREATE_OP:createBid(bid_id,user_id);break;
			case PoolOperationsHandler.DELETE_OP:deleteBid(bid_id);break;
			case PROCESS_BID:processBid(json);break;
		}
	}
	

	private void processBid(JSONObject json) {
		jmsClient.sendMessage(user_id,bid_id,(Double) json.get("bid"));
	}

	private void deleteBid(Integer bid_id) {
		NetUtil.sendRequestToPool(null,bid_id,PoolOperationsHandler.DELETE_OP);
	}

	private void createBid(Integer bid_id,Integer user_id) {
		NetUtil.sendRequestToPool(user_id,bid_id,PoolOperationsHandler.CREATE_OP);
	}
}
