package com.markovLabs.util;
import java.util.Properties;

import javax.jms.*;
import javax.naming.*;

public class JMSClient {
	private static final String QUEUE_NAME="java:jboss/exported/jms/queue/tst";
	private static final String QUEUE_CONNECTION="java:/ConnectionFactory";
	
	private QueueConnection qConnect = null;
	private QueueSession qSession = null; 
	private Queue queue = null;
	
	public JMSClient(){
		try {
		/*	Properties env = new Properties(); 
			env.put(Context.SECURITY_PRINCIPAL, "system");
			env.put(Context.SECURITY_CREDENTIALS, "manager"); 
			env.put(Context.INITIAL_CONTEXT_FACTORY,"org.apache.activemq.jndi.ActiveMQInitialContextFactory");
			env.put(Context.PROVIDER_URL, "tcp://localhost:61616"); */
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
	
	public void sendMessage(Integer id, double bid){
		try{
			MapMessage msg = qSession.createMapMessage();
			msg.setInt("id",id);
			msg.setDouble("bid", bid);
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
