//package shopping_cart.service;

//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import shopping_cart.entity.UserEntity;
//import shopping_cart.mapper.UserMapper;
//import shopping_cart.model.user.mapper.UserMapperStruct;
//import shopping_cart.model.user.*;
//import shopping_cart.exception.NotFoundException;
//import shopping_cart.model.user.request.CreateUserRequestDto;
//import shopping_cart.model.user.response.UserResponseDto;

//import java.util.List;
//import java.util.UUID;

//@Service
//@RequiredArgsConstructor
//public class UserService {
//    private final UserMapperStruct userMapperStruct;
//    private final UserMapper userMapper; // MyBatis mapper

//    public List<UserResponseDto> getAllUsers() {
//        return userMapper.getAll().stream()
//                .map(userMapperStruct::toResponse)
//                .toList();
//    }

//    public UserResponseDto getUserById(UUID id) {
//        var entity = userMapper.getUserById(id);
//        if (entity == null) throw new NotFoundException("User not found: " + id);
//        return userMapperStruct.toResponse(entity);
//    }

//    public void createUser(CreateUserRequestDto request) {
//        UserEntity entity = userMapperStruct.toEntity(request);
//        userMapper.insert(entity);
//    }
//}
