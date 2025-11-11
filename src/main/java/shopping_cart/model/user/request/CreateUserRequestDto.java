package shopping_cart.model.user.request;

public record CreateUserRequestDto(
        String username,
        String email,
        String password,
        String location
) {}
