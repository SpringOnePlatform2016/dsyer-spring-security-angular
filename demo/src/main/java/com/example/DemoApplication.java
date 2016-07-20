package com.example;

import java.util.UUID;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class DemoApplication extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and().httpBasic()
				.and().authorizeRequests().antMatchers("/index.html", "/").permitAll()
				.anyRequest().authenticated().and().logout().logoutSuccessUrl("/");
	}

	@RequestMapping("/resource")
	public Greeting greeting(@RequestParam(required = false) String foo) {
		return new Greeting("Hello World");
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}

class Greeting {

	private String id = UUID.randomUUID().toString();

	private String msg;

	@SuppressWarnings("unused")
	private Greeting() {
	}

	public Greeting(String msg) {
		this.msg = msg;
	}

	public String getId() {
		return id;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "Greeting [msg=" + msg + "]";
	}

}
