package com.example.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class Gateway {
	@Autowired
	private RestTemplate restTemplate;

	String url = "http://localhost:8081/info/";

	public static final String ACCEPT_HEADER_KEY = "Accept";
	public static final String ACCEPT_HEADER_VALUE = "application/json";

	private HttpEntity<String> setHeaders() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(ACCEPT_HEADER_KEY, ACCEPT_HEADER_VALUE);
		HttpEntity<String> request = new HttpEntity<>(httpHeaders);
		return request;
	}

	public String getInfo(String id) {
		ResponseEntity<String> response = null;
		try {
			response = restTemplate.exchange(url + id, HttpMethod.GET, setHeaders(), String.class);
			String info = response.getBody();
			return info;
		} catch (HttpClientErrorException e) {
			System.out.println("Client error occurred (4xx) with message: " + e.getMessage());
			throw e;
		} catch (HttpServerErrorException e) {
			System.out.println("Server error occurred (5xx) with message: " + e.getMessage());
			throw e;
		}
	}

}
