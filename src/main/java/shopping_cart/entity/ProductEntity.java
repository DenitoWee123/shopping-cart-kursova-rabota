package shopping_cart.entity;

import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEntity {
    private UUID id;
    private String name;
    private String category;
    private String description;
    private String sku;
    private OffsetDateTime createdAt;
}
