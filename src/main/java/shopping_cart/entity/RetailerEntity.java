package shopping_cart.entity;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RetailerEntity {
    private UUID id;
    private String name;
    private String websiteUrl;
    private OffsetDateTime createdAt;
}
