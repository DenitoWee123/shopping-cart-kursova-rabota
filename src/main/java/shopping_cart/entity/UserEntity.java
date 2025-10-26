package shopping_cart.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class UserEntity {
  private String name;
  private UUID id;
  private int age;
}
