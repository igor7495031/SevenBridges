package com.sb.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FilesReq extends Request {
	private static ArrayList<String> metadata_possible_values;
	
	static {
		metadata_possible_values = new ArrayList<String>();
		metadata_possible_values.add("tcga");
		metadata_possible_values.add("tcga_grch38");
		metadata_possible_values.add("ccle");
		metadata_possible_values.add("cptac");
		metadata_possible_values.add("target");		
	}
	
	public String request(String authToken, ArrayList<String> tokens) {
		String subrequest = tokens.remove(0);
		String result = null;
		switch(subrequest) {
		case "list":
			result = _list(authToken, tokens);
			break;
		case "stat":
			result = _stat(authToken, tokens);
			break;
		case "update":
			result = _update(authToken, tokens);
			break;
		case "download":
			result = _download(authToken, tokens);
			break;
		default:
			System.out.println("'files' request, '"+subrequest+"', does not exist");
		}
		return result;
	}
	
	private String _list(String authToken, ArrayList<String> tokens) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		String value = null;
		Iterator<String> iter = tokens.iterator();
		boolean projectOrParentPresent = false;
		while(iter.hasNext()) {
			String token = iter.next();
			if(token.contains("=")) {
				String[] split = token.split("=");
				token = split[0];
				value = split[1];
			}
			switch(token) {
			case "--project":
				if(!projectOrParentPresent) {
					projectOrParentPresent = true;
					parameters.put("project", iter.next());
				} else {
					return "there are both 'project' and 'parent' parameter present";
				}
				break;
			case "--parent":
				if(!projectOrParentPresent) {
					projectOrParentPresent = true;
					parameters.put("parent", iter.next());
				} else {
					return "there are both 'project' and 'parent' parameter present";
				}
				break;
			case "name": case "tag":
				if(parameters.containsKey(token)) {
					Object v = parameters.get(token);
					if(v instanceof ArrayList<?>) {
						((ArrayList<Object>)v).add(value);
					} else {
						ArrayList<Object> al = new ArrayList<Object>();
						al.add(v);
						al.add(value);
						parameters.replace(token, al);
					}
				} else {
					parameters.put(token, value);
				}
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
					return "'fields' parameter value is null";
				}
				break;
			case "limit":
				int lim = 0;
				try{
					lim = Integer.parseInt(value);
				} catch(Exception e) {
					return "'limit' parameter is not a number";
				}
				parameters.put("limit", lim);
				break;
			default:
				if(token.contains(".")) {
					String f = token.substring(0, token.indexOf("."));
					String s = token.substring(token.indexOf(".")+1);
					
					if(f.equals("metadata")) {
						if(parameters.containsKey(token)) {
							Object v = parameters.get(token);
							if(v instanceof ArrayList<?>) {
								((ArrayList<Object>)v).add(value);
							} else {
								ArrayList<Object> al = new ArrayList<Object>();
								al.add(v);
								al.add(value);
								parameters.replace(token, al);
							}
						} else {
							parameters.put(token, value);
						}
					} else if(f.equals("origin")) {
						if(s.equals("dataset")) {
							if(!metadata_possible_values.contains(value)) {
								return "'origin.dataset' is not valid";
							}
						}
						if(parameters.containsKey(token)) {
							Object v = parameters.get(token);
							if(v instanceof ArrayList<?>) {
								((ArrayList<Object>)v).add(value);
							} else {
								ArrayList<Object> al = new ArrayList<Object>();
								al.add(v);
								al.add(value);
								parameters.replace(token, al);
							}
						} else {
							parameters.put(token, value);
						}
					} else {
						return "unknown parameter "+f;
					}
				} else {
					return "unknown parameter "+token;
				}
			}
		}
		
		// check if project or parent is set, if not error out!
		
		return GET("/files", defaultHeaders(authToken), parameters);
	}
	
	private String _stat(String authToken, ArrayList<String> tokens) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		String value = null;
		String file_id = null;
		Iterator<String> iter = tokens.iterator();
		while(iter.hasNext()) {
			String token = iter.next();
			if(token.contains("=")) {
				String[] split = token.split("=");
				token = split[0];
				value = split[1];
			}
			switch(token) {
			case "--file":
				file_id = iter.next();
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
					return "'fields' parameter value is null";
				}
				break;
			default:
				return "unknown parameter "+token;
			}
		}
		return GET("/files/"+file_id, defaultHeaders(authToken), parameters);
	}
	
	private String _update(String authToken, ArrayList<String> tokens) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		String value = null;
		String file_id = null;
		Iterator<String> iter = tokens.iterator();
		while(iter.hasNext()) {
			String token = iter.next();
			if(token.contains("=")) {
				String[] split = token.split("=");
				token = split[0];
				value = split[1];
			}
			switch(token) {
			case "--file":
				file_id = iter.next();
				break;
			case "name":
				parameters.put(token, value);
				break;
			case "tag":
				if(parameters.containsKey("tags")) {
					ArrayList<Object> al = (ArrayList<Object>)parameters.get("tags");
					al.add(value);
				} else {
					ArrayList<Object> al = new ArrayList<Object>();
					al.add(value);
					parameters.put("tags", al);
				}
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
					return "'fields' parameter value is null";
				}
				break;
			default:
				if(token.contains("metadata.")) {
					String f = token.substring(0, token.indexOf("."));
					String s = token.substring(token.indexOf(".")+1);
					
					if(parameters.containsKey(f)) {
						HashMap<String, Object> hm = (HashMap<String, Object>)parameters.get(f);
						hm.put(s, value);
					} else {
						HashMap<String, Object> hm = new HashMap<String, Object>();
						hm.put(s, value);
						parameters.put(f, hm);
					}
				} else {
					return "unknown parameter "+token;
				}
			}
		}
		HashMap<String, String> headers = (HashMap<String, String>) defaultHeaders(authToken);
		headers.put("Content-type", "application/json");
		return PATCH("/files/"+file_id, headers, parameters);
	}
	
	private String _download(String authToken, ArrayList<String> tokens) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		String value = null;
		String file_id = null;
		String output_file = null;
		Iterator<String> iter = tokens.iterator();
		while(iter.hasNext()) {
			String token = iter.next();
			if(token.contains("=")) {
				String[] split = token.split("=");
				token = split[0];
				value = split[1];
			}
			switch(token) {
			case "--file":
				file_id = iter.next();
				break;
			case "--dest":
				output_file = iter.next();
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
					return "'fields' parameter value is null";
				}
				break;
			default:
				return "unknown parameter "+token;
			}
		}
		HashMap<String, String> headers = (HashMap<String, String>) defaultHeaders(authToken);
		headers.put("Content-type", "application/json");
		String json = GET("/files/"+file_id+"/download_info", headers, parameters);
		long startTime = System.nanoTime();
		if(DOWNLOAD(json, output_file)) {
			long endTime = System.nanoTime();
			long duration = (endTime - startTime) / 1000000000;
			return "file location: "+output_file+" time to finish: "+duration+" sec";
		} else {
			return "download was not successful";
		}
	}

}
