package org.rozsonitsg.cloudconfigclient;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"spring.cloud.config.enabled=false"})
class CloudConfigClientApplicationTests {

	@Test
	void contextLoads() {
	}

}
