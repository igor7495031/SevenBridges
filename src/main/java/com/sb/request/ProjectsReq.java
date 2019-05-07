package com.sb.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ProjectsReq extends Request {
	
	public String request(String authToken, ArrayList<String> tokens) {
		String subrequest = tokens.remove(0);
		String result = null;
		switch(subrequest) {
		case "list":
			result = _list(authToken, tokens);
			break;
		default:
			System.out.println("Project request, "+subrequest+", is not valid parameter");
			result = null;
		}
		return result;
	}

	private String _list(String authToken, ArrayList<String> tokens) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		String value = null;
		Iterator<String> iter = tokens.iterator();
		while(iter.hasNext()) {
			String token = iter.next();
			if(token.contains("=")) {
				String[] split = token.split("=");
				token = split[0];
				value = split[1];
			}
			switch(token) {
			case "name":
				if(parameters.containsKey("name")) {
					Object v = parameters.get("name");
					if(v instanceof ArrayList<?>) {
						((ArrayList<Object>)v).add(value);
					} else {
						ArrayList<Object> al = new ArrayList<Object>();
						al.add(v);
						al.add(value);
						parameters.replace("name", al);
					}
				} else {
					parameters.put("name", value);
				}
				break;
			case "offset":case "limit":
				parameters.put(token, value);
				break;
			case "fields":
				if(value != null) {
					String[] niz = value.split(",");
					ArrayList<String> fields = new ArrayList<String>();
					for(int i=0; i<niz.length; i++) {
						fields.add(niz[i]);
					}
					parameters.put("fields", fields);
				} else {
					return "fields parameter value is null";
				}
				break;
			default:
				return "unknown parameter "+token;
			}
		}
		return GET("/projects", defaultHeaders(authToken), parameters);
	}
}
