package com.example;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@RestController
public class SimpleApplication extends WebSecurityConfigurerAdapter {

	@Autowired
	private HttpSession session;

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getInterceptors().add((request, body, execution) -> {
			request.getHeaders().add("Cookie", "SESSION=" + session.getId());
			return execution.execute(request, body);
		});
		return restTemplate;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.httpBasic().and().authorizeRequests().antMatchers("/index.html", "/")
				.permitAll().anyRequest().authenticated().and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.ALWAYS).and().logout()
				.logoutSuccessUrl("/").and().csrf()
				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
	}

	@RequestMapping("/greeting")
	public Map<String, Object> greeting() {
		return restTemplate().exchange("http://localhost:9000", HttpMethod.GET, null,
				new ParameterizedTypeReference<Map<String, Object>>() {
				}).getBody();
	}

	public static void main(String[] args) {
		SpringApplication.run(SimpleApplication.class, args);
	}
}