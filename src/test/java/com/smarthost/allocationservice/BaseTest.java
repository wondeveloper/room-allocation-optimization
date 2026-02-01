package com.smarthost.allocationservice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);

    protected static final ObjectMapper mapper = new ObjectMapper();

    protected static List<BigDecimal> validPrices;

    protected static List<BigDecimal> invalidPrices;

    @BeforeAll
    public static void setUp() {
        validPrices = loadJsonFile("ValidInput.json", new TypeReference<>() {});
        invalidPrices = loadJsonFile("invalidInput.json", new TypeReference<>() {});
    }

    static <T> T loadJsonFile(String filename, TypeReference<T> typeRef) {
        try (InputStream inputStream = getResourceInputStream(filename)) {
            return mapper.readValue(inputStream, typeRef);
        } catch (Exception e) {
            logger.error("Failed to parse input file '{}' with exception: {}",
                    filename, e.getMessage());
            throw new RuntimeException(StringTemplate.STR."Failed to load test data: \{filename}", e);
        }
    }

    private static InputStream getResourceInputStream(String filename) {
        return Objects.requireNonNull(
                RoomAllocationServiceTest.class.getClassLoader()
                        .getResourceAsStream(filename),
                () -> StringTemplate.STR."Resource not found: \{filename}"
        );
    }
}
