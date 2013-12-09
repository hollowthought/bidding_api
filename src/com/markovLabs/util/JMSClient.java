package com.markovLabs.util;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

import com.markovLabs.bid.Bid;

public class JMSClient {
	private static final String QUEUE_NAME="java:jboss/exported/jms/queue/tst";
	private static final String QUEUE_CONNECTION="java:/ConnectionFactory";
	
	private QueueConnection qConnect = null;
	private QueueSession qSession = null; 
	private Queue queue = null;
	
	public JMSClient(){
		try {
			Context ctx = new InitialContext();
			QueueConnectionFactory qFactory = (QueueConnectionFactory) ctx.lookup(QUEUE_CONNECTION);
			qConnect = qFactory.createQueueConnection();
			qSession = qConnect.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			queue = (Queue)ctx.lookup(QUEUE_NAME);
			qConnect.start();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void sendMessage(Bid bid){
		try{
			MapMessage msg = qSession.createMapMessage();
			msg.setInt(Bid.ID_FIELD_NAME,bid.getUserId());
			msg.setInt(Bid.BID_FIELD_NAME, bid.getBid_id());
			msg.setDouble(Bid.BID_FIELD_NAME_VALUE, bid.getBidValue());
			msg.setInt("mgmt", 0);
			msg.setJMSReplyTo(queue);
			QueueSender qSender = qSession.createSender(queue); 
			qSender.send(msg);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void destroyClient(){
		try {
			qConnect.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
