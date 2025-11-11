package shopping_cart.mapper;

import org.apache.ibatis.annotations.*;
import shopping_cart.entity.UserEntity;

import java.util.List;
import java.util.UUID;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM app_user")
    List<UserEntity> getAll();

    @Select("SELECT * FROM app_user WHERE id = #{id}")
    UserEntity getById(UUID id);

    @Insert("""
        INSERT INTO app_user (id, username, email, password_hash, location, created_at)
        VALUES (#{id}, #{username}, #{email}, #{passwordHash}, #{location}, #{createdAt})
    """)
    void insert(UserEntity user);
}
