package shopping_cart.entity;

import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WatchlistItemEntity {
    private UUID id;
    private UUID userId;
    private UUID productId;
    private BigDecimal targetPrice;
    private OffsetDateTime createdAt;
}