package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@SpringBootApplication
@RestController
@EnableOAuth2Sso
public class SimpleApplication extends WebSecurityConfigurerAdapter {

	@Autowired
	private OAuth2ClientContext context;

	@Autowired
	private RestTemplateBuilder templates;

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate template = templates.build();
		template.getInterceptors().add((request, body, chain) -> {
			request.getHeaders().add("Authorization",
					"Bearer " + context.getAccessToken().getValue());
			return chain.execute(request, body);
		});
		return template;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.httpBasic().and().authorizeRequests().antMatchers("/index.html", "/")
				.permitAll().anyRequest().authenticated().and().logout()
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

@Configuration
@Order(0)
class ResourceServerSecurityConfiguration extends WebSecurityConfigurerAdapter {
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.httpBasic().and().antMatcher("/greeting").authorizeRequests().anyRequest()
				.authenticated();
	}
}
