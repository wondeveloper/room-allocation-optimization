package com.smarthost.allocationservice;

import com.smarthost.allocationservice.config.constant.Constants;
import com.smarthost.allocationservice.config.dto.RoomQueryRequest;
import com.smarthost.allocationservice.config.dto.RoomQueryResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.stream.Stream;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("integration")
public class RoomAllocationServiceTest extends BaseTest{

	private final MockMvc mockMvc;

    public RoomAllocationServiceTest(@Autowired MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @ParameterizedTest
	@MethodSource("expectedResultTestData")
	public void testValidPrices(int premiumRooms, int economyRooms, RoomQueryResponse expected) throws Exception {
        Assertions.assertFalse(validPrices.isEmpty());
		var requestBody = new RoomQueryRequest(premiumRooms,economyRooms, validPrices);
		mockMvc.perform(post(Constants.POST_OCCUPANCY)
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
		var requestBody = new RoomQueryRequest(3,3, invalidPrices);
		mockMvc.perform(post(Constants.POST_OCCUPANCY)
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(requestBody))
				)
				.andExpect(status().isBadRequest())
				.andExpect(status().is4xxClientError());
	}

	private static Stream<Arguments> expectedResultTestData(){
		return Stream.of(
				Arguments.of(3,3,new RoomQueryResponse(3,new BigDecimal(738),
						3,new BigDecimal("167.99"))),
				Arguments.of(7,5,new RoomQueryResponse(6,new BigDecimal(1054),
						4,new BigDecimal("189.99"))),
				Arguments.of(2,7,new RoomQueryResponse(2,new BigDecimal(583),
						4,new BigDecimal("189.99")))
		);
	}
}
