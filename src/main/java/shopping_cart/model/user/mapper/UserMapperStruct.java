package shopping_cart.model.user.mapper;

import org.mapstruct.*;
import shopping_cart.entity.UserEntity;
import shopping_cart.model.user.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = {UUID.class, OffsetDateTime.class, org.springframework.security.crypto.bcrypt.BCrypt.class})
public interface UserMapperStruct {

    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    @Mapping(target = "passwordHash", expression = "java(BCrypt.hashpw(request.password(), BCrypt.gensalt()))")
    @Mapping(target = "createdAt", expression = "java(OffsetDateTime.now())")
    UserEntity toEntity(CreateUserRequestDto request);

    UserResponseDto toResponse(UserEntity entity);
}