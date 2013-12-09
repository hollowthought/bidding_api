package com.markovLabs.tests;

import org.junit.Before;
import org.junit.Test;

import com.markovLabs.bid.Bid;
import com.markovLabs.util.JMSClient;

public class TestJMSClient {
	
	private JMSClient jmsClient;

	@Before
	public void configureJMSClient(){
		jmsClient=new JMSClient();
	}
	
	@Test
	public void test0(){
		Bid bid=new Bid(1,10,101.0);
		jmsClient.sendMessage(bid);
	}
}
