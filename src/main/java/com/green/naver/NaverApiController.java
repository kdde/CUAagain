package com.green.naver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class NaverApiController {
	
	@Value("${naver.api.client-id}")
	String CLIENT_ID;
	@Value("${naver.api.client-secret}")
	String CLIENT_SECRET;
	
	@GetMapping("/naver/auth2")
	public String naver(String code, String state,Model model) throws Exception {
		
		//*
		//access 토근 
		String apiURL="https://auth.worksmobile.com/oauth2/v2.0/token";
		apiURL += "?code="+code;
		apiURL += "&client_id="+CLIENT_ID;
		apiURL += "&client_secret="+CLIENT_SECRET;
		apiURL += "&grant_type=authorization_code";
		
		URL url=new URL(apiURL);
		
		HttpURLConnection con=(HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		//con.setRequestProperty("content-type", "application/x-www-form-urlencoded");
		int responseCode=con.getResponseCode();
		String responseJSONData=null;
		if(responseCode == HttpURLConnection.HTTP_OK) {
			responseJSONData=readBody(con.getInputStream());
			System.out.println("정상");
		}else {
			responseJSONData=readBody(con.getErrorStream());
			System.out.println("에러");
		}
		con.disconnect();
		//*/
		//System.out.println(responseData); JSON 형식 문자열데이터
		ObjectMapper mapper=new ObjectMapper();
		
		NaverTokenDTO dto=mapper.readValue(responseJSONData, NaverTokenDTO.class);
		System.out.println(dto);
		OrgResponse orgResponse=getOrgUnit(dto);
		//getOrgUnit(responseJSONData);
		//System.out.println(">>>:"+responseJSONData);
		System.out.println("====================");
		System.out.println(orgResponse);
		model.addAttribute("naver", orgResponse.getOrgUnits());
		return "naver/naver-auth2";
	}
	
	//{
	//"access_token":"kr1AAAA4ujH9bhGR+bz/hdSTb4+ULzv+YJFUDYlF7oPHRPO1yk3sVkOkGIhGij9LnLcz58VHlarNYz41qB9xSvuIhEeX9s7HApPEOcqkPlP52vEAdNOjkyS8jTPSRHO70FJdbPcSxJb2dnfhzG1YnY+15cvkVM8zs+Frh9tXzrjDEB3vzSmh6Vs24hvdWjK6ntb7/N2v7yIl0H5nG5RfEMg/3XvUT/odkEfpZNZ4b4PI1htNC1PspHbmcacFtW2QMZTN4IwL9vmjWFfTJdLf4YsGQaMnJyOtyZsHZBV8tPdDRWG5VHkk7+2JzjB7oAZlE9KwoZLKA==",
	//"refresh_token":"kr1AAAAg8fiSkokTU3RCr5kExu0VkkDm7znc/ePNhV9gUHJnCccwHX7UoB72mW+h65UgglkrAn0Cdr7/HUcppL79JjzAPnpGulu0uYvcrYov8GHWoImM0Rbx6NbozEiRLZsQElZv/9SCQUn1Ae9Zs+Mq3UC1psxHnYDTRoHTi2E423mrr3ylxFlNB9/+DYTafEAAVWM2w==",
	//"scope":"directory,directory.read,orgunit,orgunit.read",
	//"token_type":"Bearer",
	//"expires_in":"86400"
	//}

	private OrgResponse getOrgUnit(NaverTokenDTO dto) throws IOException {
		String apiURL="https://www.worksapis.com/v1.0/orgunits";
		apiURL +="?domainId=300065308";
		
		
		URL url=new URL(apiURL);
		HttpURLConnection con=(HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		//"Bearer {token}"
		con.setRequestProperty("Authorization", "Bearer "+dto.getAccess_token());
		
		int responseCode=con.getResponseCode();
		String responseJSONData=null;
		if(responseCode == HttpURLConnection.HTTP_OK) {
			responseJSONData=readBody(con.getInputStream());
			System.out.println(">>>정상");
		}else {
			responseJSONData=readBody(con.getErrorStream());
			System.out.println(">>>에러");
		}
		con.disconnect();
		//System.out.println(responseJSONData);
		ObjectMapper mapper=new ObjectMapper();
		OrgResponse reponse=mapper.readValue(responseJSONData, OrgResponse.class);
		return reponse;
		
	}

	//응답데이터를 스트림을 통해서 한줄씩읽어서 문자열로 리턴
	private String readBody(InputStream inputStream) throws IOException {
		InputStreamReader streamReader=new InputStreamReader(inputStream);
		BufferedReader lineReader=new BufferedReader(streamReader);
		
		StringBuilder responseBody=new StringBuilder();
		
		String data;
		while((data=lineReader.readLine()) != null) {
			responseBody.append(data);
		}
		
		lineReader.close();
		streamReader.close();
		return responseBody.toString();
	}

}