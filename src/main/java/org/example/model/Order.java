package org.example.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private UUID id;
    private List<Product> products;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'")
    private OffsetDateTime createDate;

}
