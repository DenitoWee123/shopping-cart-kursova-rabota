package shopping_cart.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UserEntity {
    private String id; // UUID / String db
    private String username;
    private String email;
    private String passwordHash;
    private String location;
    private LocalDateTime createdAt; // check which date time
}
