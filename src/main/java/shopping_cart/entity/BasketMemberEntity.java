package shopping_cart.entity;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BasketMemberEntity {
    private UUID id;
    private UUID basketId;
    private UUID userId;
    private String role;
    private OffsetDateTime createdAt;
}
