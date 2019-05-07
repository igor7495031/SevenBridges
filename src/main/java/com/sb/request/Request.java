package com.sb.request;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequestWithBody;

public class Request {
	private static String baseUrl = "https://cgc-api.sbgenomics.com/v2";
	
	protected Map<String, String> defaultHeaders(String authToken) {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("accept", "application/json");
		headers.put("X-SBG-Auth-Token", authToken);
		return headers;
	}
	
	private void setQueryString(GetRequest request, Map<String, Object> parameters) {
		if (parameters != null) {
			for(Entry<String, Object> param : parameters.entrySet()) {
				if (param.getValue() instanceof ArrayList<?>) {
					ArrayList<?> list = (ArrayList<?>) param.getValue();
					for(Object value : list) {
						request.queryString(param.getKey(), value);
					}
				} else {
					request.queryString(param.getKey(), param.getValue());
				}
			}
		}
	}
	
	protected String GET(String path, Map<String, String> headers, Map<String, Object> parameters) {
		GetRequest request = Unirest.get(baseUrl + path);
		request.headers(headers);
		setQueryString(request, parameters);
		HttpResponse<String> response = null;
		try {
			response = request.asString();
		} catch (UnirestException e) {
			e.printStackTrace();
		}
		return response != null ? response.getBody() : null;
	}
	
	protected String PATCH(String path, Map<String, String> headers, Map<String, Object> fields) {
		String body = null;
		try {
			body = new ObjectMapper().writeValueAsString(fields);
		} catch (JsonProcessingException e1) {
			e1.printStackTrace();
		}
		HttpRequestWithBody request = Unirest.patch(baseUrl + path);
		request.headers(headers);
		request.body(body);
		HttpResponse<String> response = null;
		try {
			response = request.asString();
		} catch (UnirestException e) {
			e.printStackTrace();
			return null;
		}
		return response != null ? response.getBody() : null;
	}

	protected boolean DOWNLOAD(String json, String output_file) {
		FileOutputStream fileOutputStream = null;
		try {
		    com.fasterxml.jackson.databind.JsonNode jsonUrl = (new ObjectMapper()).readTree(json);
		    String fileUrl = jsonUrl.get("url").asText();
		    
		    BufferedInputStream in = new BufferedInputStream(new URL(fileUrl).openStream());
	    	fileOutputStream = new FileOutputStream(output_file);
		    byte dataBuffer[] = new byte[8192];
		    int bytesRead;
		    while ((bytesRead = in.read(dataBuffer, 0, 8192)) != -1) {
		        fileOutputStream.write(dataBuffer, 0, bytesRead);
		    }
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if(fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
		}
		return true;
	}
}
