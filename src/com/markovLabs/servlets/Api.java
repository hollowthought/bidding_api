package com.markovLabs.servlets;

import java.io.BufferedReader;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.markovLabs.util.BidProcessor;
import com.markovLabs.util.JMSClient;

@WebServlet("/bid")
public class Api extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static final String BID_ID_FIELD = "bid_id";
	private static final String OPERATION_FIELD = "op";
	private JMSClient jmsClient;
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		jmsClient=new JMSClient();
	}

	// this method reads a json from the request and then perform the following
	// operations: create, delete bid and proccess bid
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, java.io.IOException {

		BufferedReader reader = req.getReader();
		StringBuilder buf = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			buf.append(line);
		}
		JSONObject json = (JSONObject) JSONValue.parse(buf.toString());

		StringBuilder mesg =new StringBuilder("{\"resp\":\"");  // message returned back to the client
		if (json == null) {
			mesg.append("FAIL: No valid JSON string was received.");
		} else {
			Object user_id = json.get(IdServer.USERID_FIELD);
			Object bid_id = json.get(BID_ID_FIELD);
			Object operation = json.get(OPERATION_FIELD);

			if (user_id == null || operation == null || bid_id==null) {
				mesg.append("FAIL: JSON string fields are not valid");
			} else {
				BidProcessor bidProcessor=new BidProcessor((Integer)bid_id,(Integer)operation,(Integer)user_id,json,jmsClient);
				if(!bidProcessor.isValidOperation()){
					mesg.append("FAIL: Invalid operation code.");
				}
				else{
					Thread runner=new Thread(bidProcessor);
					runner.start();
					mesg.append("SUCCESS");
				}
			}
		}
		mesg.append("\"}");
		PrintWriter printer = resp.getWriter();
		try {
			printer.print(mesg.toString());
			printer.close();
		} catch (Exception e) {

		}

	}
	
	public void destroy(){
		jmsClient.destroyClient();
	}

}
