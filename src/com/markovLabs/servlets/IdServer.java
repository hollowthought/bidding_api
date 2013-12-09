package com.markovLabs.servlets;

import java.io.BufferedReader;
import java.io.PrintWriter;
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

@WebServlet("/id")
public class IdServer  extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static final String OPERATION_FIELD = "op";
	private int counter=0;
	private final Set<Integer> ids=new HashSet<Integer>();
	
	public static String USERID_FIELD="user_id";

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, java.io.IOException {
		BufferedReader reader = req.getReader();
		StringBuilder buf = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			buf.append(line);
		}
		JSONObject json = (JSONObject) JSONValue.parse(buf.toString());
		StringBuilder mesg=new StringBuilder("{\"resp\":\"");
		if (json == null) {
			mesg.append("FAIL: No valid JSON string was received.");
		} else {
			Integer id = ((Long) json.get(USERID_FIELD)).intValue();
			Integer operation = ((Long)json.get(OPERATION_FIELD)).intValue();
			switch(operation){
				case 0:mesg.append(createID());break;
				case 1:mesg.append(search(id));break;
			}
		}
		mesg.append("\"}");
		PrintWriter printer = resp.getWriter();
		try {
			printer.print(mesg.toString());
			printer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private String search(Integer id){
		return Boolean.toString(ids.contains(id));
	}
	
	private Integer createID(){
		synchronized(this){
			counter++;
			ids.add(new Integer(counter));
		}
		return counter;
	}
	
}
