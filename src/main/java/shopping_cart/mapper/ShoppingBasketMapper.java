package shopping_cart.mapper;

import org.apache.ibatis.annotations.*;
import shopping_cart.entity.ShoppingBasketEntity;

import java.util.List;
import java.util.UUID;

public interface ShoppingBasketMapper {
    @Select("SELECT * FROM shopping_basket")
    List<ShoppingBasketEntity> getAll();

    @Insert("""
        INSERT INTO shopping_basket (id, user_id, name, is_shared, created_at)
        VALUES (#{id, typeHandler=shopping_cart.config.UUIDTypeHandler}, 
        #{userId, typeHandler=shopping_cart.config.UUIDTypeHandler}, 
        #{name}, #{isShared}, #{createdAt})
    """)
    void insert(ShoppingBasketEntity basket);
}
