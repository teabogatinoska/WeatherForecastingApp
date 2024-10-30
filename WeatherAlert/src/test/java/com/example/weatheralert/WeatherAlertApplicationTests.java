package com.example.WeatherAlert;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class WeatherAlertApplicationTests {

	@Test
	void contextLoads() {
	}

}
