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

@WebServlet("/api")
public class Api extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String USERID_FIELD = "user_id";
	private static final String OPERATION_FIELD = "op";
	private static final int CREATE_OP = 0;
	private static final int DELETE_OP = 1;
	private static final int PROCESS_BID = 2;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
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

		String mesg = "SUCCESS"; // message returned back to the client
		if (json == null) {
			mesg = "FAIL: No JSON string was received.";
		} else {
			Object id = json.get(USERID_FIELD);
			Object operation = (Integer) json.get(OPERATION_FIELD);

			if (id == null || operation == null) {
				mesg = "FAIL: JSON string not valid";
			} else {
				switch ((Integer) operation) {
					case CREATE_OP:createBid((Integer) id);break;
					case DELETE_OP:deleteBid((Integer) id);break;
					case PROCESS_BID:processBid(json);break;
					default:mesg = "FAIL: Invalid operation code.";
				}
			}
		}
		PrintWriter printer = resp.getWriter();
		try {
			printer.print(mesg);
			printer.close();
		} catch (Exception e) {

		}

	}

	private void processBid(JSONObject json) {
		// TODO Auto-generated method stub

	}

	private void deleteBid(Integer id) {
		// TODO Auto-generated method stub

	}

	private void createBid(Integer id) {
		// TODO Auto-generated method stub

	}

}
