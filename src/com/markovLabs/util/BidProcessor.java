package com.markovLabs.util;

import com.markovLabs.bid.Bid;
import com.markovLabs.servlets.PoolOperationsHandler;

public class BidProcessor implements Runnable{
	private Bid bid;
	private JMSClient jmsClient;
	private Integer operation;
	private static final int PROCESS_BID = 2;

	
	public BidProcessor(Bid bid, JMSClient jmsClient2,Integer operation) {
		this.operation=operation;
		this.bid=bid;
	}

	public static boolean isValidOperation(Integer operation){
		if(PoolOperationsHandler.CREATE_OP==operation || PoolOperationsHandler.DELETE_OP==operation || PROCESS_BID==operation){
			return true;
		}
		return false;
	}

	@Override
	public void run() {
		switch (operation) {
			case PoolOperationsHandler.CREATE_OP:createBid();break;
			case PoolOperationsHandler.DELETE_OP:deleteBid();break;
			case PROCESS_BID:processBid();break;
		}
	}	

	private void processBid() {
		jmsClient.sendMessage(bid);
	}

	private void deleteBid() {
		NetUtil.sendRequestToPool(null,bid.getBid_id(),PoolOperationsHandler.DELETE_OP);
	}

	private void createBid() {
		NetUtil.sendRequestToPool(bid.getUserId(),bid.getBid_id(),PoolOperationsHandler.CREATE_OP);
	}
}
