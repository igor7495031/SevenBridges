package com.sb.main;

import java.util.ArrayList;
import java.util.Scanner;

import com.sb.request.FilesReq;
import com.sb.request.ProjectsReq;

public class Main {
	private static Scanner sc = null;
	
	public static void main(String[] args) {
		String[] line = null;
		sc = new Scanner(System.in);
		while((line = readLine()) != null) {
			String result = interpretNextCommand(line);
			if(result != null) {
				if(result.contains("ERROR")) {
					System.out.println(result);
				} else {
					System.out.println(result);
				}
			} else {
				System.out.println("line: "+line +"\n caused 'null' response!");
			}
		}
		sc.close();
	}
	
	private static String[] readLine() {
		String line = null;
		if(sc.hasNextLine()) {
			line = sc.nextLine();
		}
		if(line != null && !line.equals("")) {
			return line.split(" ");
		} else {
			return null;
		}
	}
	
	private static String interpretNextCommand(String[] line) {
		ArrayList<String> tokens = new ArrayList<String>();
		for(int i=0; i<line.length; i++) {
			tokens.add(line[i]);
		}
		String result = null;
		String authToken = null;
		boolean cmdFormat = false;
		boolean next = true;
		
		while(next) {
			String token = tokens.get(0);
			switch(token) {
			case "cgccli":
				if(!cmdFormat) {
					cmdFormat = true;
					tokens.remove(token);
					int t_ind = tokens.indexOf("--token");
					tokens.remove(t_ind);
					authToken = tokens.remove(t_ind);
				} else {
					return "ERROR: 'cgccli' needs to be first word!";
				}
				break;
			case "projects":
				if(cmdFormat) { 
					tokens.remove(token);
					result = (new ProjectsReq()).request(authToken, tokens);
					next = false;
				} else {
					return "ERROR: command syntax is not correct";
				}
				break;
			case "files":
				if(cmdFormat) {
					tokens.remove(token);
					result = (new FilesReq()).request(authToken, tokens);
					next = false;
				} else {
					return "ERROR: command syntax is not correct";
				}
				break;
			}
		}
		
		return result;
	}
}
