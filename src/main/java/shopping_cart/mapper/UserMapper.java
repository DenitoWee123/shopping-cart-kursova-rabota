package shopping_cart.mapper;

import org.apache.ibatis.annotations.Mapper;
import shopping_cart.entity.UserEntity;

import java.util.List;

@Mapper
public interface UserMapper {
  List<UserEntity> getAllUsers();

  void insertUser(UserEntity user);
}
