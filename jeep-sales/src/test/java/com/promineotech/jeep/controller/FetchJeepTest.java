package com.promineotech.jeep.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.jdbc.JdbcTestUtils;

import com.promineotech.jeep.entity.Jeep;
import com.promineotech.jeep.entity.JeepModel;

import lombok.Getter;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = {
    "classpath:flyway/migrations/V1.0__Jeep_Schema.sql",
    "classpath:flyway/migrations/V1.1__Jeep_Data.sql"}, 
    config = @SqlConfig(encoding = "utf-8"))

class FetchJeepTest {
	
	@Autowired
	@Getter 
	private TestRestTemplate restTemplate;
	
	@LocalServerPort
	private int serverPort;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Test
	void testThatJeepsAreReturnedWhenAValidModelAndTrimAreSupplied() {
		JeepModel model = JeepModel.WRANGLER;
		String trim = "Sport";
		String uri = String.format("http://localhost:%d/jeeps?model=%s&trim=%s", 
				serverPort, model, trim);
		
		ResponseEntity<Jeep> response = 
				getRestTemplate().exchange(uri, HttpMethod.GET, null,
						new ParameterizedTypeReference<>() {});

		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		List<Jeep> expected = buildExpected();
		// System.out.println(expected);
		assertThat(response.getBody()).isEqualTo(expected);
			}

	private List<Jeep> buildExpected() {
		List<Jeep> list = new LinkedList<>();
	
		list.add(Jeep.builder()
				.modelId(JeepModel.WRANGLER)
				.trimLevel("Sport")
				.numDoors(2)
				.wheelSize(17)
				.basePrice(new BigDecimal("28475.00"))
				.build());
		
		list.add(Jeep.builder()
				.modelId(JeepModel.WRANGLER)
				.trimLevel("Sport")
				.numDoors(4)
				.wheelSize(17)
				.basePrice(new BigDecimal("31975.00"))
				.build());
		System.out.println(list);
		return list;
	}

//	private TestRestTemplate getRestTemplate() {
//		return restTemplate;
//	}

}
