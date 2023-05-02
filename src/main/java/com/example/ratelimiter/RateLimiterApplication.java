package com.example.ratelimiter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class RateLimiterApplication {

	public static void main(String[] args) {
		SpringApplication.run(RateLimiterApplication.class, args);
	}

	@RestController
	class HelloController {

		@GetMapping("/")
		public Map<String, String> hello() {
			HashMap<String, String> map = new HashMap<>();
			map.put("status", "200");
			map.put("message", "rate limit api");
			return map;
		}

	}
}
