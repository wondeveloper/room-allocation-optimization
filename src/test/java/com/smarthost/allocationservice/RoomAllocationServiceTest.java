package com.smarthost.allocationservice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarthost.allocationservice.config.dto.RoomQueryRequest;
import com.smarthost.allocationservice.config.dto.RoomQueryResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("integration")
public class RoomAllocationServiceTest {

	private static final Logger logger = LoggerFactory.getLogger(RoomAllocationServiceTest.class);

	private static List<BigDecimal> validPrices;

	private static List<BigDecimal> invalidPrices;

	@Autowired
	private MockMvc mockMvc;

	private static final ObjectMapper mapper = new ObjectMapper();

	@BeforeAll
	public static void setUp() {
		InputStream inputStreamForValidInput = Objects.requireNonNull(RoomAllocationServiceTest.class.getClassLoader().getResourceAsStream("ValidInput.json"));
		InputStream inputStreamForInvalidInput = Objects.requireNonNull(RoomAllocationServiceTest.class.getClassLoader().getResourceAsStream("invalidInput.json"));
		try {
			validPrices = mapper.readValue(inputStreamForValidInput, new TypeReference<>() {});
			invalidPrices = mapper.readValue(inputStreamForInvalidInput, new TypeReference<>() {});
		}catch (Exception e){
			logger.error("input file parsing failed with exception :{}", e.getMessage());
		}
	}

	@ParameterizedTest
	@MethodSource("testData")
	public void testValidPrices(Long premiumRooms, Long economyRooms, RoomQueryResponse expected) throws Exception {
        Assertions.assertFalse(validPrices.isEmpty());
		var requestBody = new RoomQueryRequest(premiumRooms,economyRooms, validPrices);
		mockMvc.perform(post("/api/v1/rooms/occupancy")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(requestBody))
		)
				.andExpect(status().is2xxSuccessful())
				.andExpect(jsonPath("$.usagePremium").isNumber())
				.andExpect(jsonPath("$.usageEconomy").isNumber())
				.andExpect(jsonPath("$.revenuePremium").isNumber())
				.andExpect(jsonPath("$.revenueEconomy").isNumber())
				.andExpect(jsonPath("$.usagePremium").value(expected.usagePremium()))
				.andExpect(jsonPath("$.usageEconomy").value(expected.usageEconomy()))
				.andExpect(jsonPath("$.revenuePremium").value(expected.revenuePremium()))
				.andExpect(jsonPath("$.revenueEconomy").value(expected.revenueEconomy()));
	}

	@Test
	public void testInvalidPrices() throws Exception {
		Assertions.assertFalse(invalidPrices.isEmpty());
		var requestBody = new RoomQueryRequest(3L,3L, invalidPrices);
		mockMvc.perform(post("/api/v1/rooms/occupancy")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(requestBody))
				)
				.andExpect(status().isBadRequest())
				.andExpect(status().is4xxClientError());
	}

	private static Stream<Arguments> testData(){
		return Stream.of(
				Arguments.of(3L,3L,new RoomQueryResponse(3L,new BigDecimal(738),
						3L,new BigDecimal("167.99"))),
				Arguments.of(7L,5L,new RoomQueryResponse(6L,new BigDecimal(1054),
						4L,new BigDecimal("189.99"))),
				Arguments.of(2L,7L,new RoomQueryResponse(2L,new BigDecimal(583),
						4L,new BigDecimal("189.99")))
		);
	}
}
