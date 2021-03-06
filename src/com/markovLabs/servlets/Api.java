package com.markovLabs.servlets;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.markovLabs.util.BidProcessor;
import com.markovLabs.util.NetUtil;
import com.markovLabs.bid.Bid;
import com.markovLabs.util.JMSClient;

@WebServlet("/bid")
public class Api extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static final String BID_ID_FIELD = "bid_id";
	private static final String OPERATION_FIELD = "op";
	private JMSClient jmsClient;
	
	private int counter=0;
	private final Set<Integer> ids=new HashSet<Integer>();

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		jmsClient = new JMSClient();
	}

	// this method reads a json from the request and then perform the following
	// operations: create, delete bid and proccess bid
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, java.io.IOException {

		BufferedReader reader = req.getReader();
		StringBuilder buf = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			buf.append(line);
		}
		JSONObject json = (JSONObject) JSONValue.parse(buf.toString());

		StringBuilder mesg = new StringBuilder("{\"resp\":\""); // message returned back to the client
		if (json == null) {
			mesg.append("FAIL: No valid JSON string was received.");
		} else {
			Object user_id = json.get(IdServer.USERID_FIELD);
			Object operation = json.get(OPERATION_FIELD);

			if (user_id == null || operation == null) {
				mesg.append("FAIL: JSON string fields are not valid");
			} else {
				Bid bid = null;
				Integer op = ((Long) operation).intValue();
				if (BidProcessor.isValidOperation(op)) {
					if (PoolOperationsHandler.CREATE_OP == op) {
						Integer bid_id = generateUniqueID();
						bid = new Bid(((Long) user_id).intValue(), bid_id);
						boostrapBidProcessor(bid,op);
						mesg.append(bid_id);
					} else {
						//TODO: use RMI to get the value from the pool
						if (PoolOperationsHandler.SEARCH_OP == op) {
							Integer bid_id=((Long)json.get(BID_ID_FIELD)).intValue();
							boolean id_exist=ids.contains(bid_id);
							mesg.append(Boolean.toString(id_exist));
							if(id_exist){
								mesg.append("\",\"bidValue\":\"");
								mesg.append(getBidValue(bid_id));
							}
						} else {
							bid = new Bid((Integer) user_id,((Long) json.get(BID_ID_FIELD)).intValue(),(Double) json.get("bid"));
							boostrapBidProcessor(bid,op);
							mesg.append("SUCCESS");
						}
					}
				} else {
					mesg.append("FAIL: Invalid operation code.");
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
	
	private String getBidValue(Integer bid_id) {
		try {
			URL req=new URL(NetUtil.POOL_URL+"?s=1");
			HttpURLConnection con=(HttpURLConnection) req.openConnection();
			OutputStream out=con.getOutputStream();
			out.write(bid_id);
			out.flush();
			BufferedReader reader=new BufferedReader(new InputStreamReader(con.getInputStream()));
			String result=reader.readLine();
			out.close();
			return result;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void boostrapBidProcessor(Bid bid, Integer op){
		Thread runner = new Thread(new BidProcessor(bid,jmsClient, op));
		runner.start();
	}

	private synchronized Integer generateUniqueID() {
		counter++;
		ids.add(new Integer(counter));
		return counter;
	}

	public void destroy() {
		jmsClient.destroyClient();
	}

}
