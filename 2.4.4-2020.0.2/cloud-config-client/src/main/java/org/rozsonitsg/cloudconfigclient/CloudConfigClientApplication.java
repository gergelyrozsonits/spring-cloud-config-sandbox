package org.rozsonitsg.cloudconfigclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class CloudConfigClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudConfigClientApplication.class, args);
	}
	
	@lombok.Value
	@AllArgsConstructor
	public static class Config {
		String atValueConfig;
	}
	
	@RestController
	@RequestMapping("/config")
	public static class ConfigController {
//		@Value("${foo}")
		@Value("foo")
		private String atValueConfig;

		@GetMapping
		public Mono<Config> get() {
			return Mono.just(new Config(atValueConfig));
		}
	}
}
