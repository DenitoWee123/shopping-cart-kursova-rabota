package shopping_cart.entity;

import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreEntity {
    private UUID id;
    private UUID retailerId;
    private String address;
    private Double latitude;
    private Double longitude;
    private OffsetDateTime createdAt;
}
