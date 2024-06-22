package com.bok.iso.mngr;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BokManagerApplicationTests {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());	

	@Test
	void contextLoads() {

		logger.info("---------------------------------");
		logger.info("--- SpringBootTest");
		logger.info("--- 2024. 6. 22, hyonchul.oh");
		logger.info("---------------------------------");
	}

}
