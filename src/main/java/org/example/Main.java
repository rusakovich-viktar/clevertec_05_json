package org.example;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
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

public class Main {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final ZoneOffset DEFAULT_ZONE_OFFSET = ZoneOffset.UTC;
    private static final UUID ORDER_ID = UUID.fromString("7ba05433-c37b-4cc5-9a07-9c0f9bdcb883");
    private static final UUID PRODUCT_ID_ONE = UUID.fromString("2b3b5cb8-3491-4cd7-9936-952e44ebb516");
    private static final UUID PRODUCT_ID_TWO = UUID.fromString("82378b7c-d8e4-4779-ac68-4a67a3d5f801");
    private static final String PRODUCT_NAME_PHONE = "Телефон";
    private static final double PRODUCT_PRICE_PHONE = 100.0;
    private static final String PRODUCT_NAME_CAR = "Машина";
    private static final double PRODUCT_PRICE_CAR = 100.0;
    private static final UUID CUSTOMER_ID = UUID.fromString("4ec29c7b-0074-4b97-be9e-518afc9e901c");
    private static final String CUSTOMER_FIRST_NAME = "Reuben";
    private static final String CUSTOMER_LAST_NAME = "Martin";
    private static final LocalDate CUSTOMER_DOB = LocalDate.of(2003, 11, 3);

    public static void main(String[] args) {

        Order order = new Order(ORDER_ID, List.of(new Product(PRODUCT_ID_ONE, PRODUCT_NAME_PHONE, PRODUCT_PRICE_PHONE), new Product(PRODUCT_ID_TWO, PRODUCT_NAME_CAR, PRODUCT_PRICE_CAR)), OffsetDateTime.now());
        List<Order> customerOrders = List.of(order);

        Customer customer = new Customer(CUSTOMER_ID, CUSTOMER_FIRST_NAME, CUSTOMER_LAST_NAME, CUSTOMER_DOB, customerOrders);

        ObjectMapper objectMapper = createAndGetObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(customer);
            System.out.println(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        JsonSerializer jsonSerializer = new JsonSerializer();
        try {
            String json = jsonSerializer.serialize(customer);
            System.out.println(json);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static ObjectMapper createAndGetObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(OffsetDateTime.class, new com.fasterxml.jackson.databind.JsonSerializer<>() {
            @Override
            public void serialize(OffsetDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
                gen.writeString(value.withOffsetSameInstant(DEFAULT_ZONE_OFFSET).format(formatter));
            }
        });
        objectMapper.registerModule(module);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }
}
