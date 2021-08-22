package com.github.dinolupo.cm;

import com.github.dinolupo.cm.business.boundary.ProjectController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CmApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	ProjectController controller;

	@Test
	void contextLoads() {
		assertThat(controller).isNotNull();
	}

}
