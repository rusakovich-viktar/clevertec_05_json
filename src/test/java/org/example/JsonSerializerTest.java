package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import org.example.model.Customer;
import org.example.model.Order;
import org.example.model.Product;
import org.example.serialization.JsonSerializer;
import org.junit.jupiter.api.Test;

class JsonSerializerTest {

    @Test
    public void testSerialize() throws Exception {
        // Given
        UUID customerId = UUID.fromString("4ec29c7b-0074-4b97-be9e-518afc9e901c");
        String customerFirstName = "Reuben";
        String customerLastName = "Martin";
        LocalDate customerDateOfBirth = LocalDate.of(2003, 11, 3);

        Order order = new Order(UUID.fromString("7ba05433-c37b-4cc5-9a07-9c0f9bdcb883"),
                List.of(new Product(UUID.fromString("2b3b5cb8-3491-4cd7-9936-952e44ebb516"), "Телефон", 100.0),
                        new Product(UUID.fromString("82378b7c-d8e4-4779-ac68-4a67a3d5f801"), "Машина", 100.0)), OffsetDateTime.of(2023, 11, 11, 15, 30, 0, 0, ZoneOffset.UTC));

        List<Order> customerOrders = List.of(order);

        Customer customer = new Customer(customerId, customerFirstName, customerLastName, customerDateOfBirth, customerOrders);

        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(OffsetDateTime.class, new com.fasterxml.jackson.databind.JsonSerializer<>() {
            @Override
            public void serialize(OffsetDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                gen.writeString(value.format(formatter));
            }
        });
        objectMapper.registerModule(module);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        JsonSerializer jsonSerializer = new JsonSerializer();

        // Then
        String expectedJson = objectMapper.writeValueAsString(customer);
        String actualJson = jsonSerializer.serialize(customer);

        // When
        assertEquals(expectedJson, actualJson);
    }

}
