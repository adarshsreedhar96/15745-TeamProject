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
import static org.springframework.http.ResponseEntity.ok;

@Component
public class Gateway {
	@Autowired
	private RestTemplate restTemplate;

	private static String url = "http://localhost:8081/info/";

	public static final String ACCEPT_HEADER_KEY = "Accept";
	public static final String ACCEPT_HEADER_VALUE = "application/json";

	private HttpEntity<String> setHeaders() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(ACCEPT_HEADER_KEY, ACCEPT_HEADER_VALUE);
		HttpEntity<String> request = new HttpEntity<>(httpHeaders);
		return request;
	}

	public String getInfo(String id) {
		String urlWithParam = url + id;
		String response = getInfoCall(urlWithParam);
		return response;
	}

	public String getInfoCall(String url) {
		ResponseEntity<String> response = null;
		try {
			response = restTemplate.exchange(url, HttpMethod.GET, setHeaders(), String.class);
			return response.getBody();
		} catch (HttpClientErrorException e) {
			System.out.println("Client error occurred (4xx) with message: " + e.getMessage());
			throw e;
		} catch (HttpServerErrorException e) {
			System.out.println("Server error occurred (5xx) with message: " + e.getMessage());
			throw e;
		}
	}

}
