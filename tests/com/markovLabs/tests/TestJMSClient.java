package com.markovLabs.tests;

import org.junit.Before;
import org.junit.Test;

import com.markovLabs.util.JMSClient;

public class TestJMSClient {
	
	private JMSClient jmsClient;

	@Before
	public void configureJMSClient(){
		jmsClient=new JMSClient();
	}
	
	@Test
	public void test0(){
		jmsClient.sendMessage(10,101.0);
	}
}
