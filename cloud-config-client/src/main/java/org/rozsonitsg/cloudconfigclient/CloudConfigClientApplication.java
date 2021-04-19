package org.rozsonitsg.cloudconfigclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Data;
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
		String configurationPropertiesConfig;
		String environmentConfig;
	}
	
	@Data
	@RefreshScope
	@Configuration
	@ConfigurationProperties("foo")
	public static class ConfigurationPropertiesConfig {
		String bar;
	}
	
	@RefreshScope
	@RestController
	@RequestMapping("/config")
	public static class ConfigController {
		@Value("${foo.bar}")
		private String atValueBasedConfig;
		@Autowired
		private ConfigurationPropertiesConfig configurationPropertiesConfig;
		@Autowired
		private Environment environment;

		@GetMapping
		public Mono<Config> get() {
			return Mono.just(new Config(
				atValueBasedConfig,
				configurationPropertiesConfig.getBar(),
				environment.getProperty("foo.bar")	
			));
		}
	}
}
