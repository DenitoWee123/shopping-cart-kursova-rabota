package shopping_cart.entity;

import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingBasketEntity {
    private UUID id;
    private UUID userId;
    private String name;
    private Boolean isShared;
    private OffsetDateTime createdAt;
}
